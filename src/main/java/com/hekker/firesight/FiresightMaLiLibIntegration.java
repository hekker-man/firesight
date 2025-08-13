package com.hekker.firesight;

import fi.dy.masa.malilib.render.MaLiLibPipelines;
import fi.dy.masa.malilib.render.RenderContext;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.util.data.Color4f;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BuiltBuffer;
import net.minecraft.util.math.BlockPos;

import java.util.List;


public class FiresightMaLiLibIntegration {

    public static void renderBlocksWithMaLiLib(List<BlockPos> positions, Color4f color) {
        try (RenderContext ctx = new RenderContext(
                () -> "firesight:overlay/fill",
                MaLiLibPipelines.POSITION_COLOR_TRANSLUCENT_NO_DEPTH_NO_CULL)) {

            BufferBuilder buffer = ctx.getBuilder();

            for (BlockPos pos : positions) {
                RenderUtils.renderAreaSidesBatched(pos, pos, color, 0.002, buffer);
            }

            BuiltBuffer meshData = buffer.endNullable();
            if (meshData != null) {
                ctx.draw(meshData, false);
                meshData.close();
            }
        }
        catch (Exception e) {
            Firesight.logger.error("Error rendering Firesight MaLiLib overlay", e);
        }
    }
}
