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

public class Rock implements HarvestableResource {

    private final String saveId;

    private final Node rockNode;

    private final Geometry rock;

    private final PhysicsSpace physicsSpace;

    private final RigidBodyControl rockPhysics;


    private Node worldParent;


    private boolean physicsActive =
            false;


    private final int maxHealth =
            60;


    private int health =
            maxHealth;


    private boolean harvested =
            false;


    public Rock(
            String saveId,
            AssetManager assetManager,
            PhysicsSpace physicsSpace,
            Vector3f position
    ) {

        this.saveId =
                saveId;


        this.physicsSpace =
                physicsSpace;


        rockNode =
                new Node(
                        "Rock_"
                                +
                                saveId
                );


        Sphere rockMesh =
                new Sphere(
                        16,
                        16,
                        0.8f
                );


        rock =
                new Geometry(
                        "RockGeometry",
                        rockMesh
                );


        Material rockMaterial =
                new Material(
                        assetManager,
                        "Common/MatDefs/Light/Lighting.j3md"
                );


        rockMaterial.setBoolean(
                "UseMaterialColors",
                true
        );


        rockMaterial.setColor(
                "Diffuse",
                new ColorRGBA(
                        0.45f,
                        0.45f,
                        0.45f,
                        1f
                )
        );


        rockMaterial.setColor(
                "Ambient",
                new ColorRGBA(
                        0.45f,
                        0.45f,
                        0.45f,
                        1f
                )
        );


        rock.setMaterial(
                rockMaterial
        );


        rock.setLocalScale(
                1.2f,
                0.8f,
                1f
        );


        rock.setLocalTranslation(
                position.x,
                position.y + 0.65f,
                position.z
        );


        rockNode.attachChild(
                rock
        );


        rockPhysics =
                new RigidBodyControl(
                        0f
                );


        rock.addControl(
                rockPhysics
        );
    }


    @Override
    public String getSaveId() {

        return saveId;
    }


    @Override
    public Node getNode() {

        return rockNode;
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
                rockNode.getParent()
                        ==
                        null
        ) {

            rootNode.attachChild(
                    rockNode
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
                rockPhysics
        );


        physicsActive =
                true;
    }


    private void removeFromWorld() {

        if (
                physicsActive
        ) {

            physicsSpace.remove(
                    rockPhysics
            );


            physicsActive =
                    false;
        }


        rockNode.removeFromParent();
    }


    @Override
    public boolean owns(
            Geometry geometry
    ) {

        return geometry == rock;
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


            removeFromWorld();


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

        return ItemType.STONE;
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


        if (
                this.harvested
        ) {

            this.health =
                    0;


            removeFromWorld();


            return;
        }


        if (
                worldParent != null
                        &&
                        rockNode.getParent()
                                ==
                                null
        ) {

            worldParent.attachChild(
                    rockNode
            );
        }


        activatePhysics();
    }
}