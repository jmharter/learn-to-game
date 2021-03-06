package com.jharter.game.ecs.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.jharter.game.ecs.components.Components.InputComp;
import com.jharter.game.ecs.systems.boilerplate.GameIteratingSystem;

public class CleanupInputSystem extends GameIteratingSystem {

	public CleanupInputSystem() {
		super(Family.all(InputComp.class).get());
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		InputComp in = Comp.InputComp.get(entity);
		if(in.input != null) {
			in.input.reset();
		}
	}

}
