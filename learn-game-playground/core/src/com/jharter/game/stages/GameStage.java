package com.jharter.game.stages;

import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.jharter.game.ashley.components.EntityBuilder;
import com.jharter.game.ashley.entities.EntityUtil;
import com.jharter.game.ashley.systems.AnimationSystem;
import com.jharter.game.ashley.systems.ApproachTargetSystem;
import com.jharter.game.ashley.systems.CleanupInputSystem;
import com.jharter.game.ashley.systems.CollisionSystem;
import com.jharter.game.ashley.systems.CursorInputSystem;
import com.jharter.game.ashley.systems.CursorPositionSystem;
import com.jharter.game.ashley.systems.CursorZoneSystem;
import com.jharter.game.ashley.systems.InteractSystem;
import com.jharter.game.ashley.systems.RemoveEntitiesSystem;
import com.jharter.game.ashley.systems.RenderEntitiesSystem;
import com.jharter.game.ashley.systems.RenderInitSystem;
import com.jharter.game.ashley.systems.RenderTilesSystem;
import com.jharter.game.ashley.systems.UpdatePhysicsSystem;
import com.jharter.game.ashley.systems.VelocityMovementSystem;
import com.jharter.game.ashley.systems.network.client.ClientAddPlayersPacketSystem;
import com.jharter.game.ashley.systems.network.client.ClientRandomMovementSystem;
import com.jharter.game.ashley.systems.network.client.ClientRemoveEntityPacketSystem;
import com.jharter.game.ashley.systems.network.client.ClientSendInputSystem;
import com.jharter.game.ashley.systems.network.client.ClientSnapshotPacketSystem;
import com.jharter.game.ashley.systems.network.offline.OfflineSelectInputSystem;
import com.jharter.game.ashley.systems.network.server.ServerInputPacketSystem;
import com.jharter.game.ashley.systems.network.server.ServerRegisterPlayerPacketSystem;
import com.jharter.game.ashley.systems.network.server.ServerRequestEntityPacketSystem;
import com.jharter.game.ashley.systems.network.server.ServerSendSnapshotSystem;
import com.jharter.game.control.GameInput;
import com.jharter.game.debug.Debug;
import com.jharter.game.network.endpoints.EndPointHelper;
import com.jharter.game.network.endpoints.GameClient;
import com.jharter.game.network.endpoints.GameServer;
import com.jharter.game.util.id.ID;
import com.jharter.game.util.id.IDGenerator;

import uk.co.carelesslabs.box2d.Box2DWorld;

public abstract class GameStage {

	private ID id;
	protected OrthographicCamera camera;
	protected Viewport viewport;
	protected PooledEngine engine;
    protected Box2DWorld box2D;
    protected GameInput stageInput;
    protected EndPointHelper endPointHelper;
    
	public GameStage(EndPointHelper endPointHelper) {
		this(IDGenerator.newID(), endPointHelper);
	}
	
	public GameStage(ID id, EndPointHelper endPointHelper) {
		this.id = id;
		this.endPointHelper = endPointHelper;
		create();
	}
    
	public ID getId() {
		return id;
	}
	
	protected void setId(ID id) {
		this.id = id;
	}
	
	public EndPointHelper getEndPointHelper() {
		return endPointHelper;
	}
	
	public OrthographicCamera getCamera() {
		return camera;
	}
	
	public Box2DWorld getBox2DWorld() {
		return box2D;
	}
	
	public GameInput getInput() {
		return stageInput;
	}
	
	public PooledEngine getEngine() {
		return engine;
	}
	
    protected void create() {
    	camera = buildCamera();
    	viewport = buildViewport(camera);
    	viewport.apply();
    	camera.position.set(0, 0, 0);
    	
        box2D = buildBox2DWorld();
        engine = buildEngine();
        addEntities(engine);
    }
	
	public GameInput buildInput(boolean active) {
		int displayW = Gdx.graphics.getWidth();
    	int displayH = Gdx.graphics.getHeight();
    	GameInput input = new GameInput(displayW, displayH, camera);
    	if(active) {
    		stageInput = input;
    	}
    	return input;
	}
	
	protected int getHorizontalPixels() {
		return 1920;
	}
	
	protected int getVerticalPixels() {
		return 1080;
	}
	
    protected OrthographicCamera buildCamera() {
    	OrthographicCamera camera = new OrthographicCamera();
        camera.zoom = 1f;
        return camera;
    }
    
    protected Viewport buildViewport(OrthographicCamera camera) {
    	//int displayW = Gdx.graphics.getWidth();
        //int displayH = Gdx.graphics.getHeight();
    	//int verticalPixels = getVerticalPixels();
        
        //int h = 1080; //(int) (displayH/Math.floor(displayH/verticalPixels));
        //int w = 1920; //(int) (displayW/(displayH/ (displayH/Math.floor(displayH/verticalPixels))));
    	
    	return new FillViewport(getHorizontalPixels(), getVerticalPixels(), camera);
    }
    
    public void resize(int width, int height) {
    	viewport.update(width, height);
    	camera.position.set(0, 0, 0);
    }
    
    protected Box2DWorld buildBox2DWorld() {
    	return new Box2DWorld();
    }
    
    protected abstract PooledEngine buildEngine();
    
    public abstract void addEntities(PooledEngine engine);
    
    public abstract EntityBuilder addPlayerEntity(ID id, Vector3 position, boolean focus);
    
    public abstract Vector3 getEntryPoint();
    
    public void tick(float deltaTime) {
        engine.update(deltaTime);
    }
    
    public void activate() {
    	if(stageInput != null) {
    		Gdx.input.setInputProcessor(stageInput);
    		stageInput.reset();
    	}
    }
    
    public void deactivate() {
    	stageInput.reset();
    }
    
    public void dispose() {
    	box2D.dispose();
    	engine.removeAllEntities();
    }

}
