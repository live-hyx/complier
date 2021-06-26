package com.par;

import java.util.ArrayList;

public class Parser {

    static Const.TokenType token;
    static int step;

    static void syntaxError(String str) { //分析错误并输出错误行号
        try {
            File.fileWriter.write("Syntax error at line " + Scanner.lineNum + ": " + str + "\n");
        } catch (Exception e) {
            System.out.println(e);
        }
    }   //语法分析错误

    static void match(Const.TokenType expected) {
        if (token == expected) {
            token = Scanner.getToken();
        } else {
            syntaxError("unexpected token(" + expected.toString() + ") -> " + token.toString());
        }
    }   //匹配token，如果正确继续往后获取token

    static TreeNode declaration() {
        TreeNode t = newDeclaredNode(Const.DeclaKind.FuncK);
        if (token == Const.TokenType.INT) {
            t.child.add(newDeclaredNode(Const.DeclaKind.IntK));
            match(Const.TokenType.INT);
        } else if (token == Const.TokenType.VOID) {
            t.child.add(newDeclaredNode(Const.DeclaKind.VoidK));
            match(Const.TokenType.VOID);
        } else {
            syntaxError("类型匹配错误");
        }

        if (token == Const.TokenType.ID) {
            TreeNode q = newExpNode(Const.ExpKind.IdK);
            q.name = Scanner.tokenString.toString();
            match(Const.TokenType.ID);

            if (token == Const.TokenType.LPAREN) {  //左小括号
                t.child.add(q);
                match(Const.TokenType.LPAREN);
                t.child.add(params());
                match(Const.TokenType.RPAREN);  //右小括号
                t.child.add(compoundStmt());
            } else if (token == Const.TokenType.LSQARE) {   //左中括号
                t.decla = Const.DeclaKind.VarDeclK;
                TreeNode m = newDeclaredNode(Const.DeclaKind.ArryDeclK);

                match(Const.TokenType.LSQARE);
                match(Const.TokenType.NUM);
                m.child.add(q);
                m.child.add(newExpNode(Const.ExpKind.ConstK));
                t.child.add(m);
                match(Const.TokenType.RSQARE);  //右中括号
                match(Const.TokenType.SEMI);
            } else if (token == Const.TokenType.SEMI) { //分号
                t.child.add(q);
                match(Const.TokenType.SEMI);
            }
        } else {
            syntaxError("Declaration Error");
        }

        return t;
    }   //分析生成声明结点

    static TreeNode declarationList() {
        TreeNode t = declaration();
        TreeNode p = t;
        while (token != Const.TokenType.INT && token != Const.TokenType.VOID && token != Const.TokenType.ENDFILE) {
            syntaxError("DeclarationList Error");
            Scanner.getToken();
            if (token == Const.TokenType.ENDFILE) {
                break;
            }
        }
        while (token == Const.TokenType.INT || token == Const.TokenType.VOID) {
            TreeNode q = declaration();
            p.sibling = q;
            p = q;
        }
        return t;
    }   //声明列表（多个声明）

    static TreeNode param(TreeNode k) {
        TreeNode t = newStmtNode(Const.StmtKind.ParamK);
        TreeNode p = null;
        TreeNode q;

        if (k == null && token == Const.TokenType.INT) {
            p = newDeclaredNode(Const.DeclaKind.IntK);
            match(Const.TokenType.INT);
        } else if (k != null) {
            p = k;
        }
        if (p != null) {
            t.child.add(p);
            if (token == Const.TokenType.ID) {
                q = newExpNode(Const.ExpKind.IdK);
                q.name = Scanner.tokenString.toString();
                t.child.add(q);
                match(Const.TokenType.ID);
            } else {
                syntaxError("Param Error");
            }
            if (token == Const.TokenType.LSQARE && t.child.get(1) != null) {
                match(Const.TokenType.LSQARE);
                t.child.add(newExpNode(Const.ExpKind.IdK));
                match(Const.TokenType.RSQARE);
            } else {
                return t;
            }
        }
        return t;
    }   //分析生成参数的树节点

    static TreeNode paramList(TreeNode k) {
        TreeNode t = param(k);
        TreeNode p = t;

        while (token == Const.TokenType.COMMA) {    //逗号
            TreeNode q;
            match(Const.TokenType.COMMA);
            q = param(null);
            p.sibling = q;
            p = q;
        }
        return t;
    }   //参数列表（多个参数）

    static TreeNode params() {
        TreeNode t = newStmtNode(Const.StmtKind.ParamsK);
        TreeNode p;

        if (token == Const.TokenType.VOID) {
            p = newDeclaredNode(Const.DeclaKind.VoidK);
            match(Const.TokenType.VOID);
            if (token == Const.TokenType.RPAREN) {
                t.child.add(p);
            } else {
                t.child.add(paramList(p));
            }
        } else if (token == Const.TokenType.INT) {
            t.child.add(paramList(null));
        } else {
            syntaxError("Params Error");
        }

        return t;
    }   //params=>paramList/param

