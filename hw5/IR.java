// This is supporting software for CS322 Compilers and Language Design II
// Copyright (c) Portland State University
// 
// Three-address IR definitions. 
// (Some nodes are not strictly 3-address.)
//
// Based on Version 5. (Last modified on JL on 2/15/14.)   
// 
// Extended with support for X86 Code generation
//
import java.util.*;

class IR {

  // X86 Code Generation Support

  // universal globals
  static final X86.Reg tempReg1 = X86.R10; 
  static final X86.Reg tempReg2 = X86.R11;

  // per-program globals
  static int funcNumber; // current function number, used to construct unique labels
  static List<String> stringLiterals; // accumulated string literals, indexed by position

  // per-function globals
  static IR.RegSet[] liveRanges; // operand liveness data
  static Map<Reg,X86.Reg> env; // location mapping 
  static int frameSize; // in bytes
  static int irPtr; // pointer into IR list

  public static final BoolLit TRUE = new BoolLit(true);
  public static final BoolLit FALSE = new BoolLit(false);

  public static boolean indexed = false;
  public static int linenum = 0;
  static String line(boolean count, String s) {
    String idx = (indexed && count) ? (linenum++) + ". " 
                   + (linenum<11 ? " " : "") : "";
    return idx + s;
  }

  // Types

  public static enum Type {
    BOOL(":B",1,X86.Size.B), INT(":I",4,X86.Size.L), PTR(":P",8,X86.Size.Q);
    final String name;
    final int size;
    final X86.Size X86_size;
    Type(String s, int i, X86.Size xs) { name=s; size=i; X86_size = xs;}
    public String toString() { return name; }
  }

  public static Type type(Const item) {
    if (item instanceof BoolLit) 
      return Type.BOOL;
    if (item instanceof IntLit) 
      return Type.INT;
    return Type.PTR;
  }

  // Program
  
  public static class Program {
    public final Data[] data;
    public final Func[] funcs;

    public Program(Data[] d, Func[] f) { data=d; funcs=f; }
    public Program(List<Data> dl, List<Func> fl) { 
      this(dl.toArray(new Data[0]), fl.toArray(new Func[0]));
    }
    public String toIndexedString() { 
      indexed = true;
      return toString();
    }
    public String toString() { 
      String str = "# IR Program\n";
      if (data != null && data.length > 0)
	str += "\n";
      for (Data d: data)
	str += d;
      for (Func f: funcs)
	str += "\n" + f;
      return str;
    }

    void gen() {
      funcNumber = 0;
      stringLiterals = new ArrayList<String>();
      X86.emit0(".text");
      for (Data d : data) 
	d.gen();
      for (Func f : funcs) {
	f.gen();
	funcNumber++;
      }
      // emit any accumulated string literals
      int i = 0;
      for (String s : stringLiterals) {
	X86.GLabel lab = new X86.GLabel("_S" + i);
	X86.emitGLabel(lab);
	X86.emitString(s);
	i++;
      }      
    }

  }

  // Global data records

  public static class Data {
    public final Global name;
    public final int size;
    public final Const[] items;
    
    public Data(Global n, int i, Const[] l) {
      name = n; size = i; items = l;
    }
    public Data(Global n, int i, List<Const> ll) {
      this(n, i, ll.toArray(new Const[0]));
    }
    public String toString() { 
      String str = "data " + name + " (sz=" + size + "): ";
      if (items.length > 0) {
	str += items[0].toString();
	for (int i=1; i<items.length; i++)
	  str += ", " + items[i];
      }
      return str + "\n";
    }

    void gen() {
      X86.GLabel l = new X86.GLabel(name.toString());
      X86.emit1(".globl", l);
      X86.emitGLabel(l);
      for (Const c : items) 
	c.gen_const();
    }
  }

  // Functions
  public static class Func {
    public final String name;
    public final String[] params;
    public final String[] locals;
    public final Inst[] code;

    public Func(String n, String[] p, String[] l, Inst[] c) {
      name=n; params=p; locals=l; code=c; 
    }
    public Func(String n, List<String> pl, List<String> ll, List<Inst> cl) {
      this(n, pl.toArray(new String[0]), ll.toArray(new String[0]),
	   cl.toArray(new Inst[0])); 
    }
    public String name_params_string() {
      return 
	line(false, "_" + name + " " + StringArrayToString(params)
	     + "\n");
    }
    public String locals_string() {
      return
	(locals.length==0? "" : 
	 line(false, StringArrayToString(locals) + "\n"));
    }
    public String toString() { 
      String body = "";
      linenum = 0;
      for (Inst s: code)
	body += s.toString();
      return name_params_string() + locals_string() + line(false,"{\n") + body + line(false,"}\n");
    }

