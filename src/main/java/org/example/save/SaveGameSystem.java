package org.example.save;

import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;

import org.example.building.BuildingSystem;
import org.example.inventory.Inventory;
import org.example.inventory.InventorySlot;
import org.example.inventory.ItemType;
import org.example.player.Player;
import org.example.survival.PlayerStats;
import org.example.time.DayNightSystem;
import org.example.tools.ToolDurabilitySystem;
import org.example.world.HarvestableResource;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.nio.file.Files;
import java.nio.file.Path;

import java.util.List;
import java.util.Properties;

public class SaveGameSystem implements ActionListener {

    private static final String SAVE_MAPPING =
            "SaveGame";

    private static final String LOAD_MAPPING =
            "LoadGame";


    private static final int SAVE_VERSION =
            9;


    private final Player player;

    private final PlayerStats playerStats;

    private final Inventory inventory;

    private final ToolDurabilitySystem toolDurabilitySystem;

    private final List<HarvestableResource> resources;

    private final DayNightSystem dayNightSystem;

    private final BuildingSystem buildingSystem;


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
            ToolDurabilitySystem toolDurabilitySystem,
            List<HarvestableResource> resources,
            DayNightSystem dayNightSystem,
            BuildingSystem buildingSystem
    ) {

        this.player =
                player;

        this.playerStats =
                playerStats;

        this.inventory =
                inventory;

        this.toolDurabilitySystem =
                toolDurabilitySystem;

        this.resources =
                resources;

        this.dayNightSystem =
                dayNightSystem;

        this.buildingSystem =
                buildingSystem;


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

        if (
                !isPressed
        ) {

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


        properties.setProperty(
                "save.version",
                Integer.toString(
                        SAVE_VERSION
                )
        );


        savePlayer(
                properties
        );


        saveStats(
                properties
        );


        saveTime(
                properties
        );


        saveInventory(
                properties
        );


        saveTools(
                properties
        );


        saveWorld(
                properties
        );


        saveBuildings(
                properties
        );


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
                    "Decken: "
                            +
                            buildingSystem.getCeilingCount()
            );

            System.out.println(
                    "================================"
            );


            System.out.println(
                    "SPIEL GESPEICHERT"
            );


            System.out.println(
                    "Tag: "
                            +
                            dayNightSystem.getDay()
            );


            System.out.println(
                    "Uhrzeit: "
                            +
                            dayNightSystem.getFormattedTime()
            );


            System.out.println(
                    "Fundamente: "
                            +
                            buildingSystem.getFoundationCount()
            );


            System.out.println(
                    "Wände: "
                            +
                            buildingSystem.getWallCount()
            );


            System.out.println(
                    "Türrahmen: "
                            +
                            buildingSystem.getDoorFrameCount()
            );


            System.out.println(
                    "Türen: "
                            +
                            buildingSystem.getDoorCount()
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


    // =========================================================
    // SPIELER SPEICHERN
    // =========================================================

    private void savePlayer(
            Properties properties
    ) {

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
    }


    // =========================================================
    // SURVIVAL-WERTE SPEICHERN
    // =========================================================

    private void saveStats(
            Properties properties
    ) {

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
    }


    // =========================================================
    // ZEIT SPEICHERN
    // =========================================================

    private void saveTime(
            Properties properties
    ) {

        properties.setProperty(
                "time.day",
                Integer.toString(
                        dayNightSystem.getDay()
                )
        );


        properties.setProperty(
                "time.minuteOfDay",
                Float.toString(
                        dayNightSystem.getMinuteOfDay()
                )
        );
    }


    // =========================================================
    // INVENTAR SPEICHERN
    // =========================================================

    private void saveInventory(
            Properties properties
    ) {

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
    }


    // =========================================================
    // WERKZEUGE SPEICHERN
    // =========================================================

    private void saveTools(
            Properties properties
    ) {

        properties.setProperty(
                "tools.stoneAxeDurability",
                Integer.toString(
                        toolDurabilitySystem
                                .getStoneAxeDurability()
                )
        );
    }


    // =========================================================
    // WELT SPEICHERN
    // =========================================================

    private void saveWorld(
            Properties properties
    ) {

        for (
                HarvestableResource resource
                :
                resources
        ) {

            String prefix =
                    "world."
                            +
                            resource.getSaveId()
                            +
                            ".";


            properties.setProperty(
                    prefix + "health",
                    Integer.toString(
                            resource.getHealth()
                    )
            );


            properties.setProperty(
                    prefix + "harvested",
                    Boolean.toString(
                            resource.isHarvested()
                    )
            );
        }
    }


    // =========================================================
    // GEBÄUDE SPEICHERN
    // =========================================================

    private void saveBuildings(
            Properties properties
    ) {

        saveFoundations(
                properties
        );


        saveWalls(
                properties
        );


        saveDoorFrames(
                properties
        );


        saveDoors(
                properties
        );

        saveCeilings(
                properties
        );
    }


    private void saveFoundations(
            Properties properties
    ) {

        int foundationCount =
                buildingSystem.getFoundationCount();


        properties.setProperty(
                "building.foundation.count",
                Integer.toString(
                        foundationCount
                )
        );


        for (
                int i = 0;
                i < foundationCount;
                i++
        ) {

            Vector3f position =
                    buildingSystem
                            .getFoundationPosition(
                                    i
                            );


            String prefix =
                    "building.foundation."
                            +
                            i
                            +
                            ".";


            properties.setProperty(
                    prefix + "x",
                    Float.toString(
                            position.x
                    )
            );


            properties.setProperty(
                    prefix + "y",
                    Float.toString(
                            position.y
                    )
            );


            properties.setProperty(
                    prefix + "z",
                    Float.toString(
                            position.z
                    )
            );
        }
    }


    private void saveWalls(
            Properties properties
    ) {

        int wallCount =
                buildingSystem.getWallCount();


        properties.setProperty(
                "building.wall.count",
                Integer.toString(
                        wallCount
                )
        );


        for (
                int i = 0;
                i < wallCount;
                i++
        ) {

            Vector3f position =
                    buildingSystem
                            .getWallPosition(
                                    i
                            );


            Quaternion rotation =
                    buildingSystem
                            .getWallRotation(
                                    i
                            );


            String prefix =
                    "building.wall."
                            +
                            i
                            +
                            ".";


            properties.setProperty(
                    prefix + "x",
                    Float.toString(
                            position.x
                    )
            );


            properties.setProperty(
                    prefix + "y",
                    Float.toString(
                            position.y
                    )
            );


            properties.setProperty(
                    prefix + "z",
                    Float.toString(
                            position.z
                    )
            );


            properties.setProperty(
                    prefix + "rotX",
                    Float.toString(
                            rotation.getX()
                    )
            );


            properties.setProperty(
                    prefix + "rotY",
                    Float.toString(
                            rotation.getY()
                    )
            );


            properties.setProperty(
                    prefix + "rotZ",
                    Float.toString(
                            rotation.getZ()
                    )
            );


            properties.setProperty(
                    prefix + "rotW",
                    Float.toString(
                            rotation.getW()
                    )
            );
        }
    }


    private void saveDoorFrames(
            Properties properties
    ) {

        int doorFrameCount =
                buildingSystem.getDoorFrameCount();


        properties.setProperty(
                "building.doorFrame.count",
                Integer.toString(
                        doorFrameCount
                )
        );


        for (
                int i = 0;
                i < doorFrameCount;
                i++
        ) {

            Vector3f position =
                    buildingSystem
                            .getDoorFramePosition(
                                    i
                            );


            Quaternion rotation =
                    buildingSystem
                            .getDoorFrameRotation(
                                    i
                            );


            String prefix =
                    "building.doorFrame."
                            +
                            i
                            +
                            ".";


            properties.setProperty(
                    prefix + "x",
                    Float.toString(
                            position.x
                    )
            );


            properties.setProperty(
                    prefix + "y",
                    Float.toString(
                            position.y
                    )
            );


            properties.setProperty(
                    prefix + "z",
                    Float.toString(
                            position.z
                    )
            );


            properties.setProperty(
                    prefix + "rotX",
                    Float.toString(
                            rotation.getX()
                    )
            );


            properties.setProperty(
                    prefix + "rotY",
                    Float.toString(
                            rotation.getY()
                    )
            );


            properties.setProperty(
                    prefix + "rotZ",
                    Float.toString(
                            rotation.getZ()
                    )
            );


            properties.setProperty(
                    prefix + "rotW",
                    Float.toString(
                            rotation.getW()
                    )
            );
        }
    }


    private void saveDoors(
            Properties properties
    ) {

        int doorCount =
                buildingSystem.getDoorCount();


        properties.setProperty(
                "building.door.count",
                Integer.toString(
                        doorCount
                )
        );


        for (
                int i = 0;
                i < doorCount;
                i++
        ) {

            Vector3f framePosition =
                    buildingSystem.getDoorParentFramePosition(
                            i
                    );

            Quaternion rotation =
                    buildingSystem.getDoorRotation(
                            i
                    );

            String prefix =
                    "building.door."
                            +
                            i
                            +
                            ".";


            properties.setProperty(prefix + "frameX", Float.toString(framePosition.x));
            properties.setProperty(prefix + "frameY", Float.toString(framePosition.y));
            properties.setProperty(prefix + "frameZ", Float.toString(framePosition.z));
            properties.setProperty(prefix + "rotX", Float.toString(rotation.getX()));
            properties.setProperty(prefix + "rotY", Float.toString(rotation.getY()));
            properties.setProperty(prefix + "rotZ", Float.toString(rotation.getZ()));
            properties.setProperty(prefix + "rotW", Float.toString(rotation.getW()));
            properties.setProperty(prefix + "open", Boolean.toString(buildingSystem.isDoorOpen(i)));
        }
    }


    private void saveCeilings(Properties properties) {

        int ceilingCount = buildingSystem.getCeilingCount();
        properties.setProperty(
                "building.ceiling.count",
                Integer.toString(ceilingCount)
        );

        for (int i = 0; i < ceilingCount; i++) {
            Vector3f position = buildingSystem.getCeilingPosition(i);
            String prefix = "building.ceiling." + i + ".";

            properties.setProperty(prefix + "x", Float.toString(position.x));
            properties.setProperty(prefix + "y", Float.toString(position.y));
            properties.setProperty(prefix + "z", Float.toString(position.z));
        }
    }


    // =========================================================
    // LADEN
    // =========================================================

    public void loadGame() {

        if (
                !Files.exists(
                        saveFile
                )
        ) {

            System.out.println(
                    "Kein Spielstand vorhanden."
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


        loadPlayer(
                properties
        );


        loadStats(
                properties
        );


        loadTime(
                properties
        );


        loadInventory(
                properties
        );


        loadTools(
                properties
        );


        loadWorld(
                properties
        );


        loadBuildings(
                properties
        );


        System.out.println(
                "================================"
        );


        System.out.println(
                "SPIELSTAND GELADEN"
        );


        System.out.println(
                "Fundamente: "
                        +
                        buildingSystem.getFoundationCount()
        );


        System.out.println(
                "Wände: "
                        +
                        buildingSystem.getWallCount()
        );


        System.out.println(
                "Türrahmen: "
                        +
                        buildingSystem.getDoorFrameCount()
        );


        System.out.println(
                "Türen: "
                        +
                        buildingSystem.getDoorCount()
        );


        System.out.println(
                "Decken: "
                        +
                        buildingSystem.getCeilingCount()
        );

        System.out.println(
                "================================"
        );
    }


    // =========================================================
    // SPIELER LADEN
    // =========================================================

    private void loadPlayer(
            Properties properties
    ) {

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
    }


    // =========================================================
    // SURVIVAL-WERTE LADEN
    // =========================================================

    private void loadStats(
            Properties properties
    ) {

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
    }


    // =========================================================
    // ZEIT LADEN
    // =========================================================

    private void loadTime(
            Properties properties
    ) {

        int day =
                readInt(
                        properties,
                        "time.day",
                        dayNightSystem.getDay()
                );


        float minuteOfDay =
                readFloat(
                        properties,
                        "time.minuteOfDay",
                        dayNightSystem.getMinuteOfDay()
                );


        dayNightSystem.loadState(
                day,
                minuteOfDay
        );
    }


    // =========================================================
    // INVENTAR LADEN
    // =========================================================

    private void loadInventory(
            Properties properties
    ) {

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
                        "Unbekanntes Item im Savegame: "
                                +
                                typeName
                );
            }
        }
    }


    // =========================================================
    // TOOLS LADEN
    // =========================================================

    private void loadTools(
            Properties properties
    ) {

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
    }


    // =========================================================
    // WELT LADEN
    // =========================================================

    private void loadWorld(
            Properties properties
    ) {

        for (
                HarvestableResource resource
                :
                resources
        ) {

            String prefix =
                    "world."
                            +
                            resource.getSaveId()
                            +
                            ".";


            int resourceHealth =
                    readInt(
                            properties,
                            prefix + "health",
                            resource.getMaxHealth()
                    );


            boolean harvested =
                    Boolean.parseBoolean(
                            properties.getProperty(
                                    prefix + "harvested",
                                    "false"
                            )
                    );


            resource.loadState(
                    resourceHealth,
                    harvested
            );
        }
    }


    // =========================================================
    // GEBÄUDE LADEN
    // =========================================================

    private void loadBuildings(
            Properties properties
    ) {

        buildingSystem.clearBuildings();


        loadFoundations(
                properties
        );


        loadWalls(
                properties
        );


        loadDoorFrames(
                properties
        );


        loadDoors(
                properties
        );

        loadCeilings(
                properties
        );
    }


    private void loadFoundations(
            Properties properties
    ) {

        int foundationCount =
                readInt(
                        properties,
                        "building.foundation.count",
                        0
                );


        for (
                int i = 0;
                i < foundationCount;
                i++
        ) {

            String prefix =
                    "building.foundation."
                            +
                            i
                            +
                            ".";


            float x =
                    readFloat(
                            properties,
                            prefix + "x",
                            0f
                    );


            float y =
                    readFloat(
                            properties,
                            prefix + "y",
                            0.12f
                    );


            float z =
                    readFloat(
                            properties,
                            prefix + "z",
                            0f
                    );


            buildingSystem.loadFoundation(
                    new Vector3f(
                            x,
                            y,
                            z
                    )
            );
        }
    }


    private void loadWalls(
            Properties properties
    ) {

        int wallCount =
                readInt(
                        properties,
                        "building.wall.count",
                        0
                );


        for (
                int i = 0;
                i < wallCount;
                i++
        ) {

            String prefix =
                    "building.wall."
                            +
                            i
                            +
                            ".";


            float x =
                    readFloat(
                            properties,
                            prefix + "x",
                            0f
                    );


            float y =
                    readFloat(
                            properties,
                            prefix + "y",
                            1.62f
                    );


            float z =
                    readFloat(
                            properties,
                            prefix + "z",
                            0f
                    );


            float rotX =
                    readFloat(
                            properties,
                            prefix + "rotX",
                            0f
                    );


            float rotY =
                    readFloat(
                            properties,
                            prefix + "rotY",
                            0f
                    );


            float rotZ =
                    readFloat(
                            properties,
                            prefix + "rotZ",
                            0f
                    );


            float rotW =
                    readFloat(
                            properties,
                            prefix + "rotW",
                            1f
                    );


            Quaternion rotation =
                    new Quaternion(
                            rotX,
                            rotY,
                            rotZ,
                            rotW
                    );


            buildingSystem.loadWall(
                    new Vector3f(
                            x,
                            y,
                            z
                    ),
                    rotation
            );
        }
    }


    private void loadDoorFrames(
            Properties properties
    ) {

        int doorFrameCount =
                readInt(
                        properties,
                        "building.doorFrame.count",
                        0
                );


        for (
                int i = 0;
                i < doorFrameCount;
                i++
        ) {

            String prefix =
                    "building.doorFrame."
                            +
                            i
                            +
                            ".";


            float x =
                    readFloat(
                            properties,
                            prefix + "x",
                            0f
                    );


            float y =
                    readFloat(
                            properties,
                            prefix + "y",
                            1.62f
                    );


            float z =
                    readFloat(
                            properties,
                            prefix + "z",
                            0f
                    );


            float rotX =
                    readFloat(
                            properties,
                            prefix + "rotX",
                            0f
                    );


            float rotY =
                    readFloat(
                            properties,
                            prefix + "rotY",
                            0f
                    );


            float rotZ =
                    readFloat(
                            properties,
                            prefix + "rotZ",
                            0f
                    );


            float rotW =
                    readFloat(
                            properties,
                            prefix + "rotW",
                            1f
                    );


            Quaternion rotation =
                    new Quaternion(
                            rotX,
                            rotY,
                            rotZ,
                            rotW
                    );


            buildingSystem.loadDoorFrame(
                    new Vector3f(
                            x,
                            y,
                            z
                    ),
                    rotation
            );
        }
    }


    private void loadDoors(
            Properties properties
    ) {

        int doorCount =
                readInt(
                        properties,
                        "building.door.count",
                        0
                );


        for (
                int i = 0;
                i < doorCount;
                i++
        ) {

            String prefix =
                    "building.door."
                            +
                            i
                            +
                            ".";

            float frameX = readFloat(properties, prefix + "frameX", 0f);
            float frameY = readFloat(properties, prefix + "frameY", 1.62f);
            float frameZ = readFloat(properties, prefix + "frameZ", 0f);
            float rotX = readFloat(properties, prefix + "rotX", 0f);
            float rotY = readFloat(properties, prefix + "rotY", 0f);
            float rotZ = readFloat(properties, prefix + "rotZ", 0f);
            float rotW = readFloat(properties, prefix + "rotW", 1f);
            boolean open = Boolean.parseBoolean(properties.getProperty(prefix + "open", "false"));

            buildingSystem.loadDoor(
                    new Vector3f(
                            frameX,
                            frameY,
                            frameZ
                    ),
                    new Quaternion(
                            rotX,
                            rotY,
                            rotZ,
                            rotW
                    ),
                    open
            );
        }
    }


    private void loadCeilings(Properties properties) {

        int ceilingCount = readInt(
                properties,
                "building.ceiling.count",
                0
        );

        for (int i = 0; i < ceilingCount; i++) {
            String prefix = "building.ceiling." + i + ".";

            float x = readFloat(properties, prefix + "x", 0f);
            float y = readFloat(properties, prefix + "y", 3.12f);
            float z = readFloat(properties, prefix + "z", 0f);

            buildingSystem.loadCeiling(new Vector3f(x, y, z));
        }
    }


    // =========================================================
    // HELPER
    // =========================================================

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