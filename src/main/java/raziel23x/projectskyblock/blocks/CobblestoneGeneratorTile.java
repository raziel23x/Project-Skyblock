package raziel23x.projectskyblock.blocks;

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

import static raziel23x.projectskyblock.utils.RegistryHandler.COBBLEGENERATOR_TILE;

public class CobblestoneGeneratorTile extends TileEntity implements ITickableTileEntity {

    private ItemStackHandler handler;
    private int tick;

    public CobblestoneGeneratorTile() {
        super(COBBLEGENERATOR_TILE.get());
    }

    @Override
    public void tick() {
        tick++;
        if (tick == 10) {
            tick=0;

            ItemStack stack = new ItemStack(Items.COBBLESTONE, 1);
            ItemHandlerHelper.insertItemStacked(getHandler(), stack, false);

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
        CompoundNBT compound = getHandler().serializeNBT();
        tag.put("inv", compound);
        return super.write(tag);
    }

    private ItemStackHandler getHandler(){
        if(handler == null) {
            handler = new ItemStackHandler(1);
        }
        return handler;
    }

    @Nonnull
    @Override
    public <T>LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY){
            return LazyOptional.of(() -> (T) getHandler());
        }
        return super.getCapability(cap, side);
    }
}
