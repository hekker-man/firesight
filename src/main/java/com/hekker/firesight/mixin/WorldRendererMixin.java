package com.hekker.firesight.mixin;

import com.hekker.firesight.Firesight;
import com.hekker.firesight.config.Configs;
import com.hekker.firesight.FiresightLitematicaIntegration;
import fi.dy.masa.malilib.util.Color4f;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {

    private static final Map<Block, Integer> flammabilityMap = new HashMap<>();
    private static boolean flammabilityInitialized = false;

    private static void initializeFlammabilityData() {
        if (flammabilityInitialized) return;
        try {
            Object2IntMap<Block> burnChances = ((FireBlockAccessor) Blocks.FIRE).getBurnChances();

            for (Block block : burnChances.keySet()) {
                int burnChance = burnChances.getInt(block);
                flammabilityMap.put(block, burnChance);
            }
            flammabilityInitialized = true;
        } catch (Exception e) {
            Firesight.logger.error("Failed to access burnChances using Mixin Accessor: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Inject(method = "renderLayer", at = @At("TAIL"))
    private void onRenderLayer(RenderLayer renderLayer, double x, double y, double z, Matrix4f viewMatrix, Matrix4f posMatrix, CallbackInfo ci) {
        if (!Configs.Generic.FIRESIGHT_RENDERING.getBooleanValue()) return;
        if (renderLayer == RenderLayer.getTranslucent()) {
            initializeFlammabilityData();

            BlockPos playerPos = MinecraftClient.getInstance().player.getBlockPos();
            int radius = Configs.Generic.SCAN_RANGE.getIntegerValue();
            Color4f highlight = Configs.Generic.VISUAL_COLOR.getColor();
            List<BlockPos> dangerZonePositions = new ArrayList<>();
            List<BlockPos> flammableDangerPositions = new ArrayList<>();

            for (int dx = -radius; dx <= radius; dx++) {
                for (int dy = -radius; dy <= radius; dy++) {
                    for (int dz = -radius; dz <= radius; dz++) {
                        BlockPos pos = playerPos.add(dx, dy, dz);
                        BlockState state = MinecraftClient.getInstance().world.getBlockState(pos);
                        Block block = state.getBlock();

                        if (block == Blocks.FIRE || block == Blocks.LAVA) {
                            dangerZonePositions.addAll(getDangerZoneForBlock(pos, block));
                        }
                    }
                }
            }

            for (BlockPos dangerPos : dangerZonePositions) {
                if (MinecraftClient.getInstance().world.getBlockState(dangerPos).isAir()) {
                    for (Direction direction : Direction.values()) {
                        BlockPos adjacentPos = dangerPos.offset(direction);
                        BlockState adjacentState = MinecraftClient.getInstance().world.getBlockState(adjacentPos);
                        Block adjacentBlock = adjacentState.getBlock();
                        if (flammabilityMap.containsKey(adjacentBlock)) {
                            flammableDangerPositions.add(dangerPos);
                            break;
                        }
                    }
                }
            }

            FiresightLitematicaIntegration.renderBlocksWithLitematica(posMatrix, flammableDangerPositions, highlight);
        }
    }

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
