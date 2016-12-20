package cc.rome753.surfacepaint;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

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
        mData.add("SurfaceView paint");
        mData.add("TextureView paint");
        mData.add("Frames");

        mListView = (ListView) findViewById(R.id.lv);
        mListView.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mData));
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        startActivity(new Intent(MainActivity.this, SurfaceActivity.class));
                        break;
                    case 1:
                        startActivity(new Intent(MainActivity.this, TextureActivity.class));
                        break;
                    case 2:
                        startActivity(new Intent(MainActivity.this, FramesActivity.class));
                        break;
                }
            }
        });
    }
}
