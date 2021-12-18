/*******************************************************************************
 *******************************************************************************/
package fr.fifoube.items;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;

import java.util.List;

public class ItemGoldNuggetSubs extends Item{
	
	public ItemGoldNuggetSubs(Properties properties) {
		super(properties);
	}
	
	@Override
	public void addInformation(ItemStack stack, World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		
		if(stack.hasTag())
		{
			if(stack.getTag().contains("weight"))
			{
				String weight = stack.getTag().getString("weight");
				tooltip.add(new StringTextComponent(I18n.format("title.weight") + weight));
			}
		}
	}

}
