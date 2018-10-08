package com.pku.xuzhimin.vrplayer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import android.net.Uri;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Surface;
import android.graphics.SurfaceTexture;
import android.view.SurfaceView;
import android.opengl.GLSurfaceView;
import android.view.TextureView;

import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private PlayerView pv_view;
    private DataSource.Factory mediaDataSourceFactory;
    private SimpleExoPlayer mPlayer;
    private GLSurfaceView mGLView;

    private static final String TAG = "MainActivity";

    private static final DefaultBandwidthMeter BANDWIDTH_METER = new DefaultBandwidthMeter();
    private MediaSource dashMediaSource;
    private DefaultTrackSelector trackSelector;

    private SensorManager sensorManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        mediaDataSourceFactory = buildDataSourceFactory(true);
        initMediaPlayer();

        // Create a GLSurfaceView instance and set it
        // as the ContentView for this Activity.
        //mGLView = new MyGLSurfaceView(this);
        //setContentView(mGLView);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor magneticSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        Sensor accelerometerSensor = sensorManager.getDefaultSensor(
                Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(listener, magneticSensor,
                SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(listener, accelerometerSensor,
                SensorManager.SENSOR_DELAY_GAME);
    }

    private void initMediaPlayer() {

        //dash测试流
        Uri mUri = Uri.parse("https://dash.akamaized.net/envivio/EnvivioDash3/manifest.mpd");//"http://xuzhimin.cc/elevr/therelaxatron.webm");//"https://dash.akamaized.net/envivio/EnvivioDash3/manifest.mpd");//"http://www.xuzhimin.cc/data/dashcast_use.mpd");

        TrackSelection.Factory trackSelectionFactory=new AdaptiveTrackSelection.Factory(BANDWIDTH_METER);
        long bandwidth = BANDWIDTH_METER.getBitrateEstimate() / 1000;
        Log.w(TAG, "[Zhimin Xu]: Available bandwidth:" + bandwidth + "Kbps.");
        //int numOfTracks = trackSelectionFactory.adaptive


        DefaultTrackSelector.Parameters trackSelectorParameters = new DefaultTrackSelector.ParametersBuilder().build();
        trackSelector = new DefaultTrackSelector(trackSelectionFactory);
        trackSelector.setParameters(trackSelectorParameters);
        mPlayer= ExoPlayerFactory.newSimpleInstance(this, trackSelector);

        pv_view.setPlayer(mPlayer);

        //使用dash的解析库
        dashMediaSource = new DashMediaSource(mUri,mediaDataSourceFactory, new DefaultDashChunkSource.Factory(mediaDataSourceFactory),null,null);

        //使用hls解析库
        //HlsMediaSource hlsMediaSource = new HlsMediaSource(mUri3, mediaDataSourceFactory, null, null);

        //mPlayer.prepare(hlsMediaSource);
        mPlayer.prepare(dashMediaSource);
        mPlayer.setPlayWhenReady(true); //自动播放
        //Looper playbackLooper = mPlayer.getPlaybackLooper();

        //mPlayer.setVideoTextureView(new TextureView(this));
        /*
        int mOpenGLTextureId = 1;
        SurfaceTexture surfaceTexture = new SurfaceTexture(mOpenGLTextureId);
        Surface surface = new Surface(surfaceTexture);
        mPlayer.setVideoSurface(surface);
        */
        //mPlayer.setVideoSurfaceView(mGLView);

        //TextureView tv = new TextureView(this);
        //mPlayer.setVideoTextureView(tv);

        Log.d("MainActivity","[Zhimin xu]: RendererCount " + mPlayer.getRendererCount() + "\r\n");
        Log.d("MainActivity","[Zhimin xu]: RendererType " + mPlayer.getRendererType(0) + "\r\n");
        Log.d("MainActivity","[Zhimin xu]: RendererType " + mPlayer.getRendererType(1) + "\r\n");
        Log.d("MainActivity","[Zhimin xu]: RendererType " + mPlayer.getRendererType(2) + "\r\n");
        Log.d("MainActivity","[Zhimin xu]: RendererType " + mPlayer.getRendererType(3) + "\r\n");
    }

    private void initView() {
        pv_view=  findViewById(R.id.player_view);

    }
    private DataSource.Factory buildDataSourceFactory(boolean useBandwidthMeter) {

        return ((MyApplication) getApplication()).buildDataSourceFactory(useBandwidthMeter ? null : null);
    }
    @Override
    protected void onStop() {
        super.onStop();
        releasePlayer();
        Log.w(TAG, "[Zhimin Xu]: Stop!");
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        releasePlayer();
        if (sensorManager != null) {
            sensorManager.unregisterListener(listener);
        }
    }

    private void releasePlayer() {

        if(mPlayer!=null){
            mPlayer.release();
            mPlayer = null;
            dashMediaSource = null;
            trackSelector = null;
        }
    }

    @SuppressLint("NewApi")
    private void getSensorList() {
        // 获取传感器管理器
        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        // 获取全部传感器列表
        List<Sensor> sensors = sensorManager.getSensorList(Sensor.TYPE_ALL);
        // 打印每个传感器信息
        StringBuilder strLog = new StringBuilder();
        int iIndex = 1;
        for (Sensor item : sensors) {
            strLog.append(iIndex + ".");
            strLog.append("	Sensor Type - " + item.getType() + "\r\n");
            strLog.append("	Sensor Name - " + item.getName() + "\r\n");
            strLog.append("	Sensor Version - " + item.getVersion() + "\r\n");
            strLog.append("	Sensor Vendor - " + item.getVendor() + "\r\n");
            strLog.append("	Maximum Range - " + item.getMaximumRange() + "\r\n");
            strLog.append("	Minimum Delay - " + item.getMinDelay() + "\r\n");
            strLog.append("	Power - " + item.getPower() + "\r\n");
            strLog.append("	Resolution - " + item.getResolution() + "\r\n");
            strLog.append("\r\n");
            iIndex++;
        }
        System.out.println(strLog.toString());
        Log.w(TAG, "[Zhimin xu]:\r\n" + strLog.toString());
    }

    private void getOrientation() {
        // 获取传感器管理器
        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        // 获取加速度感器和地磁传感器列表
        Sensor accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        Sensor magneticSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        sensorManager.registerListener(listener, accelerometerSensor, SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(listener, magneticSensor, SensorManager.SENSOR_DELAY_GAME);
    }

    private SensorEventListener listener = new SensorEventListener() {
        float[] accelerometerValues = new float[3];
        float[] magneticValues = new float[3];
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            // 判断当前是加速度传感器还是地磁传感器
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                // 注意赋值时要调用clone()方法
                accelerometerValues = event.values.clone();
            } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
                // 注意赋值时要调用clone()方法
                magneticValues = event.values.clone();
            }
            float[] R = new float[9];
            float[] values = new float[3];
            SensorManager.getRotationMatrix(R, null, accelerometerValues, magneticValues);
            SensorManager.getOrientation(R, values);
            double roll = Math.toDegrees(values[0]);
            double pitch = Math.toDegrees(values[1]);
            double yaw = Math.toDegrees(values[2]);

            Log.d("MainActivity", "ROll: " + roll + " PITCH: " + pitch + " YAW: " + yaw);

            int face = 0;
            double base_roll = 90.00;
            double base_yaw = 0;
            double size = 45.00;

            if (roll <= base_roll + size && roll >= base_roll - size) {
                if (yaw <= base_yaw + size && yaw >= base_yaw - size) {
                    face = 2;
                } else if (yaw > base_yaw + size && yaw <= 180.00 - size) {
                    face = 1;
                } else if (yaw < base_yaw - size && yaw >= -180.00 - size) {
                    face = 0;
                } else {
                    face = 3;
                }
            } else if (roll > base_roll + size) {
                face = 4;
            } else {
                face = 5;
            }
            Log.d("MainActivity", "FACE: " + face);
        }
    };

}
