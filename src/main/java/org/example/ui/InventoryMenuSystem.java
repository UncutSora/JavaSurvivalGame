package org.example.ui;

import com.jme3.asset.AssetManager;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.input.FlyByCamera;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.renderer.Camera;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Quad;

import org.example.inventory.Inventory;
import org.example.inventory.InventorySlot;
import org.example.player.Player;

public class InventoryMenuSystem implements ActionListener {

    private static final int COLUMNS = 4;

    private static final float PANEL_WIDTH = 740f;
    private static final float PANEL_HEIGHT = 455f;

    private static final float SLOT_WIDTH = 155f;
    private static final float SLOT_HEIGHT = 72f;
    private static final float SLOT_GAP = 12f;


    private final InputManager inputManager;

    private final FlyByCamera flyCam;

    private final Player player;

    private final Inventory inventory;


    private final Node menuNode =
            new Node(
                    "InventoryMenu"
            );


    private final BitmapText[] slotTexts =
            new BitmapText[
                    Inventory.SLOT_COUNT
                    ];


    private boolean open = false;


    public InventoryMenuSystem(
            AssetManager assetManager,
            Node guiNode,
            Camera camera,
            InputManager inputManager,
            FlyByCamera flyCam,
            Player player,
            Inventory inventory
    ) {

        this.inputManager =
                inputManager;

        this.flyCam =
                flyCam;

        this.player =
                player;

        this.inventory =
                inventory;


        createMenu(
                assetManager,
                camera
        );


        menuNode.setLocalTranslation(
                0f,
                0f,
                50f
        );


        guiNode.attachChild(
                menuNode
        );


        menuNode.setCullHint(
                Spatial.CullHint.Always
        );


        inputManager.addMapping(
                "ToggleInventory",
                new KeyTrigger(
                        KeyInput.KEY_I
                )
        );


        inputManager.addListener(
                this,
                "ToggleInventory"
        );
    }


    private void createMenu(
            AssetManager assetManager,
            Camera camera
    ) {

        BitmapFont font =
                assetManager.loadFont(
                        "Interface/Fonts/Default.fnt"
                );


        float panelX =
                camera.getWidth() / 2f
                        -
                        PANEL_WIDTH / 2f;


        float panelY =
                camera.getHeight() / 2f
                        -
                        PANEL_HEIGHT / 2f;


        // ==========================
        // HAUPTFENSTER
        // ==========================

        Geometry panel =
                new Geometry(
                        "InventoryPanel",
                        new Quad(
                                PANEL_WIDTH,
                                PANEL_HEIGHT
                        )
                );


        panel.setMaterial(
                createMaterial(
                        assetManager,
                        new ColorRGBA(
                                0.035f,
                                0.04f,
                                0.045f,
                                0.96f
                        )
                )
        );


        panel.setQueueBucket(
                RenderQueue.Bucket.Gui
        );


        panel.setLocalTranslation(
                panelX,
                panelY,
                0f
        );


        menuNode.attachChild(
                panel
        );


        // ==========================
        // TITEL
        // ==========================

        BitmapText title =
                new BitmapText(
                        font
                );


        title.setText(
                "INVENTAR"
        );


        title.setSize(
                28f
        );


        title.setColor(
                ColorRGBA.White
        );


        title.setLocalTranslation(
                panelX + 30f,
                panelY
                        +
                        PANEL_HEIGHT
                        -
                        28f,
                5f
        );


        menuNode.attachChild(
                title
        );


        // ==========================
        // SCHLIESS-HINWEIS
        // ==========================

        BitmapText hint =
                new BitmapText(
                        font
                );


        hint.setText(
                "[I] Inventar schließen"
        );


        hint.setSize(
                15f
        );


        hint.setColor(
                new ColorRGBA(
                        0.75f,
                        0.75f,
                        0.75f,
                        1f
                )
        );


        hint.setLocalTranslation(
                panelX
                        +
                        PANEL_WIDTH
                        -
                        190f,
                panelY
                        +
                        PANEL_HEIGHT
                        -
                        25f,
                5f
        );


        menuNode.attachChild(
                hint
        );


        // ==========================
        // 4 x 4 INVENTAR
        // ==========================

        float gridWidth =
                COLUMNS * SLOT_WIDTH
                        +
                        (COLUMNS - 1)
                                *
                                SLOT_GAP;


        float gridStartX =
                panelX
                        +
                        (PANEL_WIDTH - gridWidth)
                                /
                                2f;


        float gridTop =
                panelY
                        +
                        PANEL_HEIGHT
                        -
                        90f;


        for (
                int i = 0;
                i < Inventory.SLOT_COUNT;
                i++
        ) {

            int row =
                    i / COLUMNS;


            int column =
                    i % COLUMNS;


            float slotX =
                    gridStartX
                            +
                            column
                                    *
                                    (SLOT_WIDTH + SLOT_GAP);


            float slotY =
                    gridTop
                            -
                            SLOT_HEIGHT
                            -
                            row
                                    *
                                    (SLOT_HEIGHT + SLOT_GAP);


            createSlot(
                    assetManager,
                    font,
                    i,
                    slotX,
                    slotY
            );
        }


        // ==========================
        // FOOTER
        // ==========================

        BitmapText footer =
                new BitmapText(
                        font
                );


        footer.setText(
                "16 Inventarplätze  |  Holz/Stein bis 99  |  Werkzeuge einzeln"
        );


        footer.setSize(
                14f
        );


        footer.setColor(
                new ColorRGBA(
                        0.65f,
                        0.65f,
                        0.65f,
                        1f
                )
        );


        footer.setLocalTranslation(
                panelX + 30f,
                panelY + 27f,
                5f
        );


        menuNode.attachChild(
                footer
        );
    }


