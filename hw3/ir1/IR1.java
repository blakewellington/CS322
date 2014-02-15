// This is supporting software for CS321/CS322 Compilers and Language Design.
// Copyright (c) Portland State University
// 
// IR1 definition. (For CS322 Lab4, Jingke Li)
//
//
package ir1;
import java.io.*;
import java.util.*;

public class IR1 {
  public static final BoolLit TRUE = new BoolLit(true);
  public static final BoolLit FALSE = new BoolLit(false);

  // Type -> ":B" | ":I" | ":P"
  //
  public static enum Type {
    BOOL(":B",1), INT(":I",4), PTR(":P",8);
    final String name;
    final int size;
    Type(String s, int i) { name=s; size=i; }
    public String toString() { return name; }
  }

  // Program -> {Func}
  //
  public static class Program {
    public final Func[] funcs;

    public Program(Func[] f) { funcs=f; }
    public Program(List<Func> fl) { this(fl.toArray(new Func[0])); }

    public String toString() { 
      String str = "# IR1 Program\n";
      for (Func f: funcs)
	str += "\n" + f;
      return str;
    }
  }

  // Func -> <global> "(" [Var {"," Var}] ")" [Type] {Inst}
  //
  public static class Func {
    public final String name;
    public final Var[] params;
    public final Var[] locals;
    public final Inst[] code;
    public final Type rtype;  // could be null

    public Func(String n, Var[] p, Var[] l, Inst[] c, Type t) {
      name=n; params=p; locals=l; code=c; rtype=t; 
    }
    public Func(String n, List<Var> pl, List<Var> ll, List<Inst> cl, Type t) {
      this(n, pl.toArray(new Var[0]), ll.toArray(new Var[0]),
	   cl.toArray(new Inst[0]), t); 
    }
    public String toString() { 
      String header = "_" + name + " " + VarArrayToString(params) +
			(rtype==null ? "" : rtype + "") + "\n" +
  	                (locals.length==0? "" : VarArrayToString(locals) + "\n");
      String body = "";
      for (Inst s: code)
	body += s.toString();
      return header + "{\n" + body + "}\n";
    }
  }

  static String VarArrayToString(Var[] vars) {
    String s = "(";
    if (vars.length > 0) {
      s += vars[0];
      for (int i=1; i<vars.length; i++)
	s += ", " + vars[i];
    }
    return s + ")";
  }

  // Var -> <Id> Type

  public static class Var {
    public final String name;
    public final Type t;
    public Var(String name, Type t) { this.name=name; this.t=t; }
    public String toString() { return name + t; }
  }

  // Instructions

  public static abstract class Inst {}

  // Inst -> Dest "=" Src bop Src
  //
  public static class Binop extends Inst {
    public final BOP op;
    public final Dest dst;
    public final Src src1, src2;

    public Binop(BOP o, Dest d, Src s1, Src s2) { 
      op=o; dst=d; src1=s1; src2=s2; 
    }
    public String toString() { 
      return " " + dst + " = " + src1 + " " + op + " " + src2 + "\n";
    }
  }

  // Inst -> Dest "=" uop Src
  //
  public static class Unop extends Inst {
    public final UOP op;
    public final Dest dst;
    public final Src src;

    public Unop(UOP o, Dest d, Src s) { op=o; dst=d; src=s; }

    public String toString() { 
      return " " + dst + " = " + op + src + "\n";
    }
  }

  // Inst -> Dest "=" Src
  //
  public static class Move extends Inst {
    public final Dest dst;
    public final Src src;

    public Move(Dest d, Src s) { dst=d; src=s; }

    public String toString() { 
      return " " + dst + " = " + src + "\n"; 
    }
  }

  // Inst -> Dest "=" "[" Dest "]"
  //
  public static class Load extends Inst {
    public final Dest dst;
    public final Dest addr;

    public Load (Dest d, Dest a) { dst=d; addr=a; }

    public String toString() { 
      return " " + dst + " = [" + addr + "]\n"; 
    }
  }
    
  // Inst -> "[" Dest "]" "=" Src
  //
  public static class Store extends Inst {
    public final Dest addr;
    public final Src src;

    public Store(Dest a, Src s) { addr=a; src=s; }

    public String toString() { 
      return " [" + addr + "] = " + src + "\n"; 
    }
  }

  // Inst -> Dest "=" "malloc" "(" Src ")"
  //
  public static class Malloc extends Inst {
    public final Dest rdst;
    public final Src arg;

    public Malloc(Dest r, Src a) { rdst=r; arg=a; }
 
