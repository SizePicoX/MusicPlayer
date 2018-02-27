package Implements;


import com.List.MusicList;
import com.List.MusicNode;
import com.Music.Music;
import com.MusicPlayer;

import javax.swing.filechooser.FileFilter;
import java.io.*;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 工具类，用于：
 * 1.将有符号数转化为我们需要的无符号数
 * 2.以及在选定范围内产生不同的随机正整数数
 */
public class Implements {

    /**
     * 不允许其他类创建该工具类的对象
     */
    private Implements(){}
    /**
     * @param SingedNum 输入一个待转换的有符号数，必须时字节型变量
     * @return 输出该字节对应的无符号数，例如，输入-77，输出179
     */
    public static int SignedToUnsigned(byte SingedNum){
        int sum;
        sum = (SingedNum & 0X80) + (SingedNum & 0X40) + (SingedNum & 0X20) + (SingedNum & 0X10) +
                (SingedNum & 0X08) + (SingedNum & 0X04) + (SingedNum & 0X02) + (SingedNum & 0X01);
        return sum;
    }
    /**
     * @param max 想要获取的随机数的最大值
     * @return 返回的随机数的绝对值，在1到max之间
     */
    public static int GetRandomNum(int max){

        Random random = new Random();
        //当随机值为true时，GetRandomNum返回result，当为false时，GetRandomNum返回 -result
        boolean flag = random.nextBoolean();
        //返回的结果的绝对值
        int result = random.nextInt(max) + 1;

        //正向偏移返回正数，反向偏移返回负数
        if (flag) return result;
        return (-1) * result;
    }
    /**
     * 删除指定文件夹下所有非文件夹的文件
     * @param selectedFolder  指定的文件夹
     */
    public static boolean deleteMusicDirectory(File selectedFolder){
        /* 当给定的文件不是文件夹时 */
        if (selectedFolder.isFile()){
            return selectedFolder.delete();
        }
        /* 当给定的文件是文件夹时，则递归调用以删除其中的非文件夹文件 */
        else {
            String[] childFilePath = selectedFolder.list();
            if (childFilePath != null) {
                for (String path : childFilePath){
                    File selectedFile = new File(selectedFolder.getAbsolutePath() + "\\" + path);
                    deleteMusicDirectory(selectedFile);
                }
            }
            /* 删除文件夹 */
            return selectedFolder.delete();
        }
    }

    /**
     * 渲染器，修改musicInfoText的每一个子串的长度(方法是添加空格),
     * 使得最终输出的字符串能够与GUI中的labelPanel组件的位置相匹配
     *
     * @param musicInfoText 包含乐曲信息的字符串
     * @return 能够与labelPanel组件的位置相匹配的字符串
     */
    public static String renderer(String musicInfoText){

        String[] text = musicInfoText.split(",");

        //渲染SongName
        int cnt_ch = 0;//每一乐曲字段中的中文字数
        int cnt_en = 0;//每一乐曲字段中英文字数
        char[] elements = text[0].toCharArray();
        double px = 0.0;//text[0]对应的像素大小
        for (char element : elements) {
            //字符为英文时
            if ((element > 'a' && element < 'z') || (element > 'A' && element < 'Z')){
                px += 10.65;
                ++cnt_en;
            }
            //为中文时
            else {
                px += 21.3;
                ++cnt_ch;
            }
        }
        if (px > 22 * 14){
            text[0] = text[0].substring(0,cnt_ch + cnt_en / 2 - 3) + "...";
        }
        else {
            StringBuilder str = new StringBuilder();
            int i = (int)Math.floor((22 * 14 - px) / 14.0);//还需要补多少个空格
            while (i > 0){
                str.append(" ");
                --i;
            }
            text[0] = text[0] + str;
        }

        //渲染Artist
        cnt_ch = 0;
        cnt_en = 0;
        px = 0.0;
        elements = text[1].toCharArray();
        for (char element : elements) {
            //字符为英文时
            if ((element > 'a' && element < 'z') || (element > 'A' && element < 'Z')){
                px += 10.65;
                ++cnt_en;
            }
            //为中文时
            else {
                px += 21.3;
                ++cnt_ch;
            }
        }
        if (px > 20 * 14){
            text[1] = text[1].substring(0,cnt_ch + cnt_en / 2 - 3) + "...";
        }
        else {
            StringBuilder str = new StringBuilder();
            int i = (int)Math.floor((20 * 14 - px) / 14.0);//还需要补多少个空格
            while (i > 0){
                str.append(" ");
                --i;
            }
            text[1] = text[1] + str;
        }

        //渲染Album
        cnt_ch = 0;
        cnt_en = 0;
        px = 0.0;
        elements = text[2].toCharArray();
        for (char element : elements) {
            //字符为英文时
            if ((element > 'a' && element < 'z') || (element > 'A' && element < 'Z')){
                px += 10.65;
                ++cnt_en;
            }
            //为中文时
            else {
                px += 21.3;
                ++cnt_ch;
            }
        }
        if (px > 18 * 14){
            text[2] = text[2].substring(0,cnt_ch + cnt_en / 2 - 3) + "...          ";
        }
        else {
            StringBuilder str = new StringBuilder();
            int i = (int)Math.ceil((18 * 14 - px) / 14.0);//还需要补多少个空格
            while (i > 0){
                str.append(" ");
                --i;
            }
            text[2] = text[2] + str;
        }

        return text[0] + text[1] + text[2] + text[3];
    }

    /**
     * 在播放前,保证MusicPlay.currentMusicNode指向一个可播放的文件
     * @param node 应当是MusicPlay.currentMusicNode
     * @return 经处理后的指向可播放文件的MusicNode
     */
    public static MusicNode ensureNotNull(MusicNode node){
        try {
            //此时处理node不为空但可能指向一个不可播放的文件的情况
            String str = node.music.getMp3FilePath();
            //如果不是可播放的MP3文件则继续循环直到找到为止
            while (!(new File(str).isFile() && (str.endsWith(".mp3") || str.endsWith(".MP3")))){
                node = MusicPlayer.getNextMusic();
                str = node.music.getMp3FilePath();
            }
            return node;
        }catch (NullPointerException ex){
            //此时处理node为空的情况
            node = MusicPlayer.currentMusicList.getFirstMusic();
            return ensureNotNull(node);
        }
    }

    private static int[] next;

    private static void getNext(char[] subStrArray){
        int i = 0,j = -1;//i为假主串指针,j为假子串指针
        next = new int[subStrArray.length];
        next[0] = -1;
        while (i < subStrArray.length){
            if (j == -1 || subStrArray[i] == subStrArray[j]){
                ++i;
                ++j;
                next[i - 1] = j - 1;
            }else
                j = next[j];
        }
    }

    public static int KMP(String str,String subStr){
        char[] strArray = str.trim().toCharArray();
        char[] subStrArray = subStr.trim().toCharArray();
        //获取next数组
        getNext(subStrArray);
        //开始KMP
        int i = 0,j = 0;//i为主串指针,j为子串指针
        while (i < strArray.length && j < subStrArray.length){
            char s1 = strArray[i];
            char s2 = ' ';
            //当j为-1时代表此时没有找到下一个位置.
            if (j != -1) s2 = subStrArray[j];
            if (j == -1 || ((s1 == s2) || (Character.toLowerCase(s1) == s2) || (Character.toUpperCase(s1) == s2))){
                ++i;
                ++j;
            }
            else {
                j = next[j];
            }
        }
        if (j == subStrArray.length) return i - subStrArray.length;
        //没有找到则返回-1
        return -1;
    }
}
