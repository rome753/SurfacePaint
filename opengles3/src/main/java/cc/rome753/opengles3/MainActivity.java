package cc.rome753.opengles3;

import android.content.Intent;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import cc.rome753.opengles3.shader.FractorRender;
import cc.rome753.opengles3.shader.LightingRender;
import cc.rome753.opengles3.shader.OurCamera;
import cc.rome753.opengles3.shader.ParticleSystemRenderer;
import cc.rome753.opengles3.shader.Simple3DRender;

public class MainActivity extends AppCompatActivity {
    ListView mListView;

    List<String> mData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mData = new ArrayList<>();
        mData.add("Simple");
        mData.add("Simple3D");
        mData.add("Fractal");
        mData.add("Lighting");
        mData.add("ParticleSystem");
        mListView = (ListView) findViewById(R.id.lv);
        mListView.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mData));
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(MainActivity.this, GLActivity.class);
                i.putExtra("render", mData.get(position));
                startActivity(i);
            }
        });
    }

}