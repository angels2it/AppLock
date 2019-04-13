package applock.mindorks.com.applock.Api;

import io.reactivex.Single;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface HomeService {
    @GET("config")
    Single<ConfigResult> config();
}
