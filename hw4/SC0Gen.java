// This is supporting software for CS321/CS322 Compilers and Language Design.
// Copyright (c) Portland State University
// 
// SC0 code generator. (For CS322 Lab5, Jingke Li)
//
//
import java.util.*;
import java.io.*;
import ast0.*;

class SC0GenException extends Exception {
    public SC0GenException(String msg) { super(msg); }
}

// ---------------------
// The main SC0Gen class
// ---------------------
//
class SC0Gen {

  static ArrayList<String> vars = new ArrayList<String>();

  public static void main(String [] args) throws Exception {
    if (args.length == 1) {
      FileInputStream stream = new FileInputStream(args[0]);
      Ast0.Program p = new ast0.ast0Parser(stream).Program();
      stream.close();
      List<String> code = gen(p);
      String[] insts = code.toArray(new String[0]);
      int i = 0;
      System.out.println("# Stack Code (SC0)\n");
      for (String inst: insts) {
	System.out.println(i++ + ". " + inst);
      }
    } else {
      System.out.println("You must provide an input file name.");
    }
  }

  // Ast0.Program ---
  // Ast0.Stmt[] stmts;
  //
  static List<String> gen(Ast0.Program n) throws Exception {
    ArrayList<String> code = new ArrayList<String>();
    for (Ast0.Stmt s: n.stmts)
      code.addAll(gen(s));
    return code;
  }

  // STATEMENTS

  static List<String> gen(Ast0.Stmt n) throws Exception {
    if (n instanceof Ast0.Block)       return gen((Ast0.Block) n);
    else if (n instanceof Ast0.Assign) return gen((Ast0.Assign) n);
    else if (n instanceof Ast0.If)     return gen((Ast0.If) n);
    else if (n instanceof Ast0.While)  return gen((Ast0.While) n);
    else if (n instanceof Ast0.Print)  return gen((Ast0.Print) n);
    throw new SC0GenException("Unknown Ast0 Stmt: " + n);
  }

  // Ast0.Block ---
  // Ast0.Stmt[] stmts;
  //
  static List<String> gen(Ast0.Block n) throws Exception {
    List<String> code = new ArrayList<String>();
    
    // My Code BEGIN
    for (Ast0.Stmt statement: n.stmts) {
      code.addAll(gen(statement));
    }
    // My Code END
    
    return code;
  }

  // Ast0.Assign ---
  // Ast0.Exp lhs, rhs;
  //
  static List<String> gen(Ast0.Assign n) throws Exception {
	    List<String> code = new ArrayList<String>();
    if (n.lhs instanceof Ast0.Id) {
      String name = ((Ast0.Id) n.lhs).nm;
      int idx = vars.indexOf(name);
      if (idx < 0) {
        vars.add(name);
        idx = vars.indexOf(name);
      }
      code = gen(n.rhs);
      code.add("STORE " + idx);
    } else if (n.lhs instanceof Ast0.ArrayElm) {
    // My Code BEGIN
    	code.addAll(gen(((Ast0.ArrayElm) n.lhs).ar));	// Push the array reference to the stack
    	code.addAll(gen(((Ast0.ArrayElm) n.lhs).idx));	// Push the array index to the stack
    	code.addAll(gen(n.rhs));						// Push the RHS value to the stack.
	    code.add("ASTORE");
    // My Code END

	// ... need code ...

    } else {
      throw new SC0GenException("Wrong Ast0 lhs Exp: " + n.lhs);
    }
    return code;
  }

  // Ast0.If ---
  // Ast0.Exp cond;
  // Ast0.Stmt s1, s2;
  //
  static List<String> gen(Ast0.If n) throws Exception {
	    List<String> code = new ArrayList<String>();
    // My Code BEGIN
	    	code.addAll(gen(n.cond));				// Boolean, either 1 or 0 (T or F)
	    	// If FALSE, jump ahead the number of instructions of s1 + 2
	    	code.add("IFZ " + ((gen(n.s1)).size() + 2));	
	    	code.addAll(gen(n.s1));
	    	if (n.s2 != null) {
	    		code.add("GOTO " + ((gen(n.s2)).size() + 1));
	    		code.addAll(gen(n.s2));
	    	}
    // My Code END
    // ... need code ...
    return code;
  }

  // Ast0.While ---
  // Ast0.Exp cond;
  // Ast0.Stmt s;
  //
  static List<String> gen(Ast0.While n) throws Exception {
	    List<String> code = new ArrayList<String>();
    // My Code BEGIN
    	code.addAll(gen(n.cond));				// Boolean, either 1 or 0 (T or F)
    	// If FALSE, jump ahead the number of instructions of s1 + 2
    	code.add("IFZ " + ((gen(n.s)).size() + 2));	
    	code.addAll(gen(n.s));
    	code.add("GOTO -" + ((gen(n.s)).size() + 2));
    // My Code END
    // ... need code ...
    return code;
  }
  
  // Ast0.Print ---
  // Ast0.Exp arg;
  //
  static List<String> gen(Ast0.Print n) throws Exception {
	    List<String> code = new ArrayList<String>();
    // My Code BEGIN
	code.addAll(gen(n.arg));
    code.add("PRINT");
    // My Code END

    // ... need code ...

    return code;
  }

  // EXPRESSIONS

  static List<String> gen(Ast0.Exp n) throws Exception {
    if (n instanceof Ast0.Binop)    return gen((Ast0.Binop) n);
    if (n instanceof Ast0.Unop)     return gen((Ast0.Unop) n);
    if (n instanceof Ast0.NewArray) return gen((Ast0.NewArray) n);
    if (n instanceof Ast0.ArrayElm) return gen((Ast0.ArrayElm) n);
    if (n instanceof Ast0.Id)	    return gen((Ast0.Id) n);
    if (n instanceof Ast0.IntLit)   return gen((Ast0.IntLit) n);
    if (n instanceof Ast0.BoolLit)  return gen((Ast0.BoolLit) n);
    throw new SC0GenException("Unknown Exp node: " + n);
  }

