package infinitealloys;

import infinitealloys.block.BlockMachine;
import infinitealloys.block.BlockOre;
import infinitealloys.block.Blocks;
import infinitealloys.handlers.EventHandler;
import infinitealloys.handlers.GuiHandler;
import infinitealloys.handlers.WorldGenHandler;
import infinitealloys.item.ItemAlloyBook;
import infinitealloys.item.ItemAlloyIngot;
import infinitealloys.item.ItemBlockIA;
import infinitealloys.item.ItemGPS;
import infinitealloys.item.ItemIngot;
import infinitealloys.item.ItemMulti;
import infinitealloys.item.ItemUpgrade;
import infinitealloys.item.Items;
import infinitealloys.tile.TEHelper;
import infinitealloys.tile.TileEntityAnalyzer;
import infinitealloys.tile.TileEntityComputer;
import infinitealloys.tile.TileEntityMetalForge;
import infinitealloys.tile.TileEntityPrinter;
import infinitealloys.tile.TileEntityXray;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.stats.Achievement;
import net.minecraftforge.common.AchievementPage;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;

public class CommonProxy {

	private ItemStack[] alloys = new ItemStack[References.validAlloyCount];
	private ItemStack[] upgrades = new ItemStack[References.upgradeCount];

	public void initLocalization() {}

	public void initBlocks() {
		Blocks.ore = new BlockOre(Blocks.oreID, 0).setCreativeTab(InfiniteAlloys.tabIA).setHardness(2F).setBlockName("IAore");
		Blocks.machine = new BlockMachine(Blocks.machineID, 6).setCreativeTab(InfiniteAlloys.tabIA).setHardness(2F).setBlockName("IAmachine");
		GameRegistry.registerBlock(Blocks.ore, ItemBlockIA.class);
		GameRegistry.registerBlock(Blocks.machine, ItemBlockIA.class);
		OreDictionary.registerOre("oreZinc", new ItemStack(Blocks.ore, 1, 0));
		OreDictionary.registerOre("oreMagnesium", new ItemStack(Blocks.ore, 1, 1));
		OreDictionary.registerOre("oreScandium", new ItemStack(Blocks.ore, 1, 2));
		OreDictionary.registerOre("oreTantalum", new ItemStack(Blocks.ore, 1, 3));
		MinecraftForge.setBlockHarvestLevel(Blocks.ore, 0, "pickaxe", 1);
		MinecraftForge.setBlockHarvestLevel(Blocks.ore, 1, "pickaxe", 1);
		MinecraftForge.setBlockHarvestLevel(Blocks.ore, 2, "pickaxe", 1);
		MinecraftForge.setBlockHarvestLevel(Blocks.ore, 3, "pickaxe", 1);
		MinecraftForge.setBlockHarvestLevel(Blocks.ore, 4, "pickaxe", 2);
		MinecraftForge.setBlockHarvestLevel(Blocks.ore, 5, "pickaxe", 2);
		MinecraftForge.setBlockHarvestLevel(Blocks.ore, 6, "pickaxe", 2);
		MinecraftForge.setBlockHarvestLevel(Blocks.ore, 7, "pickaxe", 3);
		MinecraftForge.setBlockHarvestLevel(Blocks.machine, 0, "pickaxe", 0);
		MinecraftForge.setBlockHarvestLevel(Blocks.machine, 1, "pickaxe", 0);
		MinecraftForge.setBlockHarvestLevel(Blocks.machine, 2, "pickaxe", 0);
		MinecraftForge.setBlockHarvestLevel(Blocks.machine, 3, "pickaxe", 0);
	}

	public void initItems() {
		Items.multi = new ItemMulti(Items.multiID, 128).setCreativeTab(InfiniteAlloys.tabIA).setItemName("IAmulti");
		Items.ingot = new ItemIngot(Items.ingotID, 130).setCreativeTab(InfiniteAlloys.tabIA).setItemName("IAingot");
		Items.alloyIngot = new ItemAlloyIngot(Items.alloyIngotID, 130).setItemName("IAalloyIngot");
		Items.upgrade = new ItemUpgrade(Items.upgradeID, 131).setMaxStackSize(1).setCreativeTab(InfiniteAlloys.tabIA).setItemName("IAupgrade");
		Items.gps = new ItemGPS(Items.gpsID, 142).setMaxStackSize(1).setCreativeTab(InfiniteAlloys.tabIA).setItemName("IAgps");
		Items.alloyBook = new ItemAlloyBook(Items.alloyBookID, 143).setMaxStackSize(1).setCreativeTab(InfiniteAlloys.tabIA).setItemName("IAalloyBook");
		OreDictionary.registerOre("ingotZinc", new ItemStack(Items.ingot));
		OreDictionary.registerOre("ingotMagnesium", new ItemStack(Items.ingot, 1, 1));
		OreDictionary.registerOre("ingotScandium", new ItemStack(Items.ingot, 1, 2));
		OreDictionary.registerOre("ingotTantalum", new ItemStack(Items.ingot, 1, 3));
		for(int i = 0; i < alloys.length; i++)
			alloys[i] = new ItemStack(Items.alloyIngot, 1, i + 1);
		for(int i = 0; i < upgrades.length; i++)
			upgrades[i] = new ItemStack(Items.upgrade, 1, (int)Math.pow(2D, i));
	}

	public void initRecipes() {
		addRecipeDict(new ItemStack(Items.multi), " W ", "CBC", " W ", 'B', "battery", 'C', "ingotCopper", 'W', "copperWire");
		addRecipeDict(new ItemStack(Items.multi, 1, 1), "CTC", "IWI", 'C', "ingotCopper", 'I', Item.ingotIron, 'T', "ingotTin", 'W', "copperWire");
		addRecipe(new ItemStack(Blocks.machine), "ASA", "WCG", "ABA", 'A', alloys[2], 'B', alloys[3], 'C', Items.multi, 'G', Block.thinGlass, 'S', Block.stoneButton, 'W', upgrades[8]); // Computer
		addRecipe(new ItemStack(Blocks.machine, 1, 1), "BCB", "BDB", "NNN", 'B', Block.brick, 'C', Items.multi, 'D', Item.doorSteel, 'N', Block.netherrack); // Metal Forge
		addRecipe(new ItemStack(Blocks.machine, 1, 2), " C ", "RGR", "III", 'C', Items.multi, 'G', Block.glowStone, 'I', Item.ingotIron, 'R', Item.redstone); // Analyzer
		addRecipe(new ItemStack(Blocks.machine, 1, 3), "APA", "BIB", "OOC", 'A', alloys[0], 'B', alloys[1], 'C', Items.multi, 'I', new ItemStack(Item.dyePowder, 1, 15), 'O', Block.obsidian, 'P', Block.pistonBase); // Printer
		addRecipe(new ItemStack(Blocks.machine, 1, 4), "ADA", "BGB", "ECE", 'A', alloys[4], 'B', alloys[5], 'C', Items.multi, 'D', Item.diamond, 'E', Item.enderPearl, 'G', Block.thinGlass); // X-ray
		addRecipeDict(upgrades[0], "AGA", " U ", "ACA", 'A', alloys[2], 'C', "basicCircuit", 'G', Item.ingotGold, 'U', new ItemStack(Items.multi, 1, 1)); // Speed I
		addRecipeDict(upgrades[1], "ADA", " U ", "ACA", 'A', alloys[5], 'C', "eliteCircuit", 'D', Item.diamond, 'U', upgrades[0]); // Speed II
		addRecipeDict(upgrades[2], "AIA", " U ", "ACA", 'A', alloys[1], 'C', "basicCircuit", 'I', Item.shovelSteel, 'U', new ItemStack(Items.multi, 1, 1)); // Efficiency I
		addRecipeDict(upgrades[3], "AGA", " U ", "ACA", 'A', alloys[4], 'C', "advancedCircuit", 'G', Item.shovelGold, 'U', upgrades[2]); // Efficiency II
		addRecipeDict(upgrades[4], "ASA", " U ", "ACA", 'A', alloys[0], 'C', "basicCircuit", 'S', Block.chest, 'U', new ItemStack(Items.multi, 1, 1)); // Capacity I
		addRecipeDict(upgrades[5], "ASA", " U ", "ACA", 'A', alloys[3], 'C', "advancedCircuit", 'S', Block.chest, 'U', upgrades[4]); // Capacity II
		addRecipeDict(upgrades[6], "AIA", " U ", "ACA", 'A', alloys[3], 'C', "basicCircuit", 'I', Item.swordSteel, 'U', new ItemStack(Items.multi, 1, 1)); // Range I
		addRecipeDict(upgrades[7], "AGA", " U ", "ACA", 'A', alloys[5], 'C', "eliteCircuit", 'G', Item.swordGold, 'U', upgrades[6]); // Range II
		addRecipeDict(upgrades[8], "AEA", " U ", "ACA", 'A', alloys[1], 'C', "basicCircuit", 'E', Item.enderPearl, 'U', new ItemStack(Items.multi, 1, 1)); // Wireless
		addRecipeDict(upgrades[9], "ABA", " U ", "ACA", 'A', alloys[2], 'B', "battery", 'C', "basicCircuit", 'U', new ItemStack(Items.multi, 1, 1)); // Electricity Capacity I
		addRecipeDict(upgrades[10], "ABA", " U ", "ACA", 'B', alloys[4], 'B', "battery", 'C', "advancedCircuit", 'U', upgrades[9]); // Electricity Capacity II
		addRecipeDict(new ItemStack(Items.gps), " W ", "RSR", 'R', Item.redstone, 'S', "basicCircuit", 'W', upgrades[8]);
		addRecipe(new ItemStack(Items.alloyBook), "R", "B", 'B', Item.writableBook, 'R', Item.redstone);
		addSmelting(Blocks.ore.blockID, 0, new ItemStack(Items.ingot), 0.6F);
		addSmelting(Blocks.ore.blockID, 1, new ItemStack(Items.ingot, 1, 1), 0.6F);
		addSmelting(Blocks.ore.blockID, 2, new ItemStack(Items.ingot, 1, 2), 0.7F);
		addSmelting(Blocks.ore.blockID, 3, new ItemStack(Items.ingot, 1, 3), 0.7F);
		addSmelting(Blocks.ore.blockID, 4, new ItemStack(Items.ingot, 1, 4), 0.85F);
		addSmelting(Blocks.ore.blockID, 5, new ItemStack(Items.ingot, 1, 5), 0.85F);
		addSmelting(Blocks.ore.blockID, 6, new ItemStack(Items.ingot, 1, 6), 0.85F);
		addSmelting(Blocks.ore.blockID, 7, new ItemStack(Items.ingot, 1, 7), 1.0F);
	}

	public void initTileEntities() {
		GameRegistry.registerTileEntity(TileEntityComputer.class, "Computer");
		GameRegistry.registerTileEntity(TileEntityMetalForge.class, "MetalForge");
		GameRegistry.registerTileEntity(TileEntityAnalyzer.class, "Analyzer");
		GameRegistry.registerTileEntity(TileEntityPrinter.class, "Printer");
		GameRegistry.registerTileEntity(TileEntityXray.class, "Xray");
		TEHelper.addDetectable(Block.oreCoal, 1);
		TEHelper.addDetectable(Block.oreIron, 2);
		TEHelper.addDetectable(Block.oreGold, 6);
		TEHelper.addDetectable(Block.oreDiamond, 8);
		TEHelper.addDictDetectables("oreZinc", 3);
		TEHelper.addDictDetectables("oreMagnesium", 4);
		TEHelper.addDictDetectables("oreScandium", 5);
		TEHelper.addDictDetectables("oreTantalum", 6);
		TEHelper.addDetectable(Blocks.ore, 4, 7);
		TEHelper.addDetectable(Blocks.ore, 5, 8);
		TEHelper.addDetectable(Blocks.ore, 6, 9);
		TEHelper.addDetectable(Blocks.ore, 7, 10);
		TEHelper.addDictDetectables("oreCopper", 2);
		TEHelper.addDictDetectables("oreTin", 2);
	}

	public void initHandlers() {
		GameRegistry.registerWorldGenerator(new WorldGenHandler());
		MinecraftForge.EVENT_BUS.register(new EventHandler());
		NetworkRegistry.instance().registerGuiHandler(InfiniteAlloys.instance, new GuiHandler());
	}

	public void initAchievements() {
		InfiniteAlloys.achievements[0] = new Achievement(2000, "craftMetalForge", 0, 0, new ItemStack(Blocks.machine, 1, 1), null).registerAchievement();
		for(int i = 1; i <= References.validAlloyCount; i++)
			InfiniteAlloys.achievements[i] = new Achievement(2000 + i, "smeltAlloy" + i, 2 * i, 0, new ItemStack(Items.alloyIngot, 1, i), InfiniteAlloys.achievements[i - 1]).registerAchievement();
		InfiniteAlloys.achPage = new AchievementPage("Infinite Alloys", InfiniteAlloys.achievements);
		AchievementPage.registerAchievementPage(InfiniteAlloys.achPage);
	}

	public void initRendering() {}

	protected void addName(Object obj, String... keys) {
		String name = "";
		for(String key : keys)
			name = name + LanguageRegistry.instance().getStringLocalization(key);
		LanguageRegistry.addName(obj, name);
	}

	private void addRecipe(ItemStack result, Object... params) {
		GameRegistry.addRecipe(result, params);
	}

	private void addRecipeDict(ItemStack result, Object... params) {
		GameRegistry.addRecipe(new ShapedOreRecipe(result, params));
	}

	private void addSmelting(int inputID, int inputDamage, ItemStack output, float experience) {
		FurnaceRecipes.smelting().addSmelting(inputID, inputDamage, output, experience);
	}
}
