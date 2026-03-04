package com.test.mod.module.modules.render;

import com.test.mod.language.Language;
import com.test.mod.language.Text;
import com.test.mod.module.AbstractModule;
import com.test.mod.module.Category;
import com.test.mod.module.annotation.ModuleInfo;
import com.test.mod.setting.annotation.SettingInfo;
import com.test.mod.setting.attribute.SettingAttribute;
import com.test.mod.setting.settings.BooleanSetting;

@ModuleInfo(name = {
        @Text(label = "TestModule1", language = Language.English),
        @Text(label = "TestModule1", language = Language.Chinese)
}, category = Category.RENDER)
public class TestModule1 extends AbstractModule {
    @SettingInfo(name = {
            @Text(label = "TreeNode3-1", language = Language.English),
            @Text(label = "TreeNode3-1", language = Language.Chinese)
    })
    private final BooleanSetting d = new BooleanSetting(false);
    @SettingInfo(name = {
            @Text(label = "TreeNode2-1", language = Language.English),
            @Text(label = "TreeNode2-1", language = Language.Chinese)
    })
    private final BooleanSetting b = new BooleanSetting(false,
            new SettingAttribute<>(d, true)
    );

    @SettingInfo(name = {
            @Text(label = "TreeNode2-1", language = Language.English),
            @Text(label = "TreeNode2-1 ", language = Language.Chinese)
    })
    private final BooleanSetting c = new BooleanSetting(false);

    @SettingInfo(name = {
            @Text(label = "TreeNode1", language = Language.English),
            @Text(label = "TreeNode1 ", language = Language.Chinese)
    })
    private final BooleanSetting a = new BooleanSetting(false,
            new SettingAttribute<>(b, true),
            new SettingAttribute<>(c, true)
    );
    public TestModule1() {
        registerSetting(a);
    }
}
