package org.example.survival;

import com.jme3.input.FlyByCamera;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.math.Vector3f;

import org.example.building.BuildingSystem;
import org.example.player.Player;
import org.example.ui.DeathScreenHud;
import org.example.ui.InventoryMenuSystem;

public class RespawnSystem implements ActionListener {

    private static final String SET_RESPAWN_POINT =
            "SetRespawnPoint";

    private static final float RESPAWN_DELAY_SECONDS =
            3f;

    private static final Vector3f DEFAULT_RESPAWN_POSITION =
            new Vector3f(
                    0f,
                    2f,
                    8f
            );


    private final Player player;

    private final PlayerStats playerStats;

    private final BuildingSystem buildingSystem;

    private final InventoryMenuSystem inventoryMenuSystem;

    private final FlyByCamera flyCam;

    private final DeathScreenHud deathScreenHud;


    private Vector3f respawnBedPosition;

    private boolean deathSequenceActive =
            false;

    private float deathTimer =
            0f;


    public RespawnSystem(
            InputManager inputManager,
            Player player,
            PlayerStats playerStats,
            BuildingSystem buildingSystem,
            InventoryMenuSystem inventoryMenuSystem,
            FlyByCamera flyCam,
            DeathScreenHud deathScreenHud
    ) {

        this.player =
                player;

        this.playerStats =
                playerStats;

        this.buildingSystem =
                buildingSystem;

        this.inventoryMenuSystem =
                inventoryMenuSystem;

        this.flyCam =
                flyCam;

        this.deathScreenHud =
                deathScreenHud;


        inputManager.addMapping(
                SET_RESPAWN_POINT,
                new KeyTrigger(
                        KeyInput.KEY_E
                )
        );


        inputManager.addListener(
                this,
                SET_RESPAWN_POINT
        );
    }


    @Override
    public void onAction(
            String name,
            boolean isPressed,
            float tpf
    ) {

        if (
                !isPressed
                        ||
                        !name.equals(
                                SET_RESPAWN_POINT
                        )
        ) {

            return;
        }


        if (
                deathSequenceActive
                        ||
                        playerStats.isDead()
        ) {

            return;
        }


        if (
                inventoryMenuSystem.isOpen()
                        ||
                        buildingSystem.isActive()
        ) {

            return;
        }


        Vector3f targetedBedPosition =
                buildingSystem.getTargetedBedPosition();


        if (
                targetedBedPosition == null
        ) {

            return;
        }


        respawnBedPosition =
                targetedBedPosition.clone();


        System.out.println(
                "Respawnpunkt am Bett gesetzt."
        );
    }


    public void update(
            float tpf
    ) {

        validateRespawnBed();


        if (
                !playerStats.isDead()
        ) {

            if (
                    deathSequenceActive
            ) {

                cancelDeathSequence();
            }

            return;
        }


        if (
                !deathSequenceActive
        ) {

            startDeathSequence();
        }


        deathTimer +=
                Math.min(
                        tpf,
                        0.1f
                );


        float remaining =
                Math.max(
                        0f,
                        RESPAWN_DELAY_SECONDS
                                -
                                deathTimer
                );


        int secondsRemaining =
                (int) Math.ceil(
                        remaining
                );


        deathScreenHud.show(
                secondsRemaining,
                hasRespawnBed()
        );


        if (
                deathTimer
                        >=
                        RESPAWN_DELAY_SECONDS
        ) {

            respawnPlayer();
        }
    }


    private void startDeathSequence() {

        deathSequenceActive =
                true;

        deathTimer =
                0f;


        player.setInputEnabled(
                false
        );

        flyCam.setEnabled(
                false
        );

        buildingSystem.setActive(
                false
        );


        deathScreenHud.show(
                (int) RESPAWN_DELAY_SECONDS,
                hasRespawnBed()
        );


        System.out.println(
                "Du bist gestorben. Respawn in 3 Sekunden."
        );
    }


    private void respawnPlayer() {

        Vector3f respawnPosition =
                getCurrentRespawnPosition();

        boolean usingBed =
                hasRespawnBed();


        player.getCharacter()
                .setPhysicsLocation(
                        respawnPosition
                );


        playerStats.respawn();


        deathScreenHud.hide();

        deathSequenceActive =
                false;

        deathTimer =
                0f;


        if (
                inventoryMenuSystem.isOpen()
        ) {

            player.setInputEnabled(
                    false
            );

            flyCam.setEnabled(
                    false
            );
        }

        else {

            player.setInputEnabled(
                    true
            );

            flyCam.setEnabled(
                    true
            );
        }


        System.out.println(
                usingBed
                        ?
                        "Du bist an deinem Bett respawnt."
                        :
                        "Du bist am Welt-Spawn respawnt."
        );
    }


    private void cancelDeathSequence() {

        deathScreenHud.hide();

        deathSequenceActive =
                false;

        deathTimer =
                0f;
    }


    private void validateRespawnBed() {

        if (
                respawnBedPosition == null
        ) {

            return;
        }


        if (
                buildingSystem.hasBedAt(
                        respawnBedPosition
                )
        ) {

            return;
        }


        respawnBedPosition =
                null;


        System.out.println(
                "Respawnbett existiert nicht mehr. Welt-Spawn ist wieder aktiv."
        );
    }


    private Vector3f getCurrentRespawnPosition() {

        if (
                hasRespawnBed()
        ) {

            Vector3f position =
                    respawnBedPosition.clone();

            position.y +=
                    2f;

            return position;
        }


        return DEFAULT_RESPAWN_POSITION.clone();
    }


    public boolean hasRespawnBed() {

        return respawnBedPosition != null
                &&
                buildingSystem.hasBedAt(
                        respawnBedPosition
                );
    }


    public boolean isDeathSequenceActive() {

        return deathSequenceActive;
    }


    public Vector3f getRespawnBedPosition() {

        if (
                !hasRespawnBed()
        ) {

            return null;
        }


        return respawnBedPosition.clone();
    }


    public void loadRespawnBedPosition(
            Vector3f position
    ) {

        if (
                position != null
                        &&
                        buildingSystem.hasBedAt(
                                position
                        )
        ) {

            respawnBedPosition =
                    position.clone();
        }

        else {

            respawnBedPosition =
                    null;
        }
    }


    public void clearRespawnBed() {

        respawnBedPosition =
                null;
    }
}
