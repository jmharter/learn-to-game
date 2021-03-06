package com.jharter.game.ecs.systems.cursor;

import java.util.Comparator;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.utils.Array;
import com.jharter.game.ecs.components.Components.CursorChangedZoneEvent;
import com.jharter.game.ecs.components.Components.CursorComp;
import com.jharter.game.ecs.components.Components.CursorInputComp;
import com.jharter.game.ecs.components.Components.IDComp;
import com.jharter.game.ecs.components.Components.SpriteComp;
import com.jharter.game.ecs.components.Components.TargetableTag;
import com.jharter.game.ecs.components.subcomponents.TurnAction;
import com.jharter.game.util.ArrayUtil;
import com.jharter.game.util.GenericUtils;
import com.jharter.game.util.id.ID;

import uk.co.carelesslabs.Media;

public class CursorMoveSystem extends CursorSystem implements Comparator<Entity> {

	@SuppressWarnings("unchecked")
	public CursorMoveSystem() {
		super(CursorInputComp.class);
		add(TargetableTag.class, Family.all(TargetableTag.class, SpriteComp.class, IDComp.class).get(), this);
		event(CursorChangedZoneEvent.class);
	}

	@Override
	public void processEntity(Entity cursor, CursorComp c, float deltaTime) {
		CursorInputComp ci = Comp.CursorInputComp.get(cursor);

		ID origID = c.targetID;
		ID lastTargetID = c.targetID;
		c.targetID = getNewTargetID(c, ci);

		// We always want a last target, so even if it's null, set it to our target.
		if(lastTargetID == null) {
			lastTargetID = c.targetID;
		}

		boolean changedTargets = !GenericUtils.safeEquals(origID, c.targetID);
		boolean changedZones = changedTargets && hasChangedZones(c, lastTargetID);

		Comp.InvisibleTag.toggle(cursor, c.targetID == null);

		if(changedTargets) {
			Media.moveBeep.play();
			record(c, changedZones);
			if(changedZones) {
				Comp.CursorChangedZoneEvent.add(cursor);
			}
		}

		consumeMovement(ci);
	}

	private void record(CursorComp c, boolean changedZones) {
		if(c.turnActionID == null) {
			return;
		}
		TurnAction t = Comp.TurnActionComp.get(c.turnActionID).turnAction;
		if(!changedZones) {
			// If we've moved but have stayed in the same zone, we need to
			// remove the previous target which would be one of our siblings.
			if(t.targetIDs.size > 0) {
				t.targetIDs.pop();
			}
			t.targetIDs.add(c.targetID);

		// If we've changed zones, we need to figure out if we've changed zones due to a cancel or an accept
		} else if(t.targetIDs.size == 0 || (t.targetIDs.size > 0 && !c.targetID.equals(t.targetIDs.peek()))) {
			t.targetIDs.add(c.targetID);
		}
	}

	private boolean hasChangedZones(CursorComp c, ID lastTargetID) {
		if(lastTargetID == null || c.targetID == null || lastTargetID.equals(c.targetID)) {
			return false;
		}
		return !Comp.ZoneComp.get(Comp.ZonePositionComp.get(lastTargetID).zoneID).zoneID.equals(
			    Comp.ZoneComp.get(Comp.ZonePositionComp.get(c.targetID).zoneID).zoneID);
	}

	private Array<Entity> getTargets() {
		return entitiesSorted(TargetableTag.class);
	}

	private int getCurrentTargetIndex(CursorComp c) {
		Array<Entity> targets = getTargets();
		if(targets.size == 0) {
			return -1;
		}
		if(c.targetID == null) {
			return -1;
		}
		for(int i = 0; i < targets.size; i++) {
			if(Comp.IDComp.get(targets.get(i)).id.equals(c.targetID)) {
				return i;
			}
		}
		return -1;
	}

	private boolean hasMovement(CursorInputComp ci) {
		return ci.direction.x != 0 || ci.direction.y != 0;
	}

	private void consumeMovement(CursorInputComp ci) {
		ci.direction.setZero();
	}

	private ID getNewTargetID(CursorComp c, CursorInputComp ci) {
		boolean move = hasMovement(ci) && !getCursorManager().isAll(c);
		if(!move && c.targetID != null && Comp.TargetableComp.has(c.targetID)) {
			return c.targetID;
		}
		int direction = (int) (ci.direction.x != 0 ? ci.direction.x : ci.direction.y);
		Array<Entity> targets = getTargets();
		int currentIndex = getCurrentTargetIndex(c);
		int newTargetIndex = !ArrayUtil.has(targets, currentIndex) ? 0 : ArrayUtil.findNextIndex(targets, currentIndex, direction);
		if(!ArrayUtil.has(targets, newTargetIndex)) {
			return null;
		}
		return Comp.IDComp.get(targets.get(newTargetIndex)).id;
	}

	@Override
	public int compare(Entity entityA, Entity entityB) {
		SpriteComp sA = Comp.SpriteComp.get(entityA);
		SpriteComp sB = Comp.SpriteComp.get(entityB);
		if(sA.position.y == sB.position.y) {
			return (int) Math.signum(sA.position.x - sB.position.x);
		}
		return (int) Math.signum(sB.position.y - sA.position.y);
	}

}
