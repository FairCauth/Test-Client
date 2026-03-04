package com.test.mod.module.modules.movement;

import com.test.mod.language.Language;
import com.test.mod.language.Text;
import com.test.mod.module.AbstractModule;
import com.test.mod.module.Category;
import com.test.mod.module.annotation.ModuleInfo;
import com.test.mod.setting.annotation.SettingInfo;
import com.test.mod.setting.settings.BooleanSetting;
import com.test.mod.setting.settings.ModeSetting;
import com.test.mod.setting.settings.NumberSetting;

import java.util.Arrays;
@ModuleInfo(name = {
        @Text(label = "Speed", language = Language.English),
        @Text(label = "速度", language = Language.Chinese)
}, category = Category.MOVEMENT)
public class Speed extends AbstractModule {
    @SettingInfo(name = {
            @Text(label = "Mode", language = Language.English),
            @Text(label = "模式", language = Language.Chinese)
    })
    private final ModeSetting modeValue = new ModeSetting("Vanilla", Arrays.asList("Vanilla", "Grim"));
    @SettingInfo(name = {
            @Text(label = "Speed", language = Language.English),
            @Text(label = "速度", language = Language.Chinese)
    })
    private final NumberSetting speed = new NumberSetting(1.25, 1, 5.0, "#.00");
    @SettingInfo(name = {
            @Text(label = "OnlyAir", language = Language.English),
            @Text(label = "只在空中", language = Language.Chinese)
    })
    private final BooleanSetting onlyAir = new BooleanSetting(false);
    @SettingInfo(name = {
            @Text(label = "Reset", language = Language.English),
            @Text(label = "重置移动", language = Language.Chinese)
    })
    private final BooleanSetting fastStop = new BooleanSetting(false);

    public Speed() {
        registerSetting(modeValue, speed, onlyAir, fastStop);
    }
}