    void gen() {
      assert (code[0] instanceof LabelDec);
      assert (code[code.length-1] instanceof LabelDec);

      System.out.print("    # " + name_params_string());
      if (locals_string().length() > 0)
	System.out.print("    # " + locals_string());

      linenum = 0;

      // Calculate liveness information for Temp's and Id's 
      liveRanges = Liveness.calculateLiveRanges(this);
      // Assign a location (register or frame) to every Reg that has a live range
      env = Assignment.assignRegisters(this,liveRanges);
	  
      // emit the function header
      X86.emit0(".p2align 4,0x90");
      X86.GLabel f = new X86.GLabel("_" + name);
      X86.emit1(".globl",f);
      X86.emitGLabel(f);
	  
      // save any callee-save registers on the stack now
      int calleeSaveSize = 0;
      for (int i = 0; i < X86.calleeSaveRegs.length; i++) {
	X86.Reg r = X86.calleeSaveRegs[i]; 
	if (env.containsValue(r)) {
	  X86.emit1("pushq",r);
	  calleeSaveSize += X86.Size.Q.bytes;
	}
      }
	  
      // make space for the local frame
      // at entry stack pointer is of the form n16+8
      // need to change it to m16, so that ret addr push brings it back to m16+8 
      frameSize = 0;
      if ((calleeSaveSize % (2*X86.Size.Q.bytes)) == 0) 
	frameSize += X86.Size.Q.bytes;
      if (frameSize != 0) 
	X86.emit2("subq",new X86.Imm(frameSize),X86.RSP);

      // Move the incoming actual arguments to their assigned locations
      // Simply fail if function has more than 6 args
      int paramCount = params.length;
      assert (paramCount <= X86.argRegs.length);
      // Do parallel move 
      X86.Reg[] src = new X86.Reg[paramCount];
      X86.Reg[] dst = new X86.Reg[paramCount];
      int n = 0;
      for (int i = 0; i < paramCount; i++)  {
	X86.Reg d = env.get(new Id(params[i]));
	if (d != null) {
	  src[n] = X86.argRegs[i];
	  dst[n] = d;
	  n++;
	}
      }
      new X86.ParallelMover(n,src,dst,tempReg1).move();
	  
      // emit code for the body (note that irPtr is global)
      for (irPtr = 0; irPtr < code.length; irPtr++) {
	System.out.print("    # " + code[irPtr]);
	code[irPtr].gen();
      }
    }

    // Return set of successors for each instruction in a function
    IndexList[] successors() {
      SortedMap<String,Integer> labelMap = new TreeMap<String,Integer>(); 
      for (int i = 0; i < code.length; i++) {
	Inst c = code[i];
	if (c instanceof LabelDec)
	  labelMap.put(((LabelDec) c).name, i);
      }
      IndexList[] allSuccs = new IndexList[code.length]; 
      for (int i = 0; i < code.length-1; i++) { // there's always a label at the end
	Inst inst = code[i];
	IndexList succs = new IndexList();
	if (inst instanceof CJump) {
	  succs.add(labelMap.get(((CJump) inst).lab.name));
	  succs.add(i+1);      // safe because there's always a label at the end
	} else if (inst instanceof Jump) 
	  succs.add(labelMap.get(((Jump) inst).lab.name));
	else
	  succs.add(i+1);      
	allSuccs[i] = succs;
      }
      allSuccs[code.length-1] = new IndexList(); // label at the end has no successors
      return allSuccs;
    }

    /** Return sets of operands used by each Inst **/
    RegSet[] used() {
      RegSet[] used = new RegSet[code.length];
      for (int i = 0; i < code.length; i++)  
	used[i] = code[i].used();
      return used;
    }

    /** Return sets of operands defined by each Inst **/
    RegSet[] defined() {
      RegSet[] defined = new RegSet[code.length];
      for (int i = 0; i < code.length; i++)
	defined[i] = code[i].defined();
      // Parameters are implicitly defined at top of function
      for (String var : params)
	defined[0].add(new Id(var));
      return defined;
    }
  }


