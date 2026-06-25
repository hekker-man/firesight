package com.hekker.firesight;

import fi.dy.masa.malilib.render.MaLiLibPipelines;
import fi.dy.masa.malilib.render.RenderContext;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.util.data.Color4f;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.MeshData;
import net.minecraft.core.BlockPos;

import java.util.List;


public class FiresightMaLiLibIntegration {

    public static void renderBlocksWithMaLiLib(List<BlockPos> positions, Color4f color) {
        RenderContext ctx = null;
        try {
            ctx = new RenderContext(
                    () -> "firesight:overlay/fill",
                    MaLiLibPipelines.POSITION_COLOR_TRANSLUCENT_NO_DEPTH_NO_CULL,
                    0);

            BufferBuilder buffer = ctx.getBuilder();

            for (BlockPos pos : positions) {
                RenderUtils.renderAreaSidesBatched(pos, pos, color, 0.002, buffer);
            }

            MeshData meshData = buffer.build();
            if (meshData != null) {
                ctx.draw(meshData, false);
                meshData.close();
            }
        }
        catch (Exception e) {
            Firesight.logger.error("Error rendering Firesight MaLiLib overlay", e);
        }
        finally {
            if (ctx != null) {
                try {
                    ctx.close();
                }
                catch (Exception e) {
                    Firesight.logger.error("Error closing Firesight MaLiLib render context", e);
                }
            }
        }
    }
}
