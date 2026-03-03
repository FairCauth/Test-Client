package com.test.mod.transformer.process;

import com.test.mod.asm.tree.ClassNode;
import com.test.mod.transformer.ITransformer;
import com.test.mod.transformer.process.impl.*;
import lombok.Getter;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class TransformerProcessManager {
    @Getter
    private final List<TransformerProcess<?, ?>> transformerProcesses = new ArrayList<>();

    public TransformerProcessManager() {
        add(new OverwriteProcess());
        add(new ShadowMethodProcess());
        add(new ShadowFieldProcess());
        add(new InjectProcess());
        add(new HookProcess());
        add(new ASMProcess());
       // add(new AccessorProcess());
    }
    public void matchField(Field field,
                           ClassNode classNode, ClassNode mixinClassNode, Class<? extends ITransformer> iTransformer,Class<?> targetClas) {

        matchMember(field, classNode,  mixinClassNode,iTransformer,targetClas);
    }

    public void matchMethod(Method method,
                            ClassNode classNode, ClassNode mixinClassNode, Class<? extends ITransformer> iTransformer,Class<?> targetClas) {

        matchMember(method, classNode, mixinClassNode,iTransformer,targetClas);
    }
    private <V> void matchMember(
            V member,
            ClassNode targetClassNode, ClassNode mixinClassNode, Class<? extends ITransformer> iTransformer,Class<?> targetClas
    ) {
        for (TransformerProcess<?, ?> process : transformerProcesses) {
            if (!process.getTargetType().isAssignableFrom(member.getClass())) {
                continue;
            }

            Class<? extends Annotation> annotationClass =
                    process.getAnnotationClass();

            Annotation annotation =
                    ((AnnotatedElement) member)
                            .getAnnotation(annotationClass);

            if (annotation == null) continue;


            invokeProcess(process, targetClassNode, mixinClassNode, member, annotation,iTransformer,targetClas);
        }
    }
    @SuppressWarnings("unchecked")
    private <T extends Annotation, V> void invokeProcess(
            TransformerProcess<?, ?> process,
            ClassNode classNode,
            ClassNode mixinClassNode,
            Object member,
            Annotation annotation, Class<? extends ITransformer> iTransformer,Class<?> targetClas
    ) {

        TransformerProcess<T, V> typedProcess =
                (TransformerProcess<T, V>) process;

        T typedAnnotation = (T) annotation;
        V typedMember = (V) member;

        typedProcess.process(
                classNode,
                mixinClassNode,
                targetClas,
                iTransformer,
                typedMember,
                typedAnnotation
        );
    }
    private void add(TransformerProcess<?, ?> transformerProcess) {
        transformerProcesses.add(transformerProcess);
    }
}
