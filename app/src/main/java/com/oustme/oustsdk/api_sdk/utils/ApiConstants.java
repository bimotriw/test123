package com.oustme.oustsdk.api_sdk.utils;

import androidx.annotation.Keep;

/**
 * Created by oust on 5/2/19.
 */

@Keep
public class ApiConstants {
    //public static String BASE_API_URL_EVENT = "https://stage-eventapi-india.oustme.com:443/event/";
    //public static String BASE_API_URL = "https://stage-eventapi-india.oustme.com:443/event/";

    public static String BASE_API_URL_EVENT = "http://prod-oust-central-event-api.oustme.com/event/";
    public static String BASE_API_URL = "http://prod-oust-central-event-api.oustme.com/event/";

    //public static String BASE_API_URL_EVENT = "https://eventapi-india.oustme.com/event/";
    //public static String BASE_API_URL = "https://eventapi-india.oustme.com/event/";

    //public static String BASE_API_URL_EVENT = "https://dev-event.oustme.com:443/event/";
    //public static String BASE_API_URL = "https://dev-event.oustme.com:443/event/";

    public static String NEXT_EVENT_URL = BASE_API_URL+"nextEvent";
    public static String GET_STATUS_URL = BASE_API_URL+"getEventStatus_v2";

    public static String GET_EVENT_STATUS_URL = BASE_API_URL_EVENT+"v3/getEventStatus";
    public static String PUT_EVENT_STATUS_URL = BASE_API_URL_EVENT+"v3/setEventCardStatus";

    //public static String GET_EVENT_DATA_URL = BASE_API_URL+"v3/getEventStatus";
}
