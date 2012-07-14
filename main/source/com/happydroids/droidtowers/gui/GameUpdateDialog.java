/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.happydroids.platform.Platform;
import com.happydroids.server.GameUpdate;

public class GameUpdateDialog extends Dialog {
  public GameUpdateDialog(GameUpdate latestUpdate) {
    super();

    setTitle("An update is available!");
    setMessage("An update to Droid Towers is available:\n\n" + latestUpdate.notes + "\n\nWould you like to update now?");

    switch (Gdx.app.getType()) {
      case Desktop:
        addButton("Yes", new OnClickCallback() {
          @Override
          public void onClick(Dialog dialog) {
            dismiss();
            Platform.getBrowserUtil().launchWebBrowser("https://www.droidtowersgame.com/download-latest");
          }
        });
        break;

      case Android:

        break;
    }

    addButton("Not now", new VibrateClickListener() {
      @Override
      public void onClick(Actor actor, float x, float y) {
        dismiss();
      }
    });
  }
}
