package com.jharter.game.layout;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Pools;
import com.jharter.game.ashley.components.Components.ActiveCardComp;
import com.jharter.game.ashley.components.Components.CursorComp;
import com.jharter.game.ashley.components.Components.IDComp;
import com.jharter.game.ashley.components.Components.MultiPositionComp;
import com.jharter.game.ashley.components.Components.SpriteComp;
import com.jharter.game.ashley.components.Components.TextureComp;
import com.jharter.game.ashley.components.Components.ZoneComp;
import com.jharter.game.ashley.components.Components.ZonePositionComp;
import com.jharter.game.ashley.components.Mapper;
import com.jharter.game.ashley.components.subcomponents.TurnAction;
import com.jharter.game.tween.TweenType;
import com.jharter.game.tween.TweenUtil;
import com.jharter.game.util.id.ID;

import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.equations.Circ;
import uk.co.carelesslabs.Enums.ZoneType;
import uk.co.carelesslabs.Media;

public class CursorPositionSystem extends IteratingSystem {

	@SuppressWarnings("unchecked")
	public CursorPositionSystem() {
		super(Family.all(CursorComp.class, ZonePositionComp.class, SpriteComp.class, TextureComp.class).get());
	}

	@Override
	protected void processEntity(final Entity entity, float deltaTime) {
		CursorComp c = Mapper.CursorComp.get(entity);
		IDComp id = Mapper.IDComp.get(entity);
		SpriteComp s = Mapper.SpriteComp.get(entity);
		ZonePositionComp zp = Mapper.ZonePositionComp.get(entity);
		ZoneComp z = zp.getZoneComp();
		
		float targetAngle = getCursorAngle(entity, z.zoneType);
		
		tempPosition = getCursorPosition(entity, z, zp.index);
		offsetPositionForAngle(tempPosition, s, z.zoneType);
		if(tempPosition != null && !Mapper.AnimatingComp.has(entity)) {
			if(c.lastZoneID != z.zoneID) {
				
				TweenTarget tt = Pools.get(TweenTarget.class).obtain();
				tt.setFromEntity(entity);
				tt.position.x = tempPosition.x;
				tt.position.y = tempPosition.y;
				tt.angleDegrees = targetAngle;
				
				if(!tt.matchesTarget(s)) {
					
					Timeline tween;
					if(isAll(c)) {
						
						float convergeX = s.position.x + (tempPosition.x - s.position.x) * 0.75f;
						float duration = 0.25f;
						
						Timeline single = TweenUtil.tween(id.id, tt, duration);
						Timeline multiA = Timeline.createParallel();
						Timeline multiB = Timeline.createParallel();
						
						float centerY = 0;
						MultiPositionComp mp = getMultiPositionComp(entity);
						for(int i = 0; i < Mapper.ZoneComp.get(zp).objectIDs.size(); i++) {
							tempPosition = getCursorPosition(entity, z, i);
							if(tempPosition != null) {
								centerY += tempPosition.y;
							} else {
								tempPosition = new Vector3();
							}
						}
						centerY /= Mapper.ZoneComp.get(zp).objectIDs.size();
						
						for(int i = 0; i < Mapper.ZoneComp.get(zp).objectIDs.size(); i++) {
							tempPosition = getCursorPosition(entity, z, i);
							offsetPositionForAngle(tempPosition, s, z.zoneType);
							if(tempPosition != null) {
								Vector3 currP = new Vector3(s.position);
								mp.positions.add(currP);
								Vector3 targP = new Vector3(tempPosition);
										
								multiA.push(Tween.to(currP, TweenType.POSITION_XY.asInt(), duration).ease(Circ.INOUT).target(convergeX, centerY));
								multiB.push(Tween.to(currP, TweenType.POSITION_XY.asInt(), duration).ease(Circ.INOUT).target(targP.x, targP.y));
								
							} else {
								tempPosition = new Vector3();
							}
						}
						
						tween = Timeline.createParallel().push(single).push(Timeline.createSequence().push(multiA).push(multiB));
						
					} else {
						removeMultiPositionComp(entity);
						tween = TweenUtil.tween(id.id, tt);
					}
					
					TweenUtil.start(id.id, tween);
				}
				
				Pools.free(tt);
				
			} else {
				s.position.x = tempPosition.x;
				s.position.y = tempPosition.y;
				s.angleDegrees = targetAngle;
				
				if(isAll(c)) {
					MultiPositionComp mp = getMultiPositionComp(entity);
					for(int i = 0; i < Mapper.ZoneComp.get(zp).objectIDs.size(); i++) {
						tempPosition = getCursorPosition(entity, z, i);
						if(tempPosition != null) {
							Vector3 targP = new Vector3(tempPosition);
							mp.positions.add(targP);
						} else {
							tempPosition = new Vector3();
						}
					}
				} else {
					removeMultiPositionComp(entity);
				}
			}
		} else {
			tempPosition = new Vector3();
		}
	}
	
