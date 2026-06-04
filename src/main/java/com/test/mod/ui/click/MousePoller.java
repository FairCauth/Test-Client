package com.test.mod.ui.click;

import com.mojang.blaze3d.platform.Window;
import com.test.mod.utils.IMinecraft;
import org.lwjgl.glfw.GLFW;

import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWScrollCallbackI;

public class MousePoller implements IMinecraft{

    private static boolean lastLeft;
    private static boolean lastRight;
    private static boolean lastMiddle;

    private static boolean scrollInstalled = false;
    private static long installedWindow = 0L;

    private static GLFWScrollCallbackI oldScrollCallback;
    private static GLFWScrollCallbackI newScrollCallback;

    private static double scrollX;
    private static double scrollY;

    public static void installScrollCallback() {
        long window = mc.getWindow().getWindow();

        if (scrollInstalled && installedWindow == window) {
            return;
        }

        installedWindow = window;

        newScrollCallback = (win, xoffset, yoffset) -> {
            scrollX += xoffset;
            scrollY += yoffset;

            if (oldScrollCallback != null) {
                oldScrollCallback.invoke(win, xoffset, yoffset);
            }
        };

        oldScrollCallback = GLFW.glfwSetScrollCallback(window, newScrollCallback);

        scrollInstalled = true;
    }

    public static void uninstallScrollCallback() {
        if (!scrollInstalled || installedWindow == 0L) {
            return;
        }

        GLFW.glfwSetScrollCallback(installedWindow, oldScrollCallback);

        oldScrollCallback = null;
        newScrollCallback = null;
        scrollInstalled = false;
        installedWindow = 0L;

        scrollX = 0;
        scrollY = 0;
    }

    public static void update(ClickGuiScreen screen) {

        long handle = mc.getWindow().getWindow();

        double rawX = mc.mouseHandler.xpos();
        double rawY = mc.mouseHandler.ypos();

        double mouseX = rawX * mc.getWindow().getGuiScaledWidth() / mc.getWindow().getScreenWidth();
        double mouseY = rawY * mc.getWindow().getGuiScaledHeight() / mc.getWindow().getScreenHeight();

        ClickGuiScreen.mouseX = (float) mouseX;
        ClickGuiScreen.mouseY = (float) mouseY;

        boolean left = GLFW.glfwGetMouseButton(handle, GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS;
        boolean right = GLFW.glfwGetMouseButton(handle, GLFW.GLFW_MOUSE_BUTTON_RIGHT) == GLFW.GLFW_PRESS;
        boolean middle = GLFW.glfwGetMouseButton(handle, GLFW.GLFW_MOUSE_BUTTON_MIDDLE) == GLFW.GLFW_PRESS;

        if (left && !lastLeft) {
            screen.handleMouseClicked(mouseX, mouseY, 0);
        }

        if (right && !lastRight) {
            screen.handleMouseClicked(mouseX, mouseY, 1);
        }

        if (middle && !lastMiddle) {
            screen.handleMouseClicked(mouseX, mouseY, 2);
        }

        if (!left && lastLeft) {
            screen.handleMouseReleased(mouseX, mouseY, 0);
        }

        if (!right && lastRight) {
            screen.handleMouseReleased(mouseX, mouseY, 1);
        }

        if (!middle && lastMiddle) {
            screen.handleMouseReleased(mouseX, mouseY, 2);
        }

        lastLeft = left;
        lastRight = right;
        lastMiddle = middle;

        double consumedScrollY = consumeScrollY();

        if (consumedScrollY != 0) {
            screen.handleMouseScrolled(mouseX, mouseY, consumedScrollY);
        }
    }

    private static double consumeScrollY() {
        double value = scrollY;
        scrollY = 0;
        return value;
    }
}