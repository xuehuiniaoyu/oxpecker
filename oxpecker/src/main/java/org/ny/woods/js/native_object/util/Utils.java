package org.ny.woods.js.native_object.util;

import android.content.Context;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.ny.woods.js.native_object.NativeObjInfo;
import org.ny.woods.utils.IDUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;

public class Utils extends NativeObjInfo {

    private static MessageDigest md5;

    static {
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public Utils(Context context) {
        super(context);
    }

    /**
     *
     * 获取id
     * @param idString 比如：#hello
     * @return
     */
    public Object ID(String idString) {
        return IDUtil.id(idString);
    }

    /**
     * 获取字符串的md5
     * @param string
     * @return
     */
    public String md5(String string) {
        try {
            byte[] bs = md5.digest(string.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder(40);
            for (byte x : bs) {
                if ((x & 0xff) >> 4 == 0) {
                    sb.append("0").append(Integer.toHexString(x & 0xff));
                } else {
                    sb.append(Integer.toHexString(x & 0xff));
                }
            }
            return sb.toString();
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取文件的md5
     * @param file
     * @return
     */
    public String md5(File file) {
        try {
            return DigestUtils.md5Hex(IOUtils.toByteArray(new FileInputStream(file)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
