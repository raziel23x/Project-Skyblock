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

import static raziel23x.projectskyblock.init.ModEntityType.CLAYGENERATOR_TILE;

public class ClayGeneratorTile extends TileEntity implements ITickableTileEntity {
    protected ItemStackHandler handler = new ItemStackHandler() {
        @Override
        public boolean isItemValid(int slot, @Nonnull ItemStack stack) {return stack.getItem() == Items.CLAY;}
    };

    private int ticks;
    public ClayGeneratorTile() {
        super(CLAYGENERATOR_TILE.get());
    }

    @Override
    public void tick() {
        if (!world.isRemote) {
            ticks++;

            if (ticks == 10) {
                ticks = 0;

                ItemHandlerHelper.insertItemStacked(handler, new ItemStack(Items.CLAY, 1), false);
                TileEntity tile = world.getTileEntity(pos.offset(Direction.UP));

                if (tile != null && tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, Direction.DOWN).isPresent()) {
                    IItemHandler outHandler = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, Direction.DOWN).orElse(null);

                    if (handler.getStackInSlot(0) != ItemStack.EMPTY) {
                        ItemStack stack = ItemHandlerHelper.insertItem(outHandler, new ItemStack(Items.CLAY, 1), true);

                        if (stack == ItemStack.EMPTY) {
                            ItemHandlerHelper.insertItem(outHandler, handler.extractItem(0, 1, false), false);
                            markDirty();
                        }
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
