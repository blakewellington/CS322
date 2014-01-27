class Main {
  public static void main(String[] args) {
    Stmt s
     = new Seq(new ExprStmt(new Assign("t", new Int(0))),
       new Seq(new ExprStmt(new Assign("i", new Int(0))),
       new Seq(new While(new LT(new Var("i"), new Int(11)),
                         new Seq(new ExprStmt(new Assign("t", new Plus(new Var("t"), new Var("i")))),
                                 new ExprStmt(new Assign("i", new Plus(new Var("i"), new Int(1)))))),
               new Print(new Var("t")))));

    Assign init = new Assign("i", new Int (0));
    BExpr test = new LT(new Var("i"), new Int(11));
    Assign step = new Assign("i", new Plus(new Var("i"), new Int(1)));
    Stmt body = new Print(new Var("i"));
    
    s = new For(init, test, step, body);

        
    System.out.println("Complete program is:");
    s.print(4);

    System.out.println("Running on an empty memory:");
    Memory mem = new Memory();
    s.exec(mem);

    System.out.println("Done!");
  }
}
