package com.ded.necronmod.entity;

import com.ded.necronmod.DedNecronMod;
import com.ded.necronmod.init.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
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

public class CatCaterpillarEntity extends TamableAnimal implements GeoEntity {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public CatCaterpillarEntity(EntityType<? extends TamableAnimal> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public void registerControllers(software.bernie.geckolib.animation.AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new software.bernie.geckolib.animation.AnimationController<>(this, "controller", 5, event -> {

            // ПРОВЕРЯЕМ СЕТЕВОЙ ФЛАГ ПОЗЫ (он теперь идеально синхронизирован!)
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
            // Синхронизируем позу сидения с приказом ИИ на клиенте
            this.setInSittingPose(this.isOrderedToSit());
        }
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.CAT_CATERPILLAR_AMBIENT.get();
    }

    // Также можно сразу переопределить звуки получения урона и смерти, если захочешь:
    @Override
    protected SoundEvent getHurtSound(net.minecraft.world.damagesource.@NotNull DamageSource damageSource) {
        return net.minecraft.sounds.SoundEvents.CAT_HURT; // Временно используем ванильный кошачий
    }

    @Override
    protected SoundEvent getDeathSound() {
        return net.minecraft.sounds.SoundEvents.CAT_DEATH;
    }

    @Override
    public ResourceKey<LootTable> getDefaultLootTable() {
        // Указываем путь к нашей кастомной таблице лута
        return ResourceKey.create(
                Registries.LOOT_TABLE,
                ResourceLocation.fromNamespaceAndPath(DedNecronMod.MODID, "entities/cat_caterpillar")
        );
    }

    // 1. Настройка ИИ (Поведения) моба
    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new SitWhenOrderedToGoal(this)); // Команда "сидеть"
        this.goalSelector.addGoal(3, new CaterpillarSitOnBedGoal(this, 1.1D, 8)); // Кастомный прыжок на кровать!
        this.goalSelector.addGoal(4, new FollowOwnerGoal(this, 1.1D, 10.0F, 2.0F)); // Следование за хозяином
        this.goalSelector.addGoal(5, new EatGrassGoal(this)); // Поедание мелкой травы
        this.goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 1.0D)); // Обычные прогулки
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
    }

    // 2. Характеристики (Здоровье и скорость)
    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 12.0D) // 6 сердечек
                .add(Attributes.MOVEMENT_SPEED, 0.2D); // Медленная, как гусеница
    }

    // ИСПРАВЛЕНИЕ ОШИБКИ 1: Метод, определяющий, что является едой (листва)
    @Override
    public boolean isFood(ItemStack itemStack) {
        // Проверяем по ванильному тегу предметов листвы
        return itemStack.is(ItemTags.LEAVES);
    }

    // 3. Логика взаимодействия (Приручение и лечение листвой)
    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);

        // ИСПРАВЛЕНИЕ ОШИБКИ 3: Используем ItemTags.LEAVES вместо несуществующего Tags.Items.LEAVES
        if (itemStack.is(ItemTags.LEAVES)) {
            if (this.isTame()) {
                // Если уже приручена и ранена — лечим
                if (this.getHealth() < this.getMaxHealth()) {
                    if (!player.getAbilities().instabuild) {
                        itemStack.shrink(1);
                    }
                    this.heal(3.0F); // Лечит 1.5 сердечка
                    return InteractionResult.sidedSuccess(this.level().isClientSide());
                }
            } else {
                // Если не приручена — пытаемся приручить (шанс 33%)
                if (!player.getAbilities().instabuild) {
                    itemStack.shrink(1);
                }

                if (!this.level().isClientSide()) {
                    if (this.random.nextInt(3) == 0) {
                        this.tame(player);
                        this.navigation.stop();
                        this.setTarget(null);
                        this.setOrderedToSit(true); // Сажаем после приручения
                        this.level().broadcastEntityEvent(this, (byte)7); // Сердечки
                    } else {
                        this.level().broadcastEntityEvent(this, (byte)6); // Дымок неудачи
                    }
                }
                return InteractionResult.sidedSuccess(this.level().isClientSide());
            }
        }

        if (this.isTame() && this.isOwnedBy(player)) {
            if (!this.level().isClientSide()) {
                // 1. Меняем логику ИИ на сервере
                boolean willSit = !this.isOrderedToSit();
                this.setOrderedToSit(willSit);

                // 2. Мгновенно синхронизируем это с клиентом через сетевой флаг позы!
                this.setInSittingPose(willSit);

                if (willSit) {
                    this.level().playSound(
                            null,                       // Если null, звук услышат ВСЕ игроки рядом
                            this.getX(), this.getY(), this.getZ(),
                            ModSounds.CAT_CATERPILLAR_SIT.get(),
                            this.getSoundSource(),
                            1.0F,                       // Громкость (1.0 = дефолт)
                            (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F // Слегка рандомим питч/высоту звука
                    );
                }
            }
            return InteractionResult.sidedSuccess(this.level().isClientSide());
        }

        return super.mobInteract(player, hand);
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel serverLevel, AgeableMob ageableMob) {
        // Для размножения (если в будущем потребуется)
        return null;
    }

    // --- ДОПОЛНИТЕЛЬНЫЕ ИИ-ЦЕЛИ ---

    // Кастомная цель для поедания травы
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
            return this.mob.level().getBlockState(pos).is(Blocks.SHORT_GRASS) || this.mob.level().getBlockState(pos).is(Blocks.FERN);
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
                if (this.mob.level().getBlockState(pos).is(Blocks.SHORT_GRASS) || this.mob.level().getBlockState(pos).is(Blocks.FERN)) {
                    if (!this.mob.level().isClientSide()) {
                        this.mob.level().destroyBlock(pos, false);
                    }
                }
            }
        }
    }

    // ИСПРАВЛЕНИЕ ОШИБКИ 2: Кастомная цель забегания на кровать, адаптированная под нашего моба
    private static class CaterpillarSitOnBedGoal extends MoveToBlockGoal {
        private final CatCaterpillarEntity caterpillar;

        public CaterpillarSitOnBedGoal(CatCaterpillarEntity caterpillar, double speed, int searchRange) {
            super(caterpillar, speed, searchRange, 6);
            this.caterpillar = caterpillar;
        }

        @Override
        public boolean canUse() {
            // Моб пойдет на кровать, только если приручен, не сидит по команде, и хозяин близко
            return this.caterpillar.isTame() && !this.caterpillar.isOrderedToSit() && super.canUse();
        }

        @Override
        public void start() {
            super.start();
            this.caterpillar.setInSittingPose(false);
        }

        @Override
        protected int nextStartTick(PathfinderMob creature) {
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
                return state.is(net.minecraft.tags.BlockTags.BEDS);
            }
        }
    }
}