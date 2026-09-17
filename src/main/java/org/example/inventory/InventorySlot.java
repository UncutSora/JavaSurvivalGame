package org.example.inventory;

public class InventorySlot {

    private ItemType itemType;

    private int amount;


    public boolean isEmpty() {

        return itemType == null
                ||
                amount <= 0;
    }


    public ItemType getItemType() {

        return itemType;
    }


    public int getAmount() {

        return amount;
    }


    public boolean canStack(
            ItemType type
    ) {

        return !isEmpty()
                &&
                itemType == type
                &&
                amount < itemType.getMaxStack();
    }


    public int getFreeSpace(
            ItemType type
    ) {

        if (isEmpty()) {

            return type.getMaxStack();
        }


        if (!canStack(type)) {

            return 0;
        }


        return itemType.getMaxStack()
                -
                amount;
    }


    public int add(
            ItemType type,
            int requestedAmount
    ) {

        if (
                type == null
                        ||
                        requestedAmount <= 0
        ) {

            return 0;
        }


        if (isEmpty()) {

            itemType =
                    type;

            amount =
                    0;
        }


        if (itemType != type) {

            return 0;
        }


        int freeSpace =
                itemType.getMaxStack()
                        -
                        amount;


        int amountToAdd =
                Math.min(
                        requestedAmount,
                        freeSpace
                );


        amount +=
                amountToAdd;


        return amountToAdd;
    }


    public int remove(
            int requestedAmount
    ) {

        if (
                isEmpty()
                        ||
                        requestedAmount <= 0
        ) {

            return 0;
        }


        int amountToRemove =
                Math.min(
                        requestedAmount,
                        amount
                );


        amount -=
                amountToRemove;


        if (amount <= 0) {

            clear();
        }


        return amountToRemove;
    }


    public void clear() {

        itemType =
                null;

        amount =
                0;
    }
}