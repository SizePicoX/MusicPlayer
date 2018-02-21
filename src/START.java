import CloudMusicGUI.CloudMusic;
import CloudMusicGUI.CloudMusicTray;
import CloudMusicGUI.MiniCloudMusic;
import CloudMusicGUI.Tips;

import java.awt.*;

/**
 * 播放器的启动类
 */
public class START {
    public static void main(String[] args){
        //当且仅当系统支持系统托盘时，才初始化系统托盘
        if (SystemTray.isSupported()){
            //获得系统托盘
            CloudMusicTray.tray = CloudMusicTray.getCloudMusicTray();
            CloudMusic.isSupportedSystemTray = true;
        }
        else {
            //标记不支持系统托盘
            CloudMusic.isSupportedSystemTray = false;
            Point point = new Point(Toolkit.getDefaultToolkit().getScreenSize().width / 2,
                    Toolkit.getDefaultToolkit().getScreenSize().height);
            Tips tips = new Tips("该系统不支持系统托盘.",point);
            tips.pack();
            tips.setVisible(true);
        }
        //初始化CloudMusic
        CloudMusic.cloudMusic = CloudMusic.getCloudMusic();
        //初始化MiniCloudMusic
        MiniCloudMusic.miniCloudMusic = MiniCloudMusic.getMiniCloudMusic();
        CloudMusic.cloudMusic.setVisible(true);
    }
}
