package infinitealloys.tile;

import infinitealloys.core.InfiniteAlloys;
import infinitealloys.item.Items;
import infinitealloys.util.Consts;
import java.util.ArrayList;
import java.util.Arrays;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class TEMMetalForge extends TileEntityMachine {

	/** An array for the "stack sizes" of each ingot in the recipe setting */
	public byte[] recipeAmts = new byte[Consts.METAL_COUNT];
	/** recipeAmts from last tick, used to tell if the recipe has changed to reset progress */
	private byte[] lastRecipeAmts = new byte[Consts.METAL_COUNT];
	public byte presetSelection = -1;

	public TEMMetalForge(int facing) {
		this();
		front = facing;
	}

	public TEMMetalForge() {
		super(1);
		inventoryStacks = new ItemStack[21];
		baseRKPerTick = -18;
		ticksToProcess = 12800;
	}

	@Override
	public int getID() {
		return TEHelper.METAL_FORGE;
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack itemstack) {
		return super.isItemValidForSlot(slot, itemstack) || TEHelper.stackValidForSlot(TEHelper.METAL_FORGE, slot, itemstack);
	}

	@Override
	public void readFromNBT(NBTTagCompound tagCompound) {
		super.readFromNBT(tagCompound);
		recipeAmts = tagCompound.getByteArray("RecipeAmts");
	}

	@Override
	public void writeToNBT(NBTTagCompound tagCompound) {
		super.writeToNBT(tagCompound);
		tagCompound.setByteArray("RecipeAmts", recipeAmts);
	}

	public void handlePacketDataFromClient(byte[] recipeAmts) {
		this.recipeAmts = recipeAmts;
	}

	@Override
	public void updateEntity() {
		super.updateEntity();
		if(!Arrays.equals(lastRecipeAmts, recipeAmts))
			processProgress = 0;
		lastRecipeAmts = Arrays.copyOf(recipeAmts, recipeAmts.length);
	}

	/** Return the resulting ingot for the smelted ingots
	 * 
	 * @return The resulting ingot. */
	private ItemStack getIngotResult() {
		int alloy = 0;
		for(int i = 0; i < recipeAmts.length; i++)
			alloy += Math.pow(Consts.ALLOY_RADIX, i) * recipeAmts[i];
		ItemStack result = new ItemStack(Items.alloyIngot);
		NBTTagCompound tagCompound = new NBTTagCompound();
		tagCompound.setInteger("alloy", alloy);
		result.setTagCompound(tagCompound);
		result.setItemDamage(getDamageForAlloy(alloy));
		return result;
	}

	public int getDamageForAlloy(int alloy) {
		int[] validAlloys = InfiniteAlloys.instance.worldData.getValidAlloys();
		for(int i = 0; i < validAlloys.length; i++)
			if(alloy == validAlloys[i])
				return i + 1;
		return -1;
	}

	private ArrayList<Integer> getSlotsWithIngot() {
		ArrayList<Integer> slots = new ArrayList<Integer>();
		for(int i = 3; i < 21; i++)
			if(inventoryStacks[i] != null)
				slots.add(i);
		return slots;
	}

	private int[] getIngotAmts() {
		int[] amts = new int[Consts.METAL_COUNT];
		for(int slot : getSlotsWithIngot())
			amts[TEHelper.getIngotNum(inventoryStacks[slot])] += inventoryStacks[slot].stackSize;
		return amts;
	}

	private int getIngotsInRecipe() {
		int ingots = 0;
		for(int amt : recipeAmts)
			ingots += amt;
		return ingots;
	}

	@Override
	protected boolean shouldProcess() {
		int typesInRecipe = 0;
		ArrayList<Boolean> sufficientIngots = new ArrayList<Boolean>();
		for(int amt : recipeAmts)
			if(amt > 0)
				typesInRecipe++;
		for(int i = 0; i < getIngotAmts().length; i++)
			sufficientIngots.add(getIngotAmts()[i] >= recipeAmts[i]);
		if(sufficientIngots.contains(false))
			processProgress = 0;
		return (inventoryStacks[2] == null || inventoryStacks[2].isItemEqual(getIngotResult()) && getInventoryStackLimit() - inventoryStacks[2].stackSize >= 1)
				&& typesInRecipe > 1 && !sufficientIngots.contains(false);
	}

	@Override
	protected void finishProcess() {
		byte[] ingotsToRemove = Arrays.copyOf(recipeAmts, recipeAmts.length);
		for(int slot : getSlotsWithIngot()) {
			int ingotNum = TEHelper.getIngotNum(inventoryStacks[slot]);
			int ingots = ingotsToRemove[ingotNum];
			ingotsToRemove[ingotNum] -= Math.min(ingotsToRemove[ingotNum], inventoryStacks[slot].stackSize);
			decrStackSize(slot, Math.min(ingots, inventoryStacks[slot].stackSize));
		}
		ItemStack result = getIngotResult();
		if(inventoryStacks[2] == null)
			inventoryStacks[2] = result;
		else if(inventoryStacks[2].getTagCompound().getInteger("alloy") == result.getTagCompound().getInteger("alloy"))
			inventoryStacks[2].stackSize += result.stackSize;
	}

	@Override
	public int getRKChange() {
		if(shouldProcess())
			return (int)(baseRKPerTick * rkPerTickMult / processTimeMult * getIngotsInRecipe());
		return 0;
	}

	@Override
	protected void updateUpgrades() {
		if(hasUpgrade(TEHelper.SPEED2))
			processTimeMult = 0.5F;
		else if(hasUpgrade(TEHelper.SPEED1))
			processTimeMult = 0.75F;
		else
			processTimeMult = 1.0F;

		if(hasUpgrade(TEHelper.EFFICIENCY2))
			rkPerTickMult = 0.5F;
		else if(hasUpgrade(TEHelper.EFFICIENCY1))
			rkPerTickMult = 0.75F;
		else
			rkPerTickMult = 1.0F;

		if(hasUpgrade(TEHelper.CAPACITY2))
			stackLimit = 64;
		else if(hasUpgrade(TEHelper.CAPACITY1))
			stackLimit = 48;
		else
			stackLimit = 32;
	}

	@Override
	protected void populateValidUpgrades() {
		validUpgrades.add(TEHelper.SPEED1);
		validUpgrades.add(TEHelper.SPEED2);
		validUpgrades.add(TEHelper.EFFICIENCY1);
		validUpgrades.add(TEHelper.EFFICIENCY2);
		validUpgrades.add(TEHelper.CAPACITY1);
		validUpgrades.add(TEHelper.CAPACITY2);
		validUpgrades.add(TEHelper.WIRELESS);
	}
}