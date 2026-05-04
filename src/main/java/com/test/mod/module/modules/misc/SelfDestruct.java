package com.test.mod.module.modules.misc;

import com.test.mod.Main;
import com.test.mod.language.Language;
import com.test.mod.language.Text;
import com.test.mod.module.AbstractModule;
import com.test.mod.module.Category;
import com.test.mod.module.annotation.ModuleInfo;
import com.test.mod.ui.click.ClickGuiScreen;
import net.minecraft.network.chat.Component;

@ModuleInfo(name =
@Text(label = "SelfDestruct", language = Language.English),
        category = Category.Misc
)
public class SelfDestruct extends AbstractModule {
    @Override
    protected void onEnable() {
        toggle();
        Main.detach();
    }


}
