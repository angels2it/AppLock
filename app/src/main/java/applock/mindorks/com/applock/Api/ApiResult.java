package applock.mindorks.com.applock.Api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ApiResult {
    @Expose
    @SerializedName("success")
    public boolean success;
}