package com.oustme.oustsdk.layoutFour.navigationFragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.oustme.oustsdk.R;
import com.oustme.oustsdk.layoutFour.components.noticeBoard.ComponentNoticeBoard;
import com.oustme.oustsdk.layoutFour.components.noticeBoard.NoticeBoardViewModel;
import com.oustme.oustsdk.layoutFour.data.Navigation;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.utils.OustResourceUtils;

import java.util.List;

public class NoticeBoardFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{

    public static final String TAG = "NoticeBoardFragment";

    //View initialize
    SwipeRefreshLayout swipe_container;
    LinearLayout container;
    LinearLayout container_non_scrollable;

    //Dynamic component
    ComponentNoticeBoard componentNoticeBoard;

    //common for all base fragment
    private static final String ARG_NAV_ITEM = "navItem";
    Navigation navigation;

    public NoticeBoardFragment() {
        // Required empty public constructor
    }

    public static NoticeBoardFragment newInstance(Navigation navigation) {
        NoticeBoardFragment fragment = new NoticeBoardFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_NAV_ITEM, navigation);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            navigation = getArguments().getParcelable(ARG_NAV_ITEM);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_notice_board, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        initData();
    }

    private void initView(View view) {
        try {
            swipe_container = view.findViewById(R.id.swipe_container);
            container = view.findViewById(R.id.container);
            container_non_scrollable = view.findViewById(R.id.container_non_scrollable);

            swipe_container.setOnRefreshListener(this);
            swipe_container.setColorSchemeColors(OustResourceUtils.getColors());

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void onRefresh() {
        try {
            setContentComponent();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    private void initData() {
        try {
            if (container != null)
                container.removeAllViews();
            if (container_non_scrollable != null)
                container_non_scrollable.removeAllViews();

            componentNoticeBoard = null;
            setContentComponent();

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void setContentComponent() {
        try {
            List<String> listContent = navigation.getContent();
            if (listContent == null)
                return;

            for (String content : listContent) {
                if ("noticeboard".equalsIgnoreCase(content)) {
                    fetchNoticeBoard();
                }
            }

            swipe_container.setRefreshing(false);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

    }

    private void fetchNoticeBoard() {
        NoticeBoardViewModel noticeBoardViewModel = new ViewModelProvider(this).get(NoticeBoardViewModel.class);
        noticeBoardViewModel.init();
        noticeBoardViewModel.getNBTopicList().observe(getViewLifecycleOwner(), nbTopicData -> {
            if (componentNoticeBoard == null) {
                componentNoticeBoard = new ComponentNoticeBoard(getActivity(), null);
                container.addView(componentNoticeBoard);
            }

            if(nbTopicData!=null && nbTopicData.size()>0){
                componentNoticeBoard.setData(nbTopicData);
            }else{
                Log.d(TAG, "onChanged: data not found");
                componentNoticeBoard.setNoDataImage();
            }
        });
    }

    public void onSearch(String query) {
        try {
            if (query != null){
                if (componentNoticeBoard != null){
                    componentNoticeBoard.onNoticeBoardSearch(query);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }
}