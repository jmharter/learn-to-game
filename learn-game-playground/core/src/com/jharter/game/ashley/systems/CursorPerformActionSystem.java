package com.jharter.game.ashley.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.jharter.game.ashley.components.Components.ActiveCardComp;
import com.jharter.game.ashley.components.Components.CardComp;
import com.jharter.game.ashley.components.Components.CursorComp;
import com.jharter.game.ashley.components.Components.IDComp;
import com.jharter.game.ashley.components.Components.PerformTargetActionComp;
import com.jharter.game.ashley.components.Components.TargetingComp;
import com.jharter.game.ashley.components.Components.TypeComp;
import com.jharter.game.ashley.components.Components.ZoneComp;
import com.jharter.game.ashley.components.Components.ZonePositionComp;
import com.jharter.game.ashley.components.Mapper;
import com.jharter.game.ashley.systems.boilerplate.CustomIntervalIteratingSystem;

import uk.co.carelesslabs.Enums.ZoneType;

public class CursorPerformActionSystem extends CustomIntervalIteratingSystem {
	
	public static final float DEFAULT_INTERVAL = 10f;
	
	@SuppressWarnings("unchecked")
	public CursorPerformActionSystem() {
		super(Family.all(CursorComp.class, PerformTargetActionComp.class).get(), DEFAULT_INTERVAL);
	}
	
	@Override
	public void update (float deltaTime) {
		accumulator += deltaTime;
		Mapper.getTurnTimerComp().accumulator = accumulator;
		while (accumulator >= interval) {
			accumulator -= interval;
			updateInterval();
			Mapper.getTurnTimerComp().accumulator = accumulator;
		}
	}
	
	@Override
	public void processEntity(Entity entity) {
		CursorComp c = Mapper.CursorComp.get(entity);
		TargetingComp t = c.getTargetingComp();
		if(t == null || !t.hasAllTargets()) {
			return;
		}
		
		Entity actionEntity = t.getEntity(0);
		TypeComp ty = Mapper.TypeComp.get(actionEntity);
		if(ty != null) {
			switch(ty.type) {
				case CARD:
					IDComp id = Mapper.IDComp.get(actionEntity);
					ZonePositionComp zp = Mapper.ZonePositionComp.get(actionEntity);
					zp.getZoneComp().remove(id);
					
					ZoneComp z = Mapper.ZoneComp.get(ZoneType.HAND);
					CardComp ca = Mapper.CardComp.get(actionEntity);
					Entity owner = Mapper.Entity.get(ca.ownerID);
					if(Mapper.ActiveCardComp.has(owner)) {
						owner.remove(ActiveCardComp.class);
					}
					z.add(id, zp);
					break;
				default:
					break;
			}
		}
		
		t.performAcceptCallback();
		c.targetingEntityID = null;
		t.targetIDs.clear();
		entity.remove(PerformTargetActionComp.class);
	}
	
}
