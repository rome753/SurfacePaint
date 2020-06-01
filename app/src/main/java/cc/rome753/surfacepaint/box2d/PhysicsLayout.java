package cc.rome753.surfacepaint.box2d;

import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.collision.shapes.Shape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.joints.Joint;
import org.jbox2d.dynamics.joints.JointDef;
import org.jbox2d.dynamics.joints.JointType;
import org.jbox2d.dynamics.joints.MouseJoint;
import org.jbox2d.dynamics.joints.MouseJointDef;

import java.util.Random;

public class PhysicsLayout extends FrameLayout {

    private World world;
    private float gravity = 9.8f;
    private int ratio = 50;
    private float dt = 1f / 60f;
    private Random random = new Random();
    private MouseJoint mouseJoint;

    public PhysicsLayout(@NonNull Context context) {
        this(context, null);
    }

    public PhysicsLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PhysicsLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setWillNotDraw(false);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (world == null) {
            createWorld();
        }
        for (int i = 0; i < getChildCount(); i++) {
            View view = getChildAt(i);
            Body body = (Body) view.getTag();
            if (body == null) {
                createBody(view);
            }
        }

        world.step(dt, 10, 10);
        for (int i = 0; i < getChildCount(); i++) {
            View view = getChildAt(i);
            Body body = (Body) view.getTag();
            if (body != null) {
                updateView(view, body);
                view.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        float x = pixel2Meter(event.getX());
                        float y = pixel2Meter(event.getY());
                        switch (event.getAction()) {
                            case MotionEvent.ACTION_DOWN:

                                BodyDef def = new BodyDef();
                                def.position = new Vec2(x, y);
                                def.type = BodyType.STATIC;

                                MouseJointDef jointDef = new MouseJointDef();
                                jointDef.type = JointType.MOUSE;
                                jointDef.bodyA = world.createBody(def);
                                jointDef.bodyB = (Body) v.getTag();
                                mouseJoint = (MouseJoint) world.createJoint(jointDef);
//                                mouseJoint.setMaxForce(1000);

                                break;
                            case MotionEvent.ACTION_MOVE:
                                if (mouseJoint != null) {
                                    mouseJoint.setTarget(new Vec2(x, y));
                                }

                                break;
                        }
                        return true;
                    }
                });
            }
        }

        invalidate();
    }

    public void setGravity(float gravity) {
        this.gravity = gravity;
    }

    public void impulseRandom() {
        float x = random.nextInt(1000) - 1000;
        float y = random.nextInt(1000) - 1000;
        impulse(x, y);
    }

    public void impulse(float x, float y) {
        for (int i = 0; i < getChildCount(); i++) {
            Vec2 impulse = new Vec2(x, y);
            View view = getChildAt(i);
            Body body = (Body) view.getTag();
            if(body != null){
                body.applyLinearImpulse(impulse, body.getPosition());
            }
        }
    }

    public void moveBody(View view, float dx, float dy) {
        JointDef jointDef = new JointDef();
        Log.d("chao", "moveBody " + dx + "," + dy);
        Body body = (Body) view.getTag();
        if(body != null){
            Vec2 impulse = new Vec2(dx, dy);
            body.applyLinearImpulse(impulse, body.getPosition());
        }
    }

    private void updateView(View view, Body body) {
        Vec2 position = body.getPosition();
        float x = meter2Pixel(position.x) - view.getWidth() / 2;
        float y = meter2Pixel(position.y) - view.getHeight() / 2;
        view.setX(x);
        view.setY(y);
        view.setRotation(radiansToDegrees(body.getAngle() % 360));
//        Log.d("chao", "updateView " + x + "," + y);
    }

    private void createBody(View view) {
        float w = pixel2Meter(view.getWidth()), h = pixel2Meter(view.getHeight());
        float cx = pixel2Meter(view.getX()) + w;
        float cy = pixel2Meter(view.getY()) + h;
        Log.d("chao", "createBody " + w + "," + h);
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyType.DYNAMIC;
        bodyDef.position.set(cx, cy);

        Shape shape = new CircleShape();
        shape.setRadius(w / 2);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 0.5f;
        fixtureDef.friction = 0.5f;
        fixtureDef.restitution = 0.5f;

        Body body = world.createBody(bodyDef);
        body.createFixture(fixtureDef);
        body.setLinearVelocity(new Vec2(random.nextFloat(), random.nextFloat()));

        view.setTag(body);
    }

    private void createWorld() {
        float w = pixel2Meter(getWidth()), h = pixel2Meter(getHeight());
        Log.d("chao", "createWorld " + w + "," + h);
        float wall = 1f;
        world = new World(new Vec2(0, gravity));

        PolygonShape wallShape = new PolygonShape();
        wallShape.setAsBox(w, wall);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = wallShape;
        fixtureDef.density = 1f;
        fixtureDef.friction = 0.5f;
        fixtureDef.restitution = 0.5f;

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyType.STATIC;

        bodyDef.position.set(0, -wall);
        Body top = world.createBody(bodyDef);
        top.createFixture(fixtureDef);

        bodyDef.position.set(0, h + wall);
        Body bottom = world.createBody(bodyDef);
        bottom.createFixture(fixtureDef);

        wallShape.setAsBox(wall, h);

        bodyDef.position.set(-wall, 0);
        Body left = world.createBody(bodyDef);
        left.createFixture(fixtureDef);

        bodyDef.position.set(w + wall, 0);
        Body right = world.createBody(bodyDef);
        right.createFixture(fixtureDef);
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

    private float degreesToRadians(float degrees){
        return (degrees / 180f) * 3.14f;
    }

}
