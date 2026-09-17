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


    public static final int FOUNDATION_WOOD_COST =
            5;

    public static final int WALL_WOOD_COST =
            3;


    private static final float BUILD_DISTANCE =
            4f;

    private static final float GRID_SIZE =
            3f;


    private enum BuildType {

        FOUNDATION,
        WALL
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


    private final BitmapText buildText;


    private final List<Geometry> placedFoundations =
            new ArrayList<>();

    private final List<Geometry> placedWalls =
            new ArrayList<>();


    private BuildType selectedBuildType =
            BuildType.FOUNDATION;


    private boolean active =
            false;


    private int nextFoundationId =
            1;

    private int nextWallId =
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


            System.out.println(
                    "Bauteil ausgewählt: Fundament"
            );


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


            System.out.println(
                    "Bauteil ausgewählt: Wand"
            );


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
        }


        updateHud();
    }


    private void updateFoundationPreview() {

        wallPreview.setCullHint(
                Spatial.CullHint.Always
        );


        foundationPreview.setCullHint(
                Spatial.CullHint.Inherit
        );


        Vector3f target =
                getHorizontalBuildTarget();


        float snappedX =
                snap(
                        target.x
                );


        float snappedZ =
                snap(
                        target.z
                );


        foundationPreview.setLocalTranslation(
                snappedX,
                0.12f,
                snappedZ
        );


        boolean enoughWood =
                inventory.hasItem(
                        ItemType.WOOD,
                        FOUNDATION_WOOD_COST
                );


        boolean occupied =
                foundationExistsAt(
                        snappedX,
                        snappedZ
                );


        setPreviewColor(
                foundationPreviewMaterial,
                enoughWood
                        &&
                        !occupied
        );
    }


    private void updateWallPreview() {

        foundationPreview.setCullHint(
                Spatial.CullHint.Always
        );


        if (
                placedFoundations.isEmpty()
        ) {

            wallPreview.setCullHint(
                    Spatial.CullHint.Always
            );


            return;
        }


        Geometry nearestFoundation =
                getNearestFoundationToBuildTarget();


        if (
                nearestFoundation == null
        ) {

            wallPreview.setCullHint(
                    Spatial.CullHint.Always
            );


            return;
        }


        wallPreview.setCullHint(
                Spatial.CullHint.Inherit
        );


        Vector3f foundationPosition =
                nearestFoundation
                        .getLocalTranslation();


        Vector3f target =
                getHorizontalBuildTarget();


        float dx =
                target.x
                        -
                        foundationPosition.x;


        float dz =
                target.z
                        -
                        foundationPosition.z;


        float absoluteX =
                Math.abs(
                        dx
                );


        float absoluteZ =
                Math.abs(
                        dz
                );


        Vector3f wallPosition =
                new Vector3f();


        Quaternion wallRotation =
                new Quaternion();


        /*
         * Entscheiden, an welche Kante
         * des Fundaments die Wand kommt.
         */
        if (
                absoluteX
                        >
                        absoluteZ
        ) {

            /*
             * Ost / West
             */
            float direction =
                    dx >= 0f
                            ?
                            1f
                            :
                            -1f;


            wallPosition.set(
                    foundationPosition.x
                            +
                            direction * 1.5f,

                    1.62f,

                    foundationPosition.z
            );


            wallRotation.fromAngles(
                    0f,
                    (float) Math.toRadians(
                            90f
                    ),
                    0f
            );
        }

        else {

            /*
             * Nord / Süd
             */
            float direction =
                    dz >= 0f
                            ?
                            1f
                            :
                            -1f;


            wallPosition.set(
                    foundationPosition.x,
                    1.62f,
                    foundationPosition.z
                            +
                            direction * 1.5f
            );


            wallRotation.fromAngles(
                    0f,
                    0f,
                    0f
            );
        }


        wallPreview.setLocalTranslation(
                wallPosition
        );


        wallPreview.setLocalRotation(
                wallRotation
        );


        boolean enoughWood =
                inventory.hasItem(
                        ItemType.WOOD,
                        WALL_WOOD_COST
                );


        boolean occupied =
                wallExistsNear(
                        wallPosition
                );


        setPreviewColor(
                wallPreviewMaterial,
                enoughWood
                        &&
                        !occupied
        );
    }


    private Vector3f getHorizontalBuildTarget() {

        Vector3f direction =
                camera
                        .getDirection()
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


        return camera
                .getLocation()
                .add(
                        direction.mult(
                                BUILD_DISTANCE
                        )
                );
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


    private Geometry getNearestFoundationToBuildTarget() {

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
                    distance
                            <
                            nearestDistance
            ) {

                nearestDistance =
                        distance;


                nearest =
                        foundation;
            }
        }


        /*
         * Wand-Snapping funktioniert nur,
         * wenn man halbwegs in der Nähe
         * eines Fundaments schaut.
         */
        if (
                nearestDistance
                        >
                        64f
        ) {

            return null;
        }


        return nearest;
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
                    foundation
                            .getLocalTranslation();


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


    private boolean wallExistsNear(
            Vector3f position
    ) {

        for (
                Geometry wall
                :
                placedWalls
        ) {

            if (
                    wall
                            .getLocalTranslation()
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

        if (
                valid
        ) {

            material.setColor(
                    "Color",
                    new ColorRGBA(
                            0.15f,
                            1f,
                            0.25f,
                            0.42f
                    )
            );
        }

        else {

            material.setColor(
                    "Color",
                    new ColorRGBA(
                            1f,
                            0.12f,
                            0.12f,
                            0.42f
                    )
            );
        }
    }


    private void updateHud() {

        int wood =
                inventory.getAmount(
                        ItemType.WOOD
                );


        String selectedName;

        int cost;


        if (
                selectedBuildType
                        ==
                        BuildType.FOUNDATION
        ) {

            selectedName =
                    "Fundament";

            cost =
                    FOUNDATION_WOOD_COST;
        }

        else {

            selectedName =
                    "Wand";

            cost =
                    WALL_WOOD_COST;
        }


        buildText.setText(
                "BAUMODUS | [1] Fundament | [2] Wand | "
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

        if (
                selectedBuildType
                        ==
                        BuildType.FOUNDATION
        ) {

            placeFoundation();
        }

        else {

            placeWall();
        }
    }


    private void placeFoundation() {

        if (
                !inventory.hasItem(
                        ItemType.WOOD,
                        FOUNDATION_WOOD_COST
                )
        ) {

            System.out.println(
                    "Nicht genug Holz für Fundament."
            );


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

            System.out.println(
                    "Hier steht bereits ein Fundament."
            );


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

        if (
                !inventory.hasItem(
                        ItemType.WOOD,
                        WALL_WOOD_COST
                )
        ) {

            System.out.println(
                    "Nicht genug Holz für Wand."
            );


            return;
        }


        if (
                wallPreview.getCullHint()
                        ==
                        Spatial.CullHint.Always
        ) {

            System.out.println(
                    "Keine gültige Wandposition."
            );


            return;
        }


        Vector3f position =
                wallPreview
                        .getLocalTranslation()
                        .clone();


        Quaternion rotation =
                wallPreview
                        .getLocalRotation()
                        .clone();


        if (
                wallExistsNear(
                        position
                )
        ) {

            System.out.println(
                    "Hier steht bereits eine Wand."
            );


            return;
        }


        createAndAttachWall(
                position,
                rotation
        );


        inventory.removeItem(
                ItemType.WOOD,
                WALL_WOOD_COST
        );


        System.out.println(
                "Wand gebaut."
        );
    }


    private void createAndAttachFoundation(
            Vector3f position
    ) {

        Geometry foundation =
                createFoundation(
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
                createWall(
                        position,
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


    private Geometry createFoundation(
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


        return foundation;
    }


    private Geometry createWall(
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


        return wall;
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


        ColorRGBA woodColor =
                new ColorRGBA(
                        0.42f,
                        0.25f,
                        0.10f,
                        1f
                );


        material.setColor(
                "Diffuse",
                woodColor
        );


        material.setColor(
                "Ambient",
                woodColor
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


        material
                .getAdditionalRenderState()
                .setBlendMode(
                        RenderState.BlendMode.Alpha
                );


        material
                .getAdditionalRenderState()
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


            System.out.println(
                    "Baumodus aktiviert."
            );
        }

        else {

            hidePreviews();


            buildText.setCullHint(
                    Spatial.CullHint.Always
            );


            System.out.println(
                    "Baumodus deaktiviert."
            );
        }
    }


    public boolean isActive() {

        return active;
    }


    // ==========================
    // SAVE-API FUNDAMENTE
    // ==========================

    public int getFoundationCount() {

        return placedFoundations.size();
    }


    public Vector3f getFoundationPosition(
            int index
    ) {

        return placedFoundations
                .get(
                        index
                )
                .getLocalTranslation()
                .clone();
    }


    public void clearFoundations() {

        for (
                Geometry foundation
                :
                placedFoundations
        ) {

            removeGeometryWithPhysics(
                    foundation
            );
        }


        placedFoundations.clear();


        nextFoundationId =
                1;
    }


    public void loadFoundation(
            Vector3f position
    ) {

        createAndAttachFoundation(
                position.clone()
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
}