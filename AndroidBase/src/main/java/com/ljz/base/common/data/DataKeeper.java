package com.ljz.base.common.data;

import android.content.Context;
import android.content.SharedPreferences;

import com.ljz.base.common.data.cipher.Cipher;
import com.ljz.base.common.logutils.LogUtils;
import com.ljz.base.common.utils.ByteUtil;
import com.ljz.base.common.utils.HexUtil;

/**
 * @author lujianzhao
 * @date 14-7-10
 */
public class DataKeeper {
    private SharedPreferences sp;
//    public static final  String KEY_PK_HOME = "msg_pk_home";
//    public static final  String KEY_PK_NEW  = "msg_pk_new";
//    private static final String TAG         = DataKeeper.class.getSimpleName();

    public DataKeeper(Context context, String fileName) {
        sp = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
    }

    /**
     * *************** get ******************
     */

    public String get(String key, String defValue) {
        return sp.getString(key, defValue);
    }

    public boolean get(String key, boolean defValue) {
        return sp.getBoolean(key, defValue);
    }

    public float get(String key, float defValue) {
        return sp.getFloat(key, defValue);
    }

    public int getInt(String key, int defValue) {
        return sp.getInt(key, defValue);
    }

    public long get(String key, long defValue) {
        return sp.getLong(key, defValue);
    }

    public Object get(String key) {
        return get(key, (Cipher) null);
    }

    public Object get(String key, Cipher cipher) {
        try {
            String hex = get(key, (String) null);
            if (hex == null)
                return null;
            byte[] bytes = HexUtil.decodeHex(hex.toCharArray());
            if (cipher != null)
                bytes = cipher.decrypt(bytes);
            Object obj = ByteUtil.byteToObject(bytes);
            LogUtils.i(key + " get: " + obj);
            return obj;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * *************** put ******************
     */
    public void put(String key, Object ser) {
        put(key, ser, null);
    }

    public void put(String key, Object ser, Cipher cipher) {
        try {
            LogUtils.i(key + " put: " + ser);
            if (ser == null) {
                sp.edit().remove(key).apply();
            } else {
                byte[] bytes = ByteUtil.objectToByte(ser);
                if (cipher != null)
                    bytes = cipher.encrypt(bytes);
                put(key, HexUtil.encodeHexStr(bytes));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void remove(String key) {
        sp.edit().remove(key).apply();
    }

    public void put(String key, String value) {
        sp.edit().putString(key, value).apply();
    }

    public void put(String key, boolean value) {
        sp.edit().putBoolean(key, value).apply();
    }

    public void put(String key, float value) {
        sp.edit().putFloat(key, value).apply();
    }

    public void put(String key, long value) {
        sp.edit().putLong(key, value).apply();
    }

    public void put(String key, int value) {
        sp.edit().putInt(key, value).apply();
    }

}
