package com.afeilulu.stone.net;

import java.util.List;

import com.afeilulu.stone.model.Channel;

public interface IQChannel {
    public void list(final Result cr, String url);

    public void detail(final Result cr, String url);

    public interface Result {
        public void list(List<Channel> list);

        public void detail(Channel cha);
    }
}
