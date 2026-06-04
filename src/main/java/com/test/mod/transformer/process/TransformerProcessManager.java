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
        add(new ReflectProcess());
    }
    public boolean matchField(Field field, ProcessInfo processInfo) {
        return matchMember(field, processInfo);
    }

    public boolean matchMethod(Method method, ProcessInfo processInfo) {
        return matchMember(method, processInfo);
    }
    private <V> boolean matchMember(V member, ProcessInfo processInfo) {
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



            invokeProcess(process, member, annotation, processInfo);
            return process.transformMixinClass();
        }
        return false;
    }
    @SuppressWarnings("unchecked")
    private <T extends Annotation, V> void invokeProcess(
            TransformerProcess<?, ?> process,
            Object member,
            Annotation annotation,
            ProcessInfo processInfo
    ) {

        TransformerProcess<T, V> typedProcess =
                (TransformerProcess<T, V>) process;

        T typedAnnotation = (T) annotation;
        V typedMember = (V) member;

        typedProcess.process(
                processInfo,
                typedMember,
                typedAnnotation
        );
    }
    private void add(TransformerProcess<?, ?> transformerProcess) {
        transformerProcesses.add(transformerProcess);
    }
}
