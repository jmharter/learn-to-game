package com.jharter.game.ashley.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.jharter.game.ashley.components.Comp;
import com.jharter.game.ashley.components.Components.ActionQueueableComp;
import com.jharter.game.ashley.components.Components.ActionQueuedComp;
import com.jharter.game.ashley.components.Components.ActiveCardComp;
import com.jharter.game.ashley.components.Components.CardComp;
import com.jharter.game.ashley.components.Components.ChangeZoneComp;
import com.jharter.game.ashley.components.Components.IDComp;
import com.jharter.game.ashley.components.Components.TurnActionComp;
import com.jharter.game.ashley.components.Components.TypeComp;
import com.jharter.game.ashley.components.Components.ZoneComp;
import com.jharter.game.ashley.components.Components.ZonePositionComp;
import com.jharter.game.ashley.components.Ent;
import com.jharter.game.ashley.components.Link;

import uk.co.carelesslabs.Enums.ZoneType;

public class QueueTurnActionsSystem  extends IteratingSystem {

	@SuppressWarnings("unchecked")
	public QueueTurnActionsSystem() {
		super(Family.all(ActionQueueableComp.class, TypeComp.class, IDComp.class).get());
	}

	@Override
	public void processEntity(Entity entity, float deltaTime) {
		IDComp id = Comp.IDComp.get(entity);
		TypeComp ty = Comp.TypeComp.get(entity);
		
		if(ty != null) {
			switch(ty.type) {
				case CARD:
					//ZonePositionComp zp = Mapper.ZonePositionComp.get(entity);
					//zp.getZoneComp().remove(id);
					//ZoneComp z = Mapper.ZoneComp.get(ZoneType.ACTIVE_CARD);
					
					CardComp ca = Comp.CardComp.get(entity);
					ZonePositionComp zp = Comp.ZonePositionComp.get(entity);
					Entity owner = Ent.Entity.get(ca.playerID);
					ActiveCardComp ac = Comp.ActiveCardComp.get(owner);
					if(ac == null) {
						ac = Comp.create(getEngine(), ActiveCardComp.class);
						owner.add(ac);
					}
					
					ZoneComp z = zp.getZoneComp();
					ChangeZoneComp cz = Comp.create(getEngine(), ChangeZoneComp.class);
					cz.oldZoneID = z.zoneID;
					cz.newZoneID = Link.ZoneComp.getID(ca.playerID, ZoneType.ACTIVE_CARD);
					cz.useNextIndex = true;
					cz.instantChange = false;
					entity.add(cz);
					
					//z.add(id, zp);
					
					ac.activeCardID = id.id;
					
					TurnActionComp t = Comp.TurnActionComp.get(entity);
					if(t != null && t.turnAction.priority > 0) {
						t.turnAction.performAcceptCallback();
					}
					
					break;
				default:
					break;
			}
		}
		entity.remove(ActionQueueableComp.class);
		entity.add(Comp.create(getEngine(), ActionQueuedComp.class));
	}
	
}
