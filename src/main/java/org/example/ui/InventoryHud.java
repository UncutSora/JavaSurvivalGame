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

    private static final float SLOT_WIDTH = 112f;
    private static final float SLOT_HEIGHT = 82f;
    private static final float SLOT_GAP = 10f;

    private final Inventory inventory;
    private final HotbarSystem hotbarSystem;
    private final ToolDurabilitySystem toolDurabilitySystem;
    private final AssetManager assetManager;

    private final Material[] borderMaterials = new Material[3];
    private final BitmapText[] numberTexts = new BitmapText[3];
    private final BitmapText[] amountTexts = new BitmapText[3];
    private final Node[] iconNodes = new Node[3];

    private final Node durabilityNode = new Node("HotbarDurability");
    private final Geometry durabilityFill;
    private final BitmapText durabilityText;

    private final ColorRGBA normalBorder = new ColorRGBA(0.28f, 0.31f, 0.34f, 1f);
    private final ColorRGBA selectedBorder = new ColorRGBA(1f, 0.72f, 0.15f, 1f);

    public InventoryHud(
            AssetManager assetManager,
            Node guiNode,
            Camera camera,
            Inventory inventory,
            HotbarSystem hotbarSystem,
            ToolDurabilitySystem toolDurabilitySystem
    ) {
        this.assetManager = assetManager;
        this.inventory = inventory;
        this.hotbarSystem = hotbarSystem;
        this.toolDurabilitySystem = toolDurabilitySystem;

        BitmapFont font = assetManager.loadFont("Interface/Fonts/Default.fnt");

        float totalWidth = SLOT_WIDTH * 3f + SLOT_GAP * 2f;
        float startX = camera.getWidth() / 2f - totalWidth / 2f;
        float startY = 18f;

        for (int i = 0; i < 3; i++) {
            float x = startX + i * (SLOT_WIDTH + SLOT_GAP);
            createSlot(guiNode, font, i, x, startY);
        }

        durabilityText = new BitmapText(font);
        durabilityText.setSize(13f);
        durabilityText.setColor(ColorRGBA.White);
        durabilityText.setLocalTranslation(0f, 22f, 3f);
        durabilityNode.attachChild(durabilityText);

        Geometry durabilityBackground = new Geometry(
                "HotbarDurabilityBackground",
                new Quad(180f, 7f)
        );
        durabilityBackground.setMaterial(createMaterial(new ColorRGBA(0.08f, 0.09f, 0.10f, 0.95f)));
        durabilityBackground.setQueueBucket(RenderQueue.Bucket.Gui);
        durabilityNode.attachChild(durabilityBackground);

        durabilityFill = new Geometry(
                "HotbarDurabilityFill",
                new Quad(176f, 3f)
        );
        durabilityFill.setMaterial(createMaterial(new ColorRGBA(0.25f, 0.78f, 0.35f, 1f)));
        durabilityFill.setQueueBucket(RenderQueue.Bucket.Gui);
        durabilityFill.setLocalTranslation(2f, 2f, 2f);
        durabilityNode.attachChild(durabilityFill);

        durabilityNode.setLocalTranslation(
                camera.getWidth() / 2f - 90f,
                startY + SLOT_HEIGHT + 12f,
                10f
        );
        guiNode.attachChild(durabilityNode);

        update();
    }

    private void createSlot(Node guiNode, BitmapFont font, int index, float x, float y) {
        Geometry shadow = new Geometry("HotbarShadow" + index, new Quad(SLOT_WIDTH + 6f, SLOT_HEIGHT + 6f));
        shadow.setMaterial(createMaterial(new ColorRGBA(0f, 0f, 0f, 0.45f)));
        shadow.setQueueBucket(RenderQueue.Bucket.Gui);
        shadow.setLocalTranslation(x - 3f, y - 3f, 0f);
        guiNode.attachChild(shadow);

        Material borderMaterial = createMaterial(normalBorder);
        Geometry border = new Geometry("HotbarBorder" + index, new Quad(SLOT_WIDTH, SLOT_HEIGHT));
        border.setMaterial(borderMaterial);
        border.setQueueBucket(RenderQueue.Bucket.Gui);
        border.setLocalTranslation(x, y, 1f);
        guiNode.attachChild(border);
        borderMaterials[index] = borderMaterial;

        Geometry background = new Geometry(
                "HotbarBackground" + index,
                new Quad(SLOT_WIDTH - 4f, SLOT_HEIGHT - 4f)
        );
        background.setMaterial(createMaterial(new ColorRGBA(0.055f, 0.065f, 0.075f, 0.95f)));
        background.setQueueBucket(RenderQueue.Bucket.Gui);
        background.setLocalTranslation(x + 2f, y + 2f, 2f);
        guiNode.attachChild(background);

        BitmapText number = new BitmapText(font);
        number.setText(String.valueOf(index + 1));
        number.setSize(14f);
        number.setColor(new ColorRGBA(0.78f, 0.80f, 0.82f, 1f));
        number.setLocalTranslation(x + 8f, y + SLOT_HEIGHT - 7f, 5f);
        guiNode.attachChild(number);
        numberTexts[index] = number;

        BitmapText amount = new BitmapText(font);
        amount.setSize(15f);
        amount.setColor(ColorRGBA.White);
        amount.setLocalTranslation(x + SLOT_WIDTH - 34f, y + 18f, 6f);
        guiNode.attachChild(amount);
        amountTexts[index] = amount;

        Node icon = new Node("HotbarIcon" + index);
        icon.setLocalTranslation(x + 27f, y + 11f, 5f);
        guiNode.attachChild(icon);
        iconNodes[index] = icon;
    }

    private void rebuildIcon(Node target, ItemType type) {
        target.detachAllChildren();
        if (type == null) {
            return;
        }

        switch (type) {
            case WOOD:
                addRect(target, "Wood", 5f, 20f, 55f, 22f,
                        new ColorRGBA(0.48f, 0.27f, 0.10f, 1f));
                addRect(target, "WoodRing", 50f, 22f, 9f, 18f,
                        new ColorRGBA(0.72f, 0.48f, 0.23f, 1f));
                break;

            case STONE:
                addRect(target, "StoneA", 8f, 15f, 50f, 28f,
                        new ColorRGBA(0.55f, 0.58f, 0.61f, 1f));
                addRect(target, "StoneB", 18f, 34f, 34f, 11f,
                        new ColorRGBA(0.67f, 0.69f, 0.71f, 1f));
                break;

            case BERRIES:
                addRect(target, "Berry1", 9f, 15f, 18f, 18f,
                        new ColorRGBA(0.18f, 0.30f, 0.78f, 1f));
                addRect(target, "Berry2", 28f, 12f, 20f, 20f,
                        new ColorRGBA(0.13f, 0.22f, 0.68f, 1f));
                addRect(target, "Berry3", 22f, 31f, 18f, 18f,
                        new ColorRGBA(0.25f, 0.38f, 0.88f, 1f));
                addRect(target, "Leaf", 34f, 44f, 20f, 8f,
                        new ColorRGBA(0.25f, 0.62f, 0.20f, 1f));
                break;

            case WATER:
                addRect(target, "Bottle", 18f, 8f, 30f, 42f,
                        new ColorRGBA(0.12f, 0.48f, 0.76f, 1f));
                addRect(target, "BottleTop", 24f, 48f, 18f, 7f,
                        new ColorRGBA(0.72f, 0.76f, 0.78f, 1f));
                addRect(target, "WaterShine", 23f, 16f, 5f, 27f,
                        new ColorRGBA(0.48f, 0.82f, 1f, 0.9f));
                break;

            case STONE_AXE:
                addRect(target, "AxeHandle", 27f, 4f, 9f, 54f,
                        new ColorRGBA(0.52f, 0.31f, 0.14f, 1f));
                addRect(target, "AxeHead", 28f, 38f, 34f, 20f,
                        new ColorRGBA(0.68f, 0.70f, 0.71f, 1f));
                addRect(target, "AxeEdge", 57f, 40f, 6f, 16f,
                        new ColorRGBA(0.88f, 0.89f, 0.90f, 1f));
                break;

            case RAW_MEAT:
                addRect(target, "MeatMain", 8f, 14f, 49f, 32f,
                        new ColorRGBA(0.68f, 0.16f, 0.14f, 1f));
                addRect(target, "MeatFat", 16f, 36f, 35f, 9f,
                        new ColorRGBA(0.93f, 0.67f, 0.61f, 1f));
                break;

            case WOOL:
                addRect(target, "WoolA", 9f, 14f, 46f, 31f,
                        new ColorRGBA(0.90f, 0.90f, 0.86f, 1f));
                addRect(target, "WoolB", 18f, 34f, 35f, 18f,
                        new ColorRGBA(0.98f, 0.98f, 0.95f, 1f));
                break;
        }
    }

    private void addRect(Node target, String name, float x, float y,
                         float width, float height, ColorRGBA color) {
        Geometry geometry = new Geometry(name, new Quad(width, height));
        geometry.setMaterial(createMaterial(color));
        geometry.setQueueBucket(RenderQueue.Bucket.Gui);
        geometry.setLocalTranslation(x, y, 0f);
        target.attachChild(geometry);
    }

    private Material createMaterial(ColorRGBA color) {
        Material material = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        material.setColor("Color", color);
        material.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        return material;
    }

    public void update() {
        int selected = hotbarSystem.getSelectedSlot();

        for (int i = 0; i < 3; i++) {
            InventorySlot slot = inventory.getSlot(i);

            borderMaterials[i].setColor(
                    "Color",
                    selected == i + 1 ? selectedBorder : normalBorder
            );

            if (slot.isEmpty()) {
                amountTexts[i].setText("");
                rebuildIcon(iconNodes[i], null);
            } else {
                amountTexts[i].setText("x" + slot.getAmount());
                rebuildIcon(iconNodes[i], slot.getItemType());
            }
        }

        ItemType selectedType = hotbarSystem.getSelectedItemType();

        if (selectedType == ItemType.STONE_AXE && toolDurabilitySystem.hasUsableStoneAxe()) {
            durabilityNode.setCullHint(Spatial.CullHint.Inherit);
            durabilityText.setText(
                    "Steinaxt  " +
                            toolDurabilitySystem.getStoneAxeDurability() +
                            " / " +
                            toolDurabilitySystem.getStoneAxeMaxDurability()
            );
            durabilityFill.setLocalScale(
                    toolDurabilitySystem.getStoneAxeDurabilityPercent(),
                    1f,
                    1f
            );
        } else {
            durabilityNode.setCullHint(Spatial.CullHint.Always);
        }
    }
}
