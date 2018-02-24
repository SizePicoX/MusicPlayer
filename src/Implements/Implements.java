package Implements;


import com.List.MusicList;
import com.List.MusicNode;
import com.Music.Music;

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
}
