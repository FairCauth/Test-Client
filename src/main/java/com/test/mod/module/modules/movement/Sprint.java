package com.test.mod.module.modules.movement;

import com.test.mod.language.Language;
import com.test.mod.language.Text;
import com.test.mod.module.AbstractModule;
import com.test.mod.module.Category;
import com.test.mod.module.annotation.ModuleInfo;

@ModuleInfo(name =
@Text(label = "Sprint", language = Language.English),
        category = Category.Movement
)
public class Sprint extends AbstractModule {
}
