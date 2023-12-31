package com.github.alexthe666.iceandfire.entity.ai;

import java.util.EnumSet;
import java.util.List;
import java.util.Random;

import com.github.alexthe666.iceandfire.entity.EntityHippogryph;
import com.github.alexthe666.iceandfire.item.ItemHippogryphEgg;

import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

public class HippogryphAIMate extends Goal {

    private final EntityHippogryph hippo;
    World world;
    int spawnBabyDelay;
    double moveSpeed;
    private EntityHippogryph targetMate;

    public HippogryphAIMate(EntityHippogryph animal, double speedIn) {
        this(animal, speedIn, animal.getClass());
    }

    public HippogryphAIMate(EntityHippogryph hippogryph, double speed, Class<? extends AnimalEntity> mate) {
        this.hippo = hippogryph;
        this.world = hippogryph.world;
        this.moveSpeed = speed;
        this.setMutexFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean shouldExecute() {
        if (!this.hippo.isInLove() || this.hippo.isQueuedToSit()) {
            return false;
        } else {
            this.targetMate = this.getNearbyMate();
            return this.targetMate != null;
        }
    }

    @Override
    public boolean shouldContinueExecuting() {
        return this.targetMate.isAlive() && this.targetMate.isInLove() && this.spawnBabyDelay < 60;
    }

    @Override
    public void resetTask() {
        this.targetMate = null;
        this.spawnBabyDelay = 0;
    }

    @Override
    public void tick() {
        this.hippo.getLookController().setLookPositionWithEntity(this.targetMate, 10.0F,
            this.hippo.getVerticalFaceSpeed());
        this.hippo.getNavigator().tryMoveToEntityLiving(this.targetMate, this.moveSpeed);
        ++this.spawnBabyDelay;

        if (this.spawnBabyDelay >= 60 && this.hippo.getDistanceSq(this.targetMate) < 9.0D) {
            this.spawnBaby();
        }
    }

    private EntityHippogryph getNearbyMate() {
        List<EntityHippogryph> list = this.world.getEntitiesWithinAABB(EntityHippogryph.class,
            this.hippo.getBoundingBox().grow(8.0D));
        double d0 = Double.MAX_VALUE;
        EntityHippogryph entityanimal = null;

        for (EntityHippogryph entityanimal1 : list) {
            if (this.hippo.canMateWith(entityanimal1) && this.hippo.getDistanceSq(entityanimal1) < d0) {
                entityanimal = entityanimal1;
                d0 = this.hippo.getDistanceSq(entityanimal1);
            }
        }

        return entityanimal;
    }

    private void spawnBaby() {
        ItemEntity egg = new ItemEntity(world, this.hippo.getPosX(), this.hippo.getPosY(), this.hippo.getPosZ(),
            ItemHippogryphEgg.createEggStack(this.hippo.getEnumVariant(), this.targetMate.getEnumVariant()));
        this.hippo.setGrowingAge(6000);
        this.targetMate.setGrowingAge(6000);
        this.hippo.resetInLove();
        this.targetMate.resetInLove();
        egg.setLocationAndAngles(this.hippo.getPosX(), this.hippo.getPosY(), this.hippo.getPosZ(), 0.0F, 0.0F);
        if (!world.isRemote) {
            this.world.addEntity(egg);
        }
        Random random = this.hippo.getRNG();

        for (int i = 0; i < 7; ++i) {
            final double d0 = random.nextGaussian() * 0.02D;
            final double d1 = random.nextGaussian() * 0.02D;
            final double d2 = random.nextGaussian() * 0.02D;
            final double d3 = random.nextDouble() * this.hippo.getWidth() * 2.0D - this.hippo.getWidth();
            final double d4 = 0.5D + random.nextDouble() * this.hippo.getHeight();
            final double d5 = random.nextDouble() * this.hippo.getWidth() * 2.0D - this.hippo.getWidth();
            this.world.addParticle(ParticleTypes.HEART, this.hippo.getPosX() + d3, this.hippo.getPosY() + d4,
                this.hippo.getPosZ() + d5, d0, d1, d2);
        }

        if (this.world.getGameRules().getBoolean(GameRules.DO_MOB_LOOT)) {
            this.world.addEntity(new ExperienceOrbEntity(this.world, this.hippo.getPosX(), this.hippo.getPosY(),
                this.hippo.getPosZ(), random.nextInt(7) + 1));
        }
    }
}
