package org.example.inventory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Inventory {

    public static final int SLOT_COUNT = 16;

    private final List<InventorySlot> slots =
            new ArrayList<>(SLOT_COUNT);


    public Inventory() {

        for (int i = 0; i < SLOT_COUNT; i++) {

            slots.add(
                    new InventorySlot()
            );
        }
    }


    public boolean addItem(
            ItemType itemType,
            int amount
    ) {

        if (
                itemType == null
                        ||
                        amount <= 0
        ) {

            return false;
        }


        int remaining =
                amount;


        // Vorhandene Stacks auffüllen

        for (InventorySlot slot : slots) {

            if (
                    slot.canStack(
                            itemType
                    )
            ) {

                int added =
                        slot.add(
                                itemType,
                                remaining
                        );


                remaining -=
                        added;


                if (remaining <= 0) {

                    return true;
                }
            }
        }


        // Leere Slots verwenden

        for (InventorySlot slot : slots) {

            if (slot.isEmpty()) {

                int added =
                        slot.add(
                                itemType,
                                remaining
                        );


                remaining -=
                        added;


                if (remaining <= 0) {

                    return true;
                }
            }
        }


        // Inventar voll

        return false;
    }


    public boolean canAddItem(
            ItemType itemType,
            int amount
    ) {

        if (
                itemType == null
                        ||
                        amount <= 0
        ) {

            return false;
        }


        int freeSpace =
                0;


        for (InventorySlot slot : slots) {

            freeSpace +=
                    slot.getFreeSpace(
                            itemType
                    );


            if (freeSpace >= amount) {

                return true;
            }
        }


        return false;
    }


    public int getAmount(
            ItemType itemType
    ) {

        int total =
                0;


        for (InventorySlot slot : slots) {

            if (
                    !slot.isEmpty()
                            &&
                            slot.getItemType()
                                    ==
                                    itemType
            ) {

                total +=
                        slot.getAmount();
            }
        }


        return total;
    }


    public boolean hasItem(
            ItemType itemType,
            int amount
    ) {

        return getAmount(
                itemType
        )
                >=
                amount;
    }


    public boolean removeItem(
            ItemType itemType,
            int amount
    ) {

        if (
                itemType == null
                        ||
                        amount <= 0
        ) {

            return false;
        }


        if (
                !hasItem(
                        itemType,
                        amount
                )
        ) {

            return false;
        }


        int remaining =
                amount;


        for (InventorySlot slot : slots) {

            if (
                    slot.isEmpty()
                            ||
                            slot.getItemType()
                                    !=
                                    itemType
            ) {

                continue;
            }


            int removed =
                    slot.remove(
                            remaining
                    );


            remaining -=
                    removed;


            if (remaining <= 0) {

                return true;
            }
        }


        return false;
    }


    public List<InventorySlot> getSlots() {

        return Collections.unmodifiableList(
                slots
        );
    }


    public InventorySlot getSlot(
            int index
    ) {

        if (
                index < 0
                        ||
                        index >= slots.size()
        ) {

            throw new IndexOutOfBoundsException(
                    "Ungültiger Inventar-Slot: "
                            +
                            index
            );
        }


        return slots.get(
                index
        );
    }


    public int getSlotCount() {

        return slots.size();
    }
}