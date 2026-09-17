package org.example.survival;

import com.jme3.input.InputManager;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.MouseButtonTrigger;

import org.example.hotbar.HotbarSystem;
import org.example.inventory.InventorySlot;
import org.example.inventory.ItemType;
import org.example.ui.InventoryMenuSystem;

public class ConsumableSystem implements ActionListener {

    private static final float BERRY_HUNGER_VALUE =
            20f;


    private static final float WATER_THIRST_VALUE =
            35f;


    private final HotbarSystem hotbarSystem;

    private final PlayerStats playerStats;

    private final InventoryMenuSystem
            inventoryMenuSystem;


    public ConsumableSystem(
            InputManager inputManager,
            HotbarSystem hotbarSystem,
            PlayerStats playerStats,
            InventoryMenuSystem inventoryMenuSystem
    ) {

        this.hotbarSystem =
                hotbarSystem;

        this.playerStats =
                playerStats;

        this.inventoryMenuSystem =
                inventoryMenuSystem;


        inputManager.addMapping(
                "UseSelectedItem",
                new MouseButtonTrigger(
                        MouseInput.BUTTON_RIGHT
                )
        );


        inputManager.addListener(
                this,
                "UseSelectedItem"
        );
    }


    @Override
    public void onAction(
            String name,
            boolean isPressed,
            float tpf
    ) {

        if (
                !name.equals(
                        "UseSelectedItem"
                )
                        ||
                        !isPressed
        ) {

            return;
        }


        if (
                inventoryMenuSystem.isOpen()
        ) {

            return;
        }


        useSelectedItem();
    }


    private void useSelectedItem() {

        InventorySlot slot =
                hotbarSystem
                        .getSelectedInventorySlot();


        if (
                slot.isEmpty()
        ) {

            return;
        }


        ItemType itemType =
                slot.getItemType();


        // ==========================
        // BEEREN ESSEN
        // ==========================

        if (
                itemType
                        ==
                        ItemType.BERRIES
        ) {

            if (
                    playerStats.getHunger()
                            >=
                            playerStats.getMaxHunger()
                                    -
                                    0.1f
            ) {

                System.out.println(
                        "Du hast keinen Hunger."
                );


                return;
            }


            slot.remove(
                    1
            );


            playerStats.addHunger(
                    BERRY_HUNGER_VALUE
            );


            System.out.println(
                    "Beeren gegessen."
            );


            System.out.println(
                    "Hunger: "
                            +
                            Math.round(
                                    playerStats.getHunger()
                            )
                            +
                            " / "
                            +
                            Math.round(
                                    playerStats.getMaxHunger()
                            )
            );


            return;
        }


        // ==========================
        // WASSER TRINKEN
        // ==========================

        if (
                itemType
                        ==
                        ItemType.WATER
        ) {

            if (
                    playerStats.getThirst()
                            >=
                            playerStats.getMaxThirst()
                                    -
                                    0.1f
            ) {

                System.out.println(
                        "Du hast keinen Durst."
                );


                return;
            }


            slot.remove(
                    1
            );


            playerStats.addThirst(
                    WATER_THIRST_VALUE
            );


            System.out.println(
                    "Wasser getrunken."
            );


            System.out.println(
                    "Durst: "
                            +
                            Math.round(
                                    playerStats.getThirst()
                            )
                            +
                            " / "
                            +
                            Math.round(
                                    playerStats.getMaxThirst()
                            )
            );
        }
    }
}