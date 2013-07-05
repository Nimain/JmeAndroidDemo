package com.jme3.android.demo.system;

import com.jme3.android.demo.Main;
import com.jme3.android.demo.camera.CameraHandler;
import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import java.util.logging.Logger;

/**
 *
 * @author Nehon / iwgeric
 */
public class SceneAppState extends AbstractAppState {
    private static final Logger logger = Logger.getLogger(SceneAppState.class.getName());

    private Main app;
    private BulletAppState bulletAppState;
    private AssetManager assetManager;
    private Node rootNode;
    private Node worldNode;
    private Node sceneNode;
    private CharacterHandler mainCharacter;
    private Geometry navMesh;
    private Spatial navMeshTargetMarker;
    private CameraHandler cameraHandler;
    private Node groundNode;

    public SceneAppState() {
    }

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        this.app = (Main)app;
        this.assetManager = this.app.getAssetManager();
        this.rootNode = this.app.getRootNode();
        this.bulletAppState = this.app.getBulletAppState();

        loadScene();
        this.rootNode.attachChild(worldNode);

        cameraHandler = this.app.getCameraHandler();
        cameraHandler.setTarget(mainCharacter.getModel());
        cameraHandler.init();

        super.initialize(stateManager, app);
    }

    private void loadScene(){
        worldNode = (Node)assetManager.loadModel("Scenes/World1.j3o");

        Node mainCharacterNode = (Node)worldNode.getChild("Jaime");
        mainCharacter = new CharacterHandler((Node)mainCharacterNode.getChild(0));
        bulletAppState.getPhysicsSpace().add(mainCharacter.getCharPhysicsControl());

        sceneNode = (Node)worldNode.getChild("Scene");
        // create mesh collision shape around scene
        // NavMesh Geometry has JmePhysicsIgnore UserData so it will not
        //   be included in the collision shape
        CollisionShape sceneColShape = CollisionShapeFactory.createMeshShape(sceneNode);
        RigidBodyControl sceneRigidBodyControl = new RigidBodyControl(sceneColShape, 0f);
        sceneNode.addControl(sceneRigidBodyControl);
        bulletAppState.getPhysicsSpace().add(sceneRigidBodyControl);

        navMesh = (Geometry)sceneNode.getChild("NavMesh");

        navMeshTargetMarker = worldNode.getChild("NavMeshTargetMarker");
        groundNode = (Node)worldNode.getChild("Ground");

    }

    public Node getWorldNode() {
        return worldNode;
    }

    public Node getSceneNode() {
        return sceneNode;
    }

    public Node getGroundNode() {
        return groundNode;
    }

    public Geometry getNavMesh() {
        return navMesh;
    }

    public CharacterHandler getMainCharacter() {
        return mainCharacter;
    }

    public Spatial getNavMeshTargetMarker() {
        return navMeshTargetMarker;
    }

    @Override
    public void update(float tpf) {
    }

    @Override
    public void setEnabled(boolean enabled) {
        if (enabled) {
            rootNode.attachChild(worldNode);
        } else {
            worldNode.removeFromParent();
        }
        if (bulletAppState != null) {
            bulletAppState.setEnabled(enabled);
        }
        super.setEnabled(enabled);
    }

}
