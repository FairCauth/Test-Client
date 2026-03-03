package com.test.mod.setting;

import com.test.mod.setting.annotation.SettingInfo;
import com.test.mod.setting.attribute.SettingAttribute;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class SettingManager {

    public static final Map<Object, List<Setting<?>>> settingsMap = new HashMap<>();
    public static final Map<Setting<?>, SettingAttribute<?>> attributeMap = new HashMap<>();
    private static final List<?> list = new ArrayList<>();
    protected <T extends SettingManager> void registerSetting(T object, Setting<?>... settings) throws IllegalAccessException {
        registerSetting(object, object.getClass(), object, settings);
    }
    protected <T extends SettingManager> void registerSetting(T object, Class<?> clazz, Object instance, Setting<?>... settings) throws IllegalAccessException {
        List<Setting<?>> settingList = new ArrayList<>();

        Arrays.stream(settings).forEach(setting ->
        {
            setting.setLevel(0);
            List<Setting<?>> list = traverseTree(setting);
            settingList.add(setting);
            if (!list.isEmpty()) {
                list.forEach(childSetting -> {

                    childSetting.setLevel(childSetting.getAncestorCount());
                });
                settingList.addAll(list);
            }
        });//将子设置加入map
        settingsMap.put(object, settingList);

        for (Field declaredField : clazz.getDeclaredFields()) {
            if (!declaredField.isAccessible()) {
                declaredField.setAccessible(true);
            }
            SettingInfo settingInfo = declaredField.getAnnotation(SettingInfo.class);
            if (settingInfo != null) {
                Object fieldValue = declaredField.get(instance);
                if (!(fieldValue instanceof Setting<?> setting))
                    throw new RuntimeException(String.format("错误的设置 %s", declaredField));
                //System.out.println(Language.getLabel(settingInfo.name(), Language.getLanguage()));
                setting.setInstance(object);
                setting.setTexts(settingInfo.name());
            }

        }
    }


    public static <T extends SettingManager> void removeObj(T object) {
        settingsMap.remove(object);
    }
    public static void updateDisplay(List<Setting<?>> list) {
        if (list.isEmpty()) return;//递归结束
        list.stream().
                filter(setting -> setting.getParent() != null).//排除最上层节点
                forEach(setting -> {
            boolean display = attributeMap.get(setting).get();
            if (!setting.getParent().isDisplay()) display = false;
            if (display) {
                setting.setDisplay(true);
                if (!setting.getTreeSetting().isEmpty())
                    updateDisplay(setting.getTreeSetting());
            } else {//如果上层节点shouldShow为false 则下层节点shouldShow都要为false
                List<Setting<?>> settings = traverseTree(setting);
                if (!settings.isEmpty()) {
                    settings.forEach(it -> it.setDisplay(false));
                }
            }

        });
    }

    public static List<Setting<?>> traverseTree(Setting<?> setting) {
        List<Setting<?>> list = new ArrayList<>();
        dfs(setting, list);
        return list;
    }

    /**
     * dfs遍历子节点
     *
     * @param settings 用于接收数组
     */
    private static void dfs(Setting<?> setting, List<Setting<?>> settings) {
        if (setting.getParent() != null) {
            settings.add(setting);
        }
        for (Setting<?> child : setting.getTreeSetting()) {
            if (child != null) {
                dfs(child, settings);
            }
        }
    }

    /**
     * 获取所有设置
     *
     * @param object
     * @param <T>
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T extends SettingManager> List<Setting<?>> getSettings(T object) {
        if (!settingsMap.containsKey(object))
            return (List<Setting<?>>) list;
        return settingsMap.get(object);
    }
    public static <T extends SettingManager> Setting<?> getSettingByObj(T object, String settingName) {
        for (Setting<?> setting : getSettings(object)) {
            if (setting.getNameKey().equals(settingName)) {
                return setting;
            }
        }
        return null;
    }
    public static List<Setting<?>> getSettings() {
        List<Setting<?>> list = new ArrayList<>();
        for (Object key : settingsMap.keySet())
            list.addAll(settingsMap.get(key));
        return list;
    }
}
