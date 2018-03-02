import CloudMusicGUI.*;
import com.EnjoyYourMusic;
import com.MusicPlayer;

import java.awt.*;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * 播放器的启动类
 */
public class START {
    public static void main(String[] args){
        //初始化MusicPlayer
        MusicPlayer.Init();
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
        //初始化右键菜单
        CurrentMusicListRightButtonMenu.currentMusicListRightButtonMenu = CurrentMusicListRightButtonMenu.getCurrentMusicListRightButtonMenu();
        MusicListRightButtonMenu.musicListRightButtonMenu = MusicListRightButtonMenu.getMusicListRightButtonMenu();
        //初始化CloudMusic
        CloudMusic.cloudMusic = CloudMusic.getCloudMusic();
        Thread cloudMusicThread = new Thread(CloudMusic.cloudMusic,"cloudMusicThread");
        //初始化currentPlayTimeLabel以及进度条
        if (MusicPlayer.currentMusicNode != null){
            CloudMusic.cloudMusic.currentPlayTimeLabel.setText(String.valueOf(MusicPlayer.currentPlayTime / 60) + ":" + String.valueOf(MusicPlayer.currentPlayTime % 60));
            CloudMusic.cloudMusic.songTimeLabel.setText(MusicPlayer.currentMusicNode.music.getSongTime());
            CloudMusic.cloudMusic.currentPlayTime.setMaximum(MusicPlayer.currentMusicNode.getSongTime());
            CloudMusic.cloudMusic.currentPlayTime.setValue(MusicPlayer.currentPlayTime);
            CloudMusic.starTime = System.currentTimeMillis() - MusicPlayer.currentPlayTime * 1000;
            //将播放流放置到指定的位置
            try {
                double percent = (CloudMusic.cloudMusic.currentPlayTime.getValue() * 1.0) / (CloudMusic.cloudMusic.currentPlayTime.getMaximum() * 1.0);
                //计算本次偏移所需要的偏移量
                //如果是320的比特率的话才能精准的找到偏移位置.这是当前一大不足.
                String[] songTime = MusicPlayer.currentMusicNode.music.getSongTime().split(":");
                int offset = MusicPlayer.currentMusicNode.music.getID3InfoLength() +
                        (Integer.parseInt(songTime[0]) * 60 + Integer.parseInt(songTime[1])) * 320 * 1000 / 8;
                EnjoyYourMusic.buffer = new BufferedInputStream(new FileInputStream(MusicPlayer.currentMusicNode.music.getMp3FilePath()));
                EnjoyYourMusic.buffer.skip((long)(offset * percent));
                //用于恢复上一次的播放进度
                CloudMusic.flag = true;
            } catch (IOException e) {
                //do nothing
            }
        }
        cloudMusicThread.start();
        //初始化MiniCloudMusic
        MiniCloudMusic.miniCloudMusic = MiniCloudMusic.getMiniCloudMusic();

    }
}
