package com.Music;

import ID3InfoDecoder.Decoder;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;


/**
 * 选择MP3文件以添加乐曲时的最小操作单元
 */
public class Music implements Serializable{

    //这些是显示在界面中的
    private String SongName;//乐曲名字
    private String Artist;//歌手
    private String Album;//专辑
    private String SongTime;//歌曲时长
    private String mp3FilePath;//存储这首歌曲的路径，每次修改都应当被更新

    private int ID3InfoLength;//ID3头部文件长度
    /**根据给出的MP3文件路径，初始化MP3文件的主要信息
     * @param mp3FilePath  给定的MP3文件路径
     * @return 返回一个记载MP3文件基本信息的字符串数组
     * 依次表示：
     * 0.乐曲名
     * 1.歌手
     * 2.专辑
     * 3.乐曲时长
     * 4.MP3文件路径
     * 5.ID3头部文件长度
     * P.S.该数组大小为6，当解码失败时返回null
     */
    public  static String[] setMusicInfo(String mp3FilePath){
        /* 记载MP文件基本信息的数组，函数结束后返回 */
        String[] MusicInfo;
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
                    return null;
                }
            }
            else {
                System.out.println("MP3文件头标志位不是“ID3”，无法完成初始化！");
                //解码失败
                return null;
            }
            //解码成功
            return MusicInfo;
        }
        //解码失败
        else return null;
    }
    /**
     * @param MusicInfo 保存MP3文件基本信息的字符串数组
     */
    public Music(String[] MusicInfo){
        SongName = MusicInfo[0];//歌曲名
        Artist = MusicInfo[1];//歌手
        Album = MusicInfo[2];//专辑
        SongTime = MusicInfo[3];//歌曲时长
        mp3FilePath = MusicInfo[4];//MP3文件路径
        ID3InfoLength = Integer.valueOf(MusicInfo[5]);//ID3头部文件长度
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

    public boolean isSame(Music selectedMusic){
        return SongName.equals(selectedMusic.SongName) && Artist.equals(selectedMusic.Artist) &&
                Album.equals(selectedMusic.Album) && SongTime.equals(selectedMusic.SongTime);
    }

    public int getID3InfoLength() {
        return ID3InfoLength;
    }
}
