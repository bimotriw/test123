package com.oustme.oustsdk.skill_ui.ui;

import static android.graphics.Typeface.BOLD;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.chart.charts.CombinedChart;
import com.oustme.oustsdk.chart.components.Legend;
import com.oustme.oustsdk.chart.components.LegendEntry;
import com.oustme.oustsdk.chart.components.XAxis;
import com.oustme.oustsdk.chart.components.YAxis;
import com.oustme.oustsdk.chart.data.BarData;
import com.oustme.oustsdk.chart.data.BarDataSet;
import com.oustme.oustsdk.chart.data.BarEntry;
import com.oustme.oustsdk.chart.data.CombinedData;
import com.oustme.oustsdk.chart.data.Entry;
import com.oustme.oustsdk.chart.data.LineData;
import com.oustme.oustsdk.chart.data.LineDataSet;
import com.oustme.oustsdk.chart.formatter.ValueFormatter;
import com.oustme.oustsdk.chart.highlight.Highlight;
import com.oustme.oustsdk.chart.listener.OnChartValueSelectedListener;
import com.oustme.oustsdk.skill_ui.adapter.SkillHistoryAdapter;
import com.oustme.oustsdk.skill_ui.model.SkillAnalyticsResponse;
import com.oustme.oustsdk.skill_ui.model.SoccerSkillLevelDataList;
import com.oustme.oustsdk.skill_ui.model.UserSkillLevelAnalyticsDataList;
import com.oustme.oustsdk.tools.ActiveUser;
import com.oustme.oustsdk.tools.HttpManager;
import com.oustme.oustsdk.tools.OustAppState;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.util.ApiCallUtils;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class SkillHistoryActivity extends AppCompatActivity {

    Toolbar toolbar_lay;
    FrameLayout toolbar_close_icon;
    TextView skill_title;
    CombinedChart skill_chart;
    RecyclerView history_list_rv;
    TextView score;
    TextView level;
    TextView submitted_time;

    String skillName = "";
    long skillId;
    ActiveUser activeUser;
    ArrayList<SoccerSkillLevelDataList> soccerSkillLevelDataLists;

    private int count = 0;
    private int levelMax = 0;
    protected String[] dates;
    long startRange = 1;
    long endRange = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        OustSdkTools.setLocale(SkillHistoryActivity.this);
        setContentView(R.layout.activity_skill_h_istory);

        submitted_time = findViewById(R.id.submitted_time);
        level = findViewById(R.id.level);
        score = findViewById(R.id.score);
        history_list_rv = findViewById(R.id.history_list_rv);
        skill_chart = findViewById(R.id.skill_chart);
        skill_title = findViewById(R.id.skill_title);
        toolbar_close_icon = findViewById(R.id.toolbar_close_icon);
        toolbar_lay = findViewById(R.id.toolbar_lay);


        String toolbarColorCode = OustPreferences.get("toolbarColorCode");
        toolbar_lay.setBackgroundColor(Color.parseColor(toolbarColorCode));

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            skillName = bundle.getString("skillName", "");
            skillId = bundle.getLong("skillId", 0);
        }

        initData();
    }

    private void initData() {
        activeUser = OustAppState.getInstance().getActiveUser();
        soccerSkillLevelDataLists = OustAppState.getInstance().getSoccerSkillLevelDataList();
        skill_title.setText(skillName);

        if (activeUser != null) {
            if (soccerSkillLevelDataLists == null) {
                soccerSkillLevelDataLists = new ArrayList<>();
            } else {
                ArrayList<Long> rangeArrayList = new ArrayList<>();
                levelMax = soccerSkillLevelDataLists.size();
                for (SoccerSkillLevelDataList soccerSkillLevelDataList : soccerSkillLevelDataLists) {
                    rangeArrayList.add(soccerSkillLevelDataList.getScoreStartRange());
                    rangeArrayList.add(soccerSkillLevelDataList.getScoreEndRange());

                }
                if (rangeArrayList.size() != 0) {
                    Collections.sort(rangeArrayList);
                    startRange = rangeArrayList.get(0);
                    endRange = rangeArrayList.get(rangeArrayList.size() - 1);
                }
            }
            apiCallForAnalytics();

        } else {
            SkillHistoryActivity.this.finish();
        }

        toolbar_close_icon.setOnClickListener(v -> SkillHistoryActivity.this.finish());
    }

    private void apiCallForAnalytics() {

        String skill_analytics_url = OustSdkApplication.getContext().getResources().getString(R.string.skill_analytics_url);
        skill_analytics_url = skill_analytics_url.replace("{userId}", activeUser.getStudentid());
        skill_analytics_url = skill_analytics_url.replace("{soccerSkillId}", "" + skillId);
        skill_analytics_url = skill_analytics_url + "?analyticsType=OVERALL";
        skill_analytics_url = HttpManager.getAbsoluteUrl(skill_analytics_url);
        JSONObject jsonParams = new JSONObject();

        ApiCallUtils.doNetworkCall(Request.Method.GET, skill_analytics_url, OustSdkTools.getRequestObjectforJSONObject(jsonParams),
                new ApiCallUtils.NetworkCallback() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.optBoolean("success")) {


                                SkillAnalyticsResponse skillAnalyticsResponse = new Gson().fromJson(response.toString(), SkillAnalyticsResponse.class);
                                if (skillAnalyticsResponse != null) {
                                    if (skillAnalyticsResponse.getUserSoccerSkillAnalyticsDataList() != null
                                            && skillAnalyticsResponse.getUserSoccerSkillAnalyticsDataList().size() != 0) {


                                        if (skillAnalyticsResponse.getUserSoccerSkillAnalyticsDataList().get(0) != null &&
                                                skillAnalyticsResponse.getUserSoccerSkillAnalyticsDataList().get(0).getUserSkillLevelAnalyticsDataList() != null
                                                && skillAnalyticsResponse.getUserSoccerSkillAnalyticsDataList().get(0).getUserSkillLevelAnalyticsDataList().size() != 0) {


                                            ArrayList<UserSkillLevelAnalyticsDataList> levelAnalyticsDataList = skillAnalyticsResponse.getUserSoccerSkillAnalyticsDataList().get(0).getUserSkillLevelAnalyticsDataList();
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                                               /* Collections.sort(userSkillDataArrayList,
                                                                       UserSkillData.skillDataComparator);*/
                                                Collections.sort(levelAnalyticsDataList,
                                                        Comparator.comparingLong(UserSkillLevelAnalyticsDataList::getSubmissionTimeInMillis));
                                            }
                                            ArrayList<UserSkillLevelAnalyticsDataList> updatedLevelAnalyticsDataList = new ArrayList<>();
                                            for (int index = 0; index < levelAnalyticsDataList.size(); index++) {
                                                long submissionTimeResponse = levelAnalyticsDataList.get(index).getSubmissionTimeInMillis();
                                                long responseScore = levelAnalyticsDataList.get(index).getUserScore();
                                                boolean isFound = false;
                                                if (submissionTimeResponse != 0) {

                                                    for (int e = 0; e < updatedLevelAnalyticsDataList.size(); e++) {

                                                        long submissionTimeFilter = updatedLevelAnalyticsDataList.get(e).getSubmissionTimeInMillis();
                                                        if (submissionTimeFilter != 0) {
                                                            String responseDate = getDate(submissionTimeResponse, "ddMMyyyy");
                                                            String filterDate = getDate(submissionTimeFilter, "ddMMyyyy");

                                                            if (responseDate.equalsIgnoreCase(filterDate) && responseScore > updatedLevelAnalyticsDataList.get(e).getUserScore()) {
                                                                isFound = true;
                                                                updatedLevelAnalyticsDataList.set(e, levelAnalyticsDataList.get(index));
                                                            } else if (responseDate.equalsIgnoreCase(filterDate) && responseScore <= updatedLevelAnalyticsDataList.get(e).getUserScore()) {
                                                                isFound = true;
                                                            }


                                                        }

                                                    }

                                                    if (!isFound) {
                                                        updatedLevelAnalyticsDataList.add(levelAnalyticsDataList.get(index));
                                                    }


                                                }


                                            }

                                            if (updatedLevelAnalyticsDataList.size() != 0) {


                                                dates = new String[updatedLevelAnalyticsDataList.size() + 1];
                                                for (int index = 0; index < updatedLevelAnalyticsDataList.size(); index++) {
                                                    dates[index] = updatedLevelAnalyticsDataList.get(index).getSubmissionTimeInMillis() + "";


                                                }
                                                dates[updatedLevelAnalyticsDataList.size()] = "";
                                                count = updatedLevelAnalyticsDataList.size();

                                                skill_chart.getDescription().setEnabled(false);
                                                skill_chart.setBackgroundColor(Color.WHITE);
                                                skill_chart.setDrawGridBackground(false);
                                                skill_chart.setDrawBarShadow(false);
                                                skill_chart.setHighlightFullBarEnabled(false);
                                                skill_chart.setVisibleXRangeMinimum(25);
                                                skill_chart.animateX(1000);
                                                skill_chart.animateY(1000);
                                                skill_chart.animateXY(1000, 1000);

                                                // draw bars behind lines
                                                skill_chart.setDrawOrder(new CombinedChart.DrawOrder[]{
                                                        CombinedChart.DrawOrder.BAR, CombinedChart.DrawOrder.LINE
                                                });

                                                Legend l = skill_chart.getLegend();
                                                l.setWordWrapEnabled(true);
                                                l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
                                                l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
                                                l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
                                                l.setDrawInside(false);
                                                LegendEntry l1 = new LegendEntry(getResources().getString(R.string.score_text) + "", Legend.LegendForm.DEFAULT, 10f, 2f, null, Color.rgb(1, 181, 162));
                                                LegendEntry l2 = new LegendEntry(getResources().getString(R.string.level) + "", Legend.LegendForm.DEFAULT, 10f, 2f, null, Color.rgb(248, 120, 0));


                                                l.setCustom(new LegendEntry[]{l1, l2});

                                                YAxis rightAxis = skill_chart.getAxisRight();
                                                rightAxis.setAxisMaximum(levelMax);
                                                rightAxis.setDrawGridLines(false);
                                                rightAxis.setGranularity(10f);
                                                rightAxis.setAxisMinimum(0f);

                                                YAxis leftAxis = skill_chart.getAxisLeft();
                                                leftAxis.setAxisMaximum(endRange);
                                                leftAxis.setDrawGridLines(false);
                                                leftAxis.setGranularityEnabled(false);
                                                rightAxis.setGranularity(1f);
                                                leftAxis.setAxisMinimum(0f);


                                                final int[] position = {0};


                                                XAxis xAxis = skill_chart.getXAxis();
                                                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                                                xAxis.setAxisMinimum(-2f);
                                                xAxis.setGranularity(5f);
                                                xAxis.setDrawGridLines(false);
                                                xAxis.setGranularityEnabled(true);
                                                xAxis.setLabelRotationAngle(-90);
                                                xAxis.setValueFormatter(new ValueFormatter() {

                                                    @Override
                                                    public String getFormattedValue(float value) {

                                                        float spaceForBar = 10f;
                                                        int valuePosition = (int) (value / spaceForBar);
                                                        int valueReminder = (int) (value % spaceForBar);


                                                        if (valueReminder == 0 && valuePosition < dates.length) {
                                                            String dateFormat = dates[valuePosition];
                                                            try {
                                                                long dateTime = Long.parseLong(dateFormat);
                                                                SimpleDateFormat responseDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault());
                                                                SimpleDateFormat displayDateFormat = new SimpleDateFormat("dd MMM", Locale.getDefault());
                                                                displayDateFormat.setTimeZone(TimeZone.getDefault());
                                                                Date date = responseDate.parse(getDate(dateTime, "yyyy-MM-dd HH:mm:ss.SSS"));
                                                                return displayDateFormat.format(date);


                                                            } catch (Exception e) {
                                                                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                                                                return "";
                                                            }
                                                            //return "";
                                                        } else {
                                                            return "";
                                                        }


                                                        // return dates[(int) value % dates.length];

                                                    }
                                                });

                                                CombinedData data = new CombinedData();
                                                data.setData(generateLineData(updatedLevelAnalyticsDataList));
                                                data.setData(generateBarData(updatedLevelAnalyticsDataList));


                                                xAxis.setAxisMaximum(data.getXMax() + 0.25f);

                                                skill_chart.setData(data);
                                                skill_chart.invalidate();

                                                skill_chart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
                                                    @Override
                                                    public void onValueSelected(Entry e, Highlight h) {

                                                        UserSkillLevelAnalyticsDataList userSoccerSkillDailyAnalyticsDataList = (UserSkillLevelAnalyticsDataList) e.getData();
                                                        long scoreValue = userSoccerSkillDailyAnalyticsDataList.getUserScore();
                                                        int levelIndex = 0;
                                                        for (int index = 0; index < soccerSkillLevelDataLists.size(); index++) {

                                                            if (scoreValue >= soccerSkillLevelDataLists.get(index).getScoreStartRange() &&
                                                                    scoreValue <= soccerSkillLevelDataLists.get(index).getScoreEndRange()) {
                                                                levelIndex = index;
                                                                break;
                                                            } else if (scoreValue < soccerSkillLevelDataLists.get(index).getScoreStartRange()) {
                                                                levelIndex = index - 1;
                                                                break;
                                                            } else if (scoreValue > soccerSkillLevelDataLists.get(index).getScoreEndRange()) {
                                                                levelIndex = soccerSkillLevelDataLists.size() - 1;

                                                            }

                                                        }

                                                        String levelName = soccerSkillLevelDataLists.get(levelIndex).getLevelName();
                                                        String levelvalue = levelName + "";
                                                        String levelString = levelName + " " + getResources().getString(R.string.level);
                                                        SpannableString ls1 = new SpannableString(levelString);
                                                        ls1.setSpan(new RelativeSizeSpan(0.5f), levelvalue.length(), levelString.length(), 0);
                                                        ls1.setSpan(new StyleSpan(BOLD), 0, levelvalue.length(), 0);
                                                        level.setText(ls1);


                                                        String value = scoreValue + "";
                                                        String scoreString = scoreValue + " " + getResources().getString(R.string.score_text);
                                                        SpannableString ss1 = new SpannableString(scoreString);
                                                        ss1.setSpan(new RelativeSizeSpan(0.5f), value.length(), scoreString.length(), 0);
                                                        ss1.setSpan(new StyleSpan(BOLD), 0, value.length(), 0);
                                                        score.setText(ss1);

                                                        SimpleDateFormat responseDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault());
                                                        SimpleDateFormat displayDate = new SimpleDateFormat("dd MMM yyyy hh:mm a", Locale.getDefault());
                                                        displayDate.setTimeZone(TimeZone.getDefault());
                                                        try {
                                                            Date responseDateDate = responseDate.parse(getDate(userSoccerSkillDailyAnalyticsDataList.getSubmissionTimeInMillis(), "yyyy-MM-dd HH:mm:ss.SSS"));
                                                            submitted_time.setText(displayDate.format(responseDateDate));

                                                        } catch (ParseException p) {
                                                            p.printStackTrace();
                                                            submitted_time.setText(userSoccerSkillDailyAnalyticsDataList.getUserSubmissionDateTime());
                                                        }
                                                        //       submitted_time.setText(userSoccerSkillDailyAnalyticsDataList.getEntryDate());

                                                    }

                                                    @Override
                                                    public void onNothingSelected() {

                                                    }
                                                });

                                                SkillHistoryAdapter adapter = new SkillHistoryAdapter();
                                                Collections.reverse(levelAnalyticsDataList);
                                                adapter.setSkillHistoryAdapter(levelAnalyticsDataList, SkillHistoryActivity.this);


                                                history_list_rv.setVisibility(View.VISIBLE);

                                                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(SkillHistoryActivity.this);
                                                history_list_rv.setLayoutManager(mLayoutManager);
                                                history_list_rv.setItemAnimator(new DefaultItemAnimator());
                                                history_list_rv.setAdapter(adapter);


                                            }


                                        }


                                    }
                                }


                            } else {
                                if (response.optString("error") != null && !response.optString("error").isEmpty()) {
                                    OustSdkTools.showToast(response.optString("error"));
                                } else {
                                    OustSdkTools.showToast(OustSdkApplication.getContext().getResources().getString(R.string.error_message));
                                }

                            }
                        } catch (Exception e) {
                            OustSdkTools.showToast(OustSdkApplication.getContext().getResources().getString(R.string.error_message));
                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        OustSdkTools.showToast(OustSdkApplication.getContext().getResources().getString(R.string.error_message));
                    }
                });

    }

    private LineData generateLineData(ArrayList<UserSkillLevelAnalyticsDataList> updatedLevelAnalyticsDataList) {

        LineData d = new LineData();

        ArrayList<Entry> entries = new ArrayList<>();
        float spaceForBar = 10f;

        if (updatedLevelAnalyticsDataList != null && updatedLevelAnalyticsDataList.size() != 0) {
            for (int index = 0; index < updatedLevelAnalyticsDataList.size(); index++) {

                long range = updatedLevelAnalyticsDataList.get(index).getUserScore();
                int levelIndex = 0;
                for (int i = 0; i < soccerSkillLevelDataLists.size(); i++) {


                       /* if (range >= soccerSkillLevelDataLists.get(i).getScoreStartRange() && range <= soccerSkillLevelDataLists.get(i).getScoreEndRange()) {
                            entries.add(new Entry(index * spaceForBar, i + 1, updatedLevelAnalyticsDataList.get(index)));
                            //break;
                        }
*/

                    if (range >= soccerSkillLevelDataLists.get(i).getScoreStartRange() && range <= soccerSkillLevelDataLists.get(i).getScoreEndRange()) {
                        levelIndex = i;
                        break;
                    } else if (range < soccerSkillLevelDataLists.get(i).getScoreStartRange()) {
                        levelIndex = i - 1;
                        break;
                    } else if (range > soccerSkillLevelDataLists.get(i).getScoreEndRange()) {
                        levelIndex = soccerSkillLevelDataLists.size() - 1;

                    }


                }

                entries.add(new Entry(index * spaceForBar, levelIndex + 1, updatedLevelAnalyticsDataList.get(index)));

            }

        }


        LineDataSet set = new LineDataSet(entries, getResources().getString(R.string.level) + "");
        set.setColor(Color.rgb(248, 120, 0));
        set.setLineWidth(2.5f);
        set.setCircleColor(Color.rgb(248, 120, 0));
        set.setCircleRadius(5f);
        set.setFillColor(Color.rgb(248, 120, 0));
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set.setDrawValues(false);
        set.setAxisDependency(YAxis.AxisDependency.RIGHT);


        d.addDataSet(set);

        return d;
    }

    private BarData generateBarData(ArrayList<UserSkillLevelAnalyticsDataList> updatedLevelAnalyticsDataList) {

        ArrayList<BarEntry> entries = new ArrayList<>();
        float barWidth = 2f;
        float spaceForBar = 10f;

        if (updatedLevelAnalyticsDataList != null && updatedLevelAnalyticsDataList.size() != 0) {
            for (int index = 0; index < updatedLevelAnalyticsDataList.size(); index++) {
                entries.add(new BarEntry(index * spaceForBar, updatedLevelAnalyticsDataList.get(index).getUserScore(), updatedLevelAnalyticsDataList.get(index)));
            }

            UserSkillLevelAnalyticsDataList userSoccerSkillDailyAnalyticsDataList = updatedLevelAnalyticsDataList.get(updatedLevelAnalyticsDataList.size() - 1);
            long scoreValue = userSoccerSkillDailyAnalyticsDataList.getUserScore();

            int levelIndex = 0;
            for (int index = 0; index < soccerSkillLevelDataLists.size(); index++) {
                if (scoreValue >= soccerSkillLevelDataLists.get(index).getScoreStartRange() &&
                        scoreValue <= soccerSkillLevelDataLists.get(index).getScoreEndRange()) {
                    levelIndex = index;
                    break;
                } else if (scoreValue < soccerSkillLevelDataLists.get(index).getScoreStartRange()) {
                    levelIndex = index - 1;
                    break;
                } else if (scoreValue > soccerSkillLevelDataLists.get(index).getScoreEndRange()) {
                    levelIndex = soccerSkillLevelDataLists.size() - 1;

                }

            }

            String levelName = soccerSkillLevelDataLists.get(levelIndex).getLevelName();
            String levelvalue = levelName + "";
            String levelString = levelName + " " + getResources().getString(R.string.level);
            SpannableString ls1 = new SpannableString(levelString);
            ls1.setSpan(new RelativeSizeSpan(0.5f), levelvalue.length(), levelString.length(), 0);
            ls1.setSpan(new StyleSpan(BOLD), 0, levelvalue.length(), 0);
            level.setText(ls1);


            String value = scoreValue + "";
            String scoreString = scoreValue + " " + getResources().getString(R.string.score_text);
            SpannableString ss1 = new SpannableString(scoreString);
            ss1.setSpan(new RelativeSizeSpan(0.5f), value.length(), scoreString.length(), 0);
            ss1.setSpan(new StyleSpan(BOLD), 0, value.length(), 0);
            score.setText(ss1);

            SimpleDateFormat responseDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault());
            SimpleDateFormat displayDate = new SimpleDateFormat("dd MMM yyyy hh:mm a", Locale.getDefault());
            displayDate.setTimeZone(TimeZone.getDefault());
            try {
                Date responseDateDate = responseDate.parse(getDate(userSoccerSkillDailyAnalyticsDataList.getSubmissionTimeInMillis(), "yyyy-MM-dd HH:mm:ss.SSS"));
                submitted_time.setText(displayDate.format(responseDateDate));

            } catch (ParseException p) {
                p.printStackTrace();
                submitted_time.setText(userSoccerSkillDailyAnalyticsDataList.getUserSubmissionDateTime());
            }


        }


        BarDataSet set1 = new BarDataSet(entries, getResources().getString(R.string.score_text) + "");
        set1.setColor(Color.rgb(1, 181, 162));
        set1.setDrawValues(false);
        set1.setDrawIcons(false);
        set1.setAxisDependency(YAxis.AxisDependency.LEFT);


        BarData d = new BarData(set1);
        d.setBarWidth(barWidth);


        // make this BarData object grouped
        // d.groupBars(0, groupSpace, barSpace); // start at x = 0

        return d;
    }

    public static String getDate(long milliSeconds, String dateFormat) {
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat, Locale.getDefault());

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }


}
