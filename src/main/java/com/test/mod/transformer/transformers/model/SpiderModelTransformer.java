package com.test.mod.transformer.transformers.model;

import com.test.mod.transformer.ITransformer;
import com.test.mod.transformer.annotation.ClassTransformer;
import com.test.mod.transformer.annotation.Reflect;
import net.minecraft.client.model.CreeperModel;
import net.minecraft.client.model.SpiderModel;
import net.minecraft.client.model.geom.ModelPart;

import java.util.List;

@ClassTransformer(SpiderModel.class)
public class SpiderModelTransformer implements ITransformer {
    @Reflect("root")
    public native static ModelPart getRoot(SpiderModel<?> instance);
}
