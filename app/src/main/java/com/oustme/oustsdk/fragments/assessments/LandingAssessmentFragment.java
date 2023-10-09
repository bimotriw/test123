package com.oustme.oustsdk.fragments.assessments;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.adapter.courses.NewLandingCoursesAdapter;
import com.oustme.oustsdk.customviews.CustomSearchView;
import com.oustme.oustsdk.customviews.MyCustomLayoutManager;
import com.oustme.oustsdk.firebase.common.CommonLandingData;
import com.oustme.oustsdk.interfaces.common.DataLoaderNotifier;
import com.oustme.oustsdk.interfaces.common.NewLandingInterface;
import com.oustme.oustsdk.interfaces.common.PageLoader;
import com.oustme.oustsdk.interfaces.common.RowClickCallBack;
import com.oustme.oustsdk.interfaces.course.SendCertificateCallBack;
import com.oustme.oustsdk.request.SendCertificateRequest;
import com.oustme.oustsdk.response.course.CommonResponse;
import com.oustme.oustsdk.tools.ActiveUser;
import com.oustme.oustsdk.tools.HttpManager;
import com.oustme.oustsdk.tools.OustAppState;
import com.oustme.oustsdk.tools.OustDataHandler;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.OustStaticVariableHandling;
import com.oustme.oustsdk.tools.OustStrings;
import com.oustme.oustsdk.tools.filters.AssessmentFilter;
import com.oustme.oustsdk.tools.filters.CourseFilter;
import com.oustme.oustsdk.util.ApiCallUtils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by admin on 02/12/17.
 */

