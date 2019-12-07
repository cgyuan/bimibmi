package zmovie.com.dlan;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.yanbo.lib_screen.entity.ClingDevice;
import com.yanbo.lib_screen.manager.DeviceManager;

import java.util.List;

public class ClingDeviceAdapter extends RecyclerView.Adapter<ClingDeviceAdapter.ClingHolder> {

    private LayoutInflater layoutInflater;
    private List<ClingDevice> clingDevices;
    private OnDeviceCheckedListener clickListener;
    private Context context;

    public ClingDeviceAdapter(Context context) {
        super();
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
        clingDevices = DeviceManager.getInstance().getClingDeviceList();
    }

    @NonNull
    @Override
    public ClingHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.device_item_layout, parent, false);
        return new ClingHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ClingHolder holder, final int position) {
        final ClingDevice device = clingDevices.get(position);
        if (device != null && device == DeviceManager.getInstance().getCurrClingDevice()) {
            holder.dlnaStatusTv.setText("已连接");

        } else {
            holder.dlnaStatusTv.setText("未连接");
        }

        if (device == null) {
            holder.deviceNameTv.setText("Unknow device");
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, "未知设备，无法连接", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            holder.deviceNameTv.setText(device.getDevice().getDetails().getFriendlyName());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (clickListener != null) {
                        clickListener.onItemChecked(position, device);
                    }
                }
            });
        }

        if (clickListener!=null){
            clickListener.onRefreshed();
        }

    }

    @Override
    public int getItemCount() {
        return clingDevices.size();
    }

    public void refresh() {
        clingDevices = DeviceManager.getInstance().getClingDeviceList();
        notifyDataSetChanged();
        if (clickListener!=null){
            clickListener.onRefreshed();
        }
    }

    public void setItemClickListener(OnDeviceCheckedListener listener) {
        this.clickListener = listener;
    }

    static class ClingHolder extends RecyclerView.ViewHolder {
        TextView deviceNameTv;
        TextView dlnaStatusTv;
        public ClingHolder(View itemView) {
            super(itemView);
            deviceNameTv = itemView.findViewById(R.id.device_name);
            dlnaStatusTv = itemView.findViewById(R.id.dlna_status);
        }
    }


    public interface OnDeviceCheckedListener {
        void onItemChecked(int position, Object device);

        void onItemCancelChose(int position, Object device);

        void onRefreshed();
    }

}
