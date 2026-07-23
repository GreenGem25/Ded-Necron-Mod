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
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.StructureTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.storage.loot.LootTable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class TarakanEntity extends Animal implements GeoEntity {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public TarakanEntity(EntityType<? extends Animal> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new PanicGoal(this, 1.25D));

        this.goalSelector.addGoal(3, new BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(4, new TemptGoal(this, 1.25D,
                Ingredient.of(ModItems.BAMBOO_CIGARETTE.get()), false));

        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 10.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.3D);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.COUGHING_SOUND.get();
    }

    @Override
    protected SoundEvent getHurtSound(net.minecraft.world.damagesource.@NotNull DamageSource damageSource) {
        return SoundEvents.SPIDER_STEP;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.SPIDER_DEATH;
    }

    @Override
    public boolean isFood(ItemStack itemStack) {
        return itemStack.is(ModItems.BAMBOO_CIGARETTE);
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(@NotNull ServerLevel serverLevel, @NotNull AgeableMob ageableMob) {
        TarakanEntity baby = new TarakanEntity(ModEntities.TARAKAN.get(), serverLevel);

        baby.setAge(-24000);

        return baby;
    }

    @Override
    public @NotNull ResourceKey<LootTable> getDefaultLootTable() {
        return ResourceKey.create(
                Registries.LOOT_TABLE,
                ResourceLocation.fromNamespaceAndPath(DedNecronMod.MODID, "entities/tarakan")
        );
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "walk_controller", 5, event -> {
            if (event.isMoving()) {
                event.getController().setAnimation(
                        RawAnimation.begin().thenLoop("walk"));
                return PlayState.CONTINUE;
            }
            return PlayState.STOP;
        }));
        controllers.add(new AnimationController<>(this, "idle_controller", 5, event -> {
            event.getController().setAnimation(
                    RawAnimation.begin().thenLoop("idle"));
            return PlayState.CONTINUE;
        }));
    }



    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    public static boolean canSpawn(EntityType<TarakanEntity> type,
                                   ServerLevelAccessor level,
                                   MobSpawnType spawnType,
                                   BlockPos pos,
                                   RandomSource random) {

        var structureManager = level.getLevel().structureManager();
        boolean inVillage = structureManager.getStructureWithPieceAt(pos, StructureTags.VILLAGE).isValid();
        if (inVillage) {
            return true;
        }

        if (pos.getY() < 50
                && !level.canSeeSky(pos))
        {
        }

        return pos.getY() < 50
                && !level.canSeeSky(pos);
    }
}