public class LandingAssessmentFragment extends Fragment implements SearchView.OnQueryTextListener,
        CustomSearchView.OnSearchViewCollapsedEventListener, CustomSearchView.OnSearchViewExpandedEventListener, RowClickCallBack, SendCertificateCallBack, PageLoader {

    private RecyclerView newlanding_recyclerview;
    private TextView nocourse_text, loading_text;
    private RelativeLayout reflayout, ffcbannerref_imglayout, courseloader_ll;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ActiveUser activeUser;
    private boolean searchOn = false;
    private boolean startCreatingList = false;
    private String TAG = "NewLandingAssessmnetFragment";
    private int courseAssessmentType = 0;

    private DataLoaderNotifier dataLoaderNotifier;

    public void setDataLoaderNotifier(DataLoaderNotifier dataLoaderNotifier) {
        this.dataLoaderNotifier = dataLoaderNotifier;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.newlanding_fragment, container, false);
        initViews(view);
        if (OustStaticVariableHandling.getInstance().getIsNewLayout() == 1) {
            courseAssessmentType = 2;
            searchOn = true;
            newLandingInterface.showPendingCoursesUI();
            newlanding_recyclerview = view.findViewById(R.id.newlayoutlanding_recyclerview);
        }
        initLanding();
        return view;
    }

    private void initViews(View view) {
        newlanding_recyclerview = view.findViewById(R.id.newlanding_recyclerview);
        nocourse_text = view.findViewById(R.id.nocourse_text);
        reflayout = view.findViewById(R.id.reflayout);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        ffcbannerref_imglayout = view.findViewById(R.id.ffcbannerref_imglayout);
        courseloader_ll = view.findViewById(R.id.courseloader_ll);
        loading_text = view.findViewById(R.id.loading_text);

        loading_text.setText(getResources().getString(R.string.loading));
    }

    public void filterData(int type) {
        if (type == 1) {
            showCompletedAssessments();
        } else if (type == 2) {
            showPendingAssessments();
        } else if (type == 4) {
            createMainList();
        } else {
            startCreateList();
        }
    }

    public void startCreateList() {
        try {
            courseAssessmentType = 0;
            OustPreferences.saveintVar("totalAssessmentAvailable", OustAppState.getInstance().getMyAssessmentList().size());
            createAssessmentList(OustAppState.getInstance().getMyAssessmentList());
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void showCompletedAssessments() {
        searchOn = true;
        courseAssessmentType = 1;
        List<CommonLandingData> allAssessmentFilter = new ArrayList<>();
        if ((OustAppState.getInstance().getMyAssessmentList() != null) && (OustAppState.getInstance().getMyAssessmentList().size() > 0)) {
            CourseFilter courseFilter = new CourseFilter();
            allAssessmentFilter = courseFilter.completedCourseMeetCriteria(OustAppState.getInstance().getMyAssessmentList());
        }
        if (allAssessmentFilter.size() == 0) {
            int totalModules = OustPreferences.getSavedInt("totalAssessmentAvailable");
            if (OustAppState.getInstance().getMyAssessmentList().size() < totalModules - 1) {
                dataLoaderNotifier.getNextData("ASSESSMENT");
            }
        }
        OustPreferences.saveintVar("totalAssessmentAvailable", allAssessmentFilter.size());
        createAssessmentList(allAssessmentFilter);
    }

    public void showPendingAssessments() {
        searchOn = true;
        courseAssessmentType = 2;
        List<CommonLandingData> allAssessmentFilter = new ArrayList<>();
        if ((OustAppState.getInstance().getMyAssessmentList() != null) && (OustAppState.getInstance().getMyAssessmentList().size() > 0)) {
            CourseFilter courseFilter = new CourseFilter();
            allAssessmentFilter = courseFilter.pendingCourseMeetCriteria(OustAppState.getInstance().getMyAssessmentList());
        }
        if (allAssessmentFilter.size() == 0) {
            int totalModules = OustPreferences.getSavedInt("totalAssessmentAvailable");
            if (OustAppState.getInstance().getMyAssessmentList().size() < totalModules - 1) {
                dataLoaderNotifier.getNextData("ASSESSMENT");
            }
        }
        OustPreferences.saveintVar("totalAssessmentAvailable", allAssessmentFilter.size());
        createAssessmentList(allAssessmentFilter);
    }

    private NewLandingInterface newLandingInterface;

    public void setNewLandingInterface(NewLandingInterface newLandingInterface) {
        this.newLandingInterface = newLandingInterface;
    }

    public void initLanding() {
        try {
            activeUser = OustAppState.getInstance().getActiveUser();
            createLoader();
            showLoader();
            setHasOptionsMenu(true);
            if (!OustAppState.getInstance().isChallengeFragmentInit()) {
                OustAppState.getInstance().setChallengeFragmentInit(true);
                if (OustAppState.getInstance().getMyAssessmentList() == null || OustAppState.getInstance().getMyAssessmentList().size() == 0) {
                    showPendingAssessments();
                }
            } else {
                if ((OustAppState.getInstance().getMyDeskList() != null) && (OustAppState.getInstance().getMyDeskList().size() > 0)) {
                    startCreatingList = true;
                    createList();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void createMainList() {
        setf3cBannerLayout();
        startCreatingList = true;
        Collections.sort(OustAppState.getInstance().getMyAssessmentList(), sortByDate);
        createList();
    }

    public Comparator<CommonLandingData> sortByDate = new Comparator<CommonLandingData>() {
        public int compare(CommonLandingData s1, CommonLandingData s2) {
            if (s2.getAddedOn() == null) {
                return -1;
            }
            if (s1.getAddedOn() == null) {
                return 1;
            }
            if (s1.getAddedOn().equals(s2.getAddedOn())) {
                return 0;
            }
            return s2.getAddedOn().compareTo(s1.getAddedOn());
        }
    };

    private void createList() {
        if (!searchOn) {
            createAssessmentList(OustAppState.getInstance().getMyAssessmentList());
        } else {
            if (courseAssessmentType == 1) {
                showCompletedAssessments();
            } else if (courseAssessmentType == 2) {
                showPendingAssessments();
            } else {
                filterList(searchText);
            }
        }
    }

    private void setf3cBannerLayout() {
        try {
            if (OustStaticVariableHandling.getInstance().isContestLive()) {
                if (ffcbannerref_imglayout.getVisibility() == View.GONE) {
                    ffcbannerref_imglayout.setVisibility(View.VISIBLE);
                    int size = (int) getResources().getDimension(R.dimen.oustlayout_dimen8);
                    DisplayMetrics metrics = this.getResources().getDisplayMetrics();
                    int scrWidth = metrics.widthPixels;
                    int imageWidth = (scrWidth) - size;
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) ffcbannerref_imglayout.getLayoutParams();
                    float h = (imageWidth * 0.3f);
                    params.height = ((int) h);
                    ffcbannerref_imglayout.setLayoutParams(params);
                }
            } else {
                ffcbannerref_imglayout.setVisibility(View.GONE);
            }
        } catch (Exception e) {
        }
    }


    private void createLoader() {
        try {
            swipeRefreshLayout.setColorSchemeColors(OustSdkTools.getColorBack(R.color.Orange),
                    OustSdkTools.getColorBack(R.color.LiteGreen), OustSdkTools.getColorBack(R.color.Orange),
                    OustSdkTools.getColorBack(R.color.LiteGreen));
            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    if (!searchOn) {
                        createAssessmentList(OustAppState.getInstance().getMyAssessmentList());
                    }
                    swipeRefreshLayout.setRefreshing(false);
                }
            });
        } catch (Exception e) {
        }
    }

    private void showLoader() {
        try {
            swipeRefreshLayout.setColorSchemeColors(OustSdkTools.getColorBack(R.color.Orange),
                    OustSdkTools.getColorBack(R.color.LiteGreen), OustSdkTools.getColorBack(R.color.Orange),
                    OustSdkTools.getColorBack(R.color.LiteGreen));
            swipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    swipeRefreshLayout.setRefreshing(true);
                }
            });
        } catch (Exception e) {
        }
    }


    private Menu menu;

    @Override
    public void onCreateOptionsMenu(Menu menu1, MenuInflater inflater) {
        try {
            this.menu = menu1;
            super.onCreateOptionsMenu(menu, inflater);
            MenuItem searchItem = menu.findItem(R.id.action_search);
            if ((OustStaticVariableHandling.getInstance().getNewViewPager().getCurrentItem() == 1)) {
                searchItem.setVisible(false);
            }

            //OustStaticVariableHandling.getInstance().setNewSearchView((CustomSearchView) MenuItemCompat.getActionView(searchItem));
            OustStaticVariableHandling.getInstance().setNewSearchView((CustomSearchView) searchItem.getActionView());

            OustStaticVariableHandling.getInstance().getNewSearchView().setOnQueryTextListener(this);
            OustStaticVariableHandling.getInstance().getNewSearchView().setOnSearchViewCollapsedEventListener(this);
            OustStaticVariableHandling.getInstance().getNewSearchView().setOnSearchViewExpandedEventListener(this);
            OustStaticVariableHandling.getInstance().getNewSearchView().setQueryHint(getResources().getString(R.string.search_text));
            OustStaticVariableHandling.getInstance().getNewSearchView().setVisibility(View.VISIBLE);
            OustStaticVariableHandling.getInstance().getNewSearchView().requestFocusFromTouch();
            showSearchIcon();
            newLandingInterface.showAlertIcon();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public boolean getUserVisibleHint() {
        searchOn = false;
        return super.getUserVisibleHint();
    }

    private void showSearchIcon() {
        try {
            if (menu != null) {
                MenuItem searchItem = menu.findItem(R.id.action_search);
                if ((OustStaticVariableHandling.getInstance().getNewViewPager().getCurrentItem() == 1)) {
                    if (OustAppState.getInstance().getMyAssessmentList().size() > 4) {
                        searchItem.setVisible(true);
                    } else {
                        searchItem.setVisible(false);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            if (newLandingCoursesAdapter != null) {
                newLandingCoursesAdapter.enableBtn();
            }
            if (startCreatingList) {
                if ((OustAppState.getInstance().getMyDeskList() != null) && (OustAppState.getInstance().getMyDeskList().size() > 0)) {
                    createList();
                }
            } else {

            }

        } catch (Exception e) {
        }
    }

    //deligate methode called on search box text change
    @Override
    public boolean onQueryTextSubmit(String newText) {
        if (!newText.isEmpty()) {
        }
        return false;
    }

    private String searchText = "";

    @Override
    public boolean onQueryTextChange(String newText) {
        try {
            onSearchText(newText);
//            if (newText.isEmpty()) {
//                searchText="";
//                searchOn=false;
//                createAssessmentList(OustAppState.getInstance().getMyAssessmentList());
//            } else {
//                searchText=newText;
//                searchOn=true;
//                filterList(newText);
//            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        return true;
    }

    public void onSearchText(String newText) {
        try {
            if (newText.isEmpty()) {
                searchText = "";
                searchOn = false;
                createList();
            } else {
                searchText = newText;
                searchOn = true;
                filterList(newText);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void onSearchViewCollapsed() {
        try {
            newLandingInterface.showBanner();
            newLandingInterface.isSearchOn(false);
        } catch (Exception e) {
        }
    }

    @Override
    public void onSearchViewExpanded() {
        try {
            courseAssessmentType = 0;
            newLandingInterface.showAllCourseUI();
            newLandingInterface.hideBanner();
            newLandingInterface.isSearchOn(true);
            createAssessmentList(OustAppState.getInstance().getMyAssessmentList());
        } catch (Exception e) {
        }
    }

    public void filterList(String searchText) {
        try {
            List<CommonLandingData> assessmentFirebaseClassesFilter = new ArrayList<>();
            if ((OustAppState.getInstance().getMyAssessmentList() != null) && (OustAppState.getInstance().getMyAssessmentList().size() > 0)) {
                AssessmentFilter cr = new AssessmentFilter();
                assessmentFirebaseClassesFilter = cr.meetCriteria(OustAppState.getInstance().getMyAssessmentList(), searchText);
            }
            createAssessmentList(assessmentFirebaseClassesFilter);
            if (assessmentFirebaseClassesFilter.size() == 0) {
                int totalModules = OustPreferences.getSavedInt("totalAssessmentAvailable");
                if (OustAppState.getInstance().getMyAssessmentList().size() < totalModules - 1) {
                    dataLoaderNotifier.getNextData("ASSESSMENT");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private NewLandingCoursesAdapter newLandingCoursesAdapter;

    public void createAssessmentList(List<CommonLandingData> assessmentList) {
        try {
            courseloader_ll.setVisibility(View.GONE);
            if (((assessmentList != null) && (assessmentList.size() > 0))) {
                showSearchIcon();
                nocourse_text.setVisibility(View.GONE);
                newlanding_recyclerview.setVisibility(View.VISIBLE);
                if (newLandingCoursesAdapter == null) {
                    newLandingCoursesAdapter = new NewLandingCoursesAdapter(assessmentList, 1);
                    if (OustStaticVariableHandling.getInstance().getIsNewLayout() == 1) {
                        GridLayoutManager mLayoutManager1 = new GridLayoutManager(getActivity(), 2);
                        newlanding_recyclerview.setLayoutManager(mLayoutManager1);
                    } else {
                        MyCustomLayoutManager mLayoutManager = new MyCustomLayoutManager(getActivity());
                        mLayoutManager.setExtraLayoutSpace(1200);
                        newlanding_recyclerview.setLayoutManager(mLayoutManager);
                    }
                    newlanding_recyclerview.setItemAnimator(new DefaultItemAnimator());
                    newLandingCoursesAdapter.setDataLoaderNotifier(dataLoaderNotifier);
                    newLandingCoursesAdapter.setTotalModules(OustPreferences.getSavedInt("totalAssessmentAvailable"));
                    newLandingCoursesAdapter.setSendCertificateCallBack(LandingAssessmentFragment.this);
                    newLandingCoursesAdapter.setRowClickCallBack(LandingAssessmentFragment.this);
                    newLandingCoursesAdapter.setPageLoader(LandingAssessmentFragment.this);
                    newlanding_recyclerview.setAdapter(newLandingCoursesAdapter);
                } else {
                    newLandingCoursesAdapter.setTotalModules(OustPreferences.getSavedInt("totalAssessmentAvailable"));
                    newLandingCoursesAdapter.notifyDataChanges(assessmentList);
                }
            } else {
                if (!searchOn) {
                    nocourse_text.setText(getResources().getString(R.string.no_assessment_assigned));
                } else {
                    if (courseAssessmentType == 1) {
                        nocourse_text.setText(getResources().getString(R.string.noassessment_completed_str));
                    } else if (courseAssessmentType == 2) {
                        if (OustPreferences.getSavedInt("assessmentPendingCount") > 0) {
                            dataLoaderNotifier.getNextData("ASSESSMENT");
                        }
                        nocourse_text.setText(getResources().getString(R.string.noassessment_pending_str));
                    } else {
                        nocourse_text.setText(getResources().getString(R.string.no_match_found));
                    }
                }
                nocourse_text.setVisibility(View.VISIBLE);
                newlanding_recyclerview.setVisibility(View.GONE);
            }
        } catch (Exception e) {
        }
        hideLoader();
    }

    private void hideLoader() {
        swipeRefreshLayout.setRefreshing(false);
        swipeRefreshLayout.setVisibility(View.GONE);
    }


    //----------------------------------------------------------------------------------------------------
    @Override
    public void onMainRowClick(final String name, int position) {
        if (!OustSdkTools.checkInternetStatus()) {
            return;
        }
        final View popUpView = getActivity().getLayoutInflater().inflate(R.layout.hide_course_popup, null); // inflating popup layout
        final PopupWindow challengeGamePopUp = OustSdkTools.createPopUp(popUpView);
        final LinearLayout course_removemain_layout = popUpView.findViewById(R.id.course_removemain_layout);
        final Button btnYes = popUpView.findViewById(R.id.btnYes);
        final Button btnNo = popUpView.findViewById(R.id.btnNo);
        final ImageButton btnClose = popUpView.findViewById(R.id.btnClose);
        TextView popupTitle = popUpView.findViewById(R.id.txtTitle);
        TextView popupContent = popUpView.findViewById(R.id.txtRejectChallengeMsg);
        popupTitle.setText(getResources().getString(R.string.archiveassessment_header));
        popupContent.setText(getResources().getString(R.string.archiveassesssment_content));
        btnYes.setText(getResources().getString(R.string.yes));
        btnNo.setText(getResources().getString(R.string.no));

        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                challengeGamePopUp.dismiss();
                OustSdkTools.showProgressBar();
                sendArchiveRequest(name);
            }
        });

        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                challengeGamePopUp.dismiss();
            }
        });
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                challengeGamePopUp.dismiss();
            }
        });
        OustSdkTools.popupAppearEffect(course_removemain_layout);
    }


    public void sendArchiveRequest(String id) {
        String getPointsUrl = OustSdkApplication.getContext().getResources().getString(R.string.archiveassessment_url);
        getPointsUrl = getPointsUrl.replace("{userId}", activeUser.getStudentid());
        getPointsUrl = getPointsUrl.replace("{assessmentId}", id);
        try {
            getPointsUrl = HttpManager.getAbsoluteUrl(getPointsUrl);

            ApiCallUtils.doNetworkCall(Request.Method.PUT, getPointsUrl, OustSdkTools.getRequestObject(null), new ApiCallUtils.NetworkCallback() {
                @Override
                public void onResponse(JSONObject response) {
                    Gson gson = new Gson();
                    CommonResponse commonResponse = gson.fromJson(response.toString(), CommonResponse.class);
                    archiveListOver(commonResponse);
                }

                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });


            /*JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.PUT, getPointsUrl, OustSdkTools.getRequestObject(null), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Gson gson = new Gson();
                    CommonResponse commonResponse=gson.fromJson(response.toString(),CommonResponse.class);
                    archiveListOver(commonResponse);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String>  params = new HashMap<String, String>();
                    try {
                        params.put("api-key", OustPreferences.get("api_key"));
                        params.put("org-id", OustPreferences.get("tanentid"));
                    } catch (Exception e) {
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    }
                    return params;
                }
            };
            jsonObjReq.setShouldCache(false);
            jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(15000, 0, 100000f));
            OustSdkApplication.getInstance().addToRequestQueue(jsonObjReq, "first");*/
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void archiveListOver(CommonResponse commonResponse) {
        OustSdkTools.hideProgressbar();
        try {
            if (commonResponse != null) {
                if (!commonResponse.isSuccess()) {

                } else {
                    if ((commonResponse.getError() != null) && (!commonResponse.getError().isEmpty())) {
                        OustSdkTools.showToast(commonResponse.getError());
                    }
                }
            } else {
                OustSdkTools.showToast(getResources().getString(R.string.retry_internet_msg));
            }
        } catch (Exception e) {
        }
    }

    @Override
    public void sendCertificate(String id, boolean isCollection) {
        sendCertificatePopup(id);
    }

    private RelativeLayout certificateloader_layout;
    private ProgressBar certificate_loader;

    public void showCertificateLoader() {
        try {
            certificateloader_layout.setVisibility(View.VISIBLE);
            Animation rotateAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.rotate_anim);
            certificate_loader.startAnimation(rotateAnim);
            certificateloader_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });
        } catch (Exception e) {
        }
    }

    public void hideCertificateLoader() {
        try {
            certificateloader_layout.setVisibility(View.GONE);
            certificate_loader.setAnimation(null);
        } catch (Exception e) {
        }
    }

    private PopupWindow certificateemail_popup;

    public void sendCertificatePopup(final String id) {
        try {
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    View popUpView = getActivity().getLayoutInflater().inflate(R.layout.certificateemail_popup, null);
                    certificateemail_popup = OustSdkTools.createPopUp(popUpView);

                    final Button btnOK = popUpView.findViewById(R.id.otp_okbtn);
                    final ImageButton certificatepopup_btnClose = popUpView.findViewById(R.id.certificatepopup_btnClose);
                    final EditText edittext_email = popUpView.findViewById(R.id.edittext_email);
                    final TextView certifucate_titletxt = popUpView.findViewById(R.id.certifucate_titletxt);
                    certifucate_titletxt.setText(getResources().getString(R.string.your_certificate));
                    certificateloader_layout = popUpView.findViewById(R.id.certificateloader_layout);
                    certificate_loader = popUpView.findViewById(R.id.certificate_loader);
                    edittext_email.requestFocus();
                    if (OustAppState.getInstance().getActiveUser().getEmail() != null) {
                        edittext_email.setText(OustAppState.getInstance().getActiveUser().getEmail());
                    }
                    InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                    btnOK.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            OustSdkTools.oustTouchEffect(view, 200);
                            if (isValidEmail(edittext_email.getText().toString().trim())) {
                                showCertificateLoader();
                                sendCertificatetomail(edittext_email.getText().toString().trim(), id);
                            } else {
                                OustSdkTools.showToast(getResources().getString(R.string.enter_valid_mail));
                            }
                        }
                    });

                    certificatepopup_btnClose.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            certificateemail_popup.dismiss();
                        }
                    });
                    LinearLayout certificateanim_layout = popUpView.findViewById(R.id.certificateanim_layout);
                    OustSdkTools.popupAppearEffect(certificateanim_layout);
                }
            }, 500);
        } catch (Exception e) {
            //OustFlurryTools.LogFlurryErrorEvent(this.getClass().getName(), new Throwable().fillInStackTrace().getStackTrace()[1].getMethodName(), e);
        }
    }

    public void sendCertificatetomail(String mail, String id) {


        try {
            SendCertificateRequest sendCertificateRequest = new SendCertificateRequest();
            sendCertificateRequest.setStudentid(activeUser.getStudentid());
            sendCertificateRequest.setEmailid(mail);
            sendCertificateRequest.setContentType("ASSESSMENT");

            hitCertificateRequestUrl(sendCertificateRequest);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void hitCertificateRequestUrl(final SendCertificateRequest sendCertificateRequest) {
        final CommonResponse[] commonResponses = {null};
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("studentid", sendCertificateRequest.getStudentid());
            jsonObject.put("emailid", sendCertificateRequest.getEmailid());
            jsonObject.put("contentType", sendCertificateRequest.getContentType());

            JSONObject jsonParams = OustSdkTools.getRequestObjectforJSONObject(jsonObject);

            String sendcertificate_url = OustSdkApplication.getContext().getResources().getString(R.string.sendcertificate_url);
            sendcertificate_url = HttpManager.getAbsoluteUrl(sendcertificate_url);

            /*VolleyRequest volleyReq = new VolleyRequest(getActivity(), sendcertificate_url, jsonObject.toString(), new ServerResponseListener() {
                @Override
                public void onSuccess(JSONObject jsonObject) {
                    hideCertificateLoader();
                    commonResponses[0] = OustSdkTools.getCommonResponse(jsonObject.toString());
                    if (commonResponses[0] != null)
                        gotCertificatetomailResponce(commonResponses[0]);
                }

                @Override
                public void onSuccess(JSONObject jsonObject, int requestType) {

                }

                @Override
                public void onError(String error) {
                    hideCertificateLoader();
                    Log.d("send certificate error ", "onFailure: PUT REST Call: " + " Failed");
                }

            });
            volleyReq.executeToServer();*/

            ApiCallUtils.doNetworkCall(Request.Method.POST, sendcertificate_url, jsonObject, new ApiCallUtils.NetworkCallback() {
                @Override
                public void onResponse(JSONObject response) {
                    hideCertificateLoader();
                    commonResponses[0] = OustSdkTools.getCommonResponse(response.toString());
                    if (commonResponses[0] != null)
                        gotCertificatetomailResponce(commonResponses[0]);
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    hideCertificateLoader();
                    Log.d("send certificate error ", "onFailure: PUT REST Call: " + " Failed");
                }
            });

        } catch (Exception e) {
            Log.d("", "", e);
        }
    }

    public void gotCertificatetomailResponce(CommonResponse commonResponse) {
        try {
            hideCertificateLoader();
            if ((commonResponse != null)) {
                if (commonResponse.isSuccess()) {
                    if ((certificateemail_popup != null) && (certificateemail_popup.isShowing())) {
                        certificateemail_popup.dismiss();
                    }
                    OustSdkTools.showToast(getResources().getString(R.string.certificate_sent_mail));
                } else {
                    if (commonResponse.getError() != null) {
                        OustSdkTools.showToast(commonResponse.getError());
                    }
                }
            } else {
                OustSdkTools.showToast(getResources().getString(R.string.retry_internet_msg));
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public boolean isValidEmail(CharSequence target) {
        Pattern EMAIL_ADDRESS
                = Pattern.compile(
                "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                        "\\@" +
                        "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                        "(" +
                        "\\." +
                        "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                        ")+");
        return EMAIL_ADDRESS.matcher(target).matches();
    }

    @Override
    public void onPageLoad() {
        try {
            if (!OustDataHandler.getInstance().isAllAssessmentLoaded())
                courseloader_ll.setVisibility(View.VISIBLE);
            else
                courseloader_ll.setVisibility(View.GONE);
        } catch (Exception e) {
        }
    }
}

