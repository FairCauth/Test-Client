package com.test.mod.module.modules.movement;

import com.test.mod.language.Language;
import com.test.mod.language.Text;
import com.test.mod.module.AbstractModule;
import com.test.mod.module.Category;
import com.test.mod.module.annotation.ModuleInfo;
import com.test.mod.setting.annotation.SettingInfo;
import com.test.mod.setting.settings.BooleanSetting;
import com.test.mod.setting.settings.ColorSetting;
import com.test.mod.setting.settings.NumberSetting;

import java.awt.*;
@ModuleInfo(name = {
        @Text(label = "BackTrack", language = Language.English),
        @Text(label = "回溯", language = Language.Chinese)
}, category = Category.MOVEMENT)
public class Backtrack extends AbstractModule {
    @SettingInfo(name = {
            @Text(label = "Range", language = Language.English),
            @Text(label = "范围", language = Language.Chinese)
    })
    private final NumberSetting range = new NumberSetting(1.0, 0.0, 10.0, "#.0");

    @SettingInfo(name = {
            @Text(label = "Delay", language = Language.English),
            @Text(label = "延迟", language = Language.Chinese)
    })
    private final NumberSetting delay = new NumberSetting(150.0, 0.0, 5000.0, "#.0");

    @SettingInfo(name = {
            @Text(label = "FilterVelocity", language = Language.English),
            @Text(label = "过滤反击退", language = Language.Chinese)
    })
    private final BooleanSetting filterVelocity = new BooleanSetting(false);

    @SettingInfo(name = {
            @Text(label = "NextBacktrackDelay", language = Language.English),
            @Text(label = "下一次回溯延迟", language = Language.Chinese)
    })
    private final NumberSetting nextBacktrackDelay = new NumberSetting(0.0, 0.0, 1000.0, "#.0");

    @SettingInfo(name = {
            @Text(label = "Chance", language = Language.English),
            @Text(label = "几率", language = Language.Chinese)
    })
    private final NumberSetting chance = new NumberSetting(100.0, 0.0, 100.0, "#.0");

    @SettingInfo(name = {
            @Text(label = "PauseOnHurtTime", language = Language.English),
            @Text(label = "受伤时暂停", language = Language.Chinese)
    })
    private final BooleanSetting pauseOnHurtTime = new BooleanSetting(false);

    @SettingInfo(name = {
            @Text(label = "HurtTime", language = Language.English),
            @Text(label = "受伤时间", language = Language.Chinese)
    })
    private final NumberSetting hurtTimeValue = new NumberSetting(5.0, 0.0, 10.0, "#.0");

    @SettingInfo(name = {
            @Text(label = "LastAttackTimeToWork", language = Language.English),
            @Text(label = "上次攻击到工作的时间", language = Language.Chinese)
    })
    private final NumberSetting lastAttackTimeToWork = new NumberSetting(800.0, 0.0, 5000.0, "#.0");

    @SettingInfo(name = {
            @Text(label = "BacktrackColor", language = Language.English),
            @Text(label = "回溯位置颜色", language = Language.Chinese)
    })
    private final ColorSetting backtrackColor = new ColorSetting(new Color(65, 67, 67, 60));

    @SettingInfo(name = {
            @Text(label = "ActualPosColor", language = Language.English),
            @Text(label = "实际位置颜色", language = Language.Chinese)
    })
    private final ColorSetting actualPosColor = new ColorSetting(new Color(255, 255, 255, 60));

    @SettingInfo(name = {
            @Text(label = "Show Tracking", language = Language.English),
            @Text(label = "显示追踪", language = Language.Chinese)
    })
    private final BooleanSetting showTracking = new BooleanSetting(false);

    public Backtrack() {
        registerSetting(range, delay, nextBacktrackDelay, chance, pauseOnHurtTime, hurtTimeValue, lastAttackTimeToWork, filterVelocity, backtrackColor, actualPosColor, showTracking);

    }
}
