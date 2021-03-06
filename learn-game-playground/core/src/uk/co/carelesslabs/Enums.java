package uk.co.carelesslabs;

import com.badlogic.gdx.graphics.Color;
import com.jharter.game.effect.Effect;

public class Enums {

    public enum TileType {
        GRASS,
        WATER,
        CLIFF
    }

    public enum EntityType {
        HERO,
        TREE,
        BIRD,
        TILE,
        CARD,
        FRIEND,
        CURSOR,
        BACKGROUND,
        ENEMY,
        PLAYER
    }

    public enum EnityState {
        NONE,
        IDLE,
        FEEDING,
        WALKING,
        FLYING,
        HOVERING,
        LANDING
    }

    public enum MenuState {
        ACTIVE,
        DISABLED,
        HOVEROVER,
        CLICKED
    }

    public enum ZoneType {
    	HAND,
    	ENEMY,
    	ENEMY_ACTIVE_CARD,
    	FRIEND,
    	FRIEND_ACTIVE_CARD,
    	DECK,
    	DISCARD,
    	INFO,
    	CURSOR,
    	NONE
    }

    @Deprecated
    public enum CardType {
    	TARGET_ENEMY,
    	TARGET_FRIEND,
    	TARGET_CARD,
    	TARGET_FRIEND_THEN_ENEMY,
    	NONE
    }

    public enum CardOwnerAction {
		RESET_CARDS,
		DISCARD_HAND,
		FILL_HAND;
	}

    /*public enum TurnPhase {
    	START_BATTLE,
    	START_TURN,
    	SELECT_ENEMY_ACTIONS,
    	SELECT_FRIEND_ACTIONS,
    	PERFORM_FRIEND_ACTIONS,
    	PERFORM_ENEMY_ACTIONS,
    	END_TURN,
    	END_BATTLE,
    	NONE
    }*/

    public enum Direction {
    	NORTH,
    	SOUTH,
    	EAST,
    	WEST,
    	NORTH_EAST,
    	NORTH_WEST,
    	SOUTH_EAST,
    	SOUTH_WEST,
    	CENTER,
    	NONE,
    }

    public enum StatusEffectType {
    	FIRE(Color.RED, null),
    	ICE(Color.BLUE, null),
    	LIGHTNING(Color.YELLOW, null),
    	POISON(Color.PURPLE, null);

    	private Color color;
    	private Effect effect;

    	private StatusEffectType(Color color, Effect effect) {
    		this.color = color;
    		this.effect = effect;
    	}

    	public Color getColor() {
    		return color;
    	}

    	public Effect getEffect() {
    		return effect;
    	}

    }

}