    static TreeNode localDeclaration() {
        TreeNode t = null;
        TreeNode q = null;
        TreeNode p;

        while (token == Const.TokenType.INT || token == Const.TokenType.VOID) {
            p = newDeclaredNode(Const.DeclaKind.VarDeclK);
            if (token == Const.TokenType.INT) {
                p.child.add(newDeclaredNode(Const.DeclaKind.IntK));
                match(Const.TokenType.INT);
            } else if (token == Const.TokenType.VOID) {
                p.child.add(newDeclaredNode(Const.DeclaKind.VoidK));
                match(Const.TokenType.VOID);
            }
            if (token == Const.TokenType.ID) {
                TreeNode tmp = newExpNode(Const.ExpKind.IdK);
                tmp.name = Scanner.tokenString.toString();
                p.child.add(tmp);
                match(Const.TokenType.ID);

                if (token == Const.TokenType.LSQARE) {
                    p.child.add(newDeclaredNode(Const.DeclaKind.VarDeclK));
                    match(Const.TokenType.LSQARE);
                    match(Const.TokenType.RSQARE);
                }
                match(Const.TokenType.SEMI);
            } else {
                syntaxError("LocalDeclaration Error");
            }

            if (t == null) {
                t = q = p;
            } else {
                q.sibling = p;
                q = p;
            }
        }
        return t;
    }   //分析生成本地声明的结点

    static TreeNode statement() {
        TreeNode t = null;
        switch (token) {
            case IF:
                t = selectionStmt();
                break;
            case WHILE:
                t = iterationStmt();
                break;
            case RETURN:
                t = returnStmt();
                break;
            case LLPAREN:
                t = compoundStmt();
                break;
            case ID:
            case SEMI:
            case LPAREN:
            case NUM:
                t = expressionStmt();
                break;
            default:
                syntaxError("Statement Error");
                token = Scanner.getToken();
                break;
        }
        return t;
    }   //分析生成语句结点

    static TreeNode statementList() {
        TreeNode t = statement();
        TreeNode p = t;

        while (token == Const.TokenType.IF || token == Const.TokenType.LLPAREN ||
                token == Const.TokenType.ID || token == Const.TokenType.WHILE ||
                token == Const.TokenType.RETURN || token == Const.TokenType.SEMI ||
                token == Const.TokenType.LPAREN || token == Const.TokenType.NUM) {
            TreeNode q = statement();
            if (q != null) {
                if (t == null) {
                    t = p = q;
                } else {
                    p.sibling = q;
                    p = q;
                }
            }
        }
        return t;
    }   //语句列表（多个语句）

    static TreeNode compoundStmt() {
        TreeNode t = newStmtNode(Const.StmtKind.CompK);
        match(Const.TokenType.LLPAREN); //左大括号
        t.child.add(localDeclaration());
        t.child.add(statementList());
        match(Const.TokenType.RLPAREN);//右大括号

        return t;
    }   //复合语句

    static TreeNode selectionStmt() {
        TreeNode t = newStmtNode(Const.StmtKind.SeleK);
        match(Const.TokenType.IF);
        match(Const.TokenType.LPAREN);

        t.child.add(expression());
        match(Const.TokenType.RPAREN);
        t.child.add(statement());

        if (token == Const.TokenType.ELSE) {
            match(Const.TokenType.ELSE);
            t.child.add(statement());
        }
        return t;
    }   //选择语句

    static TreeNode expressionStmt() {
        TreeNode t;
        if (token == Const.TokenType.SEMI) {
            match(Const.TokenType.SEMI);
            return null;
        } else {
            t = expression();
            match(Const.TokenType.SEMI);
        }
        return t;
    }

    static TreeNode iterationStmt() {
        TreeNode t = newStmtNode(Const.StmtKind.IteraK);
        match(Const.TokenType.WHILE);
        match(Const.TokenType.LPAREN);
        t.child.add(expression());
        match(Const.TokenType.RPAREN);
        t.child.add(statement());
        return t;
    }   //迭代语句

    static TreeNode returnStmt() {
        TreeNode t = newStmtNode(Const.StmtKind.RetuK);
        match(Const.TokenType.RETURN);

        if (token == Const.TokenType.SEMI) {
            match(Const.TokenType.SEMI);
            return t;
        } else {
            t.child.add(expression());
        }
        match(Const.TokenType.SEMI);

        return t;
    }   //return语句

    static TreeNode expression() {
        TreeNode t = var();
        if (t == null) {
            t = simpleExpression(null);
        } else {
            TreeNode p;
            if (token == Const.TokenType.EQ) {
                p = newExpNode(Const.ExpKind.AssignK);
                p.name = Scanner.tokenString.toString();
                match(Const.TokenType.EQ);
                p.child.add(t);
                p.child.add(expression());
                return p;
            } else {
                t = simpleExpression(t);
            }
        }
        return t;
    }   //表达式

    static TreeNode simpleExpression(TreeNode k) {
        TreeNode t = additiveExpression(k);

        if (token == Const.TokenType.ASSIGN || token == Const.TokenType.MORE || token == Const.TokenType.LESS) {
            TreeNode q = newExpNode(Const.ExpKind.OpK);
            q.op = token;
            q.name = Scanner.tokenString.toString();
            q.child.add(t);
            t = q;
            match(token);
            t.child.add(additiveExpression(null));
            return null;
        }
        return t;
    }   //简单表达式

