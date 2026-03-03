package com.test.mod.module;

import com.darkmagician6.eventapi.EventManager;
import com.test.mod.language.Language;
import com.test.mod.language.Text;
import com.test.mod.module.annotation.ModuleInfo;
import com.test.mod.setting.Setting;
import com.test.mod.setting.SettingManager;
import com.test.mod.utils.IMinecraft;
import lombok.Getter;
import lombok.Setter;

public class AbstractModule extends SettingManager implements IMinecraft {
    @Getter
    private final Text[] texts;
    @Getter
    private final Category category;
    @Getter
    @Setter
    private int key;
    @Getter
    private boolean enable;
    public AbstractModule() {
        ModuleInfo moduleInfo = this.getClass().getAnnotation(ModuleInfo.class);
        if (moduleInfo == null)
            throw new RuntimeException(String.format("未检测到模块信息 %s", getClass().getName()));
        this.texts = moduleInfo.name();
        this.category = moduleInfo.category();
        this.key = moduleInfo.key();
        this.enable = moduleInfo.enable();
        setEnable(enable);
    }

    private void registerSettings(Setting<?>... settings) {
        try {
            registerSetting(this, settings);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
    public String getDescription() {
        return "None";
    }
    public String getTag() {
        return null;
    }
    public void toggle() {
        setEnable(!isEnable());
    }
    public void setEnable(boolean enable) {
        this.enable = enable;
        if(enable) {
            EventManager.register(this);
            onEnable();
        }else {
            EventManager.unregister(this);
            onDisable();
        }
    }
    public String getName() {
        return Language.getLabel(getTexts(), Language.getLanguage());
    }

    protected void onEnable() {}

    protected void onDisable() {}
}
