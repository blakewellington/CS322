	.text
	.globl _class_A
_class_A:
	.quad _A_foo
    # _main ()
    # (a, b, i, j)
# Allocation map
# t1	%rbx
# t2	%rax
# t3	%rax
# t4	%rax
# b	%rbx
# t5	%rax
# a	%rdi
# j	%r12
# i	%rbp
	.p2align 4,0x90
	.globl _main
_main:
	pushq %rbx
	pushq %rbp
	pushq %r12
    # 0.  Begin:
F0_Begin:
    # 1.   t1 = call _malloc(8)
	movq $8,%rdi
	call _malloc
	movq %rax,%rbx
    # 2.   [t1]:P = _class_A
	leaq _class_A(%rip),%r10
	movq %r10,(%rbx)
    # 3.   call _init0_A(t1)
	movq %rbx,%rdi
	call _init0_A
    # 4.   a = t1
	movq %rbx,%rdi
    # 5.   b = true
	movq $1,%rbx
    # 6.   t2 = [a]:P
	movq (%rdi),%rax
    # 7.   t3 = [t2]:P
	movq (%rax),%rax
    # 8.   t4 = call * t3(a, 1, 2)
	movq $1,%rsi
	movq $2,%rdx
	call * %rax
    # 9.   i = t4
	movq %rax,%rbp
    # 10.  t5 = 2 * 3
	movq $2,%rax
	imulq $3,%rax
    # 11.  j = t5
	movq %rax,%r12
    # 12.  call _printBool(b)
	movq %rbx,%rdi
	call _printBool
    # 13.  call _printInt(i)
	movq %rbp,%rdi
	call _printInt
    # 14.  call _printInt(j)
	movq %r12,%rdi
	call _printInt
    # 15.  return 
	popq %r12
	popq %rbp
	popq %rbx
	ret
    # 16. End:
F0_End:
    # _init0_A (obj)
# Allocation map
	.p2align 4,0x90
	.globl _init0_A
_init0_A:
	subq $8,%rsp
    # 0.  Begin:
F1_Begin:
    # 1.   return 
	addq $8,%rsp
	ret
    # 2.  End:
F1_End:
    # _init_A (obj)
# Allocation map
	.p2align 4,0x90
	.globl _init_A
_init_A:
	subq $8,%rsp
    # 0.  Begin:
F2_Begin:
    # 1.   return 
	addq $8,%rsp
	ret
    # 2.  End:
F2_End:
    # _A_foo (obj, i, j)
# Allocation map
# t1	%rax
# j	%rdx
# i	%rsi
	.p2align 4,0x90
	.globl _A_foo
_A_foo:
	subq $8,%rsp
    # 0.  Begin:
F3_Begin:
    # 1.   t1 = i + j
	movq %rsi,%rax
	addq %rdx,%rax
    # 2.   return t1
	addq $8,%rsp
	ret
    # 3.  End:
F3_End:
