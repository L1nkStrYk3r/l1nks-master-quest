package net.l1nkstryk3r.mq.entity;

import net.l1nkstryk3r.mq.L1nksMasterQuest;
import net.l1nkstryk3r.mq.util.ModUtils;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

/**
 * Handles registration of all custom entities for the mod
 * <p>
 *      In Minecraft, every custom entity (projectiles, mobs, etc.)
 *      must be registered into the ENTITY_TYPE registry
 * </p>
 * <p>
 *      This class creates and registers the Master Sword Beam entity type.
 * </p>
 */
public class ModEntities {
    // === entity constants ===
    private static final float BEAM_WIDTH = 0.5f;
    private static final float BEAM_HEIGHT = 0.5f;
    private static final int BEAM_TRACKING_RANGE = 4; // in chunks
    private static final int BEAM_UPDATE_INTERVAL = 10; // <10 causes de-sync with collision and hit detection

    // Public reference to the custom entity type
    // Allows us to spawn it anywhere in the code (e.g., in the Master Sword's use() method).
    // Set parameters to register a new entity type called "master_sword_beam"
    // - of(): creates a builder that knows how to construct our entity
    // - MobCategory.MISC: means it's a non-living, non-hostile entity (like an arrow)
    // - sized(): sets hitbox width/height (affects collision and rendering)
    // - clientTrackingRange(): how far away the client can "see" this entity (in chunks)
    // - updateInterval(): how frequently it syncs updates to the client (in ticks)
    // - noSave(): prevents entity from persisting to world saves
    public static final EntityType<MasterSwordBeamEntity> MASTER_SWORD_BEAM = register(
        "master_sword_beam",
        EntityType.Builder.of(MasterSwordBeamEntity::new, MobCategory.MISC)
            .sized(BEAM_WIDTH, BEAM_HEIGHT)
            .clientTrackingRange(BEAM_TRACKING_RANGE)
            .updateInterval(BEAM_UPDATE_INTERVAL)
            .noSave()
    );

    /**
     * Private helper to register a new entity (for future mod expansion)
     * @param id The unique String id for the entity
     * @param builder The EntityType builder to build the entity's attributes
     * @param <T> A given entity (e.g. MasterSwordBeamEntity)
     * @return An EntityType of that entity
     */
    private static <T extends Entity> EntityType<T> register(String id, EntityType.Builder<T> builder) {
        L1nksMasterQuest.LOGGER.info("Registering entity: {}", id);
        // Register a new entity type
        // - BuiltInRegistries.ENTITY_TYPE: we're registering an entity
        // - ResourceLocation: where the texture for the entity is located
        // - build(): finalizes and names the entity type
        return Registry.register(BuiltInRegistries.ENTITY_TYPE,
            ModUtils.id(id),
            builder.build(null)
        );
    }

    /**
     * Called from the mod initializer (e.g., on mod startup) to register all the entity types
     * <p>
     *     The registration process tells Minecraft how to construct and track our entity.
     *     Without this, the game wouldn't know that our custom entity exists.
     * </p>
     */
    public static void initialize() {
        L1nksMasterQuest.LOGGER.info("Initializing ModEntities for {}", L1nksMasterQuest.MOD_ID);
        // intentionally empty as the static initialization will trigger registration.
    }
}
