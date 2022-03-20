package cc.rome753.opengles3;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

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
        mData.add("Audio");
        mData.add("Record");
        mData.add("Lighting");
        mData.add("Group");
        mData.add("Group3D");
        mData.add("ParticleSystem");
        mListView = (ListView) findViewById(R.id.lv);
        mListView.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mData));
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Class clazz = GLActivity.class;
                if (mData.get(position).equals("Audio")) {
                    clazz = GLAudioActivity.class;
                } else if (mData.get(position).equals("Record")) {
                    clazz = GLRecordActivity.class;
                }
                Intent i = new Intent(MainActivity.this, clazz);
                i.putExtra("render", mData.get(position));
                startActivity(i);
            }
        });



        Intent i = new Intent(MainActivity.this, GLActivity.class);
        i.putExtra("render", "Fractal");
        startActivity(i);
    }

}