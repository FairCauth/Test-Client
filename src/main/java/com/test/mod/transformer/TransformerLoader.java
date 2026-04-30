package com.test.mod.transformer;


import com.test.mod.asm.tree.ClassNode;
import com.test.mod.natives.CoreNative;
import com.test.mod.transformer.annotation.ClassNameTransformer;
import com.test.mod.transformer.annotation.ClassTransformer;
import com.test.mod.transformer.annotation.TransformerMeta;
import com.test.mod.transformer.process.TransformerProcessManager;
import com.test.mod.transformer.transformers.*;
import com.test.mod.transformer.transformers.model.CreeperModelTransformer;
import com.test.mod.transformer.transformers.model.ModelPartTransformer;
import com.test.mod.transformer.transformers.model.SpiderModelTransformer;
import com.test.mod.transformer.utils.Tools;
import net.minecraft.client.renderer.LevelRenderer;

import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

public class TransformerLoader {
    //class name
    private final Map<String, Class<? extends ITransformer>> transformerMap = new HashMap<>();
    private final TransformerProcessManager transformerProcessManager = new TransformerProcessManager();

    public TransformerLoader() {
        add(
                MinecraftTransformer.class,
                GameRendererTransformer.class,
                KeyboardHandlerTransformer.class,
                LivingEntityRendererTransformer.class,
                ModelPartTransformer.class,
                GuiTransformer.class,
                LevelRendererTransformer.class,
                SpiderModelTransformer.class,
                CreeperModelTransformer.class
        );

        try {
            onTransform();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void onTransform() throws ClassNotFoundException {

        Set<String> keySet = transformerMap.keySet();
        int success = 0, error = 0;
        for (String className : keySet) {
            Class<?> targetClass = Class.forName(className);
            Class<? extends ITransformer> transformer = transformerMap.get(className);

            ClassNode classNode = null;
            ClassNode mixinClassNode = null;
            boolean transformMixinClass = false;
            int cnt = 0;
            for (int i = 0; i < 10; i++) {
                try {

                    byte[] classByte = CoreNative.getClassBytes(targetClass);
                    if (classByte == null)
                        throw new TransformerException(className + " transformer getClassBytes error");
                    //获取mixin class字节
                    byte[] mixinClassByte = CoreNative.getClassBytes(transformer);
                    if (mixinClassByte == null)
                        throw new TransformerException(transformer + " [mixin] getClassBytes error");


                    classNode = Tools.getClassNode(classByte);
                    mixinClassNode = Tools.getClassNode(mixinClassByte);

                    Method[] methods = transformer.getDeclaredMethods();
                    Field[] fields = transformer.getDeclaredFields();


                    handleTransformerFields(fields, classNode, mixinClassNode,transformer,targetClass);
                    boolean needTransformMixin = handleTransformerMethods(methods, classNode, mixinClassNode, transformer,targetClass);
                    if(needTransformMixin) transformMixinClass = true;
                    break;
                } catch (Exception e) {
                    e.printStackTrace();
                    cnt++;
                }
            }

            if(classNode == null || mixinClassNode == null) {
                System.out.println("classNode == null || mixinClassNode == null");
                return;
            }
            byte[] newClassByte = Tools.rewriteClass(classNode);

            byte[] newMixinClassByte = Tools.rewriteClass(mixinClassNode);

            if(targetClass.equals(LevelRenderer.class) ) {
                try (FileOutputStream fos = new FileOutputStream(targetClass.getName() + ".class")) {
                    fos.write(newClassByte);
                } catch (IOException ignored) {}
            }
//            if(transformer.equals(MinecraftTransformer.class) ) {
//                try (FileOutputStream fos = new FileOutputStream(transformer.getName() + ".class")) {
//                    fos.write(newMixinClassByte);
//                } catch (IOException ignored) {}
//            }

            int errorCode = CoreNative.redefineClasses(targetClass, newClassByte);
            if (errorCode != 0) {
                error++;
                throw new TransformerException(className + " transformer RedefineClass error " + errorCode);
            }

            if (transformMixinClass) {
                errorCode = CoreNative.redefineClasses(transformer, newMixinClassByte);
                if (errorCode != 0) {
                    error++;
                    throw new TransformerException(className + " [MIXINCLASS]transformer RedefineClass error " + errorCode);
                }
                System.out.println(transformer.getName() + " -> [MIXINCLASS]Transform OK " + cnt);
            }

            success++;
            System.out.println(targetClass.getName() + " -> Transform OK " + cnt);
        }
    }
    private void handleTransformerFields(Field[] fields, ClassNode classNode ,ClassNode mixinClassNode, Class<? extends ITransformer> iTransformer,Class<?> targetClas) {
        for (Field field : fields) {
            field.setAccessible(true);
            transformerProcessManager.matchField(field, classNode,mixinClassNode,iTransformer,targetClas);
        }
    }
    private int getPriority(Method method) {
        for (Annotation annotation : method.getAnnotations()) {

            TransformerMeta meta =
                    annotation.annotationType().getAnnotation(TransformerMeta.class);

            if (meta != null) {
                return meta.priority();
            }
        }
        return 0;
    }
    //优先级匹配
    private boolean handleTransformerMethods(
            Method[] methods,
            ClassNode classNode,
            ClassNode mixinClassNode,
            Class<? extends ITransformer> iTransformer,Class<?> targetClas
    ) {

        List<Method> sorted = new ArrayList<>(Arrays.asList(methods));
        //shadow优先
        sorted.sort((a, b) ->
                Integer.compare(getPriority(b), getPriority(a))
        );
        boolean transformMixinClass = false;
        for (Method method : sorted) {
            method.setAccessible(true);
            boolean t = transformerProcessManager.matchMethod(
                    method, classNode, mixinClassNode, iTransformer,targetClas
            );
            if(t) transformMixinClass = true;
        }
        return transformMixinClass;
    }
//    private void handleTransformerMethods(Method[] methods, ClassNode classNode,ClassNode mixinClassNode, ITransformer iTransformer) {
//        for (Method method : methods) {
//            method.setAccessible(true);
//            transformerProcessManager.matchMethod(method, classNode,mixinClassNode,iTransformer);
//        }
//    }
    @SafeVarargs
    private void add(Class<? extends ITransformer>... iTransformers) {
        for (Class<? extends ITransformer> iTransformer : iTransformers) {
            ClassTransformer clazzAnt = iTransformer.getAnnotation(ClassTransformer.class);
            ClassNameTransformer clazzNameAnt = iTransformer.getAnnotation(ClassNameTransformer.class);
            if(clazzAnt != null)
                transformerMap.put(clazzAnt.value().getName(), iTransformer);

            if(clazzNameAnt != null)
                transformerMap.put(clazzNameAnt.value(), iTransformer);

        }

    }
}
