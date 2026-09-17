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


        /*
         * Keine Axt vorhanden.
         */

        if (axeCount <= 0) {

            stoneAxeDurability =
                    0;

            return;
        }


        /*
         * Eine Axt wurde neu gecraftet
         * und besitzt noch keine Haltbarkeit.
         */

        if (stoneAxeDurability <= 0) {

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

        if (!hasUsableStoneAxe()) {
            return false;
        }


        stoneAxeDurability--;


        /*
         * Haltbarkeit ist noch vorhanden.
         */

        if (stoneAxeDurability > 0) {

            return false;
        }


        /*
         * Die aktuelle Axt ist kaputt.
         */

        inventory.removeItem(
                ItemType.STONE_AXE,
                1
        );


        /*
         * Falls später mehrere Äxte
         * im Inventar liegen, wird
         * automatisch die nächste benutzt.
         */

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


    public int getStoneAxeDurability() {

        return stoneAxeDurability;
    }


    public int getStoneAxeMaxDurability() {

        return STONE_AXE_MAX_DURABILITY;
    }


    public float getStoneAxeDurabilityPercent() {

        if (
                STONE_AXE_MAX_DURABILITY
                        <=
                        0
        ) {

            return 0f;
        }


        return (float) stoneAxeDurability
                /
                (float) STONE_AXE_MAX_DURABILITY;
    }
}