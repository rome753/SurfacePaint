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

import java.util.Random;

public class LiquidManager {

    private static final float GRAVITY = 10f;
    private World world;
    private ParticleSystem particleSystem;
    private Vec2 gravity = new Vec2(0, GRAVITY);
    private int ratio = 50;
    private float dt = 1f / 60f;
    private Random random = new Random();
//    private MouseJoint mouseJoint;
    private Body groundBody;

    private Vec2 hitPoint = new Vec2();
    private Paint paint = new Paint();

    protected void onDraw(Canvas canvas) {
        if (world == null) {
            createWorld(0, 0);
            createLiquid();
        }

        long time = System.currentTimeMillis();
        world.step(dt, 1, 1, 1);
        long time1 = System.currentTimeMillis();
        Log.d("chao", "step time " + (time1 - time));

        // draw particles
        for (int i = 0; i < particleSystem.getParticleCount(); i++) {
            float x = meter2Pixel(particleSystem.getParticlePositionX(i));
            float y = meter2Pixel(particleSystem.getParticlePositionY(i));
            canvas.drawCircle(x, y, 15, paint);
        }

        long time2 = System.currentTimeMillis();
        Log.d("chao", "draw time " + (time2 - time1));

    }


    private void createLiquid() {
        ParticleSystemDef psd = new ParticleSystemDef();
        psd.setDensity(1.2f);
        psd.setGravityScale(0.4f);
        psd.setRadius(0.5f);
        psd.setRepulsiveStrength(0.5f);
        particleSystem = world.createParticleSystem(psd);
        particleSystem.setMaxParticleCount(200);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(8f, 8f, 10f, 10f, 0f);

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

    private void createWorld(float wPx, float hPx) {
        float w = pixel2Meter(wPx), h = pixel2Meter(hPx);
        Log.d("chao", "createWorld " + w + "," + h);
        float wall = 1f;
        world = new World(gravity.getX(), gravity.getY());

        PolygonShape wallShape = new PolygonShape();
        wallShape.setAsBox(w, wall);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.setShape(wallShape);

        BodyDef bodyDef = new BodyDef();
        bodyDef.setType(BodyType.staticBody);

        bodyDef.setPosition(0, -wall);
        Body top = world.createBody(bodyDef);
        top.createFixture(fixtureDef);

        bodyDef.setPosition(0, h + wall);
        groundBody = world.createBody(bodyDef);
        groundBody.createFixture(fixtureDef);

        wallShape.setAsBox(wall, h);

        bodyDef.setPosition(-wall, 0);
        Body left = world.createBody(bodyDef);
        left.createFixture(fixtureDef);

        bodyDef.setPosition(w + wall, 0);
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
