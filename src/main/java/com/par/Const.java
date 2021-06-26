package com.par;

import java.io.File;

public class Const {

    static File sourceFile;
    static File targetFile;
    static final char EOF = (char)-1;

    public enum TokenType {
        // 内务记号
        ENDFILE, ERROR,
        // 保留字
        IF, ELSE, INT, RETURN, VOID, WHILE,
        // 标识符和数字
        ID, NUM,
        // 特殊符号
        ASSIGN, EQ, LT, PLUS, MINUS, TIMES, OVER, LPAREN, RPAREN,
        SEMI, MORE, LESS, COMMA, LSQARE, RSQARE, LLPAREN, RLPAREN
    }

    public enum StateType{
        //开始，数字，标识符，赋值，注释，结束
        START, INNUM, INID, INASSIGN, INCOMMENT, DONE
    }

    public enum NodeKind{//节点类型
        //声明，语句，表达式
        DeclaK, StmtK, ExpK
    }

    public enum DeclaKind{
        //变量声明，函数声明，int类型，void类型，数组声明，数组元素
        VarDeclK, FuncK, IntK, VoidK, ArryDeclK, ArryElemK
    }

    public enum StmtKind{
        //比较，表达，选择，迭代，返回，调用，参数
        CompK, ExpK, SeleK, IteraK, RetuK, CallK, ParamK, ParamsK, ArgsK
    }

    public enum ExpKind{
        //操作，常量，标识符，赋值
        OpK, ConstK, IdK, AssignK
    }

    public enum ExpType{
        //void，int，布尔类型
        Void, Integer, Boolean
    }
}

