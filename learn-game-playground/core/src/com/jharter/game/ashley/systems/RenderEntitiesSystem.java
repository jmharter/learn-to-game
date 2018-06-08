package com.jharter.game.ashley.systems;

import java.util.Comparator;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.SortedIteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.jharter.game.ashley.components.Mapper;
import com.jharter.game.ashley.components.Components.InvisibleComp;
import com.jharter.game.ashley.components.Components.PositionComp;
import com.jharter.game.ashley.components.Components.TileComp;
import com.jharter.game.ashley.components.Components.VisualComp;

public class RenderEntitiesSystem extends SortedIteratingSystem {
	
	private SpriteBatch batch;
	private OrthographicCamera camera;

	@SuppressWarnings("unchecked")
	public RenderEntitiesSystem (OrthographicCamera camera) {
		super(Family.all(PositionComp.class, VisualComp.class).exclude(InvisibleComp.class, TileComp.class).get(), new PositionSort());
		this.camera = camera;
		this.batch = new SpriteBatch();
	}

	@Override
	public void update (float deltaTime) {
		batch.begin();
		batch.setProjectionMatrix(camera.combined);
		forceSort();
		super.update(deltaTime);
		batch.end();
	}
	
	@Override
	public void processEntity(Entity entity, float deltaTime) {
		PositionComp p = Mapper.PositionComp.get(entity);
		VisualComp v = Mapper.VisualComp.get(entity);
		if(v.region == null) {
			v.region = v.defaultRegion;
		}
		if(v.region != null) {
			batch.draw(v.region, p.position.x, p.position.y);
		}
	}
	
	private static class PositionSort implements Comparator<Entity> {
		private ComponentMapper<PositionComp> pm = ComponentMapper.getFor(PositionComp.class);
		
		@Override
		public int compare(Entity entityA, Entity entityB) {
			Vector3 posA = pm.get(entityA).position;
			Vector3 posB = pm.get(entityB).position;
			if(posA.z == posB.z) {
				return (int) (posB.y - posA.y);
			}
			return (int) (posA.z - posB.z);
		}
	}
	
}