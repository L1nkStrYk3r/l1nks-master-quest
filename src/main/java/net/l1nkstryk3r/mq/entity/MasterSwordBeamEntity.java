package net.l1nkstryk3r.mq.entity;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

/**
 * The custom entity class representing the Master Sword's beam attack.
 * <p>
 *     Extends ThrowableProjectile, which provides:
 *     <ul>
 *         <li>basic motion physics</li>
 *         <li>hit detection</li>
 *         <li>owner tracking (the player who fired it)</li>
 *     </ul>
 * </p>
 */
public class MasterSwordBeamEntity extends ThrowableProjectile {
    private static final int LIFETIME_TICKS = 40;
    private static final float DAMAGE = 10.0f;

    /** Standard constructor used internally by the game when spawning from saved data. */
    public MasterSwordBeamEntity(EntityType<? extends MasterSwordBeamEntity> type, Level world) {
        super(type, world);
    }

    /** Called every tick (20x per second). */
    @Override
    public void tick() {
        super.tick();

        // Despawn after a fixed time to avoid infinite entities
        if (tickCount > LIFETIME_TICKS) discard();
    }

    /**
     * Triggered when the beam collides with something (entity or block).
     */
    @Override
    protected void onHit(HitResult hitResult) {
        super.onHit(hitResult);

        if (!level().isClientSide) {
            switch (hitResult.getType()) {
                // When hitting an entity
                case ENTITY -> {
                    EntityHitResult entityHit = (EntityHitResult) hitResult;
                    Entity target = entityHit.getEntity();

                    // Prevent self-hits (ignore hitting the shooter)
                    if (target == getOwner()) return;

                    if (target instanceof LivingEntity livingTarget && getOwner() instanceof Player playerOwner) {
                        // Deal direct damage
                        livingTarget.hurt(level().damageSources().playerAttack(playerOwner), DAMAGE);

                        createParticles();

                        // Play a hit sound effect
                        level().playSound(
                            null,                               // audible to all players
                            blockPosition(),                    // Plays at impact location
                            SoundEvents.AMETHYST_BLOCK_HIT,     // built-in sound for "magical impact" vibe
                            SoundSource.PLAYERS,                // Categorized as a player-based SE
                            0.8f,                               // Volume
                            1.4f                                // Pitch
                        );
                    }
                }

                // When hitting a block, simply despawn
                case BLOCK -> {
                    createParticles();
                    discard();
                }

                // otherwise do nothing
                default -> {}
            }
        }
    }

    /**
     * Smooths out projectile motion interpolation between server updates.
     * Helps avoid choppy-looking movement on the client
     */
    @Override
    public void lerpMotion(double x, double y, double z) {
        setDeltaMovement(getDeltaMovement().add(
            (x - getDeltaMovement().x) * 0.5,
            (y - getDeltaMovement().y) * 0.5,
            (z - getDeltaMovement().z) * 0.5
        ));
    }

    /** No synced data parameters needed for this simple projectile. */
    @Override
    protected void defineSynchedData() {}

    /** Helper to create impact particles */
    private void createParticles() {
        // Create glowing particle explosion at impact
        ((ServerLevel) level()).sendParticles(
            ParticleTypes.GLOW,
            getX(), getY(), getZ(),
            15,                         // number of particles
            0.2, 0.2, 0.2,              // x,y,z random offset in blocks
            0.05                        // spread velocity multiplier
        );
    }
}
