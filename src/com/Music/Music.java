package com.Music;

import ID3InfoDecoder.Decoder;
import java.io.UnsupportedEncodingException;


/**
 * 选择MP3文件以添加乐曲时的最小操作单元
 */
public class Music {

    //这些是显示在界面中的
    private String SongName = "未知";//乐曲名字
    private String Artist = "未知";//歌手
    private String Album = "未知";//专辑
    private String SongTime;//歌曲时长

    private String mp3FilePath;//存储这首歌曲的路径，每次修改都应当被更新


    /**
     * 根据给出的MP3文件路径，初始化它的实例
     */
    private void setMusicInfo(){
        Decoder decoder = new Decoder(mp3FilePath);
        if (decoder.IS_ID3_OR_NOT()){
            try{
                String[] MusicInfo;
                MusicInfo = decoder.getMusicInfo();

                SongName = MusicInfo[0];//TIT2,乐曲名字
                System.out.println("乐曲名字 " + SongName);//------------------TEST---------------------------

                Artist = MusicInfo[1];//TPE1，歌手
                System.out.println("歌手 " + Artist);//------------------TEST---------------------------

                Album = MusicInfo[2];//TALB，专辑
                System.out.println("专辑 " + Album);//------------------TEST---------------------------


              /*--------------------------------------------------------------------------------------------------------------
              目前无法通过检索ID3标签的方式得到歌曲时长
              故通过引入的包中的类来实现
              --------------------------------------------------------------------------------------------------------------*/

//              SongTime = MusicInfo[3];//TIME ，歌曲时长
                MusicInfo[3] = decoder.getSongTime();
                SongTime = MusicInfo[3];
                System.out.println("歌曲时长 " + SongTime);//------------------TEST---------------------------
            }catch (UnsupportedEncodingException ex){
                System.out.println("编码错误，初始化失败！");
            }
            System.out.println("初始化成功");
        }
        else {
            System.out.println("MP3文件头标志位不是“ID3”，无法完成初始化！");
        }
    }



    /**
     * @param mp3FilePath 通过歌曲路径来构造Music对象以初始化这首歌曲的基本信息
     *                    当添加歌曲时，先调用该方法创建该歌曲的实例，然后调用MusicList的addSong方法将这首歌曲添加到乐曲清单里面去
     *                    P.S.构造Music对象时不执行任何检查(执行检查开销也大)，无论添加的歌曲是否相同都不需要管，因为这是用户的事情
     */
    public Music(String mp3FilePath){
        this.mp3FilePath = mp3FilePath;
        setMusicInfo();//执行初始化
    }



    public String getSongName() {
        return SongName;
    }

    public String getArtist() {
        return Artist;
    }

    public String getAlbum() {
        return Album;
    }

    public String getSongTime() {
        return SongTime;
    }

    public String getMp3FilePath() {
        return mp3FilePath;
    }
}
