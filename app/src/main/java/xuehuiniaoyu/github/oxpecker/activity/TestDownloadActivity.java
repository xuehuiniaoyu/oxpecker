package xuehuiniaoyu.github.oxpecker.activity;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ListView;

import org.hjson.JsonArray;
import org.hjson.JsonValue;
import org.ny.woods.app.HActivity;
import org.ny.woods.layout.widget.i.ViewPart;
import org.ny.woods.parser.Oxpecker;
import org.ny.woods.utils.IDUtil;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import xuehuiniaoyu.github.oxpecker.utils.DownloadUtil;

public class TestDownloadActivity extends HActivity {

    Handler mHandler;
    private boolean leave;

    public static class DownloadInfo {
        public String src;
        public double progress;
        public double max;
        public File localFile;

        public DownloadInfo(String src) {
            this.src = src;
        }

        public String getSrc() {
            return src;
        }

        public double getProgress() {
            return progress;
        }

        public double getMax() {
            return max;
        }

        @Override
        public String toString() {
            return "{"+progress+"-"+max+"}";
        }
    }
    private HashMap<Integer, DownloadInfo> downloadCache = new HashMap<Integer, DownloadInfo>() {
        public DownloadInfo get(Integer key) {
            return super.get(key);
        }

        @Override
        public DownloadInfo put(Integer key, DownloadInfo value) {
            new Thread() {
                @Override
                public void run() {
                    DownloadUtil.download(TestDownloadActivity.this, key, value);
                }
            }.start();
            return super.put(key, value);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String dataTemplate = initDatas();

        // 将对象转换成下载对象并下载
        JsonArray array = JsonValue.readHjson(dataTemplate).asArray();
        for(int i = 0; i < array.size(); i++) {
            downloadCache.put(i, new DownloadInfo(array.get(i).asObject().getString("src", null)));
        }

        // 添加传递
        getHTemplate().as("adapter-data", dataTemplate); // 用于填充ListView
        getHTemplate().as("downloadQueue", downloadCache); // 用于获取下载进度

        //setContentViewFromAssets("download/test_download.hj");
        try {
            getOxpecker().inflaterAsync(new Oxpecker.AsyncTytpe(getAssets().open("download/test_download.hj"), Oxpecker.AsyncTytpe.STREAM), new Oxpecker.AsyncTytpe.Callback() {
                @Override
                public void onFailed(Exception e, Oxpecker oxpecker) {

                }

                @Override
                public void onSuccess(ViewPart<? extends View> hView, Oxpecker oxpecker) {
                    setContentView(hView.getView());
                    ListView listView = findViewById(IDUtil.id("#listView"));

                    // 1秒刷新一次
                    mHandler = new Handler();
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if(mHandler != null) {
                                mHandler.removeCallbacks(this);
                                if (!leave) {
                                    BaseAdapter adapter = (BaseAdapter) listView.getAdapter();
                                    if (adapter != null) {
                                        adapter.notifyDataSetChanged();
                                    }
                                    mHandler.postDelayed(this, 1000);
                                }
                            }
                        }
                    });
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String initDatas() {
        StringBuffer buffer = new StringBuffer("[");
        buffer.append("{src:\"http://www.pptbz.com/pptpic/UploadFiles_6909/201203/2012031220134655.jpg\"").append("},");
        buffer.append("{src:\"http://pic17.nipic.com/20111021/8633866_210108284151_2.jpg\"").append("},");
        buffer.append("{src:\"http://pic22.nipic.com/20120714/9622064_105642209176_2.jpg\"").append("},");
        buffer.append("{src:\"http://pic1.nipic.com/2008-11-20/20081120145730349_2.jpg\"").append("},");
        buffer.append("{src:\"http://pic1.nipic.com/2008-11-20/20081120145730349_2.jpg\"").append("},");
        buffer.append("{src:\"http://pic1.nipic.com/2008-11-20/20081120145730349_2.jpg\"").append("},");
        buffer.append("{src:\"http://pic1.nipic.com/2008-11-20/20081120145730349_2.jpg\"").append("},");
        buffer.append("{src:\"http://pic1.nipic.com/2008-11-20/20081120145730349_2.jpg\"").append("},");
        buffer.append("{src:\"http://pic1.nipic.com/2008-11-20/20081120145730349_2.jpg\"").append("},");
        buffer.append("{src:\"http://pic1.nipic.com/2008-11-20/20081120145730349_2.jpg\"").append("},");
        buffer.append("{src:\"http://pic1.nipic.com/2008-11-20/20081120145730349_2.jpg\"").append("},");
        buffer.append("{src:\"http://pic1.nipic.com/2008-11-20/20081120145730349_2.jpg\"").append("},");
        buffer.append("{src:\"http://pic1.nipic.com/2008-11-20/20081120145730349_2.jpg\"").append("},");
        buffer.append("{src:\"http://pic21.nipic.com/20120519/5454342_154115399000_2.jpg\"").append("}]");
        return buffer.toString();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        leave = true;
        mHandler = null;
    }
}
