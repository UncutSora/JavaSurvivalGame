package org.example.save;

import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.math.Vector3f;

import org.example.inventory.Inventory;
import org.example.inventory.InventorySlot;
import org.example.inventory.ItemType;
import org.example.player.Player;
import org.example.survival.PlayerStats;
import org.example.tools.ToolDurabilitySystem;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.nio.file.Files;
import java.nio.file.Path;

import java.util.Properties;

public class SaveGameSystem implements ActionListener {

    private static final String SAVE_MAPPING =
            "SaveGame";

    private static final String LOAD_MAPPING =
            "LoadGame";


    private final Player player;

    private final PlayerStats playerStats;

    private final Inventory inventory;

    private final ToolDurabilitySystem
            toolDurabilitySystem;


    private final Path saveFile =
            Path.of(
                    "saves",
                    "savegame.properties"
            );


    public SaveGameSystem(
            InputManager inputManager,
            Player player,
            PlayerStats playerStats,
            Inventory inventory,
            ToolDurabilitySystem toolDurabilitySystem
    ) {

        this.player =
                player;

        this.playerStats =
                playerStats;

        this.inventory =
                inventory;

        this.toolDurabilitySystem =
                toolDurabilitySystem;


        inputManager.addMapping(
                SAVE_MAPPING,
                new KeyTrigger(
                        KeyInput.KEY_F5
                )
        );


        inputManager.addMapping(
                LOAD_MAPPING,
                new KeyTrigger(
                        KeyInput.KEY_F9
                )
        );


        inputManager.addListener(
                this,
                SAVE_MAPPING,
                LOAD_MAPPING
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


        if (
                name.equals(
                        SAVE_MAPPING
                )
        ) {

            saveGame();

            return;
        }


        if (
                name.equals(
                        LOAD_MAPPING
                )
        ) {

            loadGame();
        }
    }


    public void saveGame() {

        Properties properties =
                new Properties();


        // ==========================
        // SPIELERPOSITION
        // ==========================

        Vector3f position =
                player.getPosition();


        properties.setProperty(
                "player.x",
                Float.toString(
                        position.x
                )
        );


        properties.setProperty(
                "player.y",
                Float.toString(
                        position.y
                )
        );


        properties.setProperty(
                "player.z",
                Float.toString(
                        position.z
                )
        );


        // ==========================
        // SURVIVAL-WERTE
        // ==========================

        properties.setProperty(
                "stats.health",
                Float.toString(
                        playerStats.getHealth()
                )
        );


        properties.setProperty(
                "stats.hunger",
                Float.toString(
                        playerStats.getHunger()
                )
        );


        properties.setProperty(
                "stats.thirst",
                Float.toString(
                        playerStats.getThirst()
                )
        );


        properties.setProperty(
                "stats.stamina",
                Float.toString(
                        playerStats.getStamina()
                )
        );


        properties.setProperty(
                "stats.exhausted",
                Boolean.toString(
                        playerStats.isSprintExhausted()
                )
        );


        // ==========================
        // INVENTAR
        // ==========================

        properties.setProperty(
                "inventory.slotCount",
                Integer.toString(
                        inventory.getSlotCount()
                )
        );


        for (
                int i = 0;
                i < inventory.getSlotCount();
                i++
        ) {

            InventorySlot slot =
                    inventory.getSlot(
                            i
                    );


            String prefix =
                    "inventory."
                            +
                            i
                            +
                            ".";


            if (
                    slot.isEmpty()
            ) {

                properties.setProperty(
                        prefix + "type",
                        ""
                );


                properties.setProperty(
                        prefix + "amount",
                        "0"
                );


                continue;
            }


            properties.setProperty(
                    prefix + "type",
                    slot
                            .getItemType()
                            .name()
            );


            properties.setProperty(
                    prefix + "amount",
                    Integer.toString(
                            slot.getAmount()
                    )
            );
        }


        // ==========================
        // AXT-HALTBARKEIT
        // ==========================

        properties.setProperty(
                "tools.stoneAxeDurability",
                Integer.toString(
                        toolDurabilitySystem
                                .getStoneAxeDurability()
                )
        );


        // ==========================
        // DATEI SCHREIBEN
        // ==========================

        try {

            Files.createDirectories(
                    saveFile.getParent()
            );


            try (
                    OutputStream outputStream =
                            Files.newOutputStream(
                                    saveFile
                            )
            ) {

                properties.store(
                        outputStream,
                        "Java Survival Game Save"
                );
            }


            System.out.println(
                    "================================"
            );


            System.out.println(
                    "SPIEL GESPEICHERT"
            );


            System.out.println(
                    "Datei: "
                            +
                            saveFile
                                    .toAbsolutePath()
            );


            System.out.println(
                    "================================"
            );

        }

        catch (
                IOException exception
        ) {

            System.out.println(
                    "FEHLER BEIM SPEICHERN!"
            );


            exception.printStackTrace();
        }
    }


    public void loadGame() {

        if (
                !Files.exists(
                        saveFile
                )
        ) {

            System.out.println(
                    "Kein Spielstand vorhanden."
            );


            System.out.println(
                    "Drücke zuerst F5 zum Speichern."
            );


            return;
        }


        Properties properties =
                new Properties();


        try (
                InputStream inputStream =
                        Files.newInputStream(
                                saveFile
                        )
        ) {

            properties.load(
                    inputStream
            );

        }

        catch (
                IOException exception
        ) {

            System.out.println(
                    "FEHLER BEIM LADEN!"
            );


            exception.printStackTrace();


            return;
        }


        // ==========================
        // SPIELERPOSITION
        // ==========================

        Vector3f currentPosition =
                player.getPosition();


        float x =
                readFloat(
                        properties,
                        "player.x",
                        currentPosition.x
                );


        float y =
                readFloat(
                        properties,
                        "player.y",
                        currentPosition.y
                );


        float z =
                readFloat(
                        properties,
                        "player.z",
                        currentPosition.z
                );


        player
                .getCharacter()
                .setPhysicsLocation(
                        new Vector3f(
                                x,
                                y,
                                z
                        )
                );


        // ==========================
        // SURVIVAL-WERTE
        // ==========================

        float health =
                readFloat(
                        properties,
                        "stats.health",
                        playerStats.getHealth()
                );


        float hunger =
                readFloat(
                        properties,
                        "stats.hunger",
                        playerStats.getHunger()
                );


        float thirst =
                readFloat(
                        properties,
                        "stats.thirst",
                        playerStats.getThirst()
                );


        float stamina =
                readFloat(
                        properties,
                        "stats.stamina",
                        playerStats.getStamina()
                );


        boolean exhausted =
                Boolean.parseBoolean(
                        properties.getProperty(
                                "stats.exhausted",
                                "false"
                        )
                );


        playerStats.loadState(
                health,
                hunger,
                thirst,
                stamina,
                exhausted
        );


        // ==========================
        // INVENTAR LEEREN
        // ==========================

        for (
                int i = 0;
                i < inventory.getSlotCount();
                i++
        ) {

            inventory
                    .getSlot(
                            i
                    )
                    .clear();
        }


        // ==========================
        // INVENTAR LADEN
        // ==========================

        for (
                int i = 0;
                i < inventory.getSlotCount();
                i++
        ) {

            String prefix =
                    "inventory."
                            +
                            i
                            +
                            ".";


            String typeName =
                    properties.getProperty(
                            prefix + "type",
                            ""
                    );


            int amount =
                    readInt(
                            properties,
                            prefix + "amount",
                            0
                    );


            if (
                    typeName.isBlank()
                            ||
                            amount <= 0
            ) {

                continue;
            }


            try {

                ItemType itemType =
                        ItemType.valueOf(
                                typeName
                        );


                inventory
                        .getSlot(
                                i
                        )
                        .setStack(
                                itemType,
                                amount
                        );

            }

            catch (
                    IllegalArgumentException exception
            ) {

                System.out.println(
                        "Unbekanntes Item im Spielstand: "
                                +
                                typeName
                );
            }
        }


        // ==========================
        // AXT-HALTBARKEIT LADEN
        // ==========================

        int durability =
                readInt(
                        properties,
                        "tools.stoneAxeDurability",
                        toolDurabilitySystem
                                .getStoneAxeMaxDurability()
                );


        toolDurabilitySystem
                .loadStoneAxeDurability(
                        durability
                );


        System.out.println(
                "================================"
        );


        System.out.println(
                "SPIELSTAND GELADEN"
        );


        System.out.println(
                "Position: "
                        +
                        x
                        +
                        ", "
                        +
                        y
                        +
                        ", "
                        +
                        z
        );


        System.out.println(
                "Leben: "
                        +
                        Math.round(
                                health
                        )
        );


        System.out.println(
                "Hunger: "
                        +
                        Math.round(
                                hunger
                        )
        );


        System.out.println(
                "Durst: "
                        +
                        Math.round(
                                thirst
                        )
        );


        System.out.println(
                "================================"
        );
    }


    private float readFloat(
            Properties properties,
            String key,
            float defaultValue
    ) {

        String value =
                properties.getProperty(
                        key
                );


        if (
                value == null
        ) {

            return defaultValue;
        }


        try {

            return Float.parseFloat(
                    value
            );

        }

        catch (
                NumberFormatException exception
        ) {

            return defaultValue;
        }
    }


    private int readInt(
            Properties properties,
            String key,
            int defaultValue
    ) {

        String value =
                properties.getProperty(
                        key
                );


        if (
                value == null
        ) {

            return defaultValue;
        }


        try {

            return Integer.parseInt(
                    value
            );

        }

        catch (
                NumberFormatException exception
        ) {

            return defaultValue;
        }
    }
}