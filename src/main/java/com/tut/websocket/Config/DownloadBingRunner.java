package com.tut.websocket.Config;

import com.tut.websocket.Util.BingImageUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 *
 * プロジェクトの開始時にBing壁紙をダウンロードするためのクラス
 **/
@Component
public class DownloadBingRunner implements CommandLineRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(DownloadBingRunner.class);

    @Override
    public void run(String... args) throws Exception {
        Integer sum = BingImageUtil.download(0,7);
        sum += BingImageUtil.download(7,7);
        LOGGER.debug("今回は"+sum+"枚の壁紙をシンクロナイズしました！！");
    }

}
