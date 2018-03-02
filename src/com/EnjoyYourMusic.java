package com;

import CloudMusicGUI.CloudMusic;
import com.List.MusicNode;
import javazoom.jl.decoder.JavaLayerException;

import java.io.*;

/**
 * enjoy your music!!!
 */
public class EnjoyYourMusic {

    public static BufferedInputStream buffer;
    /**
     * 不允许创建MusicPlayer的对象
     */
    private EnjoyYourMusic(){

    }


    /**
     * 播放MP3音频
     */
    private static void playMP3(MusicNode currentMusicNode)throws IOException,JavaLayerException{
        if (!CloudMusic.flag || CloudMusic.isPlayOtherMusic){
            buffer = new BufferedInputStream(new FileInputStream(currentMusicNode.music.getMp3FilePath()));
            //获取当前播放时间
            CloudMusic.starTime = System.currentTimeMillis();
        }
        javazoom.jl.player.Player player = new javazoom.jl.player.Player(buffer);
        if (CloudMusic.iSliderDragged){
            double percent = (CloudMusic.cloudMusic.currentPlayTime.getValue() * 1.0) / (CloudMusic.cloudMusic.currentPlayTime.getMaximum() * 1.0);
            //计算本次偏移所需要的偏移量
            //如果是320的比特率的话才能精准的找到偏移位置.这是当前一大不足.
            String[] songTime = currentMusicNode.music.getSongTime().split(":");
            int offset = currentMusicNode.music.getID3InfoLength() +
                    (Integer.parseInt(songTime[0]) * 60 + Integer.parseInt(songTime[1])) * 320 * 1000 / 8;
            buffer.skip((long)(offset * percent));
            //重定位startTime
            CloudMusic.starTime = System.currentTimeMillis() - CloudMusic.cloudMusic.currentPlayTime.getValue() * 1000;
        }
        //本次快进结束
        CloudMusic.iSliderDragged = false;
        player.play();
        buffer.close();
    }

    /**
     * 播放WAV音频
     */
    private static void playWAV(){

    }

    /**
     * 播放WMA音频
     */
    private static void playWMA(){

    }

    /**
     * 开始听歌吧!!!
     */
    public static void play(MusicNode selectedMusicNode){
        //暂存选定文件路径
        String str = selectedMusicNode.music.getMp3FilePath();
        //得到选定文件类型在str中的下标
        int index = str.length() - 3;
        //获取文件类型
        String TYPE = str.substring(index);
        //如果文件是MP3文件
        if (TYPE.equalsIgnoreCase("mp3")){
            try {
                EnjoyYourMusic.playMP3(selectedMusicNode);
            } catch (IOException | JavaLayerException e) {
                //播放标志置false
                CloudMusic.IS_PLAY_OR_PAUSE = false;
            }
        }
        //如果是WAV文件
        else if (TYPE.equalsIgnoreCase("wav")){
            EnjoyYourMusic.playWAV();
        }
        //如果是WMA文件
        else {
            EnjoyYourMusic.playWMA();
        }
    }


    /**
     *仅供测试使用.
     * 输入MP3文件路劲以播放
     */
    public static void test (String filePath) throws IOException,JavaLayerException{
        long a = System.currentTimeMillis();
        buffer = new BufferedInputStream(new FileInputStream(filePath));
        javazoom.jl.player.Player player = new javazoom.jl.player.Player(buffer);
        long b = System.currentTimeMillis();
        //初始化播放器所需要的时间
        System.out.println(b - a);
        player.play();
        buffer.close();
    }
}
