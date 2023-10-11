package com.tut.websocket.Config;

import com.tut.websocket.Util.BingImageUtil;
import com.tut.websocket.Web.SocketController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Iterator;
import java.util.Map;

/**
 *
 * このクラスはタイミングタスクで、使用前に起動クラスに@EnableScheduling注記を追加する必要があります
 **/
@Component
public class QuartzService {

    private static final Logger logger = LoggerFactory.getLogger(QuartzService.class);

    /**
     * チャット画像を定期的に削除する
     */
    @Scheduled(cron = "0 0/1 * * * ?")
    public void timerToNow(){
        logger.debug("削除する画像のクエリを開始。。。。。。。。。。");
        Map<Long,String> img = SocketController.img;
        Long now = System.currentTimeMillis()-60000;
        Iterator<Map.Entry<Long, String>> it = img.entrySet().iterator();
        int a = 0;
        while(it.hasNext()){
            Map.Entry<Long, String> entry = it.next();
            if (entry.getKey() < now){
                if (deleteFile(entry.getValue())){
                    it.remove();
                    a++;
                }
            }
        }
        logger.debug("タスクの削除が完了しました，合計"+a+"枚の画像を削除");
    }
    /**
     * Bing壁紙を定期的にダウンロードし、毎日正午12時にトリガー
     */
    @Scheduled(cron = "0 0 12 * * ?")
    public void dowBingImage(){
        logger.debug("Bing壁紙のシンクロナイズを開始。。。。。。。。。。。。。。。。");
        Integer i = BingImageUtil.download(0,1);
        logger.debug("今回は"+i+"枚の壁紙をシンクロナイズしました！");
    }

    private boolean deleteFile(String fileName) {
        File file = new File(fileName);
        // ファイルパスに対応するファイルが存在し、ファイルである場合は直接削除
        if (file.exists() && file.isFile()) {
            if (file.delete()) {
                logger.debug("画像"+fileName+"削除に成功しました");
                return true;
            } else {
                logger.debug("画像"+fileName+"削除に成功しました");
                return false;
            }
        } else {
            logger.debug("画像"+fileName+"削除に成功しました");
            return false;
        }
    }

}
