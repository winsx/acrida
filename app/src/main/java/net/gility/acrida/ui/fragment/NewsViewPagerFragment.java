package net.gility.acrida.ui.fragment;

import net.gility.acrida.R;
import net.gility.acrida.ui.adapter.ViewPageFragmentAdapter;
import net.gility.acrida.content.BlogList;
import net.gility.acrida.content.NewsList;
import net.gility.acrida.ui.help.OnTabReselectListener;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

/**
 * 资讯viewpager页面
 * 
 * @author FireAnt（http://my.oschina.net/LittleDY）
 * @created 2014年9月25日 下午2:21:52
 * 
 */
public class NewsViewPagerFragment extends BaseViewPagerFragment implements
        OnTabReselectListener {

    @Override
    protected void onSetupTabAdapter(ViewPageFragmentAdapter adapter) {
        String[] title = getResources().getStringArray(
                R.array.news_viewpage_arrays);
        adapter.addTab(title[0], "news", RtfNewsFragment.class,
                getBundle(NewsList.CATALOG_ALL));
        adapter.addTab(title[1], "news_week", RtfNewsFragment.class,
                getBundle(NewsList.CATALOG_WEEK));
        adapter.addTab(title[2], "latest_blog", RtfBlogFragment.class,
                getBundle(BlogList.CATALOG_LATEST));
        adapter.addTab(title[3], "recommend_blog", RtfBlogFragment.class,
                getBundle(BlogList.CATALOG_RECOMMEND));
    }

    private Bundle getBundle(int newType) {
        Bundle bundle = new Bundle();
        bundle.putInt(BaseListFragment.BUNDLE_KEY_CATALOG, newType);
        return bundle;
    }

    @Override
    protected void setScreenPageLimit() {
        mViewPager.setOffscreenPageLimit(3);
    }

    /**
     * 基类会根据不同的catalog展示相应的数据
     * 
     * @param catalog
     *            要显示的数据类别
     * @return
     */
    private Bundle getBundle(String catalog) {
        Bundle bundle = new Bundle();
        bundle.putString(BlogFragment.BUNDLE_BLOG_TYPE, catalog);
        return bundle;
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void initView(View view) {

    }

    @Override
    public void initData() {

    }

    @Override
    public void onTabReselect() {
        try {
            int currentIndex = mViewPager.getCurrentItem();
            Fragment currentFragment = getChildFragmentManager().getFragments()
                    .get(currentIndex);
            if (currentFragment != null
                    && currentFragment instanceof OnTabReselectListener) {
                OnTabReselectListener listener = (OnTabReselectListener) currentFragment;
                listener.onTabReselect();
            }
        } catch (NullPointerException e) {
        }
    }
}
