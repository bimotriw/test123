package com.oustme.oustsdk.fragments.courses;

import android.content.Context;
import android.os.Bundle;
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
import com.google.firebase.database.DataSnapshot;
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
import com.oustme.oustsdk.tools.filters.CourseFilter;
import com.oustme.oustsdk.util.ApiCallUtils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by admin on 02/12/17.
 */

public class LandingCourseFragment extends Fragment implements SearchView.OnQueryTextListener,
        CustomSearchView.OnSearchViewCollapsedEventListener,
        CustomSearchView.OnSearchViewExpandedEventListener,RowClickCallBack,SendCertificateCallBack,PageLoader {

    private RecyclerView newlanding_recyclerview;
    private TextView nocourse_text,loading_text;
    private RelativeLayout reflayout,ffcbannerref_imglayout,courseloader_ll;
    private SwipeRefreshLayout swipeRefreshLayout;

    private ActiveUser activeUser;
    private boolean searchOn=false;
    private String TAG="NewLandingFragment";
    public int courseShowType=0;
    private boolean startCreatingList=false;
    private NewLandingInterface newLandingInterface;

    public void setNewLandingInterface(NewLandingInterface newLandingInterface) {
        this.newLandingInterface = newLandingInterface;
    }
    private DataLoaderNotifier dataLoaderNotifier;
    public void setDataLoaderNotifier(DataLoaderNotifier dataLoaderNotifier){
        this.dataLoaderNotifier=dataLoaderNotifier;
    }


//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setHasOptionsMenu(true);
//        setRetainInstance(true);
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view= inflater.inflate(R.layout.newlanding_fragment, container, false);
        initViews(view);
        initLanding(view);
        return view;
    }
//==============================================
 //UI methodes
    private void initViews(View view) {
        newlanding_recyclerview= view.findViewById(R.id.newlanding_recyclerview);
        nocourse_text= view.findViewById(R.id.nocourse_text);
        reflayout= view.findViewById(R.id.reflayout);
        swipeRefreshLayout= view.findViewById(R.id.swipe_refresh_layout);
        ffcbannerref_imglayout= view.findViewById(R.id.ffcbannerref_imglayout);
        courseloader_ll= view.findViewById(R.id.courseloader_ll);
        loading_text= view.findViewById(R.id.loading_text);

        loading_text.setText(OustStrings.getString("loading"));
    }

    private void setf3cBannerLayout(){
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
        }catch (Exception e){
        }
    }
//start loading course data
    public void initLanding(View view){
        try {
            activeUser = OustAppState.getInstance().getActiveUser();
            Log.e(TAG," inside initLanding() "+activeUser.getUserDisplayName());
            createLoader();
            showLoader();
            setRetainInstance(true);
            setHasOptionsMenu(true);
            courseShowType=0;
            if(OustStaticVariableHandling.getInstance().getIsNewLayout()==1) {
                courseShowType=2;
                searchOn=true;
                newlanding_recyclerview = view.findViewById(R.id.newlayoutlanding_recyclerview);
                newLandingInterface.showPendingCoursesUI();
            }
            if(OustAppState.getInstance().isLandingFragmentInit()){
                if ((OustAppState.getInstance().getMyDeskList() != null) && (OustAppState.getInstance().getMyDeskList().size() > 0)) {
                    startCreatingList=true;
                    createList();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }
//-------------------------------------------
    //use swipe refresh layout as loader
    private void showLoader(){
        try {
            swipeRefreshLayout.setColorSchemeColors(OustSdkTools.getColorBack(R.color.Orange), OustSdkTools.getColorBack(R.color.LiteGreen), OustSdkTools.getColorBack(R.color.Orange), OustSdkTools.getColorBack(R.color.LiteGreen));
            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    swipeRefreshLayout.setRefreshing(false);
                }
            });
        }catch (Exception e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void createLoader(){
        try {
            swipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    swipeRefreshLayout.setRefreshing(true);
                }
            });
        }catch (Exception e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }


