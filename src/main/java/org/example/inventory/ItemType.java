package org.example.inventory;

public enum ItemType {

    WOOD("Holz"),
    STONE("Stein"),
    STONE_AXE("Steinaxt");

    private final String displayName;

    ItemType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}