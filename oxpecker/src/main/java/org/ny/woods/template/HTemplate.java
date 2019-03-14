package org.ny.woods.template;

import android.content.Context;
import android.os.Build;
import android.os.Environment;

import com.github.jknack.handlebars.Handlebars;

import org.ny.woods.exception.HException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class HTemplate {
    /**
     * 不可被更改的关键字
     */
    private final ArrayList<String> finalKeys = new ArrayList<>();
    private final HashMap<String, Object> dataCaches = new HashMap<String, Object>() {
        @Override
        public Object get(Object key) {
            if(!containsKey(key)) {
                return new StringBuffer(Handlebars.DELIM_START).append("{").append(key.toString()).append("}").append(Handlebars.DELIM_END).toString();
            }
            return super.get(key);
        }
    };
    private Handlebars mHandlebars; {
        mHandlebars = new Handlebars();
//        mHandlebars.setCharset(Charset.forName("utf-8"));
    }

    public HTemplate() {
    }

    public HTemplate(HTemplate hTemplate) {
        this.dataCaches.putAll(hTemplate.dataCaches);
        this.finalKeys.addAll(hTemplate.finalKeys);
    }

    public int size(boolean hasFinal) {
        if(hasFinal) {
            return dataCaches.size();
        }
        return dataCaches.size() - finalKeys.size();
    }

    /**
     * 为对象取个别名
     * @param key 别名
     * @param value 实际对象
     * @return
     */
    public final HTemplate as(String key, Object value) {
        if(finalKeys.contains(key)) {
            throw new HException("The keyword \""+key+"\" can not be replaced!");
        }
        dataCaches.put(key, value);
        return this;
    }

    /**
     *
     * @param context
     */
    public final void asFinal(Context context) {
        this.asFinal("package", context.getPackageName());
        this.asFinal("resource", "android.resource://"+context.getPackageName());
        this.asFinal("raw", this.get("resource")+"/raw");
        this.asFinal("drawable", this.get("resource")+"/drawable");
        this.asFinal("color", this.get("resource")+"/color");
        this.asFinal("attr", this.get("resource")+"/attr");
        this.asFinal("style", this.get("resource")+"/style");
        this.asFinal("mipmap", this.get("resource")+"/mipmap");
        this.asFinal("assets", "file:///android_asset");
        this.asFinal("sdcard", "file://"+Environment.getExternalStorageDirectory().getPath());
        this.asFinal("file", "file://"+context.getFilesDir());
        this.asFinal("cache", "file://"+context.getCacheDir());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            this.asFinal("data", "file://"+context.getDataDir().getAbsoluteFile());
        }
        else {
            this.asFinal("data", "file://"+"/data/data/"+context.getPackageName());
        }
    }

    /**
     * 为对象取个别名,添加后不可更改
     * @param key 别名
     * @param value 实际对象
     * @return
     */
    public final HTemplate asFinal(String key, Object value) {
        if(!finalKeys.contains(key)) {
            finalKeys.add(key);
        }
        dataCaches.put(key, value);
        return this;
    }

    /**
     * 复制别名
     * @param map
     */
    public void asAll(HashMap<String, Object> map) {
        dataCaches.putAll(map);
    }

    /**
     * 复制
     * @param hTemplate
     */
    public void asAll(HTemplate hTemplate) {
        dataCaches.putAll(hTemplate.dataCaches);
    }

    /**
     * 删除别名
     * @param key
     * @return
     */
    public HTemplate remove(String key) {
        if(finalKeys.contains(key)) {
            throw new HException("The keyword \""+key+"\" cannot be deleted!");
        }
        dataCaches.remove(key);
        return this;
    }

    public <T> T get(String key) {
        return (T) dataCaches.get(key);
    }

    public HTemplate clear() {
        dataCaches.clear();
        return this;
    }

    public String apply(String template) throws IOException {
        return mHandlebars.compileInline(template).apply(dataCaches);
    }

    public String apply(InputStream i) throws IOException {
        return apply(loadTestFromInputStream(i));
    }

    public static String loadTestFromInputStream(InputStream inputStream) {
        StringBuffer buffer = new StringBuffer();
        byte[] b = new byte[1024];
        int len;
        try {
            while ((len = inputStream.read(b)) != -1) {
                buffer.append(new String(b, 0, len));
            }
            inputStream.close();
        } catch (IOException e) {

        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return buffer.toString();
    }

    public static boolean isTemplate(String string) {
        return string.contains(Handlebars.DELIM_START)&&string.contains(Handlebars.DELIM_END);
    }
}
