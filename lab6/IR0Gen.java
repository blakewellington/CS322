// This is supporting software for CS321/CS322 Compilers and Language Design.
// Copyright (c) Portland State University
// 
// IR0 code generator. (For CS322 Lab6, Jingke Li)
//
// (Baseline version)
//
//
import java.util.*;
import java.io.*;
import ast0.*;
import ir0.*;

class IR0Gen {

  static class IR0GenException extends Exception {
      public IR0GenException(String msg) { super(msg); }
  }

  // Structured return value for gen routines

  static class ValPack {
    IR0.Src src;
    List<IR0.Inst> code;
    ValPack(IR0.Src s, List<IR0.Inst> sl) { src=s; code=sl; }
    ValPack(IR0.Src s) { src=s; code=new ArrayList<IR0.Inst>(); }
  } 

  public static void main(String [] args) throws Exception {
    if (args.length == 1) {
      FileInputStream stream = new FileInputStream(args[0]);
      Ast0.Program p = new ast0Parser(stream).Program();
      stream.close();
      IR0.Program ir0 = IR0Gen.gen(p);
      System.out.print(ir0.toString());
    } else {
      System.out.println("You must provide an input file name.");
    }
  }

  // Ast0.Program ---
  // Ast0.Stmt[] stmts;
  //
  // Template:
  //   code: {stmt.c}
  //
  public static IR0.Program gen(Ast0.Program n) throws Exception {
    List<IR0.Inst> code = new ArrayList<IR0.Inst>();
    for (Ast0.Stmt s: n.stmts)
      code.addAll(gen(s));
    return new IR0.Program(code);
  }

  // STATEMENTS

  static List<IR0.Inst> gen(Ast0.Stmt n) throws Exception {
    if (n instanceof Ast0.Assign)      return gen((Ast0.Assign) n);
    else if (n instanceof Ast0.If)     return gen((Ast0.If) n);
    else if (n instanceof Ast0.While)  return gen((Ast0.While) n);
    else if (n instanceof Ast0.Print)  return gen((Ast0.Print) n);
    throw new IR0GenException("Unknown Ast0 Stmt: " + n);
  }

  // Ast0.Assign ---
  // Ast0.Exp lhs, rhs;
  //
  // Template:
  //   code: rhs.c + lhs.c + ( lhs.l = rhs.v     # if lhs is id
  //                         | [lhs.l] = rhs.v ) # otherwise
  //
  static List<IR0.Inst> gen(Ast0.Assign n) throws Exception {
    List<IR0.Inst> code = new ArrayList<IR0.Inst>();
    ValPack dst, src = gen(n.rhs);
    code.addAll(src.code);
    if (n.lhs instanceof Ast0.Id) {
      dst = gen((Ast0.Id) n.lhs);
      code.add(new IR0.Move((IR0.Dest) dst.src, src.src));
    } else if (n.lhs instanceof Ast0.ArrayElm) {
      dst = genAddr((Ast0.ArrayElm) n.lhs);
      code.addAll(dst.code);
      code.add(new IR0.Store((IR0.Dest) dst.src, src.src));
    } else {
      throw new IR0GenException("Wrong Ast0 lhs Exp: " + n.lhs);
    }
    return code;
  }

  // Ast0.If ---
  // Ast0.Exp cond;
  // Ast0.Stmt s1, s2;
  //
  // Template:
  //   newLabel: L1[,L2]
  //   code: cond.c 
  //         + "if cond.v == false goto L1" 
  //         + s1.c 
  //         [+ "goto L2"] 
  //         + "L1:" 
  //         [+ s2.c]
  //         [+ "L2:"]
  //
  static List<IR0.Inst> gen(Ast0.If n) throws Exception {
    List<IR0.Inst> code = new ArrayList<IR0.Inst>();
    IR0.Label L1 = new IR0.Label();
    ValPack p = gen(n.cond);
    code.addAll(p.code);
    code.add(new IR0.CJump(IR0.RelOP.EQ, p.src, IR0.FALSE, L1));
    code.addAll(gen(n.s1));
    if (n.s2 == null) {
      code.add(new IR0.LabelDec(L1));
    } else {	
      IR0.Label L2 = new IR0.Label();
      code.add(new IR0.Jump(L2));
      code.add(new IR0.LabelDec(L1));
      code.addAll(gen(n.s2));
      code.add(new IR0.LabelDec(L2));
    }
    return code;
  }

