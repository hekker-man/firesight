package com.hekker.firesight;

import fi.dy.masa.litematica.render.RenderUtils;
import fi.dy.masa.malilib.util.Color4f;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;
import org.joml.Matrix4f;

import java.util.List;

public class FiresightLitematicaIntegration {
    public static void renderBlocksWithLitematica(Matrix4f posMatrix, List<BlockPos> positions, Color4f color) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.enablePolygonOffset();
        RenderSystem.polygonOffset(-1.2F, -0.2F);
        RenderSystem.setShaderColor(color.r, color.g, color.b, color.a);

        // Render each block in the provided list
        for (BlockPos pos : positions) {
            RenderUtils.renderAreaSides(pos, pos, color, posMatrix, MinecraftClient.getInstance());
        }

        // Reset OpenGL state to prevent affecting other rendering
        RenderSystem.polygonOffset(0.0F, 0.0F);
        RenderSystem.disablePolygonOffset();
        RenderSystem.depthMask(true);
        RenderSystem.disableBlend();
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f); // Reset color
        RenderSystem.defaultBlendFunc(); // Reset blend function
    }
}
