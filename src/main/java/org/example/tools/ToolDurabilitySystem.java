package org.example.tools;

import org.example.inventory.Inventory;
import org.example.inventory.ItemType;

public class ToolDurabilitySystem {

    private static final int STONE_AXE_MAX_DURABILITY =
            20;


    private final Inventory inventory;


    private int stoneAxeDurability =
            0;


    public ToolDurabilitySystem(
            Inventory inventory
    ) {

        this.inventory =
                inventory;


        syncWithInventory();
    }


    public void update() {

        syncWithInventory();
    }


    private void syncWithInventory() {

        int axeCount =
                inventory.getAmount(
                        ItemType.STONE_AXE
                );


        if (
                axeCount <= 0
        ) {

            stoneAxeDurability =
                    0;


            return;
        }


        if (
                stoneAxeDurability <= 0
        ) {

            stoneAxeDurability =
                    STONE_AXE_MAX_DURABILITY;
        }
    }


    public boolean hasUsableStoneAxe() {

        return inventory.hasItem(
                ItemType.STONE_AXE,
                1
        )
                &&
                stoneAxeDurability > 0;
    }


    public boolean useStoneAxe() {

        if (
                !hasUsableStoneAxe()
        ) {

            return false;
        }


        stoneAxeDurability--;


        if (
                stoneAxeDurability > 0
        ) {

            return false;
        }


        inventory.removeItem(
                ItemType.STONE_AXE,
                1
        );


        if (
                inventory.hasItem(
                        ItemType.STONE_AXE,
                        1
                )
        ) {

            stoneAxeDurability =
                    STONE_AXE_MAX_DURABILITY;
        }

        else {

            stoneAxeDurability =
                    0;
        }


        return true;
    }


    // ==========================
    // SAVE / LOAD
    // ==========================

    public void loadStoneAxeDurability(
            int durability
    ) {

        if (
                !inventory.hasItem(
                        ItemType.STONE_AXE,
                        1
                )
        ) {

            stoneAxeDurability =
                    0;


            return;
        }


        stoneAxeDurability =
                Math.max(
                        1,
                        Math.min(
                                STONE_AXE_MAX_DURABILITY,
                                durability
                        )
                );
    }


    public int getStoneAxeDurability() {

        return stoneAxeDurability;
    }


    public int getStoneAxeMaxDurability() {

        return STONE_AXE_MAX_DURABILITY;
    }


    public float getStoneAxeDurabilityPercent() {

        if (
                STONE_AXE_MAX_DURABILITY <= 0
        ) {

            return 0f;
        }


        return (float) stoneAxeDurability
                /
                (float) STONE_AXE_MAX_DURABILITY;
    }
}