package com.oustme.oustsdk.activity.common;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.provider.Settings;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;

import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.clevertap.android.sdk.CleverTapAPI;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.adapter.common.FormFillingAdapter;
import com.oustme.oustsdk.base.BaseActivity;
import com.oustme.oustsdk.firebase.common.OustFirebaseTools;
import com.oustme.oustsdk.interfaces.common.Formfill_callback;
import com.oustme.oustsdk.model.request.TrainingRequestModel;
import com.oustme.oustsdk.request.FormFillRequest;
import com.oustme.oustsdk.response.course.CommonResponse;
import com.oustme.oustsdk.tools.ActiveUser;
import com.oustme.oustsdk.tools.HttpManager;
import com.oustme.oustsdk.tools.OustAppState;
import com.oustme.oustsdk.tools.OustGATools;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.appconstants.AppConstants;
import com.oustme.oustsdk.util.ApiCallUtils;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.oustme.oustsdk.tools.appconstants.AppConstants.StringConstants.FEED_BACK_NAME;
import static com.oustme.oustsdk.tools.appconstants.AppConstants.StringConstants.TOOL_BAR_COLOR_CODE;
import static com.oustme.oustsdk.tools.appconstants.AppConstants.StringConstants.USER_DATA;

/**
 * Created by shilpysamaddar on 09/03/17.
 */

