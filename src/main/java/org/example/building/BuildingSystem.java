package org.example.building;

import com.jme3.asset.AssetManager;
import com.jme3.collision.CollisionResults;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;

import org.example.inventory.Inventory;
import org.example.inventory.ItemType;
import org.example.ui.InventoryMenuSystem;

import java.util.ArrayList;
import java.util.List;

public class BuildingSystem implements ActionListener {

    private static final String TOGGLE_BUILD =
            "ToggleBuildMode";

    private static final String PLACE_BUILDING =
            "PlaceBuilding";

    private static final String SELECT_FOUNDATION =
            "SelectFoundation";

    private static final String SELECT_WALL =
            "SelectWall";

    private static final String SELECT_DOOR_FRAME =
            "SelectDoorFrame";

    private static final String SELECT_DOOR =
            "SelectDoor";

    private static final String INTERACT_DOOR =
            "InteractDoor";

    private static final String SELECT_CEILING =
            "SelectCeiling";

    private static final String SELECT_ROOF =
            "SelectRoof";

    private static final String SELECT_STAIRS =
            "SelectStairs";

    private static final String ROTATE_BUILDING =
            "RotateBuilding";

    private static final String TOGGLE_DEMOLISH =
            "ToggleDemolishMode";


    public static final int FOUNDATION_WOOD_COST =
            5;

    public static final int WALL_WOOD_COST =
            3;

    public static final int DOOR_FRAME_WOOD_COST =
            4;

    public static final int DOOR_WOOD_COST =
            3;

    public static final int CEILING_WOOD_COST =
            4;

    public static final int ROOF_WOOD_COST =
            4;

    public static final int STAIRS_WOOD_COST =
            5;


    private static final float BUILD_DISTANCE =
            4f;

    private static final float GRID_SIZE =
            3f;

    private static final float DOOR_INTERACTION_DISTANCE =
            3.2f;

    private static final float DOOR_OPEN_ANGLE =
            -(float) Math.toRadians(90f);

    private static final float DOOR_ANIMATION_SPEED =
            4f;

    private static final float DOOR_HALF_WIDTH =
            1.0f;


    private enum BuildType {

        FOUNDATION,
        WALL,
        DOOR_FRAME,
        DOOR,
        CEILING,
        ROOF,
        STAIRS
    }


    private final AssetManager assetManager;

    private final Node rootNode;

    private final Camera camera;

    private final PhysicsSpace physicsSpace;

    private final Inventory inventory;

    private final InventoryMenuSystem inventoryMenuSystem;


    private final Geometry foundationPreview;

    private final Material foundationPreviewMaterial;


    private final Geometry wallPreview;

    private final Material wallPreviewMaterial;


    private final Node doorFramePreview;

    private final Material doorFramePreviewMaterial;

    private final Geometry doorPreview;

    private final Material doorPreviewMaterial;

    private final Geometry ceilingPreview;
    private final Material ceilingPreviewMaterial;

    private final Geometry roofPreview;
    private final Material roofPreviewMaterial;

    private final Geometry stairsPreview;
    private final Material stairsPreviewMaterial;


    private final BitmapText buildText;


    private final List<Geometry> placedFoundations =
            new ArrayList<>();

    private final List<Geometry> placedWalls =
            new ArrayList<>();

    private final List<Node> placedDoorFrames =
            new ArrayList<>();

    private final List<Geometry> placedDoors =
            new ArrayList<>();

    private final List<Geometry> placedCeilings =
            new ArrayList<>();

    private final List<Geometry> placedRoofs =
            new ArrayList<>();

    private final List<Geometry> placedStairs =
            new ArrayList<>();

    private final List<Quaternion> doorClosedRotations =
            new ArrayList<>();

    private final List<Boolean> doorOpenStates =
            new ArrayList<>();

    private final List<Boolean> doorTargetOpenStates =
            new ArrayList<>();

    private final List<Float> doorAnimationProgress =
            new ArrayList<>();


    private BuildType selectedBuildType =
            BuildType.FOUNDATION;


    private boolean active =
            false;

    private boolean demolishActive =
            false;


    private int nextFoundationId =
            1;

    private int nextWallId =
            1;

    private int nextDoorFrameId =
            1;

    private int nextDoorId =
            1;

    private int nextCeilingId =
            1;

    private int nextRoofId =
            1;

    private int nextStairsId =
            1;

    private int roofRotationSteps =
            0;

    private int stairsRotationSteps =
            0;

    private long lastDoorAnimationTimeNanos =
            System.nanoTime();


    public BuildingSystem(
            AssetManager assetManager,
            Node rootNode,
            Node guiNode,
            Camera camera,
            InputManager inputManager,
            PhysicsSpace physicsSpace,
            Inventory inventory,
            InventoryMenuSystem inventoryMenuSystem
    ) {

        this.assetManager =
                assetManager;

        this.rootNode =
                rootNode;

        this.camera =
                camera;

        this.physicsSpace =
                physicsSpace;

        this.inventory =
                inventory;

        this.inventoryMenuSystem =
                inventoryMenuSystem;


        // ==========================
        // FUNDAMENT-VORSCHAU
        // ==========================

        foundationPreview =
                new Geometry(
                        "FoundationPreview",
                        new Box(
                                1.5f,
                                0.12f,
                                1.5f
                        )
                );


        foundationPreviewMaterial =
                createPreviewMaterial();


        foundationPreview.setMaterial(
                foundationPreviewMaterial
        );


        foundationPreview.setQueueBucket(
                RenderQueue.Bucket.Transparent
        );


        foundationPreview.setCullHint(
                Spatial.CullHint.Always
        );


        rootNode.attachChild(
                foundationPreview
        );


        // ==========================
        // WAND-VORSCHAU
        // ==========================

        wallPreview =
                new Geometry(
                        "WallPreview",
                        new Box(
                                1.5f,
                                1.5f,
                                0.12f
                        )
                );


        wallPreviewMaterial =
                createPreviewMaterial();


        wallPreview.setMaterial(
                wallPreviewMaterial
        );


        wallPreview.setQueueBucket(
                RenderQueue.Bucket.Transparent
        );


        wallPreview.setCullHint(
                Spatial.CullHint.Always
        );


        rootNode.attachChild(
                wallPreview
        );


        // ==========================
        // TÜRRAHMEN-VORSCHAU
        // ==========================

        doorFramePreviewMaterial =
                createPreviewMaterial();


        doorFramePreview =
                createDoorFrameNode(
                        "DoorFramePreview",
                        doorFramePreviewMaterial
                );


        doorFramePreview.setQueueBucket(
                RenderQueue.Bucket.Transparent
        );


        doorFramePreview.setCullHint(
                Spatial.CullHint.Always
        );


        rootNode.attachChild(
                doorFramePreview
        );


        // ==========================
        // TÜR-VORSCHAU
        // ==========================

        doorPreviewMaterial =
                createPreviewMaterial();


        doorPreview =
                new Geometry(
                        "DoorPreview",
                        new Box(
                                1.0f,
                                1.22f,
                                0.10f
                        )
                );


        doorPreview.setMaterial(
                doorPreviewMaterial
        );


        doorPreview.setQueueBucket(
                RenderQueue.Bucket.Transparent
        );


        doorPreview.setCullHint(
                Spatial.CullHint.Always
        );


        rootNode.attachChild(
                doorPreview
        );


        // ==========================
        // DECKEN-VORSCHAU
        // ==========================

        ceilingPreviewMaterial = createPreviewMaterial();

        ceilingPreview = new Geometry(
                "CeilingPreview",
                new Box(1.5f, 0.12f, 1.5f)
        );

        ceilingPreview.setMaterial(ceilingPreviewMaterial);
        ceilingPreview.setQueueBucket(RenderQueue.Bucket.Transparent);
        ceilingPreview.setCullHint(Spatial.CullHint.Always);
        rootNode.attachChild(ceilingPreview);


        // ==========================
        // DACH-VORSCHAU
        // ==========================

        roofPreviewMaterial = createPreviewMaterial();

        roofPreview = new Geometry(
                "RoofPreview",
                new Box(1.72f, 0.12f, 1.5f)
        );

        roofPreview.setMaterial(roofPreviewMaterial);
        roofPreview.setQueueBucket(RenderQueue.Bucket.Transparent);
        roofPreview.setCullHint(Spatial.CullHint.Always);
        rootNode.attachChild(roofPreview);


        // ==========================
        // TREPPEN-VORSCHAU
        // ==========================

        stairsPreviewMaterial = createPreviewMaterial();

        stairsPreview = new Geometry(
                "StairsPreview",
                new Box(1.20f, 0.12f, 2.12f)
        );

        stairsPreview.setMaterial(stairsPreviewMaterial);
        stairsPreview.setQueueBucket(RenderQueue.Bucket.Transparent);
        stairsPreview.setCullHint(Spatial.CullHint.Always);
        rootNode.attachChild(stairsPreview);


        // ==========================
        // HUD
        // ==========================

        BitmapFont font =
                assetManager.loadFont(
                        "Interface/Fonts/Default.fnt"
                );


        buildText =
                new BitmapText(
                        font
                );


        buildText.setSize(
                18f
        );


        buildText.setColor(
                ColorRGBA.White
        );


        buildText.setLocalTranslation(
                20f,
                camera.getHeight() - 55f,
                20f
        );


        buildText.setCullHint(
                Spatial.CullHint.Always
        );


        guiNode.attachChild(
                buildText
        );


        // ==========================
        // INPUT
        // ==========================

        inputManager.addMapping(
                TOGGLE_BUILD,
                new KeyTrigger(
                        KeyInput.KEY_B
                )
        );


        inputManager.addMapping(
                SELECT_FOUNDATION,
                new KeyTrigger(
                        KeyInput.KEY_1
                )
        );


        inputManager.addMapping(
                SELECT_WALL,
                new KeyTrigger(
                        KeyInput.KEY_2
                )
        );


        inputManager.addMapping(
                SELECT_DOOR_FRAME,
                new KeyTrigger(
                        KeyInput.KEY_3
                )
        );


        inputManager.addMapping(
                SELECT_DOOR,
                new KeyTrigger(
                        KeyInput.KEY_4
                )
        );


        inputManager.addMapping(
                INTERACT_DOOR,
                new KeyTrigger(
                        KeyInput.KEY_E
                )
        );

        inputManager.addMapping(
                SELECT_CEILING,
                new KeyTrigger(
                        KeyInput.KEY_5
                )
        );

        inputManager.addMapping(
                SELECT_ROOF,
                new KeyTrigger(
                        KeyInput.KEY_6
                )
        );

        inputManager.addMapping(
                SELECT_STAIRS,
                new KeyTrigger(
                        KeyInput.KEY_7
                )
        );

        inputManager.addMapping(
                ROTATE_BUILDING,
                new KeyTrigger(
                        KeyInput.KEY_R
                )
        );


        inputManager.addMapping(
                TOGGLE_DEMOLISH,
                new KeyTrigger(
                        KeyInput.KEY_X
                )
        );


        inputManager.addMapping(
                PLACE_BUILDING,
                new MouseButtonTrigger(
                        MouseInput.BUTTON_LEFT
                )
        );


        inputManager.addListener(
                this,
                TOGGLE_BUILD,
                SELECT_FOUNDATION,
                SELECT_WALL,
                SELECT_DOOR_FRAME,
                SELECT_DOOR,
                SELECT_CEILING,
                SELECT_ROOF,
                SELECT_STAIRS,
                ROTATE_BUILDING,
                INTERACT_DOOR,
                TOGGLE_DEMOLISH,
                PLACE_BUILDING
        );
    }


