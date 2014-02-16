# Stack Code (SC0)

0. CONST 5	# Put 5 on the stack.
1. STORE 0	# Pop the stack and store into i
2. LOAD 0	# Load i onto the stack.
3. CONST 0	# Put 0 on the stack.
4. IFGT 3	# If val1 GT val2 then skip ahead 3 spaces.
5. CONST 0	# Otherwise put FALSE on the stack
6. GOTO 2	# ... and skip ahead 2 spaces
7. CONST 1	# Put TRUE on the stack.
8. IFZ 8	# Begin while loop: If condition is false, skip over the body of the loop.
9. LOAD 0	# Load i onto the stack.
10. PRINT	# Pop the stack and print it.
11. LOAD 0	# Load i onto the stack.
12. CONST 1	# Put 1 on the stack.
13. SUB		# SUBval1 and val2
14. STORE 0	# Pop the stack and store into i
15. GOTO -13	# Loop back to top of While loop and evaluate condition.
