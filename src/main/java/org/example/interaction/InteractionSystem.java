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


    private final float interactionDistance =
            4f;


    public InteractionSystem(
            Camera camera,
            InputManager inputManager,
            Node rootNode,
            List<HarvestableResource> resources,
            Inventory inventory,
            HotbarSystem hotbarSystem,
            ToolView toolView
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
                name.equals("Attack")
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


                int damage =
                        calculateDamage(
                                resource
                        );


                if (
                        toolView.isAxeEquipped()
                ) {

                    toolView.swing();
                }


                boolean destroyed =
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


                if (destroyed) {

                    inventory.addItem(
                            resource.getItemType(),
                            resource.getYield()
                    );


                    System.out.println(
                            resource
                                    .getItemType()
                                    .getDisplayName()
                                    +
                                    " gesammelt!"
                    );
                }

                else {

                    System.out.println(
                            "HP: "
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


    private int calculateDamage(
            HarvestableResource resource
    ) {

        boolean axeEquipped =
                hotbarSystem
                        .getSelectedItemType()
                        ==
                        ItemType.STONE_AXE

                        &&

                        inventory.hasItem(
                                ItemType.STONE_AXE,
                                1
                        );


        // ==========================
        // BAUM
        // ==========================

        if (
                resource.getItemType()
                        ==
                        ItemType.WOOD
        ) {

            if (axeEquipped) {
                return 50;
            }


            return 25;
        }


        // ==========================
        // STEIN
        // ==========================

        if (
                resource.getItemType()
                        ==
                        ItemType.STONE
        ) {

            /*
             * Die Axt ist das falsche
             * Werkzeug für Stein.
             */

            if (axeEquipped) {
                return 10;
            }


            return 30;
        }


        return 10;
    }
}