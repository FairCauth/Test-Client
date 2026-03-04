package com.test.mod.module.modules.movement;

import com.test.mod.language.Language;
import com.test.mod.language.Text;
import com.test.mod.module.AbstractModule;
import com.test.mod.module.Category;
import com.test.mod.module.annotation.ModuleInfo;
import com.test.mod.setting.annotation.SettingInfo;
import com.test.mod.setting.settings.BooleanSetting;
import com.test.mod.setting.settings.ModeSetting;

import java.util.Arrays;

@ModuleInfo(name = {
        @Text(label = "NoSlow", language = Language.English),
        @Text(label = "无减速", language = Language.Chinese)
}, category = Category.MOVEMENT)
public class NoSlow extends AbstractModule {
    @SettingInfo(name = {
            @Text(label = "Mode", language = Language.English),
            @Text(label = "模式", language = Language.Chinese)
    })
    public final ModeSetting mode = new ModeSetting("Grim", Arrays.asList("Vanilla", "Grim", "Heypixel"));

    @SettingInfo(name = {
            @Text(label = "Bow", language = Language.English),
            @Text(label = "弓", language = Language.Chinese)
    })
    public final BooleanSetting bow = new BooleanSetting(false);

    @SettingInfo(name = {
            @Text(label = "Crossbow", language = Language.English),
            @Text(label = "冲击弓", language = Language.Chinese)
    })
    public final BooleanSetting crossbow = new BooleanSetting(false);

    @SettingInfo(name = {
            @Text(label = "Trident", language = Language.English),
            @Text(label = "三叉戟", language = Language.Chinese)
    })
    public final BooleanSetting trident = new BooleanSetting(false);

    @SettingInfo(name = {
            @Text(label = "Food", language = Language.English),
            @Text(label = "食物", language = Language.Chinese)
    })
    public final BooleanSetting food = new BooleanSetting(false);

    @SettingInfo(name = {
            @Text(label = "Potion", language = Language.English),
            @Text(label = "药水", language = Language.Chinese)
    })
    public final BooleanSetting potion = new BooleanSetting(false);

    public NoSlow() {
        registerSetting(mode, bow, crossbow, trident, food, potion);
    }
}
