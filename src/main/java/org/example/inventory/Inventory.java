package org.example.inventory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Inventory {

    public static final int SLOT_COUNT =
            16;


    public static final int HOTBAR_SLOT_COUNT =
            3;


    private final List<InventorySlot> slots =
            new ArrayList<>(
                    SLOT_COUNT
            );


    public Inventory() {

        for (
                int i = 0;
                i < SLOT_COUNT;
                i++
        ) {

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


        /*
         * Erst prüfen, ob wirklich genug
         * Platz für die komplette Menge da ist.
         */

        if (
                !canAddItem(
                        itemType,
                        amount
                )
        ) {

            return false;
        }


        int remaining =
                amount;


        /*
         * Vorhandene Stacks auffüllen.
         */

        for (
                InventorySlot slot
                :
                slots
        ) {

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


        /*
         * Danach leere Slots verwenden.
         */

        for (
                InventorySlot slot
                :
                slots
        ) {

            if (
                    slot.isEmpty()
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


        return remaining <= 0;
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


        for (
                InventorySlot slot
                :
                slots
        ) {

            freeSpace +=
                    slot.getFreeSpace(
                            itemType
                    );


            if (
                    freeSpace >= amount
            ) {

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


        for (
                InventorySlot slot
                :
                slots
        ) {

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


        for (
                InventorySlot slot
                :
                slots
        ) {

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


            if (
                    remaining <= 0
            ) {

                return true;
            }
        }


        return false;
    }


    /*
     * Drag & Drop.
     *
     * Leerer Zielslot:
     * kompletter Stack wird verschoben.
     *
     * Gleiches Item:
     * Stacks werden zusammengeführt.
     *
     * Unterschiedliche Items:
     * Slots werden getauscht.
     */

    public boolean moveStack(
            int fromIndex,
            int toIndex
    ) {

        if (
                !isValidSlot(
                        fromIndex
                )
                        ||
                        !isValidSlot(
                                toIndex
                        )
        ) {

            return false;
        }


        if (
                fromIndex == toIndex
        ) {

            return false;
        }


        InventorySlot source =
                slots.get(
                        fromIndex
                );


        InventorySlot target =
                slots.get(
                        toIndex
                );


        if (
                source.isEmpty()
        ) {

            return false;
        }


        // ==========================
        // ZIEL IST LEER
        // ==========================

        if (
                target.isEmpty()
        ) {

            target.setStack(
                    source.getItemType(),
                    source.getAmount()
            );


            source.clear();


            return true;
        }


        // ==========================
        // GLEICHES ITEM
        // ==========================

        if (
                source.getItemType()
                        ==
                        target.getItemType()
        ) {

            int moved =
                    target.add(
                            source.getItemType(),
                            source.getAmount()
                    );


            if (
                    moved <= 0
            ) {

                return false;
            }


            source.remove(
                    moved
            );


            return true;
        }


        // ==========================
        // UNTERSCHIEDLICHE ITEMS
        // → STACKS TAUSCHEN
        // ==========================

        ItemType sourceType =
                source.getItemType();


        int sourceAmount =
                source.getAmount();


        ItemType targetType =
                target.getItemType();


        int targetAmount =
                target.getAmount();


        source.setStack(
                targetType,
                targetAmount
        );


        target.setStack(
                sourceType,
                sourceAmount
        );


        return true;
    }


    private boolean isValidSlot(
            int index
    ) {

        return index >= 0
                &&
                index < slots.size();
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
                !isValidSlot(
                        index
                )
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