public class FormFillingActivity extends BaseActivity implements
        ActivityCompat.OnRequestPermissionsResultCallback, Formfill_callback {

    private MenuItem oust;
    private ImageView rotate_camera;
    private RecyclerView form_recyclerview;
    private Toolbar mToolbar;
    private FrameLayout camera_frame;
    private RelativeLayout camera_image_view_layout, cameramain_layout, done_buttonlayout, loader_layout;
    private TextView done_button;
    private ProgressBar formfill_loader;
    private GoogleApiClient mGoogleApiClient;
    private FormFillingAdapter formAdapter;
    private FormFillRequest formFillRequest;
    private boolean isFrontcamera = true;
    private List<String> mPurposeList;
    private DatabaseReference mDatabaseReference;
    private ActiveUser activeUser;

    @Override
    protected int getContentView() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        return R.layout.activity_form;
    }

    public void initFromActivity() {
        try {
            formFillRequest = new FormFillRequest();
            if (OustSdkTools.checkInternetStatus()) {
                getPurposeList();
            }
            activeUser = OustAppState.getInstance().getActiveUser();
            if ((activeUser != null) && (activeUser.getStudentid() != null)) {
            } else {
                HttpManager.setBaseUrl();
                OustFirebaseTools.initFirebase();
                String activeUserGet = OustPreferences.get(USER_DATA);
                activeUser = OustSdkTools.getActiveUserData(activeUserGet);
                formFillRequest.setStudentid(activeUser.getStudentid());
                // formFillRequest.set
            }
            setToolBarColor();
            // getLoactionOfUser();
            long currenttimestamp = System.currentTimeMillis();
            formFillRequest = new FormFillRequest();
            formFillRequest.setMobile("" + activeUser.getUserMobile());
            formFillRequest.setStudentid("" + activeUser.getStudentid());
            formFillRequest.setTenantId(OustPreferences.get(AppConstants.StringConstants.TENANT_ID));

            if (formAdapter == null) {
                formAdapter = new FormFillingAdapter(currenttimestamp, formFillRequest, this, mPurposeList);
                formAdapter.setFormfill_callback(FormFillingActivity.this);
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(FormFillingActivity.this);
                form_recyclerview.setLayoutManager(mLayoutManager);
                form_recyclerview.setItemAnimator(new DefaultItemAnimator());
                form_recyclerview.setAdapter(formAdapter);
            }

            loader_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });
            camera_image_view_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                }
            });

            rotate_camera.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isFrontcamera) {
                        isFrontcamera = false;
                        camera_frame.removeView(mCameraPreview);
                        mCameraPreview = null;
                        mCamera = null;
                        cameraBtnClicked();
                    } else {
                        isFrontcamera = true;
                        camera_frame.removeView(mCameraPreview);
                        mCameraPreview = null;
                        mCamera = null;
                        cameraBtnClicked();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            Log.d("formfill", e.getMessage());
        }
    }

    private void setToolBarColor() {
        try {
            setSupportActionBar(mToolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("");
            TextView actionBarTitle = mToolbar.findViewById(R.id.title);
            String titleText = OustPreferences.get(FEED_BACK_NAME);
            actionBarTitle.setText(titleText.toUpperCase());
            String mToolbarColorCode = OustPreferences.get(TOOL_BAR_COLOR_CODE);
            if ((mToolbarColorCode != null) && (!mToolbarColorCode.isEmpty())) {
                mToolbar.setBackgroundColor(Color.parseColor(mToolbarColorCode));
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void getPurposeList() {
        mDatabaseReference = OustFirebaseTools.getRootRef().child(AppConstants.StringConstants.SYSTEM_NODE).child(AppConstants.StringConstants.HELP_SUPPORT_NODE);
        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mPurposeList = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    mPurposeList.add(snapshot.getValue(String.class));
                }
                if (mPurposeList != null && mPurposeList.size() > 0) {
                    long currenttimestamp = System.currentTimeMillis();
                    formAdapter = new FormFillingAdapter(currenttimestamp, formFillRequest, FormFillingActivity.this, mPurposeList);
                    formAdapter.setFormfill_callback(FormFillingActivity.this);
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(FormFillingActivity.this);
                    form_recyclerview.setLayoutManager(mLayoutManager);
                    form_recyclerview.setItemAnimator(new DefaultItemAnimator());
                    form_recyclerview.setAdapter(formAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void showLoader() {
        try {
            loader_layout.setVisibility(View.VISIBLE);
//            Animation rotateAnim = AnimationUtils.loadAnimation(FormFillingActivity.this, R.anim.rotate_anim);
//            formfill_loader.startAnimation(rotateAnim);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }


    public void hideLoader() {
        try {
            loader_layout.setVisibility(View.GONE);
            formfill_loader.setAnimation(null);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            if (mGoogleApiClient != null) {
                mGoogleApiClient.disconnect();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }


    @Override
    protected void initView() {
        try {
            OustSdkTools.setLocale(FormFillingActivity.this);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        rotate_camera = findViewById(R.id.rotate_camera);
        form_recyclerview = findViewById(R.id.form_recyclerview);
        mToolbar = findViewById(R.id.tabanim_toolbar);
        camera_frame = findViewById(R.id.camera_frame);
        camera_image_view_layout = findViewById(R.id.camera_image_view_layout);
        done_button = findViewById(R.id.done_button);
        cameramain_layout = findViewById(R.id.cameramain_layout);
        done_buttonlayout = findViewById(R.id.done_buttonlayout);
        loader_layout = findViewById(R.id.loader_layout);
        formfill_loader = findViewById(R.id.formfill_loader);
//        OustGATools.getInstance().reportPageViewToGoogle(FormFillingActivity.this, "Feedback Page");
    }

    @Override
    protected void initData() {
        initFromActivity();
    }

    @Override
    protected void initListener() {

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        if ((camera_image_view_layout.getVisibility() == View.VISIBLE)) {
            try {
                camera_frame.removeView(mCameraPreview);
                mCameraPreview = null;
                mCamera = null;
                camera_image_view_layout.setVisibility(View.GONE);
            } catch (Exception e) {
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }
        } else if (loader_layout.getVisibility() == View.VISIBLE) {

        } else {
//            if(formFillRequest!=null){
//                if(formFillRequest.getComments()!=null && formFillRequest.getComments().length()>0){
//                    if(formFillRequest.getMobile()!=null && formFillRequest.getMobile().length()>0){
//                        RealmHelper.addFeedToRealm(formFillRequest);
//                    }
//                }
//            }
            super.onBackPressed();
        }
    }

    // LocationManager locationManager;
    //private final int RC_PERM_GET_LOCATION = 1;
    private final int RC_PERM_GET_CAMERA = 2;

   /* private void getLoactionOfUser() {
        try {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    if (mGoogleApiClient == null) {
                        mGoogleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
                    }
                    mGoogleApiClient.connect();
                } else {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                showGpsEnabledPopup();
                            } catch (Exception e) {
                                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                            }
                        }
                    }, FIVE_HUNDRED_MILLI_SECONDS);
                }
            } else {
                ActivityCompat.requestPermissions(FormFillingActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, RC_PERM_GET_LOCATION);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }*/

   /* private void showGpsEnabledPopup() {
        try {
            final View popUpView = getLayoutInflater().inflate(R.layout.add_module_success_popup, null);
            // inflating popup layout
            final PopupWindow gpsEnablePopup = OustSdkTools.createPopUp(popUpView);
            ImageButton closeBtn = popUpView.findViewById(R.id.closeaddModuleSuccessPopupButton);
            TextView infoTxt = popUpView.findViewById(R.id.infoTxt);
            Button gotoSettingBtn = popUpView.findViewById(R.id.mystpryChallengebtn);
            Button anotherBtn = popUpView.findViewById(R.id.friendChallengeBtn);

            infoTxt.setText(getResources().getString(R.string.enable_gps));
            gotoSettingBtn.setText(getResources().getString(R.string.go_to_setting));

            anotherBtn.setVisibility(View.GONE);
            closeBtn.setVisibility(View.VISIBLE);

            gotoSettingBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    gpsEnablePopup.dismiss();
                    FormFillingActivity.this.finish();
                 *//*   Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);*//*
                }
            });

            closeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if ((gpsEnablePopup != null) && (gpsEnablePopup.isShowing())) {
                        gpsEnablePopup.dismiss();
                    }
                }
            });

            popUpView.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                        if ((gpsEnablePopup != null) && (gpsEnablePopup.isShowing())) {
                            gpsEnablePopup.dismiss();
                            FormFillingActivity.this.finish();
                        }
                        return true;
                    }
                    return false;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }*/


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        try {
          /*  if (requestCode == RC_PERM_GET_LOCATION) {
                if(grantResults!=null) {
                    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        getLoactionOfUser();
                    }
                }
            }else */
            if (requestCode == RC_PERM_GET_CAMERA) {
                if (grantResults != null) {
                    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        cameraBtnClicked();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }


    /*private void gotLoaction(Location location) {
        try {
            setLatAndLog(location);
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            Geocoder geocoder = new Geocoder(OustSdkApplication.getContext());
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null) {
                try {
                    for (Address address : addresses) {
                        setAddressRequest(address);
                        formAdapter.onFormDataChange(address, formFillRequest);
                    }
                } catch (Exception e) {
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void setAddressRequest(Address address) {
        try {
            if (address.getCountryName() != null) {
                formFillRequest.setCountry(address.getCountryName());
            }
            if (address.getSubAdminArea() != null) {
                formFillRequest.setCity(address.getSubAdminArea());
            }
            if (address.getLocality() != null) {
                formFillRequest.setArea(address.getLocality());
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void setLatAndLog(Location location) {
        try {
            formFillRequest.setLatitude((long) location.getLatitude());
            formFillRequest.setLongitude((long) location.getLongitude());
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }*/

    @Override
    public void userAgeChnage(String username) {
        formFillRequest.setMobile(username);
    }

    @Override
    public void usernameChange(String username) {

    }

    @Override
    public void userGenderChnage(String gender) {
    }

    @Override
    public void userPurposeChange(String purpose) {
        formFillRequest.setPurpose(purpose);
    }

    @Override
    public void userCommentChanges(String comment) {
        formFillRequest.setComments(comment);
    }

    @Override
    public void clickOnCameraBtn() {
        cameraBtnClicked();
    }

    @Override
    public void clickOnSaveBtn() {
        if ((imageString != null) && (!imageString.isEmpty())) {
            formFillRequest.setPhoto(imageString);
        }
        if ((formFillRequest.getComments() == null) || (formFillRequest.getComments() != null) && (formFillRequest.getComments().isEmpty())) {
            OustSdkTools.showToast(getResources().getString(R.string.report_required_message));
            return;
        }

        if ((formFillRequest.getMobile() != null) && ((formFillRequest.getMobile().length() > 5)) && (formFillRequest.getMobile().length() < 14)) {
        } else {
            OustSdkTools.showToast(getResources().getString(R.string.enter_mobile_number));
            return;
        }
        showLoader();
        if (formFillRequest.getPurpose().contains("Training Request")) {
            sendTrainingRequest();
        } else {
            sendFormRequest();
        }

    }

    public void sendTrainingRequest() {
        String sendform_reques_url = OustSdkApplication.getContext().getResources().getString(R.string.send_training_request);
        Gson gson = new GsonBuilder().create();
        TrainingRequestModel trainingRequestModel = new TrainingRequestModel();
        // trainingRequestModel.setPurpose(formFillRequest.getPurpose());
        trainingRequestModel.setStudentid(formFillRequest.getStudentid());
        // trainingRequestModel.setTimeOfRequest(System.currentTimeMillis());
        trainingRequestModel.setOrgId(OustPreferences.get(AppConstants.StringConstants.TENANT_ID));
        trainingRequestModel.setTrainingRequestData(formFillRequest.getComments());
        String jsonParams = gson.toJson(trainingRequestModel);
        try {
            sendform_reques_url = HttpManager.getAbsoluteUrl(sendform_reques_url);

            ApiCallUtils.doNetworkCall(Request.Method.PUT, sendform_reques_url, OustSdkTools.getRequestObject(jsonParams), new ApiCallUtils.NetworkCallback() {
                @Override
                public void onResponse(JSONObject response) {
                    Gson gson = new Gson();
                    CommonResponse commonResponse = gson.fromJson(response.toString(), CommonResponse.class);
                    sendRquestOver(commonResponse);
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    sendRquestOver(null);
                }
            });


            /*JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.PUT, sendform_reques_url, OustSdkTools.getRequestObject(jsonParams), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Gson gson = new Gson();
                    CommonResponse commonResponse=gson.fromJson(response.toString(),CommonResponse.class);
                    sendRquestOver(commonResponse);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    sendRquestOver(null);
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

    public void sendFormRequest() {
        String sendform_reques_url = OustSdkApplication.getContext().getResources().getString(R.string.sendform_reques_url);
        Gson gson = new GsonBuilder().create();
        String jsonParams = gson.toJson(formFillRequest);
        try {
            sendform_reques_url = HttpManager.getAbsoluteUrl(sendform_reques_url);

            ApiCallUtils.doNetworkCall(Request.Method.POST, sendform_reques_url, OustSdkTools.getRequestObject(jsonParams), new ApiCallUtils.NetworkCallback() {
                @Override
                public void onResponse(JSONObject response) {
                    Gson gson = new Gson();
                    CommonResponse commonResponse = gson.fromJson(response.toString(), CommonResponse.class);
                    sendRquestOver(commonResponse);
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    sendRquestOver(null);
                }
            });


            /*JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, sendform_reques_url, OustSdkTools.getRequestObject(jsonParams), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Gson gson = new Gson();
                    CommonResponse commonResponse=gson.fromJson(response.toString(),CommonResponse.class);
                    sendRquestOver(commonResponse);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    sendRquestOver(null);
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

    public void sendRquestOver(CommonResponse commonResponse) {
        try {
            hideLoader();
            if (commonResponse != null) {
                if (commonResponse.isSuccess()) {
                    try {
                        CleverTapAPI clevertapDefaultInstance = CleverTapAPI.getDefaultInstance(OustSdkApplication.getContext());
                        HashMap<String, Object> eventUpdate = OustSdkTools.getCleverTapEventData();
                        eventUpdate.put("ClickedOnHelp/Support", true);
                        eventUpdate.put("SubmittedQuery", formFillRequest.getComments());
                        eventUpdate.put("UploadedImage", formFillRequest.getPhoto());
                        Log.d(TAG, "CleverTap instance: " + eventUpdate.toString());
                        if (clevertapDefaultInstance != null) {
                            clevertapDefaultInstance.pushEvent("Help_Support_Clicks_Submit", eventUpdate);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    }
                    FormFillingActivity.this.finish();
                } else {
                    if ((commonResponse.getError() != null) && (!commonResponse.getError().isEmpty())) {
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

    //------------------------------
    private Camera mCamera;
    private CameraPreview mCameraPreview;
    private String imageString;

    public void cameraBtnClicked() {
        try {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                camera_image_view_layout.setVisibility(View.VISIBLE);
                mCamera = getCameraInstance();
                if (isFrontcamera) {
                    mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
                } else {
                    mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
                }
                mCamera.setDisplayOrientation(90);
                Camera.Parameters parameters = mCamera.getParameters();
                List<Camera.Size> sizes = parameters.getSupportedPictureSizes();
                Camera.Size size = sizes.get(0);
                for (int i = 0; i < sizes.size(); i++) {
                    if (sizes.get(i).width > size.width) {
                        size = sizes.get(i);
                    }
                }
                parameters.setPictureSize(size.width, size.height);
                mCamera.setParameters(parameters);
                setLayoutAspectRatiosmall();
                mCameraPreview = new CameraPreview(this, mCamera);
                camera_frame.addView(mCameraPreview);
                done_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            OustSdkTools.oustTouchEffect(done_button, 100);
                            mCamera.takePicture(null, null, mPicture);
                        } catch (Exception e) {
                        }
                    }
                });
            } else {
                ActivityCompat.requestPermissions(FormFillingActivity.this, new String[]{Manifest.permission.CAMERA}, RC_PERM_GET_CAMERA);
            }

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void setLayoutAspectRatiosmall() {
        try {
            DisplayMetrics metrics = this.getResources().getDisplayMetrics();
            int w = metrics.widthPixels;
            int scrHeight = metrics.heightPixels;
            int size = (int) getResources().getDimension(R.dimen.oustlayout_dimen40);
            int scrWidth = (w) - size;
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) cameramain_layout.getLayoutParams();
            float f1 = (float) (scrWidth * 1.22);
            params.height = (int) f1;
            cameramain_layout.setLayoutParams(params);

            RelativeLayout.LayoutParams params1 = (RelativeLayout.LayoutParams) done_buttonlayout.getLayoutParams();
            float f2 = (float) (scrWidth * 0.22);
            params1.height = (int) f2;
            done_buttonlayout.setLayoutParams(params1);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }


    private Camera getCameraInstance() {
        Camera camera = null;
        try {
            if (isFrontcamera) {
                camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
            } else {
                camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
            }
        } catch (Exception e) {
            // cannot get camera or does not exist
        }
        return camera;
    }

    Camera.PictureCallback mPicture = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            try {
                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                camera_image_view_layout.setVisibility(View.GONE);
                if ((bitmap.getWidth() > bitmap.getHeight()) || (bitmap.getHeight() == bitmap.getWidth())) {
                    Matrix matrix = new Matrix();
                    matrix.postRotate(-90);
                    bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                }
                if (!isFrontcamera) {
                    Matrix matrix = new Matrix();
                    matrix.postRotate(-180);
                    bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                }
                Bitmap bitmap1 = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getWidth());
                try {
                    Matrix matrix = new Matrix();
                    float[] mirrorY = {-1, 0, 0, 0, 1, 0, 0, 0, 1};
                    Matrix matrixMirrorY = new Matrix();
                    matrixMirrorY.setValues(mirrorY);
                    matrix.postConcat(matrixMirrorY);
                    Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap1, 0, 0, bitmap1.getWidth(), bitmap1.getHeight(), matrix, true);
                    bitMapToString(rotatedBitmap);
                    formAdapter.onFormImageChange(rotatedBitmap, formFillRequest);
                    camera_frame.removeView(mCameraPreview);
                    mCameraPreview = null;
                    mCamera = null;
                } catch (Exception e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }
            } catch (Exception e) {
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }
        }
    };

    public void bitMapToString(Bitmap bitmap) {
        try {
            if ((bitmap.getWidth() > 250) && (bitmap.getHeight() > 250)) {
                bitmap = Bitmap.createScaledBitmap(bitmap, 250, 250, false);
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] b = baos.toByteArray();
            imageString = Base64.encodeToString(b, Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {

    }

    public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
        private SurfaceHolder mSurfaceHolder;
        private Camera mCamera;

        // Constructor that obtains context and camera
        @SuppressWarnings("deprecation")
        public CameraPreview(Context context, Camera camera) {
            super(context);
            try {
                this.mCamera = camera;
                this.mSurfaceHolder = this.getHolder();
                this.mSurfaceHolder.addCallback(this);
                this.mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
            } catch (Exception e) {
            }
        }

        @Override
        public void surfaceCreated(SurfaceHolder surfaceHolder) {
            try {
                mCamera.setPreviewDisplay(surfaceHolder);
                mCamera.startPreview();
            } catch (Exception e) {
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
            try {
                mCamera.stopPreview();
                mCamera.release();
            } catch (Exception e) {
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }
        }

        @Override
        public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int width, int height) {
            // start preview with new settings
            try {
                mCamera.setPreviewDisplay(surfaceHolder);
                mCamera.startPreview();
            } catch (Exception e) {
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                // intentionally left blank for a test
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        try {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.historymenu, menu);
            oust = menu.findItem(R.id.oust);
            Drawable drawable = OustSdkTools.getImageDrawable(getResources().getString(R.string.whiteboy));
            oust.setIcon(drawable);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (itemId == R.id.oust) {
            finish();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }


}
