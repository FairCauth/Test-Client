package com.test.mod.utils.render;

import com.darkmagician6.eventapi.EventManager;
import com.darkmagician6.eventapi.EventTarget;
import com.test.mod.events.EventRender3D;
import com.test.mod.utils.IMinecraft;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

public class Projection implements IMinecraft {
    private Matrix4f modelViewMatrix, projectionMatrix;

    public Projection() {
        EventManager.register(this);
    }

    @EventTarget
    public void onRender3D(EventRender3D event) {
        this.modelViewMatrix = event.getPoseStack().last().pose();
        this.projectionMatrix = event.getMatrix4f();
    }
    public Vec3 projectToScreen(Vec3 pos) {
        int[] viewPort = {0,0,mc.getWindow().getGuiScaledWidth(), mc.getWindow().getGuiScaledHeight()};
        double[] res = world2Screen(modelViewMatrix, projectionMatrix, pos, viewPort,1);
        return new Vec3(res[0], res[1], res[2]);
    }
    private static double[] world2Screen(Matrix4f modelViewMatrix, Matrix4f projectionMatrix, Vec3 pos, int[] viewport, double guiScale) {
        float[] win_pos = new float[3];
        boolean success = gluProject(
                pos.x, pos.y, pos.z,
                matrixToArray(modelViewMatrix), matrixToArray(projectionMatrix), viewport, win_pos
        );
        if (!success) {
            return new double[]{};
        } else {
            return new double[]{
                    win_pos[0] / (float) guiScale,
                    (viewport[3] - win_pos[1]) / (float) guiScale,
                    win_pos[2]
            };
        }
    }
    private static boolean gluProject(double objx, double objy, double objz, float[] modelMatrix, float[] projMatrix, int[] viewport, float[] win_pos) {

        float[] inVec = { (float) objx, (float) objy, (float) objz, 1.0f };
        float[] outVec = new float[4];

        gluMultMatrixVecf(modelMatrix, inVec, outVec);
        gluMultMatrixVecf(projMatrix, outVec, inVec);

        if (inVec[3] == 0.0f) {
            return false;
        }

        float w = 1.0f / inVec[3];
        inVec[0] *= w;
        inVec[1] *= w;
        inVec[2] *= w;

        inVec[0] = inVec[0] * 0.5f + 0.5f;
        inVec[1] = inVec[1] * 0.5f + 0.5f;
        inVec[2] = inVec[2] * 0.5f + 0.5f;

        win_pos[0] = inVec[0] * viewport[2] + viewport[0];
        win_pos[1] = inVec[1] * viewport[3] + viewport[1];
        win_pos[2] = inVec[2];

        return true;
    }
    private static void gluMultMatrixVecf(float[] m, float[] in, float[] out) {
        //1.18
//        for (int i = 0; i < 4; i++) {
//            out[i] = in[0] * m[i] +
//                    in[1] * m[4 + i] +
//                    in[2] * m[2 * 4 + i] +
//                    in[3] * m[3 * 4 + i];
//        }
        //1.20
        for (int i = 0; i < 4; i++) {
            out[i] = in[0] * m[i * 4] +
                    in[1] * m[i * 4 + 1] +
                    in[2] * m[i * 4 + 2] +
                    in[3] * m[i * 4 + 3];
        }
    }
    private static float[] matrixToArray(Matrix4f matrix) {
        return new float[]{
                matrix.get(0, 0), matrix.get(1, 0), matrix.get(2, 0), matrix.get(3, 0),
                matrix.get(0, 1), matrix.get(1, 1), matrix.get(2, 1), matrix.get(3, 1),
                matrix.get(0, 2), matrix.get(1, 2), matrix.get(2, 2), matrix.get(3, 2),
                matrix.get(0, 3), matrix.get(1, 3), matrix.get(2, 3), matrix.get(3, 3)
        };
    }
    public Matrix4f getModelViewMatrix() {
        return modelViewMatrix;
    }

    public Matrix4f getProjectionMatrix() {
        return projectionMatrix;
    }
}
