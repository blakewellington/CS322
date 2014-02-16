# Stack Code (SC0)

0. CONST 3	# Put 3 on the stack.
1. CONST 1	# Put 1 on the stack.
2. IFGT 3	# If val1 GT val2 then skip ahead 3 spaces.
3. CONST 0	# Otherwise put FALSE on the stack
4. GOTO 2	# ... and skip ahead 2 spaces
5. CONST 1	# Put TRUE on the stack.
6. STORE 0	# Pop the stack and store into x
7. CONST 1	# Put TRUE on the stack.
8. CONST 1
9. SUB
10. CONST -1
11. MULT
12. STORE 1	# Pop the stack and store into y
13. LOAD 0	# Load x onto the stack.
14. LOAD 1	# Load y onto the stack.
15. OR		# ORval1 and val2
16. IFZ 4	# Begin If block: If condition is false, skip over the body of If statement.
17. LOAD 0	# Load x onto the stack.
18. PRINT	# Pop the stack and print it.
19. GOTO 3	# If we land here, we need to skip over the else section
20. LOAD 1	# Load y onto the stack.
21. PRINT	# Pop the stack and print it.