  // Ast0.While ---
  // Ast0.Exp cond;
  // Ast0.Stmt s;
  //
  // Template:
  //   newLabel: L1,L2
  //   code: "L1:" 
  //         + cond.c 
  //         + "if cond.v == false goto L2" 
  //         + s.c 
  //         + "goto L1" 
  //         + "L2:"
  //
  static List<IR0.Inst> gen(Ast0.While n) throws Exception {
    List<IR0.Inst> code = new ArrayList<IR0.Inst>();
    IR0.Label L1 = new IR0.Label();
    IR0.Label L2 = new IR0.Label();
    code.add(new IR0.LabelDec(L1));
    ValPack p = gen(n.cond);
    code.addAll(p.code);
    code.add(new IR0.CJump(IR0.RelOP.EQ, p.src, IR0.FALSE, L2));
    code.addAll(gen(n.s));
    code.add(new IR0.Jump(L1));
    code.add(new IR0.LabelDec(L2));
    return code;
  }
  
  // Ast0.Print ---
  // Ast0.Exp arg;
  //
  // Template:
  //   newTemp: t
  //   code: e1.c + e2.c 
  //         + "t = e1.v op e2.v"
  //
  static List<IR0.Inst> gen(Ast0.Print n) throws Exception {
    List<IR0.Inst> code = new ArrayList<IR0.Inst>();
    ValPack p = gen(n.arg);
    code.addAll(p.code);
    code.add(new IR0.Print(p.src));
    return code;
  }

  // EXPRESSIONS

  static ValPack gen(Ast0.Exp n) throws Exception {
    if (n instanceof Ast0.Binop)    return gen((Ast0.Binop) n);
    if (n instanceof Ast0.Unop)     return gen((Ast0.Unop) n);
    if (n instanceof Ast0.NewArray) return gen((Ast0.NewArray) n);
    if (n instanceof Ast0.ArrayElm) return gen((Ast0.ArrayElm) n);
    if (n instanceof Ast0.Id)	    return gen((Ast0.Id) n);
    if (n instanceof Ast0.IntLit)   return gen((Ast0.IntLit) n);
    if (n instanceof Ast0.BoolLit)  return gen((Ast0.BoolLit) n);
    throw new IR0GenException("Unknown Exp node: " + n);
  }

  // Ast0.Binop ---
  // Ast0.BOP op;
  // Ast0.Exp e1,e2;
  //
  // Template for arith and logical op:
  //   newTemp: t
  //   code: e1.c + e2.c
  //         + "t = e1.v op e2.v"
  //
  // Template for rel op:
  //   newTemp: t
  //   newLabel: L
  //   code: e1.c + e2.c
  //         + "t = true"
  //         + "if e1.v op e2.v goto L"
  //         + "t = false"
  //         + "L:"
  //
  static ValPack gen(Ast0.Binop n) throws Exception {
    List<IR0.Inst> code = new ArrayList<IR0.Inst>();
    ValPack l = gen(n.e1);
    ValPack r = gen(n.e2);
    IR0.BOP op = gen(n.op);
    IR0.Temp t = new IR0.Temp();
    code.addAll(l.code);
    code.addAll(r.code);
    if (isRelOp(n.op)) {
      IR0.Label L = new IR0.Label();
      code.add(new IR0.Move(t, IR0.TRUE));
      code.add(new IR0.CJump((IR0.RelOP) op, l.src, r.src, L));
      code.add(new IR0.Move(t, IR0.FALSE));
      code.add(new IR0.LabelDec(L));
    } else {
      code.add(new IR0.Binop((IR0.ArithOP) op, t, l.src, r.src));
    }
    return new ValPack(t, code);
  }

  // Ast0.Unop ---
  // Ast0.UOP op;
  // Ast0.Exp e;
  //
  // Template:
  //   newTemp: t
  //   code: e.c + "t = op e.v"
  //
  static ValPack gen(Ast0.Unop n) throws Exception {
    List<IR0.Inst> code = new ArrayList<IR0.Inst>();
    ValPack p = gen(n.e);
    code.addAll(p.code);
    IR0.UOP op = (n.op == Ast0.UOP.NEG) ? IR0.UOP.NEG : IR0.UOP.NOT;
    IR0.Temp t = new IR0.Temp();
    code.add(new IR0.Unop(op, t, p.src));
    return new ValPack(t, code);
  }
  
