/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.grid;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.google.common.collect.Maps;
import com.google.common.eventbus.EventBus;
import com.happydroids.droidtowers.TowerConsts;
import com.happydroids.droidtowers.entities.GameLayer;
import com.happydroids.droidtowers.entities.GridObject;
import com.happydroids.droidtowers.entities.GuavaSet;
import com.happydroids.droidtowers.events.GameGridResizeEvent;
import com.happydroids.droidtowers.events.GridObjectAddedEvent;
import com.happydroids.droidtowers.events.GridObjectRemovedEvent;
import com.happydroids.droidtowers.math.GridPoint;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class GameGrid extends GameLayer {
  private EventBus eventBus = new EventBus(GameGrid.class.getSimpleName());

  protected Vector2 gridSize;
  private GuavaSet<GridObject> objects;
  private Vector2 worldSize;
  protected GameGridRenderer gameGridRenderer;
  private Map<Class, GuavaSet<GridObject>> gridObjectsByType;
  private GridObject selectedGridObject;
  private GridObject transitGridObjectA;
  private GridObject transitGridObjectB;
  private float highestPoint;
  protected GridPositionCache positionCache;
  private Vector2 gridOrigin;
  protected Rectangle worldBounds;
  protected float gridScale;

  public GameGrid(OrthographicCamera camera) {
    this();
    gameGridRenderer = new GameGridRenderer(this, camera);
  }

  public GameGrid() {
    setTouchEnabled(true);

    gridObjectsByType = new HashMap<Class, GuavaSet<GridObject>>();
    objects = new GuavaSet<GridObject>(25);
    positionCache = new GridPositionCache(this);

    gridSize = new Vector2(8, 8);
    gridOrigin = new Vector2();
    gridScale = 1f;

    updateWorldSize();
  }

  public void updateWorldSize() {
    worldSize = new Vector2(gridSize.x * TowerConsts.GRID_UNIT_SIZE * gridScale, gridSize.y * TowerConsts.GRID_UNIT_SIZE * gridScale);
    worldBounds = new Rectangle(gridOrigin.x, gridOrigin.y, worldSize.x, worldSize.y);
    events().post(new GameGridResizeEvent(this));
  }

  public void setGridSize(int width, int height) {
    gridSize.set(width, height);
  }

  public void setGridSize(float x, float y) {
    setGridSize((int) x, (int) y);
  }

  public GameGridRenderer getRenderer() {
    return gameGridRenderer;
  }

  public Vector2 getWorldSize() {
    return worldSize.cpy();
  }

  public Vector2 getGridSize() {
    return gridSize;
  }

  public boolean addObject(GridObject gridObject) {
    objects.add(gridObject);

    GuavaSet<GridObject> gridObjectHashSet;
    if (!gridObjectsByType.containsKey(gridObject.getClass())) {
      gridObjectHashSet = new GuavaSet<GridObject>();
      gridObjectsByType.put(gridObject.getClass(), gridObjectHashSet);
    } else {
      gridObjectHashSet = gridObjectsByType.get(gridObject.getClass());
    }

    gridObjectHashSet.add(gridObject);

    float objectYPos = gridObject.getPosition().y;
    if (objectYPos > highestPoint) {
      highestPoint = objectYPos;
      if (highestPoint + TowerConsts.GAME_GRID_EXPAND_LAND_SIZE > gridSize.y) {
        gridSize.y = highestPoint + TowerConsts.GAME_GRID_EXPAND_LAND_SIZE;
        updateWorldSize();
      }
    }

    events().post(new GridObjectAddedEvent(gridObject));

    return true;
  }

  public GuavaSet<GridObject> getObjects() {
    return objects;
  }

  public boolean canObjectBeAt(GridObject gridObject) {
    if (!gridObject.canBeAt()) {
      return false;
    }

    Rectangle boundsOfGridObjectToCheck = gridObject.getBounds();
    for (GridObject child : objects) {
      if (child != gridObject) {
        if (child.getBounds().contains(boundsOfGridObjectToCheck) && !child.canShareSpace(gridObject)) {
          return false;
        }
      }
    }

    return true;
  }

  public void removeObject(GridObject gridObject) {
    objects.remove(gridObject);

    events().post(new GridObjectRemovedEvent(gridObject));
  }

  public void update(float deltaTime) {
//    HACK: have to figure out a better way to clear out previously selected grid objects, until then...
    if (selectedGridObject != null && !Gdx.input.isTouched()) {
      selectedGridObject.touchUp();
      selectedGridObject = null;
    }

    for (GridObject gridObject : objects) {
      gridObject.update(deltaTime);
    }
  }

  public GuavaSet<GridObject> getInstancesOf(Class aClass) {
    if (gridObjectsByType.containsKey(aClass)) {
      return gridObjectsByType.get(aClass);
    }

    return null;
  }

  public GuavaSet<GridObject> getInstancesOf(Class... classes) {
    GuavaSet<GridObject> found = new GuavaSet<GridObject>();
    if (classes != null) {
      for (Class otherClass : classes) {
        if (gridObjectsByType.containsKey(otherClass)) {
          found.addAll(getInstancesOf(otherClass));
        }
      }
    }

    return found;
  }

  // TODO: GROT!
  public GuavaSet<GridObject> getInstancesOf(Set<Class<? extends GridObject>> classes) {
    GuavaSet<GridObject> found = new GuavaSet<GridObject>();
    if (classes != null) {
      for (Class otherClass : classes) {
        if (gridObjectsByType.containsKey(otherClass)) {
          found.addAll(getInstancesOf(otherClass));
        }
      }
    }

    return found;
  }

  @Override
  public boolean tap(Vector2 worldPoint, int count) {
    GridPoint gridPointAtFinger = closestGridPoint(worldPoint);

    Set<GridObject> gridObjects = positionCache.getObjectsAt(gridPointAtFinger, new Vector2(1, 1));
    for (GridObject gridObject : gridObjects) {
      if (gridObject.tap(gridPointAtFinger, count)) {
        return true;
      }
    }

    return false;
  }

  public GridPoint closestGridPoint(Vector2 worldPoint) {
    return closestGridPoint(worldPoint.x, worldPoint.y);
  }

  @Override
  public boolean touchDown(Vector2 worldPoint, int pointer) {
    GridPoint gameGridPoint = closestGridPoint(worldPoint);

    Set<GridObject> gridObjects = positionCache.getObjectsAt(gameGridPoint, new Vector2(1, 1));
    for (GridObject gridObject : gridObjects) {
      if (gridObject.touchDown(gameGridPoint, worldPoint, pointer)) {
        selectedGridObject = gridObject;
        return true;
      }
    }

    selectedGridObject = null;

    return false;
  }


  @Override
  public boolean pan(Vector2 worldPoint, Vector2 deltaPoint) {
    if (selectedGridObject != null) {
      GridPoint gridPointAtFinger = closestGridPoint(worldPoint);
      GridPoint gridPointDelta = closestGridPoint(deltaPoint);
      if (selectedGridObject.pan(gridPointAtFinger, gridPointDelta)) {
        return true;
      }
    }

    selectedGridObject = null;

    return false;
  }

  public GridPoint closestGridPoint(float x, float y) {
    float gridX = (float) Math.floor((int) (x - gridOrigin.x) / TowerConsts.GRID_UNIT_SIZE);
    float gridY = (float) Math.floor((int) (y - gridOrigin.y) / TowerConsts.GRID_UNIT_SIZE);

    gridX = Math.max(0, Math.min(gridX, gridSize.x - 1));
    gridY = Math.max(0, Math.min(gridY, gridSize.y - 1));

    return new GridPoint(gridX, gridY);
  }

  public EventBus events() {
    return eventBus;
  }

  public void clearObjects() {
    gridObjectsByType = Maps.newHashMap();
    objects = new GuavaSet<GridObject>();
    positionCache = new GridPositionCache(this);
  }

  public boolean isEmpty() {
    return objects.isEmpty();
  }

  public GridPositionCache positionCache() {
    return positionCache;
  }

  public void setGridOrigin(Vector2 gridOrigin) {
    this.gridOrigin.set(gridOrigin);
  }

  public Vector2 getGridOrigin() {
    return gridOrigin;
  }

  public Rectangle getWorldBounds() {
    return worldBounds;
  }

  public float getGridScale() {
    return gridScale;
  }

  public void setGridScale(float gridScale) {
    this.gridScale = gridScale;
  }

  public float toWorldSpace(int gridSpaces) {
    return TowerConsts.GRID_UNIT_SIZE * gridSpaces * gridScale;
  }
}