    @Override
    public void onAction(
            String name,
            boolean isPressed,
            float tpf
    ) {

        if (
                name.equals(
                        TOGGLE_BUILD
                )
                        &&
                        isPressed
        ) {

            if (
                    inventoryMenuSystem.isOpen()
            ) {

                return;
            }


            if (demolishActive) {
                demolishActive = false;
            }

            setActive(
                    !active
            );


            return;
        }


        if (
                name.equals(
                        TOGGLE_DEMOLISH
                )
                        &&
                        isPressed
        ) {

            if (
                    inventoryMenuSystem.isOpen()
            ) {

                return;
            }

            demolishActive =
                    !demolishActive;

            if (demolishActive) {
                setActive(false);
                System.out.println("Abreißmodus aktiviert. Linksklick auf ein Bauteil zum Entfernen.");
            } else {
                System.out.println("Abreißmodus deaktiviert.");
            }

            return;
        }


        if (
                name.equals(
                        INTERACT_DOOR
                )
                        &&
                        isPressed
        ) {

            if (
                    !active
                            &&
                            !inventoryMenuSystem.isOpen()
            ) {

                interactWithDoor();
            }


            return;
        }


        if (
                demolishActive
                        &&
                        name.equals(
                                PLACE_BUILDING
                        )
                        &&
                        isPressed
        ) {

            demolishTarget();
            return;
        }


        if (
                !active
        ) {

            return;
        }


        if (
                inventoryMenuSystem.isOpen()
        ) {

            return;
        }


        if (
                name.equals(
                        SELECT_FOUNDATION
                )
                        &&
                        isPressed
        ) {

            selectedBuildType =
                    BuildType.FOUNDATION;


            return;
        }


        if (
                name.equals(
                        SELECT_WALL
                )
                        &&
                        isPressed
        ) {

            selectedBuildType =
                    BuildType.WALL;


            return;
        }


        if (
                name.equals(
                        SELECT_DOOR_FRAME
                )
                        &&
                        isPressed
        ) {

            selectedBuildType =
                    BuildType.DOOR_FRAME;


            return;
        }


        if (
                name.equals(
                        SELECT_DOOR
                )
                        &&
                        isPressed
        ) {

            selectedBuildType =
                    BuildType.DOOR;


            return;
        }


        if (
                name.equals(SELECT_CEILING)
                        &&
                        isPressed
        ) {
            selectedBuildType = BuildType.CEILING;
            return;
        }

        if (
                name.equals(SELECT_ROOF)
                        &&
                        isPressed
        ) {
            selectedBuildType = BuildType.ROOF;
            return;
        }

        if (
                name.equals(SELECT_STAIRS)
                        &&
                        isPressed
        ) {
            selectedBuildType = BuildType.STAIRS;
            return;
        }

        if (
                name.equals(ROTATE_BUILDING)
                        &&
                        isPressed
        ) {

            if (selectedBuildType == BuildType.ROOF) {
                roofRotationSteps =
                        (roofRotationSteps + 1) % 4;

                System.out.println(
                        "Dach gedreht: "
                                +
                                (roofRotationSteps * 90)
                                +
                                " Grad."
                );

                return;
            }

            if (selectedBuildType == BuildType.STAIRS) {
                stairsRotationSteps =
                        (stairsRotationSteps + 1) % 4;

                System.out.println(
                        "Treppe gedreht: "
                                +
                                (stairsRotationSteps * 90)
                                +
                                " Grad."
                );

                return;
            }
        }

        if (
                name.equals(
                        PLACE_BUILDING
                )
                        &&
                        isPressed
        ) {

            placeSelectedBuilding();
        }
    }


    public void update() {

        updateDoorAnimations();


        if (
                !active
        ) {

            return;
        }


        if (
                inventoryMenuSystem.isOpen()
        ) {

            hidePreviews();

            return;
        }


        switch (
                selectedBuildType
        ) {

            case FOUNDATION:

                updateFoundationPreview();

                break;


            case WALL:

                updateWallPreview();

                break;


            case DOOR_FRAME:

                updateDoorFramePreview();

                break;


            case DOOR:

                updateDoorPreview();

                break;

            case CEILING:

                updateCeilingPreview();

                break;

            case ROOF:

                updateRoofPreview();

                break;

            case STAIRS:

                updateStairsPreview();

                break;
        }


        updateHud();
    }


    private void updateFoundationPreview() {

        stairsPreview.setCullHint(Spatial.CullHint.Always);


        roofPreview.setCullHint(Spatial.CullHint.Always);


        wallPreview.setCullHint(
                Spatial.CullHint.Always
        );


        doorFramePreview.setCullHint(
                Spatial.CullHint.Always
        );


        doorPreview.setCullHint(
                Spatial.CullHint.Always
        );


        ceilingPreview.setCullHint(
                Spatial.CullHint.Always
        );


        foundationPreview.setCullHint(
                Spatial.CullHint.Inherit
        );


        Vector3f target =
                getHorizontalBuildTarget();


        float x =
                snap(
                        target.x
                );


        float z =
                snap(
                        target.z
                );


        foundationPreview.setLocalTranslation(
                x,
                0.12f,
                z
        );


        boolean valid =
                inventory.hasItem(
                        ItemType.WOOD,
                        FOUNDATION_WOOD_COST
                )
                        &&
                        !foundationExistsAt(
                                x,
                                z
                        );


        setPreviewColor(
                foundationPreviewMaterial,
                valid
        );
    }


