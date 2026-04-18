package com.test.mod.transformer.transformers;

import com.test.mod.transformer.ITransformer;
import com.test.mod.transformer.annotation.*;
import net.minecraft.client.Minecraft;

@ClassTransformer(Minecraft.class)
public class MinecraftTransformer implements ITransformer {
    @Reflect("isLocalServer")
    public native static boolean isLocalServer(Minecraft instance);

    @Reflect("isLocalServer")
    public native static void setLocalServer(Minecraft instance, boolean value);
//    @Reflect(value = "abortResourcePackRecovery", desc = "()V")
//    public native static void abortResourcePackRecovery(Minecraft instance);

    @Inject(methodName = "runTick", desc = "(Z)V", at = @At("HEAD"))
    public void runTick() {

    }
}
