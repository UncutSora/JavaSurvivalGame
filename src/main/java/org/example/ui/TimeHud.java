package org.example.ui;

import com.jme3.asset.AssetManager;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.math.ColorRGBA;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;

import org.example.time.DayNightSystem;

public class TimeHud {

    private final DayNightSystem
            dayNightSystem;


    private final BitmapText timeText;


    private int lastDisplayedMinute =
            -1;


    private int lastDisplayedDay =
            -1;


    public TimeHud(
            AssetManager assetManager,
            Node guiNode,
            Camera camera,
            DayNightSystem dayNightSystem
    ) {

        this.dayNightSystem =
                dayNightSystem;


        BitmapFont font =
                assetManager.loadFont(
                        "Interface/Fonts/Default.fnt"
                );


        timeText =
                new BitmapText(
                        font
                );


        timeText.setSize(
                20f
        );


        timeText.setColor(
                ColorRGBA.White
        );


        /*
         * Oben rechts.
         */
        timeText.setLocalTranslation(
                camera.getWidth() - 180f,
                camera.getHeight() - 25f,
                20f
        );


        guiNode.attachChild(
                timeText
        );


        update();
    }


    public void update() {

        int currentMinute =
                dayNightSystem.getMinute();


        int currentDay =
                dayNightSystem.getDay();


        /*
         * Text nur ändern,
         * wenn sich Minute oder Tag geändert haben.
         */
        if (
                currentMinute
                        ==
                        lastDisplayedMinute

                        &&

                        currentDay
                                ==
                                lastDisplayedDay
        ) {

            return;
        }


        lastDisplayedMinute =
                currentMinute;


        lastDisplayedDay =
                currentDay;


        timeText.setText(
                "TAG "
                        +
                        currentDay
                        +
                        "   "
                        +
                        dayNightSystem
                                .getFormattedTime()
        );
    }
}