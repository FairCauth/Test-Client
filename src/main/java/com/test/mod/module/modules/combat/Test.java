package com.test.mod.module.modules.combat;

import com.test.mod.language.Language;
import com.test.mod.language.Text;
import com.test.mod.module.AbstractModule;
import com.test.mod.module.Category;
import com.test.mod.module.annotation.ModuleInfo;
import org.lwjgl.glfw.GLFW;

@ModuleInfo(name =
@Text(label = "Test", language = Language.English),
        category = Category.COMBAT
)
public class Test extends AbstractModule {

}
