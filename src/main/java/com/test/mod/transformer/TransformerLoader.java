package com.test.mod.transformer;


import com.fair.preload.Preloader;
import com.test.mod.Main;
import com.test.mod.asm.Type;
import com.test.mod.asm.tree.ClassNode;
import com.test.mod.natives.CoreNative;
import com.test.mod.transformer.annotation.ClassNameTransformer;
import com.test.mod.transformer.annotation.ClassTransformer;
import com.test.mod.transformer.annotation.TransformerMeta;
import com.test.mod.transformer.process.ProcessInfo;
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
    public record TransformerClass(String originalClass, String transformClass) { }
    //class name
    private final Map<TransformerClass, Class<? extends ITransformer>> transformerMap = new HashMap<>();
    private final TransformerProcessManager transformerProcessManager = new TransformerProcessManager();
    private final Map<Class<?>, byte[]> originalBytecodeMap = new HashMap<>();

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
                CreeperModelTransformer.class,
                ItemInHandRendererTransformer.class
        );

        try {
            onTransform();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void onTransform() throws ClassNotFoundException {

        Set<TransformerClass> keySet = transformerMap.keySet();
        int success = 0, error = 0;
        Preloader.send("start transformer " + keySet.size());
        for (TransformerClass transformerClass : keySet) {
            String originalClass = transformerClass.originalClass;
            String transformClass = transformerClass.transformClass;
            Class<?> targetClass = Class.forName(transformClass);
            Class<? extends ITransformer> transformer = transformerMap.get(transformerClass);

            ClassNode classNode = null;
            ClassNode mixinClassNode = null;
            boolean transformMixinClass = false;
            int cnt = 0;
            for (int i = 0; i < 10; i++) {
                try {

                    byte[] classByte = CoreNative.getClassBytes(targetClass);
                    originalBytecodeMap.put(targetClass, classByte);
                    if (classByte == null)
                        throw new TransformerException(originalClass + " transformer getClassBytes error");
                    //获取mixin class字节
                    byte[] mixinClassByte = CoreNative.getClassBytes(transformer);
                    if (mixinClassByte == null)
                        throw new TransformerException(transformer + " [mixin] getClassBytes error");


                    classNode = Tools.getClassNode(classByte);
                    mixinClassNode = Tools.getClassNode(mixinClassByte);

                    Method[] methods = transformer.getDeclaredMethods();
                    Field[] fields = transformer.getDeclaredFields();

                    ProcessInfo processInfo = new ProcessInfo(
                            classNode,
                            mixinClassNode,
                            targetClass,
                            originalClass,
                            transformer
                    );

                    handleTransformerFields(fields, processInfo);
                    boolean needTransformMixin =
                            handleTransformerMethods(methods, processInfo);
                    if(needTransformMixin) transformMixinClass = true;
                    break;
                } catch (Exception e) {
                    e.printStackTrace();
                    cnt++;
                }
            }

            if(classNode == null || mixinClassNode == null) {
                Preloader.send("classNode == null || mixinClassNode == null!");
                System.out.println("classNode == null || mixinClassNode == null");
                return;
            }
            byte[] newClassByte = Tools.rewriteClass(classNode);

            byte[] newMixinClassByte = Tools.rewriteClass(mixinClassNode);

//            TEST CODE
//            if(targetClass.equals(LevelRenderer.class) ) {
//                try (FileOutputStream fos = new FileOutputStream(targetClass.getName() + ".class")) {
//                    fos.write(newClassByte);
//                } catch (IOException ignored) {}
//            }
//            if(transformer.equals(MinecraftTransformer.class) ) {
//                try (FileOutputStream fos = new FileOutputStream(transformer.getName() + ".class")) {
//                    fos.write(newMixinClassByte);
//                } catch (IOException ignored) {}
//            }

            int errorCode = CoreNative.redefineClasses(targetClass, newClassByte);
            if (errorCode != 0) {
                error++;
                Preloader.send(originalClass +" transformer RedefineClass error "+ errorCode);
                throw new TransformerException(originalClass + " transformer RedefineClass error " + errorCode);
            }

            if (transformMixinClass) {
                errorCode = CoreNative.redefineClasses(transformer, newMixinClassByte);
                if (errorCode != 0) {
                    error++;
                    Preloader.send(originalClass + " [MIXINCLASS]transformer RedefineClass error " + errorCode);
                    throw new TransformerException(originalClass + " [MIXINCLASS]transformer RedefineClass error " + errorCode);
                }
                Preloader.send(transformer.getName() + " -> [MIXINCLASS]Transform OK " + cnt);
                System.out.println(transformer.getName() + " -> [MIXINCLASS]Transform OK " + cnt);
            }

            success++;
            try {
                Thread.sleep(500);
            }catch (Exception ignored) {
            }
            String classOutputName = targetClass.getName() + (!originalClass.equals(transformClass) ? ("[" + originalClass + "]") : "");
            Preloader.send(classOutputName + " -> Transform OK " + cnt);
            System.out.println(classOutputName + " -> Transform OK " + cnt);
        }
    }
    private void handleTransformerFields(Field[] fields, ProcessInfo processInfo) {
        for (Field field : fields) {
            field.setAccessible(true);
            transformerProcessManager.matchField(field, processInfo);
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
            ProcessInfo processInfo
    ) {

        List<Method> sorted = new ArrayList<>(Arrays.asList(methods));
        //shadow优先
        sorted.sort((a, b) ->
                Integer.compare(getPriority(b), getPriority(a))
        );
        boolean transformMixinClass = false;
        for (Method method : sorted) {
            method.setAccessible(true);
            boolean t = transformerProcessManager.matchMethod(method, processInfo);
            if (t)
                transformMixinClass = true;
        }
        return transformMixinClass;
    }
//    private void handleTransformerMethods(Method[] methods, ClassNode classNode,ClassNode mixinClassNode, ITransformer iTransformer) {
//        for (Method method : methods) {
//            method.setAccessible(true);
//            transformerProcessManager.matchMethod(method, classNode,mixinClassNode,iTransformer);
//        }
//    }
    public void cleanup() {

        for (Map.Entry<Class<?>, byte[]> entry : originalBytecodeMap.entrySet()) {
            int errorCode = CoreNative.redefineClasses(entry.getKey(), entry.getValue());
            if (errorCode != 0) {
                System.out.println("restore failed: " + entry.getKey().getName() + " error " + errorCode);
            }else{
                System.out.println("restore " + entry.getKey().getName());
            }

        }
        originalBytecodeMap.clear();
        transformerMap.clear();
    }
    @SafeVarargs
    private void add(Class<? extends ITransformer>... iTransformers) {
        for (Class<? extends ITransformer> iTransformer : iTransformers) {
            ClassTransformer clazzAnt = iTransformer.getAnnotation(ClassTransformer.class);
            ClassNameTransformer clazzNameAnt = iTransformer.getAnnotation(ClassNameTransformer.class);
            if(clazzAnt != null)
                transformerMap.put(new TransformerClass(clazzAnt.value().getName(), clazzAnt.value().getName()), iTransformer);

            if(clazzNameAnt != null)
            {
                String className = clazzNameAnt.value();
                if (Main.mcEnvironment == Main.McEnvironment.VANILLA_OBF) {
                    String owner = className.replace(".", "/");
                    className = Main.mapping.map(owner);
                    System.out.println("map class vanilla " + clazzNameAnt.value() + " -> " + className);
                } else if (Main.mcEnvironment == Main.McEnvironment.FABRIC_OBF) {
                    String owner = className.replace(".", "/");
                    className = Main.fabric_mapping.map(owner).replace("/", ".");
                    System.out.println("map class fabric " + clazzNameAnt.value() + " -> " + className);
                }

                transformerMap.put(new TransformerClass(clazzNameAnt.value(), className), iTransformer);
            }

        }

    }
}