    private void updateWallPreview() {

        stairsPreview.setCullHint(Spatial.CullHint.Always);


        roofPreview.setCullHint(Spatial.CullHint.Always);


        foundationPreview.setCullHint(
                Spatial.CullHint.Always
        );


        doorFramePreview.setCullHint(
                Spatial.CullHint.Always
        );


        doorPreview.setCullHint(
                Spatial.CullHint.Always
        );


        ceilingPreview.setCullHint(
                Spatial.CullHint.Always
        );


        EdgeTransform edge =
                calculateCurrentWallTransform();


        if (
                edge == null
        ) {

            wallPreview.setCullHint(
                    Spatial.CullHint.Always
            );


            return;
        }


        wallPreview.setCullHint(
                Spatial.CullHint.Inherit
        );


        wallPreview.setLocalTranslation(
                edge.position
        );


        wallPreview.setLocalRotation(
                edge.rotation
        );


        boolean valid =
                inventory.hasItem(
                        ItemType.WOOD,
                        WALL_WOOD_COST
                )
                        &&
                        !edgeOccupied(
                                edge.position
                        );


        setPreviewColor(
                wallPreviewMaterial,
                valid
        );
    }


    private void updateDoorFramePreview() {

        stairsPreview.setCullHint(Spatial.CullHint.Always);


        roofPreview.setCullHint(Spatial.CullHint.Always);


        foundationPreview.setCullHint(
                Spatial.CullHint.Always
        );


        wallPreview.setCullHint(
                Spatial.CullHint.Always
        );


        doorPreview.setCullHint(
                Spatial.CullHint.Always
        );


        ceilingPreview.setCullHint(
                Spatial.CullHint.Always
        );


        EdgeTransform edge =
                calculateCurrentEdgeTransform();


        if (
                edge == null
        ) {

            doorFramePreview.setCullHint(
                    Spatial.CullHint.Always
            );


            return;
        }


        doorFramePreview.setCullHint(
                Spatial.CullHint.Inherit
        );


        doorFramePreview.setLocalTranslation(
                edge.position
        );


        doorFramePreview.setLocalRotation(
                edge.rotation
        );


        boolean valid =
                inventory.hasItem(
                        ItemType.WOOD,
                        DOOR_FRAME_WOOD_COST
                )
                        &&
                        !edgeOccupied(
                                edge.position
                        );


        setDoorFramePreviewColor(
                valid
        );
    }


    private void updateDoorPreview() {

        stairsPreview.setCullHint(Spatial.CullHint.Always);


        roofPreview.setCullHint(Spatial.CullHint.Always);


        foundationPreview.setCullHint(
                Spatial.CullHint.Always
        );

        wallPreview.setCullHint(
                Spatial.CullHint.Always
        );

        doorFramePreview.setCullHint(
                Spatial.CullHint.Always
        );


        Node frame =
                getNearestDoorFrameToBuildTarget();


        if (
                frame == null
        ) {

            doorPreview.setCullHint(
                    Spatial.CullHint.Always
            );

            return;
        }


        doorPreview.setCullHint(
                Spatial.CullHint.Inherit
        );


        Vector3f position =
                frame.getLocalTranslation()
                        .clone();

        position.y -= 0.22f;


        doorPreview.setLocalTranslation(
                position
        );

        doorPreview.setLocalRotation(
                frame.getLocalRotation()
        );


        boolean valid =
                inventory.hasItem(
                        ItemType.WOOD,
                        DOOR_WOOD_COST
                )
                        &&
                        !doorExistsForFrame(
                                frame
                        );


        setPreviewColor(
                doorPreviewMaterial,
                valid
        );
    }


    private void updateCeilingPreview() {

        stairsPreview.setCullHint(Spatial.CullHint.Always);


        roofPreview.setCullHint(Spatial.CullHint.Always);


        foundationPreview.setCullHint(Spatial.CullHint.Always);
        wallPreview.setCullHint(Spatial.CullHint.Always);
        doorFramePreview.setCullHint(Spatial.CullHint.Always);
        doorPreview.setCullHint(Spatial.CullHint.Always);

        Geometry foundation = getNearestFoundationToBuildTarget();

        if (foundation == null) {
            ceilingPreview.setCullHint(Spatial.CullHint.Always);
            return;
        }

        Vector3f position = foundation.getLocalTranslation().clone();
        position.y = 3.12f;

        ceilingPreview.setLocalTranslation(position);
        ceilingPreview.setLocalRotation(Quaternion.IDENTITY);
        ceilingPreview.setCullHint(Spatial.CullHint.Inherit);

        boolean valid =
                inventory.hasItem(ItemType.WOOD, CEILING_WOOD_COST)
                        && !ceilingExistsAt(position.x, position.y, position.z);

        setPreviewColor(ceilingPreviewMaterial, valid);
    }


    private void updateRoofPreview() {

        stairsPreview.setCullHint(Spatial.CullHint.Always);


        foundationPreview.setCullHint(Spatial.CullHint.Always);
        wallPreview.setCullHint(Spatial.CullHint.Always);
        doorFramePreview.setCullHint(Spatial.CullHint.Always);
        doorPreview.setCullHint(Spatial.CullHint.Always);
        ceilingPreview.setCullHint(Spatial.CullHint.Always);

        Geometry ceiling = getNearestCeilingToBuildTarget();

        if (ceiling == null) {
            roofPreview.setCullHint(Spatial.CullHint.Always);
            return;
        }

        Vector3f position = ceiling.getLocalTranslation().clone();
        position.y += 1.0f;

        Quaternion rotation =
                createRoofRotation();

        roofPreview.setLocalTranslation(position);
        roofPreview.setLocalRotation(rotation);
        roofPreview.setCullHint(Spatial.CullHint.Inherit);

        boolean valid =
                inventory.hasItem(ItemType.WOOD, ROOF_WOOD_COST)
                        && !roofExistsAt(position.x, position.y, position.z);

        setPreviewColor(roofPreviewMaterial, valid);
    }


    private void updateStairsPreview() {

        foundationPreview.setCullHint(Spatial.CullHint.Always);
        wallPreview.setCullHint(Spatial.CullHint.Always);
        doorFramePreview.setCullHint(Spatial.CullHint.Always);
        doorPreview.setCullHint(Spatial.CullHint.Always);
        ceilingPreview.setCullHint(Spatial.CullHint.Always);
        roofPreview.setCullHint(Spatial.CullHint.Always);

        Geometry foundation =
                getNearestFoundationForStairs();

        if (foundation == null) {
            stairsPreview.setCullHint(Spatial.CullHint.Always);
            return;
        }

        Vector3f supportPosition =
                foundation.getLocalTranslation().clone();

        Quaternion yawRotation =
                createYawRotation(stairsRotationSteps);

        Vector3f uphillDirection =
                yawRotation.mult(
                        new Vector3f(0f, 0f, 1f)
                );

        /*
         * Die Treppe spannt jetzt exakt über eine 3x3-Bauzelle:
         * unten an einer Fundamentkante, oben an der gegenüberliegenden
         * Deckenkante. Deshalb liegt ihr Mittelpunkt horizontal exakt
         * auf dem Mittelpunkt des Fundaments.
         */
        Vector3f position =
                supportPosition.clone();

        position.y =
                1.62f;

        Quaternion rotation =
                createStairsRotation();

        stairsPreview.setLocalTranslation(position);
        stairsPreview.setLocalRotation(rotation);
        stairsPreview.setCullHint(Spatial.CullHint.Inherit);

        boolean valid =
                inventory.hasItem(ItemType.WOOD, STAIRS_WOOD_COST)
                        &&
                        !stairsExistsAt(position);

        setPreviewColor(
                stairsPreviewMaterial,
                valid
        );
    }


    private Geometry getNearestFoundationForStairs() {

        if (
                placedFoundations.isEmpty()
        ) {

            return null;
        }

        Vector3f target =
                getHorizontalBuildTarget();

        Geometry nearest =
                null;

        float nearestDistance =
                Float.MAX_VALUE;

        for (
                Geometry foundation
                :
                placedFoundations
        ) {

            Vector3f position =
                    foundation.getLocalTranslation();

            float dx =
                    position.x - target.x;

            float dz =
                    position.z - target.z;

            float horizontalDistance =
                    dx * dx
                            +
                            dz * dz;

            if (
                    horizontalDistance
                            <
                            nearestDistance
            ) {

                nearestDistance =
                        horizontalDistance;

                nearest =
                        foundation;
            }
        }

        /*
         * Treppen ragen bewusst vor das Fundament.
         * Deshalb braucht die Auswahl mehr Toleranz als Wände/Decken,
         * besonders wenn der Spieler auf Wand oder obere Kante zielt.
         */
        if (
                nearestDistance > 144f
        ) {

            return null;
        }

        return nearest;
    }


