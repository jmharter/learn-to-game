package com.jharter.game.ashley.entities;

import com.badlogic.ashley.core.PooledEngine;
import com.jharter.game.ashley.components.CompManager;
import com.jharter.game.tween.GameTweenManager;
import com.jharter.game.util.id.IDManager;

public interface IEntityHandler {
	
	public PooledEngine getEngine();
	public EntityToolBox getToolBox();
	public CompManager getCompManager();
	public IDManager getIDManager();
	public GameTweenManager getTweenManager();
	
}