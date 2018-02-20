package com;

import com.List.MusicNode;
import javazoom.jl.decoder.JavaLayerException;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * enjoy your music!!!
 */
public class EnjoyYourMusic {


    /**
     * 当前播放器正在播放的音乐
     */
    private static MusicNode currentMusicNode = null;


    /**
     * 不允许创建MusicPlayer的对象
     */
    private EnjoyYourMusic(){

    }


    /**
     * 播放MP3音频
     */
    private static void playMP3() throws JavaLayerException {
        try {
            BufferedInputStream buffer = new BufferedInputStream(new FileInputStream(currentMusicNode.music.getMp3FilePath()));
            javazoom.jl.player.Player player = new javazoom.jl.player.Player(buffer);
            player.play();
        }catch(IOException ex){
            ex.printStackTrace();
        }
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
        // TODO: 2018/2/17 该方法必须要能够处理播放断点.应该时使用线程的wait方法相关来解决
        //当播放器并没有放歌
        if (currentMusicNode == null){
            EnjoyYourMusic.currentMusicNode = selectedMusicNode;
        }
        else {

        }
        //暂存选定文件路径
        String str = selectedMusicNode.music.getMp3FilePath();
        //得到选定文件类型在str中的下标
        int index = str.length() - 3;
        //获取文件类型
        String TYPE = str.substring(index);
        //如果文件是MP3文件
        if (TYPE.equalsIgnoreCase("mp3")){
            try {
                EnjoyYourMusic.playMP3();
            }catch (JavaLayerException ex){
                ex.printStackTrace();
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
}