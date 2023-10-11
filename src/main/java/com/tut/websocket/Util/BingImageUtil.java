package com.tut.websocket.Util;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.tut.websocket.Web.MyWebSocket;


public class BingImageUtil {

    /**
     * Bing壁紙シンクロナイズ
     * @param index 開始点、0は今日、1は昨日、2は一昨日、など
     * @param sum 壁紙シンクロナイズの数、最大7枚
     */
    public static Integer download(Integer index, Integer sum){
        Integer i = 0;
        String result = HttpUtil.get("https://www.bing.com/HPImageArchive.aspx?format=js&idx="+index+"&n="+sum);
        JSONObject jsonObject = JSONUtil.parseObj(result);
        JSONArray array = JSONUtil.parseArray(jsonObject.get("images"));
        for (int j = 0; j < array.size(); j++) {
            String url = "http://s.cn.bing.net"+(JSONUtil.parseObj(array.get(j)).get("url").toString());
            if (!MyWebSocket.BingImages.contains(url)){
                MyWebSocket.BingImages.add(url);
                i+=1;
            }
        }
        return i;
    }

}
