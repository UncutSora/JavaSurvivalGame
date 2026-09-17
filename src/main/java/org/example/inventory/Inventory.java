package org.example.inventory;

import java.util.EnumMap;
import java.util.Map;

public class Inventory {

    private final Map<ItemType, Integer> items =
            new EnumMap<>(ItemType.class);

    public Inventory() {

        for (ItemType itemType : ItemType.values()) {
            items.put(itemType, 0);
        }
    }

    public void addItem(ItemType itemType, int amount) {

        if (amount <= 0) {
            return;
        }

        int currentAmount =
                items.getOrDefault(itemType, 0);

        items.put(
                itemType,
                currentAmount + amount
        );
    }

    public int getAmount(ItemType itemType) {

        return items.getOrDefault(
                itemType,
                0
        );
    }

    public boolean hasItem(
            ItemType itemType,
            int amount
    ) {

        return getAmount(itemType) >= amount;
    }

    public boolean removeItem(
            ItemType itemType,
            int amount
    ) {

        if (!hasItem(itemType, amount)) {
            return false;
        }

        items.put(
                itemType,
                getAmount(itemType) - amount
        );

        return true;
    }
}