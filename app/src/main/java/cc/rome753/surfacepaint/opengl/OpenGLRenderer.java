/***
 * Excerpted from "OpenGL ES for Android",
 * published by The Pragmatic Bookshelf.
 * Copyrights apply to this code. It may not be used to create training material, 
 * courses, books, articles, and the like. Contact us if you are in doubt.
 * We make no guarantees that this code is fit for any purpose. 
 * Visit http://www.pragmaticprogrammer.com/titles/kbogla for more book information.
***/
package cc.rome753.surfacepaint.opengl;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView.Renderer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import cc.rome753.surfacepaint.R;
import cc.rome753.surfacepaint.opengl.model.Dice;
import cc.rome753.surfacepaint.opengl.model.Sphere;
import cc.rome753.surfacepaint.opengl.programs.ColorShaderProgram;
import cc.rome753.surfacepaint.opengl.programs.TextureShaderProgram;
import cc.rome753.surfacepaint.opengl.util.MatrixHelper;
import cc.rome753.surfacepaint.opengl.util.TextureHelper;

import static android.opengl.GLES20.glViewport;
import static android.opengl.Matrix.multiplyMM;
import static android.opengl.Matrix.rotateM;
import static android.opengl.Matrix.setIdentityM;
import static android.opengl.Matrix.translateM;

public class OpenGLRenderer implements Renderer {
    private final Context context;

    private final float[] projectionMatrix = new float[16];
    private final float[] modelMatrix = new float[16];

    private Dice dice;
    private Sphere sphere;

    private TextureShaderProgram textureProgram;
    private TextureShaderProgram textureProgram1;
    private ColorShaderProgram colorProgram;
    
    private int texture;
    private int texture1;

    int width, height;
    float angle = 0f;

    float rotateX = 0f, rotateY = 0f, rotateZ = 0f;

    public OpenGLRenderer(Context context) {
        this.context = context;
    }

    @Override
    public void onSurfaceCreated(GL10 glUnused, EGLConfig config) {
        // Set the background frame color
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
//        GLES20.glClearDepthf(1.0f);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
//        GLES20.glDepthFunc(GLES20.GL_LEQUAL);

        dice = new Dice();
        sphere = new Sphere();

        textureProgram = new TextureShaderProgram(context);
        textureProgram1 = new TextureShaderProgram(context);
        colorProgram = new ColorShaderProgram(context);
        
        texture = TextureHelper.loadTexture(context, R.drawable.air_hockey_surface);
        texture1 = TextureHelper.loadTexture(context, R.drawable.earth);
    }

    @Override
    public void onSurfaceChanged(GL10 glUnused, int width, int height) {
        this.width = width;
        this.height = height;
        // Set the OpenGL viewport to fill the entire surface.
        glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 glUnused) {
        // Clear the rendering surface.
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        angle += 1f;
        angle %= 360f;

        MatrixHelper.perspectiveM(projectionMatrix, 60, (float) width
                / (float) height, 1f, 10f);
        setIdentityM(modelMatrix, 0);
        translateM(modelMatrix, 0, 0f, 0f, -2.5f);
        rotateM(modelMatrix, 0, angle , 0f, 1f, 0f);
        rotateM(modelMatrix, 0, rotateZ , 0f, 0f, 1f);
        rotateM(modelMatrix, 0, rotateX , 1f, 0f, 0f);
//        rotateM(modelMatrix, 0, rotateY , 0f, 1f, 0f);

        final float[] temp = new float[16];
        multiplyMM(temp, 0, projectionMatrix, 0, modelMatrix, 0);
        System.arraycopy(temp, 0, projectionMatrix, 0, temp.length);

        // Draw the dice.
        textureProgram1.useProgram();
        textureProgram1.setUniforms(projectionMatrix, texture1);
        sphere.bindData(textureProgram1);
        sphere.draw();
//        dice.bindData(textureProgram1);
//        dice.draw();

    }

    public void rotate(float x, float y, float z){
        rotateX = y*5;
        rotateY = -x*5;
        rotateZ = z * 10;
    }
}