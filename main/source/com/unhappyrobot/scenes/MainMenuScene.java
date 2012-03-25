package com.unhappyrobot.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.FadeIn;
import com.badlogic.gdx.scenes.scene2d.actions.MoveBy;
import com.badlogic.gdx.scenes.scene2d.actions.ScaleTo;
import com.badlogic.gdx.scenes.scene2d.actions.Sequence;
import com.badlogic.gdx.scenes.scene2d.interpolators.OvershootInterpolator;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.tablelayout.Table;
import com.unhappyrobot.gui.*;
import com.unhappyrobot.utils.Platform;

import java.lang.reflect.Constructor;

public class MainMenuScene extends Scene {
  private static final String TAG = MainMenuScene.class.getSimpleName();
  public static final int BUTTON_WIDTH = 200;

  private SplashCloudLayer cloudLayer;

  @Override
  public void create(Object... args) {
    Table container = new Table(getGuiSkin());
    container.defaults().center().left();

    Label label = LabelStyles.BankGothic64.makeLabel("Droid Towers");
    container.add(label).align(Align.CENTER);
    container.row();

    Label loadingMessage = LabelStyles.Default.makeLabel("Reticulating Splines...");
    container.add(loadingMessage).align(Align.RIGHT);
    container.row().padTop(50);

    TextButton connectFacebookButton = new TextButton("login to happydroids.com", getGuiSkin());
    container.add(connectFacebookButton).fill().maxWidth(BUTTON_WIDTH);
    container.row().padTop(30);

    TextButton newGameButton = new TextButton("new game", getGuiSkin());
    container.add(newGameButton).fill().maxWidth(BUTTON_WIDTH);
    container.row().padTop(15);

    TextButton loadGameButton = new TextButton("load game", getGuiSkin());
    container.add(loadGameButton).fill().maxWidth(BUTTON_WIDTH);
    container.row().padTop(15);

    TextButton optionsButton = new TextButton("options", getGuiSkin());
    container.add(optionsButton).fill().maxWidth(BUTTON_WIDTH);
    container.row().padTop(40);

    TextButton exitGameButton = new TextButton("exit game", getGuiSkin());
    container.add(exitGameButton).fill().maxWidth(BUTTON_WIDTH);
    container.row();


    TextureAtlas atlas = new TextureAtlas(Gdx.files.internal("hud/menus.txt"));
    Image happyDroidsLogo = new Image(atlas.findRegion("happy-droids-logo"));
    happyDroidsLogo.setAlign(Align.CENTER);
    happyDroidsLogo.x = getStage().width() - happyDroidsLogo.width - 5;
    happyDroidsLogo.y = 5;
    happyDroidsLogo.scaleX = happyDroidsLogo.scaleY = 0f;
    happyDroidsLogo.setClickListener(new ClickListener() {
      public void click(Actor actor, float x, float y) {
        Platform.launchWebBrowser("http://www.happydroids.com");
      }
    });
    addActor(happyDroidsLogo);

    happyDroidsLogo.action(ScaleTo.$(1f, 1f, 0.55f)
                                   .setInterpolator(OvershootInterpolator.$(1.75f)));

    container.pack();
    addActor(container);
    center(container);

    container.color.a = 0f;

    container.action(FadeIn.$(0.85f));
    container.action(Sequence.$(
                                       MoveBy.$(0, -container.height, 0f),
                                       MoveBy.$(0, container.height, 0.75f)
                                               .setInterpolator(OvershootInterpolator.$(3f)
                                               )));

    cloudLayer = new SplashCloudLayer();


    connectFacebookButton.setClickListener(new LaunchWindowClickListener(ConnectToHappyDroidsWindow.class));
    newGameButton.setClickListener(new LaunchWindowClickListener(NewGameWindow.class));
    loadGameButton.setClickListener(new LaunchWindowClickListener(LoadGameWindow.class));

    exitGameButton.setClickListener(new ClickListener() {
      public void click(Actor actor, float x, float y) {
        Gdx.app.exit();
      }
    });
  }

  @Override
  public void pause() {
  }

  @Override
  public void resume() {
  }

  @Override
  public void render(float deltaTime) {
    cloudLayer.update(deltaTime);
    cloudLayer.render(getSpriteBatch(), getCamera());
  }

  @Override
  public void dispose() {
  }

  private class LaunchWindowClickListener implements ClickListener {
    private final Class<? extends TowerWindow> aClass;

    public LaunchWindowClickListener(Class<? extends TowerWindow> aClass) {
      this.aClass = aClass;
    }

    public void click(Actor actor, float x, float y) {
      try {
        System.out.println("aClass = " + aClass);
        Constructor<? extends TowerWindow> constructor = aClass.getConstructor(Stage.class, Skin.class);
        TowerWindow window = constructor.newInstance(getStage(), getGuiSkin());
        window.modal(true).show().centerOnStage();
      } catch (Exception e) {
        Gdx.app.error(TAG, "Error loading dialog: " + aClass.getSimpleName(), e);
      }
    }
  }
}