package com.ded.necronmod.entity;

import com.ded.necronmod.DedNecronMod;
import com.ded.necronmod.init.ModEntities;
import com.ded.necronmod.init.ModItems;
import com.ded.necronmod.init.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootTable;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.Nullable;
import java.util.UUID;

public class CatCaterpillarEntity extends TamableAnimal implements GeoEntity {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public CatCaterpillarEntity(EntityType<? extends TamableAnimal> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public void registerControllers(software.bernie.geckolib.animation.AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new software.bernie.geckolib.animation.AnimationController<>(this, "controller", 5, event -> {
            if (this.isInSittingPose()) {
                return event.setAndContinue(software.bernie.geckolib.animation.RawAnimation.begin().thenLoop("sit"));
            }

            if (event.isMoving()) {
                return event.setAndContinue(software.bernie.geckolib.animation.RawAnimation.begin().thenLoop("walk"));
            }

            return event.setAndContinue(software.bernie.geckolib.animation.RawAnimation.begin().thenLoop("idle"));
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide()) {
            this.setInSittingPose(this.isOrderedToSit());
        }
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.CAT_CATERPILLAR_AMBIENT.get();
    }

    @Override
    protected SoundEvent getHurtSound(net.minecraft.world.damagesource.@NotNull DamageSource damageSource) {
        return net.minecraft.sounds.SoundEvents.CAT_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return net.minecraft.sounds.SoundEvents.CAT_DEATH;
    }

    @Override
    public @NotNull ResourceKey<LootTable> getDefaultLootTable() {
        return ResourceKey.create(
                Registries.LOOT_TABLE,
                ResourceLocation.fromNamespaceAndPath(DedNecronMod.MODID, "entities/cat_caterpillar")
        );
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(3, new CaterpillarSitOnBedGoal(this, 1.1D, 8));
        this.goalSelector.addGoal(4, new FollowOwnerGoal(this,
                1.1D, 10.0F, 2.0F));
        this.goalSelector.addGoal(5, new PanicGoal(this, 1.25D));

        this.goalSelector.addGoal(3, new BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(4, new TemptGoal(this, 1.25D,
                Ingredient.of(ItemTags.LEAVES), false));

        this.goalSelector.addGoal(6, new EatGrassGoal(this));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(9, new RandomLookAroundGoal(this));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 12.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.2D);
    }

    @Override
    public boolean isFood(ItemStack itemStack) {
        return itemStack.is(ItemTags.LEAVES);
    }

    @Override
    public @NotNull InteractionResult mobInteract(Player player, @NotNull InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);

        if (itemStack.is(ItemTags.LEAVES)) {
            if (this.isTame()) {
                boolean processed = false;

                if (this.getHealth() < this.getMaxHealth()) {
                    this.heal(3.0F);
                    processed = true;
                }

                else if (this.getAge() == 0 && this.canFallInLove()) {
                    this.setInLove(player);
                    processed = true;
                }

                if (processed) {
                    if (!player.getAbilities().instabuild) {
                        itemStack.shrink(1);
                    }

                    if (this.level().isClientSide()) {
                        this.level().addParticle(
                                net.minecraft.core.particles.ParticleTypes.HEART,
                                this.getRandomX(1.0D), this.getRandomY() + 0.5D, this.getRandomZ(1.0D),
                                0.0D, 0.0D, 0.0D
                        );
                    }
                    return InteractionResult.sidedSuccess(this.level().isClientSide());
                }

            } else {
                if (!player.getAbilities().instabuild) {
                    itemStack.shrink(1);
                }

                if (!this.level().isClientSide()) {
                    if (this.random.nextInt(3) == 0) {
                        this.tame(player);
                        this.navigation.stop();
                        this.setTarget(null);
                        this.setOrderedToSit(true);
                        this.level().broadcastEntityEvent(this, (byte)7);
                    } else {
                        this.level().broadcastEntityEvent(this, (byte)6);
                    }
                }
                return InteractionResult.sidedSuccess(this.level().isClientSide());
            }
        }

        if (this.isTame() && this.isOwnedBy(player)) {
            if (!this.level().isClientSide()) {
                boolean willSit = !this.isOrderedToSit();
                this.setOrderedToSit(willSit);
                this.setInSittingPose(willSit);

                if (willSit) {
                    this.level().playSound(
                            null,
                            this.getX(), this.getY(), this.getZ(),
                            ModSounds.CAT_CATERPILLAR_SIT.get(),
                            this.getSoundSource(),
                            1.0F,
                            (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F
                    );
                }
            }
            return InteractionResult.sidedSuccess(this.level().isClientSide());
        }

        return super.mobInteract(player, hand);
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(@NotNull ServerLevel serverLevel, @NotNull AgeableMob ageableMob) {
        CatCaterpillarEntity baby = new CatCaterpillarEntity(ModEntities.CAT_CATERPILLAR.get(), serverLevel);

        UUID ownerUUID = this.getOwnerUUID();

        if (ownerUUID == null && ageableMob instanceof TamableAnimal tamableParent) {
            ownerUUID = tamableParent.getOwnerUUID();
        }

        if (ownerUUID != null) {
            baby.setOwnerUUID(ownerUUID);
            baby.setTame(true, true);
        }

        return baby;
    }

    private static class EatGrassGoal extends Goal {
        private final Mob mob;
        private int timer;

        public EatGrassGoal(Mob mob) {
            this.mob = mob;
        }

        @Override
        public boolean canUse() {
            if (this.mob.getRandom().nextInt(this.mob.isBaby() ? 50 : 1000) != 0) return false;
            BlockPos pos = this.mob.blockPosition();
            return this.mob.level().getBlockState(pos).is(Blocks.SHORT_GRASS) ||
                    this.mob.level().getBlockState(pos).is(Blocks.FERN);
        }

        @Override
        public void start() {
            this.timer = 40;
            this.mob.level().broadcastEntityEvent(this.mob, (byte)10);
            this.mob.getNavigation().stop();
        }

        @Override
        public void stop() {
            this.timer = 0;
        }

        @Override
        public boolean canContinueToUse() {
            return this.timer > 0;
        }

        @Override
        public void tick() {
            this.timer = Math.max(0, this.timer - 1);
            if (this.timer == 4) {
                BlockPos pos = this.mob.blockPosition();
                if (this.mob.level().getBlockState(pos).is(Blocks.SHORT_GRASS) ||
                        this.mob.level().getBlockState(pos).is(Blocks.FERN)) {
                    if (!this.mob.level().isClientSide()) {
                        this.mob.level().destroyBlock(pos, false);
                    }
                }
            }
        }
    }

    private static class CaterpillarSitOnBedGoal extends MoveToBlockGoal {
        private final CatCaterpillarEntity caterpillar;

        public CaterpillarSitOnBedGoal(CatCaterpillarEntity caterpillar, double speed, int searchRange) {
            super(caterpillar, speed, searchRange, 6);
            this.caterpillar = caterpillar;
        }

        @Override
        public boolean canUse() {
            return this.caterpillar.isTame() && !this.caterpillar.isOrderedToSit() && super.canUse();
        }

        @Override
        public void start() {
            super.start();
            this.caterpillar.setInSittingPose(false);
        }

        @Override
        protected int nextStartTick(@NotNull PathfinderMob creature) {
            return 40;
        }

        @Override
        public void stop() {
            super.stop();
            this.caterpillar.setInSittingPose(false);
        }

        @Override
        public void tick() {
            super.tick();
            if (this.isReachedTarget()) {
                this.caterpillar.setInSittingPose(true);
            } else {
                this.caterpillar.setInSittingPose(false);
            }
        }

        @Override
        protected boolean isValidTarget(LevelReader level, BlockPos pos) {
            if (!level.isEmptyBlock(pos.above())) {
                return false;
            } else {
                BlockState state = level.getBlockState(pos);
                return state.is(BlockTags.BEDS);
            }
        }
    }
}