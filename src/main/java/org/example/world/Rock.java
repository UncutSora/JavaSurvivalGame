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

    private final Node rockNode;

    private final Geometry rock;

    private final PhysicsSpace physicsSpace;

    private final RigidBodyControl rockPhysics;

    private boolean harvested = false;


    public Rock(
            AssetManager assetManager,
            PhysicsSpace physicsSpace,
            Vector3f position
    ) {

        this.physicsSpace =
                physicsSpace;


        rockNode =
                new Node(
                        "Rock"
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


        physicsSpace.add(
                rockPhysics
        );
    }


    @Override
    public Node getNode() {

        return rockNode;
    }


    @Override
    public boolean owns(
            Geometry geometry
    ) {

        return geometry == rock;
    }


    @Override
    public void harvest() {

        if (harvested) {
            return;
        }


        harvested = true;


        physicsSpace.remove(
                rockPhysics
        );


        rockNode.removeFromParent();
    }


    @Override
    public boolean isHarvested() {

        return harvested;
    }


    @Override
    public ItemType getItemType() {

        return ItemType.STONE;
    }


    @Override
    public int getYield() {

        return 1;
    }
}