    private Quaternion createRoofRotation() {

        Quaternion slopeRotation =
                new Quaternion();

        slopeRotation.fromAngles(
                0f,
                0f,
                (float) Math.toRadians(30f)
        );

        Quaternion yawRotation =
                createYawRotation(roofRotationSteps);

        return yawRotation.mult(
                slopeRotation
        );
    }


    private Quaternion createStairsRotation() {

        Quaternion slopeRotation =
                new Quaternion();

        slopeRotation.fromAngles(
                -(float) Math.toRadians(45f),
                0f,
                0f
        );

        Quaternion yawRotation =
                createYawRotation(stairsRotationSteps);

        return yawRotation.mult(
                slopeRotation
        );
    }


    private Quaternion createYawRotation(
            int rotationSteps
    ) {

        Quaternion rotation =
                new Quaternion();

        rotation.fromAngles(
                0f,
                (float) Math.toRadians(
                        rotationSteps * 90f
                ),
                0f
        );

        return rotation;
    }


    private boolean stairsExistsAt(
            Vector3f position
    ) {

        for (
                Geometry stairs
                :
                placedStairs
        ) {

            if (
                    stairs.getLocalTranslation()
                            .distanceSquared(
                                    position
                            )
                            <
                            0.05f
            ) {

                return true;
            }
        }

        return false;
    }


    private Geometry getNearestCeilingToBuildTarget() {

        if (placedCeilings.isEmpty()) {
            return null;
        }

        Vector3f target = getHorizontalBuildTarget();
        Geometry nearest = null;
        float nearestDistance = Float.MAX_VALUE;

        for (Geometry ceiling : placedCeilings) {
            float distance = ceiling.getLocalTranslation().distanceSquared(target);

            if (distance < nearestDistance) {
                nearest = ceiling;
                nearestDistance = distance;
            }
        }

        if (nearestDistance > 64f) {
            return null;
        }

        return nearest;
    }


    private boolean roofExistsAt(float x, float y, float z) {

        for (Geometry roof : placedRoofs) {
            Vector3f position = roof.getLocalTranslation();

            if (Math.abs(position.x - x) < 0.1f
                    && Math.abs(position.y - y) < 0.1f
                    && Math.abs(position.z - z) < 0.1f) {
                return true;
            }
        }

        return false;
    }


    private boolean ceilingExistsAt(float x, float y, float z) {

        for (Geometry ceiling : placedCeilings) {
            Vector3f position = ceiling.getLocalTranslation();

            if (Math.abs(position.x - x) < 0.1f
                    && Math.abs(position.y - y) < 0.1f
                    && Math.abs(position.z - z) < 0.1f) {
                return true;
            }
        }

        return false;
    }


    private Node getNearestDoorFrameToBuildTarget() {

        if (
                placedDoorFrames.isEmpty()
        ) {

            return null;
        }


        Vector3f target =
                getHorizontalBuildTarget();

        Node nearest =
                null;

        float nearestDistance =
                Float.MAX_VALUE;


        for (
                Node frame
                :
                placedDoorFrames
        ) {

            float distance =
                    frame.getLocalTranslation()
                            .distanceSquared(
                                    target
                            );

            if (
                    distance < nearestDistance
            ) {

                nearest =
                        frame;

                nearestDistance =
                        distance;
            }
        }


        if (
                nearestDistance > 36f
        ) {

            return null;
        }


        return nearest;
    }


    private boolean doorExistsForFrame(
            Node frame
    ) {

        Vector3f framePosition =
                frame.getLocalTranslation();


        for (
                Geometry door
                :
                placedDoors
        ) {

            Vector3f doorFramePosition =
                    door.getUserData(
                            "framePosition"
                    );

            if (
                    doorFramePosition != null
                            &&
                            doorFramePosition.distanceSquared(
                                    framePosition
                            ) < 0.05f
            ) {

                return true;
            }
        }


        return false;
    }


    private void interactWithDoor() {

        if (
                placedDoors.isEmpty()
        ) {

            return;
        }


        Ray ray =
                new Ray(
                        camera.getLocation(),
                        camera.getDirection()
                );


        int nearestDoorIndex =
                -1;

        float nearestDistance =
                Float.MAX_VALUE;


        for (
                int i = 0;
                i < placedDoors.size();
                i++
        ) {

            Geometry door =
                    placedDoors.get(
                            i
                    );


            CollisionResults results =
                    new CollisionResults();


            door.collideWith(
                    ray,
                    results
            );


            if (
                    results.size() == 0
            ) {

                continue;
            }


            float distance =
                    results
                            .getClosestCollision()
                            .getDistance();


            if (
                    distance <= DOOR_INTERACTION_DISTANCE
                            &&
                            distance < nearestDistance
            ) {

                nearestDistance =
                        distance;

                nearestDoorIndex =
                        i;
            }
        }


        if (
                nearestDoorIndex < 0
        ) {

            return;
        }


        boolean newTargetOpen =
                !doorTargetOpenStates.get(
                        nearestDoorIndex
                );


        doorTargetOpenStates.set(
                nearestDoorIndex,
                newTargetOpen
        );


        System.out.println(
                newTargetOpen
                        ?
                        "Tür wird geöffnet."
                        :
                        "Tür wird geschlossen."
        );
    }


    private void updateDoorAnimations() {

        long now =
                System.nanoTime();


        float deltaSeconds =
                (now - lastDoorAnimationTimeNanos)
                        /
                        1_000_000_000f;


        lastDoorAnimationTimeNanos =
                now;


        deltaSeconds =
                Math.min(
                        deltaSeconds,
                        0.05f
                );


        if (
                deltaSeconds <= 0f
        ) {

            return;
        }


        for (
                int i = 0;
                i < placedDoors.size();
                i++
        ) {

            float currentProgress =
                    doorAnimationProgress.get(
                            i
                    );


            boolean targetOpen =
                    doorTargetOpenStates.get(
                            i
                    );


            float targetProgress =
                    targetOpen
                            ?
                            1f
                            :
                            0f;


            if (
                    Math.abs(
                            currentProgress - targetProgress
                    ) < 0.0001f
            ) {

                continue;
            }


            float step =
                    DOOR_ANIMATION_SPEED
                            *
                            deltaSeconds;


            if (
                    currentProgress < targetProgress
            ) {

                currentProgress =
                        Math.min(
                                targetProgress,
                                currentProgress + step
                        );
            }

            else {

                currentProgress =
                        Math.max(
                                targetProgress,
                                currentProgress - step
                        );
            }


            doorAnimationProgress.set(
                    i,
                    currentProgress
            );


            applyDoorTransform(
                    i,
                    currentProgress
            );


            if (
                    Math.abs(
                            currentProgress - targetProgress
                    ) < 0.0001f
            ) {

                doorOpenStates.set(
                        i,
                        targetOpen
                );
            }
        }
    }


    private void applyDoorTransform(
            int index,
            float openProgress
    ) {

        Geometry door =
                placedDoors.get(
                        index
                );


        Vector3f framePosition =
                door.getUserData(
                        "framePosition"
                );


        Quaternion closedRotation =
                doorClosedRotations.get(
                        index
                );


        if (
                framePosition == null
        ) {

            return;
        }


        Vector3f closedCenter =
                framePosition.clone();

        closedCenter.y -=
                0.22f;


        Vector3f hingeOffset =
                closedRotation.mult(
                        new Vector3f(
                                -DOOR_HALF_WIDTH,
                                0f,
                                0f
                        )
                );


        Vector3f hingePosition =
                closedCenter.add(
                        hingeOffset
                );


        Quaternion swingRotation =
                new Quaternion();

        swingRotation.fromAngleAxis(
                DOOR_OPEN_ANGLE
                        *
                        openProgress,
                Vector3f.UNIT_Y
        );


        Quaternion currentRotation =
                closedRotation.mult(
                        swingRotation
                );


        Vector3f centerFromHinge =
                currentRotation.mult(
                        new Vector3f(
                                DOOR_HALF_WIDTH,
                                0f,
                                0f
                        )
                );


        Vector3f currentPosition =
                hingePosition.add(
                        centerFromHinge
                );


        door.setLocalTranslation(
                currentPosition
        );

        door.setLocalRotation(
                currentRotation
        );


        RigidBodyControl physics =
                door.getControl(
                        RigidBodyControl.class
                );


        if (
                physics != null
        ) {

            physics.setPhysicsLocation(
                    door.getWorldTranslation()
            );

            physics.setPhysicsRotation(
                    door.getWorldRotation()
            );
        }
    }


