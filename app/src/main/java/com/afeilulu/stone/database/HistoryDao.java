package com.afeilulu.stone.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.afeilulu.stone.model.PlayHistory;

import java.util.ArrayList;
import java.util.List;

public class HistoryDao {
    private DataBaseHelper dataBaseHelper;

    public HistoryDao(Context context) {
        dataBaseHelper = new DataBaseHelper(context);
    }

    /**
     * 保存数据方法
     *
     * @param po
     */
    public synchronized void save(PlayHistory po) {
        ContentValues values = new ContentValues();
        SQLiteDatabase database = dataBaseHelper.getWritableDatabase();
        try {
            values.put("id", po.getId());
            values.put("play_time", po.getPlaytime());
            values.put("video", po.getVideo());
            values.put("post", po.getPost());
            values.put("name", po.getName());
            values.put("text", po.getText());
            values.put("part", po.getPart());
            values.put("score", po.getScore());
            values.put("play_hd", po.getPlayHD());
            values.put("quality", po.getQuality());
            // values.put("view_time", "time('now')");
            Cursor cursor = database.query("history", new String[]{"id",
                            "post", "name", "text", "play_time", "video", "part"},
                    "id=?", new String[]{po.getId()}, null, null, null
            );
            if (cursor.moveToNext()) {
                database.execSQL("update history set text=?,play_time=?,part=?,view_time= datetime('now')  where id=?",
                        new Object[]{po.getText(), po.getId(), po.getPlaytime(), po.getPart()});
                return;
            }
            database.insert("history", "id", values);
        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            try {
                if (null != database) {
                    database.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

    /**
     * 搜索 此方法没有用
     *
     * @param pid
     * @return
     */
    public synchronized PlayHistory find(String pid) {
        PlayHistory his = null;
        SQLiteDatabase database = dataBaseHelper.getReadableDatabase();
        try {
            Cursor cursor = database.query("history", new String[]{"id",
                            "post", "name", "text", "play_time", "video", "score", "part", "play_hd"},
                    "id=?", new String[]{pid}, null, null, null
            );
            if (cursor.moveToNext()) {
                his = new PlayHistory();
                his.setId(cursor.getString(0));
                his.setPost(cursor.getString(1));
                his.setName(cursor.getString(2));
                his.setText(cursor.getString(3));
                his.setPlaytime(cursor.getInt(4));
                his.setVideo(cursor.getString(5));
                his.setScore(cursor.getString(6));
                his.setPart(cursor.getInt(7));
                his.setPlayHD(cursor.getInt(8));
                // System.err.println("cursor.getString(7)====="+cursor.getLong(7));
                // SimpleDateFormat format = new
                // SimpleDateFormat("yyyy-MM-dd HH:mm");
                // try {
                // his.setDate(format.parse(cursor.getString(7)));
                // } catch (ParseException e) {
                // // TODO Auto-generated catch block
                // e.printStackTrace();
                // }

                return his;
            }
        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            try {
                if (null != database) {
                    database.close();

                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        return his;
    }

    /**
     * 删除
     *
     * @param ids
     */
    public synchronized void delete(String... ids) {
        if (ids.length > 0) {
            StringBuilder sb = new StringBuilder();

            for (int i = 0; i < ids.length; i++) {
                sb.append('?').append(',');
            }
            // 在基础上减去被删除的数据
            sb.deleteCharAt(sb.length() - 1);
            SQLiteDatabase database = dataBaseHelper.getWritableDatabase();
            try {
                database.delete("history", "id in(" + sb + ")", ids);
            } catch (Exception e) {
                e.printStackTrace();

            } finally {
                try {
                    if (null != database) {
                        database.close();

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

        }
    }

    /**
     * 删除所有
     */
    public synchronized void deleteAll() {
        SQLiteDatabase database = dataBaseHelper.getWritableDatabase();
        try {
            database.delete("history", null, null);
        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            try {
                if (null != database) {
                    database.close();

                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

    /**
     * 对数据分页
     *
     * @return
     */
    public synchronized List<PlayHistory> getData() {
        List<PlayHistory> list = new ArrayList<PlayHistory>();
        SQLiteDatabase database = dataBaseHelper.getReadableDatabase();
        try {
            /*
             * 用游标对象接数据 "select * from person  limit ?,?" person不能加上where 关键字
			 */
            Cursor cursor = database.query("history", new String[]{"id",
                            "post", "name", "text", "play_time", "video", "part", "score", "quality"},
                    null, null, null, null, " view_time desc "
            );
            while (cursor.moveToNext()) {
                PlayHistory his = new PlayHistory();
                his.setId(cursor.getString(0));
                his.setPost(cursor.getString(1));
                his.setName(cursor.getString(2));
                his.setText(cursor.getString(3));
                his.setPlaytime(cursor.getInt(4));
                his.setVideo(cursor.getString(5));
                his.setPart(cursor.getInt(6));
                his.setScore(cursor.getString(7));
                his.setQuality(cursor.getString(8));

                list.add(his);
            }
        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            try {
                if (null != database) {
                    database.close();

                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        return list;
    }

    /**
     * 对android数据库SQLite进行总记录查询
     *
     * @return
     */
    public synchronized long getCount() {
        SQLiteDatabase database = dataBaseHelper.getReadableDatabase();
        try {
            Cursor cursor = database.query(" history ",
                    new String[]{"count(*)"}, null, null, null, null, null);
            if (cursor.moveToNext()) {
                return cursor.getLong(0);
            }
        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            try {
                if (null != database) {
                    database.close();

                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        return 0;
    }

}
