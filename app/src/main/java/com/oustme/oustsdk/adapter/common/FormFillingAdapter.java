package com.oustme.oustsdk.adapter.common;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;

import androidx.core.content.ContextCompat;
import androidx.appcompat.widget.AppCompatRadioButton;
import androidx.recyclerview.widget.RecyclerView;


import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.view.animation.DecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.oustme.oustsdk.R;
import com.oustme.oustsdk.customviews.TryRippleView;
import com.oustme.oustsdk.interfaces.common.Formfill_callback;
import com.oustme.oustsdk.request.FormFillRequest;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.OustStrings;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shilpysamaddar on 23/03/17.
 */

public class FormFillingAdapter extends RecyclerView.Adapter<FormFillingAdapter.DataObjectHolder> {

    private String LOG_TAG = "FormFillingAdapter";
    private long currentTime;
    private Bitmap bitmap;
    private Formfill_callback formfill_callback;
    private FormFillRequest formFillRequest;
    private Context mContext;
    private List<String> mPurposeList;
    private ArrayAdapter<String> dataAdapter;

    public void setFormfill_callback(Formfill_callback formfill_callback) {
        this.formfill_callback = formfill_callback;
    }

    public FormFillingAdapter(long currentTime, FormFillRequest formFillRequest, Context context, List<String> mPurposeList) {
        this.currentTime = currentTime;
        this.formFillRequest = formFillRequest;
        this.mContext = context;
        this.mPurposeList = mPurposeList;
    }

    public void onFormImageChange(Bitmap bitmap1, FormFillRequest formFillRequest) {
        try {
            this.bitmap = bitmap1;
            this.formFillRequest = formFillRequest;
            notifyDataSetChanged();
        } catch (Exception e) {
        }
    }

    public class DataObjectHolder extends RecyclerView.ViewHolder {
        TextView formrow_title, formrow_textView, comment_char_info;
        EditText formrow_edittext, formrow_dateedittext, formrow_commentedittext;
        LinearLayout formrow_radiobtn_layout;
        RelativeLayout cameraLayout;
        RadioGroup formrow_togglebtn;
        AppCompatRadioButton form_radiobtna, form_radiobtnb;
        Spinner formrow_spinner;
        //        Button formrow_submit_btn;
        ImageView camera_image_view, camera_icon;
        TryRippleView formrow_submit_ripple;
        Button formrow_submit_btn;

