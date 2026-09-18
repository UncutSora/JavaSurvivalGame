package org.example.world;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Sphere;

import org.example.inventory.ItemType;

import java.util.ArrayList;
import java.util.List;

public class BerryBush implements HarvestableResource {

    private final String saveId;

    private final Node bushNode;

    private final Geometry bush;


    private final List<Geometry> berries =
            new ArrayList<>();


    private final PhysicsSpace physicsSpace;

    private final RigidBodyControl bushPhysics;


    private Node worldParent;


    private boolean physicsActive =
            false;


    private final int maxHealth =
            20;


    private int health =
            maxHealth;


    private boolean harvested =
            false;


    public BerryBush(
            String saveId,
            AssetManager assetManager,
            PhysicsSpace physicsSpace,
            Vector3f position
    ) {

        this.saveId =
                saveId;


        this.physicsSpace =
                physicsSpace;


        bushNode =
                new Node(
                        "BerryBush_"
                                +
                                saveId
                );


        Sphere bushMesh =
                new Sphere(
                        12,
                        12,
                        0.7f
                );


        bush =
                new Geometry(
                        "BerryBushLeaves",
                        bushMesh
                );


        Material bushMaterial =
                new Material(
                        assetManager,
                        "Common/MatDefs/Light/Lighting.j3md"
                );


        bushMaterial.setBoolean(
                "UseMaterialColors",
                true
        );


        bushMaterial.setColor(
                "Diffuse",
                new ColorRGBA(
                        0.08f,
                        0.45f,
                        0.12f,
                        1f
                )
        );


        bushMaterial.setColor(
                "Ambient",
                new ColorRGBA(
                        0.08f,
                        0.45f,
                        0.12f,
                        1f
                )
        );


        bush.setMaterial(
                bushMaterial
        );


        bush.setLocalTranslation(
                position.x,
                position.y + 0.65f,
                position.z
        );


        bushNode.attachChild(
                bush
        );


        createBerry(
                assetManager,
                position.add(
                        0.35f,
                        0.75f,
                        0.45f
                )
        );


        createBerry(
                assetManager,
                position.add(
                        -0.30f,
                        0.85f,
                        0.50f
                )
        );


        createBerry(
                assetManager,
                position.add(
                        0.10f,
                        0.45f,
                        0.65f
                )
        );


        createBerry(
                assetManager,
                position.add(
                        -0.45f,
                        0.55f,
                        0.20f
                )
        );


        createBerry(
                assetManager,
                position.add(
                        0.45f,
                        0.50f,
                        0.10f
                )
        );


        bushPhysics =
                new RigidBodyControl(
                        0f
                );


        bush.addControl(
                bushPhysics
        );
    }


    private void createBerry(
            AssetManager assetManager,
            Vector3f position
    ) {

        Sphere berryMesh =
                new Sphere(
                        8,
                        8,
                        0.12f
                );


        Geometry berry =
                new Geometry(
                        "Berry",
                        berryMesh
                );


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
                new ColorRGBA(
                        0.8f,
                        0.05f,
                        0.08f,
                        1f
                )
        );


        material.setColor(
                "Ambient",
                new ColorRGBA(
                        0.8f,
                        0.05f,
                        0.08f,
                        1f
                )
        );


        berry.setMaterial(
                material
        );


        berry.setLocalTranslation(
                position
        );


        berries.add(
                berry
        );


        bushNode.attachChild(
                berry
        );
    }


    @Override
    public String getSaveId() {

        return saveId;
    }


    @Override
    public Node getNode() {

        return bushNode;
    }


    @Override
    public void attachToWorld(
            Node rootNode
    ) {

        worldParent =
                rootNode;


        if (
                harvested
        ) {

            return;
        }


        if (
                bushNode.getParent()
                        ==
                        null
        ) {

            rootNode.attachChild(
                    bushNode
            );
        }


        activatePhysics();
    }


    private void activatePhysics() {

        if (
                physicsActive
        ) {

            return;
        }


        physicsSpace.add(
                bushPhysics
        );


        physicsActive =
                true;
    }


    @Override
    public void detachFromWorld() {

        if (
                physicsActive
        ) {

            physicsSpace.remove(
                    bushPhysics
            );


            physicsActive =
                    false;
        }


        bushNode.removeFromParent();
    }


    @Override
    public boolean owns(
            Geometry geometry
    ) {

        return geometry == bush
                ||
                berries.contains(
                        geometry
                );
    }


    @Override
    public boolean takeDamage(
            int damage
    ) {

        if (
                harvested
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


            harvested =
                    true;


            detachFromWorld();


            return true;
        }


        return false;
    }


    @Override
    public boolean isHarvested() {

        return harvested;
    }


    @Override
    public int getHealth() {

        return health;
    }


    @Override
    public int getMaxHealth() {

        return maxHealth;
    }


    @Override
    public ItemType getItemType() {

        return ItemType.BERRIES;
    }


    @Override
    public int getYield() {

        return 3;
    }


    @Override
    public void loadState(
            int health,
            boolean harvested
    ) {

        this.health =
                Math.max(
                        0,
                        Math.min(
                                maxHealth,
                                health
                        )
                );


        this.harvested =
                harvested
                        ||
                        this.health <= 0;


        if (
                this.harvested
        ) {

            this.health =
                    0;


            detachFromWorld();


            return;
        }


        if (
                worldParent != null
                        &&
                        bushNode.getParent()
                                ==
                                null
        ) {

            worldParent.attachChild(
                    bushNode
            );
        }


        activatePhysics();
    }
}