package com.jharter.game.ecs.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.jharter.game.ecs.components.Components.ChangeZoneComp;
import com.jharter.game.ecs.components.Components.IDComp;
import com.jharter.game.ecs.components.Components.TurnActionComp;
import com.jharter.game.ecs.components.Components.TurnActionSelectedEvent;
import com.jharter.game.ecs.components.Components.ZonePositionComp;
import com.jharter.game.ecs.systems.boilerplate.GameIteratingSystem;

import uk.co.carelesslabs.Enums.ZoneType;

public class QueueTurnActionsSystem  extends GameIteratingSystem {

	public QueueTurnActionsSystem() {
		super(Family.all(IDComp.class, ZonePositionComp.class, TurnActionComp.class, TurnActionSelectedEvent.class).get());
	}

	@Override
	public void processEntity(Entity entity, float deltaTime) {
		IDComp id = Comp.IDComp.get(entity);
		ZonePositionComp zp = Comp.ZonePositionComp.get(entity);
		TurnActionComp t = Comp.TurnActionComp.get(entity);

		boolean isFriend = Comp.FriendComp.has(t.turnAction.ownerID);

		// Move the turn action to the active card zone
		ChangeZoneComp cz = Comp.ChangeZoneComp.add(entity);
		Comp.util(cz).change(zp.zoneID, getZoneID(t.turnAction.ownerID, isFriend ? ZoneType.FRIEND_ACTIVE_CARD : ZoneType.ENEMY_ACTIVE_CARD));

		// Perform the callback that occurs the moment a turn action is queued if it's
		// of a high priority
		Comp.ActiveTurnActionComp.add(t.turnAction.ownerID).activeTurnActionID = id.id;
		/*if(t != null && t.turnAction.priority > 0) {
			//t.turnAction.perform(false);
		}*/

		// Change the action's state from "queueable" to "queued" and mark it with a timestamp
		long timestamp = Comp.TurnActionSelectedEvent.get(entity).timestamp;
		Comp.TurnActionQueueItemComp.add(entity).timestamp = timestamp;
	}

}
