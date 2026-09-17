package org.example.building;

import com.jme3.asset.AssetManager;
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


    public static final int FOUNDATION_WOOD_COST =
            5;

    public static final int WALL_WOOD_COST =
            3;

    public static final int DOOR_FRAME_WOOD_COST =
            4;


    private static final float BUILD_DISTANCE =
            4f;

    private static final float GRID_SIZE =
            3f;


    private enum BuildType {

        FOUNDATION,
        WALL,
        DOOR_FRAME
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


    private final BitmapText buildText;


    private final List<Geometry> placedFoundations =
            new ArrayList<>();

    private final List<Geometry> placedWalls =
            new ArrayList<>();

    private final List<Node> placedDoorFrames =
            new ArrayList<>();


    private BuildType selectedBuildType =
            BuildType.FOUNDATION;


    private boolean active =
            false;


    private int nextFoundationId =
            1;

    private int nextWallId =
            1;

    private int nextDoorFrameId =
            1;


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


            setActive(
                    !active
            );


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
                        PLACE_BUILDING
                )
                        &&
                        isPressed
        ) {

            placeSelectedBuilding();
        }
    }


    public void update() {

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
        }


        updateHud();
    }


    private void updateFoundationPreview() {

        wallPreview.setCullHint(
                Spatial.CullHint.Always
        );


        doorFramePreview.setCullHint(
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

        foundationPreview.setCullHint(
                Spatial.CullHint.Always
        );


        doorFramePreview.setCullHint(
                Spatial.CullHint.Always
        );


        EdgeTransform edge =
                calculateCurrentEdgeTransform();


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

        foundationPreview.setCullHint(
                Spatial.CullHint.Always
        );


        wallPreview.setCullHint(
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


    private EdgeTransform calculateCurrentEdgeTransform() {

        Geometry foundation =
                getNearestFoundationToBuildTarget();


        if (
                foundation == null
        ) {

            return null;
        }


        Vector3f foundationPosition =
                foundation.getLocalTranslation();


        Vector3f target =
                getHorizontalBuildTarget();


        float dx =
                target.x - foundationPosition.x;


        float dz =
                target.z - foundationPosition.z;


        Vector3f position =
                new Vector3f();


        Quaternion rotation =
                new Quaternion();


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
                    foundationPosition.x
                            +
                            direction * 1.5f,
                    1.62f,
                    foundationPosition.z
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
                    foundationPosition.x,
                    1.62f,
                    foundationPosition.z
                            +
                            direction * 1.5f
            );
        }


        return new EdgeTransform(
                position,
                rotation
        );
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


            default:

                selectedName =
                        "Türrahmen";

                cost =
                        DOOR_FRAME_WOOD_COST;

                break;
        }


        buildText.setText(
                "BAUMODUS | [1] Fundament | [2] Wand | [3] Türrahmen | "
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


        placedFoundations.clear();

        placedWalls.clear();

        placedDoorFrames.clear();


        nextFoundationId =
                1;

        nextWallId =
                1;

        nextDoorFrameId =
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