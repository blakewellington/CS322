// This is supporting software for CS321/CS322 Compilers and Language Design.
// Copyright (c) Portland State University
// 
// IR1 code generator. (For CS322 Hw3)
//
//
import java.util.*;
import java.io.*;
import ast1.*;
import ir1.*;

class IR1GenException extends Exception {
    public IR1GenException(String msg) { super(msg); }
}

// Structured return value for gen routines
//
class ValPack {
  IR1.Src src;
  List<IR1.Inst> code;
  ValPack(IR1.Src s, List<IR1.Inst> sl) { src=s; code=sl; }
  ValPack(IR1.Src s) { src=s; code=new ArrayList<IR1.Inst>(); }
}

// ---------------------
// The main IR1Gen class
// ---------------------
//
class IR1Gen {

  public static void main(String [] args) throws Exception {
    if (args.length == 1) {
      FileInputStream stream = new FileInputStream(args[0]);
      Ast1.Program p = new ast1.ast1Parser(stream).Program();
      stream.close();
      IR1.Program ir1 = IR1Gen.gen(p);
      System.out.print(ir1.toString());
    } else {
      System.out.println("You must provide an input file name.");
    }
  }

  // Ast1.Program ---
  // Ast1.Func[] funcs;
  //
  public static IR1.Program gen(Ast1.Program n) throws Exception {
    // ... need code ... 
    List<IR1.Func> code = new ArrayList<IR1.Func>();
    for (Ast1.Func s: n.funcs)
      code.add(gen(s));
    return new IR1.Program(code);
  }

  // Ast1.Func --
  // Ast1.Type t;	   
  // String nm;	   
  // Ast1.Formal[] params; 
  // Ast1.VarDecl[] vars;  
  // Ast1.Stmt[] stmts;	   
  //
  static IR1.Func gen(Ast1.Func n) throws Exception {
    
    // ... need code ... 
	List<IR1.Var> params = new ArrayList<IR1.Var>();
	List<IR1.Inst> instructions = new ArrayList<IR1.Inst>();
	List<IR1.Var> locals = new ArrayList<IR1.Var>();
	
	for (Ast1.Formal p: n.params) {
		params.add(new IR1.Var(p.nm, gen(p.t)));
	}
	for (Ast1.VarDecl l: n.vars) {
		if (l.init != null) {
			instructions.addAll(gen(l));
		}
		locals.add(new IR1.Var(l.nm, gen(l.t)));
	}
	for (Ast1.Stmt s: n.stmts) {
		instructions.addAll((gen(s)));
	}	
    return new IR1.Func(n.nm, params, locals, instructions, gen(n.t));
  }

  // Ast1.VarDecl ---
  // Ast1.Type t;     
  // Ast1.String nm;  
  // Ast1.Exp init;   
  //
  private static List<IR1.Inst> gen(Ast1.VarDecl n) throws Exception {

    // ... need code ... 
	  List<IR1.Inst> code = new ArrayList<IR1.Inst>();
	  // new IR1.Id
	  IR1.Id id = new IR1.Id(n.nm);
	  ValPack vp = gen(n.init);
	  IR1.Move move = new IR1.Move((IR1.Dest) id, vp.src ) ;
	  code.addAll(vp.code);
	  // new Move to new Id
	  code.add(move);

	  return code;
  }

  // STATEMENTS

  static List<IR1.Inst> gen(Ast1.Stmt n) throws Exception {
    if (n instanceof Ast1.Block) 	 return gen((Ast1.Block) n);
    else if (n instanceof Ast1.Assign)   return gen((Ast1.Assign) n);
    else if (n instanceof Ast1.CallStmt) return gen((Ast1.CallStmt) n);
    else if (n instanceof Ast1.If) 	 return gen((Ast1.If) n);
    else if (n instanceof Ast1.While)    return gen((Ast1.While) n);
    else if (n instanceof Ast1.Print)    return gen((Ast1.Print) n);
    else if (n instanceof Ast1.Return)   return gen((Ast1.Return) n);
    throw new IR1GenException("Unknown Ast1 Stmt: " + n);
  }

