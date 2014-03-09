import java.util.*;

/*
  In order to use the Linear Scan Allocation algorithm, we need to be able
  to sort the liveness intervals in ascending order by both start and end
  values. Each liveness interval is associated with its IR.reg and the final
  X86.reg. To keep this association, it will be easiest to use the existing
  container and create comparator classes to sort them.

  The container that we want to sort is the liveIntervals implementation of
  the Map<IR.Reg,Liveness.Interval> container. To sort this object, we need to
  compare the start and/or end values of the embedded Liveness.Interval.

  However, there are subtle bugs that can be introduced if two elements of 
  the liveIntervals evaluate as equal (returning 0 from the comparator). To 
  avoid this, we will first compare the start value. If they are equal, compare
  the end value. If they are STILL equal, compare the associated IR.Reg name.
  This will assure an absolute ordering of the registers.
 */
class StartPointComparator implements Comparator<Map.Entry<IR.Reg, Liveness.Interval>> {

	@Override
	// Interval OR IR Reg compare for start points
	public int compare(Map.Entry<IR.Reg, Liveness.Interval> a, Map.Entry<IR.Reg, Liveness.Interval> b) {

		int intervalResult = 0;
		Liveness.Interval intervalA = a.getValue();
		Liveness.Interval intervalB = b.getValue();

		// First compare the interval.start values:
		intervalResult = compare(intervalA, intervalB);

		// If they compare as equal, go compare the keys (just the name of the IR.Reg
		// Otherwise, return the result (-1 or 1)
		if (intervalResult == 0)
			return compare(a.getKey(), b.getKey());
		else
			return intervalResult;
	}

	// Compare start values of the Liveness.Interval
	// If they are equal, compare the end values
	public int compare(Liveness.Interval a, Liveness.Interval b) {
		return a.start == b.start ? Integer.compare(a.end, b.end) : Integer.compare(a.start, b.start);
	}

	//Compare the IR.Reg name (the name of the IR variable)
	public int compare(IR.Reg a, IR.Reg b) {
		return a.toString().compareTo(b.toString());
	}
}

// Comparing end points is a little more straightforward. There is no
// need to establish a total order as in StartPointComparator
class EndPointComparator implements Comparator<Liveness.Interval> {
	@Override
	// Compare end points of the intervals.
	public int compare(Liveness.Interval a, Liveness.Interval b) {
//		return a.end == b.end ? Integer.compare (a.start, b.start) : Integer.compare (a.end, b.end);
//		return a.end == b.end ? 0 : Integer.compare (a.end, b.end);
//		return a.end == b.end ? 0 : Integer.compare (a.start, b.start);
		return Integer.compare (a.end, b.end);

	}
}


/** Register assignment */
class Assignment {

