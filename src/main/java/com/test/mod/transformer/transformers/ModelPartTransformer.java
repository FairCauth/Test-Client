package com.test.mod.transformer.transformers;

import com.test.mod.transformer.ITransformer;
import com.test.mod.transformer.annotation.ClassTransformer;
import com.test.mod.transformer.annotation.Reflect;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;

import java.util.List;

@ClassTransformer(ModelPart.class)
public class ModelPartTransformer implements ITransformer {
    @Reflect("cubes")
    public native static List<ModelPart.Cube> getCubes(ModelPart instance);
}
