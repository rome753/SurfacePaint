package cc.rome753.surfacepaint.box2d;

import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;

import cc.rome753.surfacepaint.R;

public class GalaxyActivity extends Activity {


    private Galaxy galaxy;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((galaxy = new Galaxy(this)));
        galaxy.setGravity(0f);

        View view = new View(this);
        view.setBackgroundResource(R.drawable.soccer);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(100, 100);
        params.gravity = Gravity.CENTER;
        galaxy.addView(view, params);

        for (int i = 0; i < 5; i++) {
            View view1 = new View(this);
            view1.setBackgroundResource(R.drawable.soccer);
            FrameLayout.LayoutParams params1 = new FrameLayout.LayoutParams(50, 50);
            galaxy.addView(view1, params1);
        }

    }

}