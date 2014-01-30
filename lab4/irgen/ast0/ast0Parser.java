/* Generated By:JavaCC: Do not edit this line. ast0Parser.java */
package ast0;
import java.util.*;
import java.io.*;

public class ast0Parser implements ast0ParserConstants {
  public static void main(String [] args) throws Exception {
    if (args.length == 1) {
      FileInputStream stream = new FileInputStream(args[0]);
      Ast0.Program p = new ast0Parser(stream).Program();
      stream.close();
      System.out.println(p);
    } else {
      System.out.println("Need one file name as command-line argument.");
    }
  }

// Program -> {Stmt}
//
  static final public Ast0.Program Program() throws ParseException {
  List<Ast0.Stmt> sl = new ArrayList<Ast0.Stmt>();
  Ast0.Stmt s;
    label_1:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case kwAssign:
      case kwIf:
      case kwPrint:
      case kwWhile:
        ;
        break;
      default:
        jj_la1[0] = jj_gen;
        break label_1;
      }
      s = Stmt();
               sl.add(s);
    }
    jj_consume_token(0);
    {if (true) return new Ast0.Program(sl);}
    throw new Error("Missing return statement in function");
  }

// Stmt -> "Assign" Exp Exp
//      |  "If" Exp Stmt [ "Else" Stmt ]  
//      |  "While" Exp Stmt
//      |  "Print" Exp
//
  static final public Ast0.Stmt Stmt() throws ParseException {
  Ast0.Exp lhs, rhs, cond, arg;
  Ast0.Stmt s, s1, s2=null;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case kwAssign:
      jj_consume_token(kwAssign);
      lhs = Exp();
      rhs = Exp();
                                    s = new Ast0.Assign(lhs, rhs);
      break;
    case kwIf:
      jj_consume_token(kwIf);
      cond = Exp();
      s1 = Stmt();
      if (jj_2_1(2)) {
        jj_consume_token(kwElse);
        s2 = Stmt();
      } else {
        ;
      }
                                    s = new Ast0.If(cond, s1, s2);
      break;
    case kwWhile:
      jj_consume_token(kwWhile);
      cond = Exp();
      s = Stmt();
                                    s = new Ast0.While(cond, s);
      break;
    case kwPrint:
      jj_consume_token(kwPrint);
      arg = Exp();
                                    s = new Ast0.Print(arg);
      break;
    default:
      jj_la1[1] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    {if (true) return s;}
    throw new Error("Missing return statement in function");
  }

