package org.example.hotbar;

import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import org.example.inventory.ItemType;

public class HotbarSystem implements ActionListener {

    private int selectedSlot = 1;


    public HotbarSystem(
            InputManager inputManager
    ) {

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
                selectedSlot = 1;
                break;

            case "Hotbar2":
                selectedSlot = 2;
                break;

            case "Hotbar3":
                selectedSlot = 3;
                break;
        }
    }


    public int getSelectedSlot() {

        return selectedSlot;
    }


    public ItemType getSelectedItemType() {

        switch (selectedSlot) {

            case 1:
                return ItemType.WOOD;

            case 2:
                return ItemType.STONE;

            case 3:
                return ItemType.STONE_AXE;

            default:
                return ItemType.WOOD;
        }
    }
}