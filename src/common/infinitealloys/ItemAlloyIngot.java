package infinitealloys;

import java.util.ArrayList;
import java.util.List;
import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;
import net.minecraft.src.CreativeTabs;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ItemStack;

public class ItemAlloyIngot extends ItemIA {

	public ItemAlloyIngot(int id, int texture) {
		super(id, texture);
		setHasSubtypes(true);
	}

	@Override
	public String getItemNameIS(ItemStack stack) {
		return "iaAlloyIngot";
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
		int alloy = itemstack.getTagCompound().getInteger("alloy");
		for(int i = 0; i < References.metalCount; i++) {
			metalMasses[i] = InfiniteAlloys.intAtPosRadix(References.alloyRadix, References.metalCount, alloy, i);
			totalMass += metalMasses[i];
		}
		for(int i = 0; i < References.metalCount; i++) {
			float percentage = Math.round(metalMasses[i] / totalMass * 10000F) / 100F;
			if(percentage != 0)
				list.add(percentage + "% " + InfiniteAlloys.getStringLocalization("metal." + References.metalNames[References.metalCount - 1 - i] + ".name"));
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int func_82790_a(ItemStack itemstack, int renderPass) {
		ArrayList<Integer> redVals = new ArrayList<Integer>();
		ArrayList<Integer> greenVals = new ArrayList<Integer>();
		ArrayList<Integer> blueVals = new ArrayList<Integer>();
		int alloy = itemstack.getTagCompound().getInteger("alloy");
		for(int i = 0; i < References.metalCount; i++) {
			for(int j = 0; j < InfiniteAlloys.intAtPosRadix(References.alloyRadix, References.metalCount, alloy, i); j++) {
				String ingotColor = InfiniteAlloys.addLeadingZeros(Integer.toHexString(References.metalColors[References.metalCount - 1 - i]), 6);
				redVals.add(Integer.parseInt(ingotColor.substring(0, 2), 16));
				greenVals.add(Integer.parseInt(ingotColor.substring(2, 4), 16));
				blueVals.add(Integer.parseInt(ingotColor.substring(4), 16));
			}
		}
		int redAvg = 0, greenAvg = 0, blueAvg = 0;
		if(!redVals.isEmpty()) {
			for(int red : redVals)
				redAvg += red;
			redAvg /= redVals.size();
		}
		if(!greenVals.isEmpty()) {
			for(int green : greenVals)
				greenAvg += green;
			greenAvg /= greenVals.size();
		}
		if(!blueVals.isEmpty()) {
			for(int blue : blueVals)
				blueAvg += blue;
			blueAvg /= blueVals.size();
		}
		return Integer.parseInt(Integer.toHexString(redAvg) + Integer.toHexString(greenAvg) + Integer.toHexString(blueAvg), 16);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(int id, CreativeTabs creativetabs, List list) {}
}
