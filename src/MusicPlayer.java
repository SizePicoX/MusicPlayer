import com.list.MusicNode;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * enjoy your music!!!
 */
public class MusicPlayer {
    /**
     * 播放MP3音频
     */
    private void playMP3(MusicNode selectedMusicNode) throws JavaLayerException {
        try {
            BufferedInputStream buuffer = new BufferedInputStream(new FileInputStream(selectedMusicNode.music.getMp3FilePath()));
            Player player = new Player(buuffer);
            player.play();
        }catch(IOException ex){}
    }

    /**
     * 播放WAV音频
     */
    private void playWAV(){

    }

    /**
     * 播放WMA音频
     */
    private void playWMA(){

    }

    /**
     * 开始听歌吧!!!
     */
    public void play(){

    }
}
