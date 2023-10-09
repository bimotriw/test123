package com.oustme.oustsdk.activity.common;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PorterDuff;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;

import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.customviews.CircleImageView;
import com.oustme.oustsdk.customviews.SettingPicture;
import com.oustme.oustsdk.presenter.common.GeneralSettingActivityPresenter;
import com.oustme.oustsdk.request.UserSettingRequest;
import com.oustme.oustsdk.response.common.Popup;
import com.oustme.oustsdk.response.course.CommonResponse;
import com.oustme.oustsdk.tools.ActiveUser;
import com.oustme.oustsdk.tools.HttpManager;
import com.oustme.oustsdk.tools.OustAppState;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.OustStaticVariableHandling;
import com.oustme.oustsdk.tools.appconstants.AppConstants;
import com.oustme.oustsdk.util.ApiCallUtils;
import com.oustme.oustsdk.utils.OustResourceUtils;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by shilpysamaddar on 22/03/17.
 */

public class GeneralSettingActivity extends AppCompatActivity implements View.OnClickListener {

    private CircleImageView imgAvatarButton, changeAvatar;
    private TextView txtTitle, usergender_Label;
    private EditText UserName;
    private EditText UserDateOfBirth;
    private EditText UserEmail;
    private EditText UserCountry;
    private EditText UserMobileNumber;
    private EditText genstng_userpassword;
    private EditText genstng_userconfirmpassword;
    private EditText genstng_userDOB;
    private AutoCompleteTextView UserCity;
    private RadioGroup GenderSelect, GenderSelect2;
    private int color;
    private RadioButton GenderMale, GenderFemale, GenderTran, GenderOther;
    private Button editpicturecancle_btn, editpicturesave_btn, general_saveBtn, changepassword_btn;
    private ProgressBar generalSetting_progressbar;
    private LinearLayout previewImageView;
    private LinearLayout mainavatar_latout;
    private LinearLayout main_scrollview;
    private LinearLayout generalsetting_layout;
    private LinearLayout general_save_btn_layout;
    private SettingPicture pictureeditView;
    private Button backArrowImgBtn;
    private RelativeLayout backArrow_back;
    private String imageSelectedString = "";
    private boolean usernot = true, imageUpdated = true;
    private Bitmap image;
    private DatePickerDialog DatePickerDialog;
    private final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private Bitmap finalBitmap;
    private GeneralSettingActivityPresenter presenter;
    private String userName = "";
    private boolean disableProfileEdit = false;
    boolean isChecking = true;
    int mCheckedId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        try {
            OustSdkTools.setLocale(GeneralSettingActivity.this);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        setContentView(R.layout.user_general);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            usernot = bundle.getBoolean("usernot");
        }
        getColors();
        initViews();
        initUserGeneral();
//        OustGATools.getInstance().reportPageViewToGoogle(GeneralSettingActivity.this, "User General Setting Page");

    }

    private void initViews() {
        imgAvatarButton = findViewById(R.id.imgAvatarButton);
        changeAvatar = findViewById(R.id.changeAvatar);
        txtTitle = findViewById(R.id.txtTitle);
        UserName = findViewById(R.id.genstng_userName);
        UserDateOfBirth = findViewById(R.id.genstng_userDOB);
        UserEmail = findViewById(R.id.genstng_userEmail);
        UserCity = findViewById(R.id.genstng_userCity);
        UserCountry = findViewById(R.id.genstng_userCountry);
        UserMobileNumber = findViewById(R.id.genstng_userMobNumber);
        GenderSelect = findViewById(R.id.genstng_genderSelect);
        GenderSelect2 = findViewById(R.id.genstng_genderSelect2);
        GenderMale = findViewById(R.id.genstng_userMale);
        GenderFemale = findViewById(R.id.genstng_userFemale);
        GenderTran = findViewById(R.id.genstng_userTrans);
        GenderOther = findViewById(R.id.genstng_userOther);
        usergender_Label = findViewById(R.id.usergender_Label);
        generalSetting_progressbar = findViewById(R.id.generalSetting_progressbar);
        previewImageView = findViewById(R.id.previewImageView);
        pictureeditView = findViewById(R.id.pictureeditView);
        editpicturecancle_btn = findViewById(R.id.editpicturecancle_btn);
        editpicturesave_btn = findViewById(R.id.editpicturesave_btn);
        mainavatar_latout = findViewById(R.id.mainavatar_latout);
        main_scrollview = findViewById(R.id.main_scrollview);
        generalsetting_layout = findViewById(R.id.generalsetting_layout);
        genstng_userpassword = findViewById(R.id.genstng_userpassword);
        genstng_userconfirmpassword = findViewById(R.id.genstng_userconfirmpassword);
        genstng_userDOB = findViewById(R.id.genstng_userDOB);
        general_saveBtn = findViewById(R.id.general_saveBtn);
        backArrowImgBtn = findViewById(R.id.backArrowImgBtn);
        backArrow_back = findViewById(R.id.backArrow_back);
        changepassword_btn = findViewById(R.id.changepassword_btn);
        general_save_btn_layout = findViewById(R.id.general_save_btn_layout);

        OustSdkTools.setImage(changeAvatar, getResources().getString(R.string.text));

        genstng_userDOB.setOnClickListener(this);
        general_saveBtn.setOnClickListener(this);
        backArrowImgBtn.setOnClickListener(this);
        editpicturesave_btn.setOnClickListener(this);
        editpicturecancle_btn.setOnClickListener(this);
        changeAvatar.setOnClickListener(this);
        imgAvatarButton.setOnClickListener(this);
        backArrow_back.setOnClickListener(this);
        changepassword_btn.setOnClickListener(this);
//        rippleView.setOnClickListener(this);

        try {
            disableProfileEdit = OustPreferences.getAppInstallVariable(AppConstants.StringConstants.DISABLE_PROFILE_EDIT);

            if (color != 0) {
//            general_saveBtn.setBackgroundColor(color);
                Drawable buttonDrawable = general_saveBtn.getBackground();
                buttonDrawable = DrawableCompat.wrap(buttonDrawable);
                //the color is a direct color int and not a color resource
                DrawableCompat.setTint(buttonDrawable, color);
                general_saveBtn.setBackground(buttonDrawable);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

        GenderSelect.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId != -1 && isChecking) {
                    isChecking = false;
                    GenderSelect2.clearCheck();
                    mCheckedId = checkedId;
                }
                isChecking = true;
            }
        });
        GenderSelect2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId != -1 && isChecking) {
                    isChecking = false;
                    GenderSelect.clearCheck();
                    mCheckedId = checkedId;
                }
                isChecking = true;
            }
        });
    }

    private void getColors() {
        if (OustPreferences.getAppInstallVariable("isLayout4")) {
            color = OustResourceUtils.getColors();
        } else {
            color = OustResourceUtils.getToolBarBgColor();
        }
    }

    public void initUserGeneral() {
        try {
            ActiveUser activeUser = OustAppState.getInstance().getActiveUser();
            if (activeUser != null) {
                OustSdkTools.setSnackbarElements(generalsetting_layout, GeneralSettingActivity.this);
                presenter = new GeneralSettingActivityPresenter(this);
                presenter.setTanentIdStatus(OustPreferences.get("tanentid"));
                presenter.setStartingData();
                presenter.setTypeOfOustUser(OustPreferences.get("loginType"));
                setFont();
                if (disableProfileEdit) {
                    UserName.setEnabled(false);
                    UserDateOfBirth.setEnabled(false);
                    UserEmail.setEnabled(false);
                    UserCity.setEnabled(false);
                    UserCountry.setEnabled(false);
                    UserMobileNumber.setEnabled(false);
                    GenderMale.setEnabled(false);
                    GenderFemale.setEnabled(false);
                    GenderTran.setEnabled(false);
                    GenderOther.setEnabled(false);
                    general_save_btn_layout.setVisibility(View.GONE);
                    mainavatar_latout.setEnabled(false);
                    imgAvatarButton.setEnabled(false);
                    changeAvatar.setEnabled(false);
                } else {
                    UserName.setEnabled(true);
                    UserDateOfBirth.setEnabled(true);
                    UserEmail.setEnabled(true);
                    UserCity.setEnabled(true);
                    UserCountry.setEnabled(true);
                    UserMobileNumber.setEnabled(true);
                    GenderMale.setEnabled(true);
                    GenderFemale.setEnabled(true);
                    GenderTran.setEnabled(true);
                    GenderOther.setEnabled(true);
                    general_save_btn_layout.setVisibility(View.VISIBLE);
                    mainavatar_latout.setEnabled(true);
                    imgAvatarButton.setEnabled(true);
                    changeAvatar.setEnabled(true);
                }
            } else {
                GeneralSettingActivity.this.finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void setFont() {
        try {
            GenderMale.setTypeface(OustSdkTools.getAvenirLTStdMedium());
            GenderFemale.setTypeface(OustSdkTools.getAvenirLTStdMedium());
            GenderTran.setTypeface(OustSdkTools.getAvenirLTStdMedium());
            GenderOther.setTypeface(OustSdkTools.getAvenirLTStdMedium());
            UserCity.setTypeface(OustSdkTools.getAvenirLTStdMedium());
            txtTitle.setText(getResources().getString(R.string.general));
            usergender_Label.setText(getResources().getString(R.string.gender));
            GenderFemale.setText(getResources().getString(R.string.female));
            GenderMale.setText(getResources().getString(R.string.male));
            GenderTran.setText(getResources().getString(R.string.transgender));
            GenderOther.setText(getResources().getString(R.string.other));
            general_saveBtn.setText(getResources().getString(R.string.save));
            changepassword_btn.setText(getResources().getString(R.string.change_password));
            genstng_userpassword.setHint(getResources().getString(R.string.enter_new_password));
            general_saveBtn.setText(getResources().getString(R.string.save));
            editpicturesave_btn.setText(getResources().getString(R.string.save));
            editpicturecancle_btn.setText(getResources().getString(R.string.cancel));
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void setUserAvatar(String avatar) {
        try {
            imageSelectedString = avatar;
            if (OustSdkTools.checkInternetStatus()) {
                ExecutorService executorService = Executors.newSingleThreadExecutor();
                Handler handler = new Handler(Looper.getMainLooper());
                executorService.execute(() -> {
                    //Do background work here
                    try {
                        URL url = new URL(avatar);
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                        connection.setDoInput(true);
                        connection.connect();
                        InputStream input = connection.getInputStream();
                        image = BitmapFactory.decodeStream(input);
                    } catch (Exception e) {
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    }

                    handler.post(() -> {
                        //do post execute work here/or ui update
                        bitMapToString(image);
                        imageUpdated = true;
                    });
                });
            } else {
                Picasso.get().load(avatar).networkPolicy(NetworkPolicy.OFFLINE).into(imgAvatarButton);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void setSavedUserAvatar() {
        try {
            if (OustSdkTools.tempProfile != null) {
                finalBitmap = OustSdkTools.tempProfile;
                Log.e("TAG", "setUserAvatar: setSavedUserAvatar: temp--> " + finalBitmap);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                finalBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                byte[] b = baos.toByteArray();
                imageSelectedString = Base64.encodeToString(b, Base64.DEFAULT);
                imgAvatarButton.setImageBitmap(OustSdkTools.tempProfile);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void setTypeOfUser(String type, int color) {
    }

    public void showChangeAvatarIcon() {
        changeAvatar.setVisibility(View.VISIBLE);
        //changepaswword_layout.setVisibility(View.VISIBLE);
    }

    public void setUserName(String name) {
        userName = name;
        UserName.setText(name);
    }

    public void setUserDOB(long dob) {
        try {
            SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MMM-yyyy");
            Date parsedDate = new Date(dob);
            UserDateOfBirth.setText(dateFormatter.format(parsedDate));
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void setGenderAsMale() {
        GenderMale.setChecked(true);
    }

    public void setGenderAsFemale() {
        GenderFemale.setChecked(true);
    }
    public void setGenderAsTransgender() {
        GenderTran.setChecked(true);
    }
    public void setGenderAsOther() {
        GenderOther.setChecked(true);
    }

    public void setEmail(String email) {
        UserEmail.setText(email);
    }

    public void setDisableEmail() {
        UserEmail.setEnabled(false);
    }

    public void setCity(String city) {
        try {
            if (city != null) {
                UserCity.setText(city);
            } else {
                if ((OustPreferences.get("eventusercity") != null) && (!OustPreferences.get("eventusercity").isEmpty())) {
                    UserCity.setText(OustPreferences.get("eventusercity"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void setUsersCountry(String country) {
        UserCountry.setText(country);
    }

    public void setUserMobileNo(Long mobNo) {
        if (mobNo > 1000) {
            UserMobileNumber.setText(mobNo + "");
        } else {
            if ((OustPreferences.get("eventuserphonenumber") != null) && (!OustPreferences.get("eventuserphonenumber").isEmpty())) {
                UserMobileNumber.setText(OustPreferences.get("eventuserphonenumber"));
            }
        }
    }

    public void setCityListToBeEntered(List<String> cities) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.simplecity_name, cities);
        UserCity.setThreshold(0);
        UserCity.setAdapter(adapter);
    }

    public void setUserDateOfBirthCalander() {
        try {
            Calendar newCalendar = Calendar.getInstance();
            UserDateOfBirth.setInputType(InputType.TYPE_NULL);
            DatePickerDialog = new DatePickerDialog(this, (view, year, monthOfYear, dayOfMonth) -> {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                presenter.saveSelectedDob(newDate.getTimeInMillis());
            }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
            UserDateOfBirth.setOnFocusChangeListener((v, hasFocus) -> {
                if (hasFocus) {
                    DatePickerDialog.setTitle(getResources().getString(R.string.select_dob));
                    DatePickerDialog.show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    //==============================================================================================================

    public void onClick(final View v) {
        int id = v.getId();
        if (id == R.id.changeAvatar) {
            ObjectAnimator scaleX = ObjectAnimator.ofFloat(v, "scaleX", 1.0f, 0.94f);
            ObjectAnimator scaleY = ObjectAnimator.ofFloat(v, "scaleY", 1.0f, 0.96f);
            scaleX.setDuration(150);
            scaleY.setDuration(150);
            scaleX.setRepeatCount(1);
            scaleY.setRepeatCount(1);
            scaleX.setRepeatMode(ValueAnimator.REVERSE);
            scaleY.setRepeatMode(ValueAnimator.REVERSE);
            scaleX.setInterpolator(new DecelerateInterpolator());
            scaleY.setInterpolator(new DecelerateInterpolator());
            AnimatorSet scale = new AnimatorSet();
            scale.play(scaleX).with(scaleY);
            scale.start();
            scale.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {
                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    presenter.choosePicButtonClick();
                }

                @Override
                public void onAnimationCancel(Animator animator) {
                }

                @Override
                public void onAnimationRepeat(Animator animator) {
                }
            });
        } else if (id == R.id.imgAvatarButton) {
            ObjectAnimator scaleDX = ObjectAnimator.ofFloat(v, "scaleX", 1.0f, 0.94f);
            ObjectAnimator scaleDY = ObjectAnimator.ofFloat(v, "scaleY", 1.0f, 0.96f);
            scaleDX.setDuration(150);
            scaleDY.setDuration(150);
            scaleDX.setRepeatCount(1);
            scaleDY.setRepeatCount(1);
            scaleDX.setRepeatMode(ValueAnimator.REVERSE);
            scaleDY.setRepeatMode(ValueAnimator.REVERSE);
            scaleDX.setInterpolator(new DecelerateInterpolator());
            scaleDY.setInterpolator(new DecelerateInterpolator());
            AnimatorSet scaleD = new AnimatorSet();
            scaleD.play(scaleDX).with(scaleDY);
            scaleD.start();
            scaleD.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {
                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    presenter.choosePicButtonClick();
                }

                @Override
                public void onAnimationCancel(Animator animator) {
                }

                @Override
                public void onAnimationRepeat(Animator animator) {
                }
            });
        } else if (id == R.id.genstng_userDOB) {
            DatePickerDialog.setTitle(getResources().getString(R.string.select_dob));
            DatePickerDialog.show();
        } else if (id == R.id.general_saveBtn) {

            if (!OustSdkTools.checkInternetStatus()) {
                return;
            }
            presenter.saveButtonClicked(mCheckedId, GenderMale.getId(), GenderFemale.getId(), GenderTran.getId(), GenderOther.getId());
            usernot = true;
            Log.e("TAG", "setUserAvatar - onComplete: imageSelectedString--> " + imageSelectedString);
            if (imageSelectedString != null && !imageSelectedString.isEmpty()) {
                presenter.saveImageString(imageSelectedString);
            }
            presenter.setUserInformationToSave(UserName.getText().toString(), UserDateOfBirth.getText().toString(), UserEmail.getText().toString(),
                    UserCity.getText().toString(), UserCountry.getText().toString(), UserMobileNumber.getText().toString(),
                    genstng_userpassword.getText().toString(), genstng_userconfirmpassword.getText().toString());

            imageUpdated = true;
        } else if (id == R.id.backArrowImgBtn) {
            if (imageUpdated) {
                onBackPressed();
            } else {
                Toast.makeText(GeneralSettingActivity.this, "Please click save button to update image", Toast.LENGTH_LONG).show();
            }

        } else if (id == R.id.backArrow_back) {
            ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(v, "scaleX", 1.0f, 0.94f);
            ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(v, "scaleY", 1.0f, 0.96f);
            scaleDownX.setDuration(150);
            scaleDownY.setDuration(150);
            scaleDownX.setRepeatCount(1);
            scaleDownY.setRepeatCount(1);
            scaleDownX.setRepeatMode(ValueAnimator.REVERSE);
            scaleDownY.setRepeatMode(ValueAnimator.REVERSE);
            scaleDownX.setInterpolator(new DecelerateInterpolator());
            scaleDownY.setInterpolator(new DecelerateInterpolator());
            AnimatorSet scaleDown = new AnimatorSet();
            scaleDown.play(scaleDownX).with(scaleDownY);
            scaleDown.start();
            scaleDown.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {
                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    onBackPressed();
                }

                @Override
                public void onAnimationCancel(Animator animator) {
                }

                @Override
                public void onAnimationRepeat(Animator animator) {
                }
            });
        } else if (id == R.id.editpicturesave_btn) {
            savePicturePreview();
        } else if (id == R.id.editpicturecancle_btn) {
            canclePicturePreview();
        } else if (id == R.id.changepassword_btn) {
            if (genstng_userpassword.getVisibility() == View.GONE) {
                genstng_userpassword.setVisibility(View.VISIBLE);
                genstng_userconfirmpassword.setVisibility(View.VISIBLE);
                genstng_userpassword.requestFocus();
                genstng_userpassword.setError(null);
                setPasswordFunction();
            } else {
                genstng_userpassword.setText("");
                genstng_userconfirmpassword.setText("");
                genstng_userpassword.setVisibility(View.GONE);
                genstng_userconfirmpassword.setVisibility(View.GONE);
            }

        }
    }

    @Override
    public void onBackPressed() {
        try {
            if (previewImageView.isShown()) {
                previewImageView.setVisibility(View.GONE);
                mainavatar_latout.setVisibility(View.VISIBLE);
                main_scrollview.setVisibility(View.VISIBLE);
            } else {
                if (!usernot) {
                    finishAffinity();
                } else {
                    super.onBackPressed();
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    //-------------------------------------------------------
    //change paasword function
    private void setPasswordFunction() {
        genstng_userconfirmpassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                presenter.checkValidPassword(genstng_userpassword.getText().toString(), genstng_userconfirmpassword.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        genstng_userpassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                presenter.checkPasswordValidity(genstng_userpassword.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    public void removeError() {
        genstng_userpassword.setError(null);
    }

    public void removeConfirmPasswordError() {
        genstng_userconfirmpassword.setError(null);
    }

    public void setPasswordError() {
        genstng_userpassword.requestFocus();
        genstng_userpassword.setError(getResources().getString(R.string.pwd_length_error));
    }

    public void setConfirmPasswordError() {
        genstng_userconfirmpassword.requestFocus();
        genstng_userconfirmpassword.setError(getResources().getString(R.string.pwd_match_error));
    }

    //======// ===============================================================================================================
//          save user imformation
    public void saveUserInfo(UserSettingRequest userSettingRequest, ActiveUser activeUser) {
        try {
            String toolbarColorCode = OustPreferences.get("toolbarColorCode");
            if ((toolbarColorCode != null) && (!toolbarColorCode.isEmpty())) {
                int color = Color.parseColor(OustPreferences.get("toolbarColorCode"));
                PorterDuff.Mode mode = PorterDuff.Mode.SRC_OVER;
                final LayerDrawable ld = (LayerDrawable) getApplicationContext().getResources().getDrawable(R.drawable.progressbar_test);
                final ClipDrawable d1 = (ClipDrawable) ld.findDrawableByLayerId(R.id.customPlayerProgress);
                d1.setColorFilter(color, mode);
                generalSetting_progressbar.setIndeterminateDrawable(ld);
            }
            OustSdkTools.setProgressbar(generalSetting_progressbar);
            OustSdkTools.showProgressBar();
            sendSaveSettingRequest(userSettingRequest, activeUser);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void sendSaveSettingRequest(UserSettingRequest userSettingRequest, ActiveUser activeUser) {
        String settingUrl = OustSdkApplication.getContext().getResources().getString(R.string.usersetting_url);
        settingUrl = settingUrl.replace("{studentid}", activeUser.getStudentid());
        Gson gson = new GsonBuilder().create();
        String jsonParams = gson.toJson(userSettingRequest);
        try {
            settingUrl = HttpManager.getAbsoluteUrl(settingUrl);
            System.out.println("Setting url " + settingUrl + " User " + new Gson().toJson(userSettingRequest));

            ApiCallUtils.doNetworkCall(Request.Method.PUT, settingUrl, OustSdkTools.getRequestObject(jsonParams), new ApiCallUtils.NetworkCallback() {
                @Override
                public void onResponse(JSONObject response) {
                    Gson gson = new Gson();
                    CommonResponse commonResponse = gson.fromJson(response.toString(), CommonResponse.class);
                    saveSettingProcessFinish(commonResponse);
                }

                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }


    public void saveSettingProcessFinish(CommonResponse commonResponse) {
        try {
            OustSdkTools.hideProgressbar();
            presenter.saveProcessFinish(commonResponse);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void finishSettingView() {
        try {
            saveImagetoSdCard();
            //todo open a pop up and on click on arrow-finish the activity
            showSavedPopUp();

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void showSavedPopUp() {
        try {
            final Dialog popUpView = new Dialog(GeneralSettingActivity.this, R.style.DialogTheme);
            popUpView.requestWindowFeature(Window.FEATURE_NO_TITLE);
            popUpView.setContentView(R.layout.profile_save_alert_dialog);
            Objects.requireNonNull(popUpView.getWindow()).setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
            popUpView.setCancelable(false);

//            View view = getLayoutInflater().inflate(R.layout.profile_save_alert_dialog, null);
            LinearLayout nextButton = popUpView.findViewById(R.id.btn_next);
            TextView text = popUpView.findViewById(R.id.save_text);
            userName = UserName.getText().toString();
            text.setText(String.format(getResources().getString(R.string.profile_saved_text), userName));
            if (color != 0) {
                nextButton.setBackgroundColor(color);
            } else {
                nextButton.setBackgroundColor(getResources().getColor(R.color.LiteGreen));
            }
            nextButton.setOnClickListener(view1 -> {
                popUpView.dismiss();
                GeneralSettingActivity.this.finish();
            });
            popUpView.show();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void showErrorPopup(Popup popup) {
        try {
            OustStaticVariableHandling.getInstance().setOustpopup(popup);
            Intent intent = new Intent(GeneralSettingActivity.this, PopupActivity.class);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void showToast(String message) {
        OustSdkTools.showToast(message);
    }
//=====================================================================================================

    /*
     * Capturing Camera Image will lauch camera app requrest image capture
     */


    final int MY_PERMISSIONS_REQUEST_WRITE_STORAGE = 1;
    final int MY_PERMISSIONS_REQUEST_CAMERA_ACCESS = 2;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        try {
            if (requestCode == MY_PERMISSIONS_REQUEST_WRITE_STORAGE) {
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    showEditPicOption();
                }
            } else {
                if (requestCode == MY_PERMISSIONS_REQUEST_CAMERA_ACCESS) {
                    if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                        showEditPicOption();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void checkForStoragePermission() {
        if (ContextCompat.checkSelfPermission(GeneralSettingActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            showEditPicOption();
        } else {
            ActivityCompat.requestPermissions(GeneralSettingActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_STORAGE);
        }
    }


    public void showEditPicOption() {
        try {

            Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(i, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    /**
     * Receiving activity result method will be called after closing the camera
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
                if (resultCode == RESULT_OK) {
                    Uri selectedImageUri = data.getData();
                    String[] projection = {MediaStore.MediaColumns.DATA};
                    CursorLoader cursorLoader = new CursorLoader(this, selectedImageUri, projection, null, null,
                            null);
                    Cursor cursor = cursorLoader.loadInBackground();
                    int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
                    cursor.moveToFirst();
                    String selectedImagePath = cursor.getString(column_index);
                    Bitmap bm;
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = true;
                    BitmapFactory.decodeFile(selectedImagePath, options);
                    final int REQUIRED_SIZE = 1000;
                    int scale = 1;
                    while (options.outWidth / scale / 2 >= REQUIRED_SIZE
                            && options.outHeight / scale / 2 >= REQUIRED_SIZE)
                        scale *= 2;
                    options.inSampleSize = scale;
                    options.inJustDecodeBounds = false;
                    bm = BitmapFactory.decodeFile(selectedImagePath, options);
                    bm = rotateCameraImage(bm, selectedImagePath);
                    showPicturePreview(bm);
                }
                //}
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    //
    //=======================================================================================================
    private void showPicturePreview(final Bitmap bitmap) {
        try {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            DisplayMetrics metrics = GeneralSettingActivity.this.getResources().getDisplayMetrics();
            int scrWidth = metrics.widthPixels;
            int scrHeight = metrics.heightPixels;
            pictureeditView.setScreenWH(scrWidth, scrHeight, bitmap);
            mainavatar_latout.setVisibility(View.GONE);
            main_scrollview.setVisibility(View.GONE);
            previewImageView.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

    }

    private void canclePicturePreview() {
        try {
            previewImageView.setVisibility(View.GONE);
            mainavatar_latout.setVisibility(View.VISIBLE);
            main_scrollview.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void savePicturePreview() {
        try {
            previewImageView.setVisibility(View.GONE);
            mainavatar_latout.setVisibility(View.VISIBLE);
            main_scrollview.setVisibility(View.VISIBLE);
            Bitmap bm = pictureeditView.getFinalBitmap();
            bitMapToString(bm);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void bitMapToString(Bitmap bitmap) {
        try {
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            Handler handler = new Handler(Looper.getMainLooper());
            executorService.execute(() -> {
                //Do background work here
                try {
                    if ((bitmap.getWidth() > 250) && (bitmap.getHeight() > 250)) {
                        finalBitmap = Bitmap.createScaledBitmap(bitmap, 250, 250, false);
                    } else {
                        finalBitmap = bitmap;
                    }
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    finalBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                    byte[] b = baos.toByteArray();
                    imageSelectedString = Base64.encodeToString(b, Base64.DEFAULT);
                } catch (Exception e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }

                handler.post(() -> {
                    //do post execute work here/or ui update
                   //  imageUpdated = false;
                    imgAvatarButton.setImageBitmap(finalBitmap);
                });
            });
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public Bitmap rotateCameraImage(Bitmap bitmap, String photoPath) {
        ExifInterface ei;
        Bitmap rotatedBitmap = null;
        try {
            ei = new ExifInterface(photoPath);
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED);

            switch (orientation) {

                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotatedBitmap = rotateImage(bitmap, 90);
                    break;

                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotatedBitmap = rotateImage(bitmap, 180);
                    break;

                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotatedBitmap = rotateImage(bitmap, 270);
                    break;

                case ExifInterface.ORIENTATION_NORMAL:
                default:
                    rotatedBitmap = bitmap;
            }
        } catch (IOException e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            Log.e("TAG", "rotateCameraImage: -> " + e.getMessage());
        }
        return rotatedBitmap;
    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    private void saveImagetoSdCard() {
        try {
            if (finalBitmap != null) {
                OustSdkTools.tempProfile = finalBitmap;
                imgAvatarButton.setImageBitmap(finalBitmap);
                createDirIfNecessory();
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                finalBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                String path = Environment.getExternalStorageDirectory() + "/oustme/" + getImageName();
                File myFile = new File(path);
                new FileOutputStream(myFile).write(bytes.toByteArray());
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void createDirIfNecessory() {
        try {
            File myDirectory = new File(Environment.getExternalStorageDirectory(), "/oustme");
            if (!myDirectory.exists()) {
                myDirectory.mkdirs();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private String getImageName() {
        String imageName = OustAppState.getInstance().getActiveUser().getStudentid();
        try {
            if (imageName.length() > 6) {
                imageName = imageName.substring(3, 6);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        return "oustuser" + imageName + "profile.jpg";
    }
}
