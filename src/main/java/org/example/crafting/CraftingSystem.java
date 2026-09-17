package org.example.crafting;

import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import org.example.inventory.Inventory;
import org.example.inventory.ItemType;

public class CraftingSystem implements ActionListener {

    private final Inventory inventory;

    private static final int AXE_WOOD_COST = 3;
    private static final int AXE_STONE_COST = 2;


    public CraftingSystem(
            InputManager inputManager,
            Inventory inventory
    ) {

        this.inventory = inventory;


        inputManager.addMapping(
                "CraftStoneAxe",
                new KeyTrigger(
                        KeyInput.KEY_C
                )
        );


        inputManager.addListener(
                this,
                "CraftStoneAxe"
        );
    }


    @Override
    public void onAction(
            String name,
            boolean isPressed,
            float tpf
    ) {

        if (
                name.equals("CraftStoneAxe")
                        &&
                        isPressed
        ) {

            craftStoneAxe();
        }
    }


    private void craftStoneAxe() {

        boolean hasWood =
                inventory.hasItem(
                        ItemType.WOOD,
                        AXE_WOOD_COST
                );


        boolean hasStone =
                inventory.hasItem(
                        ItemType.STONE,
                        AXE_STONE_COST
                );


        if (!hasWood || !hasStone) {

            System.out.println(
                    "Nicht genug Materialien!"
            );

            System.out.println(
                    "Steinaxt benötigt: 3 Holz + 2 Stein"
            );

            return;
        }


        inventory.removeItem(
                ItemType.WOOD,
                AXE_WOOD_COST
        );


        inventory.removeItem(
                ItemType.STONE,
                AXE_STONE_COST
        );


        inventory.addItem(
                ItemType.STONE_AXE,
                1
        );


        System.out.println(
                "Steinaxt hergestellt!"
        );
    }
}