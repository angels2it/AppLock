package applock.mindorks.com.applock.Api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ConfigResult extends ApiResult {
    @Expose
    @SerializedName("password")
    public String password;

    @Expose
    @SerializedName("apps")
    public String[] apps;
}