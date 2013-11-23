package infinitealloys.tile;

import infinitealloys.util.MachineHelper;

public class TEEPrinter extends TileEntityElectric {

	public TEEPrinter(int facing) {
		this();
		front = facing;
	}

	public TEEPrinter() {
		super(4);
		ticksToProcess = 200;
		baseRKPerTick = -36;
	}

	@Override
	public int getID() {
		return MachineHelper.PRINTER;
	}

	@Override
	protected boolean shouldProcess() {
		return inventoryStacks[0] != null && inventoryStacks[1] != null && inventoryStacks[2] == null;
	}

	@Override
	protected boolean shouldResetProgress() {
		return inventoryStacks[0] == null || inventoryStacks[1] == null;
	}

	@Override
	protected void finishProcess() {
		inventoryStacks[2] = inventoryStacks[0].copy();
		decrStackSize(inventoryStacks[1].stackSize, 1);
	}

	@Override
	protected void updateUpgrades() {
		if(hasUpgrade(MachineHelper.SPEED2))
			processTimeMult = 0.5F;
		else if(hasUpgrade(MachineHelper.SPEED1))
			processTimeMult = 0.75F;
		else
			processTimeMult = 1.0F;

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
		validUpgrades.add(MachineHelper.CAPACITY1);
		validUpgrades.add(MachineHelper.CAPACITY2);
		validUpgrades.add(MachineHelper.WIRELESS);
	}
}
