package com.test.mod.ui.system.impl;

import com.test.mod.ui.system.utils.CanvasStack;
import com.test.mod.ui.system.utils.ImageUtil;
import io.github.humbleui.skija.*;
import io.github.humbleui.types.Rect;
import lombok.experimental.UtilityClass;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
@UtilityClass
public class LiquidGlassUtil {
    String sksl = """
uniform shader inputImage;
uniform float2 size;
uniform float time;
uniform float strength;
uniform float blur;
uniform float lightIntensity;

half4 main(float2 fragCoord) {
    float2 uv = fragCoord / size;

    // 简单折射
    float dx = sin(uv.y * 20 + time * 2.0) * strength;
    float dy = cos(uv.x * 15 + time * 1.5) * strength;
    float2 pos = fragCoord + float2(dx, dy);

    // 高斯模糊 5x5
    float2 texel = 1.0 / size;
    half4 c = half4(0);
    c += inputImage.eval(pos + texel*float2(-1,-1)) * 0.0625;
    c += inputImage.eval(pos + texel*float2( 0,-1)) * 0.125;
    c += inputImage.eval(pos + texel*float2( 1,-1)) * 0.0625;
    c += inputImage.eval(pos + texel*float2(-1, 0)) * 0.125;
    c += inputImage.eval(pos + texel*float2( 0, 0)) * 0.25;
    c += inputImage.eval(pos + texel*float2( 1, 0)) * 0.125;
    c += inputImage.eval(pos + texel*float2(-1, 1)) * 0.0625;
    c += inputImage.eval(pos + texel*float2( 0, 1)) * 0.125;
    c += inputImage.eval(pos + texel*float2( 1, 1)) * 0.0625;

    // 高光
    float highlight = lightIntensity * (0.3 + 0.7 * uv.y);
    c.rgb += highlight;

    return c;
}
""";

    private static final String TEST = """
half4 main(float2 p){
    return half4(1,0,0,1);
}
""";

    private static RuntimeEffect effect;
    private static RuntimeEffect effect2;
    static {
        try {
            effect = RuntimeEffect.makeForShader(sksl);
            effect2 = RuntimeEffect.makeForShader(TEST);
        }catch (Exception e) {
            System.out.println("EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE");
            e.printStackTrace();
        }

    }
    public static Data makeUniforms(float... values) {

        ByteBuffer buffer = ByteBuffer
                .allocate(values.length * 4)
                .order(ByteOrder.nativeOrder());

        for (float v : values) {
            buffer.putFloat(v);
        }buffer.rewind();

        return Data.makeFromBytes(buffer.array());
    }
    private final Paint blurPaint = new Paint();
    public void test(CanvasStack stack,float x, float y, float width, float height) {
        stack.canvas.resetMatrix();
        Image image = ImageUtil.getTextureImage(stack.context);

        if (image != null) {
            float imgW = image.getWidth();
            float imgH = image.getHeight();

            Shader imageShader = image.makeShader(
                    FilterTileMode.CLAMP,
                    FilterTileMode.CLAMP
            );
            float strength = 6f;       // 折射强度
            float blur = 5.5f;         // 模糊半径（和 Shader权重配合）
            float light = 0.12f;       // 高光强度

            float time = System.currentTimeMillis() / 1000f;
            Data uniforms = makeUniforms(
                    imgW,
                    imgH,
                    time,
                    strength,
                    blur,
                    light
            );


            Shader shader = effect.makeShader(
                    uniforms,new Shader[]{imageShader},
                    null
            );

            blurPaint.setShader(shader);
            stack.canvas.drawRect(
                    Rect.makeXYWH(x, y, width, height),
                    blurPaint
            );
            blurPaint.setShader(null);
            shader.close();

            imageShader.close();
        }

    }
}