	/** Assign IR.Regs to locations described by X86.Operands. */
	static Map<IR.Reg,X86.Reg> assignRegisters(IR.Func func,IR.RegSet[] liveRanges) {
		Map<IR.Reg,X86.Reg> env = new HashMap<IR.Reg,X86.Reg>();

		// Approximate live ranges by live intervals
		Map<IR.Reg,Liveness.Interval> liveIntervals = Liveness.calculateLiveIntervals(liveRanges); 

		// Get preferences for assignments. 
		// Preferences are not binding. In particular, ranges that span a 
		// a call will never end up in a caller-save register, but we 
		// don't worry about that now.
		Map<IR.Reg,X86.Reg> preferences = getPreferences(func);

		// Keep track of available registers
		boolean[] regAvailable = new boolean[X86.allRegs.length];
		for (int i = 0; i < regAvailable.length; i++) {
			regAvailable[i] = true;
		}

		regAvailable[X86.RSP.r] = false;
		regAvailable[IR.tempReg1.r] = false;
		regAvailable[IR.tempReg2.r] = false;


		// Linear Scan Allocation Algorithm. This replaces the very simplistic
		// first-come-first-served algorithm supplied with this assignment.
		/*
      The Algorithm:
      1. Compute startpoint[i] and endpoint[i] of live interval i for each temporary. 
         Store the intervals in a list in order of increasing start point.
      2. Initialize set active := null and pool of free registers = all usable registers.
      3. For each live interval i in order of increasing start point:
        (a) For each interval j in active, in order of increasing end point
		 * If endpoint[j] = startpoint[i] break to step 3b.
		 * Remove j from active.
		 * Add register[j] to pool of free registers.
        (b) Set register[i] := next register from pool of free registers, and remove it from pool. 
            (If pool is already empty, need to spill.)
        (c) Add i to active, sorted by increasing end point.
		 */

		// First, create a sorted mapping from IR.Reg to liveness interval
		// (sorted by start point)
		Set<Map.Entry<IR.Reg, Liveness.Interval>> intervalsByStartPoint = new TreeSet<Map.Entry<IR.Reg, Liveness.Interval>>(new StartPointComparator());

		// Populate the mapping from the given liveIntervals mapping
		for (Map.Entry<IR.Reg, Liveness.Interval> regAndInterval: liveIntervals.entrySet()) {
			intervalsByStartPoint.add(regAndInterval);
		}

		// Create a mapping for the active registers, sorted by end point.
		Map<Liveness.Interval, X86.Reg> activeRegs = new TreeMap<Liveness.Interval, X86.Reg>(new EndPointComparator());

		// Since we will be iterating through the activeRegs and potentially altering it, we
		// need a temp interval to avoid a ConcurrentModification Exception.
		Liveness.Interval tempInterval = null;

		// Work through the list of live IR registers.
		// We must be careful to handle callee save and caller save registers.  
		// Again for simplicity, we simply refuse to use a caller-save register 
		// for any operand whose live interval includes a call; that way, we never
		// have to worry about saving caller-save registers at all.
		for (Map.Entry<IR.Reg,Liveness.Interval> i: intervalsByStartPoint) {
			for (Map.Entry<Liveness.Interval, X86.Reg> j: activeRegs.entrySet()) {
				if (j.getKey().end >= i.getValue().start) {
					break;
				}

				//To avoid ConcurrentModificationException
				tempInterval = j.getKey();
				//Add the register to the pool 
				regAvailable[j.getValue().r] = true;
			}

			if (tempInterval != null) {
				activeRegs.remove(tempInterval);
			}

			// End of Linear Scan Allocation Algorithm
			IR.Reg t = i.getKey();
			X86.Reg treg = null;
			Liveness.Interval n = i.getValue();
			if (intervalContainsCall(func,n)) {
				// insist on a callee-save reg (ignoring any preference)
				for (X86.Reg reg : X86.calleeSaveRegs) {
					if (regAvailable[reg.r]) {
						treg = reg;
						break;
					}
				}
			} else {
				// try first for a preference register (always caller-save)
				X86.Reg preg = preferences.get(t);
				if (preg !=null && regAvailable[preg.r]) {
					treg = preg;
				}
				if (treg == null) 
					// try for an arbitrary caller-save reg
					for (X86.Reg reg : X86.callerSaveRegs) {
						if (regAvailable[reg.r]) {
							treg = reg;
							break;
						}
					}
				if (treg == null) {
					// otherwise, try a callee-save 
					for (X86.Reg reg : X86.calleeSaveRegs) {
						if (regAvailable[reg.r]) {
							treg = reg;
							break;
						}
					}
				}
			}
			if (treg == null) {
				// couldn't find a register
				System.err.println("oops: out of registers");
				assert(false);
			}
			// We found a register; record it
			regAvailable[treg.r] = false;
			activeRegs.put(n, treg);
			env.put(t,treg);
			// DEBUG
			// System.err.println("allocating " + t + " to " + treg);
		}

		// For documentation purposes
		System.out.println("# Allocation map");
		for (Map.Entry<IR.Reg,X86.Reg> me : env.entrySet()) {
			System.out.println("# " + me.getKey() + "\t" + me.getValue());
		}
		return env;
	}

	/**  Return true if specified interval includes an IR instruction
       that will cause an X86.call (or invoke an X86.divide) **/
	static boolean intervalContainsCall(IR.Func func,Liveness.Interval n) {
		for (int i = n.start+1; i <= n.end; i++)
			if (func.code[i] instanceof IR.Call ||
					(func.code[i] instanceof IR.Binop && ((IR.Binop) func.code[i]).op == IR.ArithOP.DIV))
				return true;
		return false;
	}

	// Find insertion point for x in a, assuming a is sorted in ascending natural order 
	static int insertionPoint(List<Integer> a, int x) {
		int i = 0;
		for (Integer y : a) {
			if (x <= y)
				return i;
			i++;
		}
		return i;
	}

	/** Calculate (non-binding) preferences for register assignments.
      Note: all preference registers should be caller-save (otherwise they're ignored) **/
	static Map<IR.Reg,X86.Reg> getPreferences(IR.Func func) {
		Map<IR.Reg,X86.Reg> preferences = new HashMap<IR.Reg,X86.Reg>(); 

		// Incoming arguments from callee's perspective
		for (int i = 0; i < func.params.length; i++)  
			preferences.put(new IR.Id(func.params[i]),X86.argRegs[i]); 

		for (IR.Inst c : func.code) {
			if (c instanceof IR.Call) {
				IR.Call cl = (IR.Call) c;
				// Arguments from caller's perspective
				for (int i = 0; i < cl.args.length; i++) {
					IR.Src argRand = cl.args[i];
					if (argRand instanceof IR.Reg) 
						preferences.put((IR.Reg) argRand,X86.argRegs[i]);
				}
				// Return value from caller's perspective
				if (cl.rdst instanceof IR.Reg)
					preferences.put((IR.Reg) cl.rdst,X86.RAX);
			} else if (c instanceof IR.Return) {
				// Return value from callee's perspective
				IR.Return r = (IR.Return) c;
				if (r.val instanceof IR.Reg) 
					preferences.put((IR.Reg) r.val,X86.RAX);
			} else if (c instanceof IR.Binop) {
				IR.Binop b = (IR.Binop) c;
				if (b.op == IR.ArithOP.DIV) {
					// Argument and result of DIV
					if (b.src1 instanceof IR.Reg)
						preferences.put((IR.Reg) b.src1,X86.RAX);
					if (b.dst instanceof IR.Reg)
						preferences.put((IR.Reg) b.dst,X86.RAX);
				}
			}
		}
		return preferences;
	}
}