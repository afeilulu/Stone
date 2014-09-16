package com.afeilulu.stone.fragment;

import android.app.DialogFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.afeilulu.stone.R;
import com.afeilulu.stone.model.Program;
import com.afeilulu.stone.model.SearchPage;
import com.afeilulu.stone.net.IQVideo;
import com.afeilulu.stone.net.QueryFacade;
import com.afeilulu.stone.util.SourceHolder;

import java.util.List;

/**
 * Created by chen on 8/5/14.
 */
public class SourceEpisodeFragment extends DialogFragment {

    private String mName;
    private String mId;
    private View mLoading;

    public static SourceEpisodeFragment newInstance(String name,String id) {
        SourceEpisodeFragment f = new SourceEpisodeFragment();

        Bundle args = new Bundle();
        args.putString("name", name);
        args.putString("id", id);
        f.setArguments(args);

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mName = getArguments().getString("name");
        mId = getArguments().getString("id");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        getDialog().setTitle(mName);

        View rootView = inflater.inflate(R.layout.fragment_source_episode, container, false);
        mLoading = rootView.findViewById(R.id.loading);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

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
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mLoading.setVisibility(View.GONE);
                        setTabContent();
                    }
                });
            }
        }

        @Override
        public void recommend(SearchPage sp) {

        }
    };

    private void setTabContent(){

    }
}
