package org.example.interaction;

import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.input.InputManager;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.math.Ray;
import com.jme3.renderer.Camera;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;

import org.example.hotbar.HotbarSystem;
import org.example.inventory.Inventory;
import org.example.inventory.ItemType;
import org.example.tools.ToolDurabilitySystem;
import org.example.ui.InventoryMenuSystem;
import org.example.ui.ToolView;
import org.example.world.HarvestableResource;

import java.util.List;

public class InteractionSystem implements ActionListener {

    private final Camera camera;

    private final Node rootNode;

    private final List<HarvestableResource> resources;

    private final Inventory inventory;

    private final HotbarSystem hotbarSystem;

    private final ToolView toolView;

    private final ToolDurabilitySystem
            toolDurabilitySystem;

    private final InventoryMenuSystem
            inventoryMenuSystem;


    private final float interactionDistance =
            4f;


    public InteractionSystem(
            Camera camera,
            InputManager inputManager,
            Node rootNode,
            List<HarvestableResource> resources,
            Inventory inventory,
            HotbarSystem hotbarSystem,
            ToolView toolView,
            ToolDurabilitySystem toolDurabilitySystem,
            InventoryMenuSystem inventoryMenuSystem
    ) {

        this.camera =
                camera;

        this.rootNode =
                rootNode;

        this.resources =
                resources;

        this.inventory =
                inventory;

        this.hotbarSystem =
                hotbarSystem;

        this.toolView =
                toolView;

        this.toolDurabilitySystem =
                toolDurabilitySystem;

        this.inventoryMenuSystem =
                inventoryMenuSystem;


        inputManager.addMapping(
                "Attack",
                new MouseButtonTrigger(
                        MouseInput.BUTTON_LEFT
                )
        );


        inputManager.addListener(
                this,
                "Attack"
        );
    }


    @Override
    public void onAction(
            String name,
            boolean isPressed,
            float tpf
    ) {

        if (
                inventoryMenuSystem.isOpen()
        ) {

            return;
        }


        if (
                name.equals(
                        "Attack"
                )
                        &&
                        isPressed
        ) {

            attack();
        }
    }


    private void attack() {

        Ray ray =
                new Ray(
                        camera.getLocation(),
                        camera.getDirection()
                );


        CollisionResults results =
                new CollisionResults();


        rootNode.collideWith(
                ray,
                results
        );


        for (
                int i = 0;
                i < results.size();
                i++
        ) {

            CollisionResult collision =
                    results.getCollision(i);


            if (
                    collision.getDistance()
                            >
                            interactionDistance
            ) {

                break;
            }


            Geometry geometry =
                    collision.getGeometry();


            for (
                    HarvestableResource resource
                    :
                    resources
            ) {

                if (
                        resource.isHarvested()
                ) {

                    continue;
                }


                if (
                        !resource.owns(
                                geometry
                        )
                ) {

                    continue;
                }


                boolean axeEquipped =
                        isStoneAxeEquipped();


                boolean axeUsed =
                        axeEquipped
                                &&
                                isAxeRelevantResource(
                                        resource
                                );


                int damage =
                        calculateDamage(
                                resource,
                                axeEquipped
                        );


                // ==========================
                // AXTANIMATION
                // ==========================

                if (
                        axeUsed
                ) {

                    toolView.swing();
                }


                boolean collected =
                        resource.takeDamage(
                                damage
                        );


                System.out.println(
                        "Treffer: "
                                +
                                damage
                                +
                                " Schaden"
                );


                // ==========================
                // AXT-HALTBARKEIT
                // ==========================

                if (
                        axeUsed
                ) {

                    boolean axeBroken =
                            toolDurabilitySystem
                                    .useStoneAxe();


                    if (
                            axeBroken
                    ) {

                        System.out.println(
                                "Die Steinaxt ist zerbrochen!"
                        );
                    }

                    else {

                        System.out.println(
                                "Axt-Haltbarkeit: "
                                        +
                                        toolDurabilitySystem
                                                .getStoneAxeDurability()
                                        +
                                        " / "
                                        +
                                        toolDurabilitySystem
                                                .getStoneAxeMaxDurability()
                        );
                    }
                }


                // ==========================
                // RESSOURCE SAMMELN
                // ==========================

                if (
                        collected
                ) {

                    boolean added =
                            inventory.addItem(
                                    resource.getItemType(),
                                    resource.getYield()
                            );


                    if (
                            added
                    ) {

                        System.out.println(
                                resource
                                        .getItemType()
                                        .getDisplayName()
                                        +
                                        " gesammelt: +"
                                        +
                                        resource.getYield()
                        );
                    }

                    else {

                        System.out.println(
                                "Inventar voll!"
                        );
                    }
                }

                else {

                    System.out.println(
                            "Ressourcen-HP: "
                                    +
                                    resource.getHealth()
                                    +
                                    " / "
                                    +
                                    resource.getMaxHealth()
                    );
                }


                return;
            }
        }
    }


    private boolean isStoneAxeEquipped() {

        return hotbarSystem
                .getSelectedItemType()
                ==
                ItemType.STONE_AXE

                &&

                toolDurabilitySystem
                        .hasUsableStoneAxe();
    }


    private boolean isAxeRelevantResource(
            HarvestableResource resource
    ) {

        ItemType type =
                resource.getItemType();


        return type
                ==
                ItemType.WOOD

                ||

                type
                        ==
                        ItemType.STONE;
    }


    private int calculateDamage(
            HarvestableResource resource,
            boolean axeEquipped
    ) {

        ItemType type =
                resource.getItemType();


        // ==========================
        // BAUM
        // ==========================

        if (
                type
                        ==
                        ItemType.WOOD
        ) {

            if (
                    axeEquipped
            ) {

                return 50;
            }


            return 25;
        }


        // ==========================
        // STEIN
        // ==========================

        if (
                type
                        ==
                        ItemType.STONE
        ) {

            if (
                    axeEquipped
            ) {

                return 10;
            }


            return 30;
        }


        // ==========================
        // BEEREN
        // ==========================

        if (
                type
                        ==
                        ItemType.BERRIES
        ) {

            return 10;
        }


        // ==========================
        // WASSER
        // ==========================

        if (
                type
                        ==
                        ItemType.WATER
        ) {

            return 1;
        }


        return 10;
    }
}