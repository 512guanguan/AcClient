package thereisnospon.acclient.base.fragment;

import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;
import thereisnospon.acclient.R;
import thereisnospon.acclient.base.adapter.BasePullAdapter;

/**
 * Created by yzr on 16/8/20.
 * 需要 下拉刷新，上拉加载，进入自动初始化加载的功能的 Fragment 的基础类
 */
public abstract class BasePullFragment<T> extends BaseFragment
        implements SwipeRefreshLayout.OnRefreshListener{

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private BasePullAdapter<T> mSwipeAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<T> mDataList;

    public static final int ACTION_PULL_TO_REFRESH = 1;
    public static final int ACTION_LOAD_MORE = 2;
    public static final int ACTION_IDLE = 0;
    private int mCurrentState = ACTION_IDLE;//RecyclerView.SCROLL_STATE_IDLE;


    private boolean isLoadMoreEnabled = true;
    private boolean isPullToRefreshEnabled = true;

    // 子类实现 RecycleView 显示的 Adapter
    public abstract BasePullAdapter<T> createItemAdapter(List<T>list);
    public abstract RecyclerView.LayoutManager createLayoutManager();

    //需要加载更多数据时回调
    public abstract void loadMore();
    //需要刷新数据时回调
    public abstract void refresh();


    public void initRefreshViews(View parent, @IdRes int swiperRefreshLayout,
                          @IdRes int recycleView){

        mSwipeRefreshLayout = (SwipeRefreshLayout) parent.findViewById(swiperRefreshLayout);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary, R.color.colorPrimaryDark, R.color.colorAccent, R.color.colorGreen);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mRecyclerView = (RecyclerView) parent.findViewById(recycleView);

        mDataList =new ArrayList<>();
        setAdapter(createItemAdapter(mDataList));

        setLayoutManager(createLayoutManager());

        //第一次加载
        setRefreshing();

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy>0&&mCurrentState == ACTION_IDLE&&
                        isLoadMoreEnabled && checkIfNeedLoadMore()&&hasMore()) {
                    mCurrentState = ACTION_LOAD_MORE;
                    mSwipeAdapter.onLoadMoreStateChange(true);
                    mSwipeRefreshLayout.setEnabled(false);
                    loadMore();
                }
            }
        });
    }

    private boolean checkIfNeedLoadMore() {
        int lastVisibleItemPosition = findLastVisibleItem(mLayoutManager);
        int totalCount = mLayoutManager.getItemCount();
        return totalCount - lastVisibleItemPosition < 2;
    }

    int findLastVisibleItem(RecyclerView.LayoutManager laoutManager){
        if(laoutManager instanceof LinearLayoutManager){
            return ((LinearLayoutManager)laoutManager).findLastVisibleItemPosition();
        }else if(laoutManager instanceof GridLayoutManager){
            return ((GridLayoutManager)laoutManager).findLastVisibleItemPosition();
        }else if(laoutManager instanceof StaggeredGridLayoutManager){
            int positions[]=null;
            ((StaggeredGridLayoutManager)laoutManager).
                    findFirstCompletelyVisibleItemPositions(positions);
            return positions[0];
        }
        throw new UnsupportedOperationException(
                "LayoutManager must in (" +
                        "LinearLayuotManager,GridLayoutManager," +
                        "StaggeredGridLayoutManager)");
    }


    public void setAdapter(BasePullAdapter<T> adapter) {
        this.mSwipeAdapter = adapter;
        mRecyclerView.setAdapter(new ScaleInAnimationAdapter(adapter));
    }


    public void setLayoutManager(RecyclerView.LayoutManager manager) {
        this.mLayoutManager = manager;
        mRecyclerView.setLayoutManager(manager);
        if(manager instanceof GridLayoutManager){
            GridLayoutManager gm=(GridLayoutManager)manager;
            gm.setSpanSizeLookup(new FooterSpanSizeLookup(mSwipeAdapter, mSwipeAdapter.getItemCount()));
        }
    }

    private class FooterSpanSizeLookup extends GridLayoutManager.SpanSizeLookup {
        private BasePullAdapter<?> adapter;
        private int spanCount;

        public FooterSpanSizeLookup(BasePullAdapter<?> adapter, int spanCount) {
            this.adapter = adapter;
            this.spanCount = spanCount;
        }
        @Override
        public int getSpanSize(int position) {
            if (adapter.isFooter(position)) {
                return spanCount;
            }
            return 1;
        }
    }

    public void enableLoadMore(boolean enable) {
        isLoadMoreEnabled = enable;
    }

    public void enablePullToRefresh(boolean enable) {
        isPullToRefreshEnabled = enable;
        mSwipeRefreshLayout.setEnabled(enable);
    }

    public boolean hasMore(){
        return true;
    }


    @Override
    final public void onRefresh() {
        if(mCurrentState==ACTION_IDLE){
            mCurrentState = ACTION_PULL_TO_REFRESH;
            enableLoadMore(true);
            refresh();
        }
    }



    final public void notifyMoreData(List<T>list){
        mDataList.addAll(list);
        mSwipeAdapter.notifyDataSetChanged();
        onRefreshCompleted();
    }

    final public void notifyRefreshData(List<T>list){
        mDataList.clear();
        mDataList.addAll(list);
        mSwipeAdapter.notifyDataSetChanged();
        onRefreshCompleted();
    }


    public void onRefreshCompleted() {
        switch (mCurrentState) {
            case ACTION_PULL_TO_REFRESH:
                mSwipeRefreshLayout.setRefreshing(false);
                break;
            case ACTION_LOAD_MORE:
                mSwipeAdapter.onLoadMoreStateChange(false);
                if (isPullToRefreshEnabled) {
                    mSwipeRefreshLayout.setEnabled(true);
                }
                break;
        }
        mCurrentState = ACTION_IDLE;
    }


    public void setRefreshing() {
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);
                onRefresh();
            }
        });
    }

}