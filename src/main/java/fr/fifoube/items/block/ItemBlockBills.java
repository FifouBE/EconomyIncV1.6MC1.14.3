package fr.fifoube.items.block;

import fr.fifoube.blocks.blockentity.specialrenderer.ItemBillsSpecialRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.IItemRenderProperties;

import java.util.function.Consumer;

public class ItemBlockBills extends BlockItem {


    public ItemBlockBills(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public void initializeClient(Consumer<IItemRenderProperties> consumer) {
        super.initializeClient(consumer);
        consumer.accept(RenderItemBlockBills.INSTANCE);
    }

    static class RenderItemBlockBills implements IItemRenderProperties {

        public static RenderItemBlockBills INSTANCE = new RenderItemBlockBills();

        @Override
        public BlockEntityWithoutLevelRenderer getItemStackRenderer() {
            return new ItemBillsSpecialRenderer(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
        }

    }
}