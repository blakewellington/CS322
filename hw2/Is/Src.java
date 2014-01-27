//____________________________________________________________________________
// IExpr ::= Var
//        |  Int
//        |  IExpr + IExpr
//        |  IExpr - IExpr

abstract class IExpr {
  abstract int    eval(Memory mem);
  abstract String show();
}

class Var extends IExpr {
  private String name;
  Var(String name) { this.name = name; }

  int    eval(Memory mem) { return mem.load(name); }
  String show() { return name; }
}

class Int extends IExpr {
  private int num;
  Int(int num) { this.num = num; }

  int   eval(Memory mem) { return num; }
  String show() { return Integer.toString(num); }
}

class Plus extends IExpr {
  private IExpr l, r;
  Plus(IExpr l, IExpr r) { this.l = l; this.r = r; }

  int    eval(Memory mem) { return l.eval(mem) + r.eval(mem); }
  String show() { return "(" + l.show() + " + " + r.show() + ")"; }
}

class Minus extends IExpr {
  private IExpr l, r;
  Minus(IExpr l, IExpr r) { this.l = l; this.r = r; }

  int    eval(Memory mem) { return l.eval(mem) - r.eval(mem); }
  String show() { return "(" + l.show() + " - " + r.show() + ")"; }
}

//____________________________________________________________________________
// BExpr ::= IExpr < IExpr
//        |  IExpr == IExpr

abstract class BExpr {
  abstract boolean eval(Memory mem);
  abstract String  show();
}

class LT extends BExpr {
  private IExpr l, r;
  LT(IExpr l, IExpr r) { this.l = l; this.r = r; }

  boolean eval(Memory mem) { return l.eval(mem) < r.eval(mem); }
  String show()  { return "(" + l.show() + " < " + r.show() + ")"; }
}

class EqEq extends BExpr {
  private IExpr l, r;
  EqEq(IExpr l, IExpr r) { this.l = l; this.r = r; }

  boolean eval(Memory mem) { return l.eval(mem) == r.eval(mem); }
  String show()  { return "(" + l.show() + " == " + r.show() + ")"; }
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
}


class Assign extends IExpr {
  private String lhs;
  private IExpr  rhs;
  Assign(String lhs, IExpr rhs) {
    this.lhs = lhs; this.rhs = rhs;
  }
/*
  // Original methods exec() and print()
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
    mem.store(lhs, retValue );
    return retValue;
  }

  String show() {
    return (lhs + " = " + rhs.show() + ";");
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
}

class For extends Stmt {
    private Assign init;   // Note: this could be null
    private BExpr  test;   // Note: this could be null
    private Assign step;   // Note: this could be null
    private Stmt   body;
    For(Assign init, BExpr test, Assign step, Stmt body) {
      this.init = init; this.test = test; this.step = step; this.body = body;
    }
    // Fill in the missing method definitions here ...
    void exec(Memory mem) { 
    	// Convert the for loop to a while loop (since while is already implemented)
    	
    	// Evaluate the init portion of the loop
    	if (init != null) init.eval(mem);
    	while ((test == null) ? true : test.eval(mem)) {
    		body.exec(mem);
    		if (step != null) step.eval(mem);
    	}
    }

    void print(int ind) {
    	indent(ind);
    	System.out.println("for (" + init.show() + "; " + test.show() + "; " + step.show() + ") {");
        if (body != null) body.print(ind+2);
        indent(ind);
        System.out.println("}");
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
}
