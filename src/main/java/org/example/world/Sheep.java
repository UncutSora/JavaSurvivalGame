package org.example.world;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;

import java.util.Random;

public class Sheep {

    private static final int MAX_HEALTH = 30;

    private static final float WALK_SPEED = 0.55f;

    private static final float CHANGE_DIRECTION_MIN_SECONDS = 2.5f;

    private static final float CHANGE_DIRECTION_MAX_SECONDS = 6.0f;

    private static final float WORLD_HALF_SIZE = 500f;

    private static final float WORLD_MARGIN = 10f;

    private final String saveId;

    private final Node node;

    private final Random random;

    private final Vector3f moveDirection = new Vector3f();

    private int health = MAX_HEALTH;

    private boolean dead = false;

    private float directionTimer = 0f;

    public Sheep(
            String saveId,
            AssetManager assetManager,
            Vector3f position,
            long randomSeed
    ) {

        this.saveId = saveId;

        this.random = new Random(randomSeed);

        node = new Node(
                "Sheep_" + saveId
        );

        createVisual(
                assetManager
        );

        node.setLocalTranslation(
                position
        );

        chooseNewDirection();
    }


    private void createVisual(
            AssetManager assetManager
    ) {

        Material woolMaterial =
                createMaterial(
                        assetManager,
                        new ColorRGBA(
                                0.90f,
                                0.90f,
                                0.86f,
                                1f
                        )
                );

        Material skinMaterial =
                createMaterial(
                        assetManager,
                        new ColorRGBA(
                                0.18f,
                                0.16f,
                                0.14f,
                                1f
                        )
                );

        Material hoofMaterial =
                createMaterial(
                        assetManager,
                        new ColorRGBA(
                                0.08f,
                                0.07f,
                                0.06f,
                                1f
                        )
                );

        Geometry body =
                new Geometry(
                        "SheepBody",
                        new Box(
                                0.70f,
                                0.55f,
                                1.00f
                        )
                );

        body.setMaterial(
                woolMaterial
        );

        body.setLocalTranslation(
                0f,
                1.00f,
                0f
        );

        node.attachChild(
                body
        );


        Geometry head =
                new Geometry(
                        "SheepHead",
                        new Box(
                                0.38f,
                                0.38f,
                                0.42f
                        )
                );

        head.setMaterial(
                skinMaterial
        );

        head.setLocalTranslation(
                0f,
                1.05f,
                1.25f
        );

        node.attachChild(
                head
        );


        Geometry muzzle =
                new Geometry(
                        "SheepMuzzle",
                        new Box(
                                0.29f,
                                0.20f,
                                0.24f
                        )
                );

        muzzle.setMaterial(
                createMaterial(
                        assetManager,
                        new ColorRGBA(
                                0.48f,
                                0.39f,
                                0.32f,
                                1f
                        )
                )
        );

        muzzle.setLocalTranslation(
                0f,
                0.90f,
                1.62f
        );

        node.attachChild(
                muzzle
        );


        createLeg(
                assetManager,
                hoofMaterial,
                -0.48f,
                0.42f,
                0.62f
        );

        createLeg(
                assetManager,
                hoofMaterial,
                0.48f,
                0.42f,
                0.62f
        );

        createLeg(
                assetManager,
                hoofMaterial,
                -0.48f,
                0.42f,
                -0.62f
        );

        createLeg(
                assetManager,
                hoofMaterial,
                0.48f,
                0.42f,
                -0.62f
        );
    }


    private void createLeg(
            AssetManager assetManager,
            Material hoofMaterial,
            float x,
            float y,
            float z
    ) {

        Geometry leg =
                new Geometry(
                        "SheepLeg",
                        new Box(
                                0.12f,
                                0.42f,
                                0.12f
                        )
                );

        leg.setMaterial(
                hoofMaterial
        );

        leg.setLocalTranslation(
                x,
                y,
                z
        );

        node.attachChild(
                leg
        );
    }


    private Material createMaterial(
            AssetManager assetManager,
            ColorRGBA color
    ) {

        Material material =
                new Material(
                        assetManager,
                        "Common/MatDefs/Light/Lighting.j3md"
                );

        material.setBoolean(
                "UseMaterialColors",
                true
        );

        material.setColor(
                "Diffuse",
                color
        );

        material.setColor(
                "Ambient",
                color
        );

        return material;
    }


    public void attachToWorld(
            Node rootNode
    ) {

        rootNode.attachChild(
                node
        );
    }


    public void update(
            float tpf
    ) {

        if (
                dead
        ) {

            return;
        }

        float delta =
                Math.min(
                        tpf,
                        0.1f
                );

        directionTimer -=
                delta;

        if (
                directionTimer <= 0f
        ) {

            chooseNewDirection();
        }

        if (
                moveDirection.lengthSquared() > 0.001f
        ) {

            Vector3f movement =
                    moveDirection.mult(
                            WALK_SPEED * delta
                    );

            Vector3f nextPosition =
                    node.getLocalTranslation()
                            .add(
                                    movement
                            );

            if (
                    Math.abs(nextPosition.x)
                            >=
                            WORLD_HALF_SIZE - WORLD_MARGIN
                            ||
                            Math.abs(nextPosition.z)
                                    >=
                                    WORLD_HALF_SIZE - WORLD_MARGIN
            ) {

                moveDirection.negateLocal();

                nextPosition =
                        node.getLocalTranslation()
                                .add(
                                        moveDirection.mult(
                                                WALK_SPEED * delta
                                        )
                                );
            }

            nextPosition.y =
                    0f;

            node.setLocalTranslation(
                    nextPosition
            );

            float yaw =
                    (float) Math.atan2(
                            moveDirection.x,
                            moveDirection.z
                    );

            Quaternion rotation =
                    new Quaternion();

            rotation.fromAngles(
                    0f,
                    yaw,
                    0f
            );

            node.setLocalRotation(
                    rotation
            );
        }
    }


    private void chooseNewDirection() {

        directionTimer =
                CHANGE_DIRECTION_MIN_SECONDS
                        +
                        random.nextFloat()
                                *
                                (
                                        CHANGE_DIRECTION_MAX_SECONDS
                                                -
                                                CHANGE_DIRECTION_MIN_SECONDS
                                );

        if (
                random.nextFloat() < 0.22f
        ) {

            moveDirection.set(
                    0f,
                    0f,
                    0f
            );

            return;
        }

        float angle =
                random.nextFloat()
                        *
                        (float) Math.PI
                        *
                        2f;

        moveDirection.set(
                (float) Math.sin(angle),
                0f,
                (float) Math.cos(angle)
        );

        moveDirection.normalizeLocal();
    }


    public boolean owns(
            Geometry geometry
    ) {

        if (
                geometry == null
        ) {

            return false;
        }

        Spatial current =
                geometry;

        while (
                current != null
        ) {

            if (
                    current == node
            ) {

                return true;
            }

            current =
                    current.getParent();
        }

        return false;
    }


    public boolean takeDamage(
            int damage
    ) {

        if (
                dead
                        ||
                        damage <= 0
        ) {

            return false;
        }

        health -=
                damage;

        if (
                health <= 0
        ) {

            health =
                    0;

            dead =
                    true;

            node.removeFromParent();

            return true;
        }

        return false;
    }


    public Node getNode() {

        return node;
    }


    public int getHealth() {

        return health;
    }


    public int getMaxHealth() {

        return MAX_HEALTH;
    }


    public boolean isDead() {

        return dead;
    }


    public String getSaveId() {

        return saveId;
    }
}
