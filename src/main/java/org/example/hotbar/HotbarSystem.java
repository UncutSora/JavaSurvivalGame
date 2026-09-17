package org.example.hotbar;

import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;

import org.example.inventory.Inventory;
import org.example.inventory.InventorySlot;
import org.example.inventory.ItemType;

public class HotbarSystem implements ActionListener {

    private final Inventory inventory;


    private int selectedSlot =
            1;


    public HotbarSystem(
            InputManager inputManager,
            Inventory inventory
    ) {

        this.inventory =
                inventory;


        inputManager.addMapping(
                "Hotbar1",
                new KeyTrigger(
                        KeyInput.KEY_1
                )
        );


        inputManager.addMapping(
                "Hotbar2",
                new KeyTrigger(
                        KeyInput.KEY_2
                )
        );


        inputManager.addMapping(
                "Hotbar3",
                new KeyTrigger(
                        KeyInput.KEY_3
                )
        );


        inputManager.addListener(
                this,
                "Hotbar1",
                "Hotbar2",
                "Hotbar3"
        );
    }


    @Override
    public void onAction(
            String name,
            boolean isPressed,
            float tpf
    ) {

        if (!isPressed) {

            return;
        }


        switch (name) {

            case "Hotbar1":

                selectedSlot =
                        1;

                break;


            case "Hotbar2":

                selectedSlot =
                        2;

                break;


            case "Hotbar3":

                selectedSlot =
                        3;

                break;
        }
    }


    public int getSelectedSlot() {

        return selectedSlot;
    }


    public InventorySlot getSelectedInventorySlot() {

        return inventory.getSlot(
                selectedSlot - 1
        );
    }


    public ItemType getSelectedItemType() {

        InventorySlot slot =
                getSelectedInventorySlot();


        if (
                slot.isEmpty()
        ) {

            return null;
        }


        return slot.getItemType();
    }


    public int getSelectedAmount() {

        InventorySlot slot =
                getSelectedInventorySlot();


        if (
                slot.isEmpty()
        ) {

            return 0;
        }


        return slot.getAmount();
    }
}