package raziel23x.projectskyblock.gameObjs.blocks.generators;

import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static raziel23x.projectskyblock.init.ModEntityType.DIRTGENERATOR_TILE;

public class DirtGeneratorTile extends TileEntity implements ITickableTileEntity {

    private ItemStackHandler handler;
    private int ticks;

    public DirtGeneratorTile() {
        super(DIRTGENERATOR_TILE.get());
    }

    @Override
    public void tick() {
        ticks++;
        if (ticks == 10) {
            ticks = 0;

            ItemStack stack = new ItemStack(Items.DIRT, 1);
            ItemHandlerHelper.insertItemStacked(getItemHandler(), stack, false);
            assert world != null;
            TileEntity tile = world.getTileEntity(pos.offset(Direction.UP));

            if (tile != null && tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, Direction.DOWN).isPresent()) {
                IItemHandler ihandler = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, Direction.DOWN).orElse(null);

                if (handler.getStackInSlot(0) != ItemStack.EMPTY) {
                    ItemStack stack2 = handler.getStackInSlot(0).copy();
                    stack2.setCount(1);
                    ItemStack stack1 = ItemHandlerHelper.insertItem(ihandler, stack2, true);

                    if (stack1 == ItemStack.EMPTY || stack1.getCount() == 0) {
                        ItemHandlerHelper.insertItem(ihandler, handler.extractItem(0, 1, false), false);
                        markDirty();
                    }
                }
            }
        }
    }

    @Override
    public CompoundNBT write(CompoundNBT tag) {
        CompoundNBT compound = getItemHandler().serializeNBT();
        tag.put("inv", compound);
        return super.write(tag);
    }

    @Override
    public void read(BlockState state, CompoundNBT tag) {
        super.read(state, tag);
        if (tag.contains("inv")) {
            getItemHandler().deserializeNBT((CompoundNBT) tag.get("inv"));
        }
    }

    private ItemStackHandler getItemHandler() {
        if (handler == null) {
            handler = new ItemStackHandler(1);
        }
        return handler;
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return LazyOptional.of(() -> (T) getItemHandler());
        }
        return super.getCapability(cap, side);
    }
}
