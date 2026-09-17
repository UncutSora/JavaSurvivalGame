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
import com.jme3.scene.shape.Quad;
import org.example.inventory.Inventory;
import org.example.inventory.ItemType;

public class InventoryHud {

    private final Inventory inventory;

    private final BitmapText itemText;


    public InventoryHud(
            AssetManager assetManager,
            Node guiNode,
            Camera camera,
            Inventory inventory
    ) {

        this.inventory =
                inventory;


        float width =
                430f;

        float height =
                60f;


        float x =
                camera.getWidth() / 2f
                        -
                        width / 2f;


        float y =
                20f;


        Geometry background =
                new Geometry(
                        "HotbarBackground",
                        new Quad(
                                width,
                                height
                        )
                );


        Material backgroundMaterial =
                new Material(
                        assetManager,
                        "Common/MatDefs/Misc/Unshaded.j3md"
                );


        backgroundMaterial.setColor(
                "Color",
                new ColorRGBA(
                        0f,
                        0f,
                        0f,
                        0.65f
                )
        );


        backgroundMaterial
                .getAdditionalRenderState()
                .setBlendMode(
                        RenderState.BlendMode.Alpha
                );


        background.setMaterial(
                backgroundMaterial
        );


        background.setQueueBucket(
                RenderQueue.Bucket.Gui
        );


        background.setLocalTranslation(
                x,
                y,
                0
        );


        guiNode.attachChild(
                background
        );


        BitmapFont font =
                assetManager.loadFont(
                        "Interface/Fonts/Default.fnt"
                );


        itemText =
                new BitmapText(
                        font
                );


        itemText.setColor(
                ColorRGBA.White
        );


        itemText.setSize(
                20f
        );


        itemText.setLocalTranslation(
                x + 18f,
                y + 38f,
                1f
        );


        guiNode.attachChild(
                itemText
        );


        update();
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


        int stoneAxes =
                inventory.getAmount(
                        ItemType.STONE_AXE
                );


        itemText.setText(
                "Holz: "
                        +
                        wood
                        +
                        "     Stein: "
                        +
                        stone
                        +
                        "     Steinaxt: "
                        +
                        stoneAxes
                        +
                        "     [C] Craft"
        );
    }
}