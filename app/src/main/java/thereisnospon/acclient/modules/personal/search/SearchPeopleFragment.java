package thereisnospon.acclient.modules.personal.search;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.orhanobut.logger.Logger;

import java.util.List;

import thereisnospon.acclient.AppApplication;
import thereisnospon.acclient.R;
import thereisnospon.acclient.base.adapter.BasePullAdapter;
import thereisnospon.acclient.base.fragment.BasePullFragment;
import thereisnospon.acclient.data.SearchPeopleItem;
import thereisnospon.acclient.ui.adapter.SearchPeopleAdapter;

/**
 * Created by yzr on 16/6/16.
 */
public class SearchPeopleFragment extends BasePullFragment<SearchPeopleItem>
        implements  SearchPeopleContact.View{

    private static final String TAG="SearchPeopleContact";
    private SearchPeopleContact.Presenter presenter;

    private String key;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("key", key);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if(savedInstanceState == null) {
            return;
        }
        key = savedInstanceState.getString("key");
    }

    public static SearchPeopleFragment newInstance(String key) {
        SearchPeopleFragment fragment = (SearchPeopleFragment) SearchPeopleFragment.instantiate(AppApplication.context, SearchPeopleFragment.class.getName());
        fragment.key = key;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_search_people,container,false);
        initRefreshViews(view,R.id.search_people_swipe,R.id.search_people_recycle);
        presenter=new SearchPeoplePresenter(this);
        Logger.d(key);
        return view;
    }

    @Override
    public BasePullAdapter<SearchPeopleItem> createItemAdapter(List<SearchPeopleItem> list) {
        return new SearchPeopleAdapter(list);
    }

    @Override
    public RecyclerView.LayoutManager createLayoutManager() {
        return new LinearLayoutManager(getActivity());
    }

    @Override
        public void loadMore() {
        Logger.d("load more");
        presenter.loadMorePeople(key);
    }

    @Override
    public void refresh() {
        Logger.d("refresh");
        presenter.searchPeople(key);
    }

    @Override
    public void refreshPeople(List<SearchPeopleItem> list) {
        Logger.d("refresh people");
        notifyRefreshData(list);
    }

    @Override
    public void loadMorePeople(List<SearchPeopleItem> list) {
        Logger.d("load more people");
        notifyMoreData(list);
    }

    @Override
    public void onFailure(String err) {
        Logger.d("on failure"+err);
        enableLoadMore(false);
    }
}
