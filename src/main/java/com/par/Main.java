package com.par;

public class Main {

    public static void main(String[] args) {
        String sourcePath = "src/main/resources/ParsingTest.txt";   //测试源文件目录
        String targetPath = "src/main/resources/ParsingTree.txt";   //生成目标文件目录
        File.getFile(sourcePath);   //获取测试文件
        File.makeFile(targetPath);  //生成目标文件

        TreeNode root = Parser.parse(); //进行词法扫描和语法分析
        File.printKind("Syntax Tree:");
        Parser.step++;
        File.printTree(root);   //打印语法树

        try {
            File.fileWriter.close();
        }catch (Exception e){
            System.out.println(e);
        }
    }
}

