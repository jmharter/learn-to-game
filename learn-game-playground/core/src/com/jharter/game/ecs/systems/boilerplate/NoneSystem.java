package com.jharter.game.ecs.systems.boilerplate;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;

public abstract class NoneSystem extends GameIteratingSystem {

	public NoneSystem(Family family) {
		super(family);
	}
	
	public NoneSystem (Family family, int priority) {
		super(family, priority);
	}
	
	@Override
	public void performUpdate (float deltaTime) {
		if(entities.size() == 0) {
			processNone(deltaTime);
		}
	}
	
	protected void processEntity(Entity entity, float deltaTime) {
		// Intentionally stubbed out because it will never be called.
	}
	
	protected abstract void processNone(float deltaTime);
	
}
