package zmovie.com.dlan;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;

import java.util.logging.Logger;

public class JettyFileResourceServer implements Runnable {
    private static final Logger log = Logger.getLogger(JettyFileResourceServer.class.getName());
    private Server mServer = new Server(55699);

    public JettyFileResourceServer() {
        this.mServer.setGracefulShutdown(1000);
    }

    public synchronized void startIfNotRunning() {
        if (!this.mServer.isStarted() && !this.mServer.isStarting()) {
            log.info("Starting JettyResourceServer");

            try {
                this.mServer.start();
            } catch (Exception var2) {
                log.severe("Couldn't start Jetty server: " + var2);
                throw new RuntimeException(var2);
            }
        }

    }

    public synchronized void stopIfRunning() {
        if (!this.mServer.isStopped() && !this.mServer.isStopping()) {
            log.info("Stopping JettyResourceServer");

            try {
                this.mServer.stop();
            } catch (Exception var2) {
                log.severe("Couldn't stop Jetty server: " + var2);
                throw new RuntimeException(var2);
            }
        }

    }

    public String getServerState() {
        return this.mServer.getState();
    }

    public void run() {
        ServletContextHandler context = new ServletContextHandler();
        context.setContextPath("/");
        context.setInitParameter("org.eclipse.jetty.servlet.Default.gzip", "false");
        this.mServer.setHandler(context);
        context.addServlet(MyFileResourceServlet.class, "/*");
        this.startIfNotRunning();
    }
}
