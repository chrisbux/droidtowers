package com.unhappyrobot;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.tablelayout.Table;
import com.unhappyrobot.entities.*;
import com.unhappyrobot.graphics.BackgroundLayer;
import com.unhappyrobot.gui.Dialog;
import com.unhappyrobot.gui.HeadsUpDisplay;
import com.unhappyrobot.gui.OnClickCallback;
import com.unhappyrobot.input.Action;
import com.unhappyrobot.input.CameraController;
import com.unhappyrobot.input.InputSystem;
import com.unhappyrobot.utils.Random;

import java.util.ArrayList;
import java.util.List;

import static com.unhappyrobot.input.InputSystem.Keys;

public class TowerGame implements ApplicationListener {
  private OrthographicCamera camera;
  private SpriteBatch spriteBatch;
  private BitmapFont menloBitmapFont;

  private static List<GameLayer> layers;
  private Matrix4 matrix;

  private Matrix4 hudProjectionMatrix;
  private final GameGrid gameGrid = new GameGrid();
  private GameGridRenderer gameGridRenderer;
  private GestureDetector gestureDetector;
  private CameraController cameraController;
  private static TweenManager tweenManager;
  private static TowerGame instance;
  private GridObject mouseRat;
  private Skin uiSkin;
  private Button testButton;
  private Stage guiLayer;
  private BitmapFont defaultBitmapFont;
  private Dialog exitDialog = null;

  public void create() {
    instance = this;

    Random.init();
    Tween.setPoolEnabled(true);

    tweenManager = new TweenManager();


    int width = Gdx.graphics.getWidth();
    int height = Gdx.graphics.getHeight();
    camera = new OrthographicCamera(width, height);
    spriteBatch = new SpriteBatch(100);
    hudProjectionMatrix = spriteBatch.getProjectionMatrix().cpy();

    Thread.currentThread().setPriority(Thread.MAX_PRIORITY);

    layers = new ArrayList<GameLayer>();

    menloBitmapFont = new BitmapFont(Gdx.files.internal("fonts/menlo_16.fnt"), Gdx.files.internal("fonts/menlo_16.png"), false);
    defaultBitmapFont = new BitmapFont(Gdx.files.internal("default.fnt"), Gdx.files.internal("default.png"), false);

    gameGrid.setGridOrigin(0, 0);
    gameGrid.setUnitSize(64, 64);
    gameGrid.setGridSize(20, 20);
    gameGrid.resizeGrid();
    gameGrid.setGridColor(0.1f, 0.1f, 0.1f, 0.1f);

    uiSkin = new Skin(Gdx.files.internal("default-skin.ui"), Gdx.files.internal("default-skin.png"));
    guiLayer = new Stage(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);

    HeadsUpDisplay.getInstance().initialize(guiLayer, uiSkin, camera, gameGrid);

    BackgroundLayer groundLayer = new BackgroundLayer("backgrounds/ground.png");
    groundLayer.setSize(gameGrid.getWorldSize().x, 256f);
    groundLayer.setWrap(Texture.TextureWrap.Repeat);
    layers.add(groundLayer);

    BackgroundLayer skyLayer = new BackgroundLayer("backgrounds/bluesky.png");
    skyLayer.setPosition(0, 256);
    skyLayer.setSize(gameGrid.getWorldSize().x, 256f);
    skyLayer.setWrap(Texture.TextureWrap.Repeat);
    layers.add(skyLayer);

    CloudLayer cloudLayer = new CloudLayer(gameGrid.getWorldSize());
    layers.add(cloudLayer);

    gameGridRenderer = gameGrid.getRenderer();
    layers.add(gameGridRenderer);

    InputSystem.getInstance().setup(camera, gameGrid);
    InputSystem.getInstance().addInputProcessor(guiLayer, 10);
    Gdx.input.setInputProcessor(InputSystem.getInstance());

    Gdx.input.setCatchBackKey(true);
    Gdx.input.setCatchMenuKey(true);

    InputSystem.getInstance().bind(Keys.W, new Action() {
      public void run(float timeDelta) {
        camera.position.add(0, 3, 0);
      }
    });

    InputSystem.getInstance().bind(Keys.S, new Action() {
      public void run(float timeDelta) {
        camera.position.add(0, -3, 0);
      }
    });

    InputSystem.getInstance().bind(Keys.G, new Action() {
      public void run(float timeDelta) {
        gameGridRenderer.toggleGridLines();
      }
    });

    InputSystem.getInstance().bind(new int[]{Keys.BACK, Keys.ESCAPE}, new Action() {
      public void run(float timeDelta) {
        if (exitDialog != null) {
          exitDialog.dismiss();
          exitDialog = null;
          return;
        }

        exitDialog = new Dialog().setTitle("Awww, don't leave me.").setMessage("Are you sure you want to exit the game?").addButton("Yes", new OnClickCallback() {
          @Override
          public void onClick(Dialog dialog) {
            Gdx.app.exit();
          }
        }).addButton("No way!", new OnClickCallback() {
          @Override
          public void onClick(Dialog dialog) {
            dialog.dismiss();
            exitDialog = null;
          }
        }).centerOnScreen().show();
      }
    });
  }

  public void render() {
    GL10 gl = Gdx.graphics.getGL10();
    gl.glClearColor(0.48f, 0.729f, 0.870f, 1.0f);
    gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
    gl.glEnable(GL10.GL_BLEND);
    gl.glEnable(GL10.GL_TEXTURE_2D);

    float deltaTime = Gdx.graphics.getDeltaTime();

    InputSystem.getInstance().update(deltaTime);
    camera.update();

    spriteBatch.setProjectionMatrix(camera.combined);

    gl.glColor4f(1, 1, 1, 1);
    for (GameLayer layer : layers) {
      layer.render(spriteBatch, camera);
    }

    spriteBatch.setProjectionMatrix(hudProjectionMatrix);
    spriteBatch.begin();
    String infoText = String.format("fps: %d, camera(%.1f, %.1f, %.1f)", Gdx.graphics.getFramesPerSecond(), camera.position.x, camera.position.y, camera.zoom);
    menloBitmapFont.draw(spriteBatch, infoText, 5, 23);

    float javaHeapInBytes = Gdx.app.getJavaHeap() / 1048576.0f;
    float nativeHeapInBytes = Gdx.app.getNativeHeap() / 1048576.0f;
    menloBitmapFont.draw(spriteBatch, String.format("mem: (java %.2f Mb, native %.2f Mb)", javaHeapInBytes, nativeHeapInBytes), 5, 50);

    spriteBatch.end();

    guiLayer.draw();
    Table.drawDebug(guiLayer);

    update();
  }

  public void update() {
    float deltaTime = Gdx.graphics.getDeltaTime();

    DeferredManager.onGameThread().update(deltaTime);

    for (GameLayer layer : layers) {
      layer.update(deltaTime);
    }

    tweenManager.update();

    guiLayer.act(deltaTime);
  }

  public void resize(int width, int height) {
    Gdx.app.log("lifecycle", "resizing!");
    spriteBatch.getProjectionMatrix().setToOrtho2D(0, 0, width, height);
  }

  public void pause() {
    Gdx.app.log("lifecycle", "pausing!");
  }

  public void resume() {
    Gdx.app.log("lifecycle", "resuming!");
  }

  public void dispose() {
    spriteBatch.dispose();
    menloBitmapFont.dispose();
  }

  public static List<GameLayer> getLayers() {
    return layers;
  }

  public static TweenManager getTweenManager() {
    return tweenManager;
  }

  public static TowerGame getInstance() {
    return instance;
  }
}