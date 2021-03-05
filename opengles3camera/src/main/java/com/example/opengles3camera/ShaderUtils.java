package com.example.opengles3camera;

import android.util.Log;

import static android.opengl.GLES30.GL_COMPILE_STATUS;
import static android.opengl.GLES30.GL_FRAGMENT_SHADER;
import static android.opengl.GLES30.GL_LINK_STATUS;
import static android.opengl.GLES30.GL_VERTEX_SHADER;
import static android.opengl.GLES30.glAttachShader;
import static android.opengl.GLES30.glCompileShader;
import static android.opengl.GLES30.glCreateProgram;
import static android.opengl.GLES30.glCreateShader;
import static android.opengl.GLES30.glDeleteProgram;
import static android.opengl.GLES30.glDeleteShader;
import static android.opengl.GLES30.glGetProgramInfoLog;
import static android.opengl.GLES30.glGetProgramiv;
import static android.opengl.GLES30.glGetShaderInfoLog;
import static android.opengl.GLES30.glGetShaderiv;
import static android.opengl.GLES30.glLinkProgram;
import static android.opengl.GLES30.glShaderSource;

public class ShaderUtils {

    public static int loadProgramCamera() {
        int vShader = ShaderUtils.loadShader(GL_VERTEX_SHADER, Utils.loadAssets("shader_camera_v.txt"));
        int fShader = ShaderUtils.loadShader(GL_FRAGMENT_SHADER, Utils.loadAssets("shader_camera_f.txt"));
        return linkProgram(vShader, fShader);
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

    private static int linkProgram(int vShader, int fShader) {
        int program = glCreateProgram();
        if (program == 0) {
            Log.e("chao", "program == 0");
            return 0;
        }

        glAttachShader(program, vShader);
        glAttachShader(program, fShader);

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
}