    static TreeNode additiveExpression(TreeNode k) {
        TreeNode t = term(k);
        while (token == Const.TokenType.PLUS || token == Const.TokenType.MINUS) {
            TreeNode q = newExpNode(Const.ExpKind.OpK);
            q.op = token;
            q.name = Scanner.tokenString.toString();
            q.child.add(t);
            match(token);
            q.child.add(term(null));
            t = q;
        }

        return t;
    }   //加减法表达式

    static TreeNode term(TreeNode k) {
        TreeNode t = factor(k);
        while (token == Const.TokenType.TIMES || token == Const.TokenType.OVER) {
            TreeNode q = newExpNode(Const.ExpKind.OpK);
            q.op = token;
            q.name = Scanner.tokenString.toString();
            q.child.add(t);
            match(token);
            q.child.add(factor(null));
            t = q;
        }
        return t;
    }   //乘除法表达式

    static TreeNode factor(TreeNode k) {
        TreeNode t = null;

        if (k != null) {
            if (token == Const.TokenType.LPAREN) {
                t = call(k);
            } else {
                t = k;
            }
        } else {
            switch (token) {
                case LPAREN:
                    match(Const.TokenType.LPAREN);
                    t = expression();
                    match(Const.TokenType.RPAREN);
                    break;
                case ID:
                    k = var();
                    if (token == Const.TokenType.LPAREN) {
                        t = call(k);
                    } else {
                        t = k;
                    }
                    break;
                case NUM:
                    t = newExpNode(Const.ExpKind.ConstK);
                    if (token == Const.TokenType.NUM) {
                        t.val = Integer.parseInt(Scanner.tokenString.toString());
                    }
                    match(Const.TokenType.NUM);
                    break;
                default:
                    syntaxError("Factor Error: " + token.toString());
                    token = Scanner.getToken();
                    break;
            }
        }
        return t;
    }

    static TreeNode var() {
        TreeNode t = null;
        TreeNode p;
        TreeNode q;
        if (token == Const.TokenType.ID) {
            p = newExpNode(Const.ExpKind.IdK);
            p.name = Scanner.tokenString.toString();
            match(Const.TokenType.ID);
            if (token == Const.TokenType.LSQARE) {
                match(Const.TokenType.LSQARE);
                q = expression();
                match(Const.TokenType.RSQARE);

                t = newDeclaredNode(Const.DeclaKind.ArryElemK);
                t.child.add(p);
                t.child.add(q);
            } else {
                t = p;
            }
        }
        return t;
    }   //变量（标识）

    static TreeNode call(TreeNode k) {
        TreeNode t = newStmtNode(Const.StmtKind.CallK);
        if (k != null) {
            t.child.add(k);
        }
        match(Const.TokenType.LPAREN);

        if (token == Const.TokenType.RPAREN) {
            match(Const.TokenType.RPAREN);
            return t;
        } else if (k != null) {
            t.child.add(args());
            match(Const.TokenType.RPAREN);
        }
        return t;
    }   //调用

    static TreeNode args() {
        TreeNode t = newStmtNode(Const.StmtKind.ArgsK);
        TreeNode s = null;
        TreeNode p;

        if (token != Const.TokenType.RPAREN) {
            s = expression();
            p = s;
            while (token == Const.TokenType.COMMA) {
                TreeNode q;
                match(Const.TokenType.COMMA);
                q = expression();
                if (q != null) {
                    if (s == null) {
                        s = p = q;
                    } else {
                        p.sibling = q;
                        p = q;
                    }
                }
            }
        }
        if (s != null) {
            t.child.add(s);
        }
        return t;
    }   //参数

    static TreeNode newDeclaredNode(Const.DeclaKind kind) {
        TreeNode t = new TreeNode();

        t.nodeKind = Const.NodeKind.DeclaK;
        t.child = new ArrayList<>();
        t.decla = kind;
        t.lineNum = Scanner.lineNum;

        return t;
    }   //新建声明结点

    static TreeNode newExpNode(Const.ExpKind kind) {
        TreeNode t = new TreeNode();
        t.nodeKind = Const.NodeKind.ExpK;
        t.child = new ArrayList<>();
        t.exp = kind;
        t.lineNum = Scanner.lineNum;

        return t;
    }   //新建表达式结点

    static TreeNode newStmtNode(Const.StmtKind kind) {
        TreeNode t = new TreeNode();
        t.nodeKind = Const.NodeKind.StmtK;
        t.child = new ArrayList<>();
        t.stmt = kind;
        t.lineNum = Scanner.lineNum;

        return t;
    }   //新建语句结点

    static TreeNode parse() {
        step = 0;
        TreeNode root;
        token = Scanner.getToken();
        root = declarationList();
        if (token != Const.TokenType.ENDFILE) {
            syntaxError("Code ends before file\n");
        }
        return root;
    }   //语法分析
}

