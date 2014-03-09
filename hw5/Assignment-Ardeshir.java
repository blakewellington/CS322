import java.util.*;

class SortByStartPoint implements Comparator<Map.Entry<IR.Reg, Liveness.Interval>> {

	@Override
	//Interval OR IR Reg compare for start points
	public int compare(Map.Entry<IR.Reg, Liveness.Interval> e1, 
			Map.Entry<IR.Reg, Liveness.Interval> e2) {

		int intervalResult = 0;
		Liveness.Interval interval1 = e1.getValue();
		Liveness.Interval interval2 = e2.getValue();

		intervalResult = compare(interval1, interval2);

		if (intervalResult == 0)
			return compare(e1.getKey(), e2.getKey());
		else
			return intervalResult;
	}

	//Interval compare
	public int compare(Liveness.Interval e1, Liveness.Interval e2) {
		if (e1.start == e2.start)
			return Integer.compare(e1.end, e1.end);
		else
			return Integer.compare(e1.start, e2.start);
	}

	//IR Reg compare
	public int compare(IR.Reg e1, IR.Reg e2) {
		return e1.toString().compareTo(e2.toString());
	}
}

class SortByEndPoint implements Comparator<Liveness.Interval> {

	@Override
	//Interval compare for end points
	public int compare(Liveness.Interval e1, Liveness.Interval e2) {
		if (e1.end == e2.end)
			return Integer.compare(e1.start, e1.start);
		else
			return Integer.compare(e1.end, e2.end);
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

		// For now, do extremely simplistic allocation: simply allocate registers to
		// IR.Reg's eagerly.  Once a register is used, we don't
		// try to use it again, so we will very quickly run out.  

		// Keep track of available registers
		boolean[] regAvailable = new boolean[X86.allRegs.length];
		for (int i = 0; i < regAvailable.length; i++)
			regAvailable[i] = true;
		regAvailable[X86.RSP.r] = false;
		regAvailable[IR.tempReg1.r] = false;
		regAvailable[IR.tempReg2.r] = false;

		// Work through the list of live IR registers.
		// Even with our extremely simplistic approach, we must be careful
		// to handle callee save and caller save registers.  Again for simplicity,
		// we simply refuse to use a caller-save register for any operand whose
		// live interval includes a call; that way, we never have to worry about
		// saving caller-save registers at all.

		//Applying linear scan register allocator algorithm
		Set<Map.Entry<IR.Reg, Liveness.Interval>> sortByStartPoint = 
				new TreeSet<Map.Entry<IR.Reg, Liveness.Interval>>(new SortByStartPoint());

		for (Map.Entry<IR.Reg, Liveness.Interval> regAndInterval: liveIntervals.entrySet())
			sortByStartPoint.add(regAndInterval);

		Map<Liveness.Interval, X86.Reg> active = 
				new TreeMap<Liveness.Interval, X86.Reg>(new SortByEndPoint());

		Liveness.Interval tempInterval = null;

		for (Map.Entry<IR.Reg,Liveness.Interval> i: sortByStartPoint) { //Modified
			for (Map.Entry<Liveness.Interval, X86.Reg> j: active.entrySet()) {
				if (j.getKey().end >= i.getValue().start)
					break;
				//To avoid ConcurrentModificationException
				tempInterval = j.getKey();
				//Add the register to the pool 
				regAvailable[j.getValue().r] = true;
			}

			if (tempInterval != null)
				active.remove(tempInterval);
			//Done with linear scan allocator.

			IR.Reg t = i.getKey();
			X86.Reg treg = null;
			Liveness.Interval n = i.getValue();
			if (intervalContainsCall(func,n)) {
				// insist on a callee-save reg (ignoring any preference)
				for (X86.Reg reg : X86.calleeSaveRegs) 
					if (regAvailable[reg.r]) {
						treg = reg;
						break;
					}
			} else {
				// try first for a preference register (always caller-save)
				X86.Reg preg = preferences.get(t);
				if (preg !=null && regAvailable[preg.r]) 
					treg = preg;
				if (treg == null) 
					// try for an arbitrary caller-save reg
					for (X86.Reg reg : X86.callerSaveRegs) 
						if (regAvailable[reg.r]) {
							treg = reg;
							break;
						};
						if (treg == null) 
							// otherwise, try a callee-save 
							for (X86.Reg reg : X86.calleeSaveRegs) 
								if (regAvailable[reg.r]) {
									treg = reg;
									break;
								}
			}
			if (treg == null) {
				// couldn't find a register
				System.err.println("oops: out of registers");
				assert(false);
			}
			// We found a register; record it
			regAvailable[treg.r] = false;
			//Added for linear scan register allocator algorithm
			active.put(n, treg);
			env.put(t,treg);
			// DEBUG
			// System.err.println("allocating " + t + " to " + treg);
		}


		// For documentation purposes
		System.out.println("# Allocation map");
		for (Map.Entry<IR.Reg,X86.Reg> me : env.entrySet()) 
			System.out.println("# " + me.getKey() + "\t" + me.getValue());

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