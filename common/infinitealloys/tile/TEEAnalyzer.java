package infinitealloys.tile;

import infinitealloys.util.Consts;
import infinitealloys.util.Funcs;
import infinitealloys.util.MachineHelper;
import java.util.Arrays;
import net.minecraft.nbt.NBTTagCompound;

public class TEEAnalyzer extends TileEntityElectric {

	/** The amount of alloys that this machine has unlocked. This can be increased by adding an alloy book containing more recipes than this machine already
	 * knows. */
	private byte unlockedAlloyCount;

	/** A boolean for each metal telling whether or not an ingot of that metal is required to being searching for the next alloy */
	private final boolean[] requiredMetals = new boolean[Consts.METAL_COUNT];

	/** As more alloys are discovered, it takes more time to find new ones. The length of this array should ALWAYS be equal to
	 * {@link infinitealloys.util.Consts#VALID_ALLOY_COUNT Consts.VALID_ALLOY_COUNT} */
	private final int[] ticksToProcessForAlloys = new int[] { 5000, 20000, 80000, 320000, 1280000, 5120000 };

	public TEEAnalyzer(int facing) {
		this();
		front = facing;
	}

	public TEEAnalyzer() {
		super(10);
		stackLimit = 1;
		baseRKPerTick = -1000;
		updateRequiredMetals();
	}

	@Override
	public int getID() {
		return MachineHelper.ANALYZER;
	}

	@Override
	protected boolean shouldProcess() {
		// If we've run out of alloys to discover, don't process
		if(unlockedAlloyCount >= Consts.VALID_ALLOY_COUNT)
			return false;

		// Otherwise, if we're not already processing, check for the alloys that we need to start a new process
		if(getProcessProgress() <= 0)
			for(int i = 0; i < requiredMetals.length; i++)
				if(requiredMetals[i] && inventoryStacks[i] == null)
					return false;
		return true;
	}

	@Override
	protected void startProcess() {
		for(int i = 0; i < requiredMetals.length; i++)
			if(requiredMetals[i])
				decrStackSize(i, 1);
	}

	@Override
	protected void finishProcess() {
		// Increment the amount of alloys we've discovered (we just found a new one)
		unlockedAlloyCount++;

		// Update the required time and metals to fit the next alloy
		updateRequiredMetals();

		// If an alloy book is present, save the newly-discovered alloy to it
		if(inventoryStacks[8] != null) {
			int alloy = inventoryStacks[8].getTagCompound().getInteger("alloy");
			NBTTagCompound tagCompound;

			// Create two arrays for storing the saved alloys. What's in there, and a copy to edit
			int[] oldSave = new int[0];
			int[] newSave;

			// init the compound
			if(inventoryStacks[8].hasTagCompound())
				tagCompound = inventoryStacks[8].getTagCompound();
			else
				tagCompound = new NBTTagCompound();

			// If it has a save, set oldSave to it
			if(tagCompound.hasKey("alloys"))
				oldSave = tagCompound.getIntArray("alloys");

			// Make new save a copy of oldSave with one more spot
			newSave = Arrays.copyOf(oldSave, oldSave.length + 1);

			// Sort newSave so that it can be searched in the next step
			Arrays.sort(newSave);

			// Add the new alloy to newSave if there is room and it is not a repeat then set the compound to newSave
			if(newSave.length < Consts.VALID_ALLOY_COUNT && Arrays.binarySearch(newSave, alloy) < 0) {
				newSave[newSave.length - 1] = alloy;
				tagCompound.setIntArray("alloys", newSave);
				inventoryStacks[8].setTagCompound(tagCompound);
			}
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound tagCompound) {
		super.readFromNBT(tagCompound);
		unlockedAlloyCount = tagCompound.getByte("UnlockedAlloyCount");
	}

	@Override
	public void writeToNBT(NBTTagCompound tagCompound) {
		super.writeToNBT(tagCompound);
		tagCompound.setByte("UnlockedAlloyCount", unlockedAlloyCount);
	}

	public void handlePacketDataFromClient(byte unlockedAlloyCount) {
		this.unlockedAlloyCount = unlockedAlloyCount;
	}

	public byte getUnlockedAlloyCount() {
		return unlockedAlloyCount;
	}

	private void updateRequiredMetals() {
		ticksToProcess = ticksToProcessForAlloys[unlockedAlloyCount];
		for(int i = 0; i < requiredMetals.length; i++)
			requiredMetals[i] = Funcs.intAtPos(Consts.VALID_ALLOY_MAXES[unlockedAlloyCount], Consts.ALLOY_RADIX, Consts.METAL_COUNT, i) > 0;
	}

	@Override
	public void onInventoryChanged() {
		// Is an alloy book in the book slot?
		if(inventoryStacks[8] != null) {
			// Does this book have a tag compound with alloys saved in it?
			NBTTagCompound tagCompound = inventoryStacks[8].getTagCompound();
			if(tagCompound != null && tagCompound.hasKey("alloys"))
				// Set the amount of unlocked alloys to whichever is larger: itself, or the amount of alloys in the book
				// i.e. if the book has more alloys saved than the machine, teach those alloys to the machine
				unlockedAlloyCount = (byte)Math.max(unlockedAlloyCount, tagCompound.getIntArray("alloys").length);
		}
	}

	@Override
	protected void updateUpgrades() {
		if(hasUpgrade(MachineHelper.SPEED2))
			processTimeMult = 1800;
		else if(hasUpgrade(MachineHelper.SPEED1))
			processTimeMult = 2700;
		else
			processTimeMult = 3600;

		if(hasUpgrade(MachineHelper.EFFICIENCY2))
			rkPerTickMult = 0.5F;
		else if(hasUpgrade(MachineHelper.EFFICIENCY1))
			rkPerTickMult = 0.75F;
		else
			rkPerTickMult = 1.0F;
	}

	@Override
	protected void populateValidUpgrades() {
		validUpgrades.add(MachineHelper.SPEED1);
		validUpgrades.add(MachineHelper.SPEED2);
		validUpgrades.add(MachineHelper.EFFICIENCY1);
		validUpgrades.add(MachineHelper.EFFICIENCY2);
		validUpgrades.add(MachineHelper.WIRELESS);
	}
}