  public static String StringArrayToString(String[] vars) {
    String s = "(";
    if (vars.length > 0) {
      s += vars[0];
      for (int i=1; i<vars.length; i++)
	s += ", " + vars[i];
    }
    return s + ")";
  }

  // Instructions

  public static abstract class Inst {
    abstract void gen();
    abstract RegSet used();
    abstract RegSet defined();
  }

  public static class Binop extends Inst {
    public final BOP op;
    public final Dest dst;
    public final Src src1, src2;

    public Binop(BOP o, Dest d, Src s1, Src s2) { 
      op=o; dst=d; src1=s1; src2=s2; 
    }
    public String toString() { 
      return line(true, " " + dst + " = " + src1 + " " + op + " " + src2 + "\n");
    }

    void gen() {
      if (op instanceof ArithOP) {
	switch ((ArithOP) op) {
	case ADD: 
	case SUB: 
	case MUL: 
	case AND: 
	case OR:  
	  {
	    X86.Reg mdest = dst.gen_dest_operand();  
	    if (mdest == null) // dead assignment
	      break;
	    X86.Operand mright = src2.gen_source_operand(true,tempReg1); 
	    X86.Operand mleft = src1.gen_source_operand(true,tempReg2);
 	    // We are about to overwrite the destination register with
 	    // the left operand. So if the right operand is also the destination
 	    // register, it would be trashed before it could be used.
	    // Move right operand out of the way if necessary.
	    if (mright.equals(mdest)) {
	      X86.emitMov(X86.Size.Q,mright,tempReg1);
	      mright = tempReg1;
	    }
	    X86.emitMov(X86.Size.Q,mleft,mdest);
	    X86.emit2(op.X86_name() + X86.Size.Q,mright,mdest);
	    break;
	  }
	case DIV:  // (also case MOD:)
	  {
	    // We guaranteed that no caller-save register (including RAX, RDX) is 
	    // allocated across a DIV. (We also preferenced the left operand 
	    // and result to RAX.)  But it is still possible that the right
	    // operand is in RAX or RDX.
	    X86.Reg mdest = dst.gen_dest_operand();
	    if (mdest == null) // dead assignment 
	      break;
	    X86.Operand mright = src2.gen_source_operand(false,tempReg1);
	    if (mright.equals(X86.RAX) || mright.equals(X86.RDX)) {
	      X86.emitMov(X86.Size.Q,mright,tempReg1);
	      mright = tempReg1;
	    }
	    X86.Operand mleft = src1.gen_source_operand(true,X86.RAX);
	    X86.emitMov(X86.Size.Q,mleft,X86.RAX);
	    X86.emit0("cqto"); // sign-extend into RDX
	    X86.emit1("idivq",mright);
	    X86.emitMov(X86.Size.Q, X86.RAX, mdest);
	    break;
	  }
	}
      } else {
    	  // Relational Operations:
    	  // All of these behave the same, so no switch...case statement is required
    	  // An example of IR code coming in:
    	  //    T3 = T1 < T2
    	  // However, sometimes we may overwrite the destination:
    	  //    T3 = T1 >= T3
    	  // In that case, we need to move T3 to a temporary location first.
    		switch ((RelOP) op) {
    		case EQ: 
    		case NE: 
    		case LT: 
    		case LE: 
    		case GT:      	  
    		case GE:      	  
    			X86.Reg mdest = dst.gen_dest_operand();  
    			if (mdest == null) // dead assignment
    				break;
    			X86.Operand mright = src2.gen_source_operand(false,tempReg1); 
    			X86.Operand mleft = src1.gen_source_operand(false,tempReg2);
		  	    // Instructions to compare and store the result
		  	    X86.emit2("cmp" + X86.Size.Q, mright, mleft);
			    X86.emit1("set" + op.X86_name(), X86.resize_reg(X86.Size.B,mdest));
		  	    break;
    		}
	}
    }
    RegSet used() {
      RegSet s = new RegSet();
      s.add_source(src1);
      s.add_source(src2);
      return s;
    }
    RegSet defined() {
      RegSet s = new RegSet();
      s.add_dest(dst);
      return s;
    }
  }

  public static class Unop extends Inst {
    public final UOP op;
    public final Dest dst;
    public final Src src;

    public Unop(UOP o, Dest d, Src s) { op=o; dst=d; src=s; }
    public String toString() { 
      return line(true, " " + dst + " = " + op + src + "\n");
    }