    private EdgeTransform calculateCurrentWallTransform() {

        EdgeTransform stackedWall =
                getStackedWallTransformFromCrosshair();

        if (
                stackedWall != null
        ) {

            return stackedWall;
        }

        return calculateCurrentEdgeTransform();
    }


    private EdgeTransform getStackedWallTransformFromCrosshair() {

        if (
                placedWalls.isEmpty()
        ) {

            return null;
        }

        Ray ray =
                new Ray(
                        camera.getLocation(),
                        camera.getDirection()
                );

        Geometry nearestWall =
                null;

        float nearestDistance =
                Float.MAX_VALUE;

        for (
                Geometry wall
                :
                placedWalls
        ) {

            CollisionResults results =
                    new CollisionResults();

            wall.collideWith(
                    ray,
                    results
            );

            if (
                    results.size() == 0
            ) {

                continue;
            }

            float distance =
                    results
                            .getClosestCollision()
                            .getDistance();

            if (
                    distance <= BUILD_DISTANCE
                            &&
                            distance < nearestDistance
            ) {

                nearestDistance =
                        distance;

                nearestWall =
                        wall;
            }
        }

        if (
                nearestWall == null
        ) {

            return null;
        }

        Vector3f position =
                nearestWall
                        .getLocalTranslation()
                        .clone();

        position.y +=
                3f;

        Quaternion rotation =
                nearestWall
                        .getLocalRotation()
                        .clone();

        return new EdgeTransform(
                position,
                rotation
        );
    }


    private EdgeTransform calculateCurrentEdgeTransform() {

        Vector3f supportPosition =
                getNearestWallSupportPosition();


        if (
                supportPosition == null
        ) {

            return null;
        }


        Vector3f target =
                getHorizontalBuildTarget();


        float dx =
                target.x - supportPosition.x;


        float dz =
                target.z - supportPosition.z;


        Vector3f position =
                new Vector3f();


        Quaternion rotation =
                new Quaternion();


        float wallCenterY =
                supportPosition.y + 1.5f;


        if (
                Math.abs(dx)
                        >
                        Math.abs(dz)
        ) {

            float direction =
                    dx >= 0f
                            ?
                            1f
                            :
                            -1f;


            position.set(
                    supportPosition.x
                            +
                            direction * 1.5f,
                    wallCenterY,
                    supportPosition.z
            );


            rotation.fromAngles(
                    0f,
                    (float) Math.toRadians(
                            90f
                    ),
                    0f
            );
        }

        else {

            float direction =
                    dz >= 0f
                            ?
                            1f
                            :
                            -1f;


            position.set(
                    supportPosition.x,
                    wallCenterY,
                    supportPosition.z
                            +
                            direction * 1.5f
            );
        }


        return new EdgeTransform(
                position,
                rotation
        );
    }


    private Vector3f getNearestWallSupportPosition() {

        Vector3f target =
                getHorizontalBuildTarget();


        Vector3f nearestPosition =
                null;


        float nearestDistance =
                Float.MAX_VALUE;


        for (
                Geometry foundation
                :
                placedFoundations
        ) {

            Vector3f position =
                    foundation.getLocalTranslation();


            float distance =
                    position.distanceSquared(
                            target
                    );


            if (
                    distance < nearestDistance
            ) {

                nearestDistance =
                        distance;

                nearestPosition =
                        position;
            }
        }


        for (
                Geometry ceiling
                :
                placedCeilings
        ) {

            Vector3f position =
                    ceiling.getLocalTranslation();


            float distance =
                    position.distanceSquared(
                            target
                    );


            if (
                    distance < nearestDistance
            ) {

                nearestDistance =
                        distance;

                nearestPosition =
                        position;
            }
        }


        if (
                nearestPosition == null
                        ||
                        nearestDistance > 64f
        ) {

            return null;
        }


        return nearestPosition.clone();
    }


    private Vector3f getHorizontalBuildTarget() {

        Vector3f direction =
                camera.getDirection()
                        .clone();


        direction.y =
                0f;


        if (
                direction.lengthSquared()
                        <
                        0.001f
        ) {

            direction.set(
                    0f,
                    0f,
                    1f
            );
        }


        direction.normalizeLocal();


        return camera.getLocation()
                .add(
                        direction.mult(
                                BUILD_DISTANCE
                        )
                );
    }


    private Geometry getNearestFoundationToBuildTarget() {

        if (
                placedFoundations.isEmpty()
        ) {

            return null;
        }


        Vector3f target =
                getHorizontalBuildTarget();


        Geometry nearest =
                null;


        float nearestDistance =
                Float.MAX_VALUE;


        for (
                Geometry foundation
                :
                placedFoundations
        ) {

            float distance =
                    foundation
                            .getLocalTranslation()
                            .distanceSquared(
                                    target
                            );


            if (
                    distance < nearestDistance
            ) {

                nearest =
                        foundation;


                nearestDistance =
                        distance;
            }
        }


        if (
                nearestDistance > 64f
        ) {

            return null;
        }


        return nearest;
    }


    private float snap(
            float value
    ) {

        return Math.round(
                value / GRID_SIZE
        )
                *
                GRID_SIZE;
    }


    private boolean foundationExistsAt(
            float x,
            float z
    ) {

        for (
                Geometry foundation
                :
                placedFoundations
        ) {

            Vector3f position =
                    foundation.getLocalTranslation();


            if (
                    Math.abs(
                            position.x - x
                    )
                            <
                            0.1f

                            &&

                            Math.abs(
                                    position.z - z
                            )
                                    <
                                    0.1f
            ) {

                return true;
            }
        }


        return false;
    }


    private boolean edgeOccupied(
            Vector3f position
    ) {

        return wallExistsNear(
                position
        )
                ||
                doorFrameExistsNear(
                        position
                );
    }


    private boolean wallExistsNear(
            Vector3f position
    ) {

        for (
                Geometry wall
                :
                placedWalls
        ) {

            if (
                    wall.getLocalTranslation()
                            .distanceSquared(
                                    position
                            )
                            <
                            0.05f
            ) {

                return true;
            }
        }


        return false;
    }


    private boolean doorFrameExistsNear(
            Vector3f position
    ) {

        for (
                Node frame
                :
                placedDoorFrames
        ) {

            if (
                    frame.getLocalTranslation()
                            .distanceSquared(
                                    position
                            )
                            <
                            0.05f
            ) {

                return true;
            }
        }


        return false;
    }


    private void setPreviewColor(
            Material material,
            boolean valid
    ) {

        ColorRGBA color =
                valid
                        ?
                        new ColorRGBA(
                                0.15f,
                                1f,
                                0.25f,
                                0.42f
                        )
                        :
                        new ColorRGBA(
                                1f,
                                0.12f,
                                0.12f,
                                0.42f
                        );


        material.setColor(
                "Color",
                color
        );
    }


    private void setDoorFramePreviewColor(
            boolean valid
    ) {

        setPreviewColor(
                doorFramePreviewMaterial,
                valid
        );
    }


    private void updateHud() {

        int wood =
                inventory.getAmount(
                        ItemType.WOOD
                );


        String selectedName;

        int cost;


        switch (
                selectedBuildType
        ) {

            case FOUNDATION:

                selectedName =
                        "Fundament";

                cost =
                        FOUNDATION_WOOD_COST;

                break;


            case WALL:

                selectedName =
                        "Wand";

                cost =
                        WALL_WOOD_COST;

                break;


            case DOOR_FRAME:

                selectedName =
                        "Türrahmen";

                cost =
                        DOOR_FRAME_WOOD_COST;

                break;


            case DOOR:

                selectedName =
                        "Tür";

                cost =
                        DOOR_WOOD_COST;

                break;

            case CEILING:

                selectedName =
                        "Decke";

                cost =
                        CEILING_WOOD_COST;

                break;

            case ROOF:

                selectedName =
                        "Schrägdach";

                cost =
                        ROOF_WOOD_COST;

                break;

            default:

                selectedName =
                        "Treppe";

                cost =
                        STAIRS_WOOD_COST;

                break;
        }


        buildText.setText(
                "BAUMODUS | [1] Fundament | [2] Wand | [3] Türrahmen | [4] Tür | [5] Decke | [6] Schrägdach | [7] Treppe | "
                        +
                        selectedName
                        +
                        ": "
                        +
                        cost
                        +
                        " Holz | Holz: "
                        +
                        wood
                        +
                        " | Linksklick = Bauen | B = Beenden"
        );
    }


