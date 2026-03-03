package com.test.mod.ui.system.impl;


import com.test.mod.ui.system.utils.CanvasStack;
import com.test.mod.ui.system.utils.ImageUtil;
import io.github.humbleui.skija.*;
import io.github.humbleui.types.RRect;
import io.github.humbleui.types.Rect;
import lombok.experimental.UtilityClass;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

@UtilityClass
public class BlurUtil {
    private final Paint blurPaint = new Paint();
    private static final String TEST = """
half4 main(float2 p){
    return half4(1,0,0,1);
}
""";
    private static final String GAUSSIAN_SHADER = """
uniform shader inputImage;
uniform float2 iResolution;
uniform float blurRadius;

half4 main(float2 fragCoord) {

    // 归一化坐标
    float2 uv = fragCoord / iResolution;

    float2 texel = 1.0 / iResolution;

    half4 color = half4(0);

    float kernel[9];
    kernel[0] = 0.05;
    kernel[1] = 0.09;
    kernel[2] = 0.12;
    kernel[3] = 0.15;
    kernel[4] = 0.16;
    kernel[5] = 0.15;
    kernel[6] = 0.12;
    kernel[7] = 0.09;
    kernel[8] = 0.05;

    for (int x = -4; x <= 4; x++) {
        for (int y = -4; y <= 4; y++) {

            float2 offset =
                float2(x, y) * texel * blurRadius;

            color += inputImage.eval(
                (uv + offset) * iResolution
            ) * kernel[x + 4] * kernel[y + 4];
        }
    }

    return color;
}
""";


    private static RuntimeEffect effect;
    static {
        effect = RuntimeEffect.makeForShader(TEST);
    }
    public static Data makeUniforms(float... values) {

        ByteBuffer buffer = ByteBuffer
                .allocate(values.length * 4)
                .order(ByteOrder.nativeOrder());

        for (float v : values) {
            buffer.putFloat(v);
        }

        return Data.makeFromBytes(buffer.array());
    }
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

            float time = 3;
//            Data uniforms = makeUniforms(
//                    imgW,
//                    imgH,
//                    time
//            );


                Shader shader = effect.makeShader(
                        null,null,
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
    public void draw(CanvasStack stack, float x, float y, float width, float height, float radius, float blurRadius, int alpha) {
        if (width <= 0 || height <= 0 || alpha <= 0) return;

        try (ImageFilter blur = ImageFilter.makeBlur(blurRadius, blurRadius, FilterTileMode.CLAMP)) {
            blurPaint.setAlpha(alpha);
            blurPaint.setImageFilter(blur);

            stack.push();

            if (radius > 0) {
                blurPaint.setAntiAlias(true);
                stack.canvas.clipRRect(RRect.makeXYWH(x, y, width, height, radius), true);
            } else {
                blurPaint.setAntiAlias(false);
                stack.canvas.clipRect(Rect.makeXYWH(x, y, width, height), ClipMode.INTERSECT, false);
            }

            stack.canvas.resetMatrix();
            Image image = ImageUtil.getTextureImage(stack.context);
            if (image != null) {
                stack.canvas.drawImage(image, 0, 0, blurPaint);
            }

            stack.pop();
        } finally {
            blurPaint.setImageFilter(null);
        }
    }
}