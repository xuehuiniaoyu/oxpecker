package xuehuiniaoyu.github.oxpecker.utils;

import android.content.Context;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import xuehuiniaoyu.github.oxpecker.activity.TestDownloadActivity;


public class DownloadUtil {
    public static void download(Context context, int i, TestDownloadActivity.DownloadInfo downloadInfo) {
        InputStream ins;
        OutputStream ous;
        try {
            URL url = new URL(downloadInfo.src);
            HttpURLConnection  connection = (HttpURLConnection) url.openConnection();
            ins = connection.getInputStream();
            byte[] bits = new byte[1024];
            int len;
            File localFile = new File(context.getDataDir(), "download/"+ new File(downloadInfo.src).getName());
            if(!localFile.exists()) {
                localFile.getParentFile().mkdirs();
                localFile.createNewFile();
            }
            downloadInfo.localFile = localFile;
            downloadInfo.max = connection.getContentLength();
            downloadInfo.progress = localFile.length();
            ous = new FileOutputStream(localFile);
            while((len = ins.read(bits)) != -1) {
                ous.write(
                        bits, 0, len
                );
                downloadInfo.progress = localFile.length();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
