package com.test.mod.transformer.transformers;

import com.darkmagician6.eventapi.EventManager;
import com.test.mod.events.EventRender2D;
import com.test.mod.transformer.ITransformer;
import com.test.mod.transformer.annotation.At;
import com.test.mod.transformer.annotation.ClassTransformer;
import com.test.mod.transformer.annotation.Inject;
import com.test.mod.transformer.annotation.Local;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;

@ClassTransformer(Gui.class)
public class GuiTransformer implements ITransformer {
    //m_280518_
    @Inject(at = @At(value = "TAIL"), methodName = {"renderHotbar"}, desc = "(FLnet/minecraft/client/gui/GuiGraphics;)V")
    public void renderHotbar(float p_283031_, GuiGraphics p_282108_, @Local(0) Gui instance) {
        EventRender2D eventRender2D = new EventRender2D(p_282108_, p_282108_.pose(), Minecraft.getInstance().getWindow().getGuiScaledHeight(), Minecraft.getInstance().getWindow().getGuiScaledWidth());
        EventManager.call(eventRender2D);
    }
}
