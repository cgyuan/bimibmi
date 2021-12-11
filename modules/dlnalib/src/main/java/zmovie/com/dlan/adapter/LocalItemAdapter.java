package zmovie.com.dlan.adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.view.ViewGroup;

import org.fourthline.cling.support.model.DIDLContent;

/**
 * creator huangyong
 * createTime 2019/3/23 上午10:00
 * path zmovie.com.dlan.adapter
 * description:
 */
public class LocalItemAdapter extends RecyclerView.Adapter<LocalItemAdapter.ItemHolder> {


    private DIDLContent content;

    @NonNull
    @Override
    public ItemHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull ItemHolder itemHolder, int i) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    class ItemHolder extends RecyclerView.ViewHolder {

        public ItemHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
