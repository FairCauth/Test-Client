package com.test.mod.transformer.process;

import com.test.mod.asm.tree.ClassNode;
import com.test.mod.transformer.ITransformer;

public record ProcessInfo(ClassNode targetClassNode,//目标class node
                          ClassNode mixinClassNode,//自己类的class node
                          Class<?> targetClass,//目标class
                          String originalClassName, //目标类无混淆名 用于vanilla map
                          Class<? extends ITransformer> iTransformer //自己class
) { }
