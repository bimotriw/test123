package com.oustme.oustsdk.fragments.courses;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.adapter.common.FriendProfileRowAdapter;
import com.oustme.oustsdk.firebase.common.OustFirebaseTools;
import com.oustme.oustsdk.response.common.FriendProfileResponceAssessmentRow;
import com.oustme.oustsdk.response.common.FriendProfileResponceRow;
import com.oustme.oustsdk.tools.ActiveUser;
import com.oustme.oustsdk.tools.OustAppState;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.OustStrings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by shilpysamaddar on 21/03/17.
 */

public class UpCourseFragment extends Fragment {

    RecyclerView up_course_recyclerview;
    TextView nocourse_text;

    private List<FriendProfileResponceRow> allCources = new ArrayList<>();
    private ActiveUser activeUser;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.up_course_fragment, container, false);
        initViews(view);
        initLanding();
        return view;
    }

    private void initViews(View view) {
        up_course_recyclerview = view.findViewById(R.id.up_course_recyclerview);
        nocourse_text = view.findViewById(R.id.nocourse_text);
    }

    public void setUserProfileData(List<FriendProfileResponceRow> friendProfileResponceData) {
        if ((friendProfileResponceData != null)) {
            allCources = friendProfileResponceData;
        }
    }

    public void initLanding() {
        try {
            activeUser = OustAppState.getInstance().getActiveUser();
            if ((activeUser != null) && (activeUser.getStudentid() != null)) {
            } else {
                String activeUserGet = OustPreferences.get("userdata");
                activeUser = OustSdkTools.getActiveUserData(activeUserGet);
            }
//            createLoader();
//            showLoader();
            getMyCourcesFromFirebase();
            getMyAssessmentsFromFirebase();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }


    private HashMap<String, String> userCourseKeyMap = new HashMap<>();

    public void getMyCourcesFromFirebase() {
        try {
            final String message = "/landingPage/" + activeUser.getStudentKey() + "/course";
            ValueEventListener myCourceListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    try {
                        if (dataSnapshot.getValue() != null) {
                            userCourseKeyMap = new HashMap<>();
                            List<Object> learningList = new ArrayList<>();
                            Map<String, Object> levelnewMap = new HashMap<>();
                            Object o1 = dataSnapshot.getValue();
                            if (o1.getClass().equals(ArrayList.class)) {
                                learningList = (List<Object>) dataSnapshot.getValue();
                                final int[] index = new int[]{0};
                                for (int i = 0; i < learningList.size(); i++) {
                                    if ((learningList.get(i) != null)) {
                                        final Map<String, Object> lpMap = (Map<String, Object>) learningList.get(i);
                                        if (lpMap != null) {
                                            userCourseKeyMap.put(("" + i), ("" + i));
                                        }
                                    }
                                    if (i == (learningList.size() - 1)) {
                                        createNotificationList();
                                    }
                                }
                            } else if (o1.getClass().equals(HashMap.class)) {
                                final int[] index = new int[]{0};
                                levelnewMap = (Map<String, Object>) dataSnapshot.getValue();
                                int n1 = 0;
                                for (String courseKey : levelnewMap.keySet()) {
                                    n1++;
                                    if ((levelnewMap.get(courseKey) != null)) {
                                        final Map<String, Object> lpMap = (Map<String, Object>) levelnewMap.get(courseKey);
                                        if (lpMap != null) {
                                            try {
                                                userCourseKeyMap.put((courseKey), ("" + courseKey));
                                            } catch (Exception e) {
                                                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                                            }
                                        }
                                        if (n1 == (levelnewMap.size() - 1)) {
                                            createNotificationList();
                                        }
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            };
            OustFirebaseTools.getRootRef().child(message).keepSynced(true);
            OustFirebaseTools.getRootRef().child(message).addListenerForSingleValueEvent(myCourceListener);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void getMyAssessmentsFromFirebase() {
        try {
            final String message = "/landingPage/" + activeUser.getStudentKey() + "/assessment";
            ValueEventListener myCourceListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    try {
                        if (dataSnapshot.getValue() != null) {
                            //userAssessmentsKeyMap=new HashMap<>();
                            List<Object> learningList = new ArrayList<>();
                            Map<String, Object> levelnewMap = new HashMap<>();
                            Object o1 = dataSnapshot.getValue();
                            if (o1.getClass().equals(ArrayList.class)) {
                                learningList = (List<Object>) dataSnapshot.getValue();
                                for (int i = 0; i < learningList.size(); i++) {
                                    if ((learningList.get(i) != null)) {
                                        final Map<String, Object> lpMap = (Map<String, Object>) learningList.get(i);
                                        if (lpMap != null) {
                                            userCourseKeyMap.put(("" + i), ("" + i));
                                        }
                                    }
                                    if (i == (learningList.size() - 1)) {
                                        createNotificationList();
                                    }
                                }
                            } else if (o1.getClass().equals(HashMap.class)) {
                                levelnewMap = (Map<String, Object>) dataSnapshot.getValue();
                                int n1 = 0;
                                for (String courseKey : levelnewMap.keySet()) {
                                    n1++;
                                    if ((levelnewMap.get(courseKey) != null)) {
                                        final Map<String, Object> lpMap = (Map<String, Object>) levelnewMap.get(courseKey);
                                        if (lpMap != null) {
                                            try {
                                                userCourseKeyMap.put((courseKey), (courseKey));
                                            } catch (Exception e) {
                                                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                                            }
                                        }
                                        if (n1 == (levelnewMap.size() - 1)) {
                                            createNotificationList();
                                        }
                                    }
                                }
                            }
                        } else {
                            createNotificationList();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            };
            OustFirebaseTools.getRootRef().child(message).keepSynced(true);
            OustFirebaseTools.getRootRef().child(message).addListenerForSingleValueEvent(myCourceListener);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        if ((allCources != null) && (allCources.size() > 0)) {
            createNotificationList();
        }
    }

    private FriendProfileRowAdapter friendProfileRowAdaptor;

    public void createNotificationList() {
        try {
            //swipeRefreshLayout.setRefreshing(false);
            if ((allCources != null) && (allCources.size() > 0)) {
                nocourse_text.setVisibility(View.GONE);
                up_course_recyclerview.setVisibility(View.VISIBLE);
                if (friendProfileRowAdaptor == null) {
                    friendProfileRowAdaptor = new FriendProfileRowAdapter(getActivity(), new ArrayList<FriendProfileResponceAssessmentRow>(), allCources, userCourseKeyMap, 0);
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
                    up_course_recyclerview.setLayoutManager(mLayoutManager);
                    up_course_recyclerview.setItemAnimator(new DefaultItemAnimator());
                    up_course_recyclerview.setAdapter(friendProfileRowAdaptor);
                } else {
                    friendProfileRowAdaptor.notifyDateChanges(new ArrayList<FriendProfileResponceAssessmentRow>(), allCources, userCourseKeyMap);
                }
            } else {
                nocourse_text.setVisibility(View.VISIBLE);
                nocourse_text.setText(OustStrings.getString("no_course_assign"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

}
