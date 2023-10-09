package com.oustme.oustsdk.response.common;

import androidx.annotation.Keep;

import com.oustme.oustsdk.response.course.CommonResponse;

/**
 * Created by admin on 08/04/18.
 */

@Keep
public class VerifyOrgIdResponseA extends CommonResponse {
    private String authType;
    private boolean validTenant;
    private String samlSSOURL;


    public String getAuthType() {
        return authType;
    }

    public void setAuthType(String authType) {
        this.authType = authType;
    }

    public boolean isValidTenant() {
        return validTenant;
    }

    public void setValidTenant(boolean validTenant) {
        this.validTenant = validTenant;
    }

    public String getSamlSSOURL() {
        return samlSSOURL;
    }

    public void setSamlSSOURL(String samlSSOURL) {
        this.samlSSOURL = samlSSOURL;
    }


    private String s3_base_end;
    private String http_img_bucket_cdn;
    private String img_bucket_cdn;
    private String img_bucket_name;
    private String s3_Bucket_Region;
    private String awsS3KeyId;
    private String awsS3KeySecret;
    private String webAppLink;

    public String getS3_base_end() {
        return s3_base_end;
    }

    public void setS3_base_end(String s3_base_end) {
        this.s3_base_end = s3_base_end;
    }

    public String getHttp_img_bucket_cdn() {
        return http_img_bucket_cdn;
    }

    public void setHttp_img_bucket_cdn(String http_img_bucket_cdn) {
        this.http_img_bucket_cdn = http_img_bucket_cdn;
    }

    public String getImg_bucket_cdn() {
        return img_bucket_cdn;
    }

    public void setImg_bucket_cdn(String img_bucket_cdn) {
        this.img_bucket_cdn = img_bucket_cdn;
    }

    public String getImg_bucket_name() {
        return img_bucket_name;
    }

    public void setImg_bucket_name(String img_bucket_name) {
        this.img_bucket_name = img_bucket_name;
    }

    public String getS3_Bucket_Region() {
        return s3_Bucket_Region;
    }

    public void setS3_Bucket_Region(String s3_Bucket_Region) {
        this.s3_Bucket_Region = s3_Bucket_Region;
    }

    public String getAwsS3KeyId() {
        return awsS3KeyId;
    }

    public void setAwsS3KeyId(String awsS3KeyId) {
        this.awsS3KeyId = awsS3KeyId;
    }

    public String getAwsS3KeySecret() {
        return awsS3KeySecret;
    }

    public void setAwsS3KeySecret(String awsS3KeySecret) {
        this.awsS3KeySecret = awsS3KeySecret;
    }

    public String getWebAppLink() {
        return webAppLink;
    }

    public void setWebAppLink(String webAppLink) {
        this.webAppLink = webAppLink;
    }
}
