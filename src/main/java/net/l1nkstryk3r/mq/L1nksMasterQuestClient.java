package net.l1nkstryk3r.mq;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.l1nkstryk3r.mq.entity.ModEntities;
import net.l1nkstryk3r.mq.renderer.MasterSwordBeamRenderer;

/**
 * Handles client-side initialization for rendering and visuals
 * <p>
 *     This entrypoint only runs on the Minecraft client.
 *     Any rendering code or client-only assets should be registered here.
 * </p>
 */
public class L1nksMasterQuestClient implements ClientModInitializer {
    /**
     * Runs after client startup.
     * <p>
     *     Used to register entity renderers, models, screens, etc.
     * </p>
     */
    @Override
    public void onInitializeClient() {
        // register the custom renderer for the Master Sword Beam entity.
        // tells Minecraft which class handles how the projectile is drawn.
        EntityRendererRegistry.register(ModEntities.MASTER_SWORD_BEAM, MasterSwordBeamRenderer::new);
    }
}
