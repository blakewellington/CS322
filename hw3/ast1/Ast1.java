// This is supporting software for CS321/CS322 Compilers and Language Design.
// Copyright (c) Portland State University
// 
// AST1 Definition. (For CS322 HW3, Jingke Li)
//
//
package ast1;
import java.util.*;

public class Ast1 {
  static int tab=0;	// indentation for printing AST

  public abstract static class Node {
    String tab() {
      String str = "";
      for (int i = 0; i < Ast1.tab; i++)
	str += " ";
      return str;
    }
  }

  // Program Node -------------------------------------------------------

  // Program -> {Func}
  //
  public static class Program extends Node {
    public final Func[] funcs;

    public Program(Func[] fa) { funcs=fa; }
    public Program(List<Func> fl) { 
      this(fl.toArray(new Func[0]));
    }
    public String toString() { 
      String str = "# AST1 Program\n";
      for (Func f: funcs) 
	str += f;
      return str;
    }
  }   

  // Func -> "Func" ("void"|Type) <Id> "(" {Formal} ")" {VarDecl} {Stmt}
  //
  public static class Func extends Node {
    public final Type t;	   // return type (could be null)
    public final String nm;	   // func name
    public final Formal[] params;  // formal parameters
    public final VarDecl[] vars;   // local variables
    public final Stmt[] stmts;	   // func body

    public Func(Type rt, String n, Formal[] fa, VarDecl[] va, Stmt[] sa) {
      t=rt; nm=n; params=fa; vars=va; stmts=sa;
    }
    public Func(Type rt, String nm, List<Formal> fl, List<VarDecl> vl, List<Stmt> sl) {
      this(rt, nm, fl.toArray(new Formal[0]), 
	   vl.toArray(new VarDecl[0]), sl.toArray(new Stmt[0]));
    }
    public String toString() { 
      String str = "  Func " + (t==null ? "void" : t) + " " + nm + " ("; 
      for (Formal f: params) 
	str += f + " ";
      str += ")\n";
      Ast1.tab = 3; 
      for (VarDecl v: vars) 
	str += v;
      for (Stmt s: stmts) 
	str += s;
      return str;
    }
  }

  // VarDecl -> "VarDecl" Type <Id> [Exp]
  //
  public static class VarDecl extends Node {
    public final Type t;     // variable type
    public final String nm;  // variable name
    public final Exp init;   // init expr (could be null)

    public VarDecl(Type at, String v, Exp e) { t=at; nm=v; init=e; }

    public String toString() { 
      return tab() + "VarDecl " + t + " " + nm + " " + 
	(init==null ? "()" : init) + "\n"; 
    }
  }

  // Formal -> "(" "Formal" Type <Id> ")"
  //
  public static class Formal extends Node {
    public final Type t;     // parameter type
    public final String nm;  // parameter name

    public Formal(Type at, String v) { t=at; nm=v; }

    public String toString() { 
      return "(Formal " + t + " " + nm + ")"; 
    }
  }

  // Types --------------------------------------------------------------

  // Type -> "IntType"
  //      |  "BoolType"
  //      |  "(" "ArrayType" Type ")"
  //
  public static abstract class Type extends Node {}

  public static class IntType extends Type {
    public String toString() { return "IntType"; }
  }

  public static class BoolType extends Type {
    public String toString() { return "BoolType"; }
  }

  public static class ArrayType extends Type {
    public final Type et;  // array element type

    public ArrayType(Type t) { et=t; }

    public String toString() { 
      return "(ArrayType " + et + ")"; 
    }
  }

  // Statements ---------------------------------------------------------

  public static abstract class Stmt extends Node {}

  // Stmt -> "{" {Stmt} "}"
  //
  public static class Block extends Stmt {
    public final Stmt[] stmts;

    public Block(Stmt[] sa) { stmts=sa; }
    public Block(List<Stmt> sl) { 
      this(sl.toArray(new Stmt[0])); 
    }
    public String toString() { 
      String s = "";
      if (stmts!=null) {
	s = tab() + "{\n";
        Ast1.tab++; 
	for (Stmt st: stmts) 
	  s += tab() + st;
	Ast1.tab--;
	s += tab() + "}\n"; 
      }
      return s;
    }
  }

  // Stmt -> "Assign" Exp Exp
  //
  public static class Assign extends Stmt {
    public final Exp lhs;
    public final Exp rhs;

    public Assign(Exp e1, Exp e2) { lhs=e1; rhs=e2; }

    public String toString() { 
      return tab() + "Assign " + lhs + " " + rhs + "\n"; 
    }
  }

  // Stmt -> "CallStmt" <Id> "(" {Exp} ")"
  //
  public static class CallStmt extends Stmt {
    public final String nm;   // name
    public final Exp[] args;  // arguments

    public CallStmt(String s, Exp[] ea) { nm=s; args=ea; }
    public CallStmt(String s, List<Exp> el) { 
      this(s, el.toArray(new Exp[0])); 
    }
    public String toString() { 
      String s = tab() + "CallStmt " + nm + " ("; 
      for (Exp e: args) 
	s += e + " "; 
      s += ")\n"; 
      return s;
    }
  }

