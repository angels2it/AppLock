package codes.ait.applock.Api;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiInstance {
    public static Retrofit retrofit;
    public static ImeiService imeiService;
    public static HomeService homeService;
    public  static  void  Init() {
        retrofit = new Retrofit.Builder()
                .baseUrl("http://api.applock.ezs.network/v1/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        // init service
        imeiService = retrofit.create(ImeiService.class);
        homeService = retrofit.create(HomeService.class);
    }
}
