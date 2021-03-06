package com.jharter.game.ecs.systems;

import java.util.Comparator;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.jharter.game.ecs.components.Components.PendingTurnActionTag;
import com.jharter.game.ecs.components.Components.StatusEffectsComp;
import com.jharter.game.ecs.components.Components.TurnActionComp;
import com.jharter.game.ecs.components.Components.TurnActionQueueItemComp;
import com.jharter.game.ecs.components.Components.VitalsComp;
import com.jharter.game.ecs.components.subcomponents.TurnAction;
import com.jharter.game.ecs.systems.boilerplate.GameSortedIteratingSystem;

public class ApplyPendingTurnActionsSystem extends GameSortedIteratingSystem {

	public ApplyPendingTurnActionsSystem() {
		super(Family.all(PendingTurnActionTag.class, TurnActionComp.class).get());
		setComparator(new PriorityAndTimingSort());
		add(TurnActionComp.class);
		add(StatusEffectsComp.class);
		add(VitalsComp.class);
	}

	@Override
	public void beforeUpdate(float deltaTime) {
		forceSort();
		resetPending();
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		Comp.TurnActionComp.get(entity).turnAction.perform(true);
	}

	private void resetPending() {
		for(TurnActionComp t : comps(TurnActionComp.class)) {
			t.turnAction.mods.resetPending();
		}
		for(StatusEffectsComp s : comps(StatusEffectsComp.class)) {
			s.effects.resetPending();
		}
		for(VitalsComp v : comps(VitalsComp.class)) {
			v.vitals.resetPending();
		}
	}

	private class PriorityAndTimingSort implements Comparator<Entity> {

		@Override
		public int compare(Entity entityA, Entity entityB) {
			TurnAction tA = Comp.TurnActionComp.get(entityA).turnAction;
			TurnAction tB = Comp.TurnActionComp.get(entityB).turnAction;
			TurnActionQueueItemComp qA = Comp.TurnActionQueueItemComp.get(entityA);
			TurnActionQueueItemComp qB = Comp.TurnActionQueueItemComp.get(entityB);

			if(tA.priority != tB.priority) {
				if(tA.priority > tB.priority) {
					return -1;
				}
				return 1;
			}

			if(qA == null && qB == null) {
				return 0;
			}

			if(qA == null && qB != null) {
				return 1;
			}

			if(qA != null && qB == null) {
				return -1;
			}

			return 0;
		}

	}
}
