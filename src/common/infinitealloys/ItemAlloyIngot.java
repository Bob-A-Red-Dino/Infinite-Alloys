package infinitealloys;

import java.util.ArrayList;
import java.util.List;
import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;
import net.minecraft.src.CreativeTabs;
import net.minecraft.src.ItemStack;

public class ItemAlloyIngot extends ItemIA {

	public ItemAlloyIngot(int id, int texture) {
		super(id, texture);
		setCreativeTab(CreativeTabs.tabMaterials);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack itemstack, List list) {
		float[] metalMasses = new float[IAValues.metalCount];
		float totalMass = 0;
		for(int i = 0; i < IAValues.metalCount; i++) {
			metalMasses[i] = InfiniteAlloys.intAtPositionOctal(IAValues.metalCount, itemstack.getItemDamage(), i);// *
																													// IAValues.densities[i];
			totalMass += metalMasses[i];
		}
		for(int i = 0; i < IAValues.metalCount; i++) {
			float percentage = Math.round(metalMasses[i] / totalMass * 10000F) / 100F;
			if(percentage != 0)
				list.add(percentage + "% " + IAValues.metalNames[i]);
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(int id, CreativeTabs creativetabs, List list) {
		for(int i = 0; i < 100; i++)
			list.add(new ItemStack(id, 1, i));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getColorFromDamage(int damage, int renderPass) {
		ArrayList<Integer> redVals = new ArrayList<Integer>();
		ArrayList<Integer> greenVals = new ArrayList<Integer>();
		ArrayList<Integer> blueVals = new ArrayList<Integer>();
		for(int i = 0; i < IAValues.metalCount; i++) {
			for(int j = 0; j < InfiniteAlloys.intAtPositionOctal(9, damage, i); j++) {
				String ingotColor = InfiniteAlloys.addLeadingZeros(Integer.toString(IAValues.ingotColors[i]), 6);
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
		if(damage == 9)
			System.out.println("Damage: " + damage + " Color: " + Integer.toHexString(redAvg) + Integer.toHexString(greenAvg) + Integer.toHexString(blueAvg));
		return Integer.parseInt(Integer.toHexString(redAvg) + Integer.toHexString(greenAvg) + Integer.toHexString(blueAvg), 16);
	}

	@Override
	public String getItemNameIS(ItemStack itemstack) {
		return "IA Alloy Ingot";
	}
}
