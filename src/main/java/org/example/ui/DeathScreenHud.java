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

public class DeathScreenHud {

    private final Node deathNode =
            new Node(
                    "DeathScreen"
            );

    private final BitmapText titleText;

    private final BitmapText respawnText;

    private final BitmapText locationText;

    private final Camera camera;


    public DeathScreenHud(
            AssetManager assetManager,
            Node guiNode,
            Camera camera
    ) {

        this.camera =
                camera;


        Material overlayMaterial =
                new Material(
                        assetManager,
                        "Common/MatDefs/Misc/Unshaded.j3md"
                );

        overlayMaterial.setColor(
                "Color",
                new ColorRGBA(
                        0.02f,
                        0.01f,
                        0.01f,
                        0.88f
                )
        );

        overlayMaterial
                .getAdditionalRenderState()
                .setBlendMode(
                        RenderState.BlendMode.Alpha
                );


        Geometry overlay =
                new Geometry(
                        "DeathOverlay",
                        new Quad(
                                camera.getWidth(),
                                camera.getHeight()
                        )
                );

        overlay.setMaterial(
                overlayMaterial
        );

        overlay.setQueueBucket(
                RenderQueue.Bucket.Gui
        );

        overlay.setLocalTranslation(
                0f,
                0f,
                0f
        );

        deathNode.attachChild(
                overlay
        );


        BitmapFont font =
                assetManager.loadFont(
                        "Interface/Fonts/Default.fnt"
                );


        titleText =
                new BitmapText(
                        font
                );

        titleText.setText(
                "DU BIST GESTORBEN"
        );

        titleText.setSize(
                42f
        );

        titleText.setColor(
                new ColorRGBA(
                        0.90f,
                        0.12f,
                        0.10f,
                        1f
                )
        );

        titleText.setLocalTranslation(
                0f,
                0f,
                5f
        );

        deathNode.attachChild(
                titleText
        );


        respawnText =
                new BitmapText(
                        font
                );

        respawnText.setSize(
                24f
        );

        respawnText.setColor(
                ColorRGBA.White
        );

        respawnText.setLocalTranslation(
                0f,
                0f,
                5f
        );

        deathNode.attachChild(
                respawnText
        );


        locationText =
                new BitmapText(
                        font
                );

        locationText.setSize(
                17f
        );

        locationText.setColor(
                new ColorRGBA(
                        0.72f,
                        0.74f,
                        0.76f,
                        1f
                )
        );

        locationText.setLocalTranslation(
                0f,
                0f,
                5f
        );

        deathNode.attachChild(
                locationText
        );


        deathNode.setLocalTranslation(
                0f,
                0f,
                200f
        );

        deathNode.setCullHint(
                Spatial.CullHint.Always
        );

        guiNode.attachChild(
                deathNode
        );


        layoutTexts();
    }


    public void show(
            int secondsRemaining,
            boolean bedRespawn
    ) {

        deathNode.setCullHint(
                Spatial.CullHint.Inherit
        );


        respawnText.setText(
                "Respawn in "
                        +
                        Math.max(
                                0,
                                secondsRemaining
                        )
                        +
                        "..."
        );


        locationText.setText(
                bedRespawn
                        ?
                        "Respawnpunkt: Bett"
                        :
                        "Respawnpunkt: Welt-Spawn"
        );


        layoutTexts();
    }


    public void hide() {

        deathNode.setCullHint(
                Spatial.CullHint.Always
        );
    }


    private void layoutTexts() {

        centerText(
                titleText,
                camera.getHeight() * 0.60f
        );

        centerText(
                respawnText,
                camera.getHeight() * 0.48f
        );

        centerText(
                locationText,
                camera.getHeight() * 0.41f
        );
    }


    private void centerText(
            BitmapText text,
            float y
    ) {

        text.setLocalTranslation(
                camera.getWidth() / 2f
                        -
                        text.getLineWidth() / 2f,
                y,
                5f
        );
    }
}
