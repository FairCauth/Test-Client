package com.test.mod;

import com.mojang.blaze3d.platform.Window;
import com.test.mod.asm.tree.AbstractInsnNode;
import com.test.mod.asm.tree.MethodNode;
import com.test.mod.transformer.ITransformer;
import com.test.mod.transformer.annotation.*;
import com.test.mod.transformer.callback.CallbackInfo;
import com.test.mod.transformer.callback.CallbackInfoReturnable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientPacketListener;

import java.util.ListIterator;

@ClassTransformer(Minecraft.class)
public abstract class ExampleTransformer implements ITransformer {
    @Reflect("isLocalServer")
    public native static boolean isLocalServer(Minecraft instance);
    @Shadow("demo")//final不可进行赋值
    private boolean demo22;
    @Shadow("window")//final
    private Window window;
    @Shadow(value = "getConnection", desc = "()Lnet/minecraft/client/multiplayer/ClientPacketListener;")
    public abstract ClientPacketListener getConnection();
    @Shadow(value = "debugClientMetricsCancel", desc = "()V")
    public abstract void debugClientMetricsCancel();
    //Overwrite使用例子
    @Overwrite(methodName = "stop", desc = "()V")
    public void stop() {
        System.out.println("1111111111111111");
        debugClientMetricsCancel();
        System.out.println(demo22);
    }
    //Inject使用例子
    //1.参数要与原方法保持一致,如需callback 则在参数最后加CallbackInfo
    //2.Inject必须与目标方法的static保持一致
    //由于jvm不支持在目标类创建新方法，所以Inject会将你的代码直接般到目标方法

    //获取局部变量 @Local() value是对应的变量索引 如ALOAD 2
    //@Local必须在参数最后面 如果有callbackInfo 则要在CallbackInfo后
    @Inject(methodName = "clearLevel", desc = "(Lnet/minecraft/client/gui/screens/Screen;)V",
            at = @At(
                    value = "INVOKE",
                    target ="Lnet/minecraft/client/gui/screens/social/PlayerSocialManager;stopOnlineMode()V",
                    shift = At.Shift.BEFORE
            ),cancellable = true)
    public void clearLevel(Screen p_91321_,
                           CallbackInfo callbackInfo, @Local(2) ClientPacketListener clientPacketListener) {

        clientPacketListener = null;//可对局部变量进行操作
        String str = "TestDDDDDD";
        int asf = 12124;
        double b = 0.01d;
        System.out.println(asf + b);
        System.out.println(str);
        //调用类内部方法(Shadow)
        debugClientMetricsCancel();
        ClientPacketListener clientPacketListener1 = getConnection();
    }
    //静态类Inject使用例子
    //CallbackInfo带返回值版本
    @Inject(methodName = "checkIs64Bit", desc = "()Z", at = @At("HEAD"), cancellable = true)
    public static void checkIs64Bit(CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
        int a = 123;
        String f = "fasfasfas";
        System.out.println(a + f);
        callbackInfoReturnable.setReturnValue(true);

    }

    //ASM使用例子
    @ASM(methodName = "runTick", desc = "(Z)V")
    public static void runTick_asm(MethodNode methodNode){
        ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();
        while (iterator.hasNext()) {
            //iterator.add();
            //iterator.remove();
            break;
        }
    }

    //Hook使用例子 （与Inject不同 Hook必须是static; 目标方法会执行ExampleTransformer.test()，不可使用Shadow Local）
    @Hook(methodName = "selectMainFont", desc = "(Z)V", at = @At("HEAD"))
    public static void test(){
        // System.out.println("hooked");
    }


}

