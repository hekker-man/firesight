package com.hekker.firesight.mixin;

import com.hekker.firesight.Firesight;
import com.hekker.firesight.config.Configs;
import com.hekker.firesight.FiresightMaLiLibIntegration;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.resource.GraphicsResourceAllocator;
import fi.dy.masa.malilib.util.data.Color4f;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Matrix4fc;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mixin(LevelRenderer.class)
public class WorldRendererMixin {

    @Unique
    private static final Map<Block, Integer> flammabilityMap = new HashMap<>();
    @Unique
    private static boolean flammabilityInitialized = false;
    @Unique
    private static void initializeFlammabilityData() {
        if (flammabilityInitialized) return;
        try {
            Object2IntMap<Block> burnOdds = ((FireBlockAccessor) Blocks.FIRE).getBurnOdds();

            for (Block block : burnOdds.keySet()) {
                int burnOddsValue = burnOdds.getInt(block);
                flammabilityMap.put(block, burnOddsValue);
            }
            flammabilityInitialized = true;
        } catch (Exception e) {
            Firesight.logger.error("Failed to access burnOdds using Mixin Accessor", e);
        }
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void firesight$afterRender(
            GraphicsResourceAllocator allocator,
            DeltaTracker tickCounter,
            boolean renderBlockOutline,
            CameraRenderState camera,
            Matrix4fc positionMatrix,
            GpuBufferSlice fog,
            Vector4f fogColor,
            boolean shouldRenderSky,
            CallbackInfo ci
    ) {
        if (!Configs.Generic.FIRESIGHT_RENDERING.getBooleanValue()) return;
        initializeFlammabilityData();

        Minecraft minecraft = Minecraft.getInstance();
        assert minecraft.player != null;
        BlockPos playerPos = minecraft.player.blockPosition();
        int radius = Configs.Generic.SCAN_RANGE.getIntegerValue();
        Color4f highlight = Configs.Generic.VISUAL_COLOR.getColor();
        List<BlockPos> dangerZonePositions = new ArrayList<>();
        List<BlockPos> flammableDangerPositions = new ArrayList<>();

        for (int dx = -radius; dx <= radius; dx++) {
            for (int dy = -radius; dy <= radius; dy++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    BlockPos pos = playerPos.offset(dx, dy, dz);
                    assert minecraft.level != null;
                    BlockState state = minecraft.level.getBlockState(pos);
                    Block block = state.getBlock();

                    if (block == Blocks.FIRE || block == Blocks.LAVA) {
                        dangerZonePositions.addAll(getDangerZoneForBlock(pos, block));
                    }
                }
            }
        }

        for (BlockPos dangerPos : dangerZonePositions) {
            if (minecraft.level.getBlockState(dangerPos).isAir()) {
                for (Direction direction : Direction.values()) {
                    BlockPos adjacentPos = dangerPos.relative(direction);
                    BlockState adjacentState = minecraft.level.getBlockState(adjacentPos);
                    Block adjacentBlock = adjacentState.getBlock();
                    if (flammabilityMap.containsKey(adjacentBlock)) {
                        flammableDangerPositions.add(dangerPos);
                        break;
                    }
                }
            }
        }

        // === Apply camera/world transform before rendering overlay ===
        com.mojang.blaze3d.systems.RenderSystem.getModelViewStack().pushMatrix();
        com.mojang.blaze3d.systems.RenderSystem.getModelViewStack().mul(positionMatrix);

        FiresightMaLiLibIntegration.renderBlocksWithMaLiLib(flammableDangerPositions, highlight);

        com.mojang.blaze3d.systems.RenderSystem.getModelViewStack().popMatrix();
    }

    @Unique
    private List<BlockPos> getDangerZoneForBlock(BlockPos sourcePos, Block block) {
        List<BlockPos> dangerZone = new ArrayList<>();

        if (block == Blocks.FIRE) {
            for (int dx = -1; dx <= 1; dx++) {
                for (int dz = -1; dz <= 1; dz++) {
                    for (int dy = -1; dy <= 4; dy++) {
                        dangerZone.add(sourcePos.offset(dx, dy, dz));
                    }
                }
            }
        } else if (block == Blocks.LAVA) {
            for (int dx = -1; dx <= 1; dx++) {
                for (int dz = -1; dz <= 1; dz++) {
                    dangerZone.add(sourcePos.offset(dx, 1, dz));
                }
            }
            for (int dx = -2; dx <= 2; dx++) {
                for (int dz = -2; dz <= 2; dz++) {
                    dangerZone.add(sourcePos.offset(dx, 2, dz));
                }
            }
        }

        return dangerZone;
    }
}
