package codes.ait.applock.Api;

import io.reactivex.Single;
import retrofit2.http.GET;

public interface HomeService {
    @GET("config")
    Single<ConfigResult> config();
}
