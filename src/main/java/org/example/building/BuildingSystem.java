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

public class BuildingSystem implements ActionListener {

    private static final String TOGGLE_BUILD =
            "ToggleBuildMode";

    private static final String PLACE_BUILDING =
            "PlaceBuilding";


    public static final int FOUNDATION_WOOD_COST =
            5;


    private static final float BUILD_DISTANCE =
            4f;

    private static final float GRID_SIZE =
            3f;


    private final AssetManager assetManager;

    private final Node rootNode;

    private final Camera camera;

    private final PhysicsSpace physicsSpace;

    private final Inventory inventory;

    private final InventoryMenuSystem inventoryMenuSystem;


    private final Geometry preview;

    private final Material previewMaterial;

    private final BitmapText buildText;


    private boolean active =
            false;


    private int placedFoundationCount =
            0;


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
        // VORSCHAU
        // ==========================

        Box previewBox =
                new Box(
                        1.5f,
                        0.12f,
                        1.5f
                );


        preview =
                new Geometry(
                        "FoundationPreview",
                        previewBox
                );


        previewMaterial =
                new Material(
                        assetManager,
                        "Common/MatDefs/Misc/Unshaded.j3md"
                );


        previewMaterial.setColor(
                "Color",
                new ColorRGBA(
                        0.15f,
                        1f,
                        0.25f,
                        0.42f
                )
        );


        previewMaterial
                .getAdditionalRenderState()
                .setBlendMode(
                        RenderState.BlendMode.Alpha
                );


        previewMaterial
                .getAdditionalRenderState()
                .setDepthWrite(
                        false
                );


        preview.setMaterial(
                previewMaterial
        );


        preview.setQueueBucket(
                RenderQueue.Bucket.Transparent
        );


        preview.setCullHint(
                Spatial.CullHint.Always
        );


        rootNode.attachChild(
                preview
        );


        // ==========================
        // BAU-HUD
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
                PLACE_BUILDING,
                new MouseButtonTrigger(
                        MouseInput.BUTTON_LEFT
                )
        );


        inputManager.addListener(
                this,
                TOGGLE_BUILD,
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
                !name.equals(
                        PLACE_BUILDING
                )
                        ||
                        !isPressed
                        ||
                        !active
        ) {

            return;
        }


        if (
                inventoryMenuSystem.isOpen()
        ) {

            return;
        }


        placeFoundation();
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

            preview.setCullHint(
                    Spatial.CullHint.Always
            );


            return;
        }


        preview.setCullHint(
                Spatial.CullHint.Inherit
        );


        updatePreviewPosition();

        updatePreviewColor();

        updateHud();
    }


    private void updatePreviewPosition() {

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


        Vector3f target =
                camera
                        .getLocation()
                        .add(
                                direction.mult(
                                        BUILD_DISTANCE
                                )
                        );


        float snappedX =
                Math.round(
                        target.x / GRID_SIZE
                )
                        *
                        GRID_SIZE;


        float snappedZ =
                Math.round(
                        target.z / GRID_SIZE
                )
                        *
                        GRID_SIZE;


        preview.setLocalTranslation(
                snappedX,
                0.12f,
                snappedZ
        );
    }


    private void updatePreviewColor() {

        boolean canBuild =
                inventory.hasItem(
                        ItemType.WOOD,
                        FOUNDATION_WOOD_COST
                );


        if (
                canBuild
        ) {

            previewMaterial.setColor(
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

            previewMaterial.setColor(
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


        buildText.setText(
                "BAUMODUS | Fundament: "
                        +
                        FOUNDATION_WOOD_COST
                        +
                        " Holz | Holz: "
                        +
                        wood
                        +
                        " | Linksklick = Bauen | B = Beenden"
        );
    }


    private void placeFoundation() {

        if (
                !inventory.hasItem(
                        ItemType.WOOD,
                        FOUNDATION_WOOD_COST
                )
        ) {

            System.out.println(
                    "Nicht genug Holz. Fundament benötigt "
                            +
                            FOUNDATION_WOOD_COST
                            +
                            " Holz."
            );


            return;
        }


        Vector3f position =
                preview
                        .getLocalTranslation()
                        .clone();


        Geometry foundation =
                createFoundation(
                        position
                );


        rootNode.attachChild(
                foundation
        );


        RigidBodyControl physics =
                new RigidBodyControl(
                        0f
                );


        foundation.addControl(
                physics
        );


        physicsSpace.add(
                physics
        );


        inventory.removeItem(
                ItemType.WOOD,
                FOUNDATION_WOOD_COST
        );


        placedFoundationCount++;


        System.out.println(
                "Fundament gebaut."
        );


        System.out.println(
                "Verbleibendes Holz: "
                        +
                        inventory.getAmount(
                                ItemType.WOOD
                        )
        );
    }


    private Geometry createFoundation(
            Vector3f position
    ) {

        Box foundationBox =
                new Box(
                        1.5f,
                        0.12f,
                        1.5f
                );


        Geometry foundation =
                new Geometry(
                        "Foundation_"
                                +
                                placedFoundationCount,
                        foundationBox
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


        foundation.setMaterial(
                material
        );


        foundation.setLocalTranslation(
                position
        );


        return foundation;
    }


    public void setActive(
            boolean active
    ) {

        this.active =
                active;


        if (
                active
        ) {

            preview.setCullHint(
                    Spatial.CullHint.Inherit
            );


            buildText.setCullHint(
                    Spatial.CullHint.Inherit
            );


            System.out.println(
                    "Baumodus aktiviert."
            );
        }

        else {

            preview.setCullHint(
                    Spatial.CullHint.Always
            );


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
}