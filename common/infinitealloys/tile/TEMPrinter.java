package infinitealloys.tile;

import net.minecraft.item.ItemStack;

public class TEMPrinter extends TileEntityMachine {

	public TEMPrinter(int facing) {
		this();
		front = facing;
	}

	public TEMPrinter() {
		super(3);
		inventoryStacks = new ItemStack[4];
		ticksToProcess = 200;
		baseRKPerTick = -36;
	}

	@Override
	public int getID() {
		return TEHelper.PRINTER;
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack itemstack) {
		return super.isItemValidForSlot(slot, itemstack) || TEHelper.stackValidForSlot(TEHelper.PRINTER, slot, itemstack);
	}

	@Override
	public void updateEntity() {
		super.updateEntity();
		if(inventoryStacks[0] != null && inventoryStacks[1] != null && inventoryStacks[2] == null)
			processProgress = 0;
	}

	@Override
	public boolean shouldProcess() {
		return inventoryStacks[0] != null && inventoryStacks[1] != null && inventoryStacks[2] == null;
	}

	@Override
	public void finishProcess() {
		inventoryStacks[2] = inventoryStacks[0].copy();
		inventoryStacks[1].stackSize--;
		if(inventoryStacks[1].stackSize == 0)
			inventoryStacks[1] = null;
		processProgress = 0;
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