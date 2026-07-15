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

        // Spawn around the outside of the cage instead of inside the source block.
        int side = random.nextInt(5);
        double x;
        double y;
        double z;

        if (side == 4) {
            // Above the open cage.
            x = pos.getX() + 0.18D + random.nextDouble() * 0.64D;
            y = pos.getY() + 1.03D + random.nextDouble() * 0.18D;
            z = pos.getZ() + 0.18D + random.nextDouble() * 0.64D;
        } else {
            y = pos.getY() + 0.25D + random.nextDouble() * 0.65D;

            switch (side) {
                case 0 -> {
                    x = pos.getX() - 0.04D;
                    z = pos.getZ() + 0.12D + random.nextDouble() * 0.76D;
                }
                case 1 -> {
                    x = pos.getX() + 1.04D;
                    z = pos.getZ() + 0.12D + random.nextDouble() * 0.76D;
                }
                case 2 -> {
                    x = pos.getX() + 0.12D + random.nextDouble() * 0.76D;
                    z = pos.getZ() - 0.04D;
                }
                default -> {
                    x = pos.getX() + 0.12D + random.nextDouble() * 0.76D;
                    z = pos.getZ() + 1.04D;
                }
            }
        }

        switch (output) {
            case COBBLESTONE -> {
                level.addParticle(
                        ParticleTypes.POOF,
                        x, y, z,
                        0.0D, 0.018D, 0.0D
                );

                if (random.nextInt(4) == 0) {
                    level.addParticle(
                            ParticleTypes.ASH,
                            x, y, z,
                            0.0D, 0.012D, 0.0D
                    );
                }
            }

            case WATER -> {
                // Bubble particles are effectively invisible in open air.
                // Use falling-water droplets and visible splashes outside the cage.
                level.addParticle(
                        ParticleTypes.DRIPPING_WATER,
                        x, y, z,
                        0.0D, -0.01D, 0.0D
                );

                level.addParticle(
                        ParticleTypes.SPLASH,
                        x + (random.nextDouble() - 0.5D) * 0.16D,
                        y + 0.03D,
                        z + (random.nextDouble() - 0.5D) * 0.16D,
                        0.0D,
                        0.035D,
                        0.0D
                );

                if (random.nextInt(3) == 0) {
                    level.addParticle(
                            ParticleTypes.FALLING_WATER,
                            x,
                            y + 0.08D,
                            z,
                            0.0D,
                            -0.015D,
                            0.0D
                    );
                }
            }

            case LAVA -> {
                level.addParticle(
                        ParticleTypes.SMOKE,
                        x, y, z,
                        0.0D, 0.03D, 0.0D
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
