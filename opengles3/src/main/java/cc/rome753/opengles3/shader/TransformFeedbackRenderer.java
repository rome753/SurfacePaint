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
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGenBuffers;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform3fv;
import static android.opengl.GLES20.glUseProgram;
import static android.opengl.GLES20.glVertexAttribPointer;
import static android.opengl.GLES20.glViewport;
import static android.opengl.GLES30.GL_MAP_READ_BIT;
import static android.opengl.GLES30.GL_TRANSFORM_FEEDBACK;
import static android.opengl.GLES30.GL_TRANSFORM_FEEDBACK_BUFFER;
import static android.opengl.GLES30.glBeginTransformFeedback;
import static android.opengl.GLES30.glBindBufferBase;
import static android.opengl.GLES30.glBindTransformFeedback;
import static android.opengl.GLES30.glBindVertexArray;
import static android.opengl.GLES30.glEndTransformFeedback;
import static android.opengl.GLES30.glGenTransformFeedbacks;
import static android.opengl.GLES30.glGenVertexArrays;
import static android.opengl.GLES30.glMapBufferRange;
import static android.opengl.GLES30.glUnmapBuffer;

import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Random;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class TransformFeedbackRenderer extends BaseRender {

   int program;
   int[] vao;
   int[] vbo;

   int[] tfo;

   int particles = 1000000;
   int particlesLen = particles * 4;
   int particlesBytes = particlesLen * 4;

   float[] buf = new float[particlesLen]; // x,y,vx,vy
   Random r = new Random();

   float[] time = {0f, 0f, 0f}; // time,centerX,centerY

   public void updateCenter(float x, float y) {
      time[1] = (x / width - 0.5f) * 2;
      time[2] = -(y / height - 0.5f) * 2;
   }

   @Override
   public void onSurfaceCreated(GL10 gl, EGLConfig config) {

      initAll();

      program = ShaderUtils.loadProgramTransformFeedback();

      // 分配内存空间,每个浮点型占4字节空间
      ByteBuffer buffer = ByteBuffer
              .allocateDirect(particlesBytes)
              .order(ByteOrder.nativeOrder());

      buffer.position(0);
      buffer.asFloatBuffer().put(buf);

      vao = new int[2];
      glGenVertexArrays(2, vao, 0);
      vbo = new int[2];
      glGenBuffers(2, vbo, 0);
      tfo = new int[2];
      glGenTransformFeedbacks(2, tfo, 0);

      for (int i = 0; i < 2; i++) {
         glBindVertexArray(vao[i]);
         glBindBuffer(GL_ARRAY_BUFFER, vbo[i]);
         glBufferData(GL_ARRAY_BUFFER, particlesBytes, buffer, GL_DYNAMIC_DRAW);

         // Load the vertex data
         glVertexAttribPointer(0, 4, GL_FLOAT, false, 4 * 4, 0);
         glEnableVertexAttribArray(0);
//         glVertexAttribPointer(1, 2, GL_FLOAT, false, 2 * 4, 2 * 4);
//         glEnableVertexAttribArray(1);

         glBindTransformFeedback(GL_TRANSFORM_FEEDBACK, tfo[i]);
         glBindBufferBase(GL_TRANSFORM_FEEDBACK_BUFFER, 0, vbo[i]);
      }

      glClearColor(1.0f, 1.0f, 1.0f, 1.0f);

   }

   @Override
   public void onSurfaceChanged(GL10 gl, int width, int height) {
      glViewport(0, 0, width, height);
//        glViewport(-(height - width) / 2, 0, height, height);
      this.width = width;
      this.height = height;
   }

   int width, height;

   int i0 = 1;
   int frames = 0;

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


      // 绑定tfo[0]，接收输出的顶点数据，数据相当于输出到vao[0]了
      glBindTransformFeedback(GL_TRANSFORM_FEEDBACK, tfo[i0]);
      glBeginTransformFeedback(GL_POINTS);
      // 绑定vao[1]，输入顶点数据
      glBindVertexArray(vao[1 - i0]);
      glDrawArrays(GL_POINTS, 0, particles);
      glEndTransformFeedback();


//      if (frames++ < 10) {
//         ByteBuffer bb = (ByteBuffer) glMapBufferRange(GL_TRANSFORM_FEEDBACK_BUFFER, 0, particlesBytes, GL_MAP_READ_BIT);
//         if (bb != null) {
//            FloatBuffer res = bb.order(ByteOrder.LITTLE_ENDIAN).asFloatBuffer();
//            String s = "";
//            for (int i = 0; i < particlesLen; i++) {
//               float num = res.get(i);
//               s = s + " " + String.format("%.2f", num);
//            }
//            Log.d("chao data ", s);
//         } else {
//            Log.d("chao data", "null");
//         }
//         glUnmapBuffer(GL_TRANSFORM_FEEDBACK_BUFFER);
//      }

      // 交换
      i0 = 1 - i0;
   }

   void initAll() {
      float f = 0.1f;
      for (int i = 0; i < buf.length; i++) {
         buf[i] = (float)r.nextInt(1000) / 1000;
         buf[i] = (buf[i] - 0.5f) * 2;

//         if (i % 2 == 1) {
//            buf[i] = buf[i - 1];
//         } else {
//            buf[i] = f;
//            f += 0.1f;
//         }
      }
   }

}