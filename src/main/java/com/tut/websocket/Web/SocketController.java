package com.tut.websocket.Web;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.tut.websocket.Model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Created by Chaofan at 2018/7/6 9:39
 * email:chaofan2685@qq.com
 **/

@RestController
@RequestMapping("/ws")
public class SocketController {

    private static final Logger logger = LoggerFactory.getLogger(SocketController.class);

    //画像保存パス
    private String imgPath = new ApplicationHome(getClass()).getSource().getParentFile().toString()+"/img/";


    public static Map<Long,String> img = new HashMap();

    /**
     * 部屋番号に基づいてユーザを取得
     * @param room 部屋番号
     * @return
     */
    @RequestMapping("/online")
    public Map<String,Object> online(String room){
        Map<String,Object> result = new HashMap<>();
        CopyOnWriteArraySet<User> rooms = MyWebSocket.UserForRoom.get(room);
//        List<String> nicks = new ArrayList<>();
        List<Map<String,String>> users = new ArrayList<>();
        if (rooms != null){
            rooms.forEach(user -> {
                Map<String,String> map = new HashMap<>();
                map.put("nick",user.getNickname());
                map.put("id",user.getId());
                users.add(map);
            });
            result.put("onlineNum",rooms.size());
            result.put("onlineUsera",users);
        }else {
            result.put("onlineNum",0);
            result.put("onlineUsera",null);
        }
        return result;
    }

    /**
     * ニックネームがある部屋にすでに存在するかどうか、部屋にパスワードがあるかどうかを判断し、
     * もしあれば、ユーザーが入力したパスワードが正しいかどうかを判断する
     * @param room ルームナンバー
     * @param nick ニックネーム
     * @param pwd パスワード
     * @return
     */
    @RequestMapping("/judgeNick")
    public Map<String,Object> judgeNick(String room, String nick, String pwd){
        Map<String,Object> result = new HashMap<>();
        result.put("code",0);
        CopyOnWriteArraySet<User> rooms = MyWebSocket.UserForRoom.get(room);
        if (rooms != null){
            rooms.forEach(user -> {
                if (user.getNickname().equals(nick)){
                    result.put("code",1);
                    result.put("msg","ニックネームは既に存在しますので、再入力してください");
                    logger.debug("重複あり");
                }
            });
            if ((Integer)result.get("code") != 0){
                return result;
            }
            String password = MyWebSocket.PwdForRoom.get(room);
            if (StrUtil.isNotEmpty(password) && !(pwd.equals(password))){
                result.put("code",2);
                result.put("msg","パスワードが間違っています。再入力してください");
                return result;
            }else {
                result.put("code",3);
                result.put("msg","部屋にパスワードなし");
                return result;
            }
        }
        return result;
    }


    /**
     * ファイルアップロードの実装
     * */
    @RequestMapping("/fileUpload")
    public Map<String,Object> fileUpload(HttpServletRequest request, @RequestParam("file") MultipartFile file){
        Map<String,Object> result = new HashMap<>();
        //プロジェクトアクセスパスの取得
        String root = request.getRequestURL().toString().replace(request.getRequestURI(),"");
        if(file.isEmpty()){
            return null;
        }
        //ファイル名の取得
        String fileName = file.getOriginalFilename();
        //ファイル名の変更
        String imgName = RandomUtil.randomUUID() + fileName.substring(fileName.lastIndexOf("."));
        logger.debug("画像をアップロードして保存：" + imgPath + imgName);
        File dest = new File(imgPath + imgName);
        img.put(System.currentTimeMillis(),imgPath + imgName);
        //ファイルの親ディレクトリが存在するかどうかを判断する
        if(!dest.getParentFile().exists()){
            dest.getParentFile().mkdir();
        }
        try {
            //ファイルを保存
            file.transferTo(dest);
            //画像アクセスパスに戻る
            result.put("url",root +"/img/" + imgName);
            logger.debug("画像の保存に成功しました。アクセスパスは："+result.get("url"));
            return result;
        } catch (IllegalStateException e) {
            e.printStackTrace();
            logger.error("画像の保存に失敗しました！");
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("画像の保存に失敗しました！");
        }
        return null;
    }

    /**
     * すべての部屋を取得
     * @return
     */
    @RequestMapping("/allRoom")
    public Map<String,Object> allRoom(){
        Map<String,Object> result = new HashMap<>();
        HashMap<String,CopyOnWriteArraySet<User>> userForRoom = MyWebSocket.UserForRoom;
        List<String> rooms = new ArrayList<>();
        for (String key : userForRoom.keySet()) {
            rooms.add(key);
        }
        result.put("rooms",rooms);
        return result;
    }

}