	private MultiPositionComp getMultiPositionComp(Entity entity) {
		MultiPositionComp mp;
		if(Mapper.MultiPositionComp.has(entity)) {
			mp = Mapper.MultiPositionComp.get(entity);
		} else {
			mp = Mapper.Comp.get(MultiPositionComp.class);
			entity.add(mp);
		}
		return mp;
	}
	
	private void removeMultiPositionComp(Entity entity) {
		if(Mapper.MultiPositionComp.has(entity)) {
			entity.remove(MultiPositionComp.class);
		}
	}
	
	private boolean isAll(CursorComp c) {
		TurnAction ta = c.getTurnAction();
		return ta != null && ta.all;
	}
	
	private void setCursorDirection(Entity entity, ZoneType zoneType) {
		TextureComp t = Mapper.TextureComp.get(entity);
		switch(zoneType) {
			case HAND:
				t.region = Media.handPointDown;
				break;
			case FRIEND:
			case ACTIVE_CARD:
				t.region = Media.handPointRight;
				break;
			case ENEMY:
				t.region = Media.handPointLeft;
				break;
			default:
				t.region = Media.handPointDown;
				break;
		}
	}
	
	private float getCursorAngle(Entity entity, ZoneType zoneType) {
		switch(zoneType) {
			case FRIEND:
			case ACTIVE_CARD:
				return 90f;
			case ENEMY:
				return 270f;
			case HAND:
			default:
				return 0f;
		}
	}
	
	private void offsetPositionForAngle(Vector3 position, SpriteComp s, ZoneType zoneType) {
		if(position == null) {
			return;
		}
		switch(zoneType) {
			case FRIEND:
			case ACTIVE_CARD:
				position.x += s.scaledWidth();
				break;
			case ENEMY:
				position.y += s.scaledHeight();
				break;
			case HAND:
			default:
				break;
		}
	}
	
	private Vector3 tempPosition = new Vector3();
	
	private Vector3 getCursorPosition(Entity entity, ZoneComp z, int index) {
		if(!z.hasIndex(index)) {
			return null;
		}
		
		ID cursorTargetID = z.objectIDs.get(index);
		Entity target = Mapper.Entity.get(cursorTargetID);
		if(target == null) {
			return null;
		}
		
		TweenTarget lTarget = z.layout.getTarget(cursorTargetID);
		SpriteComp sTarget = Mapper.SpriteComp.get(target);
		
		if(lTarget == null || sTarget == null) {
			return null;
		}
		
		SpriteComp s = Mapper.SpriteComp.get(entity);

		switch(z.zoneType) {
			case HAND:
				tempPosition.x = lTarget.position.x + (sTarget.scaledWidth() - s.scaledWidth()) /2;
				tempPosition.y = lTarget.position.y + sTarget.scaledHeight() - (int) (s.scaledHeight() * 0.25);
				break;
			case FRIEND:
				ActiveCardComp ac = Mapper.ActiveCardComp.get(target);
				float cardOffset = 0;
				if(ac != null && ac.activeCardID != null) {
					Entity card = Mapper.Entity.get(ac.activeCardID);
					if(card != null) {
						SpriteComp sCard = Mapper.SpriteComp.get(card);
						cardOffset = sCard.scaledWidth(0.25f) + 20;
					}
				}
				tempPosition.x = lTarget.position.x - s.scaledWidth() - 20 - cardOffset;
				tempPosition.y = lTarget.position.y + (sTarget.scaledHeight() - s.scaledHeight()) / 2;
				break;
			case ACTIVE_CARD:
				tempPosition.x = lTarget.position.x - s.scaledWidth() - 20;
				tempPosition.y = lTarget.position.y + (sTarget.scaledHeight() - s.scaledHeight()) / 2;
				break;
			case ENEMY:
				tempPosition.x = lTarget.position.x + sTarget.scaledWidth() + 20;  
				tempPosition.y = lTarget.position.y + (sTarget.scaledHeight() - s.scaledHeight()) / 2;
				break;
			default:
				break;
		}
		
		return tempPosition;
	}
	
}