        DataObjectHolder(View itemView) {
            super(itemView);
            formrow_title = itemView.findViewById(R.id.formrow_title);
            formrow_textView = itemView.findViewById(R.id.formrow_textView);
            comment_char_info = itemView.findViewById(R.id.comment_char_info);
            formrow_edittext = itemView.findViewById(R.id.formrow_edittext);
            formrow_dateedittext = itemView.findViewById(R.id.formrow_dateedittext);
            formrow_commentedittext = itemView.findViewById(R.id.formrow_commentedittext);
            formrow_radiobtn_layout = itemView.findViewById(R.id.formrow_radiobtn_layout);
            formrow_togglebtn = itemView.findViewById(R.id.formrow_togglebtn);
            form_radiobtna = itemView.findViewById(R.id.form_radiobtna);
            form_radiobtnb = itemView.findViewById(R.id.form_radiobtnb);
            formrow_spinner = itemView.findViewById(R.id.formrow_spinner);
            formrow_submit_btn = itemView.findViewById(R.id.formrow_submit_btn);
            cameraLayout = itemView.findViewById(R.id.cameraLayout);
            camera_image_view = itemView.findViewById(R.id.camera_image_view);
            camera_icon = itemView.findViewById(R.id.camera_icon);
            formrow_submit_ripple = itemView.findViewById(R.id.formrow_submit_ripple);

        }
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.form, parent, false);
        return new DataObjectHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final DataObjectHolder holder, int position) {
        try {
            holder.formrow_title.setVisibility(View.GONE);
            holder.formrow_textView.setVisibility(View.GONE);
            holder.formrow_edittext.setVisibility(View.GONE);
            holder.formrow_dateedittext.setVisibility(View.GONE);
            holder.formrow_radiobtn_layout.setVisibility(View.GONE);
            holder.formrow_submit_ripple.setVisibility(View.GONE);
            holder.formrow_spinner.setVisibility(View.GONE);
            holder.cameraLayout.setVisibility(View.GONE);
            holder.comment_char_info.setVisibility(View.GONE);
            holder.formrow_commentedittext.setVisibility(View.GONE);

            holder.formrow_dateedittext.setHint(mContext.getResources().getString(R.string.enter_mobile_no));
            holder.formrow_title.setText(mContext.getResources().getString(R.string.user_name));
            holder.comment_char_info.setText(mContext.getResources().getString(R.string.some_character_remaining));
            holder.formrow_submit_btn.setText(mContext.getResources().getString(R.string.submit_form));
            if (position == 0) {
                holder.cameraLayout.setVisibility(View.VISIBLE);
                if (bitmap != null) {
                    holder.camera_image_view.setImageBitmap(bitmap);
                }
            } else if (position == 1) {
                holder.formrow_title.setVisibility(View.VISIBLE);
                holder.formrow_title.setText(mContext.getResources().getString(R.string.select_purpose));
                holder.formrow_spinner.setVisibility(View.VISIBLE);
                addItemsOnSpinner(holder.formrow_spinner);
            } else if (position == 2) {
                holder.formrow_title.setVisibility(View.VISIBLE);
                holder.formrow_title.setText(mContext.getResources().getString(R.string.description_title));
                holder.formrow_commentedittext.setVisibility(View.VISIBLE);
//                holder.comment_char_info.setVisibility(View.VISIBLE);
            } else if (position == 3) {
                holder.formrow_title.setVisibility(View.VISIBLE);
                holder.formrow_title.setText(mContext.getResources().getString(R.string.user_mobile_number));
                holder.formrow_dateedittext.setVisibility(View.VISIBLE);
                if (formFillRequest.getMobile() != null) {
                    if (!formFillRequest.getMobile().equals("0"))
                        holder.formrow_dateedittext.setText(formFillRequest.getMobile());
                    else
                        formFillRequest.setMobile("");
                }
            } else if (position == 4) {
                holder.formrow_title.setVisibility(View.VISIBLE);
                holder.formrow_title.setText(mContext.getResources().getString(R.string.location));
                holder.formrow_textView.setVisibility(View.VISIBLE);
                try {
                    if (formFillRequest != null) {
                        holder.formrow_textView.setText(formFillRequest.getTenantId());
                    } else {
                        holder.formrow_textView.setText(mContext.getResources().getString(R.string.location_not_found));
                        holder.formrow_textView.setTextColor(ContextCompat.getColor(OustSdkApplication.getContext(), R.color.DarkGray));
                    }
                } catch (Exception e) {
                    holder.formrow_textView.setText(mContext.getResources().getString(R.string.location_not_found));
                    holder.formrow_textView.setTextColor(ContextCompat.getColor(OustSdkApplication.getContext(), R.color.DarkGray));
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }
            } else if (position == 5) {
                holder.formrow_submit_ripple.setVisibility(View.VISIBLE);
            }
            holder.formrow_submit_ripple.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder.formrow_submit_ripple.setOnRippleCompleteListener(new TryRippleView.OnRippleCompleteListener() {
                        @Override
                        public void onComplete(TryRippleView rippleView) {
                            formfill_callback.clickOnSaveBtn();
                        }
                    });
                }
            });
            holder.formrow_commentedittext.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    String comment = holder.formrow_commentedittext.getText().toString().trim();
                    int comment_length = 500 - comment.length();
                    holder.comment_char_info.setText("" + comment_length + " " + OustStrings.getMainString("character_remaining"));
                    formfill_callback.userCommentChanges(comment);
                }

                @Override
                public void afterTextChanged(Editable editable) {
                }
            });

            holder.formrow_edittext.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    formfill_callback.usernameChange(holder.formrow_edittext.getText().toString());
                }

                @Override
                public void afterTextChanged(Editable editable) {
                }
            });
            final String[] prviousResult = new String[]{""};
            holder.formrow_dateedittext.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    String result = holder.formrow_dateedittext.getText().toString();
                    if (!result.isEmpty()) {
                        if (!result.equals(prviousResult[0])) {
                            prviousResult[0] = result;
                            result = result.replace(" ", "");
                            result = result.replace(".", "");

                            holder.formrow_dateedittext.setText(result);
                            holder.formrow_dateedittext.setSelection(result.length());
                            formfill_callback.userAgeChnage(result);
                        }
                    }
                }
            });

            holder.camera_icon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(view, "scaleX", 1.0f, 0.94f);
                    ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(view, "scaleY", 1.0f, 0.96f);
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
                            formfill_callback.clickOnCameraBtn();
                        }

                        @Override
                        public void onAnimationCancel(Animator animator) {
                        }

                        @Override
                        public void onAnimationRepeat(Animator animator) {
                        }
                    });
                }
            });

            holder.camera_image_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(view, "scaleX", 1.0f, 0.94f);
                    ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(view, "scaleY", 1.0f, 0.96f);
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
                            formfill_callback.clickOnCameraBtn();
                        }

                        @Override
                        public void onAnimationCancel(Animator animator) {
                        }

                        @Override
                        public void onAnimationRepeat(Animator animator) {
                        }
                    });
                }
            });

        } catch (Exception e) {
        }
    }


    public void addItemsOnSpinner(final Spinner spinner) {
        try {
            List<String> list = new ArrayList<String>();
            list.add(mContext.getResources().getString(R.string.help_feed_back));
            list.add(mContext.getResources().getString(R.string.help_quest));
            list.add(mContext.getResources().getString(R.string.help_report_issue));
            if (mPurposeList != null && mPurposeList.size() > 0) {
                dataAdapter = new ArrayAdapter<String>(OustSdkApplication.getContext(), R.layout.languagemain_row, mPurposeList);
            } else {
                dataAdapter = new ArrayAdapter<String>(OustSdkApplication.getContext(), R.layout.languagemain_row, list);
            }
            dataAdapter.setDropDownViewResource(R.layout.simple_languagerow);
            spinner.setAdapter(dataAdapter);

            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                    try {
                        formfill_callback.userPurposeChange(spinner.getSelectedItem().toString());
                    } catch (Exception e) {
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> arg0) {
                }
            });
        } catch (Exception e) {
            Log.d("formfill", e.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return 6;
    }
}
