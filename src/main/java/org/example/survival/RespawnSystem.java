package org.example.survival;

import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.math.Vector3f;

import org.example.building.BuildingSystem;
import org.example.player.Player;
import org.example.ui.InventoryMenuSystem;

public class RespawnSystem implements ActionListener {

    private static final String SET_RESPAWN_POINT =
            "SetRespawnPoint";

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


    private Vector3f respawnBedPosition;

    private boolean deathHandled =
            false;


    public RespawnSystem(
            InputManager inputManager,
            Player player,
            PlayerStats playerStats,
            BuildingSystem buildingSystem,
            InventoryMenuSystem inventoryMenuSystem
    ) {

        this.player =
                player;

        this.playerStats =
                playerStats;

        this.buildingSystem =
                buildingSystem;

        this.inventoryMenuSystem =
                inventoryMenuSystem;


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


    public void update() {

        validateRespawnBed();


        if (
                !playerStats.isDead()
        ) {

            deathHandled =
                    false;

            return;
        }


        if (
                deathHandled
        ) {

            return;
        }


        deathHandled =
                true;


        Vector3f respawnPosition =
                getCurrentRespawnPosition();


        player.getCharacter()
                .setPhysicsLocation(
                        respawnPosition
                );


        playerStats.respawn();


        System.out.println(
                hasRespawnBed()
                        ?
                        "Du bist an deinem Bett respawnt."
                        :
                        "Du bist am Welt-Spawn respawnt."
        );
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
