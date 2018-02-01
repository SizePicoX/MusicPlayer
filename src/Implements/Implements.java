package Implements;


import com.Music.Music;
import com.list.CurrentMusicList;
import com.list.MusicList;

import java.io.*;
import java.util.ArrayList;
import java.util.Random;

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
     * @param currentMusicList 被序列化的CurrentMusicList对象
     *               序列化存储的对象将被保存在 InitOfCurrentMusicList.ser 内
     */
    public static void Serialize(CurrentMusicList currentMusicList){
        try {
            FileOutputStream fs = new FileOutputStream("InitOfCurrentMusicList.ser");
            ObjectOutputStream os = new ObjectOutputStream(fs);
            /* 采用writeUnshared能重复写入同一个不断改变的对象 */
            os.writeUnshared(currentMusicList);
            os.close();
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }


    /**
     * @param musicList 被序列化的MusicList对象
     *                  序列化存储的对象将被保存在 InitOfMusicList.ser 内
     */
    public static void Serialize(MusicList musicList){
        try {
            FileOutputStream fs = new FileOutputStream("InitOfMusicList.ser");
            ObjectOutputStream os = new ObjectOutputStream(fs);
            /* 采用writeUnshared能重复写入同一个不断改变的对象 */
            os.writeUnshared(musicList);
            os.close();
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }


    /**
     * 解序列化方法
     * 启动播放器时才需要解序列化
     * 整个程序运行期间只需要调用一次
     * CurrentMusicList 放在InitOfCurrentMusicList.ser中
     * MusicList 放在InitOfMusicList.ser中
     */
    public static void DeSerialize(){

        /* 取出CurrentMusicList */
        try {
            FileInputStream fs = new FileInputStream("InitOfCurrentMusicList.ser");
            ObjectInputStream os = new ObjectInputStream(fs);
            /* 采用readUnshared读取不断在改变的对象 */
            CurrentMusicList.setCurrentMusicList((CurrentMusicList)os.readUnshared());
            os.close();
        }catch ( ClassNotFoundException ex){
            //当文件为空时
            CurrentMusicList.getCurrentMusicList().init();
        }catch (IOException ex){
            ex.printStackTrace();
        }

        /* 取出MusicList */
        try {
            FileInputStream fs = new FileInputStream("InitOfMusicList.ser");
            ObjectInputStream os = new ObjectInputStream(fs);
            /* 采用readUnshared读取不断在改变的对象 */
            MusicList.setMusicList((MusicList)os.readUnshared());
            os.close();
        }catch (ClassNotFoundException ex){
            //当文件为空时
            MusicList.getMusicList().init();
        }catch (IOException ex){
            ex.printStackTrace();
        }
    }


    /**
     * @param str 被搜索的主串
     * @param subStr 希望查找的子串
     * @return 当子串存在于主串时，返回true
     */
    public static boolean KMP(String str,String subStr){
        char[] strArr = str.toCharArray();
        char[] subStrArr = subStr.toCharArray();
        int[] Next = new int[subStrArr.length];
        //调用getNext方法得到Next数组
        getNext(subStrArr,Next);
        //开始KMP
        int i = 0; int j = 0;//分别指向主串和子串
        while (i < strArr.length && j < subStrArr.length){
            if (j == -1 || strArr[i] == subStrArr[j]){
                ++i;
                ++j;
            }
            //当子串在j位置不匹配时，重新指向新的位置
            else {
                j = Next[j];
            }
        }
        //当子串不在主串内时，返回false
        return j == subStrArr.length;
    }

    /**
     * @param subStrArr 所查询的子字符串
     * @param Next 输出的Next数组
     *             P.S.仅在KMP算法内被调用
     */
    private static void getNext(char[] subStrArr,int[] Next){
        int i = -1;  int j = 0;//分别表示主串制作==指针和子串指针
        Next[0] = -1;//代表不存在下一个指针位置
        while (j < subStrArr.length - 1){
            if (i == 0 || subStrArr[i] == subStrArr[j]){
                ++i;
                ++j;
                Next[i] = j;
            }
            else
                j = Next[j];
        }
    }
}