    private void createSlot(
            AssetManager assetManager,
            BitmapFont font,
            int index,
            float x,
            float y
    ) {

        // ==========================
        // SLOT-RAHMEN
        // ==========================

        Geometry border =
                new Geometry(
                        "InventorySlotBorder"
                                +
                                index,
                        new Quad(
                                SLOT_WIDTH,
                                SLOT_HEIGHT
                        )
                );


        border.setMaterial(
                createMaterial(
                        assetManager,
                        new ColorRGBA(
                                0.28f,
                                0.3f,
                                0.33f,
                                1f
                        )
                )
        );


        border.setQueueBucket(
                RenderQueue.Bucket.Gui
        );


        border.setLocalTranslation(
                x,
                y,
                1f
        );


        menuNode.attachChild(
                border
        );


        // ==========================
        // SLOT-HINTERGRUND
        // ==========================

        Geometry background =
                new Geometry(
                        "InventorySlotBackground"
                                +
                                index,
                        new Quad(
                                SLOT_WIDTH - 4f,
                                SLOT_HEIGHT - 4f
                        )
                );


        background.setMaterial(
                createMaterial(
                        assetManager,
                        new ColorRGBA(
                                0.09f,
                                0.1f,
                                0.11f,
                                1f
                        )
                )
        );


        background.setQueueBucket(
                RenderQueue.Bucket.Gui
        );


        background.setLocalTranslation(
                x + 2f,
                y + 2f,
                2f
        );


        menuNode.attachChild(
                background
        );


        // ==========================
        // SLOT-TEXT
        // ==========================

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
                y + SLOT_HEIGHT - 12f,
                4f
        );


        menuNode.attachChild(
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


    @Override
    public void onAction(
            String name,
            boolean isPressed,
            float tpf
    ) {

        if (
                name.equals(
                        "ToggleInventory"
                )
                        &&
                        isPressed
        ) {

            toggle();
        }
    }


    private void toggle() {

        open =
                !open;


        if (open) {

            // Inventar anzeigen

            menuNode.setCullHint(
                    Spatial.CullHint.Inherit
            );


            // Maus anzeigen

            inputManager.setCursorVisible(
                    true
            );


            // Kamera deaktivieren

            flyCam.setEnabled(
                    false
            );


            // Spieler stoppen

            player.setInputEnabled(
                    false
            );


            update();
        }

        else {

            // Inventar verstecken

            menuNode.setCullHint(
                    Spatial.CullHint.Always
            );


            // Maus wieder verstecken

            inputManager.setCursorVisible(
                    false
            );


            // Kamera aktivieren

            flyCam.setEnabled(
                    true
            );


            // Spielersteuerung aktivieren

            player.setInputEnabled(
                    true
            );
        }
    }


    public void update() {

        if (!open) {

            return;
        }


        for (
                int i = 0;
                i < inventory.getSlotCount();
                i++
        ) {

            InventorySlot slot =
                    inventory.getSlot(
                            i
                    );


            // ==========================
            // LEERER SLOT
            // ==========================

            if (slot.isEmpty()) {

                slotTexts[i].setText(
                        "Slot "
                                +
                                (i + 1)
                                +
                                "\n\nLeer"
                );


                continue;
            }


            // ==========================
            // BELEGTER SLOT
            // ==========================

            slotTexts[i].setText(
                    "Slot "
                            +
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
    }


    public boolean isOpen() {

        return open;
    }
}