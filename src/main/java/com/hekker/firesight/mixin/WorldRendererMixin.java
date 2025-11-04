package com.hekker.firesight.mixin;

import com.hekker.firesight.Firesight;
import com.hekker.firesight.FiresightMaLiLibIntegration;
import com.hekker.firesight.config.Configs;
import fi.dy.masa.malilib.util.Color4f;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.*;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {

    @Unique
    private static final Map<Block, Integer> flammabilityMap = new HashMap<>();
    @Unique
    private static boolean flammabilityInitialized = false;

    @Unique
    private static void initializeFlammabilityData() {
        if (flammabilityInitialized) return;
        try {
            Object2IntMap<Block> burnChances = ((FireBlockAccessor) Blocks.FIRE).getBurnChances();
            for (Block block : burnChances.keySet()) {
                flammabilityMap.put(block, burnChances.getInt(block));
            }
            flammabilityInitialized = true;
        } catch (Exception e) {
            Firesight.logger.error("Failed to access burnChances using Mixin Accessor", e);
        }
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void firesight$afterRender(
            RenderTickCounter tickCounter,
            boolean renderBlockOutline,
            Camera camera,
            GameRenderer gameRenderer,
            LightmapTextureManager lightmapTextureManager,
            Matrix4f positionMatrix,
            Matrix4f projectionMatrix,
            CallbackInfo ci
    ) {
        if (!Configs.Generic.FIRESIGHT_RENDERING.getBooleanValue()) return;
        initializeFlammabilityData();

        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null || mc.world == null) return;

        BlockPos playerPos = mc.player.getBlockPos();
        int radius = Configs.Generic.SCAN_RANGE.getIntegerValue();
        Color4f highlight = Configs.Generic.VISUAL_COLOR.getColor();

        List<BlockPos> dangerZonePositions = new ArrayList<>();
        List<BlockPos> flammableDangerPositions = new ArrayList<>();

        // Find nearby fire/lava and compute candidate spread cells
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dy = -radius; dy <= radius; dy++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    BlockPos pos = playerPos.add(dx, dy, dz);
                    BlockState state = mc.world.getBlockState(pos);
                    Block block = state.getBlock();
                    if (block == Blocks.FIRE || block == Blocks.LAVA) {
                        dangerZonePositions.addAll(getDangerZoneForBlock(pos, block));
                    }
                }
            }
        }

        // Keep only air cells adjacent to something flammable
        for (BlockPos dangerPos : dangerZonePositions) {
            if (mc.world.getBlockState(dangerPos).isAir()) {
                for (Direction dir : Direction.values()) {
                    BlockPos adj = dangerPos.offset(dir);
                    Block adjBlock = mc.world.getBlockState(adj).getBlock();
                    if (flammabilityMap.containsKey(adjBlock)) {
                        flammableDangerPositions.add(dangerPos);
                        break;
                    }
                }
            }
        }

        // Apply the frame's camera transform and draw
        com.mojang.blaze3d.systems.RenderSystem.getModelViewStack().pushMatrix();
        com.mojang.blaze3d.systems.RenderSystem.getModelViewStack().mul(positionMatrix);
        net.minecraft.util.math.Vec3d cam = camera.getPos();
        com.mojang.blaze3d.systems.RenderSystem.getModelViewStack()
                .translate((float) -cam.x, (float) -cam.y, (float) -cam.z);
        com.mojang.blaze3d.systems.RenderSystem.applyModelViewMatrix();

        FiresightMaLiLibIntegration.renderBlocksWithMaLiLib(flammableDangerPositions, highlight);

        com.mojang.blaze3d.systems.RenderSystem.getModelViewStack().popMatrix();
        com.mojang.blaze3d.systems.RenderSystem.applyModelViewMatrix();
    }

    @Unique
    private List<BlockPos> getDangerZoneForBlock(BlockPos sourcePos, Block block) {
        List<BlockPos> dangerZone = new ArrayList<>();

        if (block == Blocks.FIRE) {
            for (int dx = -1; dx <= 1; dx++) {
                for (int dz = -1; dz <= 1; dz++) {
                    for (int dy = -1; dy <= 4; dy++) {
                        dangerZone.add(sourcePos.add(dx, dy, dz));
                    }
                }
            }
        } else if (block == Blocks.LAVA) {
            for (int dx = -1; dx <= 1; dx++) {
                for (int dz = -1; dz <= 1; dz++) {
                    dangerZone.add(sourcePos.add(dx, 1, dz));
                }
            }
            for (int dx = -2; dx <= 2; dx++) {
                for (int dz = -2; dz <= 2; dz++) {
                    dangerZone.add(sourcePos.add(dx, 2, dz));
                }
            }
        }

        return dangerZone;
    }
}
