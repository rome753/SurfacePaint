package cc.rome753.opengles3.shader;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES31;
import android.util.Log;

import cc.rome753.opengles3.App;

import static android.opengl.GLES30.*;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class ShaderUtils {

    public static int loadProgram() {
        int vShader = ShaderUtils.loadShader(GL_VERTEX_SHADER, loadAssets("shader_base_v.glsl"));
        int fShader = ShaderUtils.loadShader(GL_FRAGMENT_SHADER, loadAssets("shader_base_f.glsl"));
        return linkProgram(vShader, fShader);
    }

    public static int loadProgramCompute() {
        int computeShader = ShaderUtils.loadShader(GLES31.GL_COMPUTE_SHADER, loadAssets("compute.glsl"));
        return linkProgram(computeShader);
    }

    public static int loadProgramComputeDraw() {
        int vShader = ShaderUtils.loadShader(GL_VERTEX_SHADER, loadAssets("compute_v.glsl"));
        int fShader = ShaderUtils.loadShader(GL_FRAGMENT_SHADER, loadAssets("compute_f.glsl"));
        return linkProgram(vShader, fShader);
    }

    public static int loadProgramBox() {
        int vShader = ShaderUtils.loadShader(GL_VERTEX_SHADER, loadAssets("box_v.glsl"));
        int fShader = ShaderUtils.loadShader(GL_FRAGMENT_SHADER, loadAssets("box_f.glsl"));
        return linkProgram(vShader, fShader);
    }

    public static int loadProgram3D() {
        int vShader = ShaderUtils.loadShader(GL_VERTEX_SHADER, loadAssets("shader3d_v.glsl"));
        int fShader = ShaderUtils.loadShader(GL_FRAGMENT_SHADER, loadAssets("shader3d_f.glsl"));
        return linkProgram(vShader, fShader);
    }

    public static int loadProgramFramebuffer() {
        int vShader = ShaderUtils.loadShader(GL_VERTEX_SHADER, loadAssets("framebuffer_v.glsl"));
        int fShader = ShaderUtils.loadShader(GL_FRAGMENT_SHADER, loadAssets("framebuffer_f.glsl"));
        return linkProgram(vShader, fShader);
    }

    public static int loadProgramFractor() {
        int vShader = ShaderUtils.loadShader(GL_VERTEX_SHADER, loadAssets("shader_fractal_v.glsl"));
        int fShader = ShaderUtils.loadShader(GL_FRAGMENT_SHADER, loadAssets("shader_fractal_f.glsl"));
        return linkProgram(vShader, fShader);
    }

    public static int loadProgramAudio() {
        int vShader = ShaderUtils.loadShader(GL_VERTEX_SHADER, loadAssets("shader_audio_v.glsl"));
        int fShader = ShaderUtils.loadShader(GL_FRAGMENT_SHADER, loadAssets("shader_audio_f.glsl"));
        return linkProgram(vShader, fShader);
    }

    public static int loadProgramGroup() {
        int vShader = ShaderUtils.loadShader(GL_VERTEX_SHADER, loadAssets("shader_group_v.glsl"));
        int fShader = ShaderUtils.loadShader(GL_FRAGMENT_SHADER, loadAssets("shader_group_f.glsl"));
        return linkProgram(vShader, fShader);
    }

    public static int loadProgramGroup3D() {
        int vShader = ShaderUtils.loadShader(GL_VERTEX_SHADER, loadAssets("shader_group3d_v.glsl"));
        int fShader = ShaderUtils.loadShader(GL_FRAGMENT_SHADER, loadAssets("shader_group3d_f.glsl"));
        return linkProgram(vShader, fShader);
    }
    public static int loadProgramParticles() {
        int vShader = ShaderUtils.loadShader(GL_VERTEX_SHADER, loadAssets("particles_v.glsl"));
        int fShader = ShaderUtils.loadShader(GL_FRAGMENT_SHADER, loadAssets("particles_f.glsl"));
        return linkProgram(vShader, fShader);
    }

    public static int loadProgramTransformFeedback() {
        int vShader = ShaderUtils.loadShader(GL_VERTEX_SHADER, loadAssets("transform_feedback_v.glsl"));
        int fShader = ShaderUtils.loadShader(GL_FRAGMENT_SHADER, loadAssets("transform_feedback_f.glsl"));
//        return linkProgram(vShader, fShader, new String[]{"vPos", "vVel"});
        return linkProgram(vShader, fShader, new String[]{"vPos"});
    }

    public static int loadProgram3DLighting() {
        int vShader = ShaderUtils.loadShader(GL_VERTEX_SHADER, loadAssets("shader_lighting_v.glsl"));
        int fShader = ShaderUtils.loadShader(GL_FRAGMENT_SHADER, loadAssets("shader_lighting_f.glsl"));
        return linkProgram(vShader, fShader);
    }

    public static int loadProgram(String vs, String fs) {
        int vShader = ShaderUtils.loadShader(GL_VERTEX_SHADER, vs);
        int fShader = ShaderUtils.loadShader(GL_FRAGMENT_SHADER, fs);
        return linkProgram(vShader, fShader);
    }

    public static int loadProgram(String vs, String fs, String[] transformFeedbackVaryings) {
        int vShader = ShaderUtils.loadShader(GL_VERTEX_SHADER, vs);
        int fShader = ShaderUtils.loadShader(GL_FRAGMENT_SHADER, fs);
        return linkProgram(vShader, fShader, transformFeedbackVaryings);
    }

    private static int loadShader(int type, String shaderSrc) {
        int shader = glCreateShader(type);
        if (shader == 0) {
            Log.e("chao", "compile shader == 0");
            return 0;
        }
        glShaderSource(shader, shaderSrc);
        glCompileShader(shader);
        int[] compileStatus = new int[1];
        glGetShaderiv(shader, GL_COMPILE_STATUS, compileStatus, 0);
        if (compileStatus[0] == 0) {
            String log = glGetShaderInfoLog(shader);
            Log.e("chao", "glGetShaderiv fail " + log);
            glDeleteShader(shader);
            return 0;
        }
        return shader;
    }

    private static int linkProgram(int computeShader) {
        return linkProgram(new int[]{computeShader}, null);
    }

    private static int linkProgram(int vShader, int fShader) {
        return linkProgram(vShader, fShader, null);
    }

    private static int linkProgram(int vShader, int fShader, String[] transformFeedbackVaryings) {
        return linkProgram(new int[]{vShader, fShader}, transformFeedbackVaryings);
    }

    private static int linkProgram(int[] shaders, String[] transformFeedbackVaryings) {
        int program = glCreateProgram();
        if (program == 0) {
            Log.e("chao", "program == 0");
            return 0;
        }

        for (int shader: shaders) {
            glAttachShader(program, shader);
        }
//        glAttachShader(program, vShader);
//        glAttachShader(program, fShader);

        if (transformFeedbackVaryings != null) {
            glTransformFeedbackVaryings(program, transformFeedbackVaryings, GL_INTERLEAVED_ATTRIBS);
        }

        glLinkProgram(program);
        int[] linkStatus = new int[1];
        glGetProgramiv(program, GL_LINK_STATUS, linkStatus, 0);
        if (linkStatus[0] == 0) {
            String log = glGetProgramInfoLog(program);
            Log.e("chao", "linkProgram fail " + log);
            glDeleteProgram(program);
            return 0;
        }
        return program;
    }


    public static String loadAssets(String name) {
        String s = null;
        try {
            InputStream is = App.getApp().getAssets().open(name);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            s = new String(buffer, StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return s;
    }

    public static Bitmap loadImageAssets(String name) {
        try {
            InputStream is = App.getApp().getAssets().open(name);
            return BitmapFactory.decodeStream(is);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
