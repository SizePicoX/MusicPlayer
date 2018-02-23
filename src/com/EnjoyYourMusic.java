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
        buffer = new BufferedInputStream(new FileInputStream(currentMusicNode.music.getMp3FilePath()));
        javazoom.jl.player.Player player = new javazoom.jl.player.Player(buffer);
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
                //do nothing
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
