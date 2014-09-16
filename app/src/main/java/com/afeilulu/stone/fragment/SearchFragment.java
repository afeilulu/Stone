package com.afeilulu.stone.fragment;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.afeilulu.stone.HomeActivity;
import com.afeilulu.stone.R;
import com.afeilulu.stone.SourceEpisodeActivity;
import com.afeilulu.stone.model.Program;
import com.afeilulu.stone.model.SearchPage;
import com.afeilulu.stone.net.IQSearch;
import com.afeilulu.stone.net.QueryFacade;
import com.afeilulu.stone.util.LogUtil;
import com.afeilulu.stone.util.SourceHolder;
import com.afeilulu.stone.util.VideoAdapter;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by chen on 7/17/14.
 */
public class SearchFragment extends ListFragment {

    private static final String TAG = LogUtil.makeLogTag(SearchFragment.class);
    private static final String ARG_SEARCH_STRING = "search_string";
    private VideoAdapter mAdapter;
    private List<Program> mList;

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static SearchFragment newInstance(String searchString) {
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
        args.putString(ARG_SEARCH_STRING, searchString);
        fragment.setArguments(args);
        return fragment;
    }

    public SearchFragment() {
    }

    private IQSearch.Result searchRes = new IQSearch.Result() {
        @Override
        public void list(final SearchPage sp) {
            if (sp == null)
                return;

            getActivity().runOnUiThread(new Runnable() {
                public void run() {

                    // Teardown from previous arguments
                    setListAdapter(null);

                    if (sp.getList() != null) {
                        mList.addAll(sp.getList());
                    }
                    setListAdapter(mAdapter);
                }
            });
        }

        @Override
        public void detail(final SearchPage sp, WeakReference<ViewGroup> weakReference) {
        }

    };
    private QueryFacade qf = QueryFacade.getInstance();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_video_list, container, false);
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((HomeActivity) activity).onSearchAttached(
                getArguments().getString(ARG_SEARCH_STRING));
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        view.setBackgroundColor(Color.WHITE);
        final ListView listView = getListView();
        listView.setSelector(android.R.color.transparent);
        listView.setCacheColorHint(Color.WHITE);


        mList = new ArrayList<Program>();
        mAdapter = new VideoAdapter(getActivity(),mList);

        // first load
        qf.search(searchRes,30,0,"");
    }

    public boolean canCollectionViewScrollUp() {
        return ViewCompat.canScrollVertically(getListView(), -1);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        String vid = mList.get(position).getId();

        // show source and episode dialog fragment

        /*// DialogFragment.show() will take care of adding the fragment
        // in a transaction.  We also want to remove any currently showing
        // dialog, so make our own transaction and take care of that here.
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        // Create and show the dialog.
        DialogFragment newFragment = SourceEpisodeFragment.newInstance(mList.get(position).getName(),vid);
        newFragment.show(ft, "dialog");*/


        SourceHolder.getInstance().setProgramId(vid);
        SourceHolder.getInstance().setProgramName(mList.get(position).getName());

        Intent intent = new Intent(getActivity(),SourceEpisodeActivity.class);
        getActivity().startActivity(intent);
    }
}
