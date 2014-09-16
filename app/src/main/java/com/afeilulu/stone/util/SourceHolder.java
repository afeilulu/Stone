package com.afeilulu.stone.util;

import android.util.Log;

import com.afeilulu.stone.model.Episode;
import com.afeilulu.stone.model.Program;
import com.afeilulu.stone.model.Source;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Singleton class for holding source and episodes of a program Created by chen
 * on 1/15/14.
 */
public class SourceHolder {
    private static final String TAG = "SourceHolder";
    private static final SourceHolder holder = new SourceHolder();
    private ArrayList<Source> sourcesList = new ArrayList<Source>();
    private Program program;
    private int type;
    private int resultCode;
    private String resultMsg;

    // get directly from list view, no need to download program xml from epg
    // be set on every time the program clicked in the list
    private String programName;
    private String programId;

    public String getProgramId() {
        return programId;
    }

    public void setProgramId(String programId) {
        this.programId = programId;
    }

    public String getProgramName() {
        return programName;
    }

    public void setProgramName(String programName) {
        this.programName = programName;
    }

    private SourceHolder() {
    }

    public static SourceHolder getInstance() {
        return holder;
    }

    public void setType(int type) {
        this.type = type;
    }

    public ArrayList<Source> getSourcesList() {
        return sourcesList;
    }

    public void addSources(Source source) {
        Source old = getSource(source.getAlias());
        if (old == null) {
            sourcesList.add(source);
        } else {
            Log.i(TAG, "type=" + type);
            if (type == 3) {
                old.getEpisodes().addAll(source.getEpisodes());
            } else {
                if (old.getEpisodes().size() < source.getEpisodes().size()) {
                    sourcesList.remove(old);
                    sourcesList.add(source);
                }
            }
        }
    }

    public void clearSources() {
        if (sourcesList != null)
            sourcesList.clear();
        resultCode = 0;
        resultMsg = null;
    }

    public void clear() {
        if (sourcesList != null)
            sourcesList.clear();
        program = null;
        resultCode = 0;
        resultMsg = null;

        programId = null;
        programName = null;

    }

    public Source maxSource() {
        if (sourcesList != null && sourcesList.size() > 0) {
            int max = 0;
            for (int i = 0; i < sourcesList.size(); i++) {
                if (sourcesList.get(i).getEpisodes().size() > sourcesList.get(max).getEpisodes().size()) {
                    max = i;
                }
            }
            return sourcesList.get(max);
        }
        return null;
    }

    public Program getProgram() {
        return program;
    }

    public void setProgram(Program program) {
        this.program = program;
    }

    private Source getSource(String alias) {
        for (Source item : sourcesList) {
            if (alias.equals(item.getAlias())) {
                return item;
            }
        }
        return null;
    }

    public void sortEpisode() {
        Source s = maxSource();
        if (s == null) {
            return;
        }
        if (type != 3) {
            return;
        }
        ArrayList<Episode> list = s.getEpisodes();
        if (list != null && list.size() > 1) {
            Collections.sort(list, new Comparator<Episode>() {
                @Override
                public int compare(Episode o1, Episode o2) {
                    Episode ct1 = (Episode) o1;
                    Episode ct2 = (Episode) o2;
                    if (ct1 == null || ct2 == null || ct1.getName() == null
                            || ct2.getName() == null) {
                        return 0;
                    } else {
                        if (program != null && program.getId().contains("121585"))
                            return (ct2.getName() + ct2.getvTitle()).compareTo(ct1.getName() + ct1.getvTitle());
                        else
                            return ct2.getName().compareTo(ct1.getName());
                    }
                }
            });
        }
    }

    public String getResultMsg() {
        return resultMsg;
    }

    public void setResultMsg(String resultMsg) {
        this.resultMsg = resultMsg;
    }

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }
}
