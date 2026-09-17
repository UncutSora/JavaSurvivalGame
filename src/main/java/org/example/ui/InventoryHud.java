package org.example.ui;

import com.jme3.asset.AssetManager;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.renderer.Camera;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Quad;

import org.example.hotbar.HotbarSystem;
import org.example.inventory.Inventory;
import org.example.inventory.InventorySlot;
import org.example.inventory.ItemType;
import org.example.tools.ToolDurabilitySystem;

public class InventoryHud {

    private static final float SLOT_WIDTH = 130f;
    private static final float SLOT_HEIGHT = 82f;
    private static final float SLOT_GAP = 10f;

    private final Inventory inventory;
    private final HotbarSystem hotbarSystem;
    private final ToolDurabilitySystem toolDurabilitySystem;

    private final Material[] borderMaterials =
            new Material[3];

    private final BitmapText[] slotTexts =
            new BitmapText[3];

    private final BitmapText selectedText;

    private final Node durabilityNode =
            new Node("HotbarDurability");

    private final Geometry durabilityFill;

    private final BitmapText durabilityText;

    private final ColorRGBA normalBorder =
            new ColorRGBA(
                    0.28f,
                    0.3f,
                    0.33f,
                    1f
            );

    private final ColorRGBA selectedBorder =
            new ColorRGBA(
                    1f,
                    0.72f,
                    0.15f,
                    1f
            );


    public InventoryHud(
            AssetManager assetManager,
            Node guiNode,
            Camera camera,
            Inventory inventory,
            HotbarSystem hotbarSystem,
            ToolDurabilitySystem toolDurabilitySystem
    ) {

        this.inventory =
                inventory;

        this.hotbarSystem =
                hotbarSystem;

        this.toolDurabilitySystem =
                toolDurabilitySystem;


        BitmapFont font =
                assetManager.loadFont(
                        "Interface/Fonts/Default.fnt"
                );


        float totalWidth =
                SLOT_WIDTH * 3f
                        +
                        SLOT_GAP * 2f;


        float startX =
                camera.getWidth() / 2f
                        -
                        totalWidth / 2f;


        float startY =
                20f;


        for (
                int i = 0;
                i < 3;
                i++
        ) {

            float x =
                    startX
                            +
                            i
                                    *
                                    (SLOT_WIDTH + SLOT_GAP);


            createSlot(
                    assetManager,
                    guiNode,
                    font,
                    i,
                    x,
                    startY
            );
        }


        selectedText =
                new BitmapText(
                        font
                );


        selectedText.setSize(
                16f
        );


        selectedText.setColor(
                ColorRGBA.White
        );


        selectedText.setLocalTranslation(
                startX,
                startY
                        +
                        SLOT_HEIGHT
                        +
                        28f,
                10f
        );


        guiNode.attachChild(
                selectedText
        );


        // ==========================
        // HALTBARKEIT
        // ==========================

        durabilityText =
                new BitmapText(
                        font
                );


        durabilityText.setSize(
                14f
        );


        durabilityText.setColor(
                ColorRGBA.White
        );


        durabilityText.setLocalTranslation(
                0f,
                27f,
                3f
        );


        durabilityNode.attachChild(
                durabilityText
        );


        Geometry durabilityBackground =
                new Geometry(
                        "HotbarDurabilityBackground",
                        new Quad(
                                180f,
                                10f
                        )
                );


        durabilityBackground.setMaterial(
                createMaterial(
                        assetManager,
                        new ColorRGBA(
                                0.1f,
                                0.1f,
                                0.1f,
                                0.9f
                        )
                )
        );


        durabilityBackground.setQueueBucket(
                RenderQueue.Bucket.Gui
        );


        durabilityNode.attachChild(
                durabilityBackground
        );


        durabilityFill =
                new Geometry(
                        "HotbarDurabilityFill",
                        new Quad(
                                176f,
                                6f
                        )
                );


        durabilityFill.setMaterial(
                createMaterial(
                        assetManager,
                        new ColorRGBA(
                                0.2f,
                                0.8f,
                                0.3f,
                                1f
                        )
                )
        );


        durabilityFill.setQueueBucket(
                RenderQueue.Bucket.Gui
        );


        durabilityFill.setLocalTranslation(
                2f,
                2f,
                2f
        );


        durabilityNode.attachChild(
                durabilityFill
        );


        durabilityNode.setLocalTranslation(
                camera.getWidth() / 2f - 90f,
                startY
                        +
                        SLOT_HEIGHT
                        +
                        48f,
                10f
        );


        guiNode.attachChild(
                durabilityNode
        );


        update();
    }


