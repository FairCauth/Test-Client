package com.test.mod.transformer;


import com.test.mod.ExampleTransformer;
import com.test.mod.asm.tree.ClassNode;
import com.test.mod.natives.CoreNative;
import com.test.mod.transformer.annotation.ClassNameTransformer;
import com.test.mod.transformer.annotation.ClassTransformer;
import com.test.mod.transformer.annotation.TransformerMeta;
import com.test.mod.transformer.process.TransformerProcessManager;
import com.test.mod.transformer.utils.Tools;
import net.minecraft.client.Minecraft;

import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

public class TransformerLoader {
    //class name
    private final Map<String, ITransformer> transformerMap = new HashMap<>();
    private final TransformerProcessManager transformerProcessManager = new TransformerProcessManager();

    public TransformerLoader() {
        add(new ExampleTransformer());

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
            ITransformer transformer = transformerMap.get(className);
            Class<?> transformerClass = transformer.getClass();
            ClassNode classNode = null;
            int cnt = 0;
            for (int i = 0; i < 10; i++) {
                try {

                    byte[] classByte = CoreNative.getClassBytes(targetClass);
                    if (classByte == null)
                        throw new TransformerException(className + " transformer getClassBytes error");
                    //获取mixin class字节
                    byte[] mixinClassByte = CoreNative.getClassBytes(transformerClass);
                    if (mixinClassByte == null)
                        throw new TransformerException(transformerClass + " [mixin] getClassBytes error");


                    classNode = Tools.getClassNode(classByte);
                    ClassNode mixinClassNode = Tools.getClassNode(mixinClassByte);

                    Method[] methods = transformerClass.getDeclaredMethods();
                    Field[] fields = transformerClass.getDeclaredFields();


                    handleTransformerFields(fields, classNode, mixinClassNode,transformer,targetClass);
                    handleTransformerMethods(methods, classNode, mixinClassNode, transformer,targetClass);
                    break;
                } catch (Exception e) {
                    e.printStackTrace();
                    cnt++;
                }
            }

            if(classNode == null) {
                System.out.println("F!!!!!!!!!!!!!!!!!!!!!!!!!!");
                return;
            }
            byte[] newClassByte = Tools.rewriteClass(classNode);

            if(targetClass.equals(Minecraft.class) ) {
                try (FileOutputStream fos = new FileOutputStream(targetClass.getName() + ".class")) {
                    fos.write(newClassByte);
                } catch (IOException ignored) {}
            }

            int errorCode = CoreNative.redefineClasses(targetClass, newClassByte);
            if (errorCode != 0) {
                error++;
                throw new TransformerException(className + " transformer RedefineClass error " + errorCode);
            }
            success++;
            System.out.println(targetClass.getName() + " -> Transform OK " + cnt);
        }
    }
    private void handleTransformerFields(Field[] fields, ClassNode classNode ,ClassNode mixinClassNode, ITransformer iTransformer,Class<?> targetClas) {
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
    private void handleTransformerMethods(
            Method[] methods,
            ClassNode classNode,
            ClassNode mixinClassNode,
            ITransformer iTransformer,Class<?> targetClas
    ) {

        List<Method> sorted = new ArrayList<>(Arrays.asList(methods));
        //shadow优先
        sorted.sort((a, b) ->
                Integer.compare(getPriority(b), getPriority(a))
        );

        for (Method method : sorted) {
            method.setAccessible(true);
            transformerProcessManager.matchMethod(
                    method, classNode, mixinClassNode, iTransformer,targetClas
            );
        }
    }
//    private void handleTransformerMethods(Method[] methods, ClassNode classNode,ClassNode mixinClassNode, ITransformer iTransformer) {
//        for (Method method : methods) {
//            method.setAccessible(true);
//            transformerProcessManager.matchMethod(method, classNode,mixinClassNode,iTransformer);
//        }
//    }
    private void add(ITransformer... iTransformers) {
        for (ITransformer iTransformer : iTransformers) {
            ClassTransformer clazzAnt = iTransformer.getClass().getAnnotation(ClassTransformer.class);
            ClassNameTransformer clazzNameAnt = iTransformer.getClass().getAnnotation(ClassNameTransformer.class);
            if(clazzAnt != null)
                transformerMap.put(clazzAnt.value().getName(), iTransformer);

            if(clazzNameAnt != null)
                transformerMap.put(clazzNameAnt.value(), iTransformer);

        }
    }
}
