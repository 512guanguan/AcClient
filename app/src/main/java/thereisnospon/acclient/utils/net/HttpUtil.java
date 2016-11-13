package thereisnospon.acclient.utils.net;

import okhttp3.OkHttpClient;
import thereisnospon.acclient.AppApplication;
import thereisnospon.acclient.utils.net.cookie.CookiesManager;
import thereisnospon.acclient.utils.net.request.GetAuthRequest;
import thereisnospon.acclient.utils.net.request.GetRequest;
import thereisnospon.acclient.utils.net.request.PostRequest;

/**
 * Created by yzr on 16/6/5.
 */
public final class HttpUtil {

    public static final String TAG="HttpUtil";
    private OkHttpClient client;
    private static HttpUtil instance;


    private CookiesManager cookiesManager;


    public CookiesManager getCookiesManager() {
        return cookiesManager;
    }

    private HttpUtil(){

        cookiesManager=new CookiesManager(AppApplication.context);
//        Log.d("NetActivityXXX","UTIL THRAD"+Thread.currentThread().getName());
        client=new OkHttpClient.Builder()
                .cookieJar(cookiesManager).build();
    }

    public static HttpUtil getInstance(){
        if(instance==null){
            instance=new HttpUtil();
        }
        return  instance;
    }

    public  OkHttpClient.Builder newBuilder(){
        return client.newBuilder();
    }

    public GetAuthRequest authGet(String url){
        return new GetAuthRequest(url,client);
    }

    public GetRequest get(String url){
        return new GetRequest(url,client);
    }


    public PostRequest post(String url){

        //Log.d("NetActivityXXX","POST THREAD"+Thread.currentThread().getName());
        return new PostRequest(url,client);}
}