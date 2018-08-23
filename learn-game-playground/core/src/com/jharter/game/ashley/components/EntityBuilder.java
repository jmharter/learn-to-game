package com.jharter.game.ashley.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.badlogic.gdx.utils.Pools;
import com.jharter.game.ashley.components.Components.ActionQueuedComp;
import com.jharter.game.ashley.components.Components.ActionReadyComp;
import com.jharter.game.ashley.components.Components.ActionSpentComp;
import com.jharter.game.ashley.components.Components.ActiveCardComp;
import com.jharter.game.ashley.components.Components.AlphaComp;
import com.jharter.game.ashley.components.Components.AnimationComp;
import com.jharter.game.ashley.components.Components.BodyComp;
import com.jharter.game.ashley.components.Components.CardComp;
import com.jharter.game.ashley.components.Components.CollisionComp;
import com.jharter.game.ashley.components.Components.CursorComp;
import com.jharter.game.ashley.components.Components.CursorInputComp;
import com.jharter.game.ashley.components.Components.CursorInputRegulatorComp;
import com.jharter.game.ashley.components.Components.DescriptionComp;
import com.jharter.game.ashley.components.Components.FocusComp;
import com.jharter.game.ashley.components.Components.IDComp;
import com.jharter.game.ashley.components.Components.InputComp;
import com.jharter.game.ashley.components.Components.InteractComp;
import com.jharter.game.ashley.components.Components.InvisibleComp;
import com.jharter.game.ashley.components.Components.MultiPositionComp;
import com.jharter.game.ashley.components.Components.PlayerComp;
import com.jharter.game.ashley.components.Components.PositionComp;
import com.jharter.game.ashley.components.Components.RemoveComp;
import com.jharter.game.ashley.components.Components.SensorComp;
import com.jharter.game.ashley.components.Components.SizeComp;
import com.jharter.game.ashley.components.Components.StatsComp;
import com.jharter.game.ashley.components.Components.TargetPositionComp;
import com.jharter.game.ashley.components.Components.TextureComp;
import com.jharter.game.ashley.components.Components.TileComp;
import com.jharter.game.ashley.components.Components.TurnActionComp;
import com.jharter.game.ashley.components.Components.TurnTimerComp;
import com.jharter.game.ashley.components.Components.TypeComp;
import com.jharter.game.ashley.components.Components.UntargetableComp;
import com.jharter.game.ashley.components.Components.VelocityComp;
import com.jharter.game.ashley.components.Components.VitalsComp;
import com.jharter.game.ashley.components.Components.ZoneComp;
import com.jharter.game.ashley.components.Components.ZonePositionComp;

public class EntityBuilder implements Poolable {
	
	public static EntityBuilder create(PooledEngine engine) {
		EntityBuilder builder = Pools.get(EntityBuilder.class).obtain();
		builder.init(engine);
		return builder;
	}
	 
	private ObjectMap<Class<? extends Component>, Component> comps = new ObjectMap<Class<? extends Component>, Component>();
	private PooledEngine engine;
	private Entity entity;
	
	private EntityBuilder() {}
	
	private void init(PooledEngine engine) {
		this.engine = engine;
		this.entity = engine.createEntity();
	}
	
	public Entity Entity() {
		return entity;
	}
	
	@SuppressWarnings("unchecked")
	public <T extends Component> T get(Class<T> componentClass) {
		if(!comps.containsKey(componentClass)) {
			T comp = engine.createComponent(componentClass);
			entity.add(comp);
			comps.put(componentClass, comp);
			return comp;
		}
		return (T) comps.get(componentClass);
	}
	
	public Family getFamily() {
		Array<Class<? extends Component>> classes = new Array<Class<? extends Component>>();
		for(Class<? extends Component> klass : comps.keys()) {
			if(comps.get(klass) != null) {
				classes.add(klass);
			}
		}
		return Family.all((Class<? extends Component>[])classes.toArray()).get();
	}
	
	public void free() {
		Pools.get(EntityBuilder.class).free(this);
	}
	
	@Override
	public void reset() {
		entity = null;
		engine = null;
		comps.clear();
	}

	public PlayerComp PlayerComp() { return get(PlayerComp.class); }
	public FocusComp FocusComp() { return get(FocusComp.class); }
	public IDComp IDComp() { return get(IDComp.class); }
	public PositionComp PositionComp() { return get(PositionComp.class); }
	public SizeComp SizeComp() { return get(SizeComp.class); }
	public TypeComp TypeComp() { return get(TypeComp.class); }
	public TileComp TileComp() { return get(TileComp.class); }
	public TextureComp TextureComp() { return get(TextureComp.class); }
	public AnimationComp AnimationComp() { return get(AnimationComp.class); }
	public BodyComp BodyComp() { return get(BodyComp.class); }
	public SensorComp SensorComp() { return get(SensorComp.class); }
	public TargetPositionComp TargetPositionComp() { return get(TargetPositionComp.class); }
	public VelocityComp VelocityComp() { return get(VelocityComp.class); }
	public CollisionComp CollisionComp() { return get(CollisionComp.class); }
	public RemoveComp RemoveComp() { return get(RemoveComp.class); }
	public InputComp InputComp() { return get(InputComp.class); }
	public InteractComp InteractComp() { return get(InteractComp.class); }
	public InvisibleComp InvisibleComp() { return get(InvisibleComp.class); }
	public CursorComp CursorComp() { return get(CursorComp.class); }
	public CursorInputComp CursorInputComp() { return get(CursorInputComp.class); }
	public CursorInputRegulatorComp CursorInputRegulatorComp() { return get(CursorInputRegulatorComp.class); }
	public ZoneComp ZoneComp() { return get(ZoneComp.class); }
	public ZonePositionComp ZonePositionComp() { return get(ZonePositionComp.class); }
	public CardComp CardComp() { return get(CardComp.class); }
	public ActiveCardComp ActiveCardComp() { return get(ActiveCardComp.class); }
	public TurnActionComp TurnActionComp() { return get(TurnActionComp.class); }
	public DescriptionComp DescriptionComp() { return get(DescriptionComp.class); }
	public VitalsComp VitalsComp() { return get(VitalsComp.class); }
	public StatsComp StatsComp() { return get(StatsComp.class); }
	public MultiPositionComp MultiPositionComp() { return get(MultiPositionComp.class); }
	public ActionReadyComp ActionReadyComp() { return get(ActionReadyComp.class); }
	public ActionQueuedComp ActionQueuedComp() { return get(ActionQueuedComp.class); }
	public ActionSpentComp ActionSpentComp() { return get(ActionSpentComp.class); }
	public TurnTimerComp TurnTimerComp() { return get(TurnTimerComp.class); }
	public UntargetableComp UntargetableComp() { return get(UntargetableComp.class); }
	public AlphaComp AlphaComp() { return get(AlphaComp.class); }
	
}
