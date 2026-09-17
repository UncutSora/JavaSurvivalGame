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
import org.example.inventory.ItemType;
import org.example.tools.ToolDurabilitySystem;

public class InventoryHud {

    private static final float SLOT_SIZE =
            92f;

    private static final float INNER_SLOT_SIZE =
            84f;

    private static final float SLOT_GAP =
            10f;


    private final Inventory inventory;

    private final HotbarSystem hotbarSystem;

    private final ToolDurabilitySystem
            toolDurabilitySystem;


    private final Material[] slotBorderMaterials =
            new Material[3];


    private final BitmapText[] amountTexts =
            new BitmapText[3];


    private final Node axeIconNode =
            new Node(
                    "AxeIcon"
            );


    private final Node durabilityNode =
            new Node(
                    "AxeDurability"
            );


    private final Geometry durabilityFill;

    private final BitmapText durabilityText;

    private final BitmapText selectedItemText;


    private final ColorRGBA normalBorderColor =
            new ColorRGBA(
                    0.3f,
                    0.3f,
                    0.3f,
                    1f
            );


    private final ColorRGBA selectedBorderColor =
            new ColorRGBA(
                    1f,
                    0.75f,
                    0.2f,
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
                SLOT_SIZE * 3
                        +
                        SLOT_GAP * 2;


        float startX =
                camera.getWidth() / 2f
                        -
                        totalWidth / 2f;


        float startY =
                24f;


        // ==========================
        // SLOT 1 - HOLZ
        // ==========================

        createSlot(
                assetManager,
                guiNode,
                font,
                0,
                1,
                "Holz",
                startX,
                startY
        );


        createWoodIcon(
                assetManager,
                guiNode,
                startX,
                startY
        );


        // ==========================
        // SLOT 2 - STEIN
        // ==========================

        float slot2X =
                startX
                        +
                        SLOT_SIZE
                        +
                        SLOT_GAP;


        createSlot(
                assetManager,
                guiNode,
                font,
                1,
                2,
                "Stein",
                slot2X,
                startY
        );


        createStoneIcon(
                assetManager,
                guiNode,
                slot2X,
                startY
        );


        // ==========================
        // SLOT 3 - STEINAXT
        // ==========================

        float slot3X =
                startX
                        +
                        (SLOT_SIZE + SLOT_GAP) * 2;


        createSlot(
                assetManager,
                guiNode,
                font,
                2,
                3,
                "Steinaxt",
                slot3X,
                startY
        );


        createAxeIcon(
                assetManager,
                guiNode,
                slot3X,
                startY
        );


        // ==========================
        // AUSGEWÄHLTES ITEM
        // ==========================

        selectedItemText =
                new BitmapText(
                        font
                );


        selectedItemText.setSize(
                17f
        );


        selectedItemText.setColor(
                ColorRGBA.White
        );


        selectedItemText.setLocalTranslation(
                startX,
                startY
                        +
                        SLOT_SIZE
                        +
                        30f,
                10f
        );


        guiNode.attachChild(
                selectedItemText
        );


        // ==========================
        // CRAFTING-HINWEIS
        // ==========================

        BitmapText craftingHint =
                new BitmapText(
                        font
                );


        craftingHint.setText(
                "[C] Steinaxt craften"
        );


        craftingHint.setSize(
                15f
        );


        craftingHint.setColor(
                new ColorRGBA(
                        0.9f,
                        0.9f,
                        0.9f,
                        1f
                )
        );


        craftingHint.setLocalTranslation(
                startX,
                startY
                        +
                        SLOT_SIZE
                        +
                        55f,
                10f
        );


        guiNode.attachChild(
                craftingHint
        );


        // ==========================
        // HALTBARKEITSANZEIGE
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
                28f,
                3f
        );


        durabilityNode.attachChild(
                durabilityText
        );


        Geometry durabilityBackground =
                new Geometry(
                        "DurabilityBackground",
                        new Quad(
                                180f,
                                10f
                        )
                );


        durabilityBackground.setMaterial(
                createGuiMaterial(
                        assetManager,
                        new ColorRGBA(
                                0.15f,
                                0.15f,
                                0.15f,
                                0.9f
                        )
                )
        );


        durabilityBackground.setQueueBucket(
                RenderQueue.Bucket.Gui
        );


        durabilityBackground.setLocalTranslation(
                0f,
                0f,
                1f
        );


