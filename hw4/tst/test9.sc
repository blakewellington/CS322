# Stack Code (SC0)

0. CONST 0	# Put FALSE on the stack.
1. STORE 0	# Pop the stack and store into flag
2. LOAD 0	# Load flag onto the stack.
3. IFZ 6	# Begin If block: If condition is false, skip over the body of If statement.
4. CONST 1	# Put 1 on the stack.
5. STORE 1	# Pop the stack and store into x
6. LOAD 0	# Load flag onto the stack.
7. PRINT	# Pop the stack and print it.
8. GOTO 21	# If we land here, we need to skip over the else section
9. CONST 2	# Put 2 on the stack.
10. STORE 1	# Pop the stack and store into x
11. LOAD 0	# Load flag onto the stack.
12. PRINT	# Pop the stack and print it.
13. CONST 10	# Put 10 on the stack.
14. STORE 2	# Pop the stack and store into i
15. LOAD 2	# Load i onto the stack.
16. CONST 0	# Put 0 on the stack.
17. IFGT 3	# If val1 GT val2 then skip ahead 3 spaces.
18. CONST 0	# Otherwise put FALSE on the stack
19. GOTO 2	# ... and skip ahead 2 spaces
20. CONST 1	# Put TRUE on the stack.
21. IFZ 8	# Begin while loop: If condition is false, skip over the body of the loop.
22. LOAD 2	# Load i onto the stack.
23. PRINT	# Pop the stack and print it.
24. LOAD 2	# Load i onto the stack.
25. CONST 1	# Put 1 on the stack.
26. SUB		# SUBval1 and val2
27. STORE 2	# Pop the stack and store into i
28. GOTO -13	# Loop back to top of While loop and evaluate condition.
29. LOAD 1	# Load x onto the stack.
30. PRINT	# Pop the stack and print it.