//-----------------------------------------
    private Menu menu;
    @Override
    public void onCreateOptionsMenu(Menu menu1, MenuInflater inflater) {
        try {
            this.menu=menu1;
            super.onCreateOptionsMenu(menu, inflater);
            MenuItem searchItem = menu.findItem(R.id.action_search);
            if((OustStaticVariableHandling.getInstance().getNewViewPager().getCurrentItem()==0)) {
                searchItem.setVisible(false);
            }
            //OustStaticVariableHandling.getInstance().setNewSearchView((CustomSearchView) MenuItemCompat.getActionView(searchItem));
            OustStaticVariableHandling.getInstance().setNewSearchView((CustomSearchView) searchItem.getActionView());
            OustStaticVariableHandling.getInstance().getNewSearchView().setOnQueryTextListener(this);
            OustStaticVariableHandling.getInstance().getNewSearchView().setOnSearchViewCollapsedEventListener(this);
            OustStaticVariableHandling.getInstance().getNewSearchView().setOnSearchViewExpandedEventListener(this);
            OustStaticVariableHandling.getInstance().getNewSearchView().setQueryHint(OustStrings.getString("search_text"));
            OustStaticVariableHandling.getInstance().getNewSearchView().setVisibility(View.VISIBLE);
            OustStaticVariableHandling.getInstance().getNewSearchView().requestFocusFromTouch();
            showSearchIcon();
            newLandingInterface.showAlertIcon();
        }catch (Exception e){

        }
    }

    //show search icon if courses size is >4
    private void showSearchIcon(){
        try {
            MenuItem searchItem = menu.findItem(R.id.action_search);
            if((OustStaticVariableHandling.getInstance().getNewViewPager().getCurrentItem()==0)) {
                if(OustAppState.getInstance().getMyDeskList().size()>4){
                    searchItem.setVisible(true);
                }else {
                    searchItem.setVisible(false);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        try {
            if(newLandingCoursesAdapter!=null){
                newLandingCoursesAdapter.enableBtn();
            }
            if(startCreatingList) {
                if ((OustAppState.getInstance().getMyDeskList() != null) && (OustAppState.getInstance().getMyDeskList().size() > 0)) {
                    createList();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            Log.e(TAG,"caught exception in onResume ",e);
        }

    }

    //deligate methode called on search box text change
    @Override
    public boolean onQueryTextSubmit(String newText) {
        if (!newText.isEmpty()) {}
        return false;
    }

    private String searchText="";
    @Override
    public boolean onQueryTextChange(String newText) {
        try {
            if (newText.isEmpty()) {
                searchText="";
                searchOn=false;
                createList();
            } else {
                searchText=newText;
                searchOn=true;
                filterList(newText);
            }
        }catch (Exception e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        return true;
    }
    @Override
    public void onSearchViewCollapsed() {
        try{
            newLandingInterface.showBanner();
            newLandingInterface.isSearchOn(false);
        }catch (Exception e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void onSearchViewExpanded() {
        try{
            courseShowType=0;
            newLandingInterface.showAllCourseUI();
            newLandingInterface.hideBanner();
            newLandingInterface.isSearchOn(true);
            createList();
        }catch (Exception e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void filterList(String searchText){
        try {
            List<CommonLandingData> allCourcesFilter=new ArrayList<>();
            if((OustAppState.getInstance().getMyDeskList()!=null)&&(OustAppState.getInstance().getMyDeskList().size()>0)) {
                CourseFilter courseFilter = new CourseFilter();
                allCourcesFilter = courseFilter.meetCriteria(OustAppState.getInstance().getMyDeskList(), searchText);
            }
            if(allCourcesFilter.size()==0){
                int totalModules=OustPreferences.getSavedInt("totalCourseAvailable");
                if (OustAppState.getInstance().getMyDeskList().size() < totalModules - 1) {
                    dataLoaderNotifier.getNextData("COURSE");
                }
            }
            createCourseList(allCourcesFilter);
        }catch (Exception e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void filterData(int type){
        if(type==1){
            showCompletedCourses();
        }else if(type==2){
            showPendingCourses();
        }else if(type==4){
            if(searchOn){
                if(!searchText.isEmpty()){
                    filterList(searchText);
                }else{
                    createMainList();
                }
            }else {
                createMainList();
            }
        }else {
            startCreateList();
        }
    }


    public void startCreateList(){
        try{
            courseShowType=0;
            createList();
        }catch (Exception e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void showCompletedCourses(){
        searchOn=true;
        List<CommonLandingData> allCourcesFilter=new ArrayList<>();
        if((OustAppState.getInstance().getMyDeskList()!=null)&&(OustAppState.getInstance().getMyDeskList().size()>0)) {
            CourseFilter courseFilter = new CourseFilter();
            allCourcesFilter = courseFilter.completedCourseMeetCriteria(OustAppState.getInstance().getMyDeskList());
        }
        courseShowType=1;
        if(allCourcesFilter.size()==0){
            int totalModules=OustPreferences.getSavedInt("totalCourseAvailable");
            if (OustAppState.getInstance().getMyDeskList().size() < totalModules - 1) {
                dataLoaderNotifier.getNextData("COURSE");
            }
        }
        OustPreferences.saveintVar("totalCourseAvailable", allCourcesFilter.size());
        createCourseList(allCourcesFilter);
    }
    public void showPendingCourses(){
        searchOn=true;
        List<CommonLandingData> allCourcesFilter=new ArrayList<>();
        if((OustAppState.getInstance().getMyDeskList()!=null)&&(OustAppState.getInstance().getMyDeskList().size()>0)) {
            CourseFilter courseFilter = new CourseFilter();
            allCourcesFilter = courseFilter.pendingCourseMeetCriteria(OustAppState.getInstance().getMyDeskList());
        }
        if(allCourcesFilter.size()==0){
            int totalModules=OustPreferences.getSavedInt("totalCourseAvailable");
            if (OustAppState.getInstance().getMyDeskList().size() < totalModules - 1) {
                dataLoaderNotifier.getNextData("COURSE");
            }
        }
        OustPreferences.saveintVar("totalCourseAvailable", allCourcesFilter.size());
        courseShowType=2;
        if((allCourcesFilter.size()>0)||(OustDataHandler.getInstance().isPaginationReachToEnd())) {
            createCourseList(allCourcesFilter);
        }
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    private DataSnapshot myDataSnapshot;

//=================================================================================================
    public void createMainList() {
        try {
            setf3cBannerLayout();
            startCreatingList=true;
//            Collections.sort(OustAppState.getInstance().getMyDeskList(), sortByDate);
            createList();
        }catch (Exception e){}
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

    private void createList(){
        if (!searchOn) {
            OustPreferences.saveintVar("totalCourseAvailable", OustAppState.getInstance().getMyDeskList().size());
            createCourseList(OustAppState.getInstance().getMyDeskList());
        } else {
            if(courseShowType==1){
                showCompletedCourses();
            }else if(courseShowType==2){
                showPendingCourses();
            } else {
                filterList(searchText);
            }
        }
    }
    private NewLandingCoursesAdapter newLandingCoursesAdapter;
    public void createCourseList( List<CommonLandingData> courseDataClassList){
        try {
            Log.e(TAG,"inside createCourseList() ");
            courseloader_ll.setVisibility(View.GONE);
            if ((courseDataClassList != null) && (courseDataClassList.size() > 0)) {
                showSearchIcon();
                nocourse_text.setVisibility(View.GONE);
                newlanding_recyclerview.setVisibility(View.VISIBLE);
                if (newLandingCoursesAdapter == null) {
                    OustAppState.getInstance().setLandingFragmentInit(true);
                    newLandingCoursesAdapter = new NewLandingCoursesAdapter(courseDataClassList,0);
                    if(OustStaticVariableHandling.getInstance().getIsNewLayout()==1) {
                        GridLayoutManager mLayoutManager1 = new GridLayoutManager(getActivity(), 2);
                        newlanding_recyclerview.setLayoutManager(mLayoutManager1);
                    }else {
                        MyCustomLayoutManager mLayoutManager = new MyCustomLayoutManager(getActivity());
                        mLayoutManager.setExtraLayoutSpace(1200);
                        newlanding_recyclerview.setLayoutManager(mLayoutManager);
                    }
                    newlanding_recyclerview.setItemAnimator(new DefaultItemAnimator());
                    newLandingCoursesAdapter.setDataLoaderNotifier(dataLoaderNotifier);
                    newLandingCoursesAdapter.setPageLoader(LandingCourseFragment.this);
                    newLandingCoursesAdapter.setRowClickCallBack(LandingCourseFragment.this);
                    newLandingCoursesAdapter.setSendCertificateCallBack(LandingCourseFragment.this);
                    newLandingCoursesAdapter.setTotalModules(OustPreferences.getSavedInt("totalCourseAvailable"));
                    newlanding_recyclerview.setAdapter(newLandingCoursesAdapter);
                } else {
                    newLandingCoursesAdapter.setTotalModules(OustPreferences.getSavedInt("totalCourseAvailable"));
                    newLandingCoursesAdapter.notifyDataChanges( courseDataClassList);
                }
            } else {
                if(!searchOn){
                    nocourse_text.setText(OustStrings.getString("no_course_assign"));
                } else {
                    if(courseShowType==1){
                        nocourse_text.setText(OustStrings.getString("nocourse_completed_str"));
                    }else if(courseShowType==2){
                        nocourse_text.setText(OustStrings.getString("nocourse_pending_str"));
                    } else {
                        nocourse_text.setText(OustStrings.getString("no_match_found"));
                    }
                }
                nocourse_text.setVisibility(View.VISIBLE);
                newlanding_recyclerview.setVisibility(View.GONE);
            }
        }catch (Exception e){
            Log.e(TAG,"inside createCourseList() exception caught",e);
        }
        hideLoader();
    }

    private void hideLoader(){
        swipeRefreshLayout.setRefreshing(false);
        swipeRefreshLayout.setVisibility(View.GONE);
    }

    //=====================================================================================================
    //===============================================================================================================
    //methodes to archive course
    @Override
    public void onMainRowClick(final String name, final int position) {
        if(!OustSdkTools.checkInternetStatus()){
            return;
        }
        try{
            final View popUpView = getActivity().getLayoutInflater().inflate(R.layout.hide_course_popup, null); // inflating popup layout
            final PopupWindow challengeGamePopUp = OustSdkTools.createPopUp(popUpView);
            final LinearLayout course_removemain_layout= popUpView.findViewById(R.id.course_removemain_layout);
            final Button btnYes = popUpView.findViewById(R.id.btnYes);
            final Button btnNo = popUpView.findViewById(R.id.btnNo);
            final ImageButton btnClose = popUpView.findViewById(R.id.btnClose);
            TextView popupTitle = popUpView.findViewById(R.id.txtTitle);
            TextView popupContent = popUpView.findViewById(R.id.txtRejectChallengeMsg);
            popupTitle.setText(OustStrings.getString("archivecourse_header"));
            popupContent.setText(OustStrings.getString("archivecourse_content"));

            btnYes.setText(OustStrings.getString("yes"));
            btnNo.setText(OustStrings.getString("no"));

            btnYes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    OustSdkTools.showProgressBar();
                    challengeGamePopUp.dismiss();
                    if(OustSdkTools.checkInternetStatus()){
                        sendArchiveRequest(name,position);
                    }
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
        }catch (Exception e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

    }

    public void sendArchiveRequest(String id, final int position){
        if(id.contains("COURSE")){
            id=id.replace("COURSE","");
        }
        String getPointsUrl = OustSdkApplication.getContext().getResources().getString(R.string.archivecourse_url);
        getPointsUrl = getPointsUrl.replace("{userId}",activeUser.getStudentid());
        getPointsUrl = getPointsUrl.replace("{courseId}",id);
        try {
            getPointsUrl = HttpManager.getAbsoluteUrl(getPointsUrl);

            ApiCallUtils.doNetworkCall(Request.Method.PUT, getPointsUrl, OustSdkTools.getRequestObject(""), new ApiCallUtils.NetworkCallback() {
                @Override
                public void onResponse(JSONObject response) {
                    Gson gson = new Gson();
                    CommonResponse commonResponse=gson.fromJson(response.toString(),CommonResponse.class);
                    archiveListOver(commonResponse,position);
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    archiveListOver(null,0);
                }
            });


            /*JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.PUT, getPointsUrl, OustSdkTools.getRequestObject(null), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Gson gson = new Gson();
                    CommonResponse commonResponse=gson.fromJson(response.toString(),CommonResponse.class);
                    archiveListOver(commonResponse,position);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    archiveListOver(null,0);
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
        }catch (Exception e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void archiveListOver(CommonResponse commonResponse, int position){
        OustSdkTools.hideProgressbar();
        try{
            if (commonResponse != null) {
                if(commonResponse.isSuccess()){
                    newLandingCoursesAdapter.notifyItemRemoved(position);
                }else {
                    if((commonResponse.getError()!=null)&&(!commonResponse.getError().isEmpty())){
                        OustSdkTools.showToast(commonResponse.getError());
                    }
                }
            } else {
                OustSdkTools.showToast(OustStrings.getString("retry_internet_msg"));
            }
        }catch (Exception e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }
    //====================================================================================================
//send certificate
    @Override
    public void sendCertificate(final String id,final boolean isCollection) {
        sendCertificatePopup(id,isCollection);
    }

    private RelativeLayout certificateloader_layout;
    private ProgressBar certificate_loader;
    public void showCertificateLoader(){
        try {
            certificateloader_layout.setVisibility(View.VISIBLE);
            Animation rotateAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.rotate_anim);
            certificate_loader.startAnimation(rotateAnim);
            certificateloader_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {}
            });
        }catch (Exception e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }
    public void hideCertificateLoader(){
        try {
            certificateloader_layout.setVisibility(View.GONE);
            certificate_loader.setAnimation(null);
        }catch (Exception e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }
    private PopupWindow certificateemail_popup;
    public void sendCertificatePopup(final String id,final boolean isCollection) {
        try {
            View popUpView = getActivity().getLayoutInflater().inflate(R.layout.certificateemail_popup, null);
            certificateemail_popup = OustSdkTools.createPopUp(popUpView);

            final Button btnOK = popUpView.findViewById(R.id.otp_okbtn);
            final ImageButton certificatepopup_btnClose = popUpView.findViewById(R.id.certificatepopup_btnClose);
            final EditText edittext_email = popUpView.findViewById(R.id.edittext_email);
            final TextView certifucate_titletxt = popUpView.findViewById(R.id.certifucate_titletxt);
            certifucate_titletxt.setText(OustStrings.getString("your_certificate"));
            certificateloader_layout= popUpView.findViewById(R.id.certificateloader_layout);
            certificate_loader= popUpView.findViewById(R.id.certificate_loader);
            if (OustAppState.getInstance().getActiveUser().getEmail() != null) {
                edittext_email.setText(OustAppState.getInstance().getActiveUser().getEmail());
            }
            InputMethodManager inputMethodManager=(InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
            edittext_email.requestFocus();
            btnOK.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    OustSdkTools.oustTouchEffect(view,200);
                    if(isValidEmail(edittext_email.getText().toString().trim())){
                        showCertificateLoader();
                        sendCertificatetomail(edittext_email.getText().toString().trim(),id,isCollection);
                    }else {
                        OustSdkTools.showToast(OustStrings.getString("enter_valid_mail"));
                    }
                }
            });

            certificatepopup_btnClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    hideKeyboard(edittext_email);
                    certificateemail_popup.dismiss();
                }
            });
            LinearLayout certificateanim_layout= popUpView.findViewById(R.id.certificateanim_layout);
            OustSdkTools.popupAppearEffect(certificateanim_layout);
        }catch (Exception e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);//OustFlurryTools.LogFlurryErrorEvent(this.getClass().getName(), new Throwable().fillInStackTrace().getStackTrace()[1].getMethodName(), e);
        }
    }
    public void hideKeyboard(View v){
        try{
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }catch (Exception e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void gotCertificatetomailResponce(CommonResponse commonResponse){
        try{
            hideCertificateLoader();
            if((commonResponse!=null)){
                if(commonResponse.isSuccess()) {
                    if ((certificateemail_popup != null) && (certificateemail_popup.isShowing())) {
                        certificateemail_popup.dismiss();
                    }
                    OustSdkTools.showToast(OustStrings.getString("certificate_sent_mail"));
                }else {
                    if(commonResponse.getError()!=null){
                        OustSdkTools.showToast(commonResponse.getError());
                    }
                }
            }else {
                OustSdkTools.showToast(OustStrings.getString("retry_internet_msg"));
            }
        }catch (Exception e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public  boolean isValidEmail(CharSequence target) {
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

    public void sendCertificatetomail(String mail,String courseId,boolean isCollection){
        try{
            JSONObject jsonObject=new JSONObject();
            jsonObject.put("studentid",activeUser.getStudentid());
            jsonObject.put("emailid",mail);
            if(isCollection){
                jsonObject.put("contentType","COURSE_COLN");
            }else {
                jsonObject.put("contentType","COURSE");
            }
            String sendcertificate_url = OustSdkApplication.getContext().getResources().getString(R.string.sendcertificate_url);
            sendcertificate_url=HttpManager.getAbsoluteUrl(sendcertificate_url);
            /*VolleyRequest volleyRequest=new VolleyRequest(getActivity(), sendcertificate_url, jsonObject.toString(), new ServerResponseListener() {
                @Override
                public void onSuccess(JSONObject jsonObject) {
                    CommonResponse commonResponse= OustSdkTools.getCommonResponse(jsonObject.toString());
                    gotCertificatetomailResponce(commonResponse);
                }

                @Override
                public void onSuccess(JSONObject jsonObject, int requestType) {}

                @Override
                public void onError(String error) {
                    gotCertificatetomailResponce(null);
                }
            });
            volleyRequest.executeToServer();*/

            ApiCallUtils.doNetworkCall(Request.Method.POST, sendcertificate_url, OustSdkTools.getRequestObjectforJSONObject(jsonObject), new ApiCallUtils.NetworkCallback() {
                @Override
                public void onResponse(JSONObject response) {
                    CommonResponse commonResponse= OustSdkTools.getCommonResponse(response.toString());
                    gotCertificatetomailResponce(commonResponse);
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                }
            });

        }catch (Exception e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void onPageLoad() {
        if(!OustDataHandler.getInstance().isAllCoursesLoaded()){
            courseloader_ll.setVisibility(View.VISIBLE);
        } else{
            courseloader_ll.setVisibility(View.GONE);
        }
    }

    public void onSearchText(String newText){
        try{
            courseShowType=0;
            if (newText.isEmpty()) {
                searchText="";
                searchOn=false;
                createList();
            } else {
                searchText=newText;
                searchOn=true;
                filterList(newText);
            }
        }catch(Exception e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }
}





