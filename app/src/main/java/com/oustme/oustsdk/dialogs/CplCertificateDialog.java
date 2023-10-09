package com.oustme.oustsdk.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.interfaces.common.CplCloseListener;
import com.oustme.oustsdk.tools.HttpManager;
import com.oustme.oustsdk.tools.OustAppState;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.util.ApiCallUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Pattern;


/**
 * Created by oust on 8/24/18.
 */

public class CplCertificateDialog extends Dialog {

    private Context mContext;
    private ImageView closeBtn;
    private EditText edittext_email;
    private CardView cert_popup_ll, confirmpopup_animlayout;
    private TextView confirm_emailtext;
    private RelativeLayout emailpopup_reenterlayout, emailpopup_confirmlayout;
    private ImageButton btnClose;
    private FrameLayout submit_certificates;

    private String cplId;
    private CplCloseListener cplCloseListener;

    public CplCertificateDialog(@NonNull Context context, String cplId, CplCloseListener cplCloseListener) {
        super(context, android.R.style.Theme_Black_NoTitleBar);
        this.mContext = context;
        this.cplId = cplId;
        this.cplCloseListener = cplCloseListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setBackgroundDrawable(mContext.getResources().getDrawable(R.color.black_semi_transparent));
        setContentView(R.layout.dialog_cpl_certificate);
        setCancelable(false);
        initViews();
    }

    private void initViews() {

        closeBtn = findViewById(R.id.closeBtn);
        edittext_email = findViewById(R.id.edittext_email);
        submit_certificates = findViewById(R.id.submit_certificates);
        cert_popup_ll = findViewById(R.id.certificate_popup_ll);
        confirmpopup_animlayout = findViewById(R.id.confirmpopup_animlayout);
        emailpopup_reenterlayout = findViewById(R.id.emailpopup_reenterlayout);
        emailpopup_confirmlayout = findViewById(R.id.emailpopup_confirmlayout);
        confirm_emailtext = findViewById(R.id.confirm_emailtext);

        Drawable actionDrawable;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            actionDrawable = mContext.getDrawable(R.drawable.course_button_bg);
            submit_certificates.setBackground(OustSdkTools.drawableColor(actionDrawable));
        }

        btnClose = findViewById(R.id.btnClose);

        closeBtn.setOnClickListener(v -> dismiss());

        submit_certificates.setOnClickListener(v -> validateEmail());

        btnClose.setOnClickListener(v -> showCertificatePopUp());

        emailpopup_reenterlayout.setOnClickListener(v -> showCertificatePopUp());

        emailpopup_confirmlayout.setOnClickListener(v -> hitServer(confirm_emailtext.getText().toString()));
    }

    private void showCertificatePopUp() {
        cert_popup_ll.setVisibility(View.VISIBLE);
        confirmpopup_animlayout.setVisibility(View.GONE);
    }

    private void showConfirmPopup(String userEmail) {
        confirm_emailtext.setText("" + userEmail);
        cert_popup_ll.setVisibility(View.GONE);
        confirmpopup_animlayout.setVisibility(View.VISIBLE);
    }

    private void validateEmail() {
        edittext_email.setError(null);
        if (!edittext_email.getText().toString().trim().isEmpty()) {
            String userEmail = edittext_email.getText().toString().trim();
            if (isValidEmail(userEmail))
                showConfirmPopup(userEmail);
            else
                edittext_email.setError("Please enter valid email address !");
        } else {
            edittext_email.setError("Please enter your email address !");
        }
    }

    private void hitServer(String userEmail) {
        JSONObject jsonObject = new JSONObject();
        String cpl_send_certificate_url = mContext.getResources().getString(R.string.cpl_send_certificate);
        try {
            if (cplId != null)
                jsonObject.put("contentId", Integer.parseInt(cplId));
            jsonObject.put("contentType", "CPL");
            jsonObject.put("emailid", userEmail);
            jsonObject.put("studentid", OustAppState.getInstance().getActiveUser().getStudentid());
            jsonObject = OustSdkTools.getRequestObjectforJSONObject(jsonObject);
            cpl_send_certificate_url = HttpManager.getAbsoluteUrl(cpl_send_certificate_url);

            ApiCallUtils.doNetworkCall(Request.Method.PUT, cpl_send_certificate_url, jsonObject, new ApiCallUtils.NetworkCallback() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        if (response.optBoolean("success")) {
                            OustSdkTools.showToast("" + response.optString("message"));
                            if (cplCloseListener != null) {
                                cplCloseListener.onCertificatePopupClose();
                            }
                            dismiss();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    }
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    OustSdkTools.showToast(mContext.getResources().getString(R.string.retry_internet_msg));
                    if (cplCloseListener != null) {
                        cplCloseListener.onCertificatePopupClose();
                    }
                    dismiss();
                }
            });
        } catch (JSONException e) {
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
}
