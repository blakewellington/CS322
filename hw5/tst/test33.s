	.text
    # _main ()
    # (i, j, k)
# Allocation map
# t1	%rcx
# j	%rcx
# k	%rbx
# i	%rax
	.p2align 4,0x90
	.globl _main
_main:
	pushq %rbx
    # 0.  Begin:
F0_Begin:
    # 1.   i = 2
	movq $2,%rax
    # 2.   j = 3
	movq $3,%rcx
    # 3.   t1 = i + j
	movq %rcx,%r10
	movq %rax,%rcx
	addq %r10,%rcx
    # 4.   k = t1
	movq %rcx,%rbx
    # 5.   call _printInt(k)
	movq %rbx,%rdi
	call _printInt
    # 6.   if k != 5 goto L0
	cmpq $5,%rbx
	jne F0_L0
    # 7.   call _printStr("OK")
	leaq _S0(%rip),%rdi
	call _printStr
    # 8.  L0:
F0_L0:
    # 9.   return 
	popq %rbx
	ret
    # 10. End:
F0_End:
_S0:
	.asciz "OK"
