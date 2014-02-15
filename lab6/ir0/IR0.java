// This is supporting software for CS321/CS322 Compilers and Language Design.
// Copyright (c) Portland State University
// 
// IR0 definition. (For CS322 Lab6, Jingke Li)
//
// (version 2, with Label node)
//
package ir0;
import java.io.*;
import java.util.*;

public class IR0 {
  public static final BoolLit TRUE = new BoolLit(true);
  public static final BoolLit FALSE = new BoolLit(false);

  // Program -> {Inst}
  //
  public static class Program {
    public final Inst[] insts;

    public Program(Inst[] sa) { insts=sa; }
    public Program(List<Inst> sl) { 
      this(sl.toArray(new Inst[0]));
    }
    public String toString() { 
      String str = "# IR0 Program\n";
      for (Inst s: insts)
	str += s;
      return str;
    }
  }

  // Instructions

  public static abstract class Inst {}

  // Inst -> Dest "=" Src bop Src
  //
  public static class Binop extends Inst {
    public final ArithOP op;
    public final Dest dst;
    public final Src src1, src2;

    public Binop(ArithOP o, Dest d, Src s1, Src s2) { 
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

  // Inst -> "if" Src rop Src "goto" Label
  //
  public static class CJump extends Inst {
    public final RelOP op;
    public final Src src1, src2;
    public final Label lab;

    public CJump(RelOP o, Src s1, Src s2, Label l) { 
      op=o; src1=s1; src2=s2; lab=l; 
    }
    public String toString() { 
      return " if " + src1 + " " + op + " " + src2 + 
	" goto " + lab + "\n";
    }
  }

  // Inst -> "goto" Label
  //
  public static class Jump extends Inst {
    public final Label lab;

    public Jump(Label l) { lab=l; }

    public String toString() { 
      return " goto " + lab + "\n"; 
    }
  }

  // Inst -> Label ":"
  //
  public static class LabelDec extends Inst { 
    public final Label lab;

    public LabelDec(Label l) { lab=l; }

    public String toString() { 
      return lab + ":\n"; 
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

  // Label

  public static class Label {
    static int labelnum=0;
    public String lab;

    public Label() { lab = "L" + labelnum++; }
    public Label(String s) { lab = s; }
    public void set(String s) { lab = s; }
    public String toString() { return lab; }
  }

  // Operands

  public interface Src {}

  public interface Dest {}

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

}
