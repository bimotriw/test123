package com.oustme.oustsdk.fragments.courses;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.core.view.MenuItemCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import androidx.appcompat.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.adapter.courses.NewLandingCoursesCollectionAdapter;
import com.oustme.oustsdk.customviews.CustomSearchView;
import com.oustme.oustsdk.firebase.common.FirebaseRefClass;
import com.oustme.oustsdk.firebase.common.OustFirebaseTools;
import com.oustme.oustsdk.firebase.course.CourseCollectionData;
import com.oustme.oustsdk.interfaces.common.FavouriteCallback;
import com.oustme.oustsdk.interfaces.common.NewLandingInterface;
import com.oustme.oustsdk.interfaces.common.RowClickCallBack;
import com.oustme.oustsdk.response.common.Questions;
import com.oustme.oustsdk.tools.ActiveUser;
import com.oustme.oustsdk.tools.OustAppState;
import com.oustme.oustsdk.tools.OustStaticVariableHandling;
import com.oustme.oustsdk.tools.OustStrings;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.filters.CourseCollectionFilter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by shilpysamaddar on 09/03/17.
 */

public class NewLandingCourseCollectionFragment  extends Fragment implements SearchView.OnQueryTextListener,
        CustomSearchView.OnSearchViewCollapsedEventListener,CustomSearchView.OnSearchViewExpandedEventListener,
        RowClickCallBack,FavouriteCallback {

    private RecyclerView newlanding_recyclerview;
    private TextView nocourse_text;
    private RelativeLayout reflayout;
    private SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayout currentcoursedetail_layout;
    private TextView currentcourse_name,loading_text;

    //courseCollectionCount for load more feature

    private List<CourseCollectionData> courseCollectionDataList=new ArrayList<>();
    private List<CourseCollectionData> courseCollectionDataListFilter;

    private ActiveUser activeUser;
    private boolean searchOn=false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.newlanding_fragment, container, false);
        initViews(view);
        initLanding();
        return view;
    }

    private void initViews(View view) {

        newlanding_recyclerview= view.findViewById(R.id.newlanding_recyclerview);
        nocourse_text= view.findViewById(R.id.nocourse_text);
        reflayout= view.findViewById(R.id.reflayout);
        swipeRefreshLayout= view.findViewById(R.id.swipe_refresh_layout);
        loading_text= view.findViewById(R.id.loading_text);

        loading_text.setText(OustStrings.getString("loading"));
    }

    //interface to callback landingActivity
    private NewLandingInterface newLandingInterface;
    public void setNewLandingInterface(NewLandingInterface newLandingInterface) {
        this.newLandingInterface = newLandingInterface;
    }


    public void initLanding(){
        activeUser = OustAppState.getInstance().getActiveUser();
        createLoader();
        showLoader();
        setHasOptionsMenu(true);
        getUserCorseCollectionData();
    }


    //create loader with SwipeRefreshLayout and show
    private void createLoader(){
        try {
            swipeRefreshLayout.setColorSchemeColors(OustSdkTools.getColorBack(R.color.Orange),
                    OustSdkTools.getColorBack(R.color.LiteGreen), OustSdkTools.getColorBack(R.color.Orange), OustSdkTools.getColorBack(R.color.LiteGreen));
            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    if(!searchOn) {
                        createCourseCollectionList(courseCollectionDataList);
                    }
                    swipeRefreshLayout.setRefreshing(false);
                }
            });
        }catch (Exception e){}
    }

    private void showLoader(){
        try {
            swipeRefreshLayout.setColorSchemeColors(OustSdkTools.getColorBack(R.color.Orange),
                    OustSdkTools.getColorBack(R.color.LiteGreen), OustSdkTools.getColorBack(R.color.Orange), OustSdkTools.getColorBack(R.color.LiteGreen));
            swipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    swipeRefreshLayout.setRefreshing(true);
                }
            });
        }catch (Exception e){}
    }


    //inflate search icon and show if count is above 4
    private Menu menu;
    @Override
    public void onCreateOptionsMenu(Menu menu1, MenuInflater inflater) {
        try {
            this.menu=menu1;
            super.onCreateOptionsMenu(menu, inflater);
            MenuItem searchItem = menu.findItem(R.id.action_search);
            if((OustStaticVariableHandling.getInstance().getNewViewPager().getCurrentItem()==1)) {
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
        }catch (Exception e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void showSearchIcon(){
        try {
            MenuItem searchItem = menu.findItem(R.id.action_search);
            if((OustStaticVariableHandling.getInstance().getNewViewPager().getCurrentItem()==1)) {
                int collectionSize=0;
                if(courseCollectionDataList!=null){
                    collectionSize=courseCollectionDataList.size();
                }
                if(collectionSize>4){
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
            if((courseCollectionDataListFilter!=null)&&(courseCollectionDataListFilter.size()>0)) {
                createCourseCollectionList(courseCollectionDataListFilter);
            }else {
                if((courseCollectionDataList!=null)&&(courseCollectionDataList.size()>0)) {
                    createCourseCollectionList(courseCollectionDataList);
                }
            }
        }catch (Exception e){}
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser){
            createCourseCollectionList(courseCollectionDataList);
        }
    }

    //deligate methode called on search box text change
    @Override
    public boolean onQueryTextSubmit(String newText) {
        if (!newText.isEmpty()) {}
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        try {
            if (newText.isEmpty()) {
                searchOn=false;
                createCourseCollectionList(courseCollectionDataList);
            } else {
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
        }catch (Exception e){}
    }

    @Override
    public void onSearchViewExpanded() {
        try{
            newLandingInterface.hideBanner();
        }catch (Exception e){}
    }


    public void filterList(String searchText){
        try {
            courseCollectionDataListFilter=new ArrayList<>();
            if((courseCollectionDataList!=null)&&(courseCollectionDataList.size()>0)) {
                CourseCollectionFilter cr = new CourseCollectionFilter();
                courseCollectionDataListFilter = cr.meetCriteria(courseCollectionDataList, searchText);
            }
            createCourseCollectionList(courseCollectionDataListFilter);
        }catch (Exception e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void getUserCorseCollectionData(){
        try{
            final String message="/landingPage/"+activeUser.getStudentKey()+"/courseColn";
            ValueEventListener myassessmentListener=new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    try {
                        if(dataSnapshot.getValue()!=null) {
                            List<Object> coursecollection_list = new ArrayList<>();
                            Map<String, Object> coursecollection_mainmap = new HashMap<>();
                            Object o1 = dataSnapshot.getValue();
                            courseCollectionDataList = new ArrayList<>();
                            if (o1.getClass().equals(ArrayList.class)) {
                                coursecollection_list = (List<Object>) o1;
                                final int[] index = new int[]{0};
                                for (int i = 0; i < coursecollection_list.size(); i++) {
                                    if ((coursecollection_list.get(i) != null)) {
                                        final Map<String, Object> courseCollectionMap = (Map<String, Object>) coursecollection_list.get(i);
                                        if (courseCollectionMap != null) {
                                            CourseCollectionData courseCollectionData=new CourseCollectionData();
                                            if(courseCollectionMap.get("addedOn")!=null){
                                                courseCollectionData.setAddedOn((String) courseCollectionMap.get("addedOn"));
                                            }
                                            if(courseCollectionMap.get("currentCourseId")!=null){
                                                courseCollectionData.setCurrentCourseId((long) courseCollectionMap.get("currentCourseId"));
                                            }
                                            if(courseCollectionMap.get("purchased")!=null){
                                                courseCollectionData.setPurchased((boolean) courseCollectionMap.get("purchased"));
                                            }
                                            if (courseCollectionMap.get("userOC") != null) {
                                                courseCollectionData.setUserOc((long) courseCollectionMap.get("userOC"));
                                            }
                                            courseCollectionData.setCollectionId(i);
                                            if (courseCollectionMap.get("userCompletionPercentage") != null) {
                                                Object o3 = courseCollectionMap.get("userCompletionPercentage");
                                                if (o3.getClass().equals(Long.class)) {
                                                    courseCollectionData.setCompletePresentage((long) o3);
                                                } else if (o3.getClass().equals(String.class)) {
                                                    String s3 = (String) o3;
                                                    courseCollectionData.setCompletePresentage(Long.parseLong(s3));
                                                } else if (o3.getClass().equals(Double.class)) {
                                                    Double s3 = (Double) o3;
                                                    long l = (new Double(s3)).longValue();
                                                    courseCollectionData.setCompletePresentage(l);
                                                }
                                            }
                                        }
                                    }
                                }
                            }else if (o1.getClass().equals(HashMap.class)) {
                                coursecollection_mainmap=(Map<String,Object>) dataSnapshot.getValue();
                                if(coursecollection_mainmap!=null) {
                                    for (String courseCollectionKey : coursecollection_mainmap.keySet()) {
                                        if ((coursecollection_mainmap.get(courseCollectionKey) != null)) {
                                            final Map<String, Object> courseCollectionMap = (Map<String, Object>) coursecollection_mainmap.get(courseCollectionKey);
                                            if (courseCollectionMap != null) {
                                                CourseCollectionData courseCollectionData=new CourseCollectionData();
                                                if(courseCollectionMap.get("addedOn")!=null){
                                                    courseCollectionData.setAddedOn((String) courseCollectionMap.get("addedOn"));
                                                }
                                                if(courseCollectionMap.get("currentCourseId")!=null){
                                                    courseCollectionData.setCurrentCourseId((long) courseCollectionMap.get("currentCourseId"));
                                                }
                                                if(courseCollectionMap.get("purchased")!=null){
                                                    courseCollectionData.setPurchased((boolean) courseCollectionMap.get("purchased"));
                                                }
                                                if (courseCollectionMap.get("userOC") != null) {
                                                    courseCollectionData.setUserOc((long) courseCollectionMap.get("userOC"));
                                                }
                                                if(courseCollectionMap.get("rating")!=null){
                                                    courseCollectionData.setRating((long) courseCollectionMap.get("rating"));
                                                }
                                                if (courseCollectionMap.get("userCompletionPercentage") != null) {
                                                    Object o3 = courseCollectionMap.get("userCompletionPercentage");
                                                    if (o3.getClass().equals(Long.class)) {
                                                        courseCollectionData.setCompletePresentage((long) o3);
                                                    } else if (o3.getClass().equals(String.class)) {
                                                        String s3 = (String) o3;
                                                        courseCollectionData.setCompletePresentage(Long.parseLong(s3));
                                                    } else if (o3.getClass().equals(Double.class)) {
                                                        Double s3 = (Double) o3;
                                                        long l = (new Double(s3)).longValue();
                                                        courseCollectionData.setCompletePresentage(l);
                                                    }
                                                }
                                                courseCollectionData.setCollectionId(Long.parseLong(courseCollectionKey));
                                                courseCollectionDataList.add(courseCollectionData);
                                            }
                                        }
                                    }
                                }
                            }
                            setCollectionSingletonListener();
                        }
                    }catch (Exception e){
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    }
                }
                @Override
                public void onCancelled(DatabaseError error) {}
            };
            DatabaseReference gameHistoryRef = OustFirebaseTools.getRootRef().child(message);
            Query query = gameHistoryRef.orderByChild("addedOn");
            query.keepSynced(true);
            query.addValueEventListener(myassessmentListener);
            OustAppState.getInstance().getFirebaseRefClassList().add(new FirebaseRefClass(myassessmentListener, message));
        }catch (Exception e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void setCollectionSingletonListener(){
        try {
            final int[] index = new int[]{0};
            for(int i=0;i<courseCollectionDataList.size();i++) {
                String collectionLinkMsg = "courseCollection/courseColn" + courseCollectionDataList.get(i).getCollectionId();
                ValueEventListener eventListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot student) {
                        try {
                            if (null != student.getValue()) {
                                index[0]++;
                                final Map<String, Object> collectionMap = (Map<String, Object>) student.getValue();
                                long currentId = 0;
                                int courseCollctionNo = 0;
                                if (collectionMap.get("courseColnId") != null) {
                                    currentId = (long) collectionMap.get("courseColnId");
                                }
                                for (int n = 0; n < courseCollectionDataList.size(); n++) {
                                    if (courseCollectionDataList.get(n).getCollectionId() == currentId) {
                                        courseCollctionNo = n;
                                    }
                                }
                                if (collectionMap.get("banner") != null) {
                                    courseCollectionDataList.get(courseCollctionNo).setBanner((String) collectionMap.get("banner"));
                                }
                                if (collectionMap.get("name") != null) {
                                    courseCollectionDataList.get(courseCollctionNo).setName((String) collectionMap.get("name"));
                                }
                                if (collectionMap.get("numEnrolledUsers") != null) {
                                    courseCollectionDataList.get(courseCollctionNo).setNumEnrolledUsers((long) collectionMap.get("numEnrolledUsers"));
                                }
                                if (collectionMap.get("courseCollectionTime") != null) {
                                    courseCollectionDataList.get(courseCollctionNo).setCourseCollectionTime((long) collectionMap.get("courseCollectionTime"));
                                }
                                if (collectionMap.get("rating") != null) {
                                    courseCollectionDataList.get(courseCollctionNo).setRating((long) collectionMap.get("rating"));
                                }
                            } else {
                                String courseID = student.getKey();
                                courseID = courseID.replace("courseColn", "");
                                for (int k = 0; k < courseCollectionDataList.size(); k++) {
                                    if (("" + (courseCollectionDataList.get(k).getCollectionId())).equalsIgnoreCase(courseID)) {
                                        courseCollectionDataList.remove(k);
                                    }
                                }
                            }
                            if (index[0] >= courseCollectionDataList.size()) {
                                createCourseCollectionList(courseCollectionDataList);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                    }
                };
                OustFirebaseTools.getRootRef().child(collectionLinkMsg).addListenerForSingleValueEvent(eventListener);
                OustFirebaseTools.getRootRef().child(collectionLinkMsg).keepSynced(true);
            }
        }catch (Exception e){}
    }


    private NewLandingCoursesCollectionAdapter newLandingCoursesCollectionAdapter;
    public void createCourseCollectionList( List<CourseCollectionData> courseCollectionDataList1){
        try {
            if (((courseCollectionDataList1 != null) && (courseCollectionDataList1.size() > 0))) {
                showSearchIcon();
                courseCollectionDataListFilter = courseCollectionDataList;
                nocourse_text.setVisibility(View.GONE);
                newlanding_recyclerview.setVisibility(View.VISIBLE);
                if (newLandingCoursesCollectionAdapter == null) {
                    newLandingCoursesCollectionAdapter = new NewLandingCoursesCollectionAdapter(getActivity(), courseCollectionDataList1);
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
                    newlanding_recyclerview.setLayoutManager(mLayoutManager);
                    newlanding_recyclerview.setItemAnimator(new DefaultItemAnimator());
                    newLandingCoursesCollectionAdapter.setRowClickCallBack(NewLandingCourseCollectionFragment.this);
                    newlanding_recyclerview.setAdapter(newLandingCoursesCollectionAdapter);
                } else {
                    newLandingCoursesCollectionAdapter.notifyDateChanges(courseCollectionDataList1);
                }
            } else {
                if(searchOn){
                    nocourse_text.setText(OustStrings.getString("no_match_found"));
                }else {
                    nocourse_text.setText(OustStrings.getString("no_assessment_assigned"));
                }
                nocourse_text.setVisibility(View.VISIBLE);
                newlanding_recyclerview.setVisibility(View.GONE);
            }
            swipeRefreshLayout.setRefreshing(false);
        }catch (Exception e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }


    //sort courseCollectionList in reverse order based on addedOn date
    public Comparator<CourseCollectionData> courseCollectionListReverseSorter = new Comparator<CourseCollectionData>() {
        public int compare(CourseCollectionData s1, CourseCollectionData s2) {
            if(s1.getAddedOn()!=null&&s2.getAddedOn()!=null) {
                String source1 = s1.getAddedOn().toUpperCase();
                String source2 = s2.getAddedOn().toUpperCase();
                return source2.compareTo(source1);
            }else{
                return  0;
            }
        }
    };

    @Override
    public void onMainRowClick(String name,int position) {}

    @Override
    public void onFavouriteCallback(Questions questions) {

    }

    @Override
    public void onReachToEnd() {

    }
}