    private void placeSelectedBuilding() {

        switch (
                selectedBuildType
        ) {

            case FOUNDATION:

                placeFoundation();

                break;


            case WALL:

                placeWall();

                break;


            case DOOR_FRAME:

                placeDoorFrame();

                break;


            case DOOR:

                placeDoor();

                break;

            case CEILING:

                placeCeiling();

                break;

            case ROOF:

                placeRoof();

                break;

            case STAIRS:

                placeStairs();

                break;
        }
    }


    private void placeFoundation() {

        if (
                !inventory.hasItem(
                        ItemType.WOOD,
                        FOUNDATION_WOOD_COST
                )
        ) {

            return;
        }


        Vector3f position =
                foundationPreview
                        .getLocalTranslation()
                        .clone();


        if (
                foundationExistsAt(
                        position.x,
                        position.z
                )
        ) {

            return;
        }


        createAndAttachFoundation(
                position
        );


        inventory.removeItem(
                ItemType.WOOD,
                FOUNDATION_WOOD_COST
        );


        System.out.println(
                "Fundament gebaut."
        );
    }


    private void placeWall() {

        EdgeTransform edge =
                calculateCurrentWallTransform();


        if (
                edge == null
                        ||
                        edgeOccupied(
                                edge.position
                        )
                        ||
                        !inventory.hasItem(
                                ItemType.WOOD,
                                WALL_WOOD_COST
                        )
        ) {

            return;
        }


        createAndAttachWall(
                edge.position,
                edge.rotation
        );


        inventory.removeItem(
                ItemType.WOOD,
                WALL_WOOD_COST
        );


        System.out.println(
                "Wand gebaut."
        );
    }


    private void placeDoorFrame() {

        EdgeTransform edge =
                calculateCurrentEdgeTransform();


        if (
                edge == null
                        ||
                        edgeOccupied(
                                edge.position
                        )
                        ||
                        !inventory.hasItem(
                                ItemType.WOOD,
                                DOOR_FRAME_WOOD_COST
                        )
        ) {

            return;
        }


        createAndAttachDoorFrame(
                edge.position,
                edge.rotation
        );


        inventory.removeItem(
                ItemType.WOOD,
                DOOR_FRAME_WOOD_COST
        );


        System.out.println(
                "Türrahmen gebaut."
        );
    }


    private void placeDoor() {

        Node frame =
                getNearestDoorFrameToBuildTarget();


        if (
                frame == null
                        ||
                        doorExistsForFrame(
                                frame
                        )
                        ||
                        !inventory.hasItem(
                                ItemType.WOOD,
                                DOOR_WOOD_COST
                        )
        ) {

            return;
        }


        createAndAttachDoor(
                frame.getLocalTranslation(),
                frame.getLocalRotation(),
                frame.getLocalTranslation(),
                false
        );


        inventory.removeItem(
                ItemType.WOOD,
                DOOR_WOOD_COST
        );


        System.out.println(
                "Tür gebaut."
        );
    }


    private void placeCeiling() {

        Geometry foundation = getNearestFoundationToBuildTarget();

        if (foundation == null
                || !inventory.hasItem(ItemType.WOOD, CEILING_WOOD_COST)) {
            return;
        }

        Vector3f position = foundation.getLocalTranslation().clone();
        position.y = 3.12f;

        if (ceilingExistsAt(position.x, position.y, position.z)) {
            return;
        }

        createAndAttachCeiling(position);
        inventory.removeItem(ItemType.WOOD, CEILING_WOOD_COST);
        System.out.println("Decke gebaut.");
    }


    private void placeRoof() {

        Geometry ceiling = getNearestCeilingToBuildTarget();

        if (ceiling == null
                || !inventory.hasItem(ItemType.WOOD, ROOF_WOOD_COST)) {
            return;
        }

        Vector3f position = ceiling.getLocalTranslation().clone();
        position.y += 1.0f;

        if (roofExistsAt(position.x, position.y, position.z)) {
            return;
        }

        Quaternion rotation =
                createRoofRotation();

        createAndAttachRoof(position, rotation);
        inventory.removeItem(ItemType.WOOD, ROOF_WOOD_COST);
        System.out.println("Schrägdach gebaut.");
    }


    private void placeStairs() {

        Geometry foundation =
                getNearestFoundationForStairs();

        if (
                foundation == null
                        ||
                        !inventory.hasItem(
                                ItemType.WOOD,
                                STAIRS_WOOD_COST
                        )
        ) {

            return;
        }

        Vector3f supportPosition =
                foundation.getLocalTranslation().clone();

        Quaternion yawRotation =
                createYawRotation(
                        stairsRotationSteps
                );

        Vector3f uphillDirection =
                yawRotation.mult(
                        new Vector3f(
                                0f,
                                0f,
                                1f
                        )
                );

        /*
         * Gleicher Anker wie bei der Vorschau:
         * Mittelpunkt der Treppe liegt auf dem Fundamentmittelpunkt.
         */
        Vector3f position =
                supportPosition.clone();

        position.y =
                1.62f;

        if (
                stairsExistsAt(
                        position
                )
        ) {

            return;
        }

        createAndAttachStairs(
                position,
                createStairsRotation()
        );

        inventory.removeItem(
                ItemType.WOOD,
                STAIRS_WOOD_COST
        );

        System.out.println(
                "Treppe gebaut."
        );
    }


    private void createAndAttachStairs(
            Vector3f position,
            Quaternion rotation
    ) {

        Geometry stairs =
                new Geometry(
                        "Stairs_"
                                +
                                nextStairsId,
                        new Box(
                                1.20f,
                                0.12f,
                                2.35f
                        )
                );

        stairs.setMaterial(
                createWoodMaterial()
        );

        stairs.setLocalTranslation(
                position
        );

        stairs.setLocalRotation(
                rotation
        );

        rootNode.attachChild(
                stairs
        );

        addPhysics(
                stairs
        );

        placedStairs.add(
                stairs
        );

        nextStairsId++;
    }


    private void createAndAttachRoof(
            Vector3f position,
            Quaternion rotation
    ) {

        Geometry roof = new Geometry(
                "Roof_" + nextRoofId,
                new Box(1.72f, 0.12f, 1.5f)
        );

        roof.setMaterial(createWoodMaterial());
        roof.setLocalTranslation(position);
        roof.setLocalRotation(rotation);
        rootNode.attachChild(roof);
        addPhysics(roof);
        placedRoofs.add(roof);
        nextRoofId++;
    }


    private void createAndAttachCeiling(Vector3f position) {

        Geometry ceiling = new Geometry(
                "Ceiling_" + nextCeilingId,
                new Box(1.5f, 0.12f, 1.5f)
        );

        ceiling.setMaterial(createWoodMaterial());
        ceiling.setLocalTranslation(position);
        rootNode.attachChild(ceiling);
        addPhysics(ceiling);
        placedCeilings.add(ceiling);
        nextCeilingId++;
    }


    private void createAndAttachFoundation(
            Vector3f position
    ) {

        Geometry foundation =
                new Geometry(
                        "Foundation_"
                                +
                                nextFoundationId,
                        new Box(
                                1.5f,
                                0.12f,
                                1.5f
                        )
                );


        foundation.setMaterial(
                createWoodMaterial()
        );


        foundation.setLocalTranslation(
                position
        );


        rootNode.attachChild(
                foundation
        );


        addPhysics(
                foundation
        );


        placedFoundations.add(
                foundation
        );


        nextFoundationId++;
    }


    private void createAndAttachWall(
            Vector3f position,
            Quaternion rotation
    ) {

        Geometry wall =
                new Geometry(
                        "Wall_"
                                +
                                nextWallId,
                        new Box(
                                1.5f,
                                1.5f,
                                0.12f
                        )
                );


        wall.setMaterial(
                createWoodMaterial()
        );


        wall.setLocalTranslation(
                position
        );


        wall.setLocalRotation(
                rotation
        );


        rootNode.attachChild(
                wall
        );


        addPhysics(
                wall
        );


        placedWalls.add(
                wall
        );


        nextWallId++;
    }


    private void createAndAttachDoorFrame(
            Vector3f position,
            Quaternion rotation
    ) {

        Node frame =
                createDoorFrameNode(
                        "DoorFrame_"
                                +
                                nextDoorFrameId,
                        createWoodMaterial()
                );


        frame.setLocalTranslation(
                position
        );


        frame.setLocalRotation(
                rotation
        );


        rootNode.attachChild(
                frame
        );


        addPhysicsToDoorFrame(
                frame
        );


        placedDoorFrames.add(
                frame
        );


        nextDoorFrameId++;
    }


