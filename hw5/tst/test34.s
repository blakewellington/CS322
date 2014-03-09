	.text
    # _main ()
    # (a, b, i)
# Allocation map
# t1	%rax
# t2	%rcx
# t3	%rcx
# b	%rdi
# t4	%rcx
# t5	%rcx
# t6	%rdx
# a	%rax
# t7	%rdx
# t8	%rdx
# t9	%rcx
# t10	%rcx
# t11	%rcx
# t12	%rcx
# t13	%rcx
# i	%rcx
	.p2align 4,0x90
	.globl _main
_main:
	subq $8,%rsp
    # 0.  Begin:
F0_Begin:
    # 1.   t1 = call _malloc(8)
	movq $8,%rdi
	call _malloc
    # 2.   a = t1
    # 3.   t2 = 0 * 4
	movq $0,%rcx
	imulq $4,%rcx
    # 4.   t3 = a + t2
	movq %rcx,%r10
	movq %rax,%rcx
	addq %r10,%rcx
    # 5.   [t3]:I = 2
	movl $2,(%rcx)
    # 6.   t4 = 1 * 4
	movq $1,%rcx
	imulq $4,%rcx
    # 7.   t5 = a + t4
	movq %rcx,%r10
	movq %rax,%rcx
	addq %r10,%rcx
    # 8.   [t5]:I = 4
	movl $4,(%rcx)
    # 9.   i = 0
	movq $0,%rcx
    # 10.  t6 = i * 4
	movq %rcx,%rdx
	imulq $4,%rdx
    # 11.  t7 = a + t6
	movq %rdx,%r10
	movq %rax,%rdx
	addq %r10,%rdx
    # 12.  t8 = [t7]:I
	movslq (%rdx),%rdx
    # 13.  t9 = i + 1
	addq $1,%rcx
    # 14.  t10 = t9 * 4
	imulq $4,%rcx
    # 15.  t11 = a + t10
	movq %rcx,%r10
	movq %rax,%rcx
	addq %r10,%rcx
    # 16.  t12 = [t11]:I
	movslq (%rcx),%rcx
    # 17.  t13 = t8 + t12
	movq %rcx,%r10
	movq %rdx,%rcx
	addq %r10,%rcx
    # 18.  b = t13
	movq %rcx,%rdi
    # 19.  call _printInt(b)
	call _printInt
    # 20.  return 
	addq $8,%rsp
	ret
    # 21. End:
F0_End:
