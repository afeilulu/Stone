package com.afeilulu.stone.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.afeilulu.stone.PlayerActivity;
import com.afeilulu.stone.R;
import com.afeilulu.stone.VideoViewActivity;
import com.afeilulu.stone.model.Episode;
import com.afeilulu.stone.util.EpisodeAdapter;
import com.afeilulu.stone.util.SourceHolder;

import java.util.ArrayList;

/**
 * Created by chen on 8/5/14.
 */
public class EpisodeFragment extends ListFragment {

    ArrayList<Episode> mList;
    EpisodeAdapter mAdapter;

    int sourceIndex;

    public static EpisodeFragment newInstance(int index) {
        EpisodeFragment f = new EpisodeFragment();

        Bundle args = new Bundle();
        args.putInt("index", index);
        f.setArguments(args);

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_video_list, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final ListView listView = getListView();
//        listView.setSelector(android.R.color.transparent);
        listView.setCacheColorHint(Color.WHITE);

        sourceIndex = getArguments().getInt("index");
        mList = SourceHolder.getInstance().getSourcesList().get(sourceIndex).getEpisodes();
        mAdapter = new EpisodeAdapter(getActivity(), mList);
        setListAdapter(mAdapter);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        Intent intent = new Intent(getActivity(), VideoViewActivity.class);
        intent.putExtra("source_index", sourceIndex);
        intent.putExtra("episode_index", position);
        startActivity(intent);
    }
}
