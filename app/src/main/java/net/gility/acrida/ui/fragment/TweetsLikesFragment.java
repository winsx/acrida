package net.gility.acrida.ui.fragment;

import java.io.InputStream;
import java.io.Serializable;
import java.util.List;

import net.gility.acrida.android.ApplicationLoader;
import net.gility.acrida.R;
import net.gility.acrida.ui.adapter.TweetLikeAdapter;
import net.gility.acrida.network.OSChinaApi;
import net.gility.acrida.model.Constants;
import net.gility.acrida.content.Entity;
import net.gility.acrida.content.Notice;
import net.gility.acrida.content.Tweet;
import net.gility.acrida.content.TweetLike;
import net.gility.acrida.content.TweetLikeList;
import net.gility.acrida.content.TweetsList;
import net.gility.acrida.utils.NoticeUtils;
import net.gility.acrida.ui.widget.StateView;
import net.gility.acrida.utils.UIHelper;
import net.gility.acrida.utils.XmlUtils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

/**
 * 赞过我动弹的列表
 * 
 * @date 2014年10月10日
 */
public class TweetsLikesFragment extends BaseListFragment<TweetLike> {

    protected static final String TAG = TweetsLikesFragment.class
	    .getSimpleName();
    private static final String CACHE_KEY_PREFIX = "mytweets_like_list_";

    private boolean mIsWatingLogin;

    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	if (mCatalog > 0) {
	    IntentFilter filter = new IntentFilter(
		    Constants.INTENT_ACTION_USER_CHANGE);
	    filter.addAction(Constants.INTENT_ACTION_LOGOUT);
	    getActivity().registerReceiver(mReceiver, filter);
	}
    }

    @Override
    public void onDestroy() {
	if (mCatalog > 0) {
	    getActivity().unregisterReceiver(mReceiver);
	}
	super.onDestroy();
    }

    @Override
    protected TweetLikeAdapter getListAdapter() {
	return new TweetLikeAdapter();
    }

    @Override
    protected String getCacheKeyPrefix() {
	return CACHE_KEY_PREFIX + mCatalog;
    }

    @Override
    protected TweetLikeList parseList(InputStream is) throws Exception {
	TweetLikeList list = XmlUtils.toBean(TweetLikeList.class, is);
	return list;
    }

    @Override
    protected TweetLikeList readList(Serializable seri) {
	return ((TweetLikeList) seri);
    }

    @Override
    protected void sendRequestData() {
	OSChinaApi.getTweetLikeList(mHandler);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
	    long id) {
	Tweet tweet = mAdapter.getItem(position).getTweet();
	if (tweet != null) {
	    UIHelper.showTweetDetail(view.getContext(), null, tweet.getId());
	}
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {

	@Override
	public void onReceive(Context context, Intent intent) {
	    setupContent();
	}
    };

    private void setupContent() {
	if (ApplicationLoader.getInstance().isLogin()) {
	    mIsWatingLogin = false;
	    mErrorLayout.setStateType(StateView.TYPE_NETWORK_LOADING);
	    requestData(true);
	} else {
	    mCatalog = TweetsList.CATALOG_ME;
	    mIsWatingLogin = true;
	    mErrorLayout.setStateType(StateView.TYPE_NETWORK_ERROR);
	    mErrorLayout.setErrorMessage(getString(R.string.unlogin_tip));
	}
    }

    @Override
    protected void requestData(boolean refresh) {
	if (ApplicationLoader.getInstance().isLogin()) {
	    mCatalog = ApplicationLoader.getInstance().getLoginUid();
	    mIsWatingLogin = false;
	    super.requestData(refresh);
	} else {
	    mIsWatingLogin = true;
	    mErrorLayout.setStateType(StateView.TYPE_NETWORK_ERROR);
	    mErrorLayout.setErrorMessage(getString(R.string.unlogin_tip));
	}
    }

    @Override
    public void initView(View view) {
	super.initView(view);
	mErrorLayout.setOnLayoutClickListener(new View.OnClickListener() {

	    @Override
	    public void onClick(View v) {
		if (ApplicationLoader.getInstance().isLogin()) {
		    mErrorLayout.setStateType(StateView.TYPE_NETWORK_LOADING);
		    requestData(true);
		} else {
		    UIHelper.showLoginActivity(getActivity());
		}
	    }
	});
    }

    @Override
    protected long getAutoRefreshTime() {
	if (mCatalog == TweetsList.CATALOG_LATEST) {
	    return 3 * 60;
	}
	return super.getAutoRefreshTime();
    }

    @Override
    protected void onRefreshNetworkSuccess() {
	// TODO Auto-generated method stub
	super.onRefreshNetworkSuccess();
	if (ApplicationLoader.getInstance().isLogin()
		&& mCatalog == ApplicationLoader.getInstance().getLoginUid()
		&& 4 == NoticeViewPagerFragment.sCurrentPage) {
	    NoticeUtils.clearNotice(Notice.TYPE_NEWLIKE);
	    UIHelper.sendBroadcastForNotice(getActivity());
	}
    }
    
    protected boolean compareTo(List<? extends Entity> data, Entity enity) {
        int s = data.size();
        
        if (enity != null && enity instanceof TweetLike) {
            TweetLike tweetLike = (TweetLike) enity;
            for (int i = 0; i < s; i++) {
                if (tweetLike.getUser().getId() == ((TweetLike)data.get(i)).getId()) {
                    return true;
                }
            }
        }
        return false;
    }
}