    private void createAndAttachDoor(
            Vector3f framePosition,
            Quaternion closedRotation,
            Vector3f savedFramePosition,
            boolean open
    ) {

        Geometry door =
                new Geometry(
                        "Door_"
                                +
                                nextDoorId,
                        new Box(
                                DOOR_HALF_WIDTH,
                                1.22f,
                                0.10f
                        )
                );


        door.setMaterial(
                createWoodMaterial()
        );


        door.setUserData(
                "framePosition",
                savedFramePosition.clone()
        );


        rootNode.attachChild(
                door
        );

        addPhysics(
                door
        );

        placedDoors.add(
                door
        );

        doorClosedRotations.add(
                closedRotation.clone()
        );

        doorOpenStates.add(
                open
        );

        doorTargetOpenStates.add(
                open
        );

        doorAnimationProgress.add(
                open
                        ?
                        1f
                        :
                        0f
        );


        applyDoorTransform(
                placedDoors.size() - 1,
                open
                        ?
                        1f
                        :
                        0f
        );


        nextDoorId++;
    }


    private Node createDoorFrameNode(
            String name,
            Material material
    ) {

        Node frame =
                new Node(
                        name
                );


        Geometry leftPost =
                new Geometry(
                        name + "_Left",
                        new Box(
                                0.22f,
                                1.5f,
                                0.12f
                        )
                );


        leftPost.setMaterial(
                material
        );


        leftPost.setLocalTranslation(
                -1.28f,
                0f,
                0f
        );


        frame.attachChild(
                leftPost
        );


        Geometry rightPost =
                new Geometry(
                        name + "_Right",
                        new Box(
                                0.22f,
                                1.5f,
                                0.12f
                        )
                );


        rightPost.setMaterial(
                material
        );


        rightPost.setLocalTranslation(
                1.28f,
                0f,
                0f
        );


        frame.attachChild(
                rightPost
        );


        Geometry topBeam =
                new Geometry(
                        name + "_Top",
                        new Box(
                                1.06f,
                                0.22f,
                                0.12f
                        )
                );


        topBeam.setMaterial(
                material
        );


        topBeam.setLocalTranslation(
                0f,
                1.28f,
                0f
        );


        frame.attachChild(
                topBeam
        );


        return frame;
    }


    private void addPhysics(
            Geometry geometry
    ) {

        RigidBodyControl physics =
                new RigidBodyControl(
                        0f
                );


        geometry.addControl(
                physics
        );


        physicsSpace.add(
                physics
        );
    }


    private void addPhysicsToDoorFrame(
            Node frame
    ) {

        for (
                Spatial child
                :
                frame.getChildren()
        ) {

            if (
                    child instanceof Geometry
            ) {

                addPhysics(
                        (Geometry) child
                );
            }
        }
    }


    private Material createWoodMaterial() {

        Material material =
                new Material(
                        assetManager,
                        "Common/MatDefs/Light/Lighting.j3md"
                );


        material.setBoolean(
                "UseMaterialColors",
                true
        );


        ColorRGBA color =
                new ColorRGBA(
                        0.42f,
                        0.25f,
                        0.10f,
                        1f
                );


        material.setColor(
                "Diffuse",
                color
        );


        material.setColor(
                "Ambient",
                color
        );


        return material;
    }


    private Material createPreviewMaterial() {

        Material material =
                new Material(
                        assetManager,
                        "Common/MatDefs/Misc/Unshaded.j3md"
                );


        material.setColor(
                "Color",
                new ColorRGBA(
                        0.15f,
                        1f,
                        0.25f,
                        0.42f
                )
        );


        material.getAdditionalRenderState()
                .setBlendMode(
                        RenderState.BlendMode.Alpha
                );


        material.getAdditionalRenderState()
                .setDepthWrite(
                        false
                );


        return material;
    }


    private void hidePreviews() {

        foundationPreview.setCullHint(
                Spatial.CullHint.Always
        );


        wallPreview.setCullHint(
                Spatial.CullHint.Always
        );


        doorFramePreview.setCullHint(
                Spatial.CullHint.Always
        );


        doorPreview.setCullHint(
                Spatial.CullHint.Always
        );


        ceilingPreview.setCullHint(
                Spatial.CullHint.Always
        );

        roofPreview.setCullHint(
                Spatial.CullHint.Always
        );

        stairsPreview.setCullHint(
                Spatial.CullHint.Always
        );
    }


    public void setActive(
            boolean active
    ) {

        this.active =
                active;


        if (
                active
        ) {

            buildText.setCullHint(
                    Spatial.CullHint.Inherit
            );
        }

        else {

            hidePreviews();


            buildText.setCullHint(
                    Spatial.CullHint.Always
            );
        }
    }


    public boolean isActive() {

        return active;
    }


    // =========================================================
    // SAVE API
    // =========================================================

    public int getFoundationCount() {

        return placedFoundations.size();
    }


    public Vector3f getFoundationPosition(
            int index
    ) {

        return placedFoundations
                .get(index)
                .getLocalTranslation()
                .clone();
    }


    public int getWallCount() {

        return placedWalls.size();
    }


    public Vector3f getWallPosition(
            int index
    ) {

        return placedWalls
                .get(index)
                .getLocalTranslation()
                .clone();
    }


    public Quaternion getWallRotation(
            int index
    ) {

        return placedWalls
                .get(index)
                .getLocalRotation()
                .clone();
    }


    public int getDoorFrameCount() {

        return placedDoorFrames.size();
    }


    public Vector3f getDoorFramePosition(
            int index
    ) {

        return placedDoorFrames
                .get(index)
                .getLocalTranslation()
                .clone();
    }


    public Quaternion getDoorFrameRotation(
            int index
    ) {

        return placedDoorFrames
                .get(index)
                .getLocalRotation()
                .clone();
    }


    public int getDoorCount() {

        return placedDoors.size();
    }


    public Vector3f getDoorParentFramePosition(
            int index
    ) {

        Vector3f framePosition =
                placedDoors
                        .get(index)
                        .getUserData(
                                "framePosition"
                        );

        return framePosition.clone();
    }


    public Quaternion getDoorRotation(
            int index
    ) {

        return doorClosedRotations
                .get(index)
                .clone();
    }


    public boolean isDoorOpen(
            int index
    ) {

        return doorTargetOpenStates.get(
                index
        );
    }


    public int getCeilingCount() {
        return placedCeilings.size();
    }


    public Vector3f getCeilingPosition(int index) {
        return placedCeilings.get(index).getLocalTranslation().clone();
    }


    public int getRoofCount() {
        return placedRoofs.size();
    }


    public Vector3f getRoofPosition(int index) {
        return placedRoofs.get(index).getLocalTranslation().clone();
    }


    public Quaternion getRoofRotation(int index) {
        return placedRoofs.get(index).getLocalRotation().clone();
    }


    public int getStairsCount() {
        return placedStairs.size();
    }


    public Vector3f getStairsPosition(int index) {
        return placedStairs.get(index).getLocalTranslation().clone();
    }


    public Quaternion getStairsRotation(int index) {
        return placedStairs.get(index).getLocalRotation().clone();
    }


    public void clearBuildings() {

        for (
                Geometry foundation
                :
                placedFoundations
        ) {

            removeGeometryWithPhysics(
                    foundation
            );
        }


        for (
                Geometry wall
                :
                placedWalls
        ) {

            removeGeometryWithPhysics(
                    wall
            );
        }


        for (
                Node frame
                :
                placedDoorFrames
        ) {

            removeDoorFrameWithPhysics(
                    frame
            );
        }


        for (
                Geometry door
                :
                placedDoors
        ) {

            removeGeometryWithPhysics(
                    door
            );
        }


        for (Geometry ceiling : placedCeilings) {
            removeGeometryWithPhysics(ceiling);
        }

        for (Geometry roof : placedRoofs) {
            removeGeometryWithPhysics(roof);
        }

        for (Geometry stairs : placedStairs) {
            removeGeometryWithPhysics(stairs);
        }

        placedFoundations.clear();

        placedWalls.clear();

        placedDoorFrames.clear();

        placedDoors.clear();

        placedCeilings.clear();

        placedRoofs.clear();

        placedStairs.clear();

        doorClosedRotations.clear();

        doorOpenStates.clear();

        doorTargetOpenStates.clear();

        doorAnimationProgress.clear();


        nextFoundationId =
                1;

        nextWallId =
                1;

        nextDoorFrameId =
                1;

        nextDoorId =
                1;

        nextCeilingId =
                1;

        nextRoofId =
                1;

        nextStairsId =
                1;
    }


    public void clearFoundations() {

        clearBuildings();
    }


