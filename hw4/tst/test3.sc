# Stack Code (SC0)

0. CONST 0	# Put FALSE on the stack.
1. STORE 0	# Pop the stack and store into flag
2. LOAD 0	# Load flag onto the stack.
3. IFZ 4	# Begin If block: If condition is false, skip over the body of If statement.
4. CONST 1	# Put 1 on the stack.
5. STORE 1	# Pop the stack and store into x
6. GOTO 3	# If we land here, we need to skip over the else section
7. CONST 2	# Put 2 on the stack.
8. STORE 1	# Pop the stack and store into x
9. LOAD 1	# Load x onto the stack.
10. PRINT	# Pop the stack and print it.