  // Stmt -> "If" Exp Stmt ["Else" Stmt]  
  //
  public static class If extends Stmt {
    public final Exp cond;
    public final Stmt s1;   // then-clause
    public final Stmt s2;   // else-clause (could be null)

    public If(Exp e, Stmt as1, Stmt as2) { cond=e; s1=as1; s2=as2; }

    public String toString() { 
      String str = tab() + "If " + cond + "\n"; 
      Ast1.tab++; 
      str += tab() + s1; 
      Ast1.tab--;
      if (s2 != null) {
	str += tab() + "Else\n";
	Ast1.tab++; 
	str += tab() + s2; 
	Ast1.tab--;
      }
      return str;
    }
  }

  // Stmt -> "While" Exp Stmt 
  //
  public static class While extends Stmt {
    public final Exp cond;
    public final Stmt s;

    public While(Exp e, Stmt as) { cond=e; s=as; }

    public String toString() { 
      String str = tab() + "While " + cond + "\n";
      Ast1.tab++; 
      str += tab() + s; 
      Ast1.tab--;
      return str;
    }
  }   

  // Stmt -> "Print" Exp
  //
  public static class Print extends Stmt {
    public final Exp arg;

    public Print(Exp e) { arg=e; }

    public String toString() { 
      return tab() + "Print " + arg + "\n"; 
    }
  }

  // Stmt -> "Return" [Exp]
  //
  public static class Return extends Stmt {
    public final Exp val;  // return-value expr (could be null)

    public Return(Exp e) { val=e; }

    public String toString() { 
      return tab() + "Return " + (val==null ? "()" : val) + "\n"; 
    }
  }

  // Expressions --------------------------------------------------------

  public static abstract class Exp extends Node {}

  public static enum BOP {
    ADD("+"), SUB("-"), MUL("*"), DIV("/"), AND("&&"), OR("||"),
    EQ("=="), NE("!="), LT("<"), LE("<="), GT(">"), GE(">=");
    private String name;

    BOP(String n) { name = n; }
    public String toString() { return name; }
  }

  public static enum UOP {
    NEG("-"), NOT("!");
    private String name;

    UOP(String n) { name = n; }
    public String toString() { return name; }
  }


  // Exp -> "(" "Binop" BOP Exp Exp ")"
  //
  public static class Binop extends Exp {
    public final BOP op;
    public final Exp e1;
    public final Exp e2;

    public Binop(BOP o, Exp ae1, Exp ae2) { op=o; e1=ae1; e2=ae2; }

    public String toString() { 
      return "(Binop " + op + " " + e1 + " " + e2 + ")";
    }
  }

  // Exp -> "(" "Unop" UOP Exp ")"
  //
  public static class Unop extends Exp {
    public final UOP op;
    public final Exp e;

    public Unop(UOP o, Exp ae) { op=o; e=ae; }

    public String toString() { 
      return "(Unop " + op + " " + e + ")";
    }
  }

  // Exp -> "(" "Call" <Id> "(" {Exp} "}" ")"
  //
  public static class Call extends Exp {
    public final String nm;   // name
    public final Exp[] args;  // arguments

    public Call(String s, Exp[] ea) { nm=s; args=ea; }
    public Call(String s, List<Exp> el) { 
      this(s, el.toArray(new Exp[0])); 
    }
    public String toString() { 
      String str ="(Call " + nm + " ("; 
      for (Exp e: args) 
	str += e + " "; 
      str += "))"; 
      return str; 
    }
  }

  // Exp -> "(" "NewArray" Type <IntLit> ")"
  //
  public static class NewArray extends Exp {
    public Type et;	   // array element type
    public final int sz;   // array size

    public NewArray(Type t, int i) { et=t; sz=i; }

    public String toString() { 
      return "(NewArray " + et + " " + sz + ")";
    }
  }

  // Exp -> "(" "ArrayElm" Exp Exp ")"
  //
  public static class ArrayElm extends Exp {
    public final Exp ar;   // array object
    public final Exp idx;  // element's index

    public ArrayElm(Exp e1, Exp e2) { ar=e1; idx=e2; }

    public String toString() { 
      return "(ArrayElm " + ar + " " + idx +")";
    }
  }

  // Exp -> <Id>
  //
  public static class Id extends Exp {
    public final String nm;

    public Id(String s) { nm=s; }
    public String toString() { return nm; }
  }

  // Exp -> <IntLit>
  //
  public static class IntLit extends Exp {
    public final int i;	

    public IntLit(int ai) { i=ai; }
    public String toString() { return i + ""; }
  }

  // Exp -> <BoolLit>
  //
  public static class BoolLit extends Exp {
    public final boolean b;	

    public BoolLit(boolean ab) { b=ab; }
    public String toString() { return b + ""; }
  }

  // Exp -> <StrLit>
  //
  public static class StrLit extends Exp {
    public final String s;

    public StrLit(String as) { s=as; }
    public String toString() { return "\"" + s + "\""; }
  }

}