    public void loadFoundation(
            Vector3f position
    ) {

        createAndAttachFoundation(
                position.clone()
        );
    }


    public void loadWall(
            Vector3f position,
            Quaternion rotation
    ) {

        createAndAttachWall(
                position.clone(),
                rotation.clone()
        );
    }


    public void loadDoorFrame(
            Vector3f position,
            Quaternion rotation
    ) {

        createAndAttachDoorFrame(
                position.clone(),
                rotation.clone()
        );
    }


    public void loadDoor(
            Vector3f framePosition,
            Quaternion rotation,
            boolean open
    ) {

        createAndAttachDoor(
                framePosition.clone(),
                rotation.clone(),
                framePosition.clone(),
                open
        );
    }


    public void loadCeiling(Vector3f position) {
        createAndAttachCeiling(position.clone());
    }


    public void loadRoof(
            Vector3f position,
            Quaternion rotation
    ) {
        createAndAttachRoof(position.clone(), rotation.clone());
    }


    public void loadStairs(
            Vector3f position,
            Quaternion rotation
    ) {
        createAndAttachStairs(
                position.clone(),
                rotation.clone()
        );
    }


    private void removeGeometryWithPhysics(
            Geometry geometry
    ) {

        RigidBodyControl physics =
                geometry.getControl(
                        RigidBodyControl.class
                );


        if (
                physics != null
        ) {

            physicsSpace.remove(
                    physics
            );
        }


        geometry.removeFromParent();
    }


    private void removeDoorFrameWithPhysics(
            Node frame
    ) {

        for (
                Spatial child
                :
                frame.getChildren()
        ) {

            if (
                    child instanceof Geometry
            ) {

                Geometry geometry =
                        (Geometry) child;


                RigidBodyControl physics =
                        geometry.getControl(
                                RigidBodyControl.class
                        );


                if (
                        physics != null
                ) {

                    physicsSpace.remove(
                            physics
                    );
                }
            }
        }


        frame.removeFromParent();
    }


    private void demolishTarget() {

        if (inventoryMenuSystem.isOpen()) {
            return;
        }

        Ray ray = new Ray(
                camera.getLocation(),
                camera.getDirection()
        );

        CollisionResults results = new CollisionResults();
        rootNode.collideWith(ray, results);

        for (int i = 0; i < results.size(); i++) {

            Spatial hit = results.getCollision(i).getGeometry();

            if (results.getCollision(i).getDistance() > BUILD_DISTANCE) {
                break;
            }

            int doorIndex = findDoorIndex(hit);
            if (doorIndex >= 0) {
                if (!inventory.canAddItem(ItemType.WOOD, DOOR_WOOD_COST)) {
                    System.out.println("Nicht genug Platz im Inventar.");
                    return;
                }
                removeDoorAt(doorIndex);
                inventory.addItem(ItemType.WOOD, DOOR_WOOD_COST);
                System.out.println("Tür abgerissen. +" + DOOR_WOOD_COST + " Holz.");
                return;
            }

            int frameIndex = findDoorFrameIndex(hit);
            if (frameIndex >= 0) {
                Node frame = placedDoorFrames.get(frameIndex);
                int linkedDoorIndex = findDoorIndexForFrame(frame);
                int refund = DOOR_FRAME_WOOD_COST + (linkedDoorIndex >= 0 ? DOOR_WOOD_COST : 0);

                if (!inventory.canAddItem(ItemType.WOOD, refund)) {
                    System.out.println("Nicht genug Platz im Inventar.");
                    return;
                }

                if (linkedDoorIndex >= 0) {
                    removeDoorAt(linkedDoorIndex);
                }
                removeDoorFrameAt(frameIndex);
                inventory.addItem(ItemType.WOOD, refund);
                System.out.println("Türrahmen abgerissen. +" + refund + " Holz.");
                return;
            }

            int wallIndex = findGeometryIndex(placedWalls, hit);
            if (wallIndex >= 0) {
                if (!inventory.canAddItem(ItemType.WOOD, WALL_WOOD_COST)) {
                    System.out.println("Nicht genug Platz im Inventar.");
                    return;
                }
                Geometry wall = placedWalls.remove(wallIndex);
                removeGeometryWithPhysics(wall);
                inventory.addItem(ItemType.WOOD, WALL_WOOD_COST);
                System.out.println("Wand abgerissen. +" + WALL_WOOD_COST + " Holz.");
                return;
            }

            int stairsIndex = findGeometryIndex(placedStairs, hit);
            if (stairsIndex >= 0) {
                if (!inventory.canAddItem(ItemType.WOOD, STAIRS_WOOD_COST)) {
                    System.out.println("Nicht genug Platz im Inventar.");
                    return;
                }
                Geometry stairs = placedStairs.remove(stairsIndex);
                removeGeometryWithPhysics(stairs);
                inventory.addItem(ItemType.WOOD, STAIRS_WOOD_COST);
                System.out.println("Treppe abgerissen. +" + STAIRS_WOOD_COST + " Holz.");
                return;
            }

            int roofIndex = findGeometryIndex(placedRoofs, hit);
            if (roofIndex >= 0) {
                if (!inventory.canAddItem(ItemType.WOOD, ROOF_WOOD_COST)) {
                    System.out.println("Nicht genug Platz im Inventar.");
                    return;
                }
                Geometry roof = placedRoofs.remove(roofIndex);
                removeGeometryWithPhysics(roof);
                inventory.addItem(ItemType.WOOD, ROOF_WOOD_COST);
                System.out.println("Schrägdach abgerissen. +" + ROOF_WOOD_COST + " Holz.");
                return;
            }

            int ceilingIndex = findGeometryIndex(placedCeilings, hit);
            if (ceilingIndex >= 0) {
                if (!inventory.canAddItem(ItemType.WOOD, CEILING_WOOD_COST)) {
                    System.out.println("Nicht genug Platz im Inventar.");
                    return;
                }
                Geometry ceiling = placedCeilings.remove(ceilingIndex);
                removeGeometryWithPhysics(ceiling);
                inventory.addItem(ItemType.WOOD, CEILING_WOOD_COST);
                System.out.println("Decke abgerissen. +" + CEILING_WOOD_COST + " Holz.");
                return;
            }

            int foundationIndex = findGeometryIndex(placedFoundations, hit);
            if (foundationIndex >= 0) {
                if (!inventory.canAddItem(ItemType.WOOD, FOUNDATION_WOOD_COST)) {
                    System.out.println("Nicht genug Platz im Inventar.");
                    return;
                }
                Geometry foundation = placedFoundations.remove(foundationIndex);
                removeGeometryWithPhysics(foundation);
                inventory.addItem(ItemType.WOOD, FOUNDATION_WOOD_COST);
                System.out.println("Fundament abgerissen. +" + FOUNDATION_WOOD_COST + " Holz.");
                return;
            }
        }
    }


    private int findGeometryIndex(List<Geometry> geometries, Spatial hit) {
        for (int i = 0; i < geometries.size(); i++) {
            if (geometries.get(i) == hit) {
                return i;
            }
        }
        return -1;
    }


    private int findDoorIndex(Spatial hit) {
        return findGeometryIndex(placedDoors, hit);
    }


    private int findDoorFrameIndex(Spatial hit) {
        for (int i = 0; i < placedDoorFrames.size(); i++) {
            Node frame = placedDoorFrames.get(i);
            if (hit == frame || hit.getParent() == frame) {
                return i;
            }
        }
        return -1;
    }


    private int findDoorIndexForFrame(Node frame) {
        Vector3f framePosition = frame.getLocalTranslation();
        for (int i = 0; i < placedDoors.size(); i++) {
            Vector3f doorFramePosition = placedDoors.get(i).getUserData("framePosition");
            if (doorFramePosition != null && doorFramePosition.distanceSquared(framePosition) < 0.05f) {
                return i;
            }
        }
        return -1;
    }


    private void removeDoorAt(int index) {
        Geometry door = placedDoors.remove(index);
        removeGeometryWithPhysics(door);
        doorClosedRotations.remove(index);
        doorOpenStates.remove(index);
        doorTargetOpenStates.remove(index);
        doorAnimationProgress.remove(index);
    }


    private void removeDoorFrameAt(int index) {
        Node frame = placedDoorFrames.remove(index);
        removeDoorFrameWithPhysics(frame);
    }


    private static class EdgeTransform {

        private final Vector3f position;

        private final Quaternion rotation;


        private EdgeTransform(
                Vector3f position,
                Quaternion rotation
        ) {

            this.position =
                    position;

            this.rotation =
                    rotation;
        }
    }
}