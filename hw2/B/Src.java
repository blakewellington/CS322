//____________________________________________________________________________
// IExpr ::= Var
//        |  Int
//        |  IExpr + IExpr
//        |  IExpr - IExpr

abstract class IExpr {
  abstract int    eval(Memory mem);
  abstract String show();
  abstract void bcgen(Bytecode b);
}

class Var extends IExpr {
  private String name;
  Var(String name) { this.name = name; }

  int    eval(Memory mem) { return mem.load(name); }
  String show() { return name; }

  void bcgen(Bytecode b) {
    b.load(name);
  }
}

class Int extends IExpr {
  private int num;
  Int(int num) { this.num = num; }

  int   eval(Memory mem) { return num; }
  String show() { return Integer.toString(num); }

  void bcgen(Bytecode b) {
    b.ldc(num);
  }
}

class Plus extends IExpr {
  private IExpr l, r;
  Plus(IExpr l, IExpr r) { this.l = l; this.r = r; }

  int    eval(Memory mem) { return l.eval(mem) + r.eval(mem); }
  String show() { return "(" + l.show() + " + " + r.show() + ")"; }

  void bcgen(Bytecode b) {
    l.bcgen(b);
    r.bcgen(b);
    b.add();
  }
}

class Minus extends IExpr {
  private IExpr l, r;
  Minus(IExpr l, IExpr r) { this.l = l; this.r = r; }

  int    eval(Memory mem) { return l.eval(mem) - r.eval(mem); }
  String show() { return "(" + l.show() + " - " + r.show() + ")"; }

  void bcgen(Bytecode b) {
    l.bcgen(b);
    r.bcgen(b);
    b.sub();
  }
}

class Assign extends IExpr {
  private String lhs;
  private IExpr  rhs;
  Assign(String lhs, IExpr rhs) {
    this.lhs = lhs; this.rhs = rhs;
  }
/*
  void exec(Memory mem) {
    mem.store(lhs, rhs.eval(mem));
  }

  void print(int ind) {
    indent(ind);
    System.out.println(lhs + " = " + rhs.show() + ";");
  }
*/
  int eval(Memory mem) {
	  int retValue = rhs.eval(mem);
	  mem.store(lhs,  retValue);
	  return retValue;
  }
  
  String show() {
	  return (lhs + " = " + rhs.show() + ";");
  }
  
  void bcgen(Bytecode b) {
    rhs.bcgen(b);
    b.dup();
    b.store(lhs);
  }
}

//____________________________________________________________________________
// BExpr ::= IExpr < IExpr
//        |  IExpr == IExpr

abstract class BExpr {
  abstract boolean eval(Memory mem);
  abstract String  show();

  abstract void bcgenIfTrue(Bytecode b, int lab);
  abstract void bcgenIfFalse(Bytecode b, int lab);
}

class LT extends BExpr {
  private IExpr l, r;
  LT(IExpr l, IExpr r) { this.l = l; this.r = r; }

  boolean eval(Memory mem) { return l.eval(mem) < r.eval(mem); }
  String show()  { return "(" + l.show() + " < " + r.show() + ")"; }

  void bcgenIfTrue(Bytecode b, int lab) {
    l.bcgen(b);
    r.bcgen(b);
    b.jl(lab);
  }

  void bcgenIfFalse(Bytecode b, int lab) {
    l.bcgen(b);
    r.bcgen(b);
    b.jnl(lab);
  }
}

class EqEq extends BExpr {
  private IExpr l, r;
  EqEq(IExpr l, IExpr r) { this.l = l; this.r = r; }

  boolean eval(Memory mem) { return l.eval(mem) == r.eval(mem); }
  String show()  { return "(" + l.show() + " == " + r.show() + ")"; }

  void bcgenIfTrue(Bytecode b, int lab) {
    l.bcgen(b);
    r.bcgen(b);
    b.je(lab);
  }

  void bcgenIfFalse(Bytecode b, int lab) {
    l.bcgen(b);
    r.bcgen(b);
    b.jne(lab);
  }
}

//____________________________________________________________________________
// Stmt  ::= Seq Stmt Stmt
//        |  Var := IExpr
//        |  While BExpr Stmt
//        |  If BExpr Stmt Stmt
//        |  Print IExpr

abstract class Stmt {
  abstract void exec(Memory mem);
  abstract void print(int ind);

  static void indent(int ind) {
    for (int i=0; i<ind; i++) {
      System.out.print(" ");
    }
  }

  abstract void bcgen(Bytecode b);
}

class Seq extends Stmt {
  private Stmt l, r;
  Seq(Stmt l, Stmt r) { this.l = l; this.r = r; }

  void exec(Memory mem) {
    l.exec(mem);
    r.exec(mem);
  }

  void print(int ind) {
    l.print(ind);
    r.print(ind);
  }

  void bcgen(Bytecode b) {
    l.bcgen(b);
    r.bcgen(b);
  }
}

class ExprStmt extends Stmt {
	  private Assign expr;
	  ExprStmt(Assign expr) {
	    this.expr = expr;
	  }

	  void exec(Memory mem) {
	    // fill this in ...
	    expr.eval(mem);
	  }

	  void print(int ind) {
	    // fill this in ...
		indent(ind);
	    System.out.println(expr.show());
	  }
	  
	  void bcgen(Bytecode b) {
		  expr.bcgen(b);
		  b.drop();
	  }
}


class While extends Stmt {
  private BExpr test;
  private Stmt  body;
  While(BExpr test, Stmt body) {
    this.test = test; this.body = body;
  }

  void exec(Memory mem) {
    while (test.eval(mem)) {
      body.exec(mem);
    }
  }

  void print(int ind) {
    indent(ind);
    System.out.println("while (" + test.show() + ") {");
    body.print(ind+2);
    indent(ind);
    System.out.println("}");
  }

  void bcgen(Bytecode b) {
    int l1 = b.newlabel(), l2 = b.newlabel();
    b.jmp(l2);
    b.atlabel(l1);
    body.bcgen(b);
    b.atlabel(l2);
    test.bcgenIfTrue(b,l1);
  }
}

class If extends Stmt {
  private BExpr test;
  private Stmt  t, f;
  If(BExpr test, Stmt t, Stmt f) {
    this.test = test; this.t = t; this.f = f;
  }

  void exec(Memory mem) {
    if (test.eval(mem)) {
      t.exec(mem);
    } else {
      f.exec(mem);
    }
  }

  void print(int ind) {
    indent(ind);
    System.out.println("if (" + test.show() + ") {");
    t.print(ind+2);
    indent(ind);
    System.out.println("} else {");
    f.print(ind+2);
    indent(ind);
    System.out.println("}");
  }

  void bcgen(Bytecode b) {
    int l1 = b.newlabel(), l2 = b.newlabel();
    test.bcgenIfFalse(b, l1);
    t.bcgen(b);
    b.jmp(l2);
    b.atlabel(l1);
    f.bcgen(b);
    b.atlabel(l2);
  }
}

class Print extends Stmt {
  private IExpr exp;
  Print(IExpr exp) { this.exp = exp; }

  void exec(Memory mem) {
    System.out.println("Output: " + exp.eval(mem));
  }

  void print(int ind) {
    indent(ind);
    System.out.println("print " + exp.show() + ";");
  }

  void bcgen(Bytecode b) {
    exp.bcgen(b);
    b.print();
  }
}
