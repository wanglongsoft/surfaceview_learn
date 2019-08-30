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
        mediaPlayer.setDisplay(surfaceHolder);//MediaPlayer与SurfaceView关联
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
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {//SurfaceView的Surface是画面不在前台时调用
        Log.d(TAG, "surfaceDestroyed: ");                      //TextureView的Surface是画面销毁时调用
    }
});
```
# MediaPlayer核心代码
### 1. 创建MediaPlayer实例
1. 创建时设置播放源，创建完成后，MediaPlayer处于***Prepared***状态
```java
mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.video_test);
```
2. 只创建实例，随后设置播放源
```java
mediaPlayer = new MediaPlayer();//MediaPlayer处于Idle状态
try {
    mediaPlayer.setDataSource(getAssets().openFd("audio_test.mp3"));//MediaPlayer处于Initialized状态
    mediaPlayer.prepareAsync();//MediaPlayer处于Preparing状态
} catch (IOException e) {
    e.printStackTrace();
}
```
### 2. 实现OnPreparedListener
```java
mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {//MediaPlayer处于Prepared状态
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
# 知识点
### MediaPlayer工作流程
   1.  首先创建MediaPlaer对象new MediaPlayer()或者MediaPlayer.create(this, R.raw.test)指定播放文件
   2.  然后调用setDataSource()方法来设置音视频频文件的路径(如果调用MediaPlayer.create，则该步骤不需要)
   3.  再调用prepare()方法使MediaPlayer进入到准备状态(如果调用MediaPlayer.create，则该步骤不需要)
   4.  调用start方法就可以播放音频  
### MediaPlayer状态机
<img src="https://developer.android.google.cn/images/mediaplayer_state_diagram.gif"  height="813" width="665">

### SurfaceView工作原理
&ensp;&ensp;&ensp;&ensp;SurfaceView创建一个置于应用窗口之后的新窗口，好像在视图层次（View Hierarchy）上穿了个"洞"，让绘图层（Surface）直接显示出来，SurfaceView窗口刷新时不需要重绘应用程序的窗口，所以这种方式的效率非常高。但是SurfaceView也有一些非常不便的限制，因SurfaceView的内容不在应用窗口上，所以不能使用平移、缩放、旋转等变换操作，也难以放在ListView或者ScrollView中，同样不能使用UI控件的一些特性，比如View.setAlpha()
### SurfaceView的双缓冲机制
&ensp;&ensp;&ensp;&ensp;SurfaceView在更新视图时用了两张Canvas，一张frontCanvas和一张backCanvas，每次显示的是frontCanvas，backCanvas存储的是上一次更改前的视图，当使用lockCanvas（）获取画布时，得到的是backCanvas而不是正在显示的frontCanvas，之后在获取到的backCanvas上绘制新视图，再unlockCanvasAndPost（canvas）此视图，那么上传的这张canvas将替换原来的frontCanvas作为新的frontCanvas，原来的frontCanvas将切换到后台作为backCanvas。例如，如果你已经先后两次绘制了视图A和B，那么你再调用lockCanvas（）获取视图，获得的将是A而不是正在显示的B，之后你讲重绘的C视图上传，那么C将取代B作为新的frontCanvas显示在SurfaceView上，原来的B则转换为backCanvas。

