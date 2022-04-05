package com.example.android;

import android.opengl.Matrix;

/**
 * c版本 @link{https://learnopengl.com/code_viewer_gh.php?code=includes/learnopengl/camera.h}
 * glm 矩阵算法 @link{https://glm.g-truc.net/0.9.8/index.html}
 */
public class OurCamera {

    // Defines several possible options for camera movement. Used as abstraction to stay away from window-system specific input methods
    public enum Camera_Movement {
        FORWARD,
        BACKWARD,
        LEFT,
        RIGHT
    }

    // Default camera values
    static final float YAW = -90.0f;
    static final float PITCH = 0.0f;
    static final float SPEED = 2.5f;
    static final float SENSITIVITY = 0.1f;
//    static final float ZOOM = 45.0f;
    static final float ZOOM = 2000.0f;


    // An abstract camera class that processes input and calculates the corresponding Euler Angles, Vectors and Matrices for use in OpenGL
    // camera Attributes
    float[] Position;
    float[] Front;
    float[] Up;
    float[] Right = new float[3];
    float[] WorldUp;
    // euler Angles
    float Yaw;
    float Pitch;
    // camera options
    float MovementSpeed;
    float MouseSensitivity;
    float Zoom;

    // constructor with vectors
    public OurCamera(float[] position){
        Position = position;
        Up = new float[]{0.0f, 1.0f,0.0f};
        Front = new float[]{0.0f, 1.0f,-1.0f};

        this.WorldUp = Up;
        this.Yaw = YAW;
        this.Pitch = PITCH;
        this.MovementSpeed = SPEED;
        this.MouseSensitivity = SENSITIVITY;
        this.Zoom = ZOOM;
        updateCameraVectors();
    }

    // constructor with scalar values
    public OurCamera(float posX, float posY, float posZ, float upX, float upY, float upZ, float yaw, float pitch) {
        Front = new float[]{0.0f, 0.0f, -1.0f};
        this.MovementSpeed = SPEED;
        this.MouseSensitivity = SENSITIVITY;
        this.Zoom = ZOOM;
        Position = new float[]{posX, posY, posZ};
        WorldUp = new float[]{upX, upY, upZ};
        Yaw = yaw;
        Pitch = pitch;
        updateCameraVectors();
    }

    void GetViewMatrix(float[] mat) {
        setLookAtM(mat, Position, add(Position, Front), Up);
    }

    // 按方向键：移动视角的位置，左右和拉近拉远
    // processes input received from any keyboard-like input system. Accepts input parameter in the form of camera defined ENUM (to abstract it from windowing systems)
    public void ProcessKeyboard(Camera_Movement direction, float deltaTime) {
        float velocity = MovementSpeed * deltaTime;
        if (direction == Camera_Movement.FORWARD)
            calcPosition(Position, Front, velocity);
        if (direction == Camera_Movement.BACKWARD)
            calcPosition(Position, Front, -velocity);
        if (direction == Camera_Movement.LEFT)
            calcPosition(Position, Right, -velocity);
        if (direction == Camera_Movement.RIGHT)
            calcPosition(Position, Right, velocity);
    }

    // 鼠标移动：旋转视角的方向
    // processes input received from a mouse input system. Expects the offset value in both the x and y direction.
    public void ProcessMouseMovement(float xoffset, float yoffset, Boolean constrainPitch) {
        xoffset *= MouseSensitivity;
        yoffset *= MouseSensitivity;

        Yaw += xoffset;
        Pitch += yoffset;

        // make sure that when pitch is out of bounds, screen doesn't get flipped
        if (constrainPitch == null) constrainPitch = true;
        if (constrainPitch) {
            if (Pitch > 89.0f)
                Pitch = 89.0f;
            if (Pitch < -89.0f)
                Pitch = -89.0f;
        }

        // update Front, Right and Up Vectors using the updated Euler angles
        updateCameraVectors();
    }

    // 鼠标滚轮：单纯放大缩小图案，视角没有变化
    // processes input received from a mouse scroll-wheel event. Only requires input on the vertical wheel-axis
    public void ProcessMouseScroll(float yoffset) {
        Zoom += yoffset;
//        if (Zoom < 1.0f)
//            Zoom = 1.0f;
//        if (Zoom > 45.0f)
//            Zoom = 45.0f;
    }

    // calculates the front vector from the Camera's (updated) Euler Angles
    void updateCameraVectors() {
        // calculate the new Front vector
        Front[0] = (float) (Math.cos(radians(Yaw)) * Math.cos(radians(Pitch)));
        Front[1] = (float) Math.sin(radians(Pitch));
        Front[2] = (float) (Math.sin(radians(Yaw)) * Math.cos(radians(Pitch)));

        // Front = glm::normalize (front);
        normalize(Front);
        // also re-calculate the Right and Up vector
        // Right = glm::normalize (glm::cross (Front, WorldUp))
        cross(Right, Front, WorldUp);
        normalize(Right);
        // normalize the vectors, because their length gets closer to 0 the more you look up or down which results in slower movement.
        // Up = glm::normalize (glm::cross (Right, Front));
        cross(Up, Right, Front);
        normalize(Up);
    }

    // 参数转换
    public static void setLookAtM(float[] mat, float[] eye, float[] center, float[] up) {
        Matrix.setLookAtM(mat, 0, eye[0], eye[1], eye[2], center[0], center[1], center[2], up[0], up[1], up[2]);
    }

    // 角度转弧度
    public static float radians(float degrees) {
        return (float) (Math.PI / 180 * degrees);
    }

    // 简单向量计算
    public static void calcPosition(float[] p, float[] a, float v) {
        p[0] += a[0] * v;
        p[1] += a[1] * v;
        p[2] += a[2] * v;
    }

    public static float[] add(float[] a, float[] b) {
        float[] f = new float[3];
        f[0] = a[0] + b[0];
        f[1] = a[1] + b[1];
        f[2] = a[2] + b[2];
        return f;
    }

    public static void cross(float[] f, float[] a, float[] b) {
        f[0] = a[1] * b[2] - b[1] * a[2];
        f[1] = a[2] * b[0] - b[2] * a[0];
        f[2] = a[0] * b[1] - b[0] * a[1];
    }

    public static void normalize(float[] v) {
        float length_of_v = (float) Math.sqrt((v[0] * v[0]) + (v[1] * v[1]) + (v[2] * v[2]));
        v[0] /= length_of_v;
        v[1] /= length_of_v;
        v[2] /= length_of_v;
    }

}
