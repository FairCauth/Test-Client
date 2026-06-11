package com.test.mod.module;

import com.darkmagician6.eventapi.EventManager;
import com.darkmagician6.eventapi.EventTarget;
import com.test.mod.events.EventKey;
import com.test.mod.module.modules.combat.AimAssist;
import com.test.mod.module.modules.combat.Reach;
import com.test.mod.module.modules.combat.Test;
import com.test.mod.module.modules.misc.ExternalGui;
import com.test.mod.module.modules.misc.SelfDestruct;
import com.test.mod.module.modules.movement.Backtrack;
import com.test.mod.module.modules.movement.NoSlow;
import com.test.mod.module.modules.render.*;
import lombok.Getter;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.Arrays;

public class ModuleManager {
    @Getter
    private final ArrayList<AbstractModule> moduleList = new ArrayList<>();

    public ModuleManager() {
        add(new ClickGui());
        add(new Skeleton(), new Reach());
        add(new Test(), new AimAssist());
        add(new Backtrack(), new NoSlow(), new NameTags(), new TestModule1(), new ESP2D(), new SelfDestruct(), new ExternalGui());
        add(new BlockAnimation());

        EventManager.register(this);
    }
    public AbstractModule getModule(String name) {
        for (AbstractModule abstractModule : moduleList) {
            if(abstractModule.getName().equals(name)) return abstractModule;
        }
        return null;
    }
    public ArrayList<AbstractModule> getModulesByCategory(Category category) {
        ArrayList<AbstractModule> list = new ArrayList<>();
        for (AbstractModule mod : getModuleList()) {
            if (mod.getCategory().equals(category)) list.add(mod);
        }
        return list;
    }

    private void add(AbstractModule... modules) {
        moduleList.addAll(Arrays.stream(modules).toList());
    }
    @EventTarget
    public void onKey(EventKey e) {
        int key = e.getKey();
        for (AbstractModule m : moduleList) {
            int bind = m.getKey(); // workaround for certain keys
            if (bind != GLFW.GLFW_KEY_UNKNOWN && bind == key
                    || bind == GLFW.GLFW_KEY_RIGHT_SHIFT && key == GLFW.GLFW_KEY_UNKNOWN) {
                m.toggle();
            }
        }
    }
    public void cleanup() {
        for (AbstractModule m : moduleList) {
            if (m.isEnable()) {
                m.toggle();
            }
            EventManager.unregister(m);
            m.cleanup();
        }
        moduleList.clear();
        EventManager.unregister(this);
    }
}
