package cc.rome753.opengles3;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.audiofx.Equalizer;
import android.media.audiofx.Visualizer;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import cc.rome753.opengles3.shader.AudioRender;

public class GLAudioActivity extends GLActivity {

    private MediaPlayer mMediaPlayer;
    private Visualizer mVisualizer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // 根据数据长度设置绘制宽度
        AudioRender.w = Visualizer.getCaptureSizeRange()[0];

        super.onCreate(savedInstanceState);
        glSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        // Create the MediaPlayer
        mMediaPlayer = MediaPlayer.create(this, R.raw.jay);

//        setupVisualizerFxAndUI();
//        // Make sure the visualizer is enabled only when you actually want to receive data, and
//        // when it makes sense to receive data.
//        mVisualizer.setEnabled(true);

        // When the stream ends, we don't need to collect any more data. We don't do this in
        // setupVisualizerFxAndUI because we likely want to have more, non-Visualizer related code
        // in this callback.
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mediaPlayer) {
                mVisualizer.setEnabled(false);
            }
        });

        mMediaPlayer.start();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            setupVisualizerFxAndUI();
            mVisualizer.setEnabled(true);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, 0);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 0) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                setupVisualizerFxAndUI();
                mVisualizer.setEnabled(true);
            }
        }
    }

    private void setupVisualizerFxAndUI() {
        // Create the Visualizer object and attach it to our media player.
        mVisualizer = new Visualizer(mMediaPlayer.getAudioSessionId());
//        mVisualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);
        mVisualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[0]);
        mVisualizer.setDataCaptureListener(new Visualizer.OnDataCaptureListener() {
            public void onWaveFormDataCapture(Visualizer visualizer, byte[] bytes,
                                              int samplingRate) {
                AudioRender render = (AudioRender) GLAudioActivity.this.render;
                render.update(bytes);
                glSurfaceView.requestRender();
            }

            public void onFftDataCapture(Visualizer visualizer, byte[] bytes, int samplingRate) {
//                AudioRender render = (AudioRender) GLAudioActivity.this.render;
//                render.update(bytes);
//                glSurfaceView.requestRender();
            }
        }, Visualizer.getMaxCaptureRate() / 2, true, true);
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (isFinishing() && mMediaPlayer != null) {
            mVisualizer.release();
//            mEqualizer.release();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }
}
