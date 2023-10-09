package com.oustme.oustsdk.interfaces.common;

import com.oustme.oustsdk.response.common.Questions;

public interface FavouriteCallback {
     void onFavouriteCallback(Questions questions);
     void onReachToEnd();
}