package zmovie.com.dlan;

import android.util.Log;

import com.yanbo.lib_screen.service.upnp.VideoResourceServlet;

import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.util.resource.FileResource;
import org.eclipse.jetty.util.resource.Resource;

import java.io.File;
import java.net.URI;

public class MyFileResourceServlet extends DefaultServlet {

    public Resource getResource(String pathInContext) {
        Resource resource = null;
//        pathInContext = pathInContext.replace("/video", "");
        Log.i(VideoResourceServlet.class.getSimpleName(), "Path:" + pathInContext);

        try {
            String path = new URI(pathInContext).getPath();
            File file = new File(path);
            if (file.exists()) {
                resource = FileResource.newResource(file);
            }
        } catch (Exception var8) {
            var8.printStackTrace();
        }

        return resource;
    }

}
