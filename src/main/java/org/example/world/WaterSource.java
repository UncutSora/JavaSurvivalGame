package org.example.world;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;

import org.example.inventory.ItemType;

public class WaterSource implements HarvestableResource {

    private final String saveId;

    private final Node waterNode;

    private final Geometry water;


    private Node worldParent;


    public WaterSource(
            String saveId,
            AssetManager assetManager,
            Vector3f position
    ) {

        this.saveId =
                saveId;


        waterNode =
                new Node(
                        "WaterSource_"
                                +
                                saveId
                );


        Box waterMesh =
                new Box(
                        1.4f,
                        0.08f,
                        1.4f
                );


        water =
                new Geometry(
                        "Water",
                        waterMesh
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
                        0.08f,
                        0.4f,
                        0.9f,
                        1f
                )
        );


        material.setColor(
                "Ambient",
                new ColorRGBA(
                        0.08f,
                        0.4f,
                        0.9f,
                        1f
                )
        );


        water.setMaterial(
                material
        );


        water.setLocalTranslation(
                position.x,
                position.y + 0.08f,
                position.z
        );


        waterNode.attachChild(
                water
        );
    }


    @Override
    public String getSaveId() {

        return saveId;
    }


    @Override
    public Node getNode() {

        return waterNode;
    }


    @Override
    public void attachToWorld(
            Node rootNode
    ) {

        worldParent =
                rootNode;


        if (
                waterNode.getParent()
                        ==
                        null
        ) {

            rootNode.attachChild(
                    waterNode
            );
        }
    }


    @Override
    public boolean owns(
            Geometry geometry
    ) {

        return geometry == water;
    }


    @Override
    public boolean takeDamage(
            int damage
    ) {

        return damage > 0;
    }


    @Override
    public boolean isHarvested() {

        return false;
    }


    @Override
    public int getHealth() {

        return 1;
    }


    @Override
    public int getMaxHealth() {

        return 1;
    }


    @Override
    public ItemType getItemType() {

        return ItemType.WATER;
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

        if (
                worldParent != null
                        &&
                        waterNode.getParent()
                                ==
                                null
        ) {

            worldParent.attachChild(
                    waterNode
            );
        }
    }
}