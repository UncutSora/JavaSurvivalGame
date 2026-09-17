package org.example.world;

import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import org.example.inventory.ItemType;

public interface HarvestableResource {

    Node getNode();

    boolean owns(Geometry geometry);

    boolean takeDamage(int damage);

    boolean isHarvested();

    int getHealth();

    int getMaxHealth();

    ItemType getItemType();

    int getYield();
}