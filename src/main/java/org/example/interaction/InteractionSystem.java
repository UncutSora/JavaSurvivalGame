package org.example.interaction;

import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.math.Ray;
import com.jme3.renderer.Camera;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import org.example.inventory.Inventory;
import org.example.inventory.ItemType;
import org.example.world.Tree;

public class InteractionSystem implements ActionListener {

    private final Camera camera;
    private final Node rootNode;
    private final Tree tree;
    private final Inventory inventory;

    private final float interactionDistance = 4f;


    public InteractionSystem(
            Camera camera,
            InputManager inputManager,
            Node rootNode,
            Tree tree,
            Inventory inventory
    ) {

        this.camera = camera;
        this.rootNode = rootNode;
        this.tree = tree;
        this.inventory = inventory;


        inputManager.addMapping(
                "Interact",
                new KeyTrigger(KeyInput.KEY_E)
        );


        inputManager.addListener(
                this,
                "Interact"
        );
    }


    @Override
    public void onAction(
            String name,
            boolean isPressed,
            float tpf
    ) {

        if (name.equals("Interact") && isPressed) {
            interact();
        }
    }


    private void interact() {

        Ray ray = new Ray(
                camera.getLocation(),
                camera.getDirection()
        );


        CollisionResults results =
                new CollisionResults();


        rootNode.collideWith(
                ray,
                results
        );


        for (int i = 0; i < results.size(); i++) {

            CollisionResult result =
                    results.getCollision(i);


            if (result.getDistance() > interactionDistance) {
                break;
            }


            Geometry geometry =
                    result.getGeometry();


            if (
                    !tree.isHarvested()
                            &&
                            tree.owns(geometry)
            ) {

                tree.harvest();


                inventory.addItem(
                        ItemType.WOOD,
                        1
                );


                System.out.println(
                        "Baum abgebaut!"
                );


                System.out.println(
                        "Holz im Inventar: "
                                +
                                inventory.getAmount(
                                        ItemType.WOOD
                                )
                );


                return;
            }
        }
    }
}