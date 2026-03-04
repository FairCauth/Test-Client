package com.test.mod.module.modules.combat;

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
        @Text(label = "AimAssist", language = Language.English),
        @Text(label = "自瞄", language = Language.Chinese)
}, category = Category.COMBAT)
public class AimAssist extends AbstractModule {
    @SettingInfo(name = {
            @Text(label = "Range", language = Language.English),
            @Text(label = "距离", language = Language.Chinese)
    })
    private final NumberSetting rangeValue = new NumberSetting(3.5, 3.0, 6.0, "#.0");

    @SettingInfo(name = {
            @Text(label = "Fov", language = Language.English),
            @Text(label = "视角", language = Language.Chinese)
    })
    private final NumberSetting fov = new NumberSetting(180.0, 1.0, 180.0, "#");
    @SettingInfo(name = {
            @Text(label = "ClickAim", language = Language.English),
            @Text(label = "单击触发", language = Language.Chinese)
    })
    private final BooleanSetting clickAim = new BooleanSetting(true);
    @SettingInfo(name = {
            @Text(label = "ClickTime", language = Language.English),
            @Text(label = "单击触发时间", language = Language.Chinese)
    })
    private final NumberSetting clickTime = new NumberSetting(500.0, 0.0, 1000.0, "#");
    @SettingInfo(name = {
            @Text(label = "Priority", language = Language.English),
            @Text(label = "优先级", language = Language.Chinese)
    })
    private final ModeSetting priorityValue = new ModeSetting(
            "Distance", Arrays.asList(
            "Health",
            "Distance",
            "LivingTime",
            "Fov",
            "Armor",
            "HurtResistance",
            "HurtTime",
            "RegenAmplifier"
    )
    );
    @SettingInfo(name = {
            @Text(label = "SmoothSpeed", language = Language.English),
            @Text(label = "平滑速度", language = Language.Chinese)
    })
    private final NumberSetting smoothSpeed = new NumberSetting(0.2, 0.01, 1.0, "#.00");
    @SettingInfo(name = {
            @Text(label = "OnlyWeapon", language = Language.English),
            @Text(label = "拿武器时触发", language = Language.Chinese)
    })
    private final BooleanSetting onlyWeapon = new BooleanSetting(true);
    @SettingInfo(name = {
            @Text(label = "WallCheck", language = Language.English),
            @Text(label = "墙体检测", language = Language.Chinese)
    })
    private final BooleanSetting wallCheck = new BooleanSetting(true);
    @SettingInfo(name = {
            @Text(label = "AimMode", language = Language.English),
            @Text(label = "瞄准模式", language = Language.Chinese)
    })
    private final ModeSetting aimMode = new ModeSetting("Simple", Arrays.asList("Simple", "Advanced"));
    @SettingInfo(name = {
            @Text(label = "FreeZone", language = Language.English),
            @Text(label = "自由区域", language = Language.Chinese)
    })
    private final NumberSetting freeZone = new NumberSetting(0.3, 0.0, 0.5, "#.00");

    @SettingInfo(name = {
            @Text(label = "LimitPitch", language = Language.English),
            @Text(label = "限制垂直视角", language = Language.Chinese)
    })
    private final BooleanSetting limitPitch = new BooleanSetting(false);

    @SettingInfo(name = {
            @Text(label = "OnlyYaw", language = Language.English),
            @Text(label = "仅水平", language = Language.Chinese)
    })
    private final BooleanSetting onlyYaw = new BooleanSetting(true, new SettingAttribute<>(limitPitch, true));

    @SettingInfo(name = {
            @Text(label = "FreeBox", language = Language.English),
            @Text(label = "自定义框", language = Language.Chinese)
    })
    private final BooleanSetting debugBox = new BooleanSetting(false);

    @SettingInfo(name = {
            @Text(label = "CenterAim", language = Language.English),
            @Text(label = "中心瞄准", language = Language.Chinese)
    })
    private final BooleanSetting centerAim = new BooleanSetting(true, new SettingAttribute<>(freeZone, true), new SettingAttribute<>(debugBox, true));

    @SettingInfo(name = {
            @Text(label = "BoxMode", language = Language.English),
            @Text(label = "显示模式", language = Language.Chinese)
    })
    private final ModeSetting boxMode = new ModeSetting("Fill", Arrays.asList("Fill", "Line", "Both"));

    @SettingInfo(name = {
            @Text(label = "ESPMode", language = Language.English),
            @Text(label = "显示模式", language = Language.Chinese)
    })
    private final ModeSetting espMode = new ModeSetting("Box", Arrays.asList("Box", "Jello"), new SettingAttribute<>(boxMode, "Box"));

    @SettingInfo(name = {
            @Text(label = "ESPColor", language = Language.English),
            @Text(label = "显示颜色", language = Language.Chinese)
    })
    private final ColorSetting espColor = new ColorSetting(new Color(100, 200, 255, 80));

    @SettingInfo(name = {
            @Text(label = "ESP", language = Language.English),
            @Text(label = "目标显示", language = Language.Chinese)
    })
    private final BooleanSetting espToggle = new BooleanSetting(true, new SettingAttribute<>(espMode, true), new SettingAttribute<>(espColor, true));

    public AimAssist() {
        registerSetting(rangeValue, fov, clickAim, clickTime, smoothSpeed, priorityValue, onlyWeapon, wallCheck, aimMode, onlyYaw, centerAim, espToggle);
    }
}
