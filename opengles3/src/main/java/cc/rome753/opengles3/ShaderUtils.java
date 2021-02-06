package cc.rome753.opengles3;
import android.util.Log;

import static android.opengl.GLES30.*;

public class ShaderUtils {

    public static int loadProgram() {
        int vShader = ShaderUtils.loadShader(GL_VERTEX_SHADER, Utils.loadAssets("vShader.txt"));
        int fShader = ShaderUtils.loadShader(GL_FRAGMENT_SHADER, Utils.loadAssets("fShader.txt"));
        return linkProgram(vShader, fShader);
    }

    public static int loadProgram3D() {
        int vShader = ShaderUtils.loadShader(GL_VERTEX_SHADER, Utils.loadAssets("3dvShader.txt"));
        int fShader = ShaderUtils.loadShader(GL_FRAGMENT_SHADER, Utils.loadAssets("3dfShader.txt"));
        return linkProgram(vShader, fShader);
    }

    public static int loadProgram(String vs, String fs) {
        int vShader = ShaderUtils.loadShader(GL_VERTEX_SHADER, vs);
        int fShader = ShaderUtils.loadShader(GL_FRAGMENT_SHADER, fs);
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
