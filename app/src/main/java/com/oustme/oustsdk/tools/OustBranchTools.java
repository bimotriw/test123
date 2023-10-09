package com.oustme.oustsdk.tools;

import android.app.Activity;
import android.util.Log;




import io.branch.referral.Branch;
import io.branch.referral.BranchError;
import io.branch.referral.BranchShortLinkBuilder;


/**
 * Created with IntelliJ IDEA.
 * User: rajatsekhar
 * Date: 03/09/15
 * Time: 12:29 PM
 * To change this template use File | Settings | File Templates.
 */
public class OustBranchTools {
    private static final Object monitor = new Object();
    private static OustBranchTools branchTools = null;

    public OustBranchTools() {
    }

    public static OustBranchTools getInstance() {
        if (branchTools == null) {
            synchronized (monitor) {
                if (branchTools == null)
                    branchTools = new OustBranchTools();
            }
        }
        return branchTools;
    }


    private static final String TAG = OustBranchTools.class.getName();
      public static String generateLink(Activity activity, String tag1, String tag2,  String channel, String feature,
                                        String gameId, String challengerId, String challengerDisplayName, String challengerAvatar,
                                        String opponentId, String opponentDisplayName, String opponentAvatar,
                                        String grade, String subject, String moduleId,
                                        String uniqueId, String title, String description, String imageURL,String assessmentId) {

          if(challengerDisplayName == null || challengerDisplayName.length() == 0) {
              challengerDisplayName = challengerId;
          }
          if(opponentDisplayName == null || opponentDisplayName.length() == 0) {
              opponentDisplayName = opponentId;
          }
          String referCode = OustPreferences.get("referCode");
          String tanentId=OustPreferences.get("tanentid");
          BranchShortLinkBuilder shortUrlBuilder = new BranchShortLinkBuilder(activity)
                  .addTag(tag1)
                  .addTag(tag2)
                  .setChannel(channel)
                  .setFeature(feature)
                  .setStage("1")
                  .addParameters("jhdg","loginpage")
                  .addParameters("gameid", gameId) // deeplink data - anything you want!
                  .addParameters("challengerId", challengerId) // deeplink data - anything you want!
                  .addParameters("challengerDisplayName", challengerDisplayName) // deeplink data - anything you want!
                  .addParameters("challengerAvatar", challengerAvatar) // deeplink data - anything you want!
                  .addParameters("opponentId", opponentId) // deeplink data - anything you want!
                  .addParameters("opponentDisplayName", opponentDisplayName) // deeplink data - anything you want!
                  .addParameters("opponentAvatar", opponentAvatar) // deeplink data - anything you want!
                  .addParameters("grade", grade) // deeplink data - anything you want!
                  .addParameters("subject", subject) // deeplink data - anything you want!
                  .addParameters("moduleId", moduleId) // deeplink data - anything you want!
                  .addParameters("uniqueid", uniqueId) // deeplink data - anything you want!
                  .addParameters("referCode", referCode) // deeplink data - anything you want!
                  .addParameters("$og_title", title)
                  .addParameters("$og_description", description)
                  .addParameters("$og_image_url", imageURL)
                  .addParameters("tanentid", tanentId)
                  .addParameters("assessmentId",assessmentId);

          // Get URL Asynchronously
          shortUrlBuilder.generateShortUrl(new Branch.BranchLinkCreateListener() {
              @Override
              public void onLinkCreate(String url, BranchError error) {
                  if (error != null) {
                      Log.e(TAG, "Branch create short url failed. Caused by -" + error.getMessage());
                  } else {
                      Log.i(TAG, "Got a Branch URL " + url);
                  }
              }
          });

          String myUrl = "http://bit.ly/1xEh2HW";
          // OR Get the URL synchronously
          myUrl = shortUrlBuilder.getShortUrl();
          if(null == myUrl) {
              myUrl = "http://bit.ly/1xEh2HW";
          }
          return  myUrl;
      }

    public static BranchShortLinkBuilder getShortUrlBuilder(Activity activity, String tag1, String tag2,  String channel, String feature,
                                      String gameId, String challengerId, String challengerDisplayName, String challengerAvatar,
                                      String opponentId, String opponentDisplayName, String opponentAvatar,
                                      String grade, String subject, String moduleId,
                                      String uniqueId, String title, String description, String imageURL,String assessmentId) {

        if(challengerDisplayName == null || challengerDisplayName.length() == 0) {
            challengerDisplayName = challengerId;
        }
        if(opponentDisplayName == null || opponentDisplayName.length() == 0) {
            opponentDisplayName = opponentId;
        }
        String referCode = OustPreferences.get("referCode");
        String tanentId=OustPreferences.get("tanentid");
        BranchShortLinkBuilder shortUrlBuilder = new BranchShortLinkBuilder(activity)
                .addTag(tag1)
                .addTag(tag2)
                .setChannel(channel)
                .setFeature(feature)
                .setStage("1")
                .addParameters("jhdg","loginpage")
                .addParameters("gameid", gameId) // deeplink data - anything you want!
                .addParameters("challengerId", challengerId) // deeplink data - anything you want!
                .addParameters("challengerDisplayName", challengerDisplayName) // deeplink data - anything you want!
                .addParameters("challengerAvatar", challengerAvatar) // deeplink data - anything you want!
                .addParameters("opponentId", opponentId) // deeplink data - anything you want!
                .addParameters("opponentDisplayName", opponentDisplayName) // deeplink data - anything you want!
                .addParameters("opponentAvatar", opponentAvatar) // deeplink data - anything you want!
                .addParameters("grade", grade) // deeplink data - anything you want!
                .addParameters("subject", subject) // deeplink data - anything you want!
                .addParameters("moduleId", moduleId) // deeplink data - anything you want!
                .addParameters("uniqueid", uniqueId) // deeplink data - anything you want!
                .addParameters("referCode", referCode) // deeplink data - anything you want!
                .addParameters("$og_title", title)
                .addParameters("$og_description", description)
                .addParameters("$og_image_url", imageURL)
                .addParameters("tanentid", tanentId)
                .addParameters("assessmentId",assessmentId);

        // Get URL Asynchronously
        return  shortUrlBuilder;
    }


}
