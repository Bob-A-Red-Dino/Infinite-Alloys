package infinitealloys.core;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.WorldEvent.Load;
import net.minecraftforge.event.world.WorldEvent.Save;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import infinitealloys.network.MessageValidAlloys;
import infinitealloys.util.Consts;
import infinitealloys.util.Funcs;
import infinitealloys.util.MachineHelper;

public class EventHandler {

  private final String fileName = "InfiniteAlloys.dat";
  private String worldDir;

  @SubscribeEvent
  public void onWorldLoad(Load event) {
    if (!event.world.isRemote) {
      if (event.world.provider.dimensionId == 0) {
        worldDir = DimensionManager.getWorld(0).getChunkSaveLocation().getPath();
        try {
          NBTTagCompound nbtTagCompound =
              CompressedStreamTools.readCompressed(new FileInputStream(worldDir + "/" + fileName));
          InfiniteAlloys.instance.loadAlloyData(nbtTagCompound); // Load the valid alloys
        } catch (FileNotFoundException e) {
          // There is no saved data, probably because this is a new world. Generate new alloy data.
          InfiniteAlloys.instance.generateAlloyData();
        } catch (Exception e) {
          // There was another error. Generate new alloy data, and print stack trace.
          InfiniteAlloys.instance.generateAlloyData();
          e.printStackTrace();
        }
      }
    } else {
      // Clear the list of blocks to be outlines by the x-ray on unload. This is only run client-side
      InfiniteAlloys.proxy.gfxHandler.xrayBlocks.clear();
    }
  }

  @SubscribeEvent
  public void onWorldSave(Save event) {
    if (!event.world.isRemote && event.world.provider.dimensionId == 0) {
      NBTTagCompound nbtTagCompound = new NBTTagCompound();
      InfiniteAlloys.instance.saveAlloyData(nbtTagCompound); // Add the alloy data
      try {
        CompressedStreamTools.writeCompressed(nbtTagCompound, new FileOutputStream(
            worldDir + "/" + fileName)); // Write the NBT data to a file
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  @SubscribeEvent
  public void onEntityJoinWorld(EntityJoinWorldEvent event) {
    if (!event.world.isRemote && event.entity instanceof EntityPlayer) {
      Funcs.sendPacketToPlayer(new MessageValidAlloys(InfiniteAlloys.instance.getValidAlloys()),
                               (EntityPlayer) event.entity);
      if (!MachineHelper.playersToSync.contains(((EntityPlayer) event.entity).getDisplayName())) {
        MachineHelper.playersToSync.add(((EntityPlayer) event.entity).getDisplayName());
      }
    }
  }

  @SubscribeEvent
  public void onBlockBreak(BlockEvent.BreakEvent event) {
    if (event.world.provider.dimensionId == Consts.dimensionId
        && !event.getPlayer().capabilities.isCreativeMode) {
      event.setCanceled(true);
    }
  }

  @SubscribeEvent
  public void onBlockPlace(BlockEvent.PlaceEvent event) {
    if (event.world.provider.dimensionId == Consts.dimensionId
        && !event.player.capabilities.isCreativeMode) {
      event.setCanceled(true);
    }
  }
}
