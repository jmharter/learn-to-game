package com.jharter.game.ashley.systems.network.offline;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.jharter.game.ashley.components.Components.InputComp;
import com.jharter.game.ashley.systems.boilerplate.GameIteratingSystem;

public class OfflineSelectInputSystem extends GameIteratingSystem {

	public OfflineSelectInputSystem() {
		super(Family.all(InputComp.class).get());
	}
	
	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		InputComp i = Comp.InputComp.get(entity);
		i.input.setRenderStateToRealTimeState();
	}
	
}
