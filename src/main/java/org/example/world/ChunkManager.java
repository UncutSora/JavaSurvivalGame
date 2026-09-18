package org.example.world;

import com.jme3.math.Vector3f;
import com.jme3.scene.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ChunkManager {

    private final Node rootNode;
    private final float chunkSize;
    private final int loadRadius;

    private final Map<Long, List<HarvestableResource>> resourcesByChunk =
            new HashMap<>();

    private final Set<Long> activeChunks =
            new HashSet<>();

    private int currentChunkX =
            Integer.MIN_VALUE;

    private int currentChunkZ =
            Integer.MIN_VALUE;


    public ChunkManager(
            Node rootNode,
            float chunkSize,
            int loadRadius
    ) {

        this.rootNode =
                rootNode;

        this.chunkSize =
                chunkSize;

        this.loadRadius =
                loadRadius;
    }


    public void register(
            HarvestableResource resource,
            Vector3f position
    ) {

        int chunkX =
                worldToChunk(
                        position.x
                );

        int chunkZ =
                worldToChunk(
                        position.z
                );

        long key =
                createChunkKey(
                        chunkX,
                        chunkZ
                );

        resourcesByChunk
                .computeIfAbsent(
                        key,
                        ignored -> new ArrayList<>()
                )
                .add(
                        resource
                );
    }


    public void update(
            Vector3f playerPosition
    ) {

        int newChunkX =
                worldToChunk(
                        playerPosition.x
                );

        int newChunkZ =
                worldToChunk(
                        playerPosition.z
                );

        if (
                newChunkX == currentChunkX
                        &&
                        newChunkZ == currentChunkZ
        ) {

            return;
        }

        currentChunkX =
                newChunkX;

        currentChunkZ =
                newChunkZ;

        refreshActiveChunks();
    }


    private void refreshActiveChunks() {

        Set<Long> requiredChunks =
                new HashSet<>();

        for (
                int xOffset = -loadRadius;
                xOffset <= loadRadius;
                xOffset++
        ) {

            for (
                    int zOffset = -loadRadius;
                    zOffset <= loadRadius;
                    zOffset++
            ) {

                long key =
                        createChunkKey(
                                currentChunkX + xOffset,
                                currentChunkZ + zOffset
                        );

                requiredChunks.add(
                        key
                );
            }
        }


        Set<Long> chunksToUnload =
                new HashSet<>(
                        activeChunks
                );

        chunksToUnload.removeAll(
                requiredChunks
        );

        for (
                long key
                :
                chunksToUnload
        ) {

            deactivateChunk(
                    key
            );
        }


        for (
                long key
                :
                requiredChunks
        ) {

            if (
                    !activeChunks.contains(
                            key
                    )
            ) {

                activateChunk(
                        key
                );
            }
        }


        activeChunks.clear();

        activeChunks.addAll(
                requiredChunks
        );
    }


    private void activateChunk(
            long key
    ) {

        List<HarvestableResource> chunkResources =
                resourcesByChunk.get(
                        key
                );

        if (
                chunkResources == null
        ) {

            return;
        }

        for (
                HarvestableResource resource
                :
                chunkResources
        ) {

            if (
                    !resource.isHarvested()
            ) {

                resource.attachToWorld(
                        rootNode
                );
            }
        }
    }


    private void deactivateChunk(
            long key
    ) {

        List<HarvestableResource> chunkResources =
                resourcesByChunk.get(
                        key
                );

        if (
                chunkResources == null
        ) {

            return;
        }

        for (
                HarvestableResource resource
                :
                chunkResources
        ) {

            resource.detachFromWorld();
        }
    }


    private int worldToChunk(
            float coordinate
    ) {

        return (int) Math.floor(
                coordinate / chunkSize
        );
    }


    private long createChunkKey(
            int chunkX,
            int chunkZ
    ) {

        return ((long) chunkX << 32)
                ^
                (chunkZ & 0xffffffffL);
    }
}
