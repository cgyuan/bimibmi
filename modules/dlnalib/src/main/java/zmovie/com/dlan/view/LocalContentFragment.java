package zmovie.com.dlan.view;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.yanbo.lib_screen.event.DIDLEvent;
import com.yanbo.lib_screen.listener.ItemClickListener;
import com.yanbo.lib_screen.manager.ClingManager;

import org.fourthline.cling.support.model.DIDLObject;
import org.fourthline.cling.support.model.container.Container;
import org.fourthline.cling.support.model.item.Item;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import zmovie.com.dlan.MediaPlayActivity;
import zmovie.com.dlan.R;

/**
 * Created by lzan13 on 2018/3/19.
 * 本地内容界面
 */
public class LocalContentFragment extends Fragment implements View.OnClickListener {

    LinearLayout parentDirectory;
    RecyclerView recyclerView;

    private RecyclerView.LayoutManager layoutManager;
    private LocalContentAdapter adapter;
    private List<DIDLObject> objectList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_local_content, null);
        initView(view);
        return view;
    }

    /**
     * 创建实例对象的工厂方法
     */
    public static LocalContentFragment newInstance() {
        LocalContentFragment fragment = new LocalContentFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }


    protected void initView(View view) {
        parentDirectory = view.findViewById(R.id.layout_parent_directory);
        recyclerView = view.findViewById(R.id.recycler_view);

        parentDirectory.setOnClickListener(this);
        layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        adapter = new LocalContentAdapter(getActivity(), objectList);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        setItemClickListener();
        searchLocalContent("0");
    }

    private void setItemClickListener() {
        adapter.setItemClickListener(new ItemClickListener() {
            @Override
            public void onItemAction(int action, Object object) {
                if (object instanceof Container) {
                    Container container = (Container) object;
                    searchLocalContent(container.getId());
                } else if (object instanceof Item) {
                    Item item = (Item) object;
                    ClingManager.getInstance().setLocalItem(item);


                    startActivity(new Intent(getActivity(), MediaPlayActivity.class));
                }
            }
        });
    }

    private void searchLocalContent(String containerId) {
        ClingManager.getInstance().searchLocalContent(containerId);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventBus(DIDLEvent event) {
        objectList.clear();
        if (event.content.getContainers().size() > 0) {
            objectList.addAll(event.content.getContainers());
        } else if (event.content.getItems().size() > 0) {
            objectList.addAll(event.content.getItems());
        }
        adapter.refresh();
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId()==R.id.layout_parent_directory){
            searchLocalContent("0");
        }
    }
}
