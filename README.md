# SurfaceView和MediaPlayer学习
&ensp;&ensp;&ensp;&ensp;该工程采用SurfaceView和MediaPlayer控制本地视频的播放，通过该工程，可以学会SurfaceView和MediaPlayer的基本用法，如:  
1. SurfaceView常用函数有哪些，与MediaPlayer关联起来的方法
2. MediaPlayer常用函数有哪些，与SurfaceView关联起来的方法
3. SurfaceView使用时有哪些注意点
# SurfaceView核心代码
### 1. SurfaceView获取
```java
surfaceView = (SurfaceView) findViewById(R.id.surface_view);
```
### 2. SurfaceView实现CallBack
```java
surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        Log.d(TAG, "surfaceCreated: setDisplay");
        mediaPlayer.setDisplay(surfaceHolder);
        if(m_isManToPlay) {
            m_isManToPlay = false;
            mediaPlayer.start();
        } else {
            if(current_position != 0) {
                mediaPlayer.seekTo(current_position);
            }
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
```
# MediaPlayer核心代码
### 1. 创建MediaPlayer实例
1. 创建时设置播放源
```java
mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.video_test);
```
2. 只创建实例，随后设置播放源
```java
mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.video_test);//构造时，传入视频源
mediaPlayer = new MediaPlayer();
try {
    mediaPlayer.setDataSource(getAssets().openFd("audio_test.mp3"));
    mediaPlayer.prepareAsync();//构造时，不传视频源，需要prepare
} catch (IOException e) {
    e.printStackTrace();
}
```
### 2. 实现OnPreparedListener
```java
mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        Log.d(TAG, "onPrepared: ");
        mediaPlayer.seekTo(1);//处理onPrepared时，surface区域黑屏
    }
});
```
### 3. 开始播放视频
```java
mediaPlayer.start();
```
### 4. 暂停播放视频
```java
mediaPlayer.pause();
```
### 5. 快进到某一位置
```java
mediaPlayer.seekTo(1);
```
### 6. 释放MediaPlayer资源，画面退出时调用
```java
mediaPlayer.release();
```
# 常见问题处理
### 1. 视频播放时，SurfaceView区域黑屏
&ensp;&ensp;&ensp;&ensp;MediaPlayer设置SurfaceHolder时，该SurfaceHolder未创建，需要在surfaceCreated回调之后，MediaPlayer才可以与SurfaceView关联
### 2. 视频播放时，SurfaceView正常显示，视频暂停后画面切入后台，再切回前台，SurfaceView区域黑屏
&ensp;&ensp;&ensp;&ensp;画面切入后台，SurfaceView销毁（surfaceDestroyed），移入前台时，SurfaceView新建（surfaceCreated），SurfaceView新建时会先将背景绘制成黑色，由于MediaPlayer一直处于暂停状态，没有状态更新，SurfaceView背景保持黑色，解决方法：画面切入后台时，记录当前播放进度，画面再次切入前台时，SurfaceView新建后，MediaPlayer调用seekTo至记录的进度
### 3. 视频播放时，SurfaceView正常显示，画面切入后台，视频有声音，再切回前台，播放进度无法保存
&ensp;&ensp;&ensp;&ensp;画面切入后台时，SurfaceView销毁，此时mediaPlayer仍处于播放状态，再切回前台，视频继续播放，解决方法：画面切入后台时，记录当前的播放时间并且暂停播放，画面切回前台后，当SurfaceView新建后，调用MediaPlayer的start接口继续播放
# 小结


