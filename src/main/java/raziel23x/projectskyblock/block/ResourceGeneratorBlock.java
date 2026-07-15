package raziel23x.projectskyblock.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;
import raziel23x.projectskyblock.blockentity.ResourceGeneratorBlockEntity;
import raziel23x.projectskyblock.config.GeneratorConfig;
import raziel23x.projectskyblock.registry.ModBlockEntities;

public final class ResourceGeneratorBlock extends BaseEntityBlock {
    public enum Output { COBBLESTONE, WATER, LAVA }

    private final Output output;

    public ResourceGeneratorBlock(Properties properties, Output output) {
        super(properties);
        this.output = output;
    }

    public Output getOutput() {
        return output;
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return simpleCodec(properties -> new ResourceGeneratorBlock(properties, output));
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ResourceGeneratorBlockEntity(pos, state);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(
            Level level,
            BlockState state,
            BlockEntityType<T> blockEntityType) {
        if (level.isClientSide) {
            return null;
        }

        return createTickerHelper(
                blockEntityType,
                ModBlockEntities.RESOURCE_GENERATOR.get(),
                ResourceGeneratorBlockEntity::serverTick
        );
    }

    @Override
    public void animateTick(
            BlockState state,
            Level level,
            BlockPos pos,
            RandomSource random) {
        if (!GeneratorConfig.PARTICLES_ENABLED.get()) {
            return;
        }

        int chance = switch (output) {
            case COBBLESTONE -> GeneratorConfig.COBBLESTONE_PARTICLE_CHANCE.get();
            case WATER -> GeneratorConfig.WATER_PARTICLE_CHANCE.get();
            case LAVA -> GeneratorConfig.LAVA_PARTICLE_CHANCE.get();
        };

        if (chance <= 0 || random.nextInt(chance) != 0) {
            return;
        }

        double x = pos.getX() + 0.35D + random.nextDouble() * 0.30D;
        double y = pos.getY() + 0.55D + random.nextDouble() * 0.25D;
        double z = pos.getZ() + 0.35D + random.nextDouble() * 0.30D;

        switch (output) {
            case COBBLESTONE -> {
                level.addParticle(
                        ParticleTypes.POOF,
                        x, y, z,
                        0.0D, 0.015D, 0.0D
                );

                if (random.nextInt(4) == 0) {
                    level.addParticle(
                            ParticleTypes.ASH,
                            x, y, z,
                            0.0D, 0.01D, 0.0D
                    );
                }
            }

            case WATER -> {
                level.addParticle(
                        ParticleTypes.SPLASH,
                        x, y, z,
                        0.0D, 0.02D, 0.0D
                );

                if (random.nextInt(5) == 0) {
                    level.addParticle(
                            ParticleTypes.BUBBLE,
                            x, y, z,
                            0.0D, 0.025D, 0.0D
                    );
                }
            }

            case LAVA -> {
                level.addParticle(
                        ParticleTypes.SMOKE,
                        x, y, z,
                        0.0D, 0.025D, 0.0D
                );

                if (random.nextInt(3) == 0) {
                    level.addParticle(
                            ParticleTypes.LAVA,
                            x, y, z,
                            0.0D, 0.0D, 0.0D
                    );
                }
            }
        }
    }

    @Override
    protected InteractionResult useWithoutItem(
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            BlockHitResult hitResult) {
        if (output != Output.COBBLESTONE) {
            return InteractionResult.PASS;
        }

        if (!level.isClientSide
                && player instanceof ServerPlayer serverPlayer
                && level.getBlockEntity(pos) instanceof ResourceGeneratorBlockEntity generator) {
            ItemStack cobblestone = generator.takeCobblestone();

            if (!cobblestone.isEmpty()) {
                giveOrDrop(serverPlayer, cobblestone);
                level.playSound(
                        null,
                        pos,
                        SoundEvents.STONE_PLACE,
                        SoundSource.BLOCKS,
                        0.55F,
                        1.15F
                );
            }
        }

        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    protected ItemInteractionResult useItemOn(
            ItemStack stack,
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            InteractionHand hand,
            BlockHitResult hitResult) {
        if (output == Output.COBBLESTONE || !stack.is(Items.BUCKET)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        if (!level.isClientSide
                && player instanceof ServerPlayer serverPlayer
                && level.getBlockEntity(pos) instanceof ResourceGeneratorBlockEntity generator
                && generator.takeBucket()) {
            ItemStack result = new ItemStack(
                    output == Output.WATER ? Items.WATER_BUCKET : Items.LAVA_BUCKET
            );

            if (!serverPlayer.getAbilities().instabuild) {
                stack.shrink(1);
            }

            giveOrDrop(serverPlayer, result);
            level.playSound(
                    null,
                    pos,
                    SoundEvents.BUCKET_FILL,
                    SoundSource.BLOCKS,
                    0.8F,
                    1.0F
            );
        }

        return ItemInteractionResult.sidedSuccess(level.isClientSide);
    }

    private static void giveOrDrop(ServerPlayer player, ItemStack stack) {
        if (!player.getInventory().add(stack)) {
            player.drop(stack, false);
        }
    }
}
