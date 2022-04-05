/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.basicmediadecoder;


import android.animation.TimeAnimator;
import android.app.Activity;
import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.net.Uri;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.TextView;

import com.example.android.GLVideoRender;
import com.example.android.OurCamera;
import com.example.android.ScaleGestureDetector;
import com.example.android.common.media.MediaCodecWrapper;

import java.io.IOException;

/**
 * This activity uses a {@link android.view.TextureView} to render the frames of a video decoded using
 * {@link android.media.MediaCodec} API.
 */
public class MainActivity extends Activity {

    private TextureView mPlaybackView;
    private TimeAnimator mTimeAnimator = new TimeAnimator();

    // A utility that wraps up the underlying input and output buffer processing operations
    // into an east to use API.
    private MediaCodecWrapper mCodecWrapper;
    private MediaExtractor mExtractor = new MediaExtractor();
    TextView mAttribView = null;



    GLVideoRender glVideoRender = new GLVideoRender();
    OurCamera ourCamera;
    float fx, fy;

    ScaleGestureDetector scaleGestureDetector;


    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sample_main);
        mPlaybackView = (TextureView) findViewById(R.id.PlaybackView);
        mAttribView =  (TextView)findViewById(R.id.AttribView);


        GLSurfaceView glSurfaceView = findViewById(R.id.glSurfaceView);
        glSurfaceView.setEGLContextClientVersion(3);
        glSurfaceView.setRenderer(glVideoRender);
        glSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
        ourCamera = glVideoRender.getOurCamera();

        scaleGestureDetector = new ScaleGestureDetector(this, new ScaleGestureDetector.OnScaleGestureListener() {
            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                float factor = detector.getScaleFactor();
                ourCamera.ProcessMouseScroll((1 - factor) * 2000);
                Log.d("chao", "scaleGestureDetector.onScale " + factor);

                float dx = detector.getFocusX() - fx;
                float dy = detector.getFocusY() - fy;
                ourCamera.ProcessMouseMovement(-dx / 2, dy / 2, true);
                fx = detector.getFocusX();
                fy = detector.getFocusY();
                return true;
            }

            @Override
            public boolean onScaleBegin(ScaleGestureDetector detector) {
                Log.d("chao", "scaleGestureDetector.onScaleBegin");
                fx = detector.getFocusX();
                fy = detector.getFocusY();
                return true;
            }

            @Override
            public void onScaleEnd(ScaleGestureDetector detector) {
                Log.d("chao", "scaleGestureDetector.onScaleEnd");
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_menu, menu);
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mTimeAnimator != null && mTimeAnimator.isRunning()) {
            mTimeAnimator.end();
        }

        if (mCodecWrapper != null ) {
            mCodecWrapper.stopAndRelease();
            mExtractor.release();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_play) {
            mAttribView.setVisibility(View.VISIBLE);
            startPlayback();
            item.setEnabled(false);
        }
        return true;
    }


    public void startPlayback() {

        // Construct a URI that points to the video resource that we want to play
        Uri videoUri = Uri.parse("android.resource://"
                + getPackageName() + "/"
                + R.raw.vid_bigbuckbunny);

        try {

            // BEGIN_INCLUDE(initialize_extractor)
            mExtractor.setDataSource(this, videoUri, null);
            int nTracks = mExtractor.getTrackCount();

            // Begin by unselecting all of the tracks in the extractor, so we won't see
            // any tracks that we haven't explicitly selected.
            for (int i = 0; i < nTracks; ++i) {
                mExtractor.unselectTrack(i);
            }


            // Find the first video track in the stream. In a real-world application
            // it's possible that the stream would contain multiple tracks, but this
            // sample assumes that we just want to play the first one.
            for (int i = 0; i < nTracks; ++i) {
                // Try to create a video codec for this track. This call will return null if the
                // track is not a video track, or not a recognized video format. Once it returns
                // a valid MediaCodecWrapper, we can break out of the loop.
//                mCodecWrapper = MediaCodecWrapper.fromVideoFormat(mExtractor.getTrackFormat(i),
//                        new Surface(mPlaybackView.getSurfaceTexture()));
                mCodecWrapper = MediaCodecWrapper.fromVideoFormat(mExtractor.getTrackFormat(i),
                        new Surface(glVideoRender.getSurfaceTexture()));
                if (mCodecWrapper != null) {
                    mExtractor.selectTrack(i);
                    break;
                }
            }
            // END_INCLUDE(initialize_extractor)




            // By using a {@link TimeAnimator}, we can sync our media rendering commands with
            // the system display frame rendering. The animator ticks as the {@link Choreographer}
            // receives VSYNC events.
            mTimeAnimator.setTimeListener(new TimeAnimator.TimeListener() {
                @Override
                public void onTimeUpdate(final TimeAnimator animation,
                                         final long totalTime,
                                         final long deltaTime) {

                    boolean isEos = ((mExtractor.getSampleFlags() & MediaCodec
                            .BUFFER_FLAG_END_OF_STREAM) == MediaCodec.BUFFER_FLAG_END_OF_STREAM);

                    // BEGIN_INCLUDE(write_sample)
                    if (!isEos) {
                        // Try to submit the sample to the codec and if successful advance the
                        // extractor to the next available sample to read.
                        boolean result = mCodecWrapper.writeSample(mExtractor, false,
                                mExtractor.getSampleTime(), mExtractor.getSampleFlags());

                        if (result) {
                            // Advancing the extractor is a blocking operation and it MUST be
                            // executed outside the main thread in real applications.
                            mExtractor.advance();
                        }
                    }
                    // END_INCLUDE(write_sample)

                    // Examine the sample at the head of the queue to see if its ready to be
                    // rendered and is not zero sized End-of-Stream record.
                    MediaCodec.BufferInfo out_bufferInfo = new MediaCodec.BufferInfo();
                    mCodecWrapper.peekSample(out_bufferInfo);

                    // BEGIN_INCLUDE(render_sample)
                    if (out_bufferInfo.size <= 0 && isEos) {
                        mTimeAnimator.end();
                        mCodecWrapper.stopAndRelease();
                        mExtractor.release();
                    } else if (out_bufferInfo.presentationTimeUs / 1000 < totalTime) {
                        // Pop the sample off the queue and send it to {@link Surface}
                        mCodecWrapper.popSample(true);
                    }
                    // END_INCLUDE(render_sample)

                }
            });

            // We're all set. Kick off the animator to process buffers and render video frames as
            // they become available
            mTimeAnimator.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    float x, y;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (ourCamera == null) {
            return false;
        }
        // 多指缩放视图
        if (event.getPointerCount() > 1) {
            return scaleGestureDetector.onTouchEvent(event);
        }
        // 单指移动视角
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                x = event.getRawX();
                y = event.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                float dx = event.getRawX() - x;
                float dy = event.getRawY() - y;

                // 防止多指跳动
                if (Math.abs(dx) > 100 || Math.abs(dy) > 100) return true;

                    ourCamera.ProcessKeyboard(dx > 0 ? OurCamera.Camera_Movement.LEFT : OurCamera.Camera_Movement.RIGHT, Math.abs(dx) / 1000);
                    ourCamera.ProcessKeyboard(dy > 0 ? OurCamera.Camera_Movement.BACKWARD : OurCamera.Camera_Movement.FORWARD, Math.abs(dy) / 1000);

                x = event.getRawX();
                y = event.getRawY();


                glVideoRender.rotModel(dx, dy);

                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                break;
        }
        return true;
    }
}
