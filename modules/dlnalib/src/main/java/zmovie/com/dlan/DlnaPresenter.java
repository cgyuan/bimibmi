package zmovie.com.dlan;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.yanbo.lib_screen.entity.ClingDevice;
import com.yanbo.lib_screen.entity.RemoteItem;
import com.yanbo.lib_screen.event.DIDLEvent;
import com.yanbo.lib_screen.event.DeviceEvent;
import com.yanbo.lib_screen.manager.ClingManager;
import com.yanbo.lib_screen.manager.DeviceManager;
import com.yanbo.lib_screen.utils.VMNetwork;

import org.fourthline.cling.model.ModelUtil;
import org.fourthline.cling.support.model.Res;
import org.fourthline.cling.support.model.item.VideoItem;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;

import per.goweii.anylayer.AnyLayer;
import per.goweii.anylayer.DialogLayer;

/**
 * creator huangyong
 * createTime 2019/3/18 下午9:47
 * path com.yanbo.videodlnascreen
 * description:
 */
public class DlnaPresenter {

    private Context context;
    private ClingDeviceAdapter mDeviceAdapter;


    /**
     * 初始化投屏管理类
     *
     * @param context
     */
    public DlnaPresenter(Context context) {
        this.context = context;
    }

    /**
     * 初始化投屏服务，搜索可用设备
     */
    public void initService() {
        EventBus.getDefault().register(this);
//        ClingManager.getInstance().searchLocalContent("20");
    }

    public void removeEventRegister() {
        EventBus.getDefault().unregister(this);
    }

    /**
     * 网络投屏
     *
     * @param remoteItem
     */
    public void startPlay(RemoteItem remoteItem) {
        ClingManager.getInstance().setRemoteItem(remoteItem);

        context.startActivity(new Intent(context,MediaPlayActivity.class));
    }

    public void startPlay(String path, String title) {
        if (path.startsWith("/")) {
            VideoItem localItem = new VideoItem();
            localItem.setId("");
            localItem.setParentID("");
            localItem.setRefID("");
            localItem.setTitle("");

            String[] videoColumns = new String[]{"_id", "title", "_data", "artist", "mime_type", "_size", "duration", "resolution"};
            Cursor cur = context.getContentResolver().query(android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI, videoColumns, "_data = ?", new String[]{path}, null);
            if (cur != null && cur.moveToFirst()) {
                String id = String.valueOf(cur.getInt(cur.getColumnIndex("_id")));
                String creator = cur.getString(cur.getColumnIndex("artist"));
                String mimeType = cur.getString(cur.getColumnIndex("mime_type"));
                long size = cur.getLong(cur.getColumnIndex("_size"));
                long duration = cur.getLong(cur.getColumnIndex("duration"));
                String durationStr = ModelUtil.toTimeString(duration / 1000L);
                String resolution = cur.getString(cur.getColumnIndex("resolution"));
                String data = cur.getString(cur.getColumnIndexOrThrow("_data"));
                String fileName = data.substring(data.lastIndexOf(File.separator));
                String ext = fileName.substring(fileName.lastIndexOf("."));
                data = data.replace(fileName, File.separator + id + ext);
                String serverURL = "http://" + VMNetwork.getLocalIP() + ":" + "55677" + "/";
                String url = serverURL + "video" + data;
                Res res = new Res(mimeType, size, durationStr, (Long)null, url);
                res.setDuration(ModelUtil.toTimeString(duration / 1000L));
                res.setResolution(resolution);
                localItem.addResource(res);
                cur.close();
                startPlayLocal(localItem);
            } else {
                Res res = new Res();
                String serverURL = "http://" + VMNetwork.getLocalIP() + ":" + "55699";
                String url = serverURL + path;
                res.setValue(url);
                localItem.addResource(res);
                startPlayLocal(localItem);
            }
        } else {
            RemoteItem item = new RemoteItem(title, "", "",
                    1, "", "1280x720", path);
            startPlay(item);
        }
    }

    /**
     * 本地投屏
     *
     * @param item
     */
    public void startPlayLocal(VideoItem item) {
        ClingManager.getInstance().setLocalItem(item);
        context.startActivity(new Intent(context,MediaPlayActivity.class));
    }

    /**
     * 退出并停止投屏服务
     */
    public void stopService() {
        ClingManager.getInstance().stopClingService();
    }

    public boolean hasDeviceConnect() {
        if (DeviceManager.getInstance().getClingDeviceList().size() > 0 && DeviceManager.getInstance().getCurrClingDevice() != null) {
            return true;
        }
        return false;
    }

    public void showDialogTip(final Context context, final String path, final String title) {

        final DialogLayer anyLayer = AnyLayer.dialog(context)
                .contentView(R.layout.dlna_device_pop_layout)
                .backgroundBlurPercent(0.15f)
                .backgroundColorInt(Color.parseColor("#33ffffff"))
                .gravity(Gravity.CENTER)
                .cancelableOnTouchOutside(true)
                .cancelableOnClickKeyBack(true);
        anyLayer.show();
        final RecyclerView deviceList = anyLayer.getView(R.id.device_list);
        deviceList.setLayoutManager(new LinearLayoutManager(context));
        mDeviceAdapter = new ClingDeviceAdapter(context);
        deviceList.setAdapter(mDeviceAdapter);

       final Button confirm =  anyLayer.getView(R.id.confirm);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hasDeviceConnect()){
                    anyLayer.dismiss();
                    startPlay(path,title);
                }
            }
        });
        mDeviceAdapter.setItemClickListener(new ClingDeviceAdapter.OnDeviceCheckedListener() {
            @Override
            public void onItemChecked(int position, Object object) {
                ClingDevice device = (ClingDevice) object;
                ClingDevice currClingDevice = DeviceManager.getInstance().getCurrClingDevice();
                if (currClingDevice==device){
                    return;
                }
                DeviceManager.getInstance().setCurrClingDevice(device);
                Toast.makeText(context,"选择了设备 " + device.getDevice().getDetails().getFriendlyName(),Toast.LENGTH_LONG).show();
                mDeviceAdapter.refresh();
            }

            @Override
            public void onItemCancelChose(int position, Object device) {
                mDeviceAdapter.refresh();
            }

            @Override
            public void onRefreshed() {
                if (hasDeviceConnect()){
//                    tips.setText("已连接设备，点击确定按钮开始投屏");
                    confirm.setEnabled(true);
                } else {
//                    tips.setText("当前没有设备连接，请点击刷新按钮搜索设备");
                    confirm.setEnabled(false);
                }
            }

        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventBus(DeviceEvent event) {
        mDeviceAdapter.refresh();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDIDLEventBus(DIDLEvent event) {
        Log.d("DIDL", event.toString());
    }

}
