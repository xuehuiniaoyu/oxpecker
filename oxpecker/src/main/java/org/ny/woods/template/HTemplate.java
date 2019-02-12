package org.ny.woods.template;

import com.github.jknack.handlebars.Handlebars;

import org.ny.woods.exception.HException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;

public class HTemplate {
    /**
     * 不可被更改的关键字
     */
    private final ArrayList<String> finalKeys = new ArrayList<>();
    private HashMap<String, Object> dataCaches = new HashMap<String, Object>() {
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
        mHandlebars.setCharset(Charset.forName("utf-8"));
    }

    public HTemplate() {
    }

    public HTemplate(HTemplate hTemplate) {
        this.dataCaches.putAll(hTemplate.dataCaches);
        this.finalKeys.addAll(hTemplate.finalKeys);
    }

    /**
     * 为对象取个别名
     * @param key 别名
     * @param value 实际对象
     * @return
     */
    public HTemplate as(String key, Object value) {
        if(finalKeys.contains(key)) {
            throw new HException("The keyword \""+key+"\" can not be replaced!");
        }
        dataCaches.put(key, value);
        return this;
    }

    /**
     * 为对象取个别名,添加后不可更改
     * @param key 别名
     * @param value 实际对象
     * @return
     */
    public HTemplate asFinal(String key, Object value) {
        as(key, value);
        if(!finalKeys.contains(key)) {
            finalKeys.add(key);
        }
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
