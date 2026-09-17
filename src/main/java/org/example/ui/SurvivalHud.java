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
import org.example.survival.PlayerStats;

public class SurvivalHud {

    private static final float BAR_WIDTH =
            220f;

    private static final float BAR_HEIGHT =
            16f;

    private static final float INNER_WIDTH =
            216f;

    private static final float INNER_HEIGHT =
            12f;


    private final PlayerStats playerStats;


    private final Geometry healthFill;

    private final Geometry hungerFill;

    private final Geometry thirstFill;

    private final Geometry staminaFill;


    private final BitmapText healthText;

    private final BitmapText hungerText;

    private final BitmapText thirstText;

    private final BitmapText staminaText;


    public SurvivalHud(
            AssetManager assetManager,
            Node guiNode,
            Camera camera,
            PlayerStats playerStats
    ) {

        this.playerStats =
                playerStats;


        BitmapFont font =
                assetManager.loadFont(
                        "Interface/Fonts/Default.fnt"
                );


        float x =
                25f;


        float startY =
                camera.getHeight()
                        -
                        65f;


        // ==========================
        // LEBEN
        // ==========================

        healthFill =
                createBar(
                        assetManager,
                        guiNode,
                        x,
                        startY,
                        new ColorRGBA(
                                0.85f,
                                0.15f,
                                0.15f,
                                1f
                        )
                );


        healthText =
                createText(
                        guiNode,
                        font,
                        x,
                        startY + 31f
                );


        // ==========================
        // HUNGER
        // ==========================

        float hungerY =
                startY - 48f;


        hungerFill =
                createBar(
                        assetManager,
                        guiNode,
                        x,
                        hungerY,
                        new ColorRGBA(
                                0.9f,
                                0.55f,
                                0.12f,
                                1f
                        )
                );


        hungerText =
                createText(
                        guiNode,
                        font,
                        x,
                        hungerY + 31f
                );


        // ==========================
        // DURST
        // ==========================

        float thirstY =
                startY - 96f;


        thirstFill =
                createBar(
                        assetManager,
                        guiNode,
                        x,
                        thirstY,
                        new ColorRGBA(
                                0.15f,
                                0.55f,
                                0.95f,
                                1f
                        )
                );


        thirstText =
                createText(
                        guiNode,
                        font,
                        x,
                        thirstY + 31f
                );


        // ==========================
        // AUSDAUER
        // ==========================

        float staminaY =
                startY - 144f;


        staminaFill =
                createBar(
                        assetManager,
                        guiNode,
                        x,
                        staminaY,
                        new ColorRGBA(
                                0.2f,
                                0.8f,
                                0.3f,
                                1f
                        )
                );


        staminaText =
                createText(
                        guiNode,
                        font,
                        x,
                        staminaY + 31f
                );


        update();
    }


    private Geometry createBar(
            AssetManager assetManager,
            Node guiNode,
            float x,
            float y,
            ColorRGBA fillColor
    ) {

        // ==========================
        // HINTERGRUND
        // ==========================

        Geometry background =
                new Geometry(
                        "StatBarBackground",
                        new Quad(
                                BAR_WIDTH,
                                BAR_HEIGHT
                        )
                );


        background.setMaterial(
                createMaterial(
                        assetManager,
                        new ColorRGBA(
                                0.05f,
                                0.05f,
                                0.05f,
                                0.8f
                        )
                )
        );


        background.setQueueBucket(
                RenderQueue.Bucket.Gui
        );


        background.setLocalTranslation(
                x,
                y,
                1f
        );


        guiNode.attachChild(
                background
        );


        // ==========================
        // FÜLLUNG
        // ==========================

        Geometry fill =
                new Geometry(
                        "StatBarFill",
                        new Quad(
                                INNER_WIDTH,
                                INNER_HEIGHT
                        )
                );


        fill.setMaterial(
                createMaterial(
                        assetManager,
                        fillColor
                )
        );


        fill.setQueueBucket(
                RenderQueue.Bucket.Gui
        );


        fill.setLocalTranslation(
                x + 2f,
                y + 2f,
                2f
        );


        guiNode.attachChild(
                fill
        );


        return fill;
    }


    private BitmapText createText(
            Node guiNode,
            BitmapFont font,
            float x,
            float y
    ) {

        BitmapText text =
                new BitmapText(
                        font
                );


        text.setSize(
                16f
        );


        text.setColor(
                ColorRGBA.White
        );


        text.setLocalTranslation(
                x,
                y,
                4f
        );


        guiNode.attachChild(
                text
        );


        return text;
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

        float healthPercent =
                playerStats.getHealth()
                        /
                        playerStats.getMaxHealth();


        float hungerPercent =
                playerStats.getHunger()
                        /
                        playerStats.getMaxHunger();


        float thirstPercent =
                playerStats.getThirst()
                        /
                        playerStats.getMaxThirst();


        float staminaPercent =
                playerStats.getStamina()
                        /
                        playerStats.getMaxStamina();


        healthFill.setLocalScale(
                healthPercent,
                1f,
                1f
        );


        hungerFill.setLocalScale(
                hungerPercent,
                1f,
                1f
        );


        thirstFill.setLocalScale(
                thirstPercent,
                1f,
                1f
        );


        staminaFill.setLocalScale(
                staminaPercent,
                1f,
                1f
        );


        healthText.setText(
                "LEBEN   "
                        +
                        Math.round(
                                playerStats.getHealth()
                        )
                        +
                        " / "
                        +
                        Math.round(
                                playerStats.getMaxHealth()
                        )
        );


        hungerText.setText(
                "HUNGER  "
                        +
                        Math.round(
                                playerStats.getHunger()
                        )
                        +
                        " / "
                        +
                        Math.round(
                                playerStats.getMaxHunger()
                        )
        );


        thirstText.setText(
                "DURST   "
                        +
                        Math.round(
                                playerStats.getThirst()
                        )
                        +
                        " / "
                        +
                        Math.round(
                                playerStats.getMaxThirst()
                        )
        );


        if (
                playerStats.isSprintExhausted()
        ) {

            staminaText.setText(
                    "AUSDAUER  "
                            +
                            Math.round(
                                    playerStats.getStamina()
                            )
                            +
                            " / "
                            +
                            Math.round(
                                    playerStats.getMaxStamina()
                            )
                            +
                            "  ERSCHÖPFT"
            );
        }

        else {

            staminaText.setText(
                    "AUSDAUER  "
                            +
                            Math.round(
                                    playerStats.getStamina()
                            )
                            +
                            " / "
                            +
                            Math.round(
                                    playerStats.getMaxStamina()
                            )
            );
        }
    }
}