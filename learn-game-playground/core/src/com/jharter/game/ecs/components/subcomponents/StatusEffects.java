package com.jharter.game.ecs.components.subcomponents;

import com.jharter.game.primitives.Array_;
import com.jharter.game.primitives.int_;

import uk.co.carelesslabs.Enums.StatusEffectType;

public class StatusEffects implements Pendable<StatusEffects> {

	public final Array_<StatusEffectType> types = new Array_<>();
	public final Array_<StatusEffectType> weakToTypes = new Array_<>();
	public final Array_<StatusEffectType> resistantToTypes = new Array_<>();
	public final Array_<StatusEffectType> immuneToTypes = new Array_<>();

	public final int_ confused = new int_().d(0);
	public final int_ skip = new int_().d(0);

	public int maxEffects = 1;

	public StatusEffects() {

	}

	@Override
	public void setToDefault() {
		types.setToDefault();
		weakToTypes.setToDefault();
		resistantToTypes.setToDefault();
		immuneToTypes.setToDefault();
		confused.setToDefault();
		skip.setToDefault();
	}

	@Override
	public void resetPending() {
		types.resetPending();
		weakToTypes.resetPending();
		resistantToTypes.resetPending();
		immuneToTypes.resetPending();
		confused.resetPending();
		skip.resetPending();
	}

	@Override
	public void clear() {
		types.clear();
		weakToTypes.clear();
		resistantToTypes.clear();
		immuneToTypes.clear();
		maxEffects = 1;
		confused.clear();
		skip.clear();
	}

	public boolean isAtCapacity() {
		return types.v().size >= maxEffects;
	}
}
