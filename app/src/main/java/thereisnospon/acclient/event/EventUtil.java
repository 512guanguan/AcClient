package thereisnospon.acclient.event;

import android.os.Handler;
import android.os.Looper;

import org.greenrobot.eventbus.EventBus;

/**
 * @author  thereisnospon
 * 用来往主线程发消息的工具类
 * Created by yzr on 16/6/5.
 */
public class EventUtil {

    public static void postEvent(Event event){
        EventBus.getDefault().post(event);
    }

    public static void posetEventOnMainThread(final Event event){
        Handler handler=new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                EventBus.getDefault().post(event);
            }
        });
    }

}
