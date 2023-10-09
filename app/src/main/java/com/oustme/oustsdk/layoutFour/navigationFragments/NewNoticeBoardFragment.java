package com.oustme.oustsdk.layoutFour.navigationFragments;

import static com.oustme.oustsdk.tools.appconstants.AppConstants.StringConstants.IS_SPLASH_BACKGROUND_IMAGE_DOWNLOADED;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.activity.common.noticeBoard.data.handlers.SubmitNBPostService;
import com.oustme.oustsdk.layoutFour.data.Navigation;
import com.oustme.oustsdk.layoutFour.newnoticeBoard.NewNoticeBoardViewModel;
import com.oustme.oustsdk.layoutFour.newnoticeBoard.activity.NewNBCreatePostList;
import com.oustme.oustsdk.layoutFour.newnoticeBoard.adapters.NewNBAllPostAdapter;
import com.oustme.oustsdk.layoutFour.newnoticeBoard.adapters.NewNBTopicAdapter;
import com.oustme.oustsdk.layoutFour.newnoticeBoard.callBacks.NewNBDeleteListener;
import com.oustme.oustsdk.layoutFour.newnoticeBoard.callBacks.NewNBPostClickCallBack;
import com.oustme.oustsdk.layoutFour.newnoticeBoard.callBacks.NewNoticeBoardCommentDeleteListener;
import com.oustme.oustsdk.layoutFour.newnoticeBoard.data.handlers.NewPostViewTracker;
import com.oustme.oustsdk.layoutFour.newnoticeBoard.dialogs.NewCommentDeleteConfirmationPopup;
import com.oustme.oustsdk.layoutFour.newnoticeBoard.model.request.NewPostViewData;
import com.oustme.oustsdk.layoutFour.newnoticeBoard.model.response.NewNBPostData;
import com.oustme.oustsdk.layoutFour.newnoticeBoard.model.response.NewNBTopicData;
import com.oustme.oustsdk.layoutFour.newnoticeBoard.presenters.NewNBTopicDetailPresenter;
import com.oustme.oustsdk.layoutFour.newnoticeBoard.view.NewNBTopicDetailView;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.OustShareTools;
import com.oustme.oustsdk.tools.appconstants.AppConstants;
import com.oustme.oustsdk.utils.OustResourceUtils;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class NewNoticeBoardFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, NewNBTopicDetailView, NewNBPostClickCallBack,
        NewNBDeleteListener, NewNoticeBoardCommentDeleteListener {

    public static final String TAG = "NewNoticeBoardFragment";

    //View initialize
    SwipeRefreshLayout swipe_container;
    LinearLayout container;
    LinearLayout container_non_scrollable;

    RelativeLayout branding_mani_layout;
    ImageView branding_bg;
    ImageView branding_icon;
    TextView branding_percentage;

    //common for all base fragment
    private static final String ARG_NAV_ITEM = "navItem";
    Navigation navigation;

    NewNBTopicDetailPresenter mPresenter;
    private NewNBAllPostAdapter nbAllPostAdapter;
    private RecyclerView rvNoticeBoard;
    private RecyclerView rv_noticeboard_posts;
    private NewNBTopicAdapter nbTopicAdapter;
    TextView text_title_2, text_title;
    private NewPostViewData postViewData;
    private View view;
    ArrayList<NewNBTopicData> nbTopicData1 = new ArrayList<>();
    int nbTopicDataCount = 0;

    View no_data_layout;
    ImageView no_image;
    TextView no_data_content, no_data;
    private static final int WRITE_STORAGE_PERMISSION = 123;
    String queryCheck = "";
    FloatingActionButton create_post;
    int color;
    int bgColor;

    public NewNoticeBoardFragment() {
        // Required empty public constructor
    }

    public static NewNoticeBoardFragment newInstance(Navigation navigation) {
        NewNoticeBoardFragment fragment = new NewNoticeBoardFragment();
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
            queryCheck = "";
        }
        getColors();

    }

    private void getColors() {
        try {
            if (OustPreferences.getAppInstallVariable("isLayout4")) {
                color = OustResourceUtils.getColors();
                bgColor = OustResourceUtils.getToolBarBgColor();
            } else {
                bgColor = OustResourceUtils.getColors();
                color = OustResourceUtils.getToolBarBgColor();

            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_notice_board_2, container, false);
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

            rvNoticeBoard = view.findViewById(R.id.rv_noticeboard);
            rv_noticeboard_posts = view.findViewById(R.id.rv_noticeboard_posts);
            text_title_2 = view.findViewById(R.id.text_title_2);
            text_title = view.findViewById(R.id.text_title);
            no_data_layout = view.findViewById(R.id.no_data_layout);
            no_image = view.findViewById(R.id.no_image);
            no_data_content = view.findViewById(R.id.no_data_content);
            no_data = view.findViewById(R.id.no_data);
            create_post = view.findViewById(R.id.create_post);
            //Branding loader
            branding_mani_layout = view.findViewById(R.id.branding_main_layout);
            branding_bg = view.findViewById(R.id.branding_bg);
            branding_icon = view.findViewById(R.id.brand_loader);
            branding_percentage = view.findViewById(R.id.percentage_text);
            //End

            no_image.setImageDrawable(getResources().getDrawable(R.drawable.no_noticeboard));
            no_data_content.setText(getResources().getString(R.string.no_noticeboard_content));

            swipe_container.setOnRefreshListener(this);
            swipe_container.setColorSchemeColors(OustResourceUtils.getColors());

            create_post.setBackgroundTintList(ColorStateList.valueOf(color));

            OustResourceUtils.setDefaultDrawableColor(create_post.getDrawable(), Color.WHITE);

            create_post.setOnClickListener(v -> {
                if (nbTopicData1 != null && nbTopicData1.size() > 0) {
                    Intent i = new Intent(getActivity(), NewNBCreatePostList.class);
                    i.putExtra("listIs", nbTopicData1);
                    startActivity(i);
                } else {
                    Toast.makeText(getActivity(), getResources().getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                }
            });


            String tenantId = OustPreferences.get("tanentid");

            if (tenantId != null && !tenantId.isEmpty()) {
                File brandingBg = new File(OustSdkApplication.getContext().getFilesDir(),
                        ("oustlearn_" + tenantId.toUpperCase().trim() + "splashScreen"));

                if (brandingBg.exists() && OustPreferences.getAppInstallVariable(IS_SPLASH_BACKGROUND_IMAGE_DOWNLOADED)) {
                    Picasso.get().load(brandingBg).into(branding_bg);
                } else {
                    String tenantBgImage = AppConstants.MediaURLConstants.CLOUD_FRONT_BASE_PATH + "appImages/splash/org/" + (tenantId.toUpperCase().trim()) + "/android/bgImage";
                    Picasso.get().load(tenantBgImage).into(branding_bg);
                }

                File brandingLoader = new File(OustSdkApplication.getContext().getFilesDir(), ("oustlearn_" + tenantId.toUpperCase().trim() + "splashIcon"));
                if (brandingLoader.exists()) {
                    Picasso.get().load(brandingLoader).into(branding_icon);
                } else {
                    String tenantIcon = AppConstants.MediaURLConstants.CLOUD_FRONT_BASE_PATH + "appImages/splash/org/" + (tenantId.toUpperCase().trim()) + "/android/icon";
                    Picasso.get().load(tenantIcon).error(getResources().getDrawable(R.drawable.app_icon)).into(branding_icon);
                }
            }
            branding_mani_layout.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void onRefresh() {
        try {
            if (queryCheck != null && queryCheck.trim().length() > 0) {
                // for search and refresh
                Log.d("tag", "for search and refresh");
                swipe_container.setRefreshing(false);
            } else {
                initData();
            }
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
            no_data.setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

    }

    private void fetchNoticeBoard() {
        NewNoticeBoardViewModel noticeBoardViewModel = new ViewModelProvider(this).get(NewNoticeBoardViewModel.class);
        noticeBoardViewModel.init();
        noticeBoardViewModel.getNBTopicList().observe(getViewLifecycleOwner(), nbTopicData -> {
            if (nbTopicData != null && nbTopicData.size() > 0) {
//                nbTopicData1.clear();
                nbTopicData1 = nbTopicData;
                branding_mani_layout.setVisibility(View.VISIBLE);
                no_data_layout.setVisibility(View.GONE);
                rvNoticeBoard.setVisibility(View.VISIBLE);
                rv_noticeboard_posts.setVisibility(View.VISIBLE);
                text_title_2.setVisibility(View.VISIBLE);
                text_title.setVisibility(View.VISIBLE);
//                if (nbTopicAdapter == null) {
                nbTopicAdapter = new NewNBTopicAdapter(getContext(), nbTopicData);
                LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
                rvNoticeBoard.setLayoutManager(mLayoutManager);
                rvNoticeBoard.setItemAnimator(new DefaultItemAnimator());
                rvNoticeBoard.setAdapter(nbTopicAdapter);
//                } else {
//                    nbTopicAdapter.notifyListChnage(nbTopicData);
//                }

                for (int it = 0; it < nbTopicData.size(); it++) {
                    if (nbTopicData.get(it).getPostUpdateData() != null) {
                        if (nbTopicData.get(it).getPostUpdateData().size() > 0) {
                            nbTopicDataCount = nbTopicDataCount + nbTopicData.get(it).getPostUpdateDataSize();
                        }
                    }
//                    Log.d("countIs1:", "" + nbTopicDataCount);
                }

                if (nbTopicDataCount > 0) {
                    mPresenter = new NewNBTopicDetailPresenter(this);
                    mPresenter.getAllPostData(nbTopicData);
                } else {
                    branding_mani_layout.setVisibility(View.GONE);
                    rv_noticeboard_posts.setVisibility(View.GONE);
                    text_title_2.setVisibility(View.GONE);
                }
                nbTopicDataCount = 0;

                create_post.setVisibility(View.VISIBLE);
                swipe_container.setRefreshing(false);

            } else {
                Log.d(TAG, "onChangedd: data not found");
                no_data_layout.setVisibility(View.VISIBLE);
                rvNoticeBoard.setVisibility(View.GONE);
                rv_noticeboard_posts.setVisibility(View.GONE);
                text_title_2.setVisibility(View.GONE);
                swipe_container.setRefreshing(false);
                text_title.setVisibility(View.GONE);
                create_post.setVisibility(View.GONE);
                branding_mani_layout.setVisibility(View.GONE);
            }
        });
    }

    public void onSearch(String query) {
        try {
            queryCheck = query;
            if (query != null) {
                try {
                    if (nbAllPostAdapter != null) {
                        nbAllPostAdapter.getFilter().filter(query);
                    }

                    new Handler().postDelayed(() -> {
                        if (nbAllPostAdapter != null) {
                            if (nbAllPostAdapter.getItemCount() != 0) {
                                no_data.setVisibility(View.GONE);
                                text_title_2.setVisibility(View.VISIBLE);
                                rv_noticeboard_posts.setVisibility(View.VISIBLE);
                            } else {
                                no_data.setVisibility(View.VISIBLE);
                                text_title_2.setVisibility(View.GONE);
                                rv_noticeboard_posts.setVisibility(View.GONE);
                            }
                        }
                    }, 2000);

                } catch (Exception e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void updateTopicBanner(String imageUrl) {

    }

    @Override
    public void setToolbarText(String title) {

    }

    @Override
    public void OnErrorOccured(String error) {
        branding_mani_layout.setVisibility(View.GONE);
    }

    @Override
    public void setOrUpdateAdapter(ArrayList<NewNBPostData> postDataList) {
        if (nbAllPostAdapter == null) {
            rv_noticeboard_posts.setVisibility(View.VISIBLE);
            nbAllPostAdapter = new NewNBAllPostAdapter(getContext(), postDataList, this);
            LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
            rv_noticeboard_posts.setLayoutManager(mLayoutManager);
            rv_noticeboard_posts.setItemAnimator(new DefaultItemAnimator());
            rv_noticeboard_posts.setAdapter(nbAllPostAdapter);
            Log.d(TAG, "sizeis0:" + postDataList.size());

            NewPostViewTracker viewTracker = new NewPostViewTracker();
            viewTracker.setRecyclerView(rv_noticeboard_posts);
            viewTracker.setNbPostClickCallBack(this);
            viewTracker.startTracking();

        } else {
            rv_noticeboard_posts.setVisibility(View.VISIBLE);
            nbAllPostAdapter.notifyListChnage(postDataList);
        }
        swipe_container.setRefreshing(false);
        branding_mani_layout.setVisibility(View.GONE);
    }

    @Override
    public void setOrUpdateAdapter2(List<NewNBPostData> postDataList) {

    }

    @Override
    public void startApiCalls() {
        Intent intent = new Intent(Intent.ACTION_SYNC, null, requireContext(), SubmitNBPostService.class);
        OustSdkApplication.getContext().startService(intent);
    }

    @Override
    public void showProgressBar() {

    }

    @Override
    public void hideProgressBar() {

    }

    @Override
    public void createLoader() {

    }

    @Override
    public void setData(NewNBTopicData nbTopicData) {

    }

    @Override
    public void onPostViewed(NewPostViewData postViewData) {
        if (mPresenter != null) {
            mPresenter.addViewPostData(postViewData);
        }
    }

    @Override
    public void onPostViewed(int position) {
        if (nbAllPostAdapter != null) {
            nbAllPostAdapter.onNBPostViewedInScroll(position);
        }
    }

    @Override
    public void onPostLike(NewPostViewData postViewData) {
        if (mPresenter != null) {
            mPresenter.sendPostLikeData(postViewData);
        }
    }

    @Override
    public void onPostComment(NewPostViewData postViewData) {
        if (mPresenter != null) {
            mPresenter.sendPostCommentData(postViewData);
        }
    }

    @Override
    public void onPostCommentDelete(NewPostViewData postViewData) {
        new NewCommentDeleteConfirmationPopup(getContext(), postViewData, this);
    }

    @Override
    public void onPostShare(NewPostViewData postViewData, View view) {
        if (mPresenter != null) {
            shareDetails(postViewData, view);
        }
    }

    private void shareDetails(NewPostViewData postViewData, View view) {
        if (ContextCompat.checkSelfPermission(requireContext(), "android.permission.WRITE_EXTERNAL_STORAGE") != PackageManager.PERMISSION_GRANTED) {
            this.postViewData = postViewData;
            this.view = view;
            ActivityCompat.requestPermissions(requireActivity(), new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, WRITE_STORAGE_PERMISSION);
        } else {
            OustShareTools.share(requireActivity(), OustSdkTools.getInstance().getScreenShot(view), "");
            mPresenter.sendPostShareData(postViewData);
        }
    }

    @Override
    public void onRequestPermissions(NewPostViewData postViewData, View view) {
        this.postViewData = postViewData;
        this.view = view;
        if (ContextCompat.checkSelfPermission(requireContext(), "android.permission.WRITE_EXTERNAL_STORAGE") != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, WRITE_STORAGE_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        try {
            if (requestCode == WRITE_STORAGE_PERMISSION) {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    shareDetails(postViewData, view);
                } else {
                    OustSdkTools.showToast("Please provide permissions to get going !");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void onDelete(NewPostViewData postViewData) {
        mPresenter.deletePostComment(postViewData);
    }

    @Override
    public void onDeleteCancel() {

    }

    @Override
    public void onDeleteComment(NewPostViewData postViewData) {

    }

    public void closeKeyboard() {
        try {
            View view = requireActivity().getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }
}