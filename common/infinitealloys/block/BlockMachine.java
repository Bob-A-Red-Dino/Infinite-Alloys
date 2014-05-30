package infinitealloys.block;

import infinitealloys.core.InfiniteAlloys;
import infinitealloys.item.ItemInternetWand;
import infinitealloys.tile.IHost;
import infinitealloys.tile.TEMComputer;
import infinitealloys.tile.TileEntityMachine;
import infinitealloys.util.Consts;
import infinitealloys.util.Funcs;
import infinitealloys.util.MachineHelper;
import infinitealloys.util.Point;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockMachine extends BlockContainer {

	public BlockMachine() {
		super(Material.iron);
		setCreativeTab(InfiniteAlloys.tabIA);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister iconRegister) {
		for(int i = 0; i < Consts.MACHINE_COUNT; i++) {
			IABlocks.machineIcons[i][0] = iconRegister.registerIcon(Consts.TEXTURE_PREFIX + MachineHelper.MACHINE_NAMES[i] + "_top");
			IABlocks.machineIcons[i][1] = iconRegister.registerIcon(Consts.TEXTURE_PREFIX + MachineHelper.MACHINE_NAMES[i] + "_bottom");
			IABlocks.machineIcons[i][2] = iconRegister.registerIcon(Consts.TEXTURE_PREFIX + MachineHelper.MACHINE_NAMES[i] + "_front");
			IABlocks.machineIcons[i][3] = iconRegister.registerIcon(Consts.TEXTURE_PREFIX + MachineHelper.MACHINE_NAMES[i] + "_side");
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(IBlockAccess blockAccess, int x, int y, int z, int side) {
		side = side <= Consts.TOP ? side : side == ((TileEntityMachine)blockAccess.getTileEntity(x, y, z)).front ? Consts.SOUTH : Consts.NORTH;
		return getIcon(side, blockAccess.getBlockMetadata(x, y, z));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int metadata) {
		switch(side) {
			case Consts.TOP:
				return IABlocks.machineIcons[metadata][0];

			case Consts.BOTTOM:
				return IABlocks.machineIcons[metadata][1];

			case Consts.SOUTH:
				return IABlocks.machineIcons[metadata][2];

			default:
				return IABlocks.machineIcons[metadata][3];
		}
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int facing, float f, float f1, float f2) {
		ItemStack heldItem = player.inventory.getCurrentItem();
		TileEntityMachine tem = (TileEntityMachine)world.getTileEntity(x, y, z);

		// Sync the network data for each host TE in this world if it has not already been done for this player
		if(!world.isRemote && MachineHelper.playersToSync.contains(player.getDisplayName())) {
			for(Object te : world.loadedTileEntityList)
				if(te instanceof IHost)
					((IHost)te).syncAllClients(player);
			MachineHelper.playersToSync.remove(player.getDisplayName());
		}

		// Is the player holding an internet wand?
		if(heldItem != null && heldItem.getItem() instanceof ItemInternetWand && (MachineHelper.isClient(tem) || tem instanceof IHost)) {

			// Put the coords of this block in a temp tag in the wand so the wand's GUI can access it
			if(!heldItem.hasTagCompound())
				heldItem.setTagCompound(new NBTTagCompound());
			heldItem.getTagCompound().setIntArray("CoordsCurrent", new int[] { world.provider.dimensionId, x, y, z });

			// Open the GUI for the wand to let the player decide what they want to do with this block
			player.openGui(InfiniteAlloys.instance, Consts.WAND_GUI, world, (int)player.posX, (int)player.posY, (int)player.posZ);
			return true;
		}

		openGui(world, player, tem, false);
		return true;
	}

	public void openGui(World world, EntityPlayer player, TileEntityMachine tem, boolean fromComputer) {
		if(!fromComputer && world.isRemote)
			MachineHelper.controllers.remove(player.getDisplayName());
		if(tem instanceof TEMComputer)
			MachineHelper.controllers.put(player.getDisplayName(), new Point(tem.xCoord, tem.yCoord, tem.zCoord));
		if(!world.isRemote) {
			tem.playersUsing.add(player.getDisplayName());
			world.markBlockForUpdate(tem.xCoord, tem.yCoord, tem.zCoord);
		}
		player.openGui(InfiniteAlloys.instance, tem.getID(), world, tem.xCoord, tem.yCoord, tem.zCoord);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int metadata) {
		try {
			return (TileEntity)MachineHelper.MACHINE_CLASSES[metadata].newInstance();
		}catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static void updateBlockState(World world, int x, int y, int z) {
		TileEntity te = world.getTileEntity(x, y, z);
		if(te != null) {
			te.validate();
			world.setTileEntity(x, y, z, te);
		}
	}

	@Override
	public void getSubBlocks(Item item, CreativeTabs creativetabs, List list) {
		for(int i = 0; i < Consts.MACHINE_COUNT; i++)
			list.add(new ItemStack(item, 1, i));
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entityLiving, ItemStack itemstack) {
		TileEntityMachine tem = (TileEntityMachine)world.getTileEntity(x, y, z);
		if(tem != null)
			tem.front = Funcs.yawToNumSide(MathHelper.floor_float(entityLiving.rotationYaw / 90F - 1.5F) & 3);
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, Block block, int metadata) {
		TileEntityMachine tem = (TileEntityMachine)world.getTileEntity(x, y, z);
		if(tem != null)
			tem.onBlockDestroyed();
		super.breakBlock(world, x, y, z, block, metadata);
	}
}
