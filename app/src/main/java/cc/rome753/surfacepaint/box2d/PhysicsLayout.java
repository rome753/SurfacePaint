package cc.rome753.surfacepaint.box2d;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.jbox2d.callbacks.QueryCallback;
import org.jbox2d.collision.AABB;
import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.collision.shapes.Shape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.joints.JointType;
import org.jbox2d.dynamics.joints.MouseJoint;
import org.jbox2d.dynamics.joints.MouseJointDef;

import java.util.Random;

public class PhysicsLayout extends FrameLayout {

    private static final float GRAVITY = 10f;
    private World world;
    private Vec2 gravity = new Vec2(0, GRAVITY);
    private int ratio = 50;
    private float dt = 1f / 60f;
    private Random random = new Random();
    private MouseJoint mouseJoint;
    private Body groundBody;

    private Vec2 hitPoint = new Vec2();

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
            }
        }

        invalidate();
    }

    QueryCallback queryCallback = new QueryCallback() {
        @Override
        public boolean reportFixture(Fixture fixture) {
            if (fixture.testPoint(hitPoint)) {
                Body hitBody = fixture.m_body;

                MouseJointDef jointDef = new MouseJointDef();
                jointDef.type = JointType.MOUSE;
                jointDef.bodyA = groundBody;
                jointDef.bodyB = hitBody;
                jointDef.collideConnected = true;
                jointDef.maxForce = 10000f;
                jointDef.target.set(hitPoint.x, hitPoint.y);
                mouseJoint = (MouseJoint) world.createJoint(jointDef);
                hitBody.setAwake(true);
                return false;
            }
            return true;
        }
    };

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final float x = pixel2Meter(event.getX());
        final float y = pixel2Meter(event.getY());
        hitPoint.set(x, y);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                world.queryAABB(queryCallback, new AABB(new Vec2(x - 0.1f, y - 0.1f), new Vec2(x + 0.1f, y + 0.1f)));
                break;
            case MotionEvent.ACTION_MOVE:
                if (mouseJoint != null) {
                    mouseJoint.setTarget(hitPoint);
                }
                break;
            case MotionEvent.ACTION_UP:
                if (mouseJoint != null) {
                    world.destroyJoint(mouseJoint);
                    mouseJoint = null;
                }
                break;
        }
        return true;
    }

    public void setGravity(float x, float y) {
        gravity.x = x;
        gravity.y = y;
        if (world != null) {
            world.setGravity(gravity);
        }
    }

    public void setForce(float x, float y) {
        for (int i = 0; i < getChildCount(); i++) {
            Vec2 impulse = new Vec2(x, y);
            View view = getChildAt(i);
            Body body = (Body) view.getTag();
            if (body != null) {
                body.applyForce(impulse, body.getPosition());
//                body.applyLinearImpulse(impulse, body.getPosition());
            }
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
        float cx = pixel2Meter(view.getX()) + w / 2;
        float cy = pixel2Meter(view.getY()) + h / 2;
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
        world = new World(gravity);

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
        groundBody = world.createBody(bodyDef);
        groundBody.createFixture(fixtureDef);

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

    private float degreesToRadians(float degrees) {
        return (degrees / 180f) * 3.14f;
    }

}
