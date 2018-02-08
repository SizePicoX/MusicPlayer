//package com.list;


//import Implements.Implements;
import javazoom.jl.decoder.*;

import java.io.*;

class A implements Serializable{
    int a;
    String a_str;
    A next;
    A prior;
    B b;
}

class B implements Serializable{
    String b_str;
}
public class test {

    public static A outPut(){
        //输出测试结果
        A test2;
        try{
            FileInputStream fs = new FileInputStream("test.ser");
            ObjectInputStream os = new ObjectInputStream(fs);
//            while (fs.available() > 0){
//                test2 = (A)os.readObject();
//                System.out.println(test2.a);
//                System.out.println(test2.a_str);
//            }
            test2 = (A)os.readObject();
//            int i = 0;
//            while (i < 6){
//                System.out.println(test2.a);
//                System.out.println(test2.a_str);
//                System.out.println(test2.b.b_str);
//                test2 = test2.next;
//                ++i;
//            }
            os.close();
            return test2;
        }catch (Exception ex){
            ex.printStackTrace();
            System.out.println("error!!!");
            return null;
        }
    }

    public static void main(String[] args)throws FileNotFoundException ,JavaLayerException{
//        //初始化B
//        B b1 = new B();  b1.b_str = "a";
//        B b2 = new B();  b2.b_str = "s";
//        B b3 = new B();  b3.b_str = "d";
//        B b4 = new B();  b4.b_str = "f";
//        B b5 = new B();  b5.b_str = "g";
//        B b6 = new B();  b6.b_str = "h";
//
//        //初始化A
//        A a1 = new A();  a1.a = 200; a1.a_str = "q";  a1.b = b1;
//        A a2 = new A();  a2.a = 0;   a2.a_str = "w";  a2.b = b2;
//        A a3 = new A();  a3.a = 3;   a3.a_str = "e";  a3.b = b3;
//        A a4 = new A();  a4.a = 4;   a4.a_str = "r";  a4.b = b4;
//        A a5 = new A();  a5.a = 5;   a5.a_str = "t";  a5.b = b5;
//        A a6 = new A();  a6.a = 6;   a6.a_str = "y";  a6.b = b6;
//        //修改指针
//        a1.next = a2;    a1.prior = a5;
//        a2.next = a3;    a2.prior = a1;
//        a3.next = a4;    a3.prior = a2;
//        a4.next = a5;    a4.prior = a3;
//        a5.next = a6;    a5.prior = a4;
//        a6.next = a1;    a6.prior = a5;
//
//
//        A test = a1;
//        FileOutputStream fs = new FileOutputStream("test.ser");
//
//            try{
//                ObjectOutputStream os = new ObjectOutputStream(fs);
//                //os.writeObject(test);
//                for (int i = 0 ; i < 6 ; ++i){
//                    os.writeObject(test);
//                    test =  test.next;
//                }
//                os.close();
//            }catch (Exception ex){
//                ex.printStackTrace();
//            }


//        //将文件置空
//               try {
//            FileOutputStream fileOutputStream = new FileOutputStream("test.ser");
//            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
//            objectOutputStream.writeObject(null);
//            objectOutputStream.close();
//        }catch (IOException ex){
//            ex.printStackTrace();
//        }

//        //输出测试
//        A qwe;
//        qwe =  test.outPut();
//        int i = 0;
//            while (i < 6){
//            System.out.println(qwe.a);
//            System.out.println(qwe.a_str);
//            System.out.println(qwe.b.b_str);
//            qwe = qwe.next;
//            ++i;
//        }



        A test2;
        int sum = 0;
        try {
            FileInputStream fs = new FileInputStream("test.ser");
            ObjectInputStream os = new ObjectInputStream(fs);

            test2 = (A)os.readObject();
            System.out.println(test2.a);
            System.out.println(test2.a_str);
            System.out.println(test2.b.b_str);

            System.out.println();

            while (fs.available() > 0){
                test2 = (A)os.readObject();
                System.out.println(test2.a);
                System.out.println(test2.a_str);
                System.out.println(test2.b.b_str);
                ++sum;
            }
            System.out.println();

            System.out.println(test2.a);
            System.out.println(test2.a_str);
            System.out.println(test2.b.b_str);

            System.out.println(sum);
        }catch (IOException | ClassNotFoundException ex){
            ex.printStackTrace();
        }

    }
}

