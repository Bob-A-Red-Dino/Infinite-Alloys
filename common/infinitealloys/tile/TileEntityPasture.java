package infinitealloys.tile;

import infinitealloys.util.Consts;
import java.util.ArrayList;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;

public class TileEntityPasture extends TileEntityMachine {

	/** The the mode value for turning the machine off */
	public static final int MODE_OFF = 0;
	/** The mode value for only trapping animals */
	public static final int MODE_ATTRACT = 1;
	/** The mode value for only repelling monsters */
	public static final int MODE_REPEL = 2;
	/** 0 is do nothing to the mob, 1 is attract the mob, 2 is repel the mob. The order can be seen in {@link #mobClasses mobClasses} */
	public byte[] mobActions = new byte[Consts.PASTURE_ANIMALS + Consts.PASTURE_MONSTERS];
	/** The entity classes for each mob to be used in the {@link #updateEntity() updateEntity} function */
	private final Class[] mobClasses = { EntityChicken.class, EntityCow.class, EntityCow.class, EntitySheep.class, EntityCreeper.class, EntitySkeleton.class, EntitySpider.class, EntityZombie.class };
	private byte maxSpots;
	private byte trapRange;
	private byte repelRange;

	public TileEntityPasture(int facing) {
		this();
		front = facing;
	}

	public TileEntityPasture() {
		super(0);
		inventoryStacks = new ItemStack[1];
		ticksToProcess = 0;
	}

	@Override
	public String getInvName() {
		return "Pasture";
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack itemstack) {
		return super.isItemValidForSlot(slot, itemstack) || TEHelper.stackValidForSlot(TEHelper.PASTURE, slot, itemstack);
	}

	@Override
	public void updateEntity() {
		super.updateEntity();
		ArrayList<EntityCreature> trapList = new ArrayList<EntityCreature>();
		ArrayList<EntityCreature> repelList = new ArrayList<EntityCreature>();

		for(int i = 0; i < mobActions.length; i++) {
			if(mobActions[i] == 1)
				for(EntityCreature creature : (ArrayList<EntityChicken>)worldObj.getEntitiesWithinAABB(mobClasses[i],
						AxisAlignedBB.getAABBPool().getAABB(xCoord - trapRange - 1, 0, zCoord - trapRange - 1, xCoord + trapRange + 2, worldObj.getHeight(), zCoord + trapRange + 2)))
					trapList.add(creature);
			else if(mobActions[i] == 2) {
				for(EntityCreature creature : (ArrayList<EntityChicken>)worldObj.getEntitiesWithinAABB(mobClasses[i],
						AxisAlignedBB.getAABBPool().getAABB(xCoord - repelRange, 0, zCoord - repelRange, xCoord + repelRange + 1, worldObj.getHeight(), zCoord + repelRange + 1))) {
					repelList.add(creature);
				}
			}
		}

		for(EntityCreature creature : trapList) {
			if(Math.abs(xCoord - creature.posX) > trapRange + 1) // Is the creature too far away in the x direction
				creature.moveEntity(xCoord + Math.signum(creature.posX - xCoord) * trapRange - creature.posX, 0, 0); // Move it back to the edge of the radius in the x direction
			if(Math.abs(zCoord - creature.posZ) > trapRange + 1) // Is the creature too far away in the z direction
				creature.moveEntity(0, 0, zCoord + Math.signum(creature.posZ - zCoord) * trapRange - creature.posZ); // Move is back to the edge of the radius in the z direction
		}

		for(EntityCreature creature : repelList) {
			if(Math.abs(xCoord - creature.posX) > repelRange) // Is the creature too close in the x direction
				creature.moveEntity(creature.posX - xCoord - Math.signum(creature.posX - xCoord) * repelRange, 0, 0); // Move it back to the edge of the radius in the x direction
			if(Math.abs(zCoord - creature.posZ) > repelRange) // Is the creature too close in the z direction
				creature.moveEntity(0, 0, creature.posZ - zCoord - Math.signum(creature.posZ - zCoord) * repelRange); // Move is back to the edge of the radius in the z direction
		}
	}

	@Override
	public boolean shouldProcess() {
		return false;
	}

	@Override
	public void finishProcessing() {}

	public void handlePacketData(byte[] mobActions) {
		this.mobActions = mobActions;
	}

	@Override
	public void readFromNBT(NBTTagCompound tagCompound) {
		super.readFromNBT(tagCompound);
		for(int i = 0; i < Consts.PASTURE_ANIMALS + Consts.PASTURE_MONSTERS; i++)
			mobActions[i] = tagCompound.getByte("Mob" + i);
	}

	@Override
	public void writeToNBT(NBTTagCompound tagCompound) {
		super.writeToNBT(tagCompound);
		for(int i = 0; i < Consts.PASTURE_ANIMALS + Consts.PASTURE_MONSTERS; i++)
			tagCompound.setByte("Mob" + i, mobActions[i]);
	}

	@Override
	public int getJoulesUsed() {
		if(shouldProcess())
			return joulesUsedPerTick;
		return 0;
	}

	@Override
	protected void updateUpgrades() {
		if(hasUpgrade(TEHelper.EFFICIENCY2))
			joulesUsedPerTick = 1;
		else if(hasUpgrade(TEHelper.EFFICIENCY1))
			joulesUsedPerTick = 2;
		else
			joulesUsedPerTick = 4;

		if(hasUpgrade(TEHelper.CAPACITY2))
			maxSpots = 8;
		else if(hasUpgrade(TEHelper.CAPACITY1))
			maxSpots = 4;
		else
			maxSpots = 2;

		if(hasUpgrade(TEHelper.RANGE2)) {
			trapRange = 15;
			repelRange = 24;
		}
		else if(hasUpgrade(TEHelper.RANGE1)) {
			trapRange = 10;
			repelRange = 16;
		}
		else {
			trapRange = 5;
			repelRange = 8;
		}

		canNetwork = hasUpgrade(TEHelper.WIRELESS);

		if(hasUpgrade(TEHelper.ELECCAPACITY2))
			setMaxEnergyStored(1000000);
		else if(hasUpgrade(TEHelper.ELECCAPACITY1))
			setMaxEnergyStored(750000);
		else
			setMaxEnergyStored(500000);
	}

	@Override
	protected void populateValidUpgrades() {
		validUpgrades.add(TEHelper.EFFICIENCY1);
		validUpgrades.add(TEHelper.EFFICIENCY2);
		validUpgrades.add(TEHelper.CAPACITY1);
		validUpgrades.add(TEHelper.CAPACITY2);
		validUpgrades.add(TEHelper.RANGE1);
		validUpgrades.add(TEHelper.RANGE2);
		validUpgrades.add(TEHelper.WIRELESS);
		validUpgrades.add(TEHelper.ELECCAPACITY1);
		validUpgrades.add(TEHelper.ELECCAPACITY2);
	}

	/** Does the pasture have enough space to enable another animal or monster
	 * 
	 * @return true if there is enough space to enable another animal or monster */
	public boolean hasFreeSpots() {
		int usedSpots = 0;
		for(byte mob : mobActions)
			if(mob > 0)
				usedSpots++;
		return usedSpots < maxSpots;
	}
}
