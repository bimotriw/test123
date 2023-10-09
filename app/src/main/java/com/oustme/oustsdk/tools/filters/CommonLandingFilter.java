package com.oustme.oustsdk.tools.filters;

import com.oustme.oustsdk.activity.common.noticeBoard.model.response.NBTopicData;
import com.oustme.oustsdk.firebase.common.CommonLandingData;
import com.oustme.oustsdk.firebase.common.CplModelData;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.ThreadPoolProvider;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Future;


/**
 * Created by admin on 12/10/17.
 */

public class CommonLandingFilter {

    public Future<List<CommonLandingData>> meetCriteria(List<CommonLandingData> allData, String searchText) {
        List<CommonLandingData> filterData = new ArrayList<>();
        return ThreadPoolProvider.getInstance().getFixedThreadExecutor().submit(() -> {
            for (CommonLandingData frd : allData) {
                if (frd.getName() != null) {
                    if ((frd.getName().toLowerCase().contains(searchText.toLowerCase()))) {
                        filterData.add(frd);
                    }
                }
            }
            return filterData;
        });

    }

    public Future<List<CommonLandingData>> pendingDataMeetCriteria(List<CommonLandingData> allData) {
        List<CommonLandingData> filterData = new ArrayList<>();
        return ThreadPoolProvider.getInstance().getFixedThreadExecutor().submit(() -> {
            for (CommonLandingData frd : allData) {
                if ((frd.getCompletionPercentage() < 100)) {
                    filterData.add(frd);
                }
            }
            return filterData;
        });

    }

    public List<CommonLandingData> completedDataMeetCriteria(List<CommonLandingData> allData) {
        List<CommonLandingData> filterData = new ArrayList<>();
        for (CommonLandingData frd : allData) {
            if ((frd.getCompletionPercentage() == 100)) {
                filterData.add(frd);
            }
        }
        return filterData;
    }

    public ArrayList<CommonLandingData> getpendingDataMeetCriteria(List<CommonLandingData> allData) {
        ArrayList<CommonLandingData> filterData = new ArrayList<>();
        for (CommonLandingData frd : allData) {
            if ((frd.getCompletionPercentage() < 100)) {
                filterData.add(frd);
            }
        }
        return filterData;
    }

    public ArrayList<CommonLandingData> getcompletedDataMeetCriteria(List<CommonLandingData> allData) {
        ArrayList<CommonLandingData> filterData = new ArrayList<>();
        for (CommonLandingData frd : allData) {
            if ((frd.getCompletionPercentage() == 100)) {
                filterData.add(frd);
            }
        }
        return filterData;
    }

    public ArrayList<CommonLandingData> getSortedDataByLearnType(List<CommonLandingData> allData) {
        ArrayList<CommonLandingData> filterData = new ArrayList<>();
        for (CommonLandingData frd : allData) {
            if (frd.getLanding_data_type() == null) {
                frd.setLanding_data_type("Learn");
            }
            if ((frd.getLanding_data_type().equalsIgnoreCase("Learn"))) {
                filterData.add(frd);
            }
        }
        return filterData;
    }

    public ArrayList<CommonLandingData> getSortedDataByGrowType(List<CommonLandingData> allData) {
        ArrayList<CommonLandingData> filterData = new ArrayList<>();
        for (CommonLandingData frd : allData) {
            if (frd.getLanding_data_type() == null) {
                frd.setLanding_data_type("Learn");
            }
            if ((frd.getLanding_data_type().equalsIgnoreCase("Grow"))) {
                filterData.add(frd);
            }
        }
        return filterData;
    }

    public ArrayList<CommonLandingData> getSortedDataByHandPickedType(ArrayList<CommonLandingData> allData) {
        ArrayList<CommonLandingData> filterData = new ArrayList<>();
        for (CommonLandingData frd : allData) {
            if (frd.getLanding_data_type() == null) {
                frd.setLanding_data_type("Learn");
            }
            if ((frd.getLanding_data_type().equalsIgnoreCase("HandPicked"))) {
                filterData.add(frd);
            }
        }
        return filterData;
    }

