package org.example.ui;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.renderer.Camera;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Quad;
import org.example.hotbar.HotbarSystem;
import org.example.inventory.Inventory;
import org.example.inventory.ItemType;

public class ToolView {

    private final Inventory inventory;

    private final HotbarSystem hotbarSystem;

    private final Node axeNode =
            new Node(
                    "StoneAxeView"
            );


    private final float baseX;

    private final float baseY;


    private static final float SWING_DURATION =
            0.20f;


    private float swingTime =
            0f;


    public ToolView(
            AssetManager assetManager,
            Node guiNode,
            Camera camera,
            Inventory inventory,
            HotbarSystem hotbarSystem
    ) {

        this.inventory =
                inventory;


        this.hotbarSystem =
                hotbarSystem;


        baseX =
                camera.getWidth() - 180f;


        baseY =
                20f;


        createAxe(
                assetManager
        );


        axeNode.setLocalTranslation(
                baseX,
                baseY,
                5f
        );


        guiNode.attachChild(
                axeNode
        );


        axeNode.setCullHint(
                Spatial.CullHint.Always
        );
    }


    private void createAxe(
            AssetManager assetManager
    ) {

        // ==========================
        // HOLZGRIFF
        // ==========================

        Geometry handle =
                new Geometry(
                        "AxeHandle",
                        new Quad(
                                20f,
                                150f
                        )
                );


        Material handleMaterial =
                new Material(
                        assetManager,
                        "Common/MatDefs/Misc/Unshaded.j3md"
                );


        handleMaterial.setColor(
                "Color",
                new ColorRGBA(
                        0.45f,
                        0.25f,
                        0.1f,
                        1f
                )
        );


        handle.setMaterial(
                handleMaterial
        );


        handle.setLocalTranslation(
                55f,
                0f,
                0f
        );


        axeNode.attachChild(
                handle
        );


        // ==========================
        // STEINKOPF
        // ==========================

        Geometry head =
                new Geometry(
                        "AxeHead",
                        new Quad(
                                95f,
                                45f
                        )
                );


        Material headMaterial =
                new Material(
                        assetManager,
                        "Common/MatDefs/Misc/Unshaded.j3md"
                );


        headMaterial.setColor(
                "Color",
                new ColorRGBA(
                        0.45f,
                        0.45f,
                        0.45f,
                        1f
                )
        );


        head.setMaterial(
                headMaterial
        );


        head.setLocalTranslation(
                15f,
                105f,
                1f
        );


        axeNode.attachChild(
                head
        );
    }


    public boolean isAxeEquipped() {

        return hotbarSystem
                .getSelectedItemType()
                ==
                ItemType.STONE_AXE

                &&

                inventory.hasItem(
                        ItemType.STONE_AXE,
                        1
                );
    }


    public void swing() {

        if (!isAxeEquipped()) {
            return;
        }


        swingTime =
                SWING_DURATION;
    }


    public void update(
            float tpf
    ) {

        if (!isAxeEquipped()) {

            axeNode.setCullHint(
                    Spatial.CullHint.Always
            );

            return;
        }


        axeNode.setCullHint(
                Spatial.CullHint.Inherit
        );


        if (swingTime > 0f) {

            swingTime -=
                    tpf;


            float progress =
                    1f
                            -
                            swingTime
                                    /
                                    SWING_DURATION;


            progress =
                    FastMath.clamp(
                            progress,
                            0f,
                            1f
                    );


            float movement =
                    FastMath.sin(
                            progress
                                    *
                                    FastMath.PI
                    );


            axeNode.setLocalTranslation(
                    baseX - movement * 70f,
                    baseY + movement * 20f,
                    5f
            );
        }

        else {

            axeNode.setLocalTranslation(
                    baseX,
                    baseY,
                    5f
            );
        }
    }
}