    public String toString() { 
      return " " + rdst +  " = malloc (" + arg + ")\n";
    }
  }

  // Inst -> Dest "=" "print" "(" Src ")"
  //
  public static class Print extends Inst {
    public final Src arg;

    public Print(Src a) { arg=a; }

    public String toString() { 
      return " print (" + arg + ")\n";
    }
  }

  // Inst -> [Dest "="] "call" <Global> "(" {Src} ")"
  //
  public static class Call extends Inst {
    public final String name;
    public final Src[] args;
    public final Dest rdst;    // could be null

    public Call(String n, Src[] a, Dest r) { 
      name=n; args=a; rdst=r;
    }
    public Call(String n, List<Src> al, Dest r) { 
      this(n, al.toArray(new Src[0]), r);
    }
    public Call(String n, List<Src> al) { 
      this(n, al.toArray(new Src[0]), null);
    }
    public String toString() { 
      String arglist = "(";
      if (args.length > 0) {
	arglist += args[0];
	for (int i=1; i<args.length; i++)
	  arglist += ", " + args[i];
      }
      arglist +=  ")";
      String retstr = (rdst==null) ? " " : " " + rdst + " = ";
      return retstr +  "call _" + name + arglist + "\n";
    }
  }

  // Inst -> "return" [Src]
  //
  public static class Return extends Inst {
    public final Src val;	// could be null

    public Return() { val=null; }
    public Return(Src s) { val=s; }

    public String toString() { 
      return " return " + (val==null ? "" : val) + "\n"; 
    }
  }

  // Inst -> "if" Src rop Src "goto" <label>
  //
  public static class CJump extends Inst {
    public final RelOP op;
    public final Src src1, src2;
    public final String label;

    public CJump(RelOP o, Src s1, Src s2, String l) { 
      op=o; src1=s1; src2=s2; label=l; 
    }
    public String toString() { 
      return " if " + src1 + " " + op + " " + src2 + 
	" goto " + label + "\n";
    }
  }

  // Inst -> "goto" <label>
  //
  public static class Jump extends Inst {
    public final String label;

    public Jump(String s) { label=s; }

    public String toString() { 
      return " goto " + label + "\n"; 
    }
  }

  // Inst -> <label> ":"
  //
  public static class LabelDec extends Inst { 
    public final String label;

    public LabelDec(String s) { label=s; }

    public String toString() { 
      return label + ":\n"; 
    }
  }

  // Operators

  public static interface BOP {}

  public static enum ArithOP implements BOP {
    ADD("+"), SUB("-"), MUL("*"), DIV("/"), AND("&&"), OR("||");
    final String name;

    ArithOP(String n) { name = n; }
    public String toString() { return name; }
  }

  public static enum RelOP implements BOP {
    EQ("=="), NE("!="), LT("<"), LE("<="), GT(">"), GE(">=");
    final String name;

    RelOP(String n) { name = n; }
    public String toString() { return name; }
  }

  public static enum UOP {
    NEG("-"), NOT("!");
    final String name;

    UOP(String n) { name = n; }
    public String toString() { return name; }
  }

  public static boolean isCompareOp(BOP op) {
    return (op == RelOP.EQ) || (op == RelOP.NE) ||
           (op == RelOP.LT) || (op == RelOP.LE) ||
           (op == RelOP.GT) || (op == RelOP.GE);
  }

  // Label

  static int labelnum=0;
  public static String newLabel() {
    return "L" + labelnum++;
  }

  // Operands

  public interface Src {}

  public interface Dest {}

  public static class Global {
    public final String name;

    public Global(String s) { name = s; }
    public String toString() { return "_" + name; }
  }

  public static class Id implements Src, Dest  {
    public final String name;

    public Id(String s) { name=s; }
    public String toString() { return name; }
  }

  public static class Temp implements Src, Dest  {
    private static int cnt=0;
    public final int num;

    public Temp() { num = ++Temp.cnt; }
    public Temp(int n) { num=n; }
    public String toString() { return "t" + num; }
  }

  public static class IntLit implements Src {
    public final int i;

    public IntLit(int v) { i=v; }
    public String toString() { return i + ""; }
  }

  public static class BoolLit implements Src {
    public final boolean b;

    public BoolLit(boolean v) { b=v; }
    public String toString() { return b + ""; }
  }

  public static class StrLit implements Src {
    public final String s;

    public StrLit(String v) { s=v; }
    public String toString() { return "\"" + s + "\""; }
  }
}
