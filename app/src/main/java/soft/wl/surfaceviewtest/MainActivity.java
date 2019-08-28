package soft.wl.surfaceviewtest;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private final String TAG = "SurfaceViewTest";
    private MediaPlayer mediaPlayer = null;

    private SurfaceView surfaceView = null;
    private Button play_button = null;
    private Button pause_button = null;

    private Boolean m_isManToPlay = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main);

        m_isManToPlay = false;

        surfaceView = (SurfaceView) findViewById(R.id.surface_view);

        if(null != surfaceView) {
            surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
                @Override
                public void surfaceCreated(SurfaceHolder surfaceHolder) {
                    Log.d(TAG, "surfaceCreated: setDisplay");
                    mediaPlayer.setDisplay(surfaceHolder);
                    if(m_isManToPlay) {
                        m_isManToPlay = false;
                        mediaPlayer.start();
                    }
                }

                @Override
                public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
                    Log.d(TAG, "surfaceChanged: ");
                }

                @Override
                public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                    Log.d(TAG, "surfaceDestroyed: ");
                }
            });
        }


        play_button = (Button) findViewById(R.id.video_play);
        if(null != play_button) {
            play_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d(TAG, "onClick: ");
                    if(!mediaPlayer.isPlaying()) {
                        mediaPlayer.start();
                    }
                }
            });
        }

        pause_button = (Button) findViewById(R.id.video_pause);
        if(null != pause_button) {
            pause_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mediaPlayer.isPlaying()) {
                        mediaPlayer.pause();
                    }
                }
            });
        }

        mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.video_test);//构造时，传入视频源
//        mediaPlayer = new MediaPlayer();
//        try {
//            mediaPlayer.setDataSource(getAssets().openFd("audio_test.mp3"));
//            mediaPlayer.prepareAsync();//构造时，不传视频源，需要prepare
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                Log.d(TAG, "onPrepared: ");
                mediaPlayer.seekTo(1);//处理onPrepared时，surface区域黑屏
            }
        });

        mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                Log.d(TAG, "onError: i : " + i + " i1 : " + i1);
                return true;
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart: ");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: ");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: ");
        if(mediaPlayer.isPlaying()) {
            m_isManToPlay = true;
            mediaPlayer.pause();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: ");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(null != mediaPlayer) {
            mediaPlayer.release();
            mediaPlayer = null;
        }

        m_isManToPlay = false;
    }
}