  // Ast1.Block ---
  // Ast1.Stmt[] stmts;
  //
  static List<IR1.Inst> gen(Ast1.Block n) throws Exception {

    // ... need code ... 
	  List<IR1.Inst> code = new ArrayList<IR1.Inst>();
	  for (Ast1.Stmt s: n.stmts) {
		  code.addAll(gen(s));
	  }

	    return code;
  }

  // Ast1.Assign ---
  // Ast1.Exp lhs, rhs;
  //
  // Template:
  //   code: rhs.c + lhs.c + ( lhs.l = rhs.v     # if lhs is id
  //                         | [lhs.l] = rhs.v ) # otherwise
  //
  static List<IR1.Inst> gen(Ast1.Assign n) throws Exception {
	    List<IR1.Inst> code = new ArrayList<IR1.Inst>();
	    ValPack dst, src = gen(n.rhs);
	    code.addAll(src.code);
	    if (n.lhs instanceof Ast1.Id) {
	      dst = gen((Ast1.Id) n.lhs);
	      code.add(new IR1.Move((IR1.Dest) dst.src, src.src));
	    } else if (n.lhs instanceof Ast1.ArrayElm) {
	      dst = genAddr((Ast1.ArrayElm) n.lhs);
	      code.addAll(dst.code);
	      code.add(new IR1.Store((IR1.Dest) dst.src, src.src));
	    } else {
	      throw new IR1GenException("Wrong Ast1 lhs Exp: " + n.lhs);
	    }
	    return code;
    // ... need code ...

  }

  // Ast1.CallStmt ---
  // Ast1.String nm;
  // Ast1.Exp[] args;
  //
  static List<IR1.Inst> gen(Ast1.CallStmt n) throws Exception {

	  List<IR1.Inst> code = new ArrayList<IR1.Inst>();
    // ... need code ...
	  List<IR1.Src> sources = new ArrayList<IR1.Src>();
	  
	  for (Ast1.Exp e: n.args){
		 ValPack p = gen(e);
		 code.addAll(p.code);
		 sources.add(p.src);
	  }
	  
	  IR1.Call c = new IR1.Call(n.nm, sources);
	  code.add(c);
    return code;
  }

  // Ast1.If ---
  // Ast1.Exp cond;
  // Ast1.Stmt s1, s2;
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
  static List<IR1.Inst> gen(Ast1.If n) throws Exception {
	    List<IR1.Inst> code = new ArrayList<IR1.Inst>();
	    String alt = IR1.newLabel();
	    ValPack p = gen(n.cond);
	    code.addAll(p.code);
	    code.add(new IR1.CJump(IR1.RelOP.EQ, p.src, IR1.FALSE, alt));
	    code.addAll(gen(n.s1));
	    if (n.s2 == null) {
	      code.add(new IR1.LabelDec(alt));
	    } else {	
	      String done = IR1.newLabel();
	      code.add(new IR1.Jump(done));
	      code.add(new IR1.LabelDec(alt));
	      code.addAll(gen(n.s2));
	      code.add(new IR1.LabelDec(done));
	    }
	    return code;
    // ... need code ...

  }

  // Ast1.While ---
  // Ast1.Exp cond;
  // Ast1.Stmt s;
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
  static List<IR1.Inst> gen(Ast1.While n) throws Exception {
	    List<IR1.Inst> code = new ArrayList<IR1.Inst>();
	    String test = IR1.newLabel();
	    String done = IR1.newLabel();
	    code.add(new IR1.LabelDec(test));
	    ValPack p = gen(n.cond);
	    code.addAll(p.code);
	    code.add(new IR1.CJump(IR1.RelOP.EQ, p.src, IR1.FALSE, done));
	    code.addAll(gen(n.s));
	    code.add(new IR1.Jump(test));
	    code.add(new IR1.LabelDec(done));
	    return code;
    // ... need code ...

  }
  
  // Ast1.Print ---
  // Ast1.Exp arg;
  //
  // Template:
  //   code: arg.c
  //         + "call print (arg.v)"
  //
  static List<IR1.Inst> gen(Ast1.Print n) throws Exception {
	    List<IR1.Inst> code = new ArrayList<IR1.Inst>();
	    List<IR1.Src> sources = new ArrayList<IR1.Src>(); 
	    ValPack p = gen(n.arg);
	    sources.add(p.src);
	    code.addAll(p.code);
	    code.add(new IR1.Call("print", sources));
	    return code;


  }

