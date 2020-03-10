package zmovie.com.dlan;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class JettyResourceService extends Service {
    private static final String TAG = JettyResourceService.class.getSimpleName();
    private Binder binder = new JettyResourceService.LocalBinder();
    private ExecutorService mThreadPool = Executors.newCachedThreadPool();
    private JettyFileResourceServer mJettyResourceServer;
    private static final int NOTIFICATION_ID = 10;
    private static final String CHANEL = "deamon";
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
            /*NotificationChannel channel = new NotificationChannel(CHANEL, CHANEL,
                    NotificationManager.IMPORTANCE_LOW);
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (manager == null)
                return;
            manager.createNotificationChannel(channel);

            Notification notification = new NotificationCompat.Builder(this, CHANEL).setAutoCancel(true).setCategory(
                    Notification.CATEGORY_SERVICE).setOngoing(true).setPriority(
                    NotificationManager.IMPORTANCE_LOW).build();
            startForeground(NOTIFICATION_ID, notification);*/
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            //如果 18 以上的设备 启动一个Service startForeground给相同的id
            //然后结束那个Service
            startForeground(NOTIFICATION_ID, new Notification());
            startService(new Intent(this, InnnerService.class));
        } else {
            startForeground(NOTIFICATION_ID, new Notification());
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
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
}
