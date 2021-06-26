package com.lex;

import java.util.*;
import java.io.*;
import java.lang.String;

public class Lexical {

    //测试
    public static void main(String[] args) {

        Lexical lexical=new Lexical();
        lexical.lexicalAnalysis("test.txt");
    }

    //变量定义
    private static String[] reservedWord = new String[]{"if", "else", "int", "return", "void", "while"};
    private static char[] symbol = new char[]{'+', '-', '*', '/', '<', '>', '=', '!', ';', ',',
            '(', ')', ']', '[', '{', '}'};
    private static File f; //测试文件名
    private static BufferedWriter bw;//写缓冲
    private static BufferedReader br;//读缓冲
    private static StringBuilder sb=new StringBuilder();

    //判断类型
    //判断是否为数字
    public static boolean isNumber(char c) {
        if(c>='0'&&c<='9')
            return true;
        else
            return false;
    }

    //判断是否为字母
    public static boolean isLetter(char c) {
        if((c>='a'&&c<='z') || (c>='A'&&c<='Z'))
            return true;
        else
            return false;
    }

    //判断是否为关键字
    public static boolean isKeyword(String s) {
        for(int i=0;i<reservedWord.length;i++)
        {
            if(reservedWord[i].equals(s))
                return true;
        }
        return false;
    }

    //判断是否为定界字符
    public static boolean isSymbol(char c) {
        for(int i=0;i<symbol.length;i++)
        {
            if(c==symbol[i])
                return true;
        }
        return false;
    }

    //读取文件
    public static StringBuilder readFile(StringBuilder sb,String fileSrc) {
        try {
            FileReader fileReader = new FileReader(fileSrc);
            BufferedReader br = new BufferedReader(fileReader);
            String temp = null;
            while ((temp = br.readLine()) != null) {
                sb.append(temp);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb;
    }

    public static void lexicalAnalysis(String filePath) {//进行词法分析

        int count=1;    //行数
        try {
            f = new File("result.txt");
            bw = new BufferedWriter(new FileWriter(f));
            br = new BufferedReader(new FileReader(filePath));

            while(true) {
                int i=0;
                String container=br.readLine();
                if(container==null)
                    break;
                else {
                    System.out.println(count+":"+container);
                    bw.write(count+":"+container+"\r\n");
                    while (i< container.length()) {  	//对容器进行读取
                        char ch = container.charAt(i); 	//对字符进行索引（读取）

                        //如果第一个字符是字母
                        if (isLetter(ch)) {
                            StringBuilder sb1 = new StringBuilder();
                            sb1.append(ch);
                            ch = container.charAt(++i);	//读取下一个字符
                            while (isLetter(ch) || isNumber(ch)) { //当后续字符是数字或者字母
                                sb1.append(ch);
                                if (i == container.length() - 1) {//读到行末尾
                                    ++i;
                                    break;
                                }
                                else
                                    ch = container.charAt(++i);
                            }
                            //判断当前字符串是否是关键字
                            if (isKeyword(sb1.toString())) {

                                bw.write("	"+count+":  "+"reserved word:" + sb1.toString());
                                bw.newLine();//换行
                            }
                            //标识符，即为ID
                            else {
                                bw.write("	"+count+":  "+"ID, name=" + sb1.toString());
                                bw.newLine();
                            }
                        }

                        //如果第一个是分界符
                        else if (isSymbol(ch)) {
                            StringBuilder sb1 = new StringBuilder();
                            //如果是单分界符，直接写入
                            if (ch == ';' || ch == ',' || ch == '*' ||
                                    ch == '(' || ch == ')' || ch == '[' ||
                                    ch == ']' || ch == '{' || ch == '}' ||
                                    ch == '+' || ch == '-' ) {
                                bw.write("	"+count+":  "+ch);
                                bw.newLine();
                                i++;
                            }
                            //如果是双分界符
                            else if (ch == '>' || ch == '<' || ch == '=') {
                                sb1.append(ch);
                                char next = container.charAt(++i);
                                //判断下一个字符是否为‘=’
                                //满足<=	、>= 、==，也一并写入
                                if (next == '=') {
                                    sb1.append(next);
                                    bw.write("	"+count+":  "+sb1.toString());
                                    bw.newLine();
                                    i++;
                                }
                                else {
                                    bw.write("	"+count+":  "+ch);
                                    bw.newLine();
                                }
                            }

                            //判断注释
                            else if (ch == '/') {
                                sb1.append(ch);
                                if (i == container.length() - 1) {
                                    break;
                                }
                                //继续读取下一个字符
                                ch = container.charAt(++i);
                                if(ch == '*') {//为/*注释
                                    while(true){
                                        ch = container.charAt(++i);
                                        if(ch == '*'){// 为多行注释结束
                                            ch = container.charAt(++i);
                                            if(ch == '/') {
                                                //ch = container.charAt(++i);
                                                break;
                                            }
                                        }
                                    }
                                }
                                else{//否则为 /，直接写入
                                    bw.write("	"+count+":  "+sb1.toString());
                                    bw.newLine();
                                }
                            }
                        }

                        //如果第一个是数字
                        else if (isNumber(ch)) {
                            StringBuilder sb1 = new StringBuilder();
                            sb1.append(ch);
                            ch = container.charAt(++i);

                            if (isNumber(ch)) {  //数字后是数字
                                while (isNumber(ch)) {
                                    sb1.append(ch);
                                    ch = container.charAt(++i);
                                }
                                bw.write("	"+count+":"+"  NUM, val=" + sb1.toString());
                                bw.newLine();
                            }
                            else if (isLetter(ch)) { //数字后是字母
                                System.out.println("error:非法字符");
                            }
                            else {
                                bw.write("	"+count+":"+"  NUM, val=" + sb1.toString());
                                bw.newLine();
                            }
                        }
                        else
                            i++;
                    }

                }
                count++;
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
        finally{
            try {
                bw.close();
                br.close();
            }
            catch(IOException e){
                e.printStackTrace();
            }
        }
    }

}