  // Ast0.NewArray ---
  // int len;
  // 
  // Template:
  //   newTemp: t
  //   code: "t = malloc (len * 4)"
  //
  static ValPack gen(Ast0.NewArray n) throws Exception {
    List<IR0.Inst> code = new ArrayList<IR0.Inst>();
    IR0.IntLit arg = new IR0.IntLit(n.len * 4);
    IR0.Temp t = new IR0.Temp();
    code.add(new IR0.Malloc(t, arg));
    return new ValPack(t, code);
  }

  // Ast0.ArrayElm ---
  // Ast0.Exp ar, idx;
  //
  // Template:
  //   newTemp: t1,t2,t3
  //   code: ar.c + idx.c
  //         + "t1 = idx.v * 4"
  //         + "t2 = ar.v + t1"
  //         + "t3 = [t2]"
  //
  static ValPack gen(Ast0.ArrayElm n) throws Exception {
    ValPack p = genAddr(n);
    IR0.Temp t = new IR0.Temp();
    p.code.add(new IR0.Load(t, (IR0.Dest)p.src));
    return new ValPack(t, p.code);
  }
  
  static ValPack genAddr(Ast0.ArrayElm n) throws Exception {
    List<IR0.Inst> code = new ArrayList<IR0.Inst>();
    ValPack ar = gen(n.ar);
    ValPack idx = gen(n.idx);
    IR0.Temp t1 = new IR0.Temp();
    IR0.Temp t2 = new IR0.Temp();
    code.addAll(ar.code);
    code.addAll(idx.code);
    IR0.IntLit intSz = new IR0.IntLit(4);
    code.add(new IR0.Binop(IR0.ArithOP.MUL, t1, idx.src, intSz));
    code.add(new IR0.Binop(IR0.ArithOP.ADD, t2, ar.src, t1));
    return new ValPack(t2, code);
  }

  // Ast0.Id ---
  // String nm;
  //
  static ValPack gen(Ast0.Id n) throws Exception {
    return new ValPack(new IR0.Id(n.nm));
  }

  // Ast0.IntLit ---
  // int i;
  //
  static ValPack gen(Ast0.IntLit n) throws Exception {
    return  new ValPack(new IR0.IntLit(n.i));
  }

  // Ast0.BoolLit ---
  // boolean b;
  //
  static ValPack gen(Ast0.BoolLit n) throws Exception {
    return  new ValPack(n.b ? IR0.TRUE : IR0.FALSE);
  }

  // OPERATORS

  static IR0.BOP gen(Ast0.BOP op) throws Exception {
    switch (op) {
    case ADD: return IR0.ArithOP.ADD;
    case SUB: return IR0.ArithOP.SUB;
    case MUL: return IR0.ArithOP.MUL;
    case DIV: return IR0.ArithOP.DIV;
    case AND: return IR0.ArithOP.AND;
    case OR:  return IR0.ArithOP.OR; 
    case EQ:  return IR0.RelOP.EQ; 
    case NE:  return IR0.RelOP.NE; 
    case LT:  return IR0.RelOP.LT; 
    case LE:  return IR0.RelOP.LE; 
    case GT:  return IR0.RelOP.GT; 
    case GE:  return IR0.RelOP.GE; 
    }
    throw new IR0GenException("Unknown BOP: " + op);
  }
   
  static boolean isRelOp(Ast0.BOP op) throws Exception {
    switch (op) {
    case EQ:  return true; 
    case NE:  return true; 
    case LT:  return true; 
    case LE:  return true; 
    case GT:  return true; 
    case GE:  return true; 
    }
    return false;
  }
   
  static IR0.RelOP negateRelOp(Ast0.BOP op) throws Exception {
    switch (op) {
    case EQ:  return IR0.RelOP.NE; 
    case NE:  return IR0.RelOP.EQ; 
    case LT:  return IR0.RelOP.GE; 
    case LE:  return IR0.RelOP.GT; 
    case GT:  return IR0.RelOP.LE; 
    case GE:  return IR0.RelOP.LT; 
    }
    throw new IR0GenException("Expect a RelOp: " + op);
  }
   
}
