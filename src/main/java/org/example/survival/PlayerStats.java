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


    private static final float HUNGER_DRAIN =
            0.35f;

    private static final float THIRST_DRAIN =
            0.55f;


    private static final float STAMINA_SPRINT_DRAIN =
            22f;

    private static final float STAMINA_REGEN =
            16f;


    private static final float EXHAUSTION_RECOVERY =
            25f;


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

        float delta =
                Math.min(
                        tpf,
                        0.1f
                );


        if (
                health <= 0f
        ) {

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

        if (
                stamina <= 0f
        ) {

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
        // HUNGERSCHADEN
        // ==========================

        if (
                hunger <= 0f
        ) {

            health -=
                    STARVATION_DAMAGE
                            *
                            delta;
        }


        // ==========================
        // DURSTSCHADEN
        // ==========================

        if (
                thirst <= 0f
        ) {

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
    // SAVE / LOAD
    // ==========================

    public void loadState(
            float health,
            float hunger,
            float thirst,
            float stamina,
            boolean sprintExhausted
    ) {

        this.health =
                clamp(
                        health,
                        0f,
                        MAX_HEALTH
                );


        this.hunger =
                clamp(
                        hunger,
                        0f,
                        MAX_HUNGER
                );


        this.thirst =
                clamp(
                        thirst,
                        0f,
                        MAX_THIRST
                );


        this.stamina =
                clamp(
                        stamina,
                        0f,
                        MAX_STAMINA
                );


        this.sprintExhausted =
                sprintExhausted;


        if (
                this.health <= 0f
        ) {

            player.setCanSprint(
                    false
            );

            return;
        }


        player.setCanSprint(
                !this.sprintExhausted
                        &&
                        this.stamina > 0f
        );
    }


    // ==========================
    // ESSEN / TRINKEN
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


    public void respawn() {

        health =
                MAX_HEALTH;

        hunger =
                MAX_HUNGER;

        thirst =
                MAX_THIRST;

        stamina =
                MAX_STAMINA;

        sprintExhausted =
                false;

        player.setCanSprint(
                true
        );
    }


    public boolean isDead() {

        return health <= 0f;
    }
}