    void gen() {
      X86.Reg mdest = dst.gen_dest_operand();
      if (mdest == null) // dead assignment
	return;
      X86.Operand msrc = src.gen_source_operand(true,tempReg1);
      X86.emitMov(X86.Size.Q,msrc,mdest);
      X86.emit1(op.X86_name() + X86.Size.Q,mdest);
    }
    RegSet used() {
      RegSet s = new RegSet();
      s.add_source(src);
      return s;
    }
    RegSet defined() {
      RegSet s = new RegSet();
      s.add_dest(dst);
      return s;
    }
  }

  public static class Move extends Inst {
    public final Dest dst;
    public final Src src;

    public Move(Dest d, Src s) { dst=d; src=s; }
    public String toString() { 
      return line(true, " " + dst + " = " + src + "\n"); 
    }

    void gen() {
      X86.Reg mdest = dst.gen_dest_operand();
      if (mdest == null)  // dead assignment
	return; 
      X86.Operand msrc = src.gen_source_operand(true,mdest);
      X86.emitMov(X86.Size.Q,msrc,mdest);  // often a no-op
    }
    RegSet used() {
      RegSet s = new RegSet();
      s.add_source(src);
      return s;
    }
    RegSet defined() {
      RegSet s = new RegSet();
      s.add_dest(dst);
      return s;
    }
  }

  public static class Load extends Inst {
    public final Type type;
    public final Dest dst;
    public final Addr addr;

    public Load (Type t, Dest d, Addr a) { type=t; dst=d; addr=a; }
    public String toString() { 
      return line(true, " " + dst + " = " + addr + type + "\n"); 
    }

    void gen() {
      X86.Reg mdest = dst.gen_dest_operand();
      if (mdest == null)  // dead assignment
	return;
      X86.Operand a = addr.gen_addr_operand(tempReg1);
      if (type == Type.BOOL) 
	X86.emit2("movzbq",a,mdest);
      else if (type == Type.INT)
	X86.emit2("movslq",a,mdest);
      else // (type == Type.PTR)
	X86.emit2("movq",a,mdest);
    }
    RegSet used() {
      RegSet s = new RegSet();
      s.add_source(addr.base);
      return s;
    }
    RegSet defined() {
      RegSet s = new RegSet();
      s.add_dest(dst);
      return s;
    }
  }
    
  public static class Store extends Inst {
    public final Type type;
    public final Addr addr;
    public final Src src;

    public Store(Type t, Addr a, Src s) { type=t; addr=a; src=s; }
    public String toString() { 
      return line(true, " " + addr + type + " = " + src + "\n"); 
    }

    void gen() {
      X86.Operand s = src.gen_source_operand(true,tempReg1);
      if (s instanceof X86.Reg)
	s = X86.resize_reg(type.X86_size,(X86.Reg) s);
      X86.Operand a = addr.gen_addr_operand(tempReg2);
      X86.emitMov(type.X86_size,s,a);
    }
    RegSet used() {
      RegSet s = new RegSet();
      s.add_source(src);
      s.add_source(addr.base);
      return s;
    }
    RegSet defined() {
      return new RegSet();
    }
  }

  public static class Call extends Inst {
    public final CallTgt tgt;
    public final boolean ind;	// true if indirect
    public final Src[] args;
    public final Dest rdst;    // could be null

    public Call(CallTgt f, boolean b, Src[] a, Dest r) { 
      tgt=f; ind=b; args=a; rdst=r;
    }
    public Call(CallTgt f, boolean b, List<Src> al, Dest r) { 
      this(f, b, al.toArray(new Src[0]), r);
    }
    public Call(CallTgt f, boolean b, List<Src> al) { 
      this(f, b, al.toArray(new Src[0]), null);
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
      return line(true, retstr +  "call " + (ind ? "* " : "") +
		  tgt + arglist + "\n");
    }

