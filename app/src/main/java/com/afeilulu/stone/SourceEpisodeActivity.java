package com.afeilulu.stone;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.View;

import com.afeilulu.stone.fragment.EpisodeFragment;
import com.afeilulu.stone.model.Program;
import com.afeilulu.stone.model.SearchPage;
import com.afeilulu.stone.net.IQVideo;
import com.afeilulu.stone.net.QueryFacade;
import com.afeilulu.stone.util.LogUtil;
import com.afeilulu.stone.util.SourceHolder;
import com.astuetz.PagerSlidingTabStrip;

import java.util.List;

/**
 * Created by chen on 8/5/14.
 */
public class SourceEpisodeActivity extends FragmentActivity {

    private final static String TAG = LogUtil.makeLogTag(SourceEpisodeActivity.class);

    private String mName;
    private String mId;

    private View mLoading;

    private PagerSlidingTabStrip tabs;
    private ViewPager pager;
    private MyPagerAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_source_episode);

        mLoading = findViewById(R.id.loading);

        mName = SourceHolder.getInstance().getProgramName();
        mId = SourceHolder.getInstance().getProgramId();

        setTitle(mName);

        tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        pager = (ViewPager) findViewById(R.id.pager);

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        long vid = 0;
        try {
            vid = Long.parseLong(mId);
        } catch (Exception e) {

        }

        mLoading.setVisibility(View.VISIBLE);

        // start to get source and episode and fill them into a singleton instance
        SourceHolder.getInstance().clear();
        QueryFacade qf = QueryFacade.getInstance();
        qf.source(result, 0, 20, vid);
    }

    private IQVideo.Result result = new IQVideo.Result() {

        @Override
        public void list(List<Program> list) {

        }

        @Override
        public void detail(Program video) {

        }

        @Override
        public void source(SourceHolder sh) {
            if (sh == null) {
                SourceHolder.getInstance().setResultCode(10);
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setTabContent();
                        mLoading.setVisibility(View.GONE);
                    }
                });
            }
        }

        @Override
        public void recommend(SearchPage sp) {

        }
    };

    private void setTabContent(){

        tabs.setIndicatorColor(getResources().getColor(R.color.accent));

        adapter = new MyPagerAdapter(getSupportFragmentManager());

        pager.setAdapter(adapter);

        final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources()
                .getDisplayMetrics());

        pager.setPageMargin(pageMargin);

        tabs.setViewPager(pager);

    }

    public class MyPagerAdapter extends FragmentPagerAdapter implements PagerSlidingTabStrip.IconTabProvider{

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return SourceHolder.getInstance().getSourcesList().get(position).getName();
        }

        @Override
        public int getCount() {
            return SourceHolder.getInstance().getSourcesList().size();
        }

            @Override
        public Fragment getItem(int position) {
            return EpisodeFragment.newInstance(position);
        }

        @Override
        public int getPageIconResId(int i) {
            return getSourceIconResId(i);
        }
    }

    public int getSourceIconResId(int index){
        String alias = SourceHolder.getInstance().getSourcesList().get(index).getAlias();
        if (alias.contains("youku"))
            return R.drawable.site_youku;
        else if (alias.contains("sohu"))
            return R.drawable.site_sohu;
        else if (alias.contains("qq"))
            return R.drawable.site_qq;
        else if (alias.contains("qiyi"))
            return R.drawable.site_qiyi;
        else if (alias.contains("funshion"))
            return R.drawable.site_funshion;
        else if (alias.contains("pptv"))
            return R.drawable.site_pptv;
        else if (alias.contains("tudou"))
            return R.drawable.site_tudou;

        return 0;
    }

}
