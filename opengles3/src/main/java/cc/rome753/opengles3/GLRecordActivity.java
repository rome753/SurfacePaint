package cc.rome753.opengles3;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.audiofx.Visualizer;
import android.opengl.GLSurfaceView;
import android.os.Bundle;

import cc.rome753.opengles3.shader.AudioRender;

public class GLRecordActivity extends GLActivity {
    //指定音频源 这个和MediaRecorder是相同的 MediaRecorder.AudioSource.MIC指的是麦克风
    private static final int mAudioSource = MediaRecorder.AudioSource.MIC;
    //指定采样率 （MediaRecoder 的采样率通常是8000Hz AAC的通常是44100Hz。 设置采样率为44100，目前为常用的采样率，官方文档表示这个值可以兼容所有的设置）
//    private static final int mSampleRateInHz=44100 ;
    private static final int mSampleRateInHz=4096;
    //指定捕获音频的声道数目。在AudioFormat类中指定用于此的常量
    private static final int mChannelConfig= AudioFormat.CHANNEL_CONFIGURATION_MONO; //单声道
    //指定音频量化位数 ,在AudioFormaat类中指定了以下各种可能的常量。通常我们选择ENCODING_PCM_16BIT和ENCODING_PCM_8BIT PCM代表的是脉冲编码调制，它实际上是原始音频样本。
    //因此可以设置每个样本的分辨率为16位或者8位，16位将占用更多的空间和处理能力,表示的音频也更加接近真实。
    private static final int mAudioFormat=AudioFormat.ENCODING_PCM_8BIT;
//    private static final int mAudioFormat=AudioFormat.ENCODING_PCM_16BIT;
    //指定缓冲区大小。调用AudioRecord类的getMinBufferSize方法可以获得。
    private int mBufferSizeInBytes= AudioRecord.getMinBufferSize(mSampleRateInHz,mChannelConfig, mAudioFormat);//计算最小缓冲区
    //创建AudioRecord。AudioRecord类实际上不会保存捕获的音频，因此需要手动创建文件并保存下载。
    private AudioRecord mAudioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,mSampleRateInHz,mChannelConfig,
            mAudioFormat, mBufferSizeInBytes);//创建AudioRecorder对象


    private Visualizer mVisualizer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // 根据数据长度设置绘制宽度
        AudioRender.w = mBufferSizeInBytes;

        super.onCreate(savedInstanceState);
        glSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);


        new Thread() {
            @Override
            public void run() {
                mAudioRecord.startRecording();

                initAudioTrack();

                setupVisualizerFxAndUI();
                // Make sure the visualizer is enabled only when you actually want to receive data, and
                // when it makes sense to receive data.
                mVisualizer.setEnabled(true);

                while(start && mAudioRecord.getRecordingState() == AudioRecord.RECORDSTATE_RECORDING) {
                    byte[] buffer = new byte[mBufferSizeInBytes];
                    int result = mAudioRecord.read(buffer, 0, mBufferSizeInBytes);
//                    mAudioTrack.write(buffer, 0, mBufferSizeInBytes);

                    AudioRender render = (AudioRender) GLRecordActivity.this.render;
                    render.update(buffer);
                    glSurfaceView.requestRender();
                }
            }
        }.start();
    }
    boolean start = true;

    @Override
    protected void onDestroy() {
        start = false;
        mVisualizer.setEnabled(false);
        mVisualizer.release();
        mAudioRecord.stop();
        mAudioRecord.release();
        super.onDestroy();
    }

    private AudioTrack mAudioTrack;
    private void initAudioTrack() {
        mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, mSampleRateInHz, mChannelConfig, mAudioFormat, mBufferSizeInBytes, AudioTrack.MODE_STREAM);
        mAudioTrack.play();
    }

    private void setupVisualizerFxAndUI() {
        // Create the Visualizer object and attach it to our media player.
        mVisualizer = new Visualizer(mAudioTrack.getAudioSessionId());
//        mVisualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);
        mVisualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[0]);
        mVisualizer.setDataCaptureListener(new Visualizer.OnDataCaptureListener() {
            public void onWaveFormDataCapture(Visualizer visualizer, byte[] bytes,
                                              int samplingRate) {
//                AudioRender render = (AudioRender) GLRecordActivity.this.render;
//                render.update(bytes);
//                glSurfaceView.requestRender();
            }

            public void onFftDataCapture(Visualizer visualizer, byte[] bytes, int samplingRate) {
//                AudioRender render = (AudioRender) GLRecordActivity.this.render;
//                render.update(bytes);
//                glSurfaceView.requestRender();
            }
        }, Visualizer.getMaxCaptureRate() / 2, true, true);
    }

}
