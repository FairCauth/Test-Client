package com.test.mod.module.modules.render;

import com.test.mod.language.Language;
import com.test.mod.language.Text;
import com.test.mod.module.AbstractModule;
import com.test.mod.module.Category;
import com.test.mod.module.annotation.ModuleInfo;
import com.test.mod.setting.annotation.SettingInfo;
import com.test.mod.setting.attribute.SettingAttribute;
import com.test.mod.setting.settings.BooleanSetting;
import com.test.mod.transformer.transformers.MinecraftTransformer;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;

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
    public void log(String message) {
        String nameText = "Test >>";
        MutableComponent prefix = Component.literal("");

        for (int i = 0; i < nameText.length(); i++) {
            String c = String.valueOf(nameText.charAt(i));

            // hook color
            Style style = Style.EMPTY.withColor(TextColor.fromRgb((0x0829 << 8) | i));
            prefix.append(Component.literal(c).withStyle(style));
        }

        MutableComponent msg = Component.literal(" " + message).withStyle(ChatFormatting.GRAY);
        mc.gui.getChat().addMessage(prefix.append(msg));
    }
    @Override
    protected void onEnable() {

        MinecraftTransformer.setLocalServer(mc, true);
        log(MinecraftTransformer.isLocalServer(mc) + " isLOCAL");
    }
}