        durabilityNode.attachChild(
                durabilityBackground
        );


        durabilityFill =
                new Geometry(
                        "DurabilityFill",
                        new Quad(
                                176f,
                                6f
                        )
                );


        durabilityFill.setMaterial(
                createGuiMaterial(
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
                startX
                        +
                        totalWidth / 2f
                        -
                        90f,
                startY
                        +
                        SLOT_SIZE
                        +
                        72f,
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
            int arrayIndex,
            int slotNumber,
            String itemName,
            float x,
            float y
    ) {

        Geometry border =
                new Geometry(
                        "SlotBorder"
                                +
                                slotNumber,
                        new Quad(
                                SLOT_SIZE,
                                SLOT_SIZE
                        )
                );


        Material borderMaterial =
                createGuiMaterial(
                        assetManager,
                        normalBorderColor
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


        slotBorderMaterials[arrayIndex] =
                borderMaterial;


        Geometry background =
                new Geometry(
                        "SlotBackground"
                                +
                                slotNumber,
                        new Quad(
                                INNER_SLOT_SIZE,
                                INNER_SLOT_SIZE
                        )
                );


        background.setMaterial(
                createGuiMaterial(
                        assetManager,
                        new ColorRGBA(
                                0.06f,
                                0.06f,
                                0.06f,
                                0.82f
                        )
                )
        );


        background.setQueueBucket(
                RenderQueue.Bucket.Gui
        );


        background.setLocalTranslation(
                x + 4f,
                y + 4f,
                2f
        );


        guiNode.attachChild(
                background
        );


        BitmapText slotNumberText =
                new BitmapText(
                        font
                );


        slotNumberText.setText(
                String.valueOf(
                        slotNumber
                )
        );


        slotNumberText.setSize(
                17f
        );


        slotNumberText.setColor(
                ColorRGBA.White
        );


        slotNumberText.setLocalTranslation(
                x + 9f,
                y + SLOT_SIZE - 9f,
                6f
        );


        guiNode.attachChild(
                slotNumberText
        );


        BitmapText itemNameText =
                new BitmapText(
                        font
                );


        itemNameText.setText(
                itemName
        );


        itemNameText.setSize(
                13f
        );


        itemNameText.setColor(
                new ColorRGBA(
                        0.85f,
                        0.85f,
                        0.85f,
                        1f
                )
        );


        itemNameText.setLocalTranslation(
                x + 8f,
                y + 18f,
                6f
        );


        guiNode.attachChild(
                itemNameText
        );


        BitmapText amountText =
                new BitmapText(
                        font
                );


        amountText.setSize(
                16f
        );


        amountText.setColor(
                ColorRGBA.White
        );


        amountText.setLocalTranslation(
                x + SLOT_SIZE - 34f,
                y + 18f,
                7f
        );


        guiNode.attachChild(
                amountText
        );


        amountTexts[arrayIndex] =
                amountText;
    }


    private void createWoodIcon(
            AssetManager assetManager,
            Node guiNode,
            float x,
            float y
    ) {

        Material material =
                createGuiMaterial(
                        assetManager,
                        new ColorRGBA(
                                0.48f,
                                0.27f,
                                0.12f,
                                1f
                        )
                );


        Geometry log1 =
                new Geometry(
                        "WoodIcon1",
                        new Quad(
                                15f,
                                46f
                        )
                );


        log1.setMaterial(
                material
        );


        log1.setQueueBucket(
                RenderQueue.Bucket.Gui
        );


        log1.setLocalTranslation(
                x + 29f,
                y + 30f,
                5f
        );


        guiNode.attachChild(
                log1
        );


        Geometry log2 =
                new Geometry(
                        "WoodIcon2",
                        new Quad(
                                15f,
                                46f
                        )
                );


        log2.setMaterial(
                material
        );


        log2.setQueueBucket(
                RenderQueue.Bucket.Gui
        );


        log2.setLocalTranslation(
                x + 49f,
                y + 30f,
                5f
        );


        guiNode.attachChild(
                log2
        );
    }


    private void createStoneIcon(
            AssetManager assetManager,
            Node guiNode,
            float x,
            float y
    ) {

        Material material =
                createGuiMaterial(
                        assetManager,
                        new ColorRGBA(
                                0.48f,
                                0.5f,
                                0.53f,
                                1f
                        )
                );


        Geometry stone =
                new Geometry(
                        "StoneIcon",
                        new Quad(
                                42f,
                                34f
                        )
                );


        stone.setMaterial(
                material
        );


        stone.setQueueBucket(
                RenderQueue.Bucket.Gui
        );


        stone.setLocalTranslation(
                x + 25f,
                y + 38f,
                5f
        );


        guiNode.attachChild(
                stone
        );
    }


    private void createAxeIcon(
            AssetManager assetManager,
            Node guiNode,
            float x,
            float y
    ) {

        Geometry handle =
                new Geometry(
                        "AxeIconHandle",
                        new Quad(
                                10f,
                                50f
                        )
                );


        handle.setMaterial(
                createGuiMaterial(
                        assetManager,
                        new ColorRGBA(
                                0.48f,
                                0.28f,
                                0.12f,
                                1f
                        )
                )
        );


        handle.setQueueBucket(
                RenderQueue.Bucket.Gui
        );


        handle.setLocalTranslation(
                36f,
                22f,
                0f
        );


        axeIconNode.attachChild(
                handle
        );


        Geometry head =
                new Geometry(
                        "AxeIconHead",
                        new Quad(
                                42f,
                                22f
                        )
                );


        head.setMaterial(
                createGuiMaterial(
                        assetManager,
                        new ColorRGBA(
                                0.55f,
                                0.57f,
                                0.6f,
                                1f
                        )
                )
        );


        head.setQueueBucket(
                RenderQueue.Bucket.Gui
        );


        head.setLocalTranslation(
                19f,
                59f,
                1f
        );


        axeIconNode.attachChild(
                head
        );


        axeIconNode.setLocalTranslation(
                x,
                y,
                5f
        );


        guiNode.attachChild(
                axeIconNode
        );
    }


    private Material createGuiMaterial(
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

        int wood =
                inventory.getAmount(
                        ItemType.WOOD
                );


        int stone =
                inventory.getAmount(
                        ItemType.STONE
                );


        int axes =
                inventory.getAmount(
                        ItemType.STONE_AXE
                );


        amountTexts[0].setText(
                "x" + wood
        );


        amountTexts[1].setText(
                "x" + stone
        );


        amountTexts[2].setText(
                "x" + axes
        );


        int selectedSlot =
                hotbarSystem
                        .getSelectedSlot();


        // ==========================
        // AUSWAHLRAHMEN
        // ==========================

        for (
                int i = 0;
                i < slotBorderMaterials.length;
                i++
        ) {

            if (
                    i
                            ==
                            selectedSlot - 1
            ) {

                slotBorderMaterials[i]
                        .setColor(
                                "Color",
                                selectedBorderColor
                        );
            }

            else {

                slotBorderMaterials[i]
                        .setColor(
                                "Color",
                                normalBorderColor
                        );
            }
        }


        // ==========================
        // AXT
        // ==========================

        if (axes > 0) {

            axeIconNode.setCullHint(
                    Spatial.CullHint.Inherit
            );


            durabilityNode.setCullHint(
                    Spatial.CullHint.Inherit
            );


            int durability =
                    toolDurabilitySystem
                            .getStoneAxeDurability();


            int maxDurability =
                    toolDurabilitySystem
                            .getStoneAxeMaxDurability();


            durabilityText.setText(
                    "Axt-Haltbarkeit: "
                            +
                            durability
                            +
                            " / "
                            +
                            maxDurability
            );


            float durabilityPercent =
                    toolDurabilitySystem
                            .getStoneAxeDurabilityPercent();


            durabilityFill.setLocalScale(
                    durabilityPercent,
                    1f,
                    1f
            );
        }

        else {

            axeIconNode.setCullHint(
                    Spatial.CullHint.Always
            );


            durabilityNode.setCullHint(
                    Spatial.CullHint.Always
            );
        }


        // ==========================
        // AUSGEWÄHLTES ITEM
        // ==========================

        switch (selectedSlot) {

            case 1:

                selectedItemText.setText(
                        "Ausgewählt: Holz"
                );

                break;


            case 2:

                selectedItemText.setText(
                        "Ausgewählt: Stein"
                );

                break;


            case 3:

                if (axes > 0) {

                    selectedItemText.setText(
                            "Ausgewählt: Steinaxt"
                    );
                }

                else {

                    selectedItemText.setText(
                            "Steinaxt nicht vorhanden"
                    );
                }

                break;


            default:

                selectedItemText.setText(
                        ""
                );

                break;
        }
    }
}