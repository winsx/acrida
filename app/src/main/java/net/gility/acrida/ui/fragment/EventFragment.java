package net.gility.acrida.ui.fragment;

import java.io.InputStream;
import java.io.Serializable;

import net.gility.acrida.android.ApplicationLoader;
import net.gility.acrida.R;
import net.gility.acrida.ui.adapter.EventAdapter;
import net.gility.acrida.network.OSChinaApi;
import net.gility.acrida.model.Constants;
import net.gility.acrida.content.Event;
import net.gility.acrida.content.EventList;
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
 * 活动列表fragment
 * 
 * @author FireAnt（http://my.oschina.net/LittleDY）
 * @version 创建时间：2014年12月8日 下午5:17:32
 * 
 */
public class EventFragment extends BaseListFragment<Event> {

    public static final String BUNDLE_KEY_EVENT_TYPE = "eventlist_type";

    protected static final String TAG = EventFragment.class.getSimpleName();
    private static final String CACHE_KEY_PREFIX = "eventlist_";

    private int event_type;

    @Override
    protected EventAdapter getListAdapter() {
        EventAdapter adapter = new EventAdapter();
        adapter.setEventType(event_type);
        return adapter;
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            requestData(true);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            event_type = args.getInt(BUNDLE_KEY_EVENT_TYPE);
        }

        if (event_type == EventList.EVENT_LIST_TYPE_MY_EVENT) {
            IntentFilter filter = new IntentFilter(
                    Constants.INTENT_ACTION_USER_CHANGE);
            filter.addAction(Constants.INTENT_ACTION_LOGOUT);
            getActivity().registerReceiver(mReceiver, filter);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (event_type == EventList.EVENT_LIST_TYPE_MY_EVENT) {
            getActivity().unregisterReceiver(mReceiver);
        }
    }

    @Override
    public void initView(View view) {
        super.initView(view);
        mErrorLayout.setOnLayoutClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                clickErrorLayout();
            }
        });
    }

    private void clickErrorLayout() {
        if (event_type == EventList.EVENT_LIST_TYPE_NEW_EVENT) {
            mErrorLayout.setStateType(StateView.TYPE_NETWORK_LOADING);
            requestData(true);
        } else {
            if (ApplicationLoader.getInstance().isLogin()) {
                mErrorLayout.setStateType(StateView.TYPE_NETWORK_LOADING);
                requestData(true);
            } else {
                UIHelper.showLoginActivity(getActivity());
            }
        }
    }

    @Override
    protected void requestData(boolean refresh) {

        if (event_type == EventList.EVENT_LIST_TYPE_NEW_EVENT) {
            mCatalog = -1;
            super.requestData(refresh);
            return;
        }
        if (ApplicationLoader.getInstance().isLogin()) {
            mCatalog = ApplicationLoader.getInstance().getLoginUid();
            super.requestData(refresh);
        } else {
            mErrorLayout.setStateType(StateView.TYPE_NETWORK_ERROR);
            mErrorLayout.setErrorMessage(getString(R.string.unlogin_tip));
        }
    }

    @Override
    protected String getCacheKeyPrefix() {
        return CACHE_KEY_PREFIX + mCatalog;
    }

    @Override
    protected EventList parseList(InputStream is) throws Exception {
        EventList list = XmlUtils.toBean(EventList.class, is);
        return list;
    }

    @Override
    protected EventList readList(Serializable seri) {
        return ((EventList) seri);
    }

    @Override
    protected void sendRequestData() {
        OSChinaApi.getEventList(mCurrentPage, mCatalog, mHandler);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
            long id) {
        Event event = mAdapter.getItem(position);
        if (event != null)
            UIHelper.showEventDetail(view.getContext(), event.getId());
    }

}
