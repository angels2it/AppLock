package codes.ait.applock.Data;

import java.util.Arrays;
import java.util.List;

public class AppConfig {
    public String password;
    public List<String> apps;

    public AppConfig () {
        password = "123456";
        apps = Arrays.asList("com.google.camera");
    }
}
