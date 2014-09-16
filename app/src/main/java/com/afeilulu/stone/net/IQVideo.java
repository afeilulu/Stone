package com.afeilulu.stone.net;

import com.afeilulu.stone.model.Program;
import com.afeilulu.stone.model.SearchPage;
import com.afeilulu.stone.util.IPEntry;
import com.afeilulu.stone.util.SourceHolder;

import java.util.List;

public interface IQVideo {
    public void list(final Result cr, String url);

    public void detail(final Result cr, String url, String uid);

    public void source(final Result cr, IPEntry ip, long vid);

    public void recommend(final Result res, IPEntry ip, int type, String name);

    public interface Result {
        public void list(List<Program> list);

        public void detail(Program video);

        public void source(SourceHolder sh);

        public void recommend(SearchPage sp);

    }
}