    public ArrayList<CplModelData> getCplDataModels(HashMap<String, CplModelData> cplModelDataHashMap) {
        ArrayList<CplModelData> cplModelDataArrayList = new ArrayList<>();
        for (Map.Entry entry : cplModelDataHashMap.entrySet()) {
            CplModelData cplModelData = (CplModelData) entry.getValue();
            if (cplModelData != null && cplModelData.isListenerSet())
                cplModelDataArrayList.add((CplModelData) entry.getValue());
        }
        return cplModelDataArrayList;
    }

    public ArrayList<CommonLandingData> getTagCourses(ArrayList<CommonLandingData> allData, String tag) {
        if (tag.equalsIgnoreCase("all")) {
            return allData;
        }
        ArrayList<CommonLandingData> filterData = new ArrayList<>();
        if (allData != null) {
            for (CommonLandingData frd : allData) {
                if (frd.getCourseTags() != null && (frd.getCourseTags().contains(tag))) {
                    filterData.add(frd);
                }
            }
        }
        return filterData;
    }

    public Future<ArrayList<CommonLandingData>> getPendingModules(List<CommonLandingData> allData) {
        ArrayList<CommonLandingData> filterData = new ArrayList<>();
        return ThreadPoolProvider.getInstance().getFixedThreadExecutor().submit(() -> {
            for (CommonLandingData frd : allData) {
                if ((frd.getCompletionPercentage() < 100 && frd.getCompletionPercentage() > 0)) {
                    filterData.add(frd);
                }
            }
            return filterData;
        });

    }

    public Future<ArrayList<CommonLandingData>> getCompletedModules(List<CommonLandingData> allData) {
        ArrayList<CommonLandingData> filterData = new ArrayList<>();
        return ThreadPoolProvider.getInstance().getFixedThreadExecutor().submit(() -> {
            for (CommonLandingData frd : allData) {
                if ((frd.getCompletionPercentage() == 100)) {
                    filterData.add(frd);
                }
            }
            return filterData;
        });

    }