  // Ast1.Return ---  
  // Ast1.Exp val;
  //
  static List<IR1.Inst> gen(Ast1.Return n) throws Exception {
	    List<IR1.Inst> code = new ArrayList<IR1.Inst>();
	    if (n.val != null) {
		    ValPack p = gen(n.val);
		    code.addAll(p.code);
		    code.add(new IR1.Return(p.src));
	    }
	    else {
	    	code.add(new IR1.Return());
	    }
	    return code;
  }

  // EXPRESSIONS

  static ValPack gen(Ast1.Exp n) throws Exception {
    if (n instanceof Ast1.Binop)    return gen((Ast1.Binop) n);
    if (n instanceof Ast1.Unop)     return gen((Ast1.Unop) n);
    if (n instanceof Ast1.Call)     return gen((Ast1.Call) n);
    if (n instanceof Ast1.NewArray) return gen((Ast1.NewArray) n);
    if (n instanceof Ast1.ArrayElm) return gen((Ast1.ArrayElm) n);
    if (n instanceof Ast1.Id)	    return gen((Ast1.Id) n);
    if (n instanceof Ast1.IntLit)   return gen((Ast1.IntLit) n);
    if (n instanceof Ast1.BoolLit)  return gen((Ast1.BoolLit) n);
    if (n instanceof Ast1.StrLit)   return gen((Ast1.StrLit) n);
    throw new IR1GenException("Unknown Exp node: " + n);
  }

  // Ast1.Binop ---
  // Ast1.BOP op;
  // Ast1.Exp e1,e2;
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
  static ValPack gen(Ast1.Binop n) throws Exception {
	    List<IR1.Inst> code = new ArrayList<IR1.Inst>();
	    ValPack l = gen(n.e1);
	    ValPack r = gen(n.e2);
	    IR1.BOP op = gen(n.op);
	    code.addAll(l.code);
	    code.addAll(r.code);
	    IR1.Temp t = new IR1.Temp();
	    if (isRelOp(n.op)) {
	      String lab = IR1.newLabel();
	      code.add(new IR1.Move(t, IR1.TRUE));
	      code.add(new IR1.CJump((IR1.RelOP) op, l.src, r.src, lab));
	      code.add(new IR1.Move(t, IR1.FALSE));
	      code.add(new IR1.LabelDec(lab));
	    } else {
		code.add(new IR1.Binop((IR1.ArithOP) op, t, l.src, r.src));
	    }	
	    return new ValPack(t, code);
    // ... need code ...

  }

  // Ast1.Unop ---
  // Ast1.UOP op;
  // Ast1.Exp e;
  //
  // Template:
  //   newTemp: t
  //   code: e.c + "t = op e.v"
  //
  static ValPack gen(Ast1.Unop n) throws Exception {
	    List<IR1.Inst> code = new ArrayList<IR1.Inst>();
	    ValPack p = gen(n.e);
	    code.addAll(p.code);
	    IR1.UOP op = (n.op == Ast1.UOP.NEG) ? IR1.UOP.NEG : IR1.UOP.NOT;
	    IR1.Temp t = new IR1.Temp();
	    code.add(new IR1.Unop(op, t, p.src));
	    return new ValPack(t, code);
    // ... need code ...

  }
  
  // Ast1.Call ---
  // Ast1.String nm;
  // Ast1.Exp[] args;
  //
  static ValPack gen(Ast1.Call n) throws Exception {
	  List<IR1.Inst> code = new ArrayList<IR1.Inst>();
	  List<IR1.Src> sources = new ArrayList<IR1.Src>();
	  
	  for (Ast1.Exp e: n.args){
		 ValPack p = gen(e);
		 code.addAll(p.code);
		 sources.add(p.src);
	  }
	  
	  IR1.Call c = new IR1.Call(n.nm, sources, new IR1.Temp());
	  code.add(c);
    return new ValPack((IR1.Src) c.rdst, code);
  }

