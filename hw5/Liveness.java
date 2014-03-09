import java.io.*;
import java.util.*;

/**  Liveness analysis producing LiveOut sets and intervals */
class Liveness {

  // Calculate liveOut information for each instruction in a function
  static IR.RegSet[] calculateLiveRanges (IR.Func func) {
    IR.IndexList[] allSuccs = func.successors();
    
    IR.RegSet[] used = func.used();
    IR.RegSet[] defined = func.defined();
    // DEBUG
    // System.err.println(func.name + " use/def:");
    // for (int i = 0; i < func.code.length; i++) 
    //   System.err.println(i + "\t" + "U:" + used[i] + "\t" + "D:" + defined[i]);

    // Now solve dataflow equations to calculate
    // set of operands that are live out of each Inst
    IR.RegSet[] liveIn = new IR.RegSet[func.code.length];
    IR.RegSet[] liveOut = new IR.RegSet[func.code.length];
    for (int i = 0; i < func.code.length; i++) {
      liveIn[i] = new IR.RegSet();
      liveOut[i] = new IR.RegSet();
    }
    
    boolean changed = true;
    while (changed) {
      changed = false;
      for (int i = func.code.length-1; i >= 0; i--) {
	IR.RegSet newLiveIn = liveOut[i].copy();
	newLiveIn.diff(defined[i]);
	newLiveIn.union(used[i]);
	liveIn[i] = newLiveIn;
	IR.RegSet newLiveOut = new IR.RegSet();
	for (int n = 0; n < allSuccs[i].size(); n++) 
	  newLiveOut.union(liveIn[allSuccs[i].get(n)]);
	if (!liveOut[i].equals(newLiveOut)) {
	  liveOut[i] = newLiveOut;
	  changed = true;
	}
      }
    }

    // DEBUG
    // System.err.println(func.name + " liveOut:");
    // for (int i = 0; i < liveOut.length; i++) 
    //   System.err.println(i + "\t" + liveOut[i]);
    return liveOut;
  }

  static class Interval {
    int start;
    int end;
    Interval (int start, int end) {
      this.start = start; this.end = end;
    }
  }

  // calculate live intervals from live ranges
  static Map<IR.Reg,Interval> calculateLiveIntervals(IR.RegSet[] liveRanges) {
    Map<IR.Reg,Interval> liveIntervals = new HashMap<IR.Reg,Interval>();  
    for (int i = 0; i < liveRanges.length; i++) {
      for (IR.Reg t : liveRanges[i]) {
	Interval n = liveIntervals.get(t);
	if (n == null) {
	  n = new Interval(i,i);
	  liveIntervals.put(t,n);
	} else
	  n.end = i;
      }
    }
    // DEBUG 
    // Set<Map.Entry<IR.Reg,Interval>> lis = liveIntervals.entrySet();
    // for (Map.Entry<IR.Reg,Interval> me : lis) {
    //   IR.Reg t = me.getKey();
    //   Liveness.Interval n = me.getValue();
    //   System.err.println(t + "\t[" + n.start + "," + n.end + "]");
    // }
    return liveIntervals;
  }

}


