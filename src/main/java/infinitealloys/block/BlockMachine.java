package infinitealloys.block;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.LinkedList;
import java.util.List;

import infinitealloys.core.InfiniteAlloys;
import infinitealloys.item.IAItems;
import infinitealloys.tile.IHost;
import infinitealloys.tile.TileEntityMachine;
import infinitealloys.util.Consts;
import infinitealloys.util.EnumMachine;
import infinitealloys.util.MachineHelper;

public final class BlockMachine extends BlockContainer {

  public static final PropertyDirection FACING_PROP =
      PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);

  public BlockMachine() {
    super(Material.iron);
    setCreativeTab(InfiniteAlloys.creativeTab);
    setHardness(2f);
    setDefaultState(blockState.getBaseState().withProperty(FACING_PROP, EnumFacing.SOUTH));
  }

  @Override
  public int getRenderType() {
    return 3;
  }

  @Override
  public boolean isOpaqueCube() {
    return false;
  }

  @Override
  protected BlockState createBlockState() {
    return new BlockState(this, FACING_PROP);
  }

  @Override
  public IBlockState getStateFromMeta(int meta) {
    return getDefaultState().withProperty(FACING_PROP, EnumFacing.getHorizontal(meta));
  }

  @Override
  public int getMetaFromState(IBlockState state) {
    return ((EnumFacing) state.getValue(FACING_PROP)).getHorizontalIndex();
  }

  @Override
  @SuppressWarnings("unchecked")
  public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player,
                                  EnumFacing side, float hitX, float hitY, float hitZ) {
    ItemStack heldItem = player.inventory.getCurrentItem();
    TileEntityMachine tem = (TileEntityMachine) world.getTileEntity(pos);

    // Sync the network data for each host TE in this world if it has not already been done for this player
    if (!world.isRemote && MachineHelper.playersToSync.contains(player.getName())) {
      world.loadedTileEntityList.stream().filter(te -> te instanceof IHost)
          .forEach(te -> ((IHost) te).syncAllClients(player));
      MachineHelper.playersToSync.remove(player.getName());
    }

    // Is the player holding a network wand?
    if (heldItem != null && heldItem.getItem() == IAItems.internetWand
        && (MachineHelper.isClient(tem) || tem instanceof IHost)) {

      // Put the coords of this block in a temp tag in the wand so the wand's GUI can access it
      if (!heldItem.hasTagCompound()) {
        heldItem.setTagCompound(new NBTTagCompound());
      }
      heldItem.getTagCompound()
          .setIntArray("CoordsCurrent", new int[]{world.provider.getDimensionId(),
                                                  pos.getX(), pos.getY(), pos.getZ()});

      // Open the GUI for the wand to let the player decide what they want to do with this block
      player.openGui(InfiniteAlloys.instance, Consts.WAND_GUI_ID, world,
                     (int) player.posX, (int) player.posY, (int) player.posZ);
    } else {
      // Open the regular GUI
      openGui(world, player, tem.getPos());
    }

    return true;
  }

  public void openGui(World world, EntityPlayer player, BlockPos machinePos) {
    final TileEntityMachine tem = ((TileEntityMachine) world.getTileEntity(machinePos));
    if (!world.isRemote) {
      world.markBlockForUpdate(tem.getPos());
    }
    player.openGui(InfiniteAlloys.instance, tem.getMachineType().getGuiId(), world,
                   machinePos.getX(), machinePos.getY(), machinePos.getZ());
  }

  @Override
  public TileEntity createNewTileEntity(World world, int metadata) {
    try {
      return EnumMachine.byBlock(this).getNewTEM();
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  @Override
  public void onNeighborChange(IBlockAccess world, BlockPos pos, BlockPos neighbor) {
    ((TileEntityMachine) world.getTileEntity(pos)).onNeighborChange(neighbor);
  }

  @Override
  public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer,
                              ItemStack stack) {
    final TileEntityMachine tem = (TileEntityMachine) world.getTileEntity(pos);
    if (tem != null) {
      world.setBlockState(pos, world.getBlockState(pos).withProperty(
          BlockMachine.FACING_PROP, placer.getHorizontalFacing().getOpposite()));
      if (stack.hasTagCompound()) {
        tem.loadNBTData(stack.getTagCompound());
      }
    }
  }

  @Override
  public void breakBlock(World world, BlockPos pos, IBlockState state) {
    TileEntityMachine tem = (TileEntityMachine) world.getTileEntity(pos);
    if (tem != null) {
      tem.onBlockDestroyed();
    }
    super.breakBlock(world, pos, state);
  }

  @Override
  public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state,
                                  int fortune) {
    return new LinkedList<>(); // Drops are handled by the TileEntity in onBlockDestroyed
  }
}