    void gen() {
      // Just fail if there are more than 6 args.
      int argCount = args.length;
      assert (argCount <= X86.argRegs.length);
      // If indirect jump through a register, make
      // sure this is not an argument register that
      // we're about to overwrite!
      X86.Operand call_target = null;
      if (ind) {
	if (tgt instanceof Reg) {
	  call_target = env.get(tgt);
	  assert (call_target != null); // dead value surely isn't used as a source
	  for (int i = 0; i < argCount; i++)
	    if (call_target.equals(X86.argRegs[i])) {
	      X86.emitMov(X86.Size.Q,call_target,tempReg2);
	      call_target = tempReg2;
	      break;
	    }
	} else //  tgt instanceof Global 
	  call_target = new X86.AddrName(((Global)tgt).toString());
      }
      // Move arguments into the argument regs.
      // First do parallel move of register sources.
      X86.Reg src[] = new X86.Reg[argCount];
      X86.Reg dst[] = new X86.Reg[argCount];
      boolean moved[] = new boolean[argCount]; // initialized to false
      int n = 0;
      for (int i = 0; i < argCount; i++) {
	IR.Src s = args[i];
	if (s instanceof Reg) {
	  X86.Operand rand = env.get((Reg) s);
	  if (rand instanceof X86.Reg) {
	    src[n] = (X86.Reg) rand;
	    dst[n] = X86.argRegs[i];
	    n++;
	    moved[i] = true; 
	  }
	}
      }
      new X86.ParallelMover(n,src,dst,tempReg1).move();
      // Now handle any immediate sources.
      for (int i = 0; i < argCount; i++) 
	if (!moved[i]) {
	  X86.Operand r = args[i].gen_source_operand(true,X86.argRegs[i]);
	  X86.emitMov(X86.Size.Q,r,X86.argRegs[i]);
	}
      if (ind) {
	X86.emit1("call *", call_target);
      } else
	X86.emit1("call", new X86.GLabel((((Global)tgt).toString())));
      if (rdst != null) {
	X86.Reg r = rdst.gen_dest_operand();
	X86.emitMov(X86.Size.Q,X86.RAX,r);
      }
    }
    RegSet used() {
      RegSet s = new RegSet();
      if (tgt instanceof Reg)
	s.add((Reg) tgt);
      for (Src a : args)
	s.add_source(a);
      return s;
    }
    RegSet defined() {
      RegSet s = new RegSet();
      if (rdst != null)
	s.add_dest(rdst);
      return s;
    }
  }

  public static class Return extends Inst {
    public final Src val;	// could be null

    public Return() { val=null; }
    public Return(Src s) { val=s; }
    public String toString() { 
      return line(true, " return " + (val==null ? "" : val) + "\n"); 
    }

    void gen() {
      // return value if any
      if (val != null) {
	X86.Operand r = val.gen_source_operand(true,X86.RAX);  
	X86.emitMov(X86.Size.Q,r,X86.RAX);
      }
      // exit sequence
      // pop the frame
      if (frameSize != 0)
	X86.emit2("addq",new X86.Imm(frameSize),X86.RSP);
      // restore any callee save registers
      for (int i = X86.calleeSaveRegs.length-1; i >= 0; i--) {
	X86.Reg r = X86.calleeSaveRegs[i];  
	if (env.containsValue(r))
	  X86.emit1("popq",r);
      }
      // and we're done
      X86.emit0("ret");
    }
    RegSet used() {
      RegSet s = new RegSet();
      if (val != null)
	s.add_source(val);
      return s;
    }
    RegSet defined() {
      return new RegSet();
    }
  }

  public static class CJump extends Inst {
    public final RelOP op;
    public final Src src1, src2;
    public final Label lab;

    public CJump(RelOP o, Src s1, Src s2, Label l) { 
      op=o; src1=s1; src2=s2; lab=l; 
    }
    public String toString() { 
      return line(true, " if " + src1 + " " + op + " " + src2 + 
	" goto " + lab + "\n");
    }

    void gen() {
      // remember: left and right are switched under gnu assembler
      X86.Operand mleft = src1.gen_source_operand(false,tempReg1); 
      X86.Operand mright = src2.gen_source_operand(true,tempReg2); 
      X86.emit2("cmp" + X86.Size.Q,mright,mleft);
      X86.emit0("j" + op.X86_name() + " F" + funcNumber + "_" + lab.name);
    }
    RegSet used() {
      RegSet s = new RegSet();
      s.add_source(src1);
      s.add_source(src2);
      return s;
    }
    RegSet defined() {
      return new RegSet();
    }
  }

  public static class Jump extends Inst {
    public final Label lab;

    public Jump(Label l) { lab=l; }
    public String toString() { 
      return line(true, " goto " + lab + "\n"); 
    }
    
