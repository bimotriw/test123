package com.oustme.oustsdk.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.tools.HttpManager;
import com.oustme.oustsdk.tools.OustAppState;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.util.ApiCallUtils;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by oust on 8/23/18.
 */

public class CplRatingsDialog extends Dialog {

    private Context mContext;
    private TextView cpl_rate_header;
    private EditText cpl_rate_feedback_et;
    private ImageView cpl_rate_img1, cpl_rate_img2, cpl_rate_img3, cpl_rate_img4, cpl_rate_img5;
    private LinearLayout cpl_rate_submit_ll;
    private int rating = 5;
    private String cplId;
    private String cplHeader;

    public CplRatingsDialog(@NonNull Context context, String cplId, String cplHeader) {
        super(context, android.R.style.Theme_Black_NoTitleBar);
        this.mContext = context;
        this.cplId = cplId;
        this.cplHeader = cplHeader;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setBackgroundDrawable(mContext.getResources().getDrawable(R.color.black_semi_transparent));
        setContentView(R.layout.dialog_cpl_rating);
        setCancelable(false);

        initViews();

    }

    private void initViews() {

        cpl_rate_header = findViewById(R.id.cpl_rate_header);
        //cpl_rating_txt = findViewById(R.id.cpl_rating_txt);

        cpl_rate_feedback_et = findViewById(R.id.cpl_rate_feedback_et);

        cpl_rate_img1 = findViewById(R.id.cpl_rate_img1);
        cpl_rate_img2 = findViewById(R.id.cpl_rate_img2);
        cpl_rate_img3 = findViewById(R.id.cpl_rate_img3);
        cpl_rate_img4 = findViewById(R.id.cpl_rate_img4);
        cpl_rate_img5 = findViewById(R.id.cpl_rate_img5);

        cpl_rate_submit_ll = findViewById(R.id.cpl_rate_submit_ll);

        cpl_rate_header.setText("Rate the " + cplHeader);

        cpl_rate_img1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rating = 1;
                rateStars(1);
            }
        });
        cpl_rate_img2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rating = 2;
                rateStars(2);
            }
        });
        cpl_rate_img3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rating = 3;
                rateStars(3);
            }
        });
        cpl_rate_img4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rating = 4;
                rateStars(4);
            }
        });
        cpl_rate_img5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rating = 5;
                rateStars(5);
            }
        });

        cpl_rate_submit_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitRateResponse();
            }
        });

    }

    private void submitRateResponse() {
        JSONObject jsonObject = new JSONObject();
        String cpl_rating_url = mContext.getResources().getString(R.string.cpl_rating_url);
        try {
            jsonObject.put("cplId", Integer.parseInt(cplId));
            if (cpl_rate_feedback_et.getText().toString() != null && !cpl_rate_feedback_et.getText().toString().isEmpty()) {
                jsonObject.put("feedback", cpl_rate_feedback_et.getText().toString());
            }else{
                jsonObject.put("feedback","");
            }
            jsonObject.put("rating", rating);
            jsonObject.put("studentid", OustAppState.getInstance().getActiveUser().getStudentKey());
            jsonObject=OustSdkTools.getRequestObjectforJSONObject(jsonObject);
            cpl_rating_url = HttpManager.getAbsoluteUrl(cpl_rating_url);

            ApiCallUtils.doNetworkCall(Request.Method.PUT, cpl_rating_url, jsonObject, new ApiCallUtils.NetworkCallback() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        if (response.optBoolean("success")) {
                            OustSdkTools.showToast(""+response.optString("message"));

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
                    dismiss();
                }
            });


            /*JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.PUT, cpl_rating_url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        if (response.optBoolean("success")) {
                            OustSdkTools.showToast(""+response.optString("message"));

                            dismiss();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    OustSdkTools.showToast(mContext.getResources().getString(R.string.retry_internet_msg));
                    dismiss();
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
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
            jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(10000, 0, 100000f));
            OustSdkApplication.getInstance().addToRequestQueue(jsonObjReq, "first");*/

        } catch (JSONException e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

    }

    private void rateStars(int i) {
        switch (i) {
            case 1:
                cpl_rate_img1.setImageDrawable(mContext.getResources().getDrawable(R.drawable.star_filla));
                cpl_rate_img2.setImageDrawable(mContext.getResources().getDrawable(R.drawable.star_emptya));
                cpl_rate_img3.setImageDrawable(mContext.getResources().getDrawable(R.drawable.star_emptya));
                cpl_rate_img4.setImageDrawable(mContext.getResources().getDrawable(R.drawable.star_emptya));
                cpl_rate_img5.setImageDrawable(mContext.getResources().getDrawable(R.drawable.star_emptya));
                break;
            case 2:
                cpl_rate_img1.setImageDrawable(mContext.getResources().getDrawable(R.drawable.star_filla));
                cpl_rate_img2.setImageDrawable(mContext.getResources().getDrawable(R.drawable.star_filla));
                cpl_rate_img3.setImageDrawable(mContext.getResources().getDrawable(R.drawable.star_emptya));
                cpl_rate_img4.setImageDrawable(mContext.getResources().getDrawable(R.drawable.star_emptya));
                cpl_rate_img5.setImageDrawable(mContext.getResources().getDrawable(R.drawable.star_emptya));
                break;
            case 3:
                cpl_rate_img1.setImageDrawable(mContext.getResources().getDrawable(R.drawable.star_filla));
                cpl_rate_img2.setImageDrawable(mContext.getResources().getDrawable(R.drawable.star_filla));
                cpl_rate_img3.setImageDrawable(mContext.getResources().getDrawable(R.drawable.star_filla));
                cpl_rate_img4.setImageDrawable(mContext.getResources().getDrawable(R.drawable.star_emptya));
                cpl_rate_img5.setImageDrawable(mContext.getResources().getDrawable(R.drawable.star_emptya));
                break;
            case 4:
                cpl_rate_img1.setImageDrawable(mContext.getResources().getDrawable(R.drawable.star_filla));
                cpl_rate_img2.setImageDrawable(mContext.getResources().getDrawable(R.drawable.star_filla));
                cpl_rate_img3.setImageDrawable(mContext.getResources().getDrawable(R.drawable.star_filla));
                cpl_rate_img4.setImageDrawable(mContext.getResources().getDrawable(R.drawable.star_filla));
                cpl_rate_img5.setImageDrawable(mContext.getResources().getDrawable(R.drawable.star_emptya));
                break;
            case 5:
                cpl_rate_img1.setImageDrawable(mContext.getResources().getDrawable(R.drawable.star_filla));
                cpl_rate_img2.setImageDrawable(mContext.getResources().getDrawable(R.drawable.star_filla));
                cpl_rate_img3.setImageDrawable(mContext.getResources().getDrawable(R.drawable.star_filla));
                cpl_rate_img4.setImageDrawable(mContext.getResources().getDrawable(R.drawable.star_filla));
                cpl_rate_img5.setImageDrawable(mContext.getResources().getDrawable(R.drawable.star_filla));
                break;

        }
    }


}
