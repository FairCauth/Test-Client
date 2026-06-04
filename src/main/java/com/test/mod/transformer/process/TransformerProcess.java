package com.test.mod.transformer.process;

import com.test.mod.Main;
import com.test.mod.asm.Opcodes;
import com.test.mod.asm.tree.MethodNode;
import com.test.mod.transformer.mapping.forge.Mapping;
import com.test.mod.transformer.utils.Tools;
import lombok.Getter;

import java.lang.annotation.Annotation;

public abstract class TransformerProcess<T extends Annotation, V> implements Opcodes {

    @Getter
    private final Class<T> annotationClass;
    @Getter
    private final Class<V> targetType;

    public TransformerProcess(Class<T> annotationClass, Class<V> targetType) {
        this.annotationClass = annotationClass;
        this.targetType = targetType;
    }

    public boolean transformMixinClass() {
        return false;
    }


    protected MethodNode getTargetMethodNode(ProcessInfo processInfo, String[] names, String desc, boolean remap) {
        MethodNode targetMethodNode = null;

        for (String name : names) {
            if(remap)
            {
                if(Main.mcEnvironment == Main.McEnvironment.FORGE_OBF) {
                    name = Mapping.get(processInfo.targetClass(), name, desc);
                } else if (Main.mcEnvironment == Main.McEnvironment.VANILLA_OBF) {
                    String owner = processInfo.originalClassName().replace(".", "/");
                    name = Main.mapping.mapMethodName(owner, name, desc);
                    desc = Main.mapping.mapMethodDesc(desc);
                } else if (Main.mcEnvironment == Main.McEnvironment.FABRIC_OBF) {
                    String owner = processInfo.originalClassName().replace(".", "/");
                    name = Main.fabric_mapping.mapMethodName(owner, name, desc);
                    desc = Main.fabric_mapping.mapMethodDesc(desc);
                }

            }
            targetMethodNode = Tools.getMethod(processInfo.targetClassNode(), desc, name);
            if (targetMethodNode != null) break;
        }
        return targetMethodNode;
    }
    public abstract void process(ProcessInfo processInfo,
                                 V object,
                                 T annotation
    );
}