//  Exp -> "(" "Binop" BOP Exp Exp ")"
//      |  "(" "Unop" UOP Exp ")"
//      |  "(" "NewArray" <IntLit> ")"
//      |  "(" "ArrayElm" Exp Exp ")"
//      | <Id> 
//      | <IntLit>
//      | <BoolLit>
//
  static final public Ast0.Exp Exp() throws ParseException {
  String s;
  int sz, i;
  boolean b;
  Ast0.BOP bop;
  Ast0.UOP uop;
  Ast0.Exp e=null, e1, e2, ar, idx;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case 21:
      jj_consume_token(21);
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case kwBinop:
        jj_consume_token(kwBinop);
        bop = binOp();
        e1 = Exp();
        e2 = Exp();
                                              e = new Ast0.Binop(bop, e1, e2);
        break;
      case kwUnop:
        jj_consume_token(kwUnop);
        uop = unOp();
        e = Exp();
                                              e = new Ast0.Unop(uop, e);
        break;
      case kwNewArray:
        jj_consume_token(kwNewArray);
        sz = IntLit();
                                              e = new Ast0.NewArray(sz);
        break;
      case kwArrayElm:
        jj_consume_token(kwArrayElm);
        ar = Exp();
        idx = Exp();
                                              e = new Ast0.ArrayElm(ar, idx);
        break;
      default:
        jj_la1[2] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
      jj_consume_token(22);
      break;
    case Id:
      s = Id();
                                              e = new Ast0.Id(s);
      break;
    case IntLit:
      i = IntLit();
                                              e = new Ast0.IntLit(i);
      break;
    case BoolLit:
      b = BoolLit();
                                              e = new Ast0.BoolLit(b);
      break;
    default:
      jj_la1[3] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    {if (true) return e;}
    throw new Error("Missing return statement in function");
  }

  static final public Ast0.BOP binOp() throws ParseException {
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case 23:
      jj_consume_token(23);
             {if (true) return Ast0.BOP.ADD;}
      break;
    case 24:
      jj_consume_token(24);
             {if (true) return Ast0.BOP.SUB;}
      break;
    case 25:
      jj_consume_token(25);
             {if (true) return Ast0.BOP.MUL;}
      break;
    case 26:
      jj_consume_token(26);
             {if (true) return Ast0.BOP.DIV;}
      break;
    case 27:
      jj_consume_token(27);
             {if (true) return Ast0.BOP.AND;}
      break;
    case 28:
      jj_consume_token(28);
             {if (true) return Ast0.BOP.OR;}
      break;
    case 29:
      jj_consume_token(29);
             {if (true) return Ast0.BOP.EQ;}
      break;
    case 30:
      jj_consume_token(30);
             {if (true) return Ast0.BOP.NE;}
      break;
    case 31:
      jj_consume_token(31);
             {if (true) return Ast0.BOP.LT;}
      break;
    case 32:
      jj_consume_token(32);
             {if (true) return Ast0.BOP.LE;}
      break;
    case 33:
      jj_consume_token(33);
             {if (true) return Ast0.BOP.GT;}
      break;
    case 34:
      jj_consume_token(34);
             {if (true) return Ast0.BOP.GE;}
      break;
    default:
      jj_la1[4] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    throw new Error("Missing return statement in function");
  }

  static final public Ast0.UOP unOp() throws ParseException {
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case 24:
      jj_consume_token(24);
             {if (true) return Ast0.UOP.NEG;}
      break;
    case 35:
      jj_consume_token(35);
             {if (true) return Ast0.UOP.NOT;}
      break;
    default:
      jj_la1[5] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    throw new Error("Missing return statement in function");
  }

  static final public String Id() throws ParseException {
  Token n;
    n = jj_consume_token(Id);
           {if (true) return n.image;}
    throw new Error("Missing return statement in function");
  }

  static final public int IntLit() throws ParseException {
  Token n;
    n = jj_consume_token(IntLit);
               {if (true) return Integer.parseInt(n.image);}
    throw new Error("Missing return statement in function");
  }

  static final public boolean BoolLit() throws ParseException {
  Token n;
    n = jj_consume_token(BoolLit);
                {if (true) return Boolean.parseBoolean(n.image);}
    throw new Error("Missing return statement in function");
  }

  static private boolean jj_2_1(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_1(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(0, xla); }
  }

  static private boolean jj_3R_4() {
    if (jj_scan_token(kwIf)) return true;
    return false;
  }

  static private boolean jj_3R_3() {
    if (jj_scan_token(kwAssign)) return true;
    return false;
  }

  static private boolean jj_3R_2() {
    Token xsp;
    xsp = jj_scanpos;
    if (jj_3R_3()) {
    jj_scanpos = xsp;
    if (jj_3R_4()) {
    jj_scanpos = xsp;
    if (jj_3R_5()) {
    jj_scanpos = xsp;
    if (jj_3R_6()) return true;
    }
    }
    }
    return false;
  }

  static private boolean jj_3_1() {
    if (jj_scan_token(kwElse)) return true;
    if (jj_3R_2()) return true;
    return false;
  }

  static private boolean jj_3R_6() {
    if (jj_scan_token(kwPrint)) return true;
    return false;
  }

  static private boolean jj_3R_5() {
    if (jj_scan_token(kwWhile)) return true;
    return false;
  }

  static private boolean jj_initialized_once = false;
  /** Generated Token Manager. */
  static public ast0ParserTokenManager token_source;
  static SimpleCharStream jj_input_stream;
  /** Current token. */
  static public Token token;
  /** Next token. */
  static public Token jj_nt;
  static private int jj_ntk;
  static private Token jj_scanpos, jj_lastpos;
  static private int jj_la;
  static private int jj_gen;
  static final private int[] jj_la1 = new int[6];
  static private int[] jj_la1_0;
  static private int[] jj_la1_1;
  static {
      jj_la1_init_0();
      jj_la1_init_1();
   }
   private static void jj_la1_init_0() {
      jj_la1_0 = new int[] {0xa900,0xa900,0x5280,0x390000,0xff800000,0x1000000,};
   }
   private static void jj_la1_init_1() {
      jj_la1_1 = new int[] {0x0,0x0,0x0,0x0,0x7,0x8,};
   }
  static final private JJCalls[] jj_2_rtns = new JJCalls[1];
  static private boolean jj_rescan = false;
  static private int jj_gc = 0;

  /** Constructor with InputStream. */
  public ast0Parser(java.io.InputStream stream) {
     this(stream, null);
  }
  /** Constructor with InputStream and supplied encoding */
  public ast0Parser(java.io.InputStream stream, String encoding) {
    if (jj_initialized_once) {
      System.out.println("ERROR: Second call to constructor of static parser.  ");
      System.out.println("       You must either use ReInit() or set the JavaCC option STATIC to false");
      System.out.println("       during parser generation.");
      throw new Error();
    }
    jj_initialized_once = true;
    try { jj_input_stream = new SimpleCharStream(stream, encoding, 1, 1); } catch(java.io.UnsupportedEncodingException e) { throw new RuntimeException(e); }
    token_source = new ast0ParserTokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 6; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Reinitialise. */
  static public void ReInit(java.io.InputStream stream) {
     ReInit(stream, null);
  }
  /** Reinitialise. */
  static public void ReInit(java.io.InputStream stream, String encoding) {
    try { jj_input_stream.ReInit(stream, encoding, 1, 1); } catch(java.io.UnsupportedEncodingException e) { throw new RuntimeException(e); }
    token_source.ReInit(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 6; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Constructor. */
  public ast0Parser(java.io.Reader stream) {
    if (jj_initialized_once) {
      System.out.println("ERROR: Second call to constructor of static parser. ");
      System.out.println("       You must either use ReInit() or set the JavaCC option STATIC to false");
      System.out.println("       during parser generation.");
      throw new Error();
    }
    jj_initialized_once = true;
    jj_input_stream = new SimpleCharStream(stream, 1, 1);
    token_source = new ast0ParserTokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 6; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Reinitialise. */
  static public void ReInit(java.io.Reader stream) {
    jj_input_stream.ReInit(stream, 1, 1);
    token_source.ReInit(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 6; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Constructor with generated Token Manager. */
  public ast0Parser(ast0ParserTokenManager tm) {
    if (jj_initialized_once) {
      System.out.println("ERROR: Second call to constructor of static parser. ");
      System.out.println("       You must either use ReInit() or set the JavaCC option STATIC to false");
      System.out.println("       during parser generation.");
      throw new Error();
    }
    jj_initialized_once = true;
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 6; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Reinitialise. */
  public void ReInit(ast0ParserTokenManager tm) {
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 6; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  static private Token jj_consume_token(int kind) throws ParseException {
    Token oldToken;
    if ((oldToken = token).next != null) token = token.next;
    else token = token.next = token_source.getNextToken();
    jj_ntk = -1;
    if (token.kind == kind) {
      jj_gen++;
      if (++jj_gc > 100) {
        jj_gc = 0;
        for (int i = 0; i < jj_2_rtns.length; i++) {
          JJCalls c = jj_2_rtns[i];
          while (c != null) {
            if (c.gen < jj_gen) c.first = null;
            c = c.next;
          }
        }
      }
      return token;
    }
    token = oldToken;
    jj_kind = kind;
    throw generateParseException();
  }

  static private final class LookaheadSuccess extends java.lang.Error { }
  static final private LookaheadSuccess jj_ls = new LookaheadSuccess();
  static private boolean jj_scan_token(int kind) {
    if (jj_scanpos == jj_lastpos) {
      jj_la--;
      if (jj_scanpos.next == null) {
        jj_lastpos = jj_scanpos = jj_scanpos.next = token_source.getNextToken();
      } else {
        jj_lastpos = jj_scanpos = jj_scanpos.next;
      }
    } else {
      jj_scanpos = jj_scanpos.next;
    }
    if (jj_rescan) {
      int i = 0; Token tok = token;
      while (tok != null && tok != jj_scanpos) { i++; tok = tok.next; }
      if (tok != null) jj_add_error_token(kind, i);
    }
    if (jj_scanpos.kind != kind) return true;
    if (jj_la == 0 && jj_scanpos == jj_lastpos) throw jj_ls;
    return false;
  }


/** Get the next Token. */
  static final public Token getNextToken() {
    if (token.next != null) token = token.next;
    else token = token.next = token_source.getNextToken();
    jj_ntk = -1;
    jj_gen++;
    return token;
  }

/** Get the specific Token. */
  static final public Token getToken(int index) {
    Token t = token;
    for (int i = 0; i < index; i++) {
      if (t.next != null) t = t.next;
      else t = t.next = token_source.getNextToken();
    }
    return t;
  }

  static private int jj_ntk() {
    if ((jj_nt=token.next) == null)
      return (jj_ntk = (token.next=token_source.getNextToken()).kind);
    else
      return (jj_ntk = jj_nt.kind);
  }

  static private java.util.List<int[]> jj_expentries = new java.util.ArrayList<int[]>();
  static private int[] jj_expentry;
  static private int jj_kind = -1;
  static private int[] jj_lasttokens = new int[100];
  static private int jj_endpos;

  static private void jj_add_error_token(int kind, int pos) {
    if (pos >= 100) return;
    if (pos == jj_endpos + 1) {
      jj_lasttokens[jj_endpos++] = kind;
    } else if (jj_endpos != 0) {
      jj_expentry = new int[jj_endpos];
      for (int i = 0; i < jj_endpos; i++) {
        jj_expentry[i] = jj_lasttokens[i];
      }
      jj_entries_loop: for (java.util.Iterator<?> it = jj_expentries.iterator(); it.hasNext();) {
        int[] oldentry = (int[])(it.next());
        if (oldentry.length == jj_expentry.length) {
          for (int i = 0; i < jj_expentry.length; i++) {
            if (oldentry[i] != jj_expentry[i]) {
              continue jj_entries_loop;
            }
          }
          jj_expentries.add(jj_expentry);
          break jj_entries_loop;
        }
      }
      if (pos != 0) jj_lasttokens[(jj_endpos = pos) - 1] = kind;
    }
  }

  /** Generate ParseException. */
  static public ParseException generateParseException() {
    jj_expentries.clear();
    boolean[] la1tokens = new boolean[36];
    if (jj_kind >= 0) {
      la1tokens[jj_kind] = true;
      jj_kind = -1;
    }
    for (int i = 0; i < 6; i++) {
      if (jj_la1[i] == jj_gen) {
        for (int j = 0; j < 32; j++) {
          if ((jj_la1_0[i] & (1<<j)) != 0) {
            la1tokens[j] = true;
          }
          if ((jj_la1_1[i] & (1<<j)) != 0) {
            la1tokens[32+j] = true;
          }
        }
      }
    }
    for (int i = 0; i < 36; i++) {
      if (la1tokens[i]) {
        jj_expentry = new int[1];
        jj_expentry[0] = i;
        jj_expentries.add(jj_expentry);
      }
    }
    jj_endpos = 0;
    jj_rescan_token();
    jj_add_error_token(0, 0);
    int[][] exptokseq = new int[jj_expentries.size()][];
    for (int i = 0; i < jj_expentries.size(); i++) {
      exptokseq[i] = jj_expentries.get(i);
    }
    return new ParseException(token, exptokseq, tokenImage);
  }

  /** Enable tracing. */
  static final public void enable_tracing() {
  }

  /** Disable tracing. */
  static final public void disable_tracing() {
  }

  static private void jj_rescan_token() {
    jj_rescan = true;
    for (int i = 0; i < 1; i++) {
    try {
      JJCalls p = jj_2_rtns[i];
      do {
        if (p.gen > jj_gen) {
          jj_la = p.arg; jj_lastpos = jj_scanpos = p.first;
          switch (i) {
            case 0: jj_3_1(); break;
          }
        }
        p = p.next;
      } while (p != null);
      } catch(LookaheadSuccess ls) { }
    }
    jj_rescan = false;
  }

  static private void jj_save(int index, int xla) {
    JJCalls p = jj_2_rtns[index];
    while (p.gen > jj_gen) {
      if (p.next == null) { p = p.next = new JJCalls(); break; }
      p = p.next;
    }
    p.gen = jj_gen + xla - jj_la; p.first = token; p.arg = xla;
  }

  static final class JJCalls {
    int gen;
    Token first;
    int arg;
    JJCalls next;
  }

}
