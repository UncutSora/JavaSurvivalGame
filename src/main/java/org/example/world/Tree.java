package org.example.world;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Sphere;

import org.example.inventory.ItemType;

public class Tree implements HarvestableResource {

    private final String saveId;

    private final Node treeNode;

    private final Geometry trunk;

    private final Geometry leaves;

    private final PhysicsSpace physicsSpace;

    private final RigidBodyControl trunkPhysics;

    private Node worldParent;

    private boolean physicsActive =
            false;

    private final int maxHealth =
            100;

    private int health =
            maxHealth;

    private boolean harvested =
            false;


    public Tree(
            String saveId,
            AssetManager assetManager,
            PhysicsSpace physicsSpace,
            Vector3f position
    ) {

        this.saveId =
                saveId;

        this.physicsSpace =
                physicsSpace;


        treeNode =
                new Node(
                        "Tree_" + saveId
                );


        // ==========================
        // STAMM
        // ==========================

        Box trunkMesh =
                new Box(
                        0.35f,
                        1.5f,
                        0.35f
                );


        trunk =
                new Geometry(
                        "TreeTrunk",
                        trunkMesh
                );


        Material trunkMaterial =
                new Material(
                        assetManager,
                        "Common/MatDefs/Light/Lighting.j3md"
                );


        trunkMaterial.setBoolean(
                "UseMaterialColors",
                true
        );


        trunkMaterial.setColor(
                "Diffuse",
                new ColorRGBA(
                        0.45f,
                        0.28f,
                        0.12f,
                        1f
                )
        );


        trunkMaterial.setColor(
                "Ambient",
                new ColorRGBA(
                        0.45f,
                        0.28f,
                        0.12f,
                        1f
                )
        );


        trunk.setMaterial(
                trunkMaterial
        );


        trunk.setLocalTranslation(
                position.x,
                position.y + 1.5f,
                position.z
        );


        treeNode.attachChild(
                trunk
        );


        // ==========================
        // BAUMKRONE
        // ==========================

        Sphere leavesMesh =
                new Sphere(
                        16,
                        16,
                        1.3f
                );


        leaves =
                new Geometry(
                        "TreeLeaves",
                        leavesMesh
                );


        Material leavesMaterial =
                new Material(
                        assetManager,
                        "Common/MatDefs/Light/Lighting.j3md"
                );


        leavesMaterial.setBoolean(
                "UseMaterialColors",
                true
        );


        leavesMaterial.setColor(
                "Diffuse",
                new ColorRGBA(
                        0.1f,
                        0.55f,
                        0.15f,
                        1f
                )
        );


        leavesMaterial.setColor(
                "Ambient",
                new ColorRGBA(
                        0.1f,
                        0.55f,
                        0.15f,
                        1f
                )
        );


        leaves.setMaterial(
                leavesMaterial
        );


        leaves.setLocalTranslation(
                position.x,
                position.y + 3.5f,
                position.z
        );


        treeNode.attachChild(
                leaves
        );


        // ==========================
        // PHYSIK
        // ==========================

        trunkPhysics =
                new RigidBodyControl(
                        0f
                );


        trunk.addControl(
                trunkPhysics
        );
    }


    @Override
    public String getSaveId() {

        return saveId;
    }


    @Override
    public Node getNode() {

        return treeNode;
    }


    @Override
    public void attachToWorld(
            Node rootNode
    ) {

        worldParent =
                rootNode;


        if (harvested) {

            return;
        }


        if (
                treeNode.getParent()
                        ==
                        null
        ) {

            rootNode.attachChild(
                    treeNode
            );
        }


        activatePhysics();
    }


    private void activatePhysics() {

        if (physicsActive) {

            return;
        }


        physicsSpace.add(
                trunkPhysics
        );


        physicsActive =
                true;
    }


    @Override
    public void detachFromWorld() {

        if (physicsActive) {

            physicsSpace.remove(
                    trunkPhysics
            );


            physicsActive =
                    false;
        }


        treeNode.removeFromParent();
    }


    @Override
    public boolean owns(
            Geometry geometry
    ) {

        return geometry == trunk
                ||
                geometry == leaves;
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

        return ItemType.WOOD;
    }


    @Override
    public int getYield() {

        return 1;
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


        if (this.harvested) {

            this.health =
                    0;


            detachFromWorld();


            return;
        }


        if (
                worldParent != null
                        &&
                        treeNode.getParent()
                                ==
                                null
        ) {

            worldParent.attachChild(
                    treeNode
            );
        }


        activatePhysics();
    }
}