  // Ast1.NewArray ---
  // int sz;
  // 
  // Template:
  //   newTemp: t
  //   code: "t = call malloc (sz * 4)"
  //
  static ValPack gen(Ast1.NewArray n) throws Exception {
	    List<IR1.Inst> code = new ArrayList<IR1.Inst>();
	    IR1.IntLit arg = new IR1.IntLit(n.sz * 4);
	    IR1.Temp t = new IR1.Temp();
	    

	    List<IR1.Src> sources = new ArrayList<IR1.Src>();
	    sources.add(arg);
	    
	    code.add(new IR1.Call("malloc", sources, t));
	    return new ValPack(t, code);
    // ... need code ...

  }

  // Ast1.ArrayElm ---
  // Ast1.Exp ar, idx;
  //
  // Template:
  //   newTemp: t1,t2,t3
  //   code: ar.c + idx.c
  //         + "t1 = idx.v * 4"
  //         + "t2 = ar.v + t1"
  //         + "t3 = [t2]"
  //
  static ValPack gen(Ast1.ArrayElm n) throws Exception {
	    ValPack p = genAddr(n);
	    IR1.Temp t = new IR1.Temp();
	    p.code.add(new IR1.Load(t, (IR1.Dest)p.src));
	    return new ValPack(t, p.code);
    // ... need code ...

  }
  
  // Template:  --- skip the load instruction
  //   newTemp: t1,t2
  //   code: ar.c + idx.c
  //         + "t1 = idx.v * 4"
  //         + "t2 = ar.v + t1"
  //
  static ValPack genAddr(Ast1.ArrayElm n) throws Exception {
	    List<IR1.Inst> code = new ArrayList<IR1.Inst>();
	    ValPack ar = gen(n.ar);
	    ValPack idx = gen(n.idx);
	    IR1.Temp t1 = new IR1.Temp();
	    IR1.Temp t2 = new IR1.Temp();
	    code.addAll(ar.code);
	    code.addAll(idx.code);
	    IR1.IntLit intSz = new IR1.IntLit(4);
	    code.add(new IR1.Binop(IR1.ArithOP.MUL, t1, idx.src, intSz));
	    code.add(new IR1.Binop(IR1.ArithOP.ADD, t2, ar.src, t1));
	    return new ValPack(t2, code);
    // ... need code ...

  }

  // Ast1.Id ---
  // String nm;
  //
  static ValPack gen(Ast1.Id n) throws Exception {
    return new ValPack(new IR1.Id(n.nm));
  }

  // Ast1.IntLit ---
  // int i;
  //
  static ValPack gen(Ast1.IntLit n) throws Exception {
    return  new ValPack(new IR1.IntLit(n.i));
  }

  // Ast1.BoolLit ---
  // boolean b;
  //
  static ValPack gen(Ast1.BoolLit n) throws Exception {
    return  new ValPack(n.b ? IR1.TRUE : IR1.FALSE);
  }

  // Ast1.StrLit ---
  // String s;
  //
  static ValPack gen(Ast1.StrLit n) throws Exception {
      return  new ValPack(new IR1.StrLit(n.s));
  }

  // OPERATORS

  static IR1.BOP gen(Ast1.BOP op) throws Exception {
    switch (op) {
    case ADD: return IR1.ArithOP.ADD;
    case SUB: return IR1.ArithOP.SUB;
    case MUL: return IR1.ArithOP.MUL;
    case DIV: return IR1.ArithOP.DIV;
    case AND: return IR1.ArithOP.AND;
    case OR:  return IR1.ArithOP.OR; 
    case EQ:  return IR1.RelOP.EQ; 
    case NE:  return IR1.RelOP.NE; 
    case LT:  return IR1.RelOP.LT; 
    case LE:  return IR1.RelOP.LE; 
    case GT:  return IR1.RelOP.GT; 
    case GE:  return IR1.RelOP.GE; 
    }
    throw new IR1GenException("Unknown BOP: " + op);
  }
   
  static boolean isRelOp(Ast1.BOP op) throws Exception {
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
   
  // Type mapping (AST1 -> IR1)
  static IR1.Type gen(Ast1.Type n) throws Exception {
    if (n == null)                   return null;
    if (n instanceof Ast1.IntType)   return IR1.Type.INT;
    if (n instanceof Ast1.BoolType)  return IR1.Type.BOOL;
    if (n instanceof Ast1.ArrayType) return IR1.Type.PTR;
    throw new IR1GenException("Unknown Ast type: " + n);
  }
}
