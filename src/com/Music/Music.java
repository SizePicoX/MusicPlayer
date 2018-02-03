package com.Music;

import ID3InfoDecoder.Decoder;
import java.io.UnsupportedEncodingException;


/**
 * 选择MP3文件以添加乐曲时的最小操作单元
 */
public class Music {

    //这些是显示在界面中的
    private String SongName;//乐曲名字
    private String Artist;//歌手
    private String Album;//专辑
    private String SongTime;//歌曲时长
    private String mp3FilePath;//存储这首歌曲的路径，每次修改都应当被更新


    /**根据给出的MP3文件路径，初始化MP3文件的主要信息
     * @param mp3FilePath  给定的MP3文件路径
     * @return 返回一个记载MP3文件基本信息的字符串数组
     *  P.S.该数组大小为5，第一个元素为标志位，为“true”时表示该字符数组有效，为“false”时表示无效，
     *                  此时上层调用该方法者需要放弃本次构造，换下一个路径上的文件来构造Music对象
     */
    public  static String[] setMusicInfo(String mp3FilePath){
        /* 记载MP文件基本信息的数组，函数结束后返回 */
        String[] MusicInfo = new String[5];
        /* 初始化解码器 */
        Decoder decoder = new Decoder(mp3FilePath);

        /* 当Decoder构造函数成功时，才执行解码 */
        if (decoder.isModify()){
            //当且仅当给定文件为MP3且为ID3V2.3时才开始解码
            if (decoder.IS_ID3_OR_NOT()){
                try{
                    //调用解码器，获取MP3文件的主要信息
                    MusicInfo = decoder.getMusicInfo();
                }catch (UnsupportedEncodingException ex){
                    ex.printStackTrace();
                    //解码失败
                    MusicInfo[0] = "false";
                    return MusicInfo;
                }
            }
            else {
                System.out.println("MP3文件头标志位不是“ID3”，无法完成初始化！");
                //解码失败
                MusicInfo[0] = "false";
                return MusicInfo;
            }
            //解码成功
            MusicInfo[0] = "true";
            return MusicInfo;
        }
        //解码失败
        else {
         MusicInfo[0] = "false";
         return MusicInfo;
        }
    }



    /**
     * @param mp3FilePath MP3文件的路径
     * @param MusicInfo 保存MP3文件基本信息的字符串数组
     */
    public Music(String mp3FilePath,String[] MusicInfo){
        this.mp3FilePath = mp3FilePath;
        /* 由于MusicInfo的第一位是标志位，故下标从1开始 */
        SongName = MusicInfo[1];//歌曲名
        Artist = MusicInfo[2];//歌手
        Album = MusicInfo[3];//专辑
        SongTime = MusicInfo[4];//歌曲时长
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
