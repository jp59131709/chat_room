package com.tut.websocket.Web;

import cn.hutool.core.util.StrUtil;
import com.tut.websocket.Model.User;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;


@ServerEndpoint(value = "/websocket")
@Component
public class MyWebSocket {
    private static final Logger logger = LoggerFactory.getLogger(MyWebSocket.class);

    //現在のオンライン接続数を記録する静的変数。スレッドセーフに設計すべき。
    private static int onlineCount = 0;

    //クライアントとの接続セッションは、クライアントにデータを送信するために必要です
    private Session session;

    //ユーザーと部屋番号の対応関係を記録するために使用（sessionId，room）
    private static HashMap<String,String> RoomForUser = new HashMap<String,String>();

    //部屋とその中のユーザーグループとの対応関係を記録するために使用する（room，List＜ユーザー＞）
    public static HashMap<String,CopyOnWriteArraySet<User>> UserForRoom = new HashMap<String,CopyOnWriteArraySet<User>>();

    //部屋とその中のユーザーグループとの対応関係を記録するために使用する（room，List＜ユーザー＞）
    public static HashMap<String,String> PwdForRoom = new HashMap<String,String>();

    //bing壁紙を保存するために使用する
    public static List<String> BingImages = new ArrayList<>();

    private Gson gson = new Gson();

    private Random random = new Random();

    /**
     * 接続が正常に呼び出された方法
     * @param session
     */
    @OnOpen
    public void onOpen(Session session) throws IOException {
        logger.debug("---------------------{}との接続確立に成功しました---------------------",session.getId());
        this.session = session;
        addOnlineCount();
        Map<String,String> result = new HashMap<>();
        result.put("type","bing");
        result.put("msg",BingImages.get(random.nextInt(BingImages.size())));
        result.put("sendUser","システムメッセージ");
        result.put("id",session.getId());
        this.sendMessage(gson.toJson(result));
    }

    /**
     * 接続停止呼び出しの方法
     */
    @OnClose
    public void onClose() {
        subOnlineCount();
        CopyOnWriteArraySet<User> users = getUsers(session);
        if (users!=null){
            String nick = "ある人";
            for (User user : users) {
                if (user.getId().equals(session.getId())){
                    nick = user.getNickname();
                }
            }
            Map<String,String> result = new HashMap<>();
            result.put("type","init");
            result.put("msg",nick+"部屋を出る");
            result.put("sendUser","システムメッセージ");
            sendMessagesOther(users,gson.toJson(result));
            User closeUser = getUser(session);
            users.remove(closeUser);
            if (users.size() == 0){
                String room = RoomForUser.get(session.getId());
                UserForRoom.remove(room);
                PwdForRoom.remove(room);
            }
            RoomForUser.remove(session.getId());
        }
    }

    /**
     * クライアント・メッセージを受信した後に呼び出す方法
     * @param message メッセージの内容
     * @param session
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        Map<String,String> map = new Gson().fromJson(message, new TypeToken<HashMap<String,String>>(){}.getType());
        Map<String,String> result = new HashMap<>();
        User user = null;
        String shiels = map.containsKey("shiels")?map.get("shiels").toString():null;
        switch (map.get("type")){
            case "msg" :
                user = getUser(session);
                result.put("type","msg");
                result.put("msg",map.get("msg"));
                result.put("sendUser",user.getNickname());
                result.put("shake",map.get("shake"));
                break;
            case "init":
                String room = map.get("room");
                String nick = map.get("nick");
                String pwd = map.get("pwd");
                if (room != null && nick != null){
                    user = new User(session.getId(),nick,this);
                    //部屋が存在しない場合は、新しい部屋を作成します
                    if (UserForRoom.get(room) == null){
                        CopyOnWriteArraySet<User> roomUsers = new CopyOnWriteArraySet<>();
                        roomUsers.add(user);
                        UserForRoom.put(room,roomUsers);
                        if (StrUtil.isNotEmpty(pwd)){
                            PwdForRoom.put(room,pwd);
                        }
                        RoomForUser.put(session.getId(),room);
                    }else {
                        UserForRoom.get(room).add(user);
                        RoomForUser.put(session.getId(),room);
                    }
                    result.put("type","init");
                    result.put("msg",nick+"ルームへの参加に成功しました");
                    result.put("sendUser","システムメッセージ");
                }
                break;
            case "img":
                user = getUser(session);
                result.put("type","img");
                result.put("msg",map.get("msg"));
                result.put("sendUser",user.getNickname());
                break;
            case "ping":
                return;
        }
        if (StrUtil.isEmpty(shiels)){
            sendMessagesOther(getUsers(session),gson.toJson(result));
        }else {
            sendMessagesOther(getUsers(session),gson.toJson(result),shiels);
        }
    }

    /**
     * 接続エラー発生時の呼び出し方法
     * @param session
     * @param error
     */
    @OnError
    public void onError(Session session, Throwable error) {
        logger.debug("---------------------{}との接続にエラーが発生しました---------------------",session.getId());
        subOnlineCount();
        CopyOnWriteArraySet<User> users = getUsers(session);
        if (users!=null){
            String nick = "ある人";
            for (User user : users) {
                if (user.getId().equals(session.getId())){
                    nick = user.getNickname();
                }
            }
            Map<String,String> result = new HashMap<>();
            result.put("type","init");
            result.put("msg",nick+"部屋を出る");
            result.put("sendUser","システムメッセージ");
            sendMessagesOther(users,gson.toJson(result));
            User closeUser = getUser(session);
            users.remove(closeUser);
            if (users.size() == 0){
                String room = RoomForUser.get(session.getId());
                UserForRoom.remove(room);
                PwdForRoom.remove(room);
            }
            RoomForUser.remove(session.getId());
        }
        error.printStackTrace();
    }


    public void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
    }


    /**
     * オンライン人数の取得
     * @return
     */
    public static synchronized int getOnlineCount() {
        return onlineCount;
    }

    public static synchronized void addOnlineCount() {
        MyWebSocket.onlineCount++;
    }

    public static synchronized void subOnlineCount() {
        MyWebSocket.onlineCount--;
    }


    /**
     * 現在のユーザーのセッションに基づいて、自分の部屋のすべてのユーザーを取得
     * @param session
     * @return
     */
    private CopyOnWriteArraySet<User> getUsers(Session session){
        String room = RoomForUser.get(session.getId());
        CopyOnWriteArraySet<User> users = UserForRoom.get(room);
        return users;
    }

    private User getUser(Session session){
        String room = RoomForUser.get(session.getId());
        CopyOnWriteArraySet<User> users = UserForRoom.get(room);
        for (User user : users){
            if (session.getId().equals(user.getId())){
                return user;
            }
        }
        return null;
    }

    /**
     * ある部屋のすべての人にメッセージを送信
     * @param users
     * @param message
     */
    private void sendMessagesAll(CopyOnWriteArraySet<User> users, String message){
        //群発メッセージ
        for (User item : users) {
            try {
                item.getWebSocket().sendMessage(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 自分以外の部屋にメッセージを送信する
     * @param users
     * @param message
     */
    private void sendMessagesOther(CopyOnWriteArraySet<User> users, String message){
        //群発メッセージ
        for (User item : users) {
            if (item.getWebSocket() != this){
                try {
                    item.getWebSocket().sendMessage(message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 自分以外の部屋にメッセージを送信する
     * @param users
     * @param message
     */
    private void sendMessagesOther(CopyOnWriteArraySet<User> users, String message, String shiel){
        List<String> shiels = Arrays.asList(shiel.split(","));
        //群発メッセージ
        for (User item : users) {
            if (item.getWebSocket() != this && !shiels.contains(item.getId())){
                try {
                    item.getWebSocket().sendMessage(message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}