    void gen() {
      X86.emit0("jmp F" + funcNumber + "_" + lab.name);
    }
    RegSet used() {
      return new RegSet();
    }
    RegSet defined() {
      return new RegSet();
    }
  }

  public static class LabelDec extends Inst { 
    public final String name;

    public LabelDec(String s) { name=s; }
    public String toString() { 
      return line(true, name + ":\n"); 
    }

    void gen() {
      X86.emitLabel(new X86.Label("F" + funcNumber + "_" + name));
    }
    RegSet used() {
      return new RegSet();
    }
    RegSet defined() {
      return new RegSet();
    }
  }

  // Operators

  public static interface BOP {
    public abstract String X86_name();
  }

  public static enum ArithOP implements BOP {
    ADD("+","add"), SUB("-","sub"), MUL("*","imul"), DIV("/","idiv"), AND("&&","and"), OR("||","or");
    final String name;
    final String xname;

    ArithOP(String n,String xn) { name = n; xname = xn;}
    public String toString() { return name; }
    public String X86_name() { return xname; }
  }

  public static enum RelOP implements BOP {
    EQ("==","e"), NE("!=","ne"), LT("<","l"), LE("<=","le"), GT(">","g"), GE(">=","ge");
    final String name;
    final String xname; 

    RelOP(String n,String xn) { name = n; xname = xn;}
    public String toString() { return name; }
    public String X86_name() { return xname; }
  }

  public static enum UOP {
    NEG("-","neg"), NOT("!","not");
    final String name;
    final String xname;

    UOP(String n, String xn) { name = n; xname = xn;}
    public String toString() { return name; }
    public String X86_name() { return xname; }
  }

  public static boolean isCompareOp(BOP op) {
    return (op == RelOP.EQ) || (op == RelOP.NE) ||
           (op == RelOP.LT) || (op == RelOP.LE) ||
           (op == RelOP.GT) || (op == RelOP.GE);
  }

  // Label

  public static class Label {
    static int labelnum=0;
    public String name;

    public Label() { name = "L" + labelnum++; }
    public Label(String s) { name = s; }
    public void set(String s) { name = s; }
    public String toString() { return name; }
  }

  // Address

  public static class Addr {   // Memory at base + offset
    public final Src base;  
    public final int offset;

    public Addr(Src b) { base=b; offset=0; }
    public Addr(Src b, int o) { base=b; offset=o; }
    public String toString() {
      return "" + ((offset == 0) ? "" : offset) + "[" + base + "]";
    }
    X86.Operand gen_addr_operand(X86.Reg temp) {
      X86.Operand xbase = base.gen_source_operand(false,temp);
      assert (xbase instanceof X86.Reg);
      return new X86.Mem((X86.Reg)xbase,offset);
    }
  }

  // Operands

  public interface Src {
    /** Generate a source operand.
	If imm_ok is true, returning an immediate is ok; 
	otherwise, result is definitely a register (possibly temp). 
	Not all operands can return an immediate, so it is always possible 
	that temp will be used even if imm_ok is true. **/
    X86.Operand gen_source_operand(boolean imm_ok, X86.Reg temp);
  }

  public interface Dest {
    /** Generate a destination operand of the specified size.
	Returns null if dest wasn't given a location or if we are 
	outside its live range; either way, it is dead at this point. **/
    X86.Reg gen_dest_operand();
  }

  public interface CallTgt {}

  public interface Const {
    abstract void gen_const();
  }

  public interface Reg {}

  public static class Id implements Reg, Src, Dest, CallTgt  {
    public final String name;

    public Id(String s) { name=s; }
    public String toString() { return name; }
    public boolean equals(Object l) {
      return (l instanceof Id && (((Id) l).name.equals(name)));
    }
    public int hashCode() {  
      return name.hashCode(); 
    }

    public X86.Operand gen_source_operand(boolean imm_ok, X86.Reg temp) {
      X86.Reg mrand = env.get(this);
      assert (mrand != null); // dead value surely isn't used as a source
      return mrand; 
    }
    public X86.Reg gen_dest_operand() {
      if (!liveRanges[irPtr].contains(this))
      	return null;
      return env.get(this);
    }
  }

  public static class Temp implements Reg, Src, Dest, CallTgt  {
    private static int cnt=0;
    public final int num;

