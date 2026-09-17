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
import org.example.world.Tree;
import org.example.world.WaterSource;

import java.util.ArrayList;
import java.util.List;

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


    private void setupPhysics() {

        bulletAppState =
                new BulletAppState();


        stateManager.attach(
                bulletAppState
        );
    }


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


    private void createInventoryMenuSystem() {

        inventoryMenuSystem =
                new InventoryMenuSystem(
                        assetManager,
                        guiNode,
                        cam,
                        inputManager,
                        flyCam,
                        player,
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


    private void createSaveGameSystem() {

        saveGameSystem =
                new SaveGameSystem(
                        inputManager,
                        player,
                        playerStats,
                        inventory,
                        toolDurabilitySystem,
                        resources,
                        dayNightSystem,
                        buildingSystem
                );
    }


    private void createInteractionSystem() {

        new InteractionSystem(
                cam,
                inputManager,
                rootNode,
                resources,
                inventory,
                hotbarSystem,
                toolView,
                toolDurabilitySystem,
                inventoryMenuSystem,
                buildingSystem
        );
    }


    private void createResources() {

        // ==========================
        // 25 BÄUME
        // ==========================

        addTree("tree_01", 0f, 0f, 0f);
        addTree("tree_02", 5f, 0f, -3f);
        addTree("tree_03", -5f, 0f, -4f);
        addTree("tree_04", 7f, 0f, 4f);
        addTree("tree_05", -7f, 0f, 3f);

        addTree("tree_06", 10f, 0f, 0f);
        addTree("tree_07", -10f, 0f, -1f);
        addTree("tree_08", 12f, 0f, -6f);
        addTree("tree_09", -12f, 0f, -7f);
        addTree("tree_10", 9f, 0f, 9f);

        addTree("tree_11", -9f, 0f, 10f);
        addTree("tree_12", 14f, 0f, 5f);
        addTree("tree_13", -14f, 0f, 6f);
        addTree("tree_14", 15f, 0f, -10f);
        addTree("tree_15", -15f, 0f, -11f);

        addTree("tree_16", 4f, 0f, 13f);
        addTree("tree_17", -4f, 0f, 14f);
        addTree("tree_18", 17f, 0f, 1f);
        addTree("tree_19", -17f, 0f, 2f);
        addTree("tree_20", 18f, 0f, 11f);

        addTree("tree_21", -18f, 0f, 12f);
        addTree("tree_22", 3f, 0f, -15f);
        addTree("tree_23", -3f, 0f, -16f);
        addTree("tree_24", 11f, 0f, -17f);
        addTree("tree_25", -11f, 0f, -18f);


        // ==========================
        // 12 STEINE
        // ==========================

        addRock("rock_01", 3f, 0f, 3f);
        addRock("rock_02", -3f, 0f, 2f);
        addRock("rock_03", 4f, 0f, -6f);
        addRock("rock_04", -4f, 0f, -7f);

        addRock("rock_05", 8f, 0f, -10f);
        addRock("rock_06", -8f, 0f, -9f);
        addRock("rock_07", 13f, 0f, 10f);
        addRock("rock_08", -13f, 0f, 11f);

        addRock("rock_09", 16f, 0f, -4f);
        addRock("rock_10", -16f, 0f, -5f);
        addRock("rock_11", 6f, 0f, 17f);
        addRock("rock_12", -6f, 0f, 18f);


        // ==========================
        // 6 BEERENSTRÄUCHER
        // ==========================

        addBerryBush(
                "berry_01",
                2f,
                0f,
                -2f
        );


        addBerryBush(
                "berry_02",
                -2f,
                0f,
                -3f
        );


        addBerryBush(
                "berry_03",
                6f,
                0f,
                1f
        );


        addBerryBush(
                "berry_04",
                -8f,
                0f,
                7f
        );


        addBerryBush(
                "berry_05",
                12f,
                0f,
                12f
        );


        addBerryBush(
                "berry_06",
                -12f,
                0f,
                -13f
        );


        // ==========================
        // 2 WASSERQUELLEN
        // ==========================

        addWaterSource(
                "water_01",
                0f,
                0f,
                -9f
        );


        addWaterSource(
                "water_02",
                14f,
                0f,
                14f
        );
    }


    // ==========================
    // RESOURCE-HELPER
    // ==========================

    private void addTree(
            String id,
            float x,
            float y,
            float z
    ) {

        addResource(
                new Tree(
                        id,
                        assetManager,
                        bulletAppState.getPhysicsSpace(),
                        new Vector3f(
                                x,
                                y,
                                z
                        )
                )
        );
    }


    private void addRock(
            String id,
            float x,
            float y,
            float z
    ) {

        addResource(
                new Rock(
                        id,
                        assetManager,
                        bulletAppState.getPhysicsSpace(),
                        new Vector3f(
                                x,
                                y,
                                z
                        )
                )
        );
    }


    private void addBerryBush(
            String id,
            float x,
            float y,
            float z
    ) {

        addResource(
                new BerryBush(
                        id,
                        assetManager,
                        bulletAppState.getPhysicsSpace(),
                        new Vector3f(
                                x,
                                y,
                                z
                        )
                )
        );
    }


    private void addWaterSource(
            String id,
            float x,
            float y,
            float z
    ) {

        addResource(
                new WaterSource(
                        id,
                        assetManager,
                        new Vector3f(
                                x,
                                y,
                                z
                        )
                )
        );
    }


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


    private void createGround() {

        /*
         * Größerer Boden:
         *
         * Box benutzt Halbgrößen.
         * 35 bedeutet also ungefähr
         * 70 x 70 Einheiten Spielfläche.
         */

        Box groundBox =
                new Box(
                        35f,
                        0.1f,
                        35f
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