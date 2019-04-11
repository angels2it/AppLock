package applock.mindorks.com.applock.Api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Imei {
    @Expose
    @SerializedName("code")
    private String code;

    public Imei () {

    }
    public  Imei (String code) {
        this.code = code;
    }
}
