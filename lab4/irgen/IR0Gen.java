// This is supporting software for CS321/CS322 Compilers and Language Design.
// Copyright (c) Portland State University
// 
// IR0 code generator. (For CS322 Lab4, Jingke Li)
//
//
import java.util.*;
import java.io.*;
import ast0.*;
import ir0.*;

class IR0GenException extends Exception {
    public IR0GenException(String msg) { super(msg); }
}

// Structured return value for gen routines
//
class ValPack {
  IR0.Src src;
  List<IR0.Inst> code;
  ValPack(IR0.Src s, List<IR0.Inst> sl) { src=s; code=sl; }
  ValPack(IR0.Src s) { src=s; code=new ArrayList<IR0.Inst>(); }
}

// ---------------------
// The main IR0Gen class
// ---------------------
//
class IR0Gen {

  public static void main(String [] args) throws Exception {
    if (args.length == 1) {
      FileInputStream stream = new FileInputStream(args[0]);
      Ast0.Program p = new ast0.ast0Parser(stream).Program();
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

    // ... need code ...
    return null;

  }

  // Ast0.If ---
  // Ast0.Exp cond;
  // Ast0.Stmt s1, s2;
  //
  // Template:
  //   newLabel: L1[,L2]
  //   code: cond.c 
  //         + "if cond.v == 0 goto L1" 
  //         + s1.c 
  //         [+ "goto L2"] 
  //         + "L1:" 
  //         [+ s2.c]
  //         [+ "L2:"]
  //
  static List<IR0.Inst> gen(Ast0.If n) throws Exception {

    // ... need code ...
    return null;

  }

  // Ast0.While ---
  // Ast0.Exp cond;
  // Ast0.Stmt s;
  //
  // Template:
  //   newLabel: L1,L2
  //   code: "L1:" 
  //         + cond.c 
  //         + "if cond.v == 0 goto L2" 
  //         + s.c 
  //         + "goto L1" 
  //         + "L2:"
  //
  static List<IR0.Inst> gen(Ast0.While n) throws Exception {

    // ... need code ...
    return null;

  }
  
  // Ast0.Print ---
  // Ast0.Exp arg;
  //
  // Template:
  //   code: arg.c
  //         + "print (arg.v)"
  //
  static List<IR0.Inst> gen(Ast0.Print n) throws Exception {

    // ... need code ...
    return null;

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
  // Template for arith op:
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

    // ... need code ...
    return null;

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
    ValPack p = gen(n.e);
    IR0.UOP op = (n.op == Ast0.UOP.NEG) ? IR0.UOP.NEG : IR0.UOP.NOT;
    IR0.Temp t = new IR0.Temp();
    IR0.Inst s = new IR0.Unop(op, t, p.src);
    p.code.add(s);
    return new ValPack(t, p.code);

    // ... need code ...

    return null;
  }
  
  // Ast0.NewArray ---
  // int sz;
  // 
  // Template:
  //   newTemp: t
  //   code: "t = malloc (sz * 4)"
  //
  static ValPack gen(Ast0.NewArray n) throws Exception {

    // ... need code ...
    return null;
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

    // ... need code ...
    return null;

  }
  
  static ValPack genAddr(Ast0.ArrayElm n) throws Exception {

    // ... need code ...
    return null;

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
