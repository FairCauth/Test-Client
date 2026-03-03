package com.test.mod.module.modules.render;

import com.test.mod.language.Language;
import com.test.mod.language.Text;
import com.test.mod.module.AbstractModule;
import com.test.mod.module.Category;
import com.test.mod.module.annotation.ModuleInfo;
import com.test.mod.ui.click.ClickGuiScreen;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

@ModuleInfo(name =
@Text(label = "ClickGui", language = Language.English),
        category = Category.RENDER, key = GLFW.GLFW_KEY_RIGHT_SHIFT
)
public class ClickGui extends AbstractModule {
//    @EventTarget
//    public void onRender(RenderSkiaEvent event) {
//
//        Render2D.drawRectBlur(event.getCanvasStack(),10,10,100,100,10, 15);
//    }
    public static ClickGuiScreen dropdownGui = null;
    @Override
    protected void onEnable() {
        if(dropdownGui == null) {
            dropdownGui= new ClickGuiScreen(Component.literal("gd"));
        }
        mc.execute(() -> mc.setScreen(dropdownGui));
        super.onEnable();
        toggle();
    }
}
