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
import org.example.inventory.ItemType;
import org.example.player.Player;
import org.example.tools.ToolDurabilitySystem;

public class InventoryMenuSystem implements ActionListener {

    private static final int COLUMNS = 4;
    private static final float PANEL_WIDTH = 980f;
    private static final float PANEL_HEIGHT = 570f;
    private static final float SLOT_WIDTH = 145f;
    private static final float SLOT_HEIGHT = 96f;
    private static final float SLOT_GAP = 10f;

    private final InputManager inputManager;
    private final FlyByCamera flyCam;
    private final Player player;
    private final Inventory inventory;
    private final ToolDurabilitySystem toolDurabilitySystem;
    private final Node menuNode = new Node("InventoryMenu");
    private final BitmapText[] slotLabels = new BitmapText[Inventory.SLOT_COUNT];
    private final BitmapText[] slotAmounts = new BitmapText[Inventory.SLOT_COUNT];
    private final Node[] slotIconNodes = new Node[Inventory.SLOT_COUNT];
    private final Material[] slotBorderMaterials = new Material[Inventory.SLOT_COUNT];
    private final float[] slotX = new float[Inventory.SLOT_COUNT];
    private final float[] slotY = new float[Inventory.SLOT_COUNT];

    private AssetManager assetManager;
    private BitmapFont font;
    private BitmapText dragText;
    private BitmapText detailName;
    private BitmapText detailDescription;
    private BitmapText detailStats;
    private Node detailIconNode;
    private int draggedFromIndex = -1;
    private int detailIndex = 0;
    private boolean open = false;

    private final ColorRGBA borderNormal = new ColorRGBA(0.28f, 0.31f, 0.34f, 1f);
    private final ColorRGBA borderHotbar = new ColorRGBA(0.55f, 0.43f, 0.16f, 1f);
    private final ColorRGBA borderSelected = new ColorRGBA(1f, 0.72f, 0.15f, 1f);

