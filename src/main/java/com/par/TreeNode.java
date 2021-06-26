package com.par;

import java.util.List;

public class TreeNode { //树节点

    List<TreeNode> child;  //子节点
    TreeNode sibling;  //兄弟节点
    int lineNum;
    int val;
    String name;

    Const.NodeKind nodeKind;
    Const.DeclaKind decla;
    Const.StmtKind stmt;
    Const.ExpKind exp;
    Const.TokenType op;

}

