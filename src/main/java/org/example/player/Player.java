package org.example.player;

import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;

public class Player implements ActionListener {

    private static final float WALK_SPEED =
            0.12f;

    private static final float SPRINT_SPEED =
            0.20f;


    private final CharacterControl character;

    private final Camera camera;

    private final InputManager inputManager;

    private final Vector3f walkDirection =
            new Vector3f();


    private boolean forward;

    private boolean backward;

    private boolean left;

    private boolean right;

    private boolean sprintKeyPressed;

    private boolean canSprint =
            true;

    private boolean moving;


    public Player(
            Camera camera,
            InputManager inputManager,
            PhysicsSpace physicsSpace
    ) {

        this.camera =
                camera;

        this.inputManager =
                inputManager;


        CapsuleCollisionShape playerShape =
                new CapsuleCollisionShape(
                        0.45f,
                        1.2f,
                        1
                );


        character =
                new CharacterControl(
                        playerShape,
                        0.05f
                );


        character.setJumpSpeed(
                8f
        );


        character.setFallSpeed(
                20f
        );


        character.setGravity(
                20f
        );


        character.setPhysicsLocation(
                new Vector3f(
                        0,
                        2f,
                        8f
                )
        );


        physicsSpace.add(
                character
        );


        setupControls();
    }


    private void setupControls() {

        inputManager.addMapping(
                "PlayerForward",
                new KeyTrigger(
                        KeyInput.KEY_W
                )
        );


        inputManager.addMapping(
                "PlayerBackward",
                new KeyTrigger(
                        KeyInput.KEY_S
                )
        );


        inputManager.addMapping(
                "PlayerLeft",
                new KeyTrigger(
                        KeyInput.KEY_A
                )
        );


        inputManager.addMapping(
                "PlayerRight",
                new KeyTrigger(
                        KeyInput.KEY_D
                )
        );


        inputManager.addMapping(
                "PlayerJump",
                new KeyTrigger(
                        KeyInput.KEY_SPACE
                )
        );


        inputManager.addMapping(
                "PlayerSprint",
                new KeyTrigger(
                        KeyInput.KEY_LSHIFT
                )
        );


        inputManager.addListener(
                this,
                "PlayerForward",
                "PlayerBackward",
                "PlayerLeft",
                "PlayerRight",
                "PlayerJump",
                "PlayerSprint"
        );
    }


    @Override
    public void onAction(
            String name,
            boolean isPressed,
            float tpf
    ) {

        switch (name) {

            case "PlayerForward":

                forward =
                        isPressed;

                break;


            case "PlayerBackward":

                backward =
                        isPressed;

                break;


            case "PlayerLeft":

                left =
                        isPressed;

                break;


            case "PlayerRight":

                right =
                        isPressed;

                break;


            case "PlayerSprint":

                sprintKeyPressed =
                        isPressed;

                break;


            case "PlayerJump":

                if (isPressed) {

                    character.jump();
                }

                break;
        }
    }


    public void update(
            float tpf
    ) {

        // ==========================
        // KAMERA-VORWÄRTSRICHTUNG
        // ==========================

        Vector3f cameraForward =
                camera
                        .getDirection()
                        .clone();


        cameraForward.y =
                0f;


        if (
                cameraForward.lengthSquared()
                        >
                        0f
        ) {

            cameraForward.normalizeLocal();
        }


        // ==========================
        // KAMERA-SEITENRICHTUNG
        // ==========================

        Vector3f cameraLeft =
                camera
                        .getLeft()
                        .clone();


        cameraLeft.y =
                0f;


        if (
                cameraLeft.lengthSquared()
                        >
                        0f
        ) {

            cameraLeft.normalizeLocal();
        }


        // ==========================
        // BEWEGUNG ZURÜCKSETZEN
        // ==========================

        walkDirection.set(
                0f,
                0f,
                0f
        );


        if (forward) {

            walkDirection.addLocal(
                    cameraForward
            );
        }


        if (backward) {

            walkDirection.subtractLocal(
                    cameraForward
            );
        }


        if (left) {

            walkDirection.addLocal(
                    cameraLeft
            );
        }


        if (right) {

            walkDirection.subtractLocal(
                    cameraLeft
            );
        }


        moving =
                walkDirection.lengthSquared()
                        >
                        0.001f;


        // ==========================
        // GESCHWINDIGKEIT
        // ==========================

        if (moving) {

            walkDirection.normalizeLocal();


            float currentSpeed;


            if (isSprinting()) {

                currentSpeed =
                        SPRINT_SPEED;
            }

            else {

                currentSpeed =
                        WALK_SPEED;
            }


            walkDirection.multLocal(
                    currentSpeed
            );
        }


        character.setWalkDirection(
                walkDirection
        );


        // ==========================
        // KAMERA FOLGT SPIELER
        // ==========================

        Vector3f playerPosition =
                character
                        .getPhysicsLocation();


        camera.setLocation(
                playerPosition.add(
                        0f,
                        0.65f,
                        0f
                )
        );
    }


    public boolean isMoving() {

        return moving;
    }


    public boolean isSprinting() {

        return sprintKeyPressed
                &&
                canSprint
                &&
                moving;
    }


    public void setCanSprint(
            boolean canSprint
    ) {

        this.canSprint =
                canSprint;
    }


    public Vector3f getPosition() {

        return character
                .getPhysicsLocation()
                .clone();
    }


    public CharacterControl getCharacter() {

        return character;
    }
}