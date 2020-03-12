package com.cyuan.bimibimi.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.cyuan.bimibimi.R;
import com.cyuan.bimibimi.ui.home.MainActivity;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import zmovie.com.dlan.JettyFileResourceServer;

public class JettyResourceService extends Service {
    private static final String TAG = JettyResourceService.class.getSimpleName();
    private Binder binder = new JettyResourceService.LocalBinder();
    private ExecutorService mThreadPool = Executors.newCachedThreadPool();
    private JettyFileResourceServer mJettyResourceServer;
    private static final int NOTIFICATION_ID = 10;
    private static final String CHANEL = "daemon";
    private PowerManager.WakeLock mJettyResourceServerWakeLock;

    public JettyResourceService() {
    }

    @SuppressLint("InvalidWakeLockTag")
    public void onCreate() {
        super.onCreate();
//        PowerManager powerManager = (PowerManager) getApplicationContext().getSystemService(Context.POWER_SERVICE);
//        mJettyResourceServerWakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "handleJettyResource");
        this.mJettyResourceServer = new JettyFileResourceServer();
//        mJettyResourceServerWakeLock.acquire();
        this.mThreadPool.execute(this.mJettyResourceServer);

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
//            NotificationChannel channel = new NotificationChannel(CHANEL, CHANEL,
//                    NotificationManager.IMPORTANCE_LOW);
//            NotificationManager manager = (NotificationManager) getApplication().getSystemService(Context.NOTIFICATION_SERVICE);
//            if (manager == null)
//                return;
//            manager.createNotificationChannel(channel);
//            Notification notification = getNotification();
//            startForeground(NOTIFICATION_ID, notification);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            //如果 18 以上的设备 启动一个Service startForeground给相同的id
            Notification notification = getNotification();
            //然后结束那个Service
            startForeground(NOTIFICATION_ID, notification);
            startService(new Intent(this, InnnerService.class));
        } else {
            startForeground(NOTIFICATION_ID, new Notification());
        }
    }

    @NotNull
    private Notification getNotification() {
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANEL)
                .setContentTitle("远程文件访问正在运行")
                .setContentText("本服务用于将保证本地视频投屏到电视正常运行")
                .setSmallIcon(R.mipmap.ic_launch_little_tv)
                .setTicker("服务正在后台运行...")
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setWhen(System.currentTimeMillis())
                .setDefaults(NotificationCompat.DEFAULT_LIGHTS)
                .setOngoing(true)
                .setContentIntent(pendingIntent);
        Notification notification = builder.build();
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        return notification;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        bindService(new Intent(this, DaemonService.class), new MyConnection(), BIND_AUTO_CREATE);
        this.mJettyResourceServer.startIfNotRunning();
        return super.onStartCommand(intent, flags, startId);
    }

    public void onDestroy() {
        this.mJettyResourceServer.stopIfRunning();
//        mJettyResourceServerWakeLock.release();
        super.onDestroy();
    }

    public IBinder onBind(Intent intent) {
        return this.binder;
    }


    public class LocalBinder extends Binder {
        LocalBinder() {
        }

        public JettyResourceService getService() {
            return JettyResourceService.this;
        }
    }

    public static class InnnerService extends Service {

        @Override
        public void onCreate() {
            super.onCreate();
            startForeground(NOTIFICATION_ID, new Notification());
            stopSelf();
        }

        @Nullable
        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }
    }

    class MyConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            bindService(new Intent(JettyResourceService.this, DaemonService.class), new MyConnection(), BIND_AUTO_CREATE);
        }

        @Override
        public void onBindingDied(ComponentName name) {

        }
    }
}
