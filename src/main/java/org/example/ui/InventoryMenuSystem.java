package org.example.ui;

import com.jme3.asset.AssetManager;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.input.FlyByCamera;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
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

    private static final int COLUMNS =
            4;

    private static final float PANEL_WIDTH =
            740f;

    private static final float PANEL_HEIGHT =
            455f;

    private static final float SLOT_WIDTH =
            155f;

    private static final float SLOT_HEIGHT =
            72f;

    private static final float SLOT_GAP =
            12f;


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


    private final float[] slotX =
            new float[
                    Inventory.SLOT_COUNT
                    ];


    private final float[] slotY =
            new float[
                    Inventory.SLOT_COUNT
                    ];


    private BitmapText dragText;


    private int draggedFromIndex =
            -1;


    private boolean open =
            false;


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


        inputManager.addMapping(
                "InventoryDrag",
                new MouseButtonTrigger(
                        MouseInput.BUTTON_LEFT
                )
        );


        inputManager.addListener(
                this,
                "ToggleInventory",
                "InventoryDrag"
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


        BitmapText hint =
                new BitmapText(
                        font
                );


        hint.setText(
                "Ziehen = verschieben / stapeln / tauschen     [I] schließen"
        );


        hint.setSize(
                14f
        );


        hint.setColor(
                new ColorRGBA(
                        0.72f,
                        0.72f,
                        0.72f,
                        1f
                )
        );


        hint.setLocalTranslation(
                panelX + 250f,
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


            float currentX =
                    gridStartX
                            +
                            column
                                    *
                                    (SLOT_WIDTH + SLOT_GAP);


            float currentY =
                    gridTop
                            -
                            SLOT_HEIGHT
                            -
                            row
                                    *
                                    (SLOT_HEIGHT + SLOT_GAP);


            slotX[i] =
                    currentX;


            slotY[i] =
                    currentY;


            createSlot(
                    assetManager,
                    font,
                    i,
                    currentX,
                    currentY
            );
        }


        dragText =
                new BitmapText(
                        font
                );


        dragText.setSize(
                18f
        );


        dragText.setColor(
                new ColorRGBA(
                        1f,
                        0.85f,
                        0.25f,
                        1f
                )
        );


        dragText.setCullHint(
                Spatial.CullHint.Always
        );


        menuNode.attachChild(
                dragText
        );


        BitmapText footer =
                new BitmapText(
                        font
                );


        footer.setText(
                "Slots 1 - 3 = HOTBAR     |     Slots 4 - 16 = Rucksack"
        );


        footer.setSize(
                15f
        );


        footer.setColor(
                new ColorRGBA(
                        0.75f,
                        0.75f,
                        0.75f,
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


        ColorRGBA borderColor;


        if (
                index
                        <
                        Inventory.HOTBAR_SLOT_COUNT
        ) {

            borderColor =
                    new ColorRGBA(
                            0.7f,
                            0.52f,
                            0.12f,
                            1f
                    );
        }

        else {

            borderColor =
                    new ColorRGBA(
                            0.28f,
                            0.3f,
                            0.33f,
                            1f
                    );
        }


        border.setMaterial(
                createMaterial(
                        assetManager,
                        borderColor
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


        BitmapText text =
                new BitmapText(
                        font
                );


        text.setSize(
                14f
        );


        text.setColor(
                ColorRGBA.White
        );


        text.setLocalTranslation(
                x + 10f,
                y + SLOT_HEIGHT - 10f,
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

            return;
        }


        if (
                !name.equals(
                        "InventoryDrag"
                )
                        ||
                        !open
        ) {

            return;
        }


        if (isPressed) {

            startDrag();
        }

        else {

            finishDrag();
        }
    }


    private void startDrag() {

        Vector2f cursor =
                inputManager
                        .getCursorPosition()
                        .clone();


        int slotIndex =
                findSlotAt(
                        cursor
                );


        if (
                slotIndex < 0
        ) {

            return;
        }


        InventorySlot slot =
                inventory.getSlot(
                        slotIndex
                );


        if (
                slot.isEmpty()
        ) {

            return;
        }


        draggedFromIndex =
                slotIndex;


        dragText.setText(
                slot
                        .getItemType()
                        .getDisplayName()
                        +
                        " x"
                        +
                        slot.getAmount()
        );


        dragText.setCullHint(
                Spatial.CullHint.Inherit
        );
    }


    private void finishDrag() {

        if (
                draggedFromIndex < 0
        ) {

            return;
        }


        Vector2f cursor =
                inputManager
                        .getCursorPosition()
                        .clone();


        int targetIndex =
                findSlotAt(
                        cursor
                );


        if (
                targetIndex >= 0
        ) {

            inventory.moveStack(
                    draggedFromIndex,
                    targetIndex
            );
        }


        draggedFromIndex =
                -1;


        dragText.setCullHint(
                Spatial.CullHint.Always
        );


        update();
    }


    private int findSlotAt(
            Vector2f cursor
    ) {

        for (
                int i = 0;
                i < Inventory.SLOT_COUNT;
                i++
        ) {

            boolean insideX =
                    cursor.x
                            >=
                            slotX[i]
                            &&
                            cursor.x
                                    <=
                                    slotX[i]
                                            +
                                            SLOT_WIDTH;


            boolean insideY =
                    cursor.y
                            >=
                            slotY[i]
                            &&
                            cursor.y
                                    <=
                                    slotY[i]
                                            +
                                            SLOT_HEIGHT;


            if (
                    insideX
                            &&
                            insideY
            ) {

                return i;
            }
        }


        return -1;
    }


    private void toggle() {

        open =
                !open;


        if (open) {

            menuNode.setCullHint(
                    Spatial.CullHint.Inherit
            );


            inputManager.setCursorVisible(
                    true
            );


            flyCam.setEnabled(
                    false
            );


            player.setInputEnabled(
                    false
            );


            update();
        }

        else {

            cancelDrag();


            menuNode.setCullHint(
                    Spatial.CullHint.Always
            );


            inputManager.setCursorVisible(
                    false
            );


            flyCam.setEnabled(
                    true
            );


            player.setInputEnabled(
                    true
            );
        }
    }


    private void cancelDrag() {

        draggedFromIndex =
                -1;


        dragText.setCullHint(
                Spatial.CullHint.Always
        );
    }


    public void update() {

        if (!open) {

            return;
        }


        if (
                draggedFromIndex >= 0
        ) {

            Vector2f cursor =
                    inputManager
                            .getCursorPosition();


            dragText.setLocalTranslation(
                    cursor.x + 15f,
                    cursor.y + 18f,
                    100f
            );
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


            String slotTitle;


            if (
                    i
                            <
                            Inventory.HOTBAR_SLOT_COUNT
            ) {

                slotTitle =
                        "HOTBAR "
                                +
                                (i + 1);
            }

            else {

                slotTitle =
                        "Slot "
                                +
                                (i + 1);
            }


            if (
                    slot.isEmpty()
            ) {

                slotTexts[i].setText(
                        slotTitle
                                +
                                "\n\nLeer"
                );


                continue;
            }


            slotTexts[i].setText(
                    slotTitle
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