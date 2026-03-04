package com.test.mod.module.modules.render;

import com.test.mod.language.Language;
import com.test.mod.language.Text;
import com.test.mod.module.AbstractModule;
import com.test.mod.module.Category;
import com.test.mod.module.annotation.ModuleInfo;
import com.test.mod.setting.annotation.SettingInfo;
import com.test.mod.setting.attribute.SettingAttribute;
import com.test.mod.setting.settings.BooleanSetting;
import com.test.mod.setting.settings.ColorSetting;
import com.test.mod.setting.settings.ModeSetting;
import com.test.mod.setting.settings.NumberSetting;

import java.awt.*;
import java.util.Arrays;

@ModuleInfo(name = {
        @Text(label = "NameTags", language = Language.English),
        @Text(label = "名字标签", language = Language.Chinese)
}, category = Category.RENDER)
public class NameTags extends AbstractModule {
    @SettingInfo(name = {
            @Text(label = "Health", language = Language.English),
            @Text(label = "血量 ", language = Language.Chinese)
    })
    private final BooleanSetting healthValue = new BooleanSetting(true);
    @SettingInfo(name = {
            @Text(label = "Distance", language = Language.English),
            @Text(label = "距离 ", language = Language.Chinese)
    })
    private final BooleanSetting distanceValue = new BooleanSetting(false);
    @SettingInfo(name = {
            @Text(label = "Show Enchantments", language = Language.English),
            @Text(label = "显示附魔", language = Language.Chinese)
    })
    private final BooleanSetting showEnchantments = new BooleanSetting(true);
    @SettingInfo(name = {
            @Text(label = "Armor", language = Language.English),
            @Text(label = "盔甲 ", language = Language.Chinese)
    })
    private final BooleanSetting armorValue = new BooleanSetting(false, new SettingAttribute<>(showEnchantments, true));
    @SettingInfo(name = {
            @Text(label = "Scale", language = Language.English),
            @Text(label = "大小 ", language = Language.Chinese)
    })
    private final NumberSetting scaleValue = new NumberSetting(1.5F, 1F, 4F, "#.00");

    @SettingInfo(name = {
            @Text(label = "Self", language = Language.English),
            @Text(label = "自己 ", language = Language.Chinese)
    })
    private final BooleanSetting self = new BooleanSetting(false);

    @SettingInfo(name = {
            @Text(label = "Dis Mc Name", language = Language.English),
            @Text(label = "禁用原版标签", language = Language.Chinese)
    })
    private final BooleanSetting disableVanilla = new BooleanSetting(true);

    @SettingInfo(name = {
            @Text(label = "Skia X Offset", language = Language.English),
            @Text(label = "Skia X 偏移", language = Language.Chinese)
    })
    private final NumberSetting skiaXOffset = new NumberSetting(-8F, -50F, 50F, "#.0");

    @SettingInfo(name = {
            @Text(label = "Skia Y Offset", language = Language.English),
            @Text(label = "Skia Y 偏移", language = Language.Chinese)
    })
    private final NumberSetting skiaYOffset = new NumberSetting(6F, -50F, 50F, "#.0");

    @SettingInfo(name = {
            @Text(label = "Background Color", language = Language.English),
            @Text(label = "背景颜色", language = Language.Chinese)
    })
    private final ColorSetting backgroundColorValue = new ColorSetting(new Color(200, 200, 200, 120));

    @SettingInfo(name = {
            @Text(label = "Glass Color", language = Language.English),
            @Text(label = "玻璃颜色", language = Language.Chinese)
    })
    private final ColorSetting glassColorValue = new ColorSetting(new Color(240, 240, 240, 60));

    @SettingInfo(name = {
            @Text(label = "Blur", language = Language.English),
            @Text(label = "模糊 ", language = Language.Chinese)
    })
    private final BooleanSetting blurValue = new BooleanSetting(true,
            new SettingAttribute<>(glassColorValue, true),
            new SettingAttribute<>(backgroundColorValue, false)
    );
    @SettingInfo(name = {
            @Text(label = "Show VIP Icon", language = Language.English),
            @Text(label = "显示VIP图标", language = Language.Chinese)
    })
    private final BooleanSetting showVipIcon = new BooleanSetting(true);

    @SettingInfo(name = {
            @Text(label = "UI Style", language = Language.English),
            @Text(label = "UI风格", language = Language.Chinese)
    })
    private final ModeSetting uiStyle = new ModeSetting("Modern", Arrays.asList("Modern", "Classic"), new SettingAttribute<>(showVipIcon, "Classic"));

    @SettingInfo(name = {
            @Text(label = "Render Mode", language = Language.English),
            @Text(label = "渲染模式", language = Language.Chinese)
    })
    private final ModeSetting renderMode = new ModeSetting("MC", Arrays.asList("MC", "Skia"),
            new SettingAttribute<>(uiStyle, "Skia"),
            new SettingAttribute<>(skiaXOffset, "Skia"),
            new SettingAttribute<>(skiaYOffset, "Skia"),
            new SettingAttribute<>(blurValue, "Skia")
    );
    public NameTags() {
        registerSetting(renderMode, healthValue, distanceValue, armorValue, scaleValue, self, disableVanilla);
    }
}
