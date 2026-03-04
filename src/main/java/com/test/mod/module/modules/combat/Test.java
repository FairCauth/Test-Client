package com.test.mod.module.modules.combat;

import com.test.mod.language.Language;
import com.test.mod.language.Text;
import com.test.mod.module.AbstractModule;
import com.test.mod.module.Category;
import com.test.mod.module.annotation.ModuleInfo;
import com.test.mod.setting.annotation.SettingInfo;
import com.test.mod.setting.settings.BooleanSetting;
import com.test.mod.setting.settings.ModeSetting;
import com.test.mod.setting.settings.NumberSetting;
import org.lwjgl.glfw.GLFW;

import java.util.Arrays;

@ModuleInfo(name =
@Text(label = "Test", language = Language.English),
        category = Category.COMBAT
)
public class Test extends AbstractModule {
    @SettingInfo(name = {
            @Text(label = "Health", language = Language.English),
            @Text(label = "血量", language = Language.Chinese)
    })
    private final NumberSetting healthValue = new NumberSetting( 3.5d, 1.0d, 20d, "#.00");
    @SettingInfo(name = {
            @Text(label = "Delay", language = Language.English),
            @Text(label = "延迟", language = Language.Chinese)
    })
    private final NumberSetting delayValue = new NumberSetting(150d, 0d, 500d, "#.00");
    @SettingInfo(name = {
            @Text(label = "OpenInv", language = Language.English),
            @Text(label = "打开背包", language = Language.Chinese)
    })
    private final BooleanSetting openInventoryValue = new BooleanSetting(true);
    @SettingInfo(name = {
            @Text(label = "NoAir", language = Language.English),
            @Text(label = "不在空中", language = Language.Chinese)
    })
    private final BooleanSetting noAirValue = new BooleanSetting(true);
    @SettingInfo(name = {
            @Text(label = "Mode", language = Language.English),
            @Text(label = "模式", language = Language.Chinese)
    })
    private final ModeSetting modeValue = new ModeSetting("Normal", Arrays.asList("Normal", "Jump", "Port"));

    public Test() {
        registerSetting(healthValue, delayValue, openInventoryValue, noAirValue, modeValue);
    }
}
