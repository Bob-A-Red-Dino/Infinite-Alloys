package infinitealloys.client;

import infinitealloys.block.Blocks;
import infinitealloys.core.CommonProxy;
import infinitealloys.item.Items;
import infinitealloys.util.Consts;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;

public class ClientProxy extends CommonProxy {

	@Override
	public void initLocalization() {
		for(String langFile : Consts.langFiles)
			LanguageRegistry.instance().loadLocalization(getClass().getResource(langFile), langFile.substring(langFile.lastIndexOf('/') + 1, langFile.lastIndexOf('.')), true);
	}

	@Override
	public void initBlocks() {
		super.initBlocks();
		for(int i = 0; i < Consts.METAL_COUNT; i++)
			addName(new ItemStack(Blocks.ore, 0, i), "metal." + Consts.METAL_NAMES[i] + ".name", "tile.ore.name");
		for(int i = 0; i < Consts.MACHINE_COUNT; i++)
			addName(new ItemStack(Blocks.machine, 1, i), "machine." + Consts.MACHINE_NAMES[i] + ".name");
	}

	@Override
	public void initItems() {
		super.initItems();
		for(int i = 0; i < Consts.MULTI_ITEM_COUNT; i++)
			addName(new ItemStack(Items.multi, 1, i), "multi." + Consts.MULTI_ITEM_NAMES[i] + ".name");
		for(int i = 0; i < Consts.METAL_COUNT; i++)
			addName(new ItemStack(Items.ingot, 1, i), "metal." + Consts.METAL_NAMES[i] + ".name", "item.ingot.name");
		addName(new ItemStack(Items.alloyIngot), "item.alloyIngot.name");
		addName(new ItemStack(Items.upgrade), "item.upgrade.name");
		addName(new ItemStack(Items.gps), "item.gps.name");
		addName(new ItemStack(Items.alloyBook), "item.alloyBook.name");
	}

	@Override
	public void initHandlers() {
		super.initHandlers();
		MinecraftForge.EVENT_BUS.register(gfxHandler);
	}

	@Override
	public void initRendering() {
		MinecraftForgeClient.preloadTexture(Consts.TEXTURE_PATH + "gui/computer.png");
		MinecraftForgeClient.preloadTexture(Consts.TEXTURE_PATH + "gui/metalforge.png");
		MinecraftForgeClient.preloadTexture(Consts.TEXTURE_PATH + "gui/analyzer.png");
		MinecraftForgeClient.preloadTexture(Consts.TEXTURE_PATH + "gui/printer.png");
		MinecraftForgeClient.preloadTexture(Consts.TEXTURE_PATH + "gui/xray.png");
		MinecraftForgeClient.preloadTexture(Consts.TEXTURE_PATH + "gui/extras.png");
		gfxHandler.renderID = RenderingRegistry.getNextAvailableRenderId();
		RenderingRegistry.registerBlockHandler(gfxHandler);
	}
}
