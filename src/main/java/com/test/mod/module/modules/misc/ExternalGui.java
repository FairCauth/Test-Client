package com.test.mod.module.modules.misc;

import com.fair.preload.Preloader;
import com.test.mod.Main;
import com.test.mod.language.Language;
import com.test.mod.language.Text;
import com.test.mod.module.AbstractModule;
import com.test.mod.module.Category;
import com.test.mod.module.annotation.ModuleInfo;

@ModuleInfo(name =
@Text(label = "ExternalGui", language = Language.English),
        category = Category.Misc
)
public class ExternalGui extends AbstractModule {
    @Override
    protected void onEnable() {
        Preloader.connect("127.0.0.1", 9999);
        Main.INSTANCE.externalGui.registerMain();
    }
}
