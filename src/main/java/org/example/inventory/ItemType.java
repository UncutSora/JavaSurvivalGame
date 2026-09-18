package org.example.inventory;

public enum ItemType {

    WOOD(
            "Holz",
            99
    ),

    STONE(
            "Stein",
            99
    ),

    STONE_AXE(
            "Steinaxt",
            1
    ),

    BERRIES(
            "Beeren",
            20
    ),

    WATER(
            "Wasser",
            10
    ),

    RAW_MEAT(
            "Rohes Fleisch",
            20
    ),

    WOOL(
            "Wolle",
            30
    ),

    SAPLING(
            "Setzling",
            20
    );


    private final String displayName;

    private final int maxStack;


    ItemType(
            String displayName,
            int maxStack
    ) {

        this.displayName =
                displayName;

        this.maxStack =
                maxStack;
    }


    public String getDisplayName() {

        return displayName;
    }


    public int getMaxStack() {

        return maxStack;
    }
}