    public HashMap<String, ArrayList<CommonLandingData>> getCourseModulesHashMap(List<CommonLandingData> allData) {
        HashMap<String, ArrayList<CommonLandingData>> filterData = new HashMap<>();
        for (CommonLandingData frd : allData) {
            if (frd.getAddedOn() != null && !frd.getAddedOn().isEmpty()) {
                try {
                    long addedOn = Long.parseLong(frd.getAddedOn());
                    if (addedOn != 0) {
                        Date date = new Date(addedOn);
                        String dateKey = new SimpleDateFormat("yyyyMMdd", Locale.US).format(date);
                        if (filterData.size() != 0) {
                            if (filterData.get(dateKey) == null) {
                                filterData.put(dateKey, new ArrayList<>());
                            }
                        } else {
                            filterData.put(dateKey, new ArrayList<>());
                        }
                        Objects.requireNonNull(filterData.get(dateKey)).add(frd);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    OustSdkTools.sendSentryException(e);
                }

            } else {
                String dateKey = new SimpleDateFormat("yyyyMMdd", Locale.US).format(new Date());
                if (filterData.size() != 0) {
                    if (filterData.get(dateKey) == null) {
                        filterData.put(dateKey, new ArrayList<>());
                    }
                } else {
                    filterData.put(dateKey, new ArrayList<>());
                }
                Objects.requireNonNull(filterData.get(dateKey)).add(frd);
            }
        }
        return filterData;
    }

    public HashMap<String, ArrayList<CommonLandingData>> getCplModulesHashMap(List<CommonLandingData> allData) {
        HashMap<String, ArrayList<CommonLandingData>> filterData = new HashMap<>();
        for (CommonLandingData frd : allData) {
            if (frd.getAddedOn() != null && !frd.getAddedOn().isEmpty()) {
                try {
                    long addedOn = Long.parseLong(frd.getAddedOn());
                    if (addedOn != 0) {
                        Date date = new Date(addedOn);
                        String dateKey = new SimpleDateFormat("yyyyMMdd", Locale.US).format(date);
                        if (filterData.size() != 0) {
                            if (filterData.get(dateKey) == null) {
                                filterData.put(dateKey, new ArrayList<>());
                            }
                        } else {
                            filterData.put(dateKey, new ArrayList<>());
                        }
                        Objects.requireNonNull(filterData.get(dateKey)).add(0, frd);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    OustSdkTools.sendSentryException(e);
                }

            } else {
                String dateKey = new SimpleDateFormat("yyyyMMdd", Locale.US).format(new Date());
                if (filterData.size() != 0) {
                    if (filterData.get(dateKey) == null) {
                        filterData.put(dateKey, new ArrayList<>());
                    }
                } else {
                    filterData.put(dateKey, new ArrayList<>());
                }
                Objects.requireNonNull(filterData.get(dateKey)).add(frd);
            }
        }
        return filterData;
    }

    public HashMap<String, ArrayList<CommonLandingData>> getContestModulesHashMap(List<CommonLandingData> allData) {
        HashMap<String, ArrayList<CommonLandingData>> filterData = new HashMap<>();
        for (CommonLandingData frd : allData) {
            if (frd.getAddedOn() != null && !frd.getAddedOn().isEmpty()) {
                try {
                    long addedOn = Long.parseLong(frd.getAddedOn());
                    if (addedOn != 0) {
                        Date date = new Date(addedOn);
                        String dateKey = new SimpleDateFormat("yyyyMMdd", Locale.US).format(date);
                        if (filterData.size() != 0) {
                            if (filterData.get(dateKey) == null) {
                                filterData.put(dateKey, new ArrayList<>());
                            }
                        } else {
                            filterData.put(dateKey, new ArrayList<>());
                        }
                        Objects.requireNonNull(filterData.get(dateKey)).add(frd);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    OustSdkTools.sendSentryException(e);
                }

            } else {
                String dateKey = new SimpleDateFormat("yyyyMMdd", Locale.US).format(new Date());
                if (filterData.size() != 0) {
                    if (filterData.get(dateKey) == null) {
                        filterData.put(dateKey, new ArrayList<>());
                    }
                } else {
                    filterData.put(dateKey, new ArrayList<>());
                }
                Objects.requireNonNull(filterData.get(dateKey)).add(frd);
            }
        }
        return filterData;
    }

    public Future<ArrayList<CommonLandingData>> getNewModules(List<CommonLandingData> allData) {
        ArrayList<CommonLandingData> filterData = new ArrayList<>();
        return ThreadPoolProvider.getInstance().getFixedThreadExecutor().submit(() -> {
            for (CommonLandingData frd : allData) {
                if ((frd.getCompletionPercentage() == 0)) {
                    filterData.add(frd);
                }
            }
            return filterData;
        });
    }

    public HashMap<String, ArrayList<CommonLandingData>> getAssessmentModulesHashMap(List<CommonLandingData> allData) {
        HashMap<String, ArrayList<CommonLandingData>> filterData = new HashMap<>();
        for (CommonLandingData frd : allData) {
            if (frd.getAddedOn() != null && !frd.getAddedOn().isEmpty()) {
                try {
                    long addedOn = Long.parseLong(frd.getAddedOn());
                    if (addedOn != 0) {
                        Date date = new Date(addedOn);
                        String dateKey = new SimpleDateFormat("yyyyMMdd", Locale.US).format(date);
                        if (filterData.size() != 0) {
                            if (filterData.get(dateKey) == null) {
                                filterData.put(dateKey, new ArrayList<>());
                            }
                        } else {
                            filterData.put(dateKey, new ArrayList<>());
                        }
                        Objects.requireNonNull(filterData.get(dateKey)).add(frd);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    OustSdkTools.sendSentryException(e);
                }

            } else {
                String dateKey = new SimpleDateFormat("yyyyMMdd", Locale.US).format(new Date());
                if (filterData.size() != 0) {
                    if (filterData.get(dateKey) == null) {
                        filterData.put(dateKey, new ArrayList<>());
                    }
                } else {
                    filterData.put(dateKey, new ArrayList<>());
                }
                Objects.requireNonNull(filterData.get(dateKey)).add(frd);
            }
        }
        return filterData;
    }

    public Future<ArrayList<CommonLandingData>> getCourseModules(List<CommonLandingData> allData) {
        ArrayList<CommonLandingData> filterData = new ArrayList<>();
        return ThreadPoolProvider.getInstance().getFixedThreadExecutor().submit(() -> {
            for (CommonLandingData frd : allData) {
                if ((frd.getCompletionPercentage() < 100 && frd.getCompletionPercentage() >= 0) && frd.getType() != null &&
                        (frd.getType().equalsIgnoreCase("course") ||
                                frd.getType().equalsIgnoreCase("CLASSROOM") || frd.getType().equalsIgnoreCase("WEBINAR"))) {
                    filterData.add(frd);
                }
            }
            return filterData;
        });
    }

    public Future<ArrayList<CommonLandingData>> getAssessmentModules(List<CommonLandingData> allData) {
        ArrayList<CommonLandingData> filterData = new ArrayList<>();
        return ThreadPoolProvider.getInstance().getFixedThreadExecutor().submit(() -> {
            for (CommonLandingData frd : allData) {
                if ((frd.getCompletionPercentage() >= 0) && frd.getType() != null && frd.getType().equalsIgnoreCase("Assessment")) {
                    filterData.add(frd);
                }
            }
            return filterData;
        });
    }

    public Future<ArrayList<CommonLandingData>> getCplModules(List<CommonLandingData> allData) {
        ArrayList<CommonLandingData> filterData = new ArrayList<>();
        return ThreadPoolProvider.getInstance().getFixedThreadExecutor().submit(() -> {
            for (CommonLandingData frd : allData) {
                if ((frd.getCompletionPercentage() < 100 && frd.getCompletionPercentage() >= 0) && frd.getType() != null && (frd.getType().equalsIgnoreCase("CPL") || frd.getType().equalsIgnoreCase("MULTILINGUAL"))) {
                    filterData.add(frd);
                }
            }
            return filterData;
        });
    }

    public Future<ArrayList<CommonLandingData>> getFFFCModules(List<CommonLandingData> allData) {
        ArrayList<CommonLandingData> filterData = new ArrayList<>();
        return ThreadPoolProvider.getInstance().getFixedThreadExecutor().submit(() -> {
            for (CommonLandingData frd : allData) {
                if (frd.getType() != null && frd.getType().equalsIgnoreCase("FFF_CONTEXT")) {
                    filterData.add(frd);
                }
            }
            return filterData;
        });
    }

    public Future<ArrayList<CommonLandingData>> getSurveyModules(List<CommonLandingData> allData) {
        ArrayList<CommonLandingData> filterData = new ArrayList<>();
        return ThreadPoolProvider.getInstance().getFixedThreadExecutor().submit(() -> {
            for (CommonLandingData frd : allData) {
                if ((frd.getCompletionPercentage() < 100 && frd.getCompletionPercentage() >= 0) && frd.getType() != null && frd.getType().equalsIgnoreCase("Survey")) {
                    filterData.add(frd);
                }
            }
            return filterData;
        });

    }

    public Future<List<NBTopicData>> noticeBoardCriteria(List<NBTopicData> nbTopicDataArrayList, String searchText) {
        List<NBTopicData> filterData = new ArrayList<>();
        return ThreadPoolProvider.getInstance().getFixedThreadExecutor().submit(() -> {
            for (NBTopicData frd : nbTopicDataArrayList) {
                if (frd.getTopic() != null) {
                    if ((frd.getTopic().toLowerCase().contains(searchText.toLowerCase()))) {
                        filterData.add(frd);
                    }
                }
            }
            return filterData;
        });

    }

    public Future<ArrayList<CommonLandingData>> getCatalogueModules(ArrayList<CommonLandingData> allList) {
        ArrayList<CommonLandingData> filterData = new ArrayList<>();
        return ThreadPoolProvider.getInstance().getFixedThreadExecutor().submit(() -> {
            for (CommonLandingData frd : allList) {
                if (frd.getType() != null && frd.getType().equalsIgnoreCase("CATALOGUE")) {
                    filterData.add(frd);
                }
            }
            return filterData;
        });

    }
}
