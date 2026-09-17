package org.example;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Quad;
import com.jme3.system.AppSettings;

import org.example.building.BuildingSystem;
import org.example.crafting.CraftingSystem;
import org.example.hotbar.HotbarSystem;
import org.example.interaction.InteractionSystem;
import org.example.inventory.Inventory;
import org.example.player.Player;
import org.example.save.SaveGameSystem;
import org.example.survival.ConsumableSystem;
import org.example.survival.PlayerStats;
import org.example.time.DayNightSystem;
import org.example.tools.ToolDurabilitySystem;
import org.example.ui.InventoryHud;
import org.example.ui.InventoryMenuSystem;
import org.example.ui.SurvivalHud;
import org.example.ui.TimeHud;
import org.example.ui.ToolView;
import org.example.world.BerryBush;
import org.example.world.HarvestableResource;
import org.example.world.Rock;
import org.example.world.Sheep;
import org.example.world.Tree;
import org.example.world.WaterSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main extends SimpleApplication {

    private BulletAppState bulletAppState;

    private Player player;

    private PlayerStats playerStats;

    private SurvivalHud survivalHud;

    private Inventory inventory;

    private InventoryHud inventoryHud;

    private InventoryMenuSystem inventoryMenuSystem;

    private HotbarSystem hotbarSystem;

    private ToolView toolView;

    private ToolDurabilitySystem toolDurabilitySystem;

    private ConsumableSystem consumableSystem;

    private SaveGameSystem saveGameSystem;

    private DayNightSystem dayNightSystem;

    private TimeHud timeHud;

    private BuildingSystem buildingSystem;


    private final List<HarvestableResource> resources =
            new ArrayList<>();

    private final List<Sheep> sheep =
            new ArrayList<>();


    // =========================================================
    // WELT
    // =========================================================

    private static final float WORLD_SIZE =
            1000f;


    private static final float WORLD_HALF_SIZE =
            WORLD_SIZE / 2f;


    /*
     * Gleicher Seed =
     * gleiche Welt bei jedem Spielstart.
     *
     * Wichtig für Save/Load.
     */
    private static final long WORLD_SEED =
            20260917L;


    // =========================================================
    // RESSOURCENMENGEN
    // =========================================================

    private static final int TREE_COUNT =
            3500;


    private static final int ROCK_COUNT =
            1600;


    private static final int BERRY_BUSH_COUNT =
            900;


    private static final int WATER_SOURCE_COUNT =
            60;


    private static final int SHEEP_COUNT =
            80;


    // =========================================================
    // GENERIERUNGS-EINSTELLUNGEN
    // =========================================================

    private static final float SAFE_SPAWN_RADIUS =
            20f;


    private static final float WORLD_BORDER_MARGIN =
            20f;


    private static final float TREE_MIN_DISTANCE =
            2.2f;


    private static final float ROCK_MIN_DISTANCE =
            1.8f;


    private static final float BERRY_MIN_DISTANCE =
            1.5f;


    private static final float WATER_MIN_DISTANCE =
            10f;


    private static final float SHEEP_MIN_DISTANCE =
            3.5f;


    public static void main(
            String[] args
    ) {

        Main game =
                new Main();


        AppSettings settings =
                new AppSettings(
                        true
                );


        settings.setTitle(
                "Java Survival Game"
        );


        settings.setResolution(
                1280,
                720
        );


        game.setSettings(
                settings
        );


        game.setShowSettings(
                false
        );


        game.start();
    }


    @Override
    public void simpleInitApp() {

        setupPhysics();

        createGround();

        createDayNightSystem();

        createResources();

        createPlayer();

        createPlayerStats();

        createSurvivalHud();

        createInventory();

        createHotbarSystem();

        createToolDurabilitySystem();

        createInventoryHud();

        createToolView();

        createCraftingSystem();

        createInventoryMenuSystem();

        createConsumableSystem();

        createBuildingSystem();

        createSaveGameSystem();

        createInteractionSystem();

        createTimeHud();

        createCrosshair();


        flyCam.setMoveSpeed(
                0f
        );


        setDisplayFps(
                false
        );


        setDisplayStatView(
                false
        );
    }


    // =========================================================
    // PHYSIK
    // =========================================================

    private void setupPhysics() {

        bulletAppState =
                new BulletAppState();


        stateManager.attach(
                bulletAppState
        );
    }


    // =========================================================
    // TAG / NACHT
    // =========================================================

    private void createDayNightSystem() {

        dayNightSystem =
                new DayNightSystem(
                        rootNode,
                        viewPort
                );
    }


    private void createTimeHud() {

        timeHud =
                new TimeHud(
                        assetManager,
                        guiNode,
                        cam,
                        dayNightSystem
                );
    }


    // =========================================================
    // SPIELER
    // =========================================================

    private void createPlayer() {

        player =
                new Player(
                        cam,
                        inputManager,
                        bulletAppState
                                .getPhysicsSpace()
                );


        cam.lookAt(
                new Vector3f(
                        0f,
                        1.5f,
                        0f
                ),
                Vector3f.UNIT_Y
        );
    }


    // =========================================================
    // SURVIVAL
    // =========================================================

    private void createPlayerStats() {

        playerStats =
                new PlayerStats(
                        player
                );
    }


    private void createSurvivalHud() {

        survivalHud =
                new SurvivalHud(
                        assetManager,
                        guiNode,
                        cam,
                        playerStats
                );
    }


    // =========================================================
    // INVENTAR
    // =========================================================

    private void createInventory() {

        inventory =
                new Inventory();
    }


    private void createHotbarSystem() {

        hotbarSystem =
                new HotbarSystem(
                        inputManager,
                        inventory
                );
    }


    private void createToolDurabilitySystem() {

        toolDurabilitySystem =
                new ToolDurabilitySystem(
                        inventory
                );
    }


    private void createInventoryHud() {

        inventoryHud =
                new InventoryHud(
                        assetManager,
                        guiNode,
                        cam,
                        inventory,
                        hotbarSystem,
                        toolDurabilitySystem
                );
    }


    private void createInventoryMenuSystem() {

        inventoryMenuSystem =
                new InventoryMenuSystem(
                        assetManager,
                        guiNode,
                        cam,
                        inputManager,
                        flyCam,
                        player,
                        inventory,
                        toolDurabilitySystem
                );
    }


    // =========================================================
    // TOOLS / CRAFTING / ITEMS
    // =========================================================

    private void createToolView() {

        toolView =
                new ToolView(
                        assetManager,
                        guiNode,
                        cam,
                        inventory,
                        hotbarSystem
                );
    }


    private void createCraftingSystem() {

        new CraftingSystem(
                inputManager,
                inventory
        );
    }


    private void createConsumableSystem() {

        consumableSystem =
                new ConsumableSystem(
                        inputManager,
                        hotbarSystem,
                        playerStats,
                        inventoryMenuSystem
                );
    }


    // =========================================================
    // BUILDING
    // =========================================================

    private void createBuildingSystem() {

        buildingSystem =
                new BuildingSystem(
                        assetManager,
                        rootNode,
                        guiNode,
                        cam,
                        inputManager,
                        bulletAppState
                                .getPhysicsSpace(),
                        inventory,
                        inventoryMenuSystem
                );
    }


    // =========================================================
    // SAVE
    // =========================================================

    private void createSaveGameSystem() {

        saveGameSystem =
                new SaveGameSystem(
                        inputManager,
                        player,
                        playerStats,
                        inventory,
                        toolDurabilitySystem,
                        resources,
                        sheep,
                        dayNightSystem,
                        buildingSystem
                );
    }


    // =========================================================
    // INTERACTION
    // =========================================================

    private void createInteractionSystem() {

        new InteractionSystem(
                cam,
                inputManager,
                rootNode,
                resources,
                sheep,
                inventory,
                hotbarSystem,
                toolView,
                toolDurabilitySystem,
                inventoryMenuSystem,
                buildingSystem,
                dayNightSystem
        );
    }


    // =========================================================
    // WELTGENERIERUNG
    // =========================================================

    private void createResources() {

        Random random =
                new Random(
                        WORLD_SEED
                );


        generateTrees(
                random
        );


        generateRocks(
                random
        );


        generateBerryBushes(
                random
        );


        generateWaterSources(
                random
        );


        generateSheep(
                random
        );


        System.out.println(
                "================================"
        );


        System.out.println(
                "WELT GENERIERT"
        );


        System.out.println(
                "Weltgröße: "
                        +
                        (int) WORLD_SIZE
                        +
                        " x "
                        +
                        (int) WORLD_SIZE
        );


        System.out.println(
                "Bäume: "
                        +
                        TREE_COUNT
        );


        System.out.println(
                "Steine: "
                        +
                        ROCK_COUNT
        );


        System.out.println(
                "Beerensträucher: "
                        +
                        BERRY_BUSH_COUNT
        );


        System.out.println(
                "Wasserstellen: "
                        +
                        WATER_SOURCE_COUNT
        );


        System.out.println(
                "Schafe: "
                        +
                        SHEEP_COUNT
        );


        System.out.println(
                "Ressourcen gesamt: "
                        +
                        resources.size()
        );


        System.out.println(
                "================================"
        );
    }


    // =========================================================
    // BÄUME
    // =========================================================

    private void generateTrees(
            Random random
    ) {

        for (
                int i = 1;
                i <= TREE_COUNT;
                i++
        ) {

            Vector3f position =
                    generateResourcePosition(
                            random,
                            TREE_MIN_DISTANCE
                    );


            Tree tree =
                    new Tree(
                            createId(
                                    "tree",
                                    i
                            ),
                            assetManager,
                            bulletAppState
                                    .getPhysicsSpace(),
                            position
                    );


            addResource(
                    tree
            );
        }
    }


    // =========================================================
    // STEINE
    // =========================================================

    private void generateRocks(
            Random random
    ) {

        for (
                int i = 1;
                i <= ROCK_COUNT;
                i++
        ) {

            Vector3f position =
                    generateResourcePosition(
                            random,
                            ROCK_MIN_DISTANCE
                    );


            Rock rock =
                    new Rock(
                            createId(
                                    "rock",
                                    i
                            ),
                            assetManager,
                            bulletAppState
                                    .getPhysicsSpace(),
                            position
                    );


            addResource(
                    rock
            );
        }
    }


    // =========================================================
    // BEEREN
    // =========================================================

    private void generateBerryBushes(
            Random random
    ) {

        for (
                int i = 1;
                i <= BERRY_BUSH_COUNT;
                i++
        ) {

            Vector3f position =
                    generateResourcePosition(
                            random,
                            BERRY_MIN_DISTANCE
                    );


            BerryBush berryBush =
                    new BerryBush(
                            createId(
                                    "berry",
                                    i
                            ),
                            assetManager,
                            bulletAppState
                                    .getPhysicsSpace(),
                            position
                    );


            addResource(
                    berryBush
            );
        }
    }


    // =========================================================
    // WASSER
    // =========================================================

    private void generateWaterSources(
            Random random
    ) {

        for (
                int i = 1;
                i <= WATER_SOURCE_COUNT;
                i++
        ) {

            Vector3f position =
                    generateResourcePosition(
                            random,
                            WATER_MIN_DISTANCE
                    );


            WaterSource waterSource =
                    new WaterSource(
                            createId(
                                    "water",
                                    i
                            ),
                            assetManager,
                            position
                    );


            addResource(
                    waterSource
            );
        }
    }


    // =========================================================
    // SCHAFE
    // =========================================================

    private void generateSheep(
            Random random
    ) {

        for (
                int i = 1;
                i <= SHEEP_COUNT;
                i++
        ) {

            Vector3f position;


            if (
                    i <= 8
            ) {

                float angle =
                        (float) (
                                (i - 1)
                                        *
                                        (Math.PI * 2.0 / 8.0)
                        );

                float radius =
                        16f
                                +
                                (i % 2) * 3f;

                position =
                        new Vector3f(
                                (float) Math.cos(angle) * radius,
                                0f,
                                (float) Math.sin(angle) * radius
                        );
            }

            else {

                position =
                        generateResourcePosition(
                                random,
                                SHEEP_MIN_DISTANCE
                        );
            }


            Sheep currentSheep =
                    new Sheep(
                            createId(
                                    "sheep",
                                    i
                            ),
                            assetManager,
                            position,
                            WORLD_SEED + i * 31L
                    );


            sheep.add(
                    currentSheep
            );


            currentSheep.attachToWorld(
                    rootNode
            );
        }
    }


    // =========================================================
    // POSITION GENERIEREN
    // =========================================================

    private Vector3f generateResourcePosition(
            Random random,
            float minimumDistance
    ) {

        int attempts =
                0;


        while (
                attempts < 300
        ) {

            attempts++;


            float x =
                    randomRange(
                            random,
                            -WORLD_HALF_SIZE
                                    +
                                    WORLD_BORDER_MARGIN,
                            WORLD_HALF_SIZE
                                    -
                                    WORLD_BORDER_MARGIN
                    );


            float z =
                    randomRange(
                            random,
                            -WORLD_HALF_SIZE
                                    +
                                    WORLD_BORDER_MARGIN,
                            WORLD_HALF_SIZE
                                    -
                                    WORLD_BORDER_MARGIN
                    );


            Vector3f candidate =
                    new Vector3f(
                            x,
                            0f,
                            z
                    );


            // ==========================
            // SPAWN FREI HALTEN
            // ==========================

            float spawnDistanceSquared =
                    candidate.x
                            *
                            candidate.x

                            +

                            candidate.z
                                    *
                                    candidate.z;


            if (
                    spawnDistanceSquared
                            <
                            SAFE_SPAWN_RADIUS
                                    *
                                    SAFE_SPAWN_RADIUS
            ) {

                continue;
            }


            // ==========================
            // ABSTAND ZU ANDEREN
            // ==========================

            if (
                    isFarEnoughFromResources(
                            candidate,
                            minimumDistance
                    )
            ) {

                return candidate;
            }
        }


        /*
         * Fallback:
         * Falls nach sehr vielen Versuchen
         * kein freier Platz gefunden wurde.
         */
        return generateFallbackPosition(
                random
        );
    }


    private Vector3f generateFallbackPosition(
            Random random
    ) {

        float x =
                randomRange(
                        random,
                        -WORLD_HALF_SIZE
                                +
                                WORLD_BORDER_MARGIN,
                        WORLD_HALF_SIZE
                                -
                                WORLD_BORDER_MARGIN
                );


        float z =
                randomRange(
                        random,
                        -WORLD_HALF_SIZE
                                +
                                WORLD_BORDER_MARGIN,
                        WORLD_HALF_SIZE
                                -
                                WORLD_BORDER_MARGIN
                );


        return new Vector3f(
                x,
                0f,
                z
        );
    }


    // =========================================================
    // ABSTANDS-CHECK
    // =========================================================

    private boolean isFarEnoughFromResources(
            Vector3f candidate,
            float minimumDistance
    ) {

        float minimumDistanceSquared =
                minimumDistance
                        *
                        minimumDistance;


        for (
                HarvestableResource resource
                :
                resources
        ) {

            Vector3f resourcePosition =
                    resource
                            .getNode()
                            .getWorldTranslation();


            float dx =
                    resourcePosition.x
                            -
                            candidate.x;


            float dz =
                    resourcePosition.z
                            -
                            candidate.z;


            float distanceSquared =
                    dx * dx
                            +
                            dz * dz;


            if (
                    distanceSquared
                            <
                            minimumDistanceSquared
            ) {

                return false;
            }
        }


        return true;
    }


    // =========================================================
    // RANDOM
    // =========================================================

    private float randomRange(
            Random random,
            float min,
            float max
    ) {

        return min
                +
                random.nextFloat()
                        *
                        (max - min);
    }


    // =========================================================
    // SAVE-ID
    // =========================================================

    private String createId(
            String type,
            int number
    ) {

        return String.format(
                "%s_%04d",
                type,
                number
        );
    }


    // =========================================================
    // RESOURCE REGISTRIEREN
    // =========================================================

    private void addResource(
            HarvestableResource resource
    ) {

        resources.add(
                resource
        );


        resource.attachToWorld(
                rootNode
        );
    }


    // =========================================================
    // BODEN
    // =========================================================

    private void createGround() {

        /*
         * Box verwendet Halbgrößen.
         *
         * 500 links + 500 rechts
         * = 1000 Einheiten Gesamtgröße.
         */
        Box groundBox =
                new Box(
                        WORLD_HALF_SIZE,
                        0.1f,
                        WORLD_HALF_SIZE
                );


        Geometry ground =
                new Geometry(
                        "Ground",
                        groundBox
                );


        Material material =
                new Material(
                        assetManager,
                        "Common/MatDefs/Light/Lighting.j3md"
                );


        material.setBoolean(
                "UseMaterialColors",
                true
        );


        ColorRGBA groundColor =
                new ColorRGBA(
                        0.25f,
                        0.6f,
                        0.25f,
                        1f
                );


        material.setColor(
                "Diffuse",
                groundColor
        );


        material.setColor(
                "Ambient",
                groundColor
        );


        ground.setMaterial(
                material
        );


        ground.setLocalTranslation(
                0f,
                -0.1f,
                0f
        );


        rootNode.attachChild(
                ground
        );


        RigidBodyControl physics =
                new RigidBodyControl(
                        0f
                );


        ground.addControl(
                physics
        );


        bulletAppState
                .getPhysicsSpace()
                .add(
                        physics
                );
    }


    // =========================================================
    // CROSSHAIR
    // =========================================================

    private void createCrosshair() {

        Material crosshairMaterial =
                new Material(
                        assetManager,
                        "Common/MatDefs/Misc/Unshaded.j3md"
                );


        crosshairMaterial.setColor(
                "Color",
                ColorRGBA.White
        );


        Geometry horizontal =
                new Geometry(
                        "CrosshairHorizontal",
                        new Quad(
                                14f,
                                2f
                        )
                );


        horizontal.setMaterial(
                crosshairMaterial
        );


        horizontal.setLocalTranslation(
                cam.getWidth() / 2f - 7f,
                cam.getHeight() / 2f - 1f,
                2f
        );


        guiNode.attachChild(
                horizontal
        );


        Geometry vertical =
                new Geometry(
                        "CrosshairVertical",
                        new Quad(
                                2f,
                                14f
                        )
                );


        vertical.setMaterial(
                crosshairMaterial
        );


        vertical.setLocalTranslation(
                cam.getWidth() / 2f - 1f,
                cam.getHeight() / 2f - 7f,
                2f
        );


        guiNode.attachChild(
                vertical
        );
    }


    private float getCurrentGameMinute() {

        return dayNightSystem.getDay()
                *
                24f
                *
                60f
                +
                dayNightSystem.getMinuteOfDay();
    }


    // =========================================================
    // UPDATE
    // =========================================================

    @Override
    public void simpleUpdate(
            float tpf
    ) {

        if (
                dayNightSystem != null
        ) {

            dayNightSystem.update(
                    tpf
            );
        }


        if (
                timeHud != null
        ) {

            timeHud.update();
        }


        if (
                player != null
        ) {

            player.update(
                    tpf
            );
        }


        if (
                playerStats != null
        ) {

            playerStats.update(
                    tpf
            );
        }


        if (
                toolDurabilitySystem != null
        ) {

            toolDurabilitySystem.update();
        }


        if (
                survivalHud != null
        ) {

            survivalHud.update();
        }


        if (
                inventoryHud != null
        ) {

            inventoryHud.update();
        }


        if (
                inventoryMenuSystem != null
        ) {

            inventoryMenuSystem.update();
        }


        for (
                Sheep currentSheep
                :
                sheep
        ) {

            currentSheep.update(
                    tpf,
                    getCurrentGameMinute()
            );
        }


        if (
                buildingSystem != null
        ) {

            buildingSystem.update();
        }


        if (
                toolView != null
        ) {

            toolView.update(
                    tpf
            );
        }
    }
}