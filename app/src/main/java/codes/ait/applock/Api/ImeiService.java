package codes.ait.applock.Api;

import io.reactivex.Single;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ImeiService {
    @POST("imei")
    Single<ApiResult> addImei(@Body() Imei imei);
}
