class Main {
  public static void main(String[] args) {
    Stmt s
    
    = new Seq(new ExprStmt(new Assign("t", new Int(0))),
    	       new Seq(new ExprStmt(new Assign("i", new Int(0))),
    	       new Seq(new While(new LT(new Var("i"), new Int(11)),
    	                         new Seq(new ExprStmt(new Assign("t", new Plus(new Var("t"), new Var("i")))),
    	                                 new ExprStmt(new Assign("i", new Plus(new Var("i"), new Int(1)))))),
    	               new Print(new Var("t")))));

//    = new Seq(new ExprStmt(new Assign("t", new Int(0))), new Print(new Var("t")));
    
    
    
    System.out.println("Complete program is:");
    s.print(4);

    System.out.println("Running on an empty memory:");
    Memory mem = new Memory();
    s.exec(mem);

    System.out.println("Generating bytecode:");
    Bytecode b = new Bytecode();
    s.bcgen(b);
    b.stop();
    b.dump();
    System.out.println("Running on an empty memory:");
    b.exec();

    System.out.println("Done!");
  }
}
