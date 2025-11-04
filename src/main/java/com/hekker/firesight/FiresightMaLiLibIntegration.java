package com.hekker.firesight;

import com.mojang.blaze3d.systems.RenderSystem;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.util.Color4f;
import net.minecraft.client.render.*;
import net.minecraft.util.math.BlockPos;

import java.util.List;

public class FiresightMaLiLibIntegration {

    public static void renderBlocksWithMaLiLib(List<BlockPos> positions, Color4f color) {
        if (positions == null || positions.isEmpty()) return;

        try {
            // Translucent overlay, depth test ON (no writes), no cull
            RenderUtils.setupBlend();
            RenderSystem.depthMask(false);
            RenderSystem.disableCull();
            RenderSystem.setShader(GameRenderer::getPositionColorProgram);
            RenderSystem.applyModelViewMatrix();

            Tessellator tess = Tessellator.getInstance();
            BufferBuilder buf = tess.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);

            for (BlockPos pos : positions) {
                // Slight expand to avoid z-fighting with block faces
                RenderUtils.drawBlockBoundingBoxSidesBatchedQuads(pos, color, 0.002, buf);
            }

            BuiltBuffer built = buf.end();
            BufferRenderer.drawWithGlobalProgram(built);
            built.close();
        } catch (Exception e) {
            Firesight.logger.error("Error rendering Firesight overlay", e);
        } finally {
            RenderSystem.enableCull();
            RenderSystem.depthMask(true);
            RenderSystem.disableBlend();
        }
    }
}
