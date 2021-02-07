package cc.rome753.opengles3;

import android.opengl.Matrix;
import android.renderscript.Matrix3f;
import android.widget.Space;

/**
 * c文件 @link{https://learnopengl.com/code_viewer_gh.php?code=includes/learnopengl/camera.h}
 */
public class OurCamera {

    // Defines several possible options for camera movement. Used as abstraction to stay away from window-system specific input methods
    enum Camera_Movement {
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
    static final float ZOOM = 45.0f;


    // An abstract camera class that processes input and calculates the corresponding Euler Angles, Vectors and Matrices for use in OpenGL
    // camera Attributes
    float[] Position;
    float[] Front;
    float[] Up;
    float[] Right;
    float[] WorldUp;
    // euler Angles
    float Yaw;
    float Pitch;
    // camera options
    float MovementSpeed;
    float MouseSensitivity;
    float Zoom;

    // constructor with vectors
    public OurCamera(){
        Position = new float[]{0.0f, 0.0f,0.0f};
        Up = new float[]{0.0f, 1.0f,0.0f};
        Front = new float[]{0.0f, 1.0f,-1.0f};

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
        setLookAtM(mat, Position, Front, Up);
    }

    // processes input received from any keyboard-like input system. Accepts input parameter in the form of camera defined ENUM (to abstract it from windowing systems)
    void ProcessKeyboard(Camera_Movement direction, float deltaTime) {
//        float velocity = MovementSpeed * deltaTime;
//        if (direction == Camera_Movement.FORWARD)
//            Position += Front * velocity;
//        if (direction == Camera_Movement.BACKWARD)
//            Position -= Front * velocity;
//        if (direction == Camera_Movement.LEFT)
//            Position -= Right * velocity;
//        if (direction == Camera_Movement.RIGHT)
//            Position += Right * velocity;
    }

    // processes input received from a mouse input system. Expects the offset value in both the x and y direction.
    void ProcessMouseMovement(float xoffset, float yoffset, Boolean constrainPitch) {
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

    // processes input received from a mouse scroll-wheel event. Only requires input on the vertical wheel-axis
    void ProcessMouseScroll(float yoffset) {
        Zoom -= (float) yoffset;
        if (Zoom < 1.0f)
            Zoom = 1.0f;
        if (Zoom > 45.0f)
            Zoom = 45.0f;
    }

    // calculates the front vector from the Camera's (updated) Euler Angles
    void updateCameraVectors() {
        // calculate the new Front vector
//        float[] front;
//        front.x = cos(glm::radians (Yaw)) *cos(glm::radians (Pitch));
//        front.y = sin(glm::radians (Pitch));
//        front.z = sin(glm::radians (Yaw)) *cos(glm::radians (Pitch));
//        Matrix.
//        Front = glm::normalize (front);
//        // also re-calculate the Right and Up vector
//        Right = glm::normalize (glm::cross (Front, WorldUp))
//        ;  // normalize the vectors, because their length gets closer to 0 the more you look up or down which results in slower movement.
//        Up = glm::normalize (glm::cross (Right, Front));
    }

    /** 简单的数组运算操作 **/

    public static void add(float[] a, float b) {
        for (int i = 0; i < a.length; i++) {
            a[i] *= b;
        }
    }

    public static void add(float[] a1, float[] a2) {
        for (int i = 0; i < a1.length; i++) {
            a1[i] += a2[i];
        }
    }

    public static void minus(float[] a1, float[] a2) {
        for (int i = 0; i < a1.length; i++) {
            a1[i] -= a2[i];
        }
    }

    // 参数转换
    public static void setLookAtM(float[] mat, float[] eye, float[] center, float[] up) {
        Matrix.setLookAtM(mat, 0, eye[0], eye[1], eye[2], center[0], center[1], center[2], up[0], up[1], up[2]);
    }

}
