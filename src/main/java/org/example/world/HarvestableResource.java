package org.example.world;

import com.jme3.scene.Geometry;
import com.jme3.scene.Node;

import org.example.inventory.ItemType;

public interface HarvestableResource {

    String getSaveId();


    Node getNode();


    void attachToWorld(
            Node rootNode
    );


    void detachFromWorld();


    boolean owns(
            Geometry geometry
    );


    boolean takeDamage(
            int damage
    );


    boolean isHarvested();


    int getHealth();


    int getMaxHealth();


    ItemType getItemType();


    int getYield();


    void loadState(
            int health,
            boolean harvested
    );
}