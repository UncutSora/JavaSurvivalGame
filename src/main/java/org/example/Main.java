package org.example;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Quad;
import com.jme3.system.AppSettings;

import org.example.crafting.CraftingSystem;
import org.example.hotbar.HotbarSystem;
import org.example.interaction.InteractionSystem;
import org.example.inventory.Inventory;
import org.example.player.Player;
import org.example.survival.PlayerStats;
import org.example.tools.ToolDurabilitySystem;
import org.example.ui.InventoryHud;
import org.example.ui.SurvivalHud;
import org.example.ui.ToolView;
import org.example.world.HarvestableResource;
import org.example.world.Rock;
import org.example.world.Tree;

import java.util.ArrayList;
import java.util.List;

public class Main extends SimpleApplication {

    private BulletAppState bulletAppState;

    private Player player;

    private PlayerStats playerStats;

    private SurvivalHud survivalHud;

    private Inventory inventory;

    private InventoryHud inventoryHud;

    private HotbarSystem hotbarSystem;

    private ToolView toolView;

    private ToolDurabilitySystem
            toolDurabilitySystem;


    private final List<HarvestableResource>
            resources =
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

        createLight();

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

        createInteractionSystem();

        createCrosshair();


        viewPort.setBackgroundColor(
                new ColorRGBA(
                        0.5f,
                        0.75f,
                        1f,
                        1f
                )
        );


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
                        0,
                        1.5f,
                        0
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
                        inputManager
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


    private void createInteractionSystem() {

        new InteractionSystem(
                cam,
                inputManager,
                rootNode,
                resources,
                inventory,
                hotbarSystem,
                toolView,
                toolDurabilitySystem
        );
    }


    private void createResources() {

        // ==========================
        // BÄUME
        // ==========================

        addResource(
                new Tree(
                        assetManager,
                        bulletAppState
                                .getPhysicsSpace(),
                        new Vector3f(
                                0,
                                0,
                                0
                        )
                )
        );


        addResource(
                new Tree(
                        assetManager,
                        bulletAppState
                                .getPhysicsSpace(),
                        new Vector3f(
                                5,
                                0,
                                -3
                        )
                )
        );


        addResource(
                new Tree(
                        assetManager,
                        bulletAppState
                                .getPhysicsSpace(),
                        new Vector3f(
                                -5,
                                0,
                                -4
                        )
                )
        );


        addResource(
                new Tree(
                        assetManager,
                        bulletAppState
                                .getPhysicsSpace(),
                        new Vector3f(
                                7,
                                0,
                                4
                        )
                )
        );


        addResource(
                new Tree(
                        assetManager,
                        bulletAppState
                                .getPhysicsSpace(),
                        new Vector3f(
                                -7,
                                0,
                                3
                        )
                )
        );


        // ==========================
        // STEINE
        // ==========================

        addResource(
                new Rock(
                        assetManager,
                        bulletAppState
                                .getPhysicsSpace(),
                        new Vector3f(
                                3,
                                0,
                                3
                        )
                )
        );


        addResource(
                new Rock(
                        assetManager,
                        bulletAppState
                                .getPhysicsSpace(),
                        new Vector3f(
                                -3,
                                0,
                                2
                        )
                )
        );


        addResource(
                new Rock(
                        assetManager,
                        bulletAppState
                                .getPhysicsSpace(),
                        new Vector3f(
                                4,
                                0,
                                -6
                        )
                )
        );


        addResource(
                new Rock(
                        assetManager,
                        bulletAppState
                                .getPhysicsSpace(),
                        new Vector3f(
                                -4,
                                0,
                                -7
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


        rootNode.attachChild(
                resource.getNode()
        );
    }


    private void createGround() {

        Box groundBox =
                new Box(
                        25f,
                        0.1f,
                        25f
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


        material.setColor(
                "Diffuse",
                new ColorRGBA(
                        0.25f,
                        0.6f,
                        0.25f,
                        1f
                )
        );


        material.setColor(
                "Ambient",
                new ColorRGBA(
                        0.25f,
                        0.6f,
                        0.25f,
                        1f
                )
        );


        ground.setMaterial(
                material
        );


        ground.setLocalTranslation(
                0,
                -0.1f,
                0
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


    private void createLight() {

        DirectionalLight sun =
                new DirectionalLight();


        sun.setDirection(
                new Vector3f(
                        -1,
                        -2,
                        -1
                ).normalizeLocal()
        );


        sun.setColor(
                ColorRGBA.White
        );


        rootNode.addLight(
                sun
        );


        AmbientLight ambient =
                new AmbientLight();


        ambient.setColor(
                ColorRGBA.White.mult(
                        0.4f
                )
        );


        rootNode.addLight(
                ambient
        );
    }


    @Override
    public void simpleUpdate(
            float tpf
    ) {

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
                toolView != null
        ) {

            toolView.update(
                    tpf
            );
        }
    }
}