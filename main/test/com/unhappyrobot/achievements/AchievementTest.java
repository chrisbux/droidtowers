package com.unhappyrobot.achievements;

import com.unhappyrobot.TowerGameTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import static com.unhappyrobot.Expect.expect;

@RunWith(TowerGameTestRunner.class)
public class AchievementTest {
  @Test
  public void toRewardString_shouldOutputProperMessageForGives() {
    Achievement achievement = new Achievement("Sample");
    achievement.addReward(new AchievementReward(RewardType.GIVE, AchievementThing.MONEY, 100));

    expect(achievement.toRewardString()).toEqual("Complete: Sample!\nReceived ¢ 100\n");
  }

  @Test
  public void toRewardString_shouldOutputProperMessageForUnlocks() {
    Achievement achievement = new Achievement("Sample");
    achievement.addReward(new AchievementReward(RewardType.UNLOCK, AchievementThing.MAIDS_OFFICE));

    expect(achievement.toRewardString()).toEqual("Complete: Sample!\nUnlocked Maid's Closet\n");
  }

  @Test
  public void toRewardString_shouldHandleMultipleRewards() {
    Achievement achievement = new Achievement("Sample");
    achievement.addReward(new AchievementReward(RewardType.GIVE, AchievementThing.MONEY, 100));
    achievement.addReward(new AchievementReward(RewardType.UNLOCK, AchievementThing.MAIDS_OFFICE));

    expect(achievement.toRewardString()).toEqual("Complete: Sample!\nReceived ¢ 100\nUnlocked Maid's Closet\n");
  }
}
