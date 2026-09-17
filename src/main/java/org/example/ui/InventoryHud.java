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
import org.example.hotbar.HotbarSystem;
import org.example.inventory.Inventory;
import org.example.inventory.ItemType;

public class InventoryHud {

    private final Inventory inventory;

    private final HotbarSystem hotbarSystem;

    private final BitmapText itemText;


    public InventoryHud(
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


        float width =
                620f;


        float height =
                70f;


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
                19f
        );


        itemText.setLocalTranslation(
                x + 18f,
                y + 45f,
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


        int axes =
                inventory.getAmount(
                        ItemType.STONE_AXE
                );


        int selected =
                hotbarSystem
                        .getSelectedSlot();


        itemText.setText(

                formatSlot(
                        1,
                        "Holz",
                        wood,
                        selected
                )

                        +

                        "     "

                        +

                        formatSlot(
                                2,
                                "Stein",
                                stone,
                                selected
                        )

                        +

                        "     "

                        +

                        formatSlot(
                                3,
                                "Steinaxt",
                                axes,
                                selected
                        )

                        +

                        "\n[C] Steinaxt craften"
        );
    }


    private String formatSlot(
            int slot,
            String name,
            int amount,
            int selected
    ) {

        if (slot == selected) {

            return "> "
                    +
                    slot
                    +
                    " "
                    +
                    name
                    +
                    ": "
                    +
                    amount
                    +
                    " <";
        }


        return slot
                +
                " "
                +
                name
                +
                ": "
                +
                amount;
    }
}