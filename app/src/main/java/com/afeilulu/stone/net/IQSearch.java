package com.afeilulu.stone.net;

import android.view.ViewGroup;

import com.afeilulu.stone.model.SearchPage;
import com.afeilulu.stone.util.IPEntry;

import java.lang.ref.WeakReference;

public interface IQSearch {
    public void list(final Result cr, IPEntry ip, SearchPage req);

    public void detail(final Result cr, IPEntry ip, SearchPage req);

    public interface Result {
        public void list(SearchPage res);

        public void detail(SearchPage res, WeakReference<ViewGroup> wr);
    }
}
