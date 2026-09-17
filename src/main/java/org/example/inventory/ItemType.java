package org.example.inventory;

public enum ItemType {

    WOOD("Holz"),
    STONE("Stein");

    private final String displayName;

    ItemType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}