    public Temp(int n) { num=n; }
    public Temp() { num = ++Temp.cnt; }
    public static void reset() { Temp.cnt = 0; }
    public static int getcnt() { return Temp.cnt; }
    public String toString() { return "t" + num; }
    public boolean equals(Object l) {
      return (l instanceof Temp && (((Temp) l).num == num));
    }
    public int hashCode() {  
      return num; 
    }

    public X86.Operand gen_source_operand(boolean imm_ok, X86.Reg temp) {
      X86.Reg mrand = env.get(this);
      assert (mrand != null); // dead value surely isn't used as a source
      return mrand; 
    }
    public X86.Reg gen_dest_operand() {
      if (!liveRanges[irPtr].contains(this))
      	return null;
      return env.get(this);
    }
  }

  public static class Global implements Src, CallTgt, Const {
    public final String name;

    public Global(String s) { name = s; }
    public String toString() { return "_" + name; }  // should be environment dependent
    public void gen_const() {
      X86.emit1(".quad", new X86.GLabel(toString()));
    }
    public X86.Operand gen_source_operand(boolean imm_ok, X86.Reg temp) {
      X86.AddrName mrand = new X86.AddrName(toString());
      X86.emit2("leaq",mrand,temp);
      return temp;
    }
  }

  public static class IntLit implements Src, Const {
    public final int i;

    public IntLit(int v) { i=v; }
    public String toString() { return i + ""; }
    public void gen_const() {
      X86.emit0(".long" + toString());
    }

    public X86.Operand gen_source_operand(boolean imm_ok, X86.Reg temp) {
      X86.Imm mrand = new X86.Imm(i);
      if (imm_ok)
	return mrand;
      else {
	X86.emitMov(X86.Size.Q,mrand,temp);
	return temp;
      }
    }
  }  


  public static class BoolLit implements Src {
    public final boolean b;

    public BoolLit(boolean v) { b=v; }
    public String toString() { return b + ""; }

    public X86.Operand gen_source_operand(boolean imm_ok, X86.Reg temp) {
      X86.Imm mrand = new X86.Imm(b ? 1 : 0);
      if (imm_ok)
	return mrand;
      else {
	X86.emitMov(X86.Size.Q,mrand,temp);
	return temp;
      }
    }
  }

  public static class StrLit implements Src {
    public final String s;

    public StrLit(String v) { s=v; }
    public String toString() { return "\"" + s + "\""; }

    public X86.Operand gen_source_operand(boolean imm_ok, X86.Reg temp) {
      X86.AddrName mrand = new X86.AddrName("_S" + stringLiterals.size());
      stringLiterals.add(s);
      X86.emit2("leaq",mrand,temp);
      return temp;
    }
  }

  // Utility class for describing sets of registers
  static class RegSet implements Iterable<Reg> {
    HashSet<Reg> set; 
    RegSet() {
      set = new HashSet<Reg>();
    }
    void add_source(Src rand) {
      if (rand instanceof Reg)
	add((Reg)rand);
    }
    void add_dest(Dest rand) {
      if (rand instanceof Reg)
	add((Reg)rand);
    }
    void add(Reg rand) {
      set.add(rand);
    }
    void remove(Reg rand) {
      set.remove(rand);
    }
    boolean contains(Reg rand) {
      return set.contains(rand);
    }
    public boolean equals(Object os) {
      return (os instanceof RegSet) && 
	(((RegSet) os).set.equals(set));  // expensive!
    }
    void diff(RegSet os) {
      for (Reg rand : os.set) 
	set.remove(rand);
    }
    void union(RegSet os) {
      for (Reg rand : os.set)
	set.add(rand);
    }	
    RegSet copy() {
      RegSet s = new RegSet();
      s.set.addAll(set);
      return s;
    }
    public String toString() {
      String r = "{ ";
      for (Reg rand : set) 
	r += rand + " ";
      r += "}";
      return r;
    }
    public Iterator<Reg> iterator() {
      return set.iterator();
    }
  }

  // Utility class for describing lists of integers
  // Mainly useful just for its specialized version of toString
  static class IndexList {
    List<Integer> list;
    IndexList() {
	list = new ArrayList<Integer>();
    }
    void add(int i) {
	list.add(i);
    }
    int get(int p) {
	return list.get(p);
    }
    int size() {
	return list.size();
    }
    public String toString() {
	String r = "[";
	if (size() > 0) {
	    r += get(0);
	    for (int i = 1; i < size(); i++) 
		r += "," + get(i);
	}
	r += "]";
	return r;
    }
  }
}
