package com.test.mod.module.modules.combat;

import com.test.mod.language.Language;
import com.test.mod.language.Text;
import com.test.mod.module.AbstractModule;
import com.test.mod.module.Category;
import com.test.mod.module.annotation.ModuleInfo;
import com.test.mod.setting.annotation.SettingInfo;
import com.test.mod.setting.settings.NumberSetting;

@ModuleInfo(name =
@Text(label = "Reach", language = Language.English),
        category = Category.Combat
)
public class Reach extends AbstractModule {
    @SettingInfo(name = {
            @Text(label = "Range", language = Language.English),
            @Text(label = "Range", language = Language.Chinese)
    })
    public final NumberSetting range = new NumberSetting(3.0d, 3.0d, 6.0d, "#.00");

    public Reach() {
        registerSetting(range);
    }
}
