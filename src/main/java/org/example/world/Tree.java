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

    private final Node treeNode;

    private final Geometry trunk;
    private final Geometry leaves;

    private final PhysicsSpace physicsSpace;

    private final RigidBodyControl trunkPhysics;

    private boolean harvested = false;


    public Tree(
            AssetManager assetManager,
            PhysicsSpace physicsSpace,
            Vector3f position
    ) {

        this.physicsSpace = physicsSpace;

        treeNode = new Node("Tree");


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
                ColorRGBA.Brown
        );


        trunkMaterial.setColor(
                "Ambient",
                ColorRGBA.Brown
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
        // BLÄTTER
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
        // KOLLISION
        // ==========================

        trunkPhysics =
                new RigidBodyControl(
                        0f
                );


        trunk.addControl(
                trunkPhysics
        );


        physicsSpace.add(
                trunkPhysics
        );
    }


    @Override
    public Node getNode() {

        return treeNode;
    }


    @Override
    public boolean owns(
            Geometry geometry
    ) {

        return geometry == trunk
                || geometry == leaves;
    }


    @Override
    public void harvest() {

        if (harvested) {
            return;
        }


        harvested = true;


        physicsSpace.remove(
                trunkPhysics
        );


        treeNode.removeFromParent();
    }


    @Override
    public boolean isHarvested() {

        return harvested;
    }


    @Override
    public ItemType getItemType() {

        return ItemType.WOOD;
    }


    @Override
    public int getYield() {

        return 1;
    }
}