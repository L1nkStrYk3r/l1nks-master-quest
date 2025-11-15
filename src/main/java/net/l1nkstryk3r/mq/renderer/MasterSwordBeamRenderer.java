package net.l1nkstryk3r.mq.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.l1nkstryk3r.mq.entity.MasterSwordBeamEntity;
import net.l1nkstryk3r.mq.util.ModUtils;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

/**
 * Handles rendering of the Master Sword Beam projectile
 * <p>
 *     This renderer draws a glowing, energy-like beam using a textured quad.
 *     It uses two perpendicular planes to simulate a 3D appearance.
 * </p>
 */
public class MasterSwordBeamRenderer extends EntityRenderer<MasterSwordBeamEntity> {

    /** Constructor called automatically when renderer is registered. */
    public MasterSwordBeamRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    /** Returns the texture used for rendering this entity. */
    @Override
    @NotNull
    public ResourceLocation getTextureLocation(MasterSwordBeamEntity entity) {
        return ModUtils.id("textures/entity/master_sword_beam.png");
    }

    /**
     * Defines how to draw the beam entity
     *
     * @param entity The beam being rendered
     * @param entityYaw Entity rotation around the Y-axis
     * @param partialTicks Smooth frame interpolation
     * @param buffer Vertex buffer for drawing geometry
     * @param packedLight Lighting data for shading
     */
    @Override
    public void render(
        MasterSwordBeamEntity entity,
        float entityYaw,
        float partialTicks,
        PoseStack poseStack,
        MultiBufferSource buffer,
        int packedLight
    ) {
        // push to preserve the current transform state
        poseStack.pushPose();

        // interpolate position smoothly between ticks for smoother visuals
        double interpX = Mth.lerp(partialTicks, entity.xOld, entity.getX());
        double interpY = Mth.lerp(partialTicks, entity.yOld, entity.getY());
        double interpZ = Mth.lerp(partialTicks, entity.zOld, entity.getZ());

        // Translate to the beam's current world position relative to the camera
        Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
        Vec3 camPos = camera.getPosition();
        poseStack.translate(interpX - camPos.x, interpY - camPos.y - 0.15D, interpZ - camPos.z);

        // Rotate the beam to face the correct direction
        poseStack.mulPose(Axis.YP.rotationDegrees(entity.getYRot()));
        poseStack.mulPose(Axis.XP.rotationDegrees(-entity.getXRot()));
        poseStack.mulPose(Axis.ZP.rotationDegrees(180.0F));

        // Alpha fade over lifetime
        float life = (entity.tickCount + partialTicks) * 0.2f;
        float alpha = Math.max(1.0f - (entity.tickCount / 40.0f), 0.1f);

        // UV mapping
        float minU = 0.0f;
        float maxU = 1.0f;
        float minV = life % 1.0f;
        float maxV = minV + 0.5f;

        // size of the beam (tweak for visual effects)
        float halfWidth = 1.6f;
        float length = 3.0f;

        // Render buffer for the beam texture
        VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.energySwirl(getTextureLocation(entity), 0, 0));

        // draw projectile planes
        for (float rotation : new float[]{ 0.0f, 90.0f }) {
            poseStack.mulPose(Axis.ZP.rotationDegrees(rotation));
            PoseStack.Pose pose = poseStack.last();
            vertexConsumer.vertex(pose.pose(), -halfWidth, 0, 0)
                .color(255, 255, 255, (int)(alpha * 255))
                .uv(minU, minV)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(packedLight)
                .normal(pose.normal(), 0, 1, 0)
                .endVertex();
            vertexConsumer.vertex(pose.pose(), halfWidth, 0, 0)
                .color(255, 255, 255, (int)(alpha * 255))
                .uv(maxU, minV)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(packedLight)
                .normal(pose.normal(), 0, 1, 0)
                .endVertex();
            vertexConsumer.vertex(pose.pose(), halfWidth, 0, length)
                .color(255, 255, 255, (int)(alpha * 255))
                .uv(maxU, maxV)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(packedLight)
                .normal(pose.normal(), 0, 1, 0)
                .endVertex();
            vertexConsumer.vertex(pose.pose(), -halfWidth, 0, length)
                .color(255, 255, 255, (int)(alpha * 255))
                .uv(minU, maxV)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(packedLight)
                .normal(pose.normal(), 0, 1, 0)
                .endVertex();
        }

        // restore transform state
        poseStack.popPose();

        // Render the entity (for lighting/shadow integration)
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }
}
