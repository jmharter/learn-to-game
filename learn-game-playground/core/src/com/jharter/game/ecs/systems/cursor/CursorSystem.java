package com.jharter.game.ecs.systems.cursor;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.jharter.game.ecs.components.Components.ActivePlayerComp;
import com.jharter.game.ecs.components.Components.AnimatingComp;
import com.jharter.game.ecs.components.Components.CursorComp;
import com.jharter.game.ecs.components.Components.DisabledComp;
import com.jharter.game.ecs.components.subcomponents.TurnAction;
import com.jharter.game.ecs.systems.boilerplate.FirstSystem;
import com.jharter.game.ecs.systems.boilerplate.GameEntitySystem;
import com.jharter.game.util.ArrayUtil;
import com.jharter.game.util.id.ID;

public abstract class CursorSystem extends FirstSystem {
	
	public CursorSystem(Class<? extends Component>... cursorComps) {
		this(Family.all(GameEntitySystem.combine(cursorComps, CursorComp.class)).exclude(DisabledComp.class, AnimatingComp.class).get());
	}
	
	private CursorSystem(Family family) {
		super(family);
		add(ActivePlayerComp.class, Family.all(ActivePlayerComp.class).get());
	}
	
	@Override
	protected void processEntity(Entity cursor, float deltaTime) {
		processEntity(cursor, Comp.CursorComp.get(cursor), deltaTime);
	}
	
	protected abstract void processEntity(Entity cursor, CursorComp c, float deltaTime);
	
	protected ActivePlayerComp getActivePlayer() {
		return comp(ActivePlayerComp.class);
	}
	
	protected ID getActivePlayerID() {
		ActivePlayerComp a = getActivePlayer();
		return a == null ? null : a.activePlayerID;
	}
	
	protected TurnAction getTurnAction(CursorComp c) {
		Entity taEntity = Comp.Entity.get(c.turnActionID);
		if(taEntity == null) {
			return null;
		}
		return Comp.TurnActionComp.get(taEntity).turnAction;
	}		
	
	protected boolean isDisabled(Entity cursor) {
		return Comp.DisabledComp.has(cursor);
	}
	
	protected boolean nextPlayer() {
		return changePlayer(true);
	}
	
	protected boolean prevPlayer() {
		return changePlayer(false);
	}
	
	protected boolean changePlayer(boolean next) {
		ActivePlayerComp a = getActivePlayer();
		ImmutableArray<ID> playerIDs = getPlayerIDs();
		int i = playerIDs.indexOf(a.activePlayerID, false);
		int counter = 0;
		while(counter < playerIDs.size()) {
			i = ArrayUtil.prevOrNextIndex(playerIDs, i, next);
			ID playerID = playerIDs.get(i);
			if(!a.spentPlayers.contains(playerID, false)) {
				a.activePlayerID = playerID;
				return true;
			}
			counter++;
		}
		return false;
	}
	
}