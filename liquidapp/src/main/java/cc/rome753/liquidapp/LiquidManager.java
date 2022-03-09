package cc.rome753.liquidapp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.FrameLayout;

import com.google.fpl.liquidfun.Body;
import com.google.fpl.liquidfun.BodyDef;
import com.google.fpl.liquidfun.BodyType;
import com.google.fpl.liquidfun.FixtureDef;
import com.google.fpl.liquidfun.ParticleFlag;
import com.google.fpl.liquidfun.ParticleGroup;
import com.google.fpl.liquidfun.ParticleGroupDef;
import com.google.fpl.liquidfun.ParticleSystem;
import com.google.fpl.liquidfun.ParticleSystemDef;
import com.google.fpl.liquidfun.PolygonShape;
import com.google.fpl.liquidfun.Vec2;
import com.google.fpl.liquidfun.World;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Random;

public class LiquidManager {

    // 超过2800个点会越界
    //      Caused by: java.lang.ArrayIndexOutOfBoundsException: Particle index is out of bounds. Check startIndex and numParticles.
    //        at com.google.fpl.liquidfun.liquidfunJNI.ParticleSystem_copyPositionBuffer(Native Method)
    //        at com.google.fpl.liquidfun.ParticleSystem.copyPositionBuffer(ParticleSystem.java:93)
    //        at cc.rome753.liquidapp.LiquidManager.copyPos(LiquidManager.java:65)
    public static int MAX_COUNT = 2800;
    private static final float GRAVITY = 10f;
    private World world;
    private ParticleSystem particleSystem;
    private Vec2 gravity = new Vec2(0, GRAVITY);
    private int ratio = 50;
    private float dt = 1f / 60f;
    private Random random = new Random();
//    private MouseJoint mouseJoint;
    private Body groundBody;


    ByteBuffer mParticlePositionBuffer;

    public LiquidManager() {
        mParticlePositionBuffer = ByteBuffer
                .allocateDirect(2 * 4 * MAX_COUNT)
                .order(ByteOrder.nativeOrder());

        createWorld();
        createLiquid();

        for (int i = 0; i < 250; i++) {
            step();
        }

        copyPos();

    }

    public void copyPos() {
        mParticlePositionBuffer.rewind();
        particleSystem.copyPositionBuffer(0, MAX_COUNT, mParticlePositionBuffer);
    }

    public void step() {
        world.step(dt, 1, 1, 1);
    }

    private void createLiquid() {
        ParticleSystemDef psd = new ParticleSystemDef();
        psd.setDensity(1.2f);
        psd.setGravityScale(0.4f);
        psd.setRadius(0.2f);
        psd.setRepulsiveStrength(0.5f);
        particleSystem = world.createParticleSystem(psd);
        particleSystem.setMaxParticleCount(MAX_COUNT);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(8f, 8f, 0f, 0f, 0f);

        ParticleGroupDef pd = new ParticleGroupDef();
        pd.setFlags(0);
        pd.setGroupFlags(0);
        pd.setLinearVelocity(new Vec2(0,0));
        pd.setShape(shape);

        // signal 11 (SIGSEGV), code 1 (SEGV_MAPERR), fault addr 0x25
        // Cause: null pointer dereference
        particleSystem.createParticleGroup(pd);

        psd.delete();
        shape.delete();
        pd.delete();

        Log.d("chao", "create particles " + particleSystem.getParticleCount());
    }

    public void setGravity(float x, float y) {
        gravity.set(x, y);
        if (world != null) {
            world.setGravity(x, y);
        }
    }

    private void createWorld() {
        float w = 20, h = 20;
        Log.d("chao", "createWorld " + w + "," + h);
        float wall = 1f;
        world = new World(gravity.getX(), gravity.getY());

        PolygonShape wallShape = new PolygonShape();
        wallShape.setAsBox(w, wall);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.setShape(wallShape);

        BodyDef bodyDef = new BodyDef();
        bodyDef.setType(BodyType.staticBody);

        bodyDef.setPosition(0, -h / 2 -wall);
        Body top = world.createBody(bodyDef);
        top.createFixture(fixtureDef);

        bodyDef.setPosition(0, h / 2 + wall);
        groundBody = world.createBody(bodyDef);
        groundBody.createFixture(fixtureDef);

        wallShape.setAsBox(wall, h);

        bodyDef.setPosition(-wall - w / 2, 0);
        Body left = world.createBody(bodyDef);
        left.createFixture(fixtureDef);

        bodyDef.setPosition(w / 2 + wall, 0);
        Body right = world.createBody(bodyDef);
        right.createFixture(fixtureDef);

        fixtureDef.delete();
        wallShape.delete();
        bodyDef.delete();
    }

    private float pixel2Meter(float pixel) {
        return pixel / ratio;
    }

    private float meter2Pixel(float meter) {
        return meter * ratio;
    }

    private float radiansToDegrees(float radians) {
        return radians / 3.14f * 180f;
    }

    private float degreesToRadians(float degrees) {
        return (degrees / 180f) * 3.14f;
    }

}