    private void createSlot(
            AssetManager assetManager,
            Node guiNode,
            BitmapFont font,
            int index,
            float x,
            float y
    ) {

        Geometry border =
                new Geometry(
                        "HotbarBorder"
                                +
                                index,
                        new Quad(
                                SLOT_WIDTH,
                                SLOT_HEIGHT
                        )
                );


        Material borderMaterial =
                createMaterial(
                        assetManager,
                        normalBorder
                );


        border.setMaterial(
                borderMaterial
        );


        border.setQueueBucket(
                RenderQueue.Bucket.Gui
        );


        border.setLocalTranslation(
                x,
                y,
                1f
        );


        guiNode.attachChild(
                border
        );


        borderMaterials[index] =
                borderMaterial;


        Geometry background =
                new Geometry(
                        "HotbarBackground"
                                +
                                index,
                        new Quad(
                                SLOT_WIDTH - 6f,
                                SLOT_HEIGHT - 6f
                        )
                );


        background.setMaterial(
                createMaterial(
                        assetManager,
                        new ColorRGBA(
                                0.05f,
                                0.055f,
                                0.06f,
                                0.9f
                        )
                )
        );


        background.setQueueBucket(
                RenderQueue.Bucket.Gui
        );


        background.setLocalTranslation(
                x + 3f,
                y + 3f,
                2f
        );


        guiNode.attachChild(
                background
        );


        BitmapText text =
                new BitmapText(
                        font
                );


        text.setSize(
                15f
        );


        text.setColor(
                ColorRGBA.White
        );


        text.setLocalTranslation(
                x + 10f,
                y + SLOT_HEIGHT - 10f,
                5f
        );


        guiNode.attachChild(
                text
        );


        slotTexts[index] =
                text;
    }


    private Material createMaterial(
            AssetManager assetManager,
            ColorRGBA color
    ) {

        Material material =
                new Material(
                        assetManager,
                        "Common/MatDefs/Misc/Unshaded.j3md"
                );


        material.setColor(
                "Color",
                color
        );


        material
                .getAdditionalRenderState()
                .setBlendMode(
                        RenderState.BlendMode.Alpha
                );


        return material;
    }


    public void update() {

        int selected =
                hotbarSystem
                        .getSelectedSlot();


        for (
                int i = 0;
                i < 3;
                i++
        ) {

            InventorySlot slot =
                    inventory.getSlot(
                            i
                    );


            if (
                    slot.isEmpty()
            ) {

                slotTexts[i].setText(
                        (i + 1)
                                +
                                "\n\nLeer"
                );
            }

            else {

                slotTexts[i].setText(
                        (i + 1)
                                +
                                "\n"
                                +
                                slot
                                        .getItemType()
                                        .getDisplayName()
                                +
                                "\nx"
                                +
                                slot.getAmount()
                );
            }


            if (
                    selected
                            ==
                            i + 1
            ) {

                borderMaterials[i]
                        .setColor(
                                "Color",
                                selectedBorder
                        );
            }

            else {

                borderMaterials[i]
                        .setColor(
                                "Color",
                                normalBorder
                        );
            }
        }


        ItemType selectedType =
                hotbarSystem
                        .getSelectedItemType();


        if (
                selectedType == null
        ) {

            selectedText.setText(
                    "Ausgewählt: Leer"
            );
        }

        else {

            selectedText.setText(
                    "Ausgewählt: "
                            +
                            selectedType
                                    .getDisplayName()
            );
        }


        if (
                selectedType
                        ==
                        ItemType.STONE_AXE
                        &&
                        toolDurabilitySystem
                                .hasUsableStoneAxe()
        ) {

            durabilityNode.setCullHint(
                    Spatial.CullHint.Inherit
            );


            durabilityText.setText(
                    "Axt-Haltbarkeit: "
                            +
                            toolDurabilitySystem
                                    .getStoneAxeDurability()
                            +
                            " / "
                            +
                            toolDurabilitySystem
                                    .getStoneAxeMaxDurability()
            );


            durabilityFill.setLocalScale(
                    toolDurabilitySystem
                            .getStoneAxeDurabilityPercent(),
                    1f,
                    1f
            );
        }

        else {

            durabilityNode.setCullHint(
                    Spatial.CullHint.Always
            );
        }
    }
}