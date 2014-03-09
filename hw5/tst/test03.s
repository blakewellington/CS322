	.text
    # _main ()
    # (b, i)
# Allocation map
# t1	%rax
# t2	%rcx
# t3	%rdx
# b	%rbx
# t4	%rdx
# t5	%rdx
# t6	%rdx
# t7	%rbp
# t8	%rdx
# t9	%rdx
# i	%r12
	.p2align 4,0x90
	.globl _main
_main:
	pushq %rbx
	pushq %rbp
	pushq %r12
    # 0.  Begin:
F0_Begin:
    # 1.   t1 = 1 > 2
	movq $2,%r10
	movq $1,%r11
	cmpq %r10,%r11
	setg %al
    # 2.   t2 = 3 < 4
	movq $4,%r10
	movq $3,%r11
	cmpq %r10,%r11
	setl %cl
    # 3.   t3 = !false
	movq $0,%rdx
	notq %rdx
    # 4.   t4 = t2 && t3
	movq %rdx,%r10
	movq %rcx,%rdx
	andq %r10,%rdx
    # 5.   t5 = t1 || t4
	movq %rdx,%r10
	movq %rax,%rdx
	orq %r10,%rdx
    # 6.   b = t5
	movq %rdx,%rbx
    # 7.   t6 = 2 * 4
	movq $2,%rdx
	imulq $4,%rdx
    # 8.   t7 = 2 + t6
	movq $2,%rbp
	addq %rdx,%rbp
    # 9.   t8 = 9 / 3
	movq $3,%r10
	movq $9,%rax
	cqto
	idivq %r10
	movq %rax,%rdx
    # 10.  t9 = t7 - t8
	movq %rdx,%r10
	movq %rbp,%rdx
	subq %r10,%rdx
    # 11.  i = t9
	movq %rdx,%r12
    # 12.  call _printBool(b)
	movq %rbx,%rdi
	call _printBool
    # 13.  call _printInt(i)
	movq %r12,%rdi
	call _printInt
    # 14.  return 
	popq %r12
	popq %rbp
	popq %rbx
	ret
    # 15. End:
F0_End:
