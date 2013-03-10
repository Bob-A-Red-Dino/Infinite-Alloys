package infinitealloys.item;

import infinitealloys.core.FuncHelper;
import infinitealloys.core.InfiniteAlloys;
import infinitealloys.core.References;
import java.util.List;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemAlloyIngot extends ItemIA {

	public ItemAlloyIngot(int id, int texture) {
		super(id, texture);
		setHasSubtypes(true);
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		return "IAalloyIngot";
	}

	@Override
	public boolean getShareTag() {
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack itemstack, EntityPlayer player, List list, boolean b) {
		float[] metalMasses = new float[References.metalCount];
		float totalMass = 0;
		int alloy;
		int damage = itemstack.getItemDamage() - 1;
		if(itemstack.hasTagCompound())
			alloy = itemstack.getTagCompound().getInteger("alloy");
		else if(damage >= 0 && damage < References.validAlloyCount)
			alloy = InfiniteAlloys.instance.worldData.getValidAlloys()[damage];
		else
			return;
		for(int i = 0; i < References.metalCount; i++) {
			metalMasses[i] = FuncHelper.intAtPos(References.alloyRadix, References.metalCount, alloy, i);
			totalMass += metalMasses[i];
		}
		for(int i = 0; i < References.metalCount; i++) {
			float percentage = Math.round(metalMasses[i] / totalMass * 10000F) / 100F;
			if(percentage != 0)
				list.add(percentage + "% " + FuncHelper.getLoc("metal." + References.metalNames[References.metalCount - 1 - i] + ".name"));
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getColorFromItemStack(ItemStack itemstack, int renderPass) {
		int colorCount = 0;
		int redTot = 0, blueTot = 0, greenTot = 0;
		int alloy = 0;
		if(itemstack.hasTagCompound())
			alloy = itemstack.getTagCompound().getInteger("alloy");
		else if(itemstack.getItemDamage() > 0)
			alloy = InfiniteAlloys.instance.worldData.getValidAlloys()[itemstack.getItemDamage() - 1];
		for(int i = 0; i < References.metalCount; i++) {
			for(int j = 0; j < FuncHelper.intAtPos(References.alloyRadix, References.metalCount, alloy, i); j++) {
				int ingotColor = References.metalColors[References.metalCount - 1 - i];
				colorCount++;
				redTot += ingotColor >> 16 & 255;
				greenTot += ingotColor >> 8 & 255;
				blueTot += ingotColor & 255;
			}
		}
		int redAvg = 0, greenAvg = 0, blueAvg = 0;
		if(colorCount != 0) {
			redAvg = redTot / colorCount;
			greenAvg = greenTot / colorCount;
			blueAvg = blueTot / colorCount;
		}
		return (redAvg << 16) + (greenAvg << 8) + blueAvg;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(int id, CreativeTabs creativetabs, List list) {}
}