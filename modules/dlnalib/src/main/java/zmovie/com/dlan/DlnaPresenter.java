package zmovie.com.dlan;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.yanbo.lib_screen.entity.ClingDevice;
import com.yanbo.lib_screen.entity.RemoteItem;
import com.yanbo.lib_screen.event.DeviceEvent;
import com.yanbo.lib_screen.manager.ClingManager;
import com.yanbo.lib_screen.manager.DeviceManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

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
        ClingManager.getInstance().startClingService();
        EventBus.getDefault().register(this);
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
        RemoteItem item = new RemoteItem(title, "", "",
                1, "", "1280x720", path);
        startPlay(item);
    }

    /**
     * 本地投屏
     *
     * @param remoteItem
     */
    public void startPlayLocal(RemoteItem remoteItem) {
        ClingManager.getInstance().setRemoteItem(remoteItem);
        MediaPlayActivity.startSelf((Activity) context);
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

}
