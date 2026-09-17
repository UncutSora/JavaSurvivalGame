package org.example.survival;

import org.example.player.Player;

public class PlayerStats {

    private static final float MAX_HEALTH =
            100f;

    private static final float MAX_HUNGER =
            100f;

    private static final float MAX_THIRST =
            100f;

    private static final float MAX_STAMINA =
            100f;


    /*
     * Verbrauch pro Sekunde.
     */

    private static final float HUNGER_DRAIN =
            0.35f;

    private static final float THIRST_DRAIN =
            0.55f;


    /*
     * Ausdauer.
     */

    private static final float STAMINA_SPRINT_DRAIN =
            22f;

    private static final float STAMINA_REGEN =
            16f;


    /*
     * Nach vollständiger Erschöpfung
     * muss sich die Ausdauer erst wieder
     * bis hierhin regenerieren.
     */

    private static final float EXHAUSTION_RECOVERY =
            25f;


    /*
     * Schaden bei Hunger / Durst = 0.
     */

    private static final float STARVATION_DAMAGE =
            2f;

    private static final float DEHYDRATION_DAMAGE =
            4f;


    private final Player player;


    private float health =
            MAX_HEALTH;

    private float hunger =
            MAX_HUNGER;

    private float thirst =
            MAX_THIRST;

    private float stamina =
            MAX_STAMINA;


    private boolean sprintExhausted =
            false;


    public PlayerStats(
            Player player
    ) {

        this.player =
                player;
    }


    public void update(
            float tpf
    ) {

        /*
         * Verhindert extreme Sprünge,
         * falls das Spiel kurz hängt.
         */

        float delta =
                Math.min(
                        tpf,
                        0.1f
                );


        if (health <= 0f) {

            health =
                    0f;


            player.setCanSprint(
                    false
            );


            return;
        }


        // ==========================
        // HUNGER
        // ==========================

        hunger -=
                HUNGER_DRAIN
                        *
                        delta;


        hunger =
                clamp(
                        hunger,
                        0f,
                        MAX_HUNGER
                );


        // ==========================
        // DURST
        // ==========================

        thirst -=
                THIRST_DRAIN
                        *
                        delta;


        thirst =
                clamp(
                        thirst,
                        0f,
                        MAX_THIRST
                );


        // ==========================
        // AUSDAUER
        // ==========================

        if (
                player.isSprinting()
                        &&
                        !sprintExhausted
        ) {

            stamina -=
                    STAMINA_SPRINT_DRAIN
                            *
                            delta;
        }

        else {

            stamina +=
                    STAMINA_REGEN
                            *
                            delta;
        }


        stamina =
                clamp(
                        stamina,
                        0f,
                        MAX_STAMINA
                );


        // ==========================
        // ERSCHÖPFUNG
        // ==========================

        if (stamina <= 0f) {

            sprintExhausted =
                    true;
        }


        if (
                sprintExhausted
                        &&
                        stamina
                                >=
                                EXHAUSTION_RECOVERY
        ) {

            sprintExhausted =
                    false;
        }


        player.setCanSprint(
                !sprintExhausted
                        &&
                        stamina > 0f
        );


        // ==========================
        // HUNGER-SCHADEN
        // ==========================

        if (hunger <= 0f) {

            health -=
                    STARVATION_DAMAGE
                            *
                            delta;
        }


        // ==========================
        // DURST-SCHADEN
        // ==========================

        if (thirst <= 0f) {

            health -=
                    DEHYDRATION_DAMAGE
                            *
                            delta;
        }


        health =
                clamp(
                        health,
                        0f,
                        MAX_HEALTH
                );
    }


    // ==========================
    // SPÄTER FÜR ESSEN / TRINKEN
    // ==========================

    public void addHunger(
            float amount
    ) {

        hunger =
                clamp(
                        hunger + amount,
                        0f,
                        MAX_HUNGER
                );
    }


    public void addThirst(
            float amount
    ) {

        thirst =
                clamp(
                        thirst + amount,
                        0f,
                        MAX_THIRST
                );
    }


    public void heal(
            float amount
    ) {

        health =
                clamp(
                        health + amount,
                        0f,
                        MAX_HEALTH
                );
    }


    public void damage(
            float amount
    ) {

        health =
                clamp(
                        health - amount,
                        0f,
                        MAX_HEALTH
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


    public float getHealth() {

        return health;
    }


    public float getMaxHealth() {

        return MAX_HEALTH;
    }


    public float getHunger() {

        return hunger;
    }


    public float getMaxHunger() {

        return MAX_HUNGER;
    }


    public float getThirst() {

        return thirst;
    }


    public float getMaxThirst() {

        return MAX_THIRST;
    }


    public float getStamina() {

        return stamina;
    }


    public float getMaxStamina() {

        return MAX_STAMINA;
    }


    public boolean isSprintExhausted() {

        return sprintExhausted;
    }


    public boolean isDead() {

        return health <= 0f;
    }
}