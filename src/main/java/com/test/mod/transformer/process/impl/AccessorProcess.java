package com.test.mod.transformer.process.impl;

import com.test.mod.asm.Opcodes;
import com.test.mod.asm.tree.ClassNode;
import com.test.mod.asm.tree.FieldNode;
import com.test.mod.asm.tree.MethodNode;
import com.test.mod.transformer.ITransformer;
import com.test.mod.transformer.annotation.Accessor;
import com.test.mod.transformer.mapping.Mapping;
import com.test.mod.transformer.process.TransformerProcess;

import java.lang.reflect.Method;
//RIP :( 不允许改access redefine error 64
public class AccessorProcess extends TransformerProcess<Accessor, Method> {
    public AccessorProcess() {
        super(Accessor.class, Method.class);
    }

    @Override
    public void process(ClassNode targetClassNode,
                        ClassNode mixinClassNode,
                        Class<?> targetClass,
                        Class<? extends ITransformer> iTransformer,
                        Method object,
                        Accessor annotation) {
        String fieldName = annotation.value();
        fieldName = Mapping.get(targetClass,fieldName, null);
        for (FieldNode field : targetClassNode.fields) {
            if (field.name.equals(fieldName)) {
                System.out.println("Accessor: making field public -> " + field.name + " " + field.access);
                field.access = ACC_PUBLIC;
                break;
            }
        }
    }
}
