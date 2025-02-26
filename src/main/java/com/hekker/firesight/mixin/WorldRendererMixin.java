package com.hekker.firesight.mixin;

import com.hekker.firesight.FiresightClient;
import com.hekker.firesight.FiresightLitematicaIntegration;
import fi.dy.masa.malilib.util.Color4f;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.client.render.WorldRenderer;

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
            FiresightClient.LOGGER.error("Failed to access burnChances using Mixin Accessor: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Inject(method = "renderLayer", at = @At("TAIL"))
    private void onRenderLayer(RenderLayer renderLayer, double x, double y, double z, Matrix4f viewMatrix, Matrix4f posMatrix, CallbackInfo ci) {
        if (renderLayer == RenderLayer.getTranslucent()) {
            initializeFlammabilityData();

            BlockPos playerPos = MinecraftClient.getInstance().player.getBlockPos();
            int radius = 10; // Define the radius around the player
            List<BlockPos> dangerZonePositions = new ArrayList<>();
            List<BlockPos> flammableDangerPositions = new ArrayList<>();

            // Identify danger zones and blocks that are in the danger zone and adjacent to flammable blocks
            for (int dx = -radius; dx <= radius; dx++) {
                for (int dy = -radius; dy <= radius; dy++) {
                    for (int dz = -radius; dz <= radius; dz++) {
                        BlockPos pos = playerPos.add(dx, dy, dz);
                        BlockState state = MinecraftClient.getInstance().world.getBlockState(pos);
                        Block block = state.getBlock();

                        // Collect danger zone from fire and lava blocks
                        if (block == Blocks.FIRE || block == Blocks.LAVA) {
                            dangerZonePositions.addAll(getDangerZoneForBlock(pos, block));
                        }
                    }
                }
            }

            // Check for flammable adjacency in the danger zone
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

            // Render only the flammable danger zone blocks in yellow
            Color4f flammableDangerColor = new Color4f(1.0f, 0.5f, 0.0f, 0.5f); // Semi-transparent yellow
            FiresightLitematicaIntegration.renderBlocksWithLitematica(posMatrix, flammableDangerPositions, flammableDangerColor);
        }
    }

    private List<BlockPos> getDangerZoneForBlock(BlockPos sourcePos, Block block) {
        List<BlockPos> dangerZone = new ArrayList<>();

        if (block == Blocks.FIRE) {
            // Fire spread danger zone
            for (int dx = -1; dx <= 1; dx++) {
                for (int dz = -1; dz <= 1; dz++) {
                    for (int dy = -1; dy <= 4; dy++) {
                        dangerZone.add(sourcePos.add(dx, dy, dz));
                    }
                }
            }
        } else if (block == Blocks.LAVA) {
            // Lava spread danger zone
            for (int dx = -1; dx <= 1; dx++) {
                for (int dz = -1; dz <= 1; dz++) {
                    dangerZone.add(sourcePos.add(dx, 1, dz)); // 3x1x3 directly above
                }
            }
            for (int dx = -2; dx <= 2; dx++) {
                for (int dz = -2; dz <= 2; dz++) {
                    dangerZone.add(sourcePos.add(dx, 2, dz)); // 5x1x5 two blocks above
                }
            }
        }

        return dangerZone;
    }
}