  // Ast0.Binop ---
  // Ast0.BOP op;
  // Ast0.Exp e1,e2;
  //
  static List<String> gen(Ast0.Binop n) throws Exception {
    List<String> code = gen(n.e1);
    code.addAll(gen(n.e2)); 
    String op = gen(n.op);
    switch (n.op) {
    case ADD:  
    case SUB:   
    case MUL:  
    case DIV:  
    case AND:  
    case OR:   
    	code.add(op);
    	break;
    case EQ:   
    	code.add("IFNE 3");		// If not equal, jump over the next instructions
    	code.add("CONST 1");	// Set return value to TRUE
    	code.add("GOTO 2");
    	code.add("CONST 0"); 	// Set return value to FALSE
    	break;
    case NE:   
    	code.add("IFEQ 3");		// If not equal, jump over the next instructions
    	code.add("CONST 1");	// Set return value to TRUE
    	code.add("GOTO 2");
    	code.add("CONST 0"); 	// Set return value to FALSE
    	break;
    case LT:   
    	code.add("IFGE 3");		// If not LT, jump over the next instructions
    	code.add("CONST 1");	// Set return value to TRUE
    	code.add("GOTO 2");
    	code.add("CONST 0"); 	// Set return value to FALSE
    	break;
    case LE:   
    	code.add("IFGT 3");		// If not LE, jump over the next instructions
    	code.add("CONST 1");	// Set return value to TRUE
    	code.add("GOTO 2");
    	code.add("CONST 0"); 	// Set return value to FALSE
    	break;
    case GT:   
    	code.add("IFLE 3");		// If not GT, jump over the next instructions
    	code.add("CONST 1");	// Set return value to TRUE
    	code.add("GOTO 2");
    	code.add("CONST 0"); 	// Set return value to FALSE
    	break;
    case GE:   
    	code.add("IFLT 2");		// If not GE, jump over the next instructions
    	code.add("CONST 1");	// Set return value to TRUE
    	code.add("GOTO 2");
    	code.add("CONST 0"); 	// Set return value to FALSE
    }
    return code;
  }

  // Ast0.Unop ---
  // Ast0.UOP op;
  // Ast0.Exp e;
  //
  static List<String> gen(Ast0.Unop n) throws Exception {
	    List<String> code = new ArrayList<String>();
    // My Code BEGIN
    // Two types of Unops: negation (!) and negative (-)
    code.addAll(gen(n.e));		// Put the value of the expression on the stack
    if (n.op == Ast0.UOP.NEG) {
    	code.add("NEG");
    } else {  // NOT acts only on Booleans, represented by 1 (TRUE) or 0 (FALSE)
    	// subtract 1 and multiply the result by -1
    	// Eg: TRUE (1) becomes FALSE (0)
    	//     FALSE (0) becomes TRUE (1)
    	code.add("CONST 1");		// Put a 1 on the stack
    	code.add("SUB");			// Subtract the 1 from what was already on the stack
    	code.add("CONST -1");		// Put a -1 on the stack
    	code.add("MULT");			// Multiply by the -1
    }
    // My Code End

    // ... need code ...

    return code;
  }
  
  // Ast0.NewArray ---
  // int sz;
  // 
  static List<String> gen(Ast0.NewArray n) throws Exception {
	    List<String> code = new ArrayList<String>();
    // My Code BEGIN

	    code.add("CONST " + n.sz);
	    code.add("NEWARRAY");
    // My Code END

    // ... need code ...

    return code;
  }

  // Ast0.ArrayElm ---
  // Ast0.Exp ar, idx;
  //
  static List<String> gen(Ast0.ArrayElm n) throws Exception {
	    List<String> code = new ArrayList<String>();
    // My Code BEGIN
	    code.addAll(gen(n.ar));
	    code.addAll(gen(n.idx));
	    code.add("ALOAD");
    // My Code END

    // ... need code ...

    return code;
  }
  
  // Ast0.Id ---
  // String nm;
  //
  static List<String> gen(Ast0.Id n) throws Exception {
    List<String> code = new ArrayList<String>();
    int idx = vars.indexOf(n.nm);
    if (idx < 0)
      throw new SC0GenException("Id is not defined: " + n.nm);
    code.add("LOAD " + idx);
    return code;
  }

  // Ast0.IntLit ---
  // int i;
  //
  static List<String> gen(Ast0.IntLit n) throws Exception {
    List<String> code = new ArrayList<String>();
    code.add("CONST " + n.i);
    return code;
  }

  // Ast0.BoolLit ---
  // boolean b;
  //
  static List<String> gen(Ast0.BoolLit n) {
    List<String> code = new ArrayList<String>();
    code.add("CONST " + (n.b ? 1 : 0));
    return code;
  }

  // OPERATORS

  static String gen(Ast0.BOP op) {
    switch (op) {
    case ADD: return "ADD";
    case SUB: return "SUB";
    case MUL: return "MUL";
    case DIV: return "DIV";
    case AND: return "AND";
    case OR:  return "OR";
    case EQ:  return "IFEQ";
    case NE:  return "IFNE"; 
    case LT:  return "IFLT"; 
    case LE:  return "IFLE"; 
    case GT:  return "IFGT"; 
    case GE:  return "IFGE"; 
    }
    return null;
  }
   
}
