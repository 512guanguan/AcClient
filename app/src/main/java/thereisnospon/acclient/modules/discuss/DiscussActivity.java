package thereisnospon.acclient.modules.discuss;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.widget.FrameLayout;

import thereisnospon.acclient.R;
import thereisnospon.acclient.base.activity.AppBarActivity;
import thereisnospon.acclient.event.Arg;

/**
 * @author thereisnospon
 * 讨论模块的 Activity
 * Created by yzr on 16/9/9.
 */
public final class DiscussActivity extends AppBarActivity {



    @Override
    protected void setupContent(@NonNull FrameLayout contentLayout) {
        String pid=getIntent().getStringExtra(Arg.PROBLEM_DISUCSS);
        if(pid==null){//加载讨论模块首页
            setupFragment(contentLayout.getId(), DiscussFragment.newInstance());
        }else{//指定题目的讨论区
            setupFragment(contentLayout.getId(), DiscussFragment.newInstance(pid));
        }
    }

	public static void showInstance(@NonNull Activity cxt) {
		Intent intent = new Intent(cxt, DiscussActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
		ActivityCompat.startActivity(cxt,
		                             intent,
		                             ActivityOptionsCompat.makeBasic()
		                                                  .toBundle());
	}

    public static void showInstance(@NonNull Activity cxt, int id, @NonNull ActivityOptionsCompat options) {
        Intent intent = new Intent(cxt, DiscussActivity.class);
        intent.putExtra(Arg.PROBLEM_DISUCSS, String.valueOf(id));
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        ActivityCompat.startActivity(cxt, intent, options.toBundle());
    }




}
