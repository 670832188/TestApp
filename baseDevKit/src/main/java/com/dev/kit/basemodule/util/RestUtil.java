package com.dev.kit.basemodule.util;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Copyright: Copyright (c) 2015年 Beijing Yunshan Information Technology Co., Ltd. All rights reserved. <br>
 * Version:V1.4.2 <br>
 * Author: 周取辉 <br>
 * Date:   16/02/16 11:29  <br>
 * Desc:   网络访问的工具类 <br>
 * <p>
 * Edit History：
 */
public class RestUtil {
    /**
     * 对服务器返回结果进行加盐校验
     */
    public static <T> boolean verifyResultFromServer(T date) {
        Gson gson = new Gson();
        String jsonStr = gson.toJson(date);
        try {
            JSONObject jsonObject = new JSONObject(jsonStr);
            String sign = jsonObject.getString("sign");
            jsonObject.remove("sign");
            StringBuilder sbKVS = new StringBuilder();
            Map<String, List> kvs = new HashMap<>();
            List<String> keys = new LinkedList<>();
            fetchLeafKV(jsonObject, kvs);
            keys.addAll(kvs.keySet());
            Collections.sort(keys);
            Iterator<String> keyIter = keys.iterator();
            while (keyIter.hasNext()) {
                String key = keyIter.next();
                sbKVS.append(key).append(KV_SEPARATOR).append(mergeValues(kvs.get(key))).append(KVP_SEPARATOR);
            }
            sbKVS.deleteCharAt(sbKVS.length() - 1);
            String strKvs = sbKVS.toString();
            return RsaUtils.verify(strKvs, getPubKey(), sign, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static synchronized String sign(String paramJson) throws JSONException {
        JSONObject jsonObject = new JSONObject(paramJson);
        StringBuilder sbKVS = new StringBuilder();

        Map<String, List> kvs = new HashMap<>();
        List<String> keys = new LinkedList<>();
        fetchLeafKV(jsonObject, kvs);

        keys.addAll(kvs.keySet());
        Collections.sort(keys);
        Iterator<String> keyIter = keys.iterator();
        while (keyIter.hasNext()) {
            String key = keyIter.next();
            sbKVS.append(key).append(KV_SEPARATOR).append(mergeValues(kvs.get(key))).append(KVP_SEPARATOR);
        }
        sbKVS.deleteCharAt(sbKVS.length() - 1);
        String strKvs = sbKVS.toString();
        try {
            String sign = RsaUtils.sign(strKvs, getPriKey(), "UTF-8");
            jsonObject.put("sign", sign);
            String param = jsonObject.toString();
            LogUtil.e("param: " + param);
            return param;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String mergeValues(List vls) {
        StringBuilder sbVls = new StringBuilder();
        Collections.sort(vls);
        Iterator iterator = vls.iterator();
        while (iterator.hasNext()) {
            Object obj = iterator.next();
            sbVls.append(obj).append(V_SEPARATOR);
        }
        sbVls.deleteCharAt(sbVls.length() - 1);
        return sbVls.toString();
    }

    private static final String KVP_SEPARATOR = "&";
    private static final String KV_SEPARATOR = "=";
    private static final String V_SEPARATOR = "-";

    private static void fetchLeafKV(String key, JSONArray jsonArray, Map<String, List> kvs) throws JSONException {
        for (int i = 0; i < jsonArray.length(); i++) {
            Object valObj = jsonArray.get(i);
            if (valObj instanceof JSONObject) {
                fetchLeafKV((JSONObject) valObj, kvs);
            } else if (valObj instanceof JSONArray) {
                JSONArray array = (JSONArray) valObj;
                fetchLeafKV(key, array, kvs);
            } else {
                List vLst = null;
                String lKey = key;
                if (kvs.containsKey(lKey)) {
                    vLst = kvs.get(lKey);
                } else {
                    vLst = new LinkedList();
                    kvs.put(lKey, vLst);
                }
                vLst.add(valObj.toString());
            }
        }
    }

    private static void fetchLeafKV(JSONObject jsonObject, Map<String, List> kvs) throws JSONException {
        Iterator<String> iterator = jsonObject.keys();
        String key = null;
        while (iterator.hasNext()) {
            key = iterator.next();
            Object valObj = jsonObject.get(key);
            if (valObj instanceof JSONObject) {
                fetchLeafKV((JSONObject) valObj, kvs);
            } else if (valObj instanceof JSONArray) {
                JSONArray array = (JSONArray) valObj;
                fetchLeafKV(key, array, kvs);
            } else {
                List vLst = null;
                String lKey = key;
                if (kvs.containsKey(lKey)) {
                    vLst = kvs.get(lKey);
                } else {
                    vLst = new LinkedList();
                    kvs.put(lKey, vLst);
                }
                vLst.add(valObj.toString());
            }
        }
    }

    private static String getPriKey() {
        return "";
    }

    private static String getPubKey() {
        return "";
    }
}
