package zmovie.com.dlan;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class JettyResourceService extends Service {
    private static final String TAG = JettyResourceService.class.getSimpleName();
    private Binder binder = new JettyResourceService.LocalBinder();
    private ExecutorService mThreadPool = Executors.newCachedThreadPool();
    private JettyFileResourceServer mJettyResourceServer;

    public JettyResourceService() {
    }

    public void onCreate() {
        super.onCreate();
        this.mJettyResourceServer = new JettyFileResourceServer();
        this.mThreadPool.execute(this.mJettyResourceServer);
    }

    public void onDestroy() {
        this.mJettyResourceServer.stopIfRunning();
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
}
