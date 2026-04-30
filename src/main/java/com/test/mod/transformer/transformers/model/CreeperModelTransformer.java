package com.test.mod.transformer.transformers.model;

import com.test.mod.transformer.ITransformer;
import com.test.mod.transformer.annotation.ClassTransformer;
import com.test.mod.transformer.annotation.Reflect;
import net.minecraft.client.model.CreeperModel;
import net.minecraft.client.model.geom.ModelPart;

import java.util.List;

@ClassTransformer(CreeperModel.class)
public class CreeperModelTransformer implements ITransformer {
    //   private final ModelPart root;
    @Reflect("root")
    public native static ModelPart getRoot(CreeperModel<?> instance);
}
