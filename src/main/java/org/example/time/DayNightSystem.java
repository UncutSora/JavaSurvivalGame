package org.example.time;

import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;

public class DayNightSystem {

    /*
     * 600 echte Sekunden = 1 kompletter Spieltag.
     *
     * Also:
     * 10 echte Minuten = 24 Stunden im Spiel.
     *
     * Später können wir diesen Wert jederzeit ändern.
     */
    private static final float REAL_SECONDS_PER_GAME_DAY =
            600f;

    private static final float GAME_MINUTES_PER_DAY =
            1440f;

    private static final float GAME_MINUTES_PER_REAL_SECOND =
            GAME_MINUTES_PER_DAY
                    /
                    REAL_SECONDS_PER_GAME_DAY;


    private final DirectionalLight sun;

    private final AmbientLight ambientLight;

    private final ViewPort viewPort;


    /*
     * Spiel beginnt morgens um 08:00.
     */
    private float minuteOfDay =
            8f * 60f;


    private int day =
            1;


    public DayNightSystem(
            Node rootNode,
            ViewPort viewPort
    ) {

        this.viewPort =
                viewPort;


        // ==========================
        // SONNE
        // ==========================

        sun =
                new DirectionalLight();


        rootNode.addLight(
                sun
        );


        // ==========================
        // UMGEBUNGSLICHT
        // ==========================

        ambientLight =
                new AmbientLight();


        rootNode.addLight(
                ambientLight
        );


        applyLighting();
    }


    public void update(
            float tpf
    ) {

        /*
         * Schutz gegen sehr große Frames,
         * z.B. wenn das Spiel kurz hängt.
         */
        float safeTpf =
                Math.min(
                        tpf,
                        0.1f
                );


        minuteOfDay +=
                safeTpf
                        *
                        GAME_MINUTES_PER_REAL_SECOND;


        while (
                minuteOfDay >= GAME_MINUTES_PER_DAY
        ) {

            minuteOfDay -=
                    GAME_MINUTES_PER_DAY;


            day++;
        }


        applyLighting();
    }


    private void applyLighting() {

        /*
         * Wir verschieben den Winkel so,
         * dass:
         *
         * 06:00 = Sonnenaufgang
         * 12:00 = Sonne oben
         * 18:00 = Sonnenuntergang
         * 00:00 = Nacht
         */

        float angle =
                (minuteOfDay - 360f)
                        /
                        GAME_MINUTES_PER_DAY
                        *
                        ((float) Math.PI * 2f);


        float sunHeight =
                (float) Math.sin(
                        angle
                );


        /*
         * DirectionalLight zeigt in die Richtung,
         * in die das Licht strahlt.
         */
        Vector3f sunDirection =
                new Vector3f(
                        (float) Math.cos(angle),
                        -sunHeight,
                        0.35f
                );


        sunDirection.normalizeLocal();


        sun.setDirection(
                sunDirection
        );


        /*
         * dayFactor:
         *
         * 0 = Nacht
         * 1 = heller Tag
         *
         * Durch den Offset entstehen weichere
         * Sonnenauf- und Untergänge.
         */
        float dayFactor =
                clamp(
                        (sunHeight + 0.15f)
                                /
                                0.45f,
                        0f,
                        1f
                );


        // ==========================
        // SONNENLICHT
        // ==========================

        float sunBrightness =
                0.05f
                        +
                        dayFactor * 0.95f;


        sun.setColor(
                new ColorRGBA(
                        1.0f * sunBrightness,
                        0.95f * sunBrightness,
                        0.82f * sunBrightness,
                        1f
                )
        );


        // ==========================
        // UMGEBUNGSLICHT
        // ==========================

        float ambientBrightness =
                0.08f
                        +
                        dayFactor * 0.37f;


        ambientLight.setColor(
                new ColorRGBA(
                        ambientBrightness,
                        ambientBrightness,
                        ambientBrightness * 1.08f,
                        1f
                )
        );


        // ==========================
        // HIMMEL
        // ==========================

        ColorRGBA nightSky =
                new ColorRGBA(
                        0.015f,
                        0.025f,
                        0.08f,
                        1f
                );


        ColorRGBA daySky =
                new ColorRGBA(
                        0.50f,
                        0.75f,
                        1.0f,
                        1f
                );


        viewPort.setBackgroundColor(
                blend(
                        nightSky,
                        daySky,
                        dayFactor
                )
        );
    }


    private ColorRGBA blend(
            ColorRGBA from,
            ColorRGBA to,
            float amount
    ) {

        float value =
                clamp(
                        amount,
                        0f,
                        1f
                );


        return new ColorRGBA(
                from.r
                        +
                        (to.r - from.r)
                                *
                                value,

                from.g
                        +
                        (to.g - from.g)
                                *
                                value,

                from.b
                        +
                        (to.b - from.b)
                                *
                                value,

                1f
        );
    }


    private float clamp(
            float value,
            float min,
            float max
    ) {

        return Math.max(
                min,
                Math.min(
                        max,
                        value
                )
        );
    }


    public int getDay() {

        return day;
    }


    public int getHour() {

        return (int) (
                minuteOfDay
                        /
                        60f
        );
    }


    public int getMinute() {

        return (int) (
                minuteOfDay
                        %
                        60f
        );
    }


    public float getMinuteOfDay() {

        return minuteOfDay;
    }


    public String getFormattedTime() {

        return String.format(
                "%02d:%02d",
                getHour(),
                getMinute()
        );
    }


    /*
     * Das brauchen wir im nächsten Schritt
     * für F5 / F9.
     */
    public void loadState(
            int day,
            float minuteOfDay
    ) {

        this.day =
                Math.max(
                        1,
                        day
                );


        this.minuteOfDay =
                minuteOfDay;


        while (
                this.minuteOfDay
                        >=
                        GAME_MINUTES_PER_DAY
        ) {

            this.minuteOfDay -=
                    GAME_MINUTES_PER_DAY;
        }


        while (
                this.minuteOfDay < 0f
        ) {

            this.minuteOfDay +=
                    GAME_MINUTES_PER_DAY;
        }


        applyLighting();
    }
}