package com.jharter.game.ecs.systems.cursor;

import com.badlogic.ashley.core.Entity;
import com.jharter.game.ecs.components.Components.CursorComp;
import com.jharter.game.ecs.components.Components.CursorInputComp;
import com.jharter.game.ecs.components.Components.CursorInputRegulatorComp;
import com.jharter.game.ecs.components.Components.InputComp;

public class CursorInputSystem extends CursorSystem {

	@SuppressWarnings("unchecked")
	public CursorInputSystem() {
		super(InputComp.class, CursorInputComp.class, CursorInputRegulatorComp.class);
	}
	
	@Override
	public void processEntity(Entity cursor, CursorComp c, float deltaTime) {
		InputComp in = Comp.InputComp.get(cursor);
		CursorInputComp ci = Comp.CursorInputComp.get(cursor);
		CursorInputRegulatorComp cir = Comp.CursorInputRegulatorComp.get(cursor);
		
		if (in.input.isDown()) {
			ci.direction.y++;
		}
		if (in.input.isUp()) {
			ci.direction.y--;
		}
		if (in.input.isLeft()) {
			ci.direction.x--;
		}
		if (in.input.isRight()) {
			ci.direction.x++;
		}
		
		if(ignoreMovement(cir, hasMovement(ci), deltaTime)) {
			ci.direction.setZero();
		}
		
		if(in.input.isAccept()) {
			ci.accept = true;
			in.input.setAccept(false);
		}
		
		if(in.input.isCancel()) {
			ci.cancel = true;
			in.input.setCancel(false);
		}
		
		if(in.input.isPrev()) {
			ci.prev = true;
			in.input.setPrev(false);
		}
		
		if(in.input.isNext()) {
			ci.next = true;
			in.input.setNext(false);
		}
	}
	
	private boolean hasMovement(CursorInputComp ci) {
		return ci.direction.x != 0 || ci.direction.y != 0;
	}
	
	private boolean ignoreMovement(CursorInputRegulatorComp cir, boolean moved, float deltaTime) {
		if(!moved) {
			cir.processedMove = false;
			cir.processedMoveDelta = 0;
			cir.maxProcessedMoveDelta = 0.2f;
			return true;
		} else if(moved && cir.processedMove) {
			cir.processedMoveDelta += deltaTime;
			if(cir.processedMoveDelta < cir.maxProcessedMoveDelta) {
				return true;
			} else if(cir.maxProcessedMoveDelta > 0.005f){
				cir.maxProcessedMoveDelta /= 1.5f;
			}
		}
		cir.processedMove = true;
		cir.processedMoveDelta = 0;
		return false;
	}
	
}