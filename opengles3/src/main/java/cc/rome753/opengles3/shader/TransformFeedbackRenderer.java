// The MIT License (MIT)
//
// Copyright (c) 2013 Dan Ginsburg, Budirijanto Purnomo
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in
// all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
// THE SOFTWARE.

//
// Book:      OpenGL(R) ES 3.0 Programming Guide, 2nd Edition
// Authors:   Dan Ginsburg, Budirijanto Purnomo, Dave Shreiner, Aaftab Munshi
// ISBN-10:   0-321-93388-5
// ISBN-13:   978-0-321-93388-1
// Publisher: Addison-Wesley Professional
// URLs:      http://www.opengles-book.com
//            http://my.safaribooksonline.com/book/animation-and-3d/9780133440133
//

// ParticleSystem
//
//    This is an example that demonstrates rendering a particle system
//    using a vertex shader and point sprites.
//

package cc.rome753.opengles3.shader;

import static android.opengl.GLES20.GL_ARRAY_BUFFER;
import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_DYNAMIC_DRAW;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_POINTS;
import static android.opengl.GLES20.glBindBuffer;
import static android.opengl.GLES20.glBufferData;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glDisable;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnable;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGenBuffers;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform3fv;
import static android.opengl.GLES20.glUseProgram;
import static android.opengl.GLES20.glVertexAttribPointer;
import static android.opengl.GLES20.glViewport;
import static android.opengl.GLES30.GL_RASTERIZER_DISCARD;
import static android.opengl.GLES30.GL_TRANSFORM_FEEDBACK;
import static android.opengl.GLES30.GL_TRANSFORM_FEEDBACK_BUFFER;
import static android.opengl.GLES30.glBeginTransformFeedback;
import static android.opengl.GLES30.glBindBufferBase;
import static android.opengl.GLES30.glBindTransformFeedback;
import static android.opengl.GLES30.glBindVertexArray;
import static android.opengl.GLES30.glEndTransformFeedback;
import static android.opengl.GLES30.glGenTransformFeedbacks;
import static android.opengl.GLES30.glGenVertexArrays;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Random;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class TransformFeedbackRenderer extends BaseRender {

   int program;
   int[] vao;
   int[] vbo;

   int[] tfo;

   static int MAX_COUNT = 100000;
   float[] pts = new float[MAX_COUNT * 4]; // x,y,vx,vy
   Random r = new Random();

   float[] time = {0f, 0f, 0f}; // time,centerX,centerY

   @Override
   public void onSurfaceCreated(GL10 gl, EGLConfig config) {

      initAll();

      program = ShaderUtils.loadProgramTransformFeedback();


      // 分配内存空间,每个浮点型占4字节空间
      ByteBuffer buffer = ByteBuffer
              .allocateDirect(pts.length * 4)
              .order(ByteOrder.nativeOrder());

      buffer.position(0);
      buffer.asFloatBuffer().put(pts);

      vao = new int[2];
      glGenVertexArrays(2, vao, 0);
      vbo = new int[2];
      glGenBuffers(2, vbo, 0);
      tfo = new int[2];
      glGenTransformFeedbacks(2, tfo, 0);

      for (int i = 0; i < 2; i++) {

         glBindTransformFeedback(GL_TRANSFORM_FEEDBACK, tfo[i]);

         glBindVertexArray(vao[i]);
         glBindBuffer(GL_ARRAY_BUFFER, vbo[i]);
         glBufferData(GL_ARRAY_BUFFER, MAX_COUNT * 4 * 4, buffer, GL_DYNAMIC_DRAW);
         glBindBufferBase(GL_TRANSFORM_FEEDBACK_BUFFER, 0, vbo[i]);

         // Load the vertex data
         glVertexAttribPointer(0, 2, GL_FLOAT, false, 2 * 4, 0);
         glEnableVertexAttribArray(0);
         glVertexAttribPointer(1, 2, GL_FLOAT, false, 2 * 4, 2 * 4);
         glEnableVertexAttribArray(1);

      }



      glBindBuffer(GL_ARRAY_BUFFER, 0);
      glBindVertexArray(0);

      glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

   }

   @Override
   public void onSurfaceChanged(GL10 gl, int width, int height) {
      glViewport(0, 0, width, height);
//        glViewport(-(height - width) / 2, 0, height, height);
      this.width = width;
      this.height = height;
   }

   int width, height;

   int i0 = 0;

   @Override
   public void onDrawFrame(GL10 gl) {
      super.onDrawFrame(gl);

      // Clear the color buffer
      glClear(GL_COLOR_BUFFER_BIT);

      // Use the program object
      glUseProgram(program);

      time[0] += 0.01;

      int loc = glGetUniformLocation(program, "time");
      glUniform3fv(loc, 1, time, 0);


      glEnable(GL_RASTERIZER_DISCARD);

      glBindVertexArray(vao[i0]);
      glBindBufferBase(GL_TRANSFORM_FEEDBACK_BUFFER, 0, vbo[i0]);
      glBindTransformFeedback(GL_TRANSFORM_FEEDBACK, tfo[i0]);
      glBeginTransformFeedback(GL_POINTS);
      glDrawArrays(GL_POINTS, 0, MAX_COUNT);
      glEndTransformFeedback();

      glDisable(GL_RASTERIZER_DISCARD);

      int i1 = (i0 + 1) % 2;
      glBindVertexArray(vao[i1]);
      glBindBufferBase(GL_TRANSFORM_FEEDBACK_BUFFER, 0, vbo[i1]);
      glBindTransformFeedback(GL_TRANSFORM_FEEDBACK, tfo[i1]);
      glDrawArrays(GL_POINTS, 0, MAX_COUNT);


      // 交换
      i0 = i1;
   }

   void initAll() {
      for (int i = 0; i < pts.length; i++) {
         pts[i] = (float)r.nextInt(1000) / 1000;
         if (i % 4 < 2) {
            pts[i] = (pts[i] - 0.5f) * 2;
         } else {
            pts[i] = pts[i] - 0.5f;
         }
      }

   }


}