    public InventoryMenuSystem(
            AssetManager assetManager,
            Node guiNode,
            Camera camera,
            InputManager inputManager,
            FlyByCamera flyCam,
            Player player,
            Inventory inventory,
            ToolDurabilitySystem toolDurabilitySystem
    ) {
        this.assetManager = assetManager;
        this.inputManager = inputManager;
        this.flyCam = flyCam;
        this.player = player;
        this.inventory = inventory;
        this.toolDurabilitySystem = toolDurabilitySystem;

        createMenu(camera);
        menuNode.setLocalTranslation(0f, 0f, 50f);
        guiNode.attachChild(menuNode);
        menuNode.setCullHint(Spatial.CullHint.Always);

        inputManager.addMapping("ToggleInventory", new KeyTrigger(KeyInput.KEY_I));
        inputManager.addMapping("InventoryDrag", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        inputManager.addListener(this, "ToggleInventory", "InventoryDrag");
    }

    private void createMenu(Camera camera) {
        font = assetManager.loadFont("Interface/Fonts/Default.fnt");

        float panelX = camera.getWidth() / 2f - PANEL_WIDTH / 2f;
        float panelY = camera.getHeight() / 2f - PANEL_HEIGHT / 2f;

        createRect("InventoryShadow", panelX - 6f, panelY - 6f,
                PANEL_WIDTH + 12f, PANEL_HEIGHT + 12f,
                new ColorRGBA(0f, 0f, 0f, 0.48f), 0f);

        createRect("InventoryPanel", panelX, panelY, PANEL_WIDTH, PANEL_HEIGHT,
                new ColorRGBA(0.035f, 0.045f, 0.055f, 0.97f), 1f);

        createRect("HeaderLine", panelX, panelY + PANEL_HEIGHT - 70f, PANEL_WIDTH, 2f,
                new ColorRGBA(0.24f, 0.27f, 0.30f, 1f), 2f);

        createText("INVENTAR", 30f, ColorRGBA.White,
                panelX + 32f, panelY + PANEL_HEIGHT - 27f, 5f);

        createText("Ziehen = verschieben / stapeln / tauschen     [I] schließen",
                14f, new ColorRGBA(0.72f, 0.74f, 0.77f, 1f),
                panelX + 325f, panelY + PANEL_HEIGHT - 30f, 5f);

        createRect("BackpackTab", panelX + 30f, panelY + PANEL_HEIGHT - 125f,
                190f, 42f, new ColorRGBA(0.16f, 0.14f, 0.08f, 1f), 2f);
        createText("RUCKSACK", 16f, new ColorRGBA(1f, 0.78f, 0.25f, 1f),
                panelX + 55f, panelY + PANEL_HEIGHT - 96f, 5f);

        float gridStartX = panelX + 30f;
        float gridTop = panelY + PANEL_HEIGHT - 145f;

        for (int i = 0; i < Inventory.SLOT_COUNT; i++) {
            int row = i / COLUMNS;
            int column = i % COLUMNS;
            float x = gridStartX + column * (SLOT_WIDTH + SLOT_GAP);
            float y = gridTop - SLOT_HEIGHT - row * (SLOT_HEIGHT + SLOT_GAP);
            slotX[i] = x;
            slotY[i] = y;
            createSlot(i, x, y);
        }

        float detailsX = panelX + 670f;
        float detailsY = panelY + 62f;
        float detailsWidth = 280f;
        float detailsHeight = 390f;

        createRect("DetailsBorder", detailsX, detailsY, detailsWidth, detailsHeight,
                new ColorRGBA(0.25f, 0.29f, 0.33f, 1f), 2f);
        createRect("DetailsBackground", detailsX + 2f, detailsY + 2f,
                detailsWidth - 4f, detailsHeight - 4f,
                new ColorRGBA(0.055f, 0.065f, 0.075f, 1f), 3f);

        createText("ITEM-DETAILS", 13f, new ColorRGBA(0.58f, 0.61f, 0.64f, 1f),
                detailsX + 18f, detailsY + detailsHeight - 18f, 5f);

        createRect("DetailIconBackground", detailsX + 18f, detailsY + 235f,
                244f, 105f, new ColorRGBA(0.075f, 0.085f, 0.095f, 1f), 4f);

        detailIconNode = new Node("DetailIcon");
        detailIconNode.setLocalTranslation(detailsX + 96f, detailsY + 255f, 8f);
        menuNode.attachChild(detailIconNode);

        detailName = createText("", 22f, ColorRGBA.White,
                detailsX + 18f, detailsY + 215f, 6f);
        detailDescription = createText("", 14f, new ColorRGBA(0.72f, 0.74f, 0.77f, 1f),
                detailsX + 18f, detailsY + 180f, 6f);
        detailStats = createText("", 14f, new ColorRGBA(0.88f, 0.89f, 0.90f, 1f),
                detailsX + 18f, detailsY + 82f, 6f);

        createText("Slots 1 - 3 = HOTBAR     |     Slots 4 - 16 = Rucksack",
                14f, new ColorRGBA(0.68f, 0.70f, 0.73f, 1f),
                panelX + 30f, panelY + 26f, 5f);

        dragText = new BitmapText(font);
        dragText.setSize(17f);
        dragText.setColor(new ColorRGBA(1f, 0.82f, 0.25f, 1f));
        dragText.setCullHint(Spatial.CullHint.Always);
        menuNode.attachChild(dragText);
    }

    private void createSlot(int index, float x, float y) {
        Material borderMaterial = createMaterial(index < Inventory.HOTBAR_SLOT_COUNT
                ? borderHotbar : borderNormal);
        Geometry border = createQuad("InventorySlotBorder" + index, SLOT_WIDTH, SLOT_HEIGHT, borderMaterial);
        border.setLocalTranslation(x, y, 2f);
        menuNode.attachChild(border);
        slotBorderMaterials[index] = borderMaterial;

        createRect("InventorySlotBackground" + index, x + 2f, y + 2f,
                SLOT_WIDTH - 4f, SLOT_HEIGHT - 4f,
                new ColorRGBA(0.075f, 0.085f, 0.095f, 1f), 3f);

        String title = index < Inventory.HOTBAR_SLOT_COUNT
                ? "HOTBAR " + (index + 1)
                : "Slot " + (index + 1);

        slotLabels[index] = createText(title, 12f,
                new ColorRGBA(0.67f, 0.70f, 0.73f, 1f),
                x + 8f, y + SLOT_HEIGHT - 8f, 6f);

        slotAmounts[index] = createText("", 15f, ColorRGBA.White,
                x + SLOT_WIDTH - 40f, y + 20f, 7f);

        Node icon = new Node("InventoryIcon" + index);
        icon.setLocalTranslation(x + 44f, y + 17f, 7f);
        menuNode.attachChild(icon);
        slotIconNodes[index] = icon;
    }

    private void rebuildIcon(Node target, ItemType type, float scale) {
        target.detachAllChildren();
        if (type == null) {
            return;
        }

        switch (type) {
            case WOOD:
                addIconRect(target, "Wood", 5f, 20f, 62f, 24f,
                        new ColorRGBA(0.48f, 0.27f, 0.10f, 1f), scale);
                addIconRect(target, "WoodRing", 55f, 22f, 10f, 20f,
                        new ColorRGBA(0.72f, 0.48f, 0.23f, 1f), scale);
                break;

            case STONE:
                addIconRect(target, "StoneA", 10f, 16f, 52f, 30f,
                        new ColorRGBA(0.55f, 0.58f, 0.61f, 1f), scale);
                addIconRect(target, "StoneB", 20f, 36f, 36f, 12f,
                        new ColorRGBA(0.67f, 0.69f, 0.71f, 1f), scale);
                break;

            case BERRIES:
                addIconRect(target, "Berry1", 12f, 17f, 20f, 20f,
                        new ColorRGBA(0.18f, 0.30f, 0.78f, 1f), scale);
                addIconRect(target, "Berry2", 32f, 14f, 22f, 22f,
                        new ColorRGBA(0.13f, 0.22f, 0.68f, 1f), scale);
                addIconRect(target, "Berry3", 25f, 34f, 20f, 20f,
                        new ColorRGBA(0.25f, 0.38f, 0.88f, 1f), scale);
                addIconRect(target, "Leaf", 39f, 48f, 22f, 9f,
                        new ColorRGBA(0.25f, 0.62f, 0.20f, 1f), scale);
                break;

            case WATER:
                addIconRect(target, "Bottle", 19f, 9f, 34f, 45f,
                        new ColorRGBA(0.12f, 0.48f, 0.76f, 1f), scale);
                addIconRect(target, "BottleTop", 27f, 52f, 18f, 8f,
                        new ColorRGBA(0.72f, 0.76f, 0.78f, 1f), scale);
                addIconRect(target, "WaterShine", 25f, 18f, 5f, 28f,
                        new ColorRGBA(0.48f, 0.82f, 1f, 0.9f), scale);
                break;

            case STONE_AXE:
                addIconRect(target, "AxeHandle", 30f, 5f, 10f, 58f,
                        new ColorRGBA(0.52f, 0.31f, 0.14f, 1f), scale);
                addIconRect(target, "AxeHead", 31f, 42f, 38f, 22f,
                        new ColorRGBA(0.68f, 0.70f, 0.71f, 1f), scale);
                addIconRect(target, "AxeEdge", 63f, 44f, 7f, 18f,
                        new ColorRGBA(0.88f, 0.89f, 0.90f, 1f), scale);
                break;

            case RAW_MEAT:
                addIconRect(target, "MeatMain", 11f, 16f, 52f, 35f,
                        new ColorRGBA(0.68f, 0.16f, 0.14f, 1f), scale);
                addIconRect(target, "MeatFat", 18f, 40f, 38f, 10f,
                        new ColorRGBA(0.93f, 0.67f, 0.61f, 1f), scale);
                break;

            case WOOL:
                addIconRect(target, "WoolA", 12f, 16f, 48f, 34f,
                        new ColorRGBA(0.90f, 0.90f, 0.86f, 1f), scale);
                addIconRect(target, "WoolB", 22f, 38f, 38f, 20f,
                        new ColorRGBA(0.98f, 0.98f, 0.95f, 1f), scale);
                addIconRect(target, "WoolShadow", 9f, 12f, 16f, 18f,
                        new ColorRGBA(0.72f, 0.72f, 0.69f, 1f), scale);
                break;
        }
    }

    private void addIconRect(Node target, String name, float x, float y,
                             float width, float height, ColorRGBA color, float scale) {
        Geometry geometry = createQuad(name, width * scale, height * scale, createMaterial(color));
        geometry.setLocalTranslation(x * scale, y * scale, 0f);
        target.attachChild(geometry);
    }

    private Geometry createQuad(String name, float width, float height, Material material) {
        Geometry geometry = new Geometry(name, new Quad(width, height));
        geometry.setMaterial(material);
        geometry.setQueueBucket(RenderQueue.Bucket.Gui);
        return geometry;
    }

    private void createRect(String name, float x, float y, float width, float height,
                            ColorRGBA color, float z) {
        Geometry geometry = createQuad(name, width, height, createMaterial(color));
        geometry.setLocalTranslation(x, y, z);
        menuNode.attachChild(geometry);
    }

    private BitmapText createText(String value, float size, ColorRGBA color,
                                  float x, float y, float z) {
        BitmapText text = new BitmapText(font);
        text.setText(value);
        text.setSize(size);
        text.setColor(color);
        text.setLocalTranslation(x, y, z);
        menuNode.attachChild(text);
        return text;
    }

    private Material createMaterial(ColorRGBA color) {
        Material material = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        material.setColor("Color", color);
        material.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        return material;
    }

    @Override
    public void onAction(String name, boolean isPressed, float tpf) {
        if (name.equals("ToggleInventory") && isPressed) {
            toggle();
            return;
        }

        if (!name.equals("InventoryDrag") || !open) {
            return;
        }

        if (isPressed) {
            startDrag();
        } else {
            finishDrag();
        }
    }

    private void startDrag() {
        int slotIndex = findSlotAt(inputManager.getCursorPosition());
        if (slotIndex < 0) {
            return;
        }

        detailIndex = slotIndex;
        updateDetails();

        InventorySlot slot = inventory.getSlot(slotIndex);
        if (slot.isEmpty()) {
            return;
        }

        draggedFromIndex = slotIndex;
        dragText.setText(slot.getItemType().getDisplayName() + " x" + slot.getAmount());
        dragText.setCullHint(Spatial.CullHint.Inherit);
    }

    private void finishDrag() {
        if (draggedFromIndex < 0) {
            return;
        }

        int targetIndex = findSlotAt(inputManager.getCursorPosition());
        if (targetIndex >= 0) {
            inventory.moveStack(draggedFromIndex, targetIndex);
            detailIndex = targetIndex;
        }

        draggedFromIndex = -1;
        dragText.setCullHint(Spatial.CullHint.Always);
        update();
    }

    private int findSlotAt(Vector2f cursor) {
        for (int i = 0; i < Inventory.SLOT_COUNT; i++) {
            boolean insideX = cursor.x >= slotX[i] && cursor.x <= slotX[i] + SLOT_WIDTH;
            boolean insideY = cursor.y >= slotY[i] && cursor.y <= slotY[i] + SLOT_HEIGHT;
            if (insideX && insideY) {
                return i;
            }
        }
        return -1;
    }

    private void toggle() {
        open = !open;

        if (open) {
            menuNode.setCullHint(Spatial.CullHint.Inherit);
            inputManager.setCursorVisible(true);
            flyCam.setEnabled(false);
            player.setInputEnabled(false);
            selectFirstUsefulSlot();
            update();
        } else {
            cancelDrag();
            menuNode.setCullHint(Spatial.CullHint.Always);
            inputManager.setCursorVisible(false);
            flyCam.setEnabled(true);
            player.setInputEnabled(true);
        }
    }

    private void selectFirstUsefulSlot() {
        if (detailIndex >= 0 && detailIndex < inventory.getSlotCount()
                && !inventory.getSlot(detailIndex).isEmpty()) {
            return;
        }

        for (int i = 0; i < inventory.getSlotCount(); i++) {
            if (!inventory.getSlot(i).isEmpty()) {
                detailIndex = i;
                return;
            }
        }
        detailIndex = 0;
    }

    private void cancelDrag() {
        draggedFromIndex = -1;
        dragText.setCullHint(Spatial.CullHint.Always);
    }

    public void update() {
        if (!open) {
            return;
        }

        if (draggedFromIndex >= 0) {
            Vector2f cursor = inputManager.getCursorPosition();
            dragText.setLocalTranslation(cursor.x + 15f, cursor.y + 18f, 100f);
        }

        for (int i = 0; i < inventory.getSlotCount(); i++) {
            InventorySlot slot = inventory.getSlot(i);

            ColorRGBA color;
            if (i == detailIndex) {
                color = borderSelected;
            } else if (i < Inventory.HOTBAR_SLOT_COUNT) {
                color = borderHotbar;
            } else {
                color = borderNormal;
            }
            slotBorderMaterials[i].setColor("Color", color);

            if (slot.isEmpty()) {
                slotAmounts[i].setText("Leer");
                slotAmounts[i].setColor(new ColorRGBA(0.48f, 0.51f, 0.54f, 1f));
                rebuildIcon(slotIconNodes[i], null, 0.72f);
            } else {
                slotAmounts[i].setText("x" + slot.getAmount());
                slotAmounts[i].setColor(ColorRGBA.White);
                rebuildIcon(slotIconNodes[i], slot.getItemType(), 0.72f);
            }
        }

        updateDetails();
    }

    private void updateDetails() {
        if (detailIndex < 0 || detailIndex >= inventory.getSlotCount()) {
            return;
        }

        InventorySlot slot = inventory.getSlot(detailIndex);
        detailIconNode.detachAllChildren();

        if (slot.isEmpty()) {
            detailName.setText("Leerer Slot");
            detailDescription.setText("Hier ist Platz für\nRessourcen oder Werkzeuge.");
            detailStats.setText("");
            return;
        }

        ItemType type = slot.getItemType();
        rebuildIcon(detailIconNode, type, 1.25f);
        detailName.setText(type.getDisplayName());

        switch (type) {
            case STONE_AXE:
                detailDescription.setText("Einfache Axt aus Stein.\nZum Fällen von Bäumen\nund Abbauen von Ressourcen.");
                detailStats.setText(
                        "Menge        " + slot.getAmount()
                                + "\nHaltbarkeit   "
                                + toolDurabilitySystem.getStoneAxeDurability()
                                + " / "
                                + toolDurabilitySystem.getStoneAxeMaxDurability()
                );
                break;

            case WOOD:
                detailDescription.setText("Grundlegender Baustoff.\nWird für Gebäude und\nCrafting verwendet.");
                detailStats.setText("Menge        " + slot.getAmount() + "\nMax. Stack   " + type.getMaxStack());
                break;

            case STONE:
                detailDescription.setText("Robuste Ressource für\nWerkzeuge und Crafting.");
                detailStats.setText("Menge        " + slot.getAmount() + "\nMax. Stack   " + type.getMaxStack());
                break;

            case BERRIES:
                detailDescription.setText("Essbare Beeren.\nStillen etwas Hunger.");
                detailStats.setText("Menge        " + slot.getAmount() + "\nMax. Stack   " + type.getMaxStack());
                break;

            case WATER:
                detailDescription.setText("Trinkbares Wasser.\nStillt deinen Durst.");
                detailStats.setText("Menge        " + slot.getAmount() + "\nMax. Stack   " + type.getMaxStack());
                break;

            case RAW_MEAT:
                detailDescription.setText("Rohes Fleisch vom Schaf.\nNoch nicht zum sicheren\nVerzehr vorbereitet.");
                detailStats.setText("Menge        " + slot.getAmount() + "\nMax. Stack   " + type.getMaxStack());
                break;

            case WOOL:
                detailDescription.setText("Weiche Schafwolle.\nWird spaeter fuer ein\nBett benoetigt.");
                detailStats.setText("Menge        " + slot.getAmount() + "\nMax. Stack   " + type.getMaxStack());
                break;
        }
    }

    public boolean isOpen() {
        return open;
    }
}
