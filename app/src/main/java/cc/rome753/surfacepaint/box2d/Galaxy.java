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

public class Galaxy extends FrameLayout {

    private World world;
    private float gravity = 9.8f;
    private int ratio = 50;
    private float dt = 1f / 60f;
    private Random random = new Random();
    private MouseJoint mouseJoint;
    private Body star;

    private Vec2 hitPoint = new Vec2();

    public Galaxy(@NonNull Context context) {
        this(context, null);
    }

    public Galaxy(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Galaxy(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
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
                if (view.getWidth() > 50) {
                    createStar(view);
                } else {
                    createBody(view);
                }
            }
        }

        setForce();
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
                jointDef.bodyA = star;
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

    public void setGravity(float gravity) {
        this.gravity = gravity;
    }

    Vec2 force = new Vec2();

    public void setForce() {
        for (int i = 0; i < getChildCount(); i++) {
            View view = getChildAt(i);
            Body body = (Body) view.getTag();
            if (body != null && body.getType() == BodyType.DYNAMIC) {
                Vec2 pos0 = body.getPosition();
                Vec2 pos1 = star.getPosition();
                float fx = pos1.x - pos0.x;
                float fy = pos1.y - pos0.y;
                force.set(fx, fy);
                body.applyForce(force, body.getPosition());
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

    private void createStar(View view) {
        float w = pixel2Meter(view.getWidth()), h = pixel2Meter(view.getHeight());
        float cx = pixel2Meter(view.getX()) + w;
        float cy = pixel2Meter(view.getY()) + h;
        Log.d("chao", "createStar " + w + "," + h);
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyType.STATIC;
        bodyDef.position.set(cx, cy);

        Shape shape = new CircleShape();
        shape.setRadius(w / 2);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 0.5f;
        fixtureDef.friction = 0.5f;
        fixtureDef.restitution = 0.5f;

        star = world.createBody(bodyDef);
        star.createFixture(fixtureDef);

        view.setTag(star);
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
