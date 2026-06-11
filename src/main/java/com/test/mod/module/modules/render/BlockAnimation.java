package com.test.mod.module.modules.render;

import com.test.mod.language.Language;
import com.test.mod.language.Text;
import com.test.mod.module.AbstractModule;
import com.test.mod.module.Category;
import com.test.mod.module.annotation.ModuleInfo;
import com.test.mod.setting.annotation.SettingInfo;
import com.test.mod.setting.settings.BooleanSetting;
import com.test.mod.setting.settings.ModeSetting;

import java.util.Arrays;

@ModuleInfo(name =
@Text(label = "BlockAnimation", language = Language.English),
        category = Category.Render
)
public class BlockAnimation extends AbstractModule {

    @SettingInfo(name = {
            @Text(label = "Mode", language = Language.English),
            @Text(label = "Mode", language = Language.Chinese)
    })
    public static final ModeSetting mode = new ModeSetting("1.7", Arrays.asList("1.7", "Sigma"));
    public BlockAnimation() {
        registerSetting(mode);
    }
}
