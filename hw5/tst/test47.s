	.text
	.globl _class_A
_class_A:
	.quad _A_foo
	.quad _A_bar
    # _main ()
    # (a)
# Allocation map
# t1	%rbx
# t2	%rax
# t3	%rax
# t4	%rax
# a	%rdi
	.p2align 4,0x90
	.globl _main
_main:
	pushq %rbx
    # 0.  Begin:
F0_Begin:
    # 1.   t1 = call _malloc(12)
	movq $12,%rdi
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
    # 5.   t2 = [a]:P
	movq (%rdi),%rax
    # 6.   t3 = [t2]:P
	movq (%rax),%rax
    # 7.   t4 = call * t3(a, 2)
	movq $2,%rsi
	call * %rax
    # 8.   call _printInt(t4)
	movq %rax,%rdi
	call _printInt
    # 9.   return 
	popq %rbx
	ret
    # 10. End:
F0_End:
    # _init0_A (obj)
# Allocation map
# obj	%rdi
	.p2align 4,0x90
	.globl _init0_A
_init0_A:
	subq $8,%rsp
    # 0.  Begin:
F1_Begin:
    # 1.   8[obj]:I = 10
	movl $10,8(%rdi)
    # 2.   return 
	addq $8,%rsp
	ret
    # 3.  End:
F1_End:
    # _init_A (obj, k)
# Allocation map
# obj	%rdi
# k	%rsi
	.p2align 4,0x90
	.globl _init_A
_init_A:
	subq $8,%rsp
    # 0.  Begin:
F2_Begin:
    # 1.   8[obj]:I = k
	movl %esi,8(%rdi)
    # 2.   return 
	addq $8,%rsp
	ret
    # 3.  End:
F2_End:
    # _A_foo (obj, i)
# Allocation map
# t1	%rbp
# t2	%rax
# t3	%rax
# t4	%rax
# t5	%rcx
# t6	%rcx
# t7	%rcx
# t8	%rcx
# t9	%rcx
# obj	%rbx
# i	%rsi
	.p2align 4,0x90
	.globl _A_foo
_A_foo:
	pushq %rbx
	pushq %rbp
	subq $8,%rsp
	movq %rdi,%rbx
    # 0.  Begin:
F3_Begin:
    # 1.   if i <= 0 goto L0
	cmpq $0,%rsi
	jle F3_L0
    # 2.   t1 = 8[obj]:I
	movslq 8(%rbx),%rbp
    # 3.   t2 = [obj]:P
	movq (%rbx),%rax
    # 4.   t3 = 8[t2]:P
	movq 8(%rax),%rax
    # 5.   t4 = call * t3(obj, i)
	movq %rbx,%rdi
	call * %rax
    # 6.   t5 = [obj]:P
	movq (%rbx),%rcx
    # 7.   t6 = [t5]:P
	movq (%rcx),%rcx
    # 8.   t7 = call * t6(obj, t4)
	movq %rbx,%rdi
	movq %rax,%rsi
	call * %rcx
	movq %rax,%rcx
    # 9.   t8 = t1 + t7
	movq %rcx,%r10
	movq %rbp,%rcx
	addq %r10,%rcx
    # 10.  8[obj]:I = t8
	movl %ecx,8(%rbx)
    # 11. L0:
F3_L0:
    # 12.  t9 = 8[obj]:I
	movslq 8(%rbx),%rcx
    # 13.  return t9
	movq %rcx,%rax
	addq $8,%rsp
	popq %rbp
	popq %rbx
	ret
    # 14. End:
F3_End:
    # _A_bar (obj, i)
# Allocation map
# t1	%rax
# i	%rsi
	.p2align 4,0x90
	.globl _A_bar
_A_bar:
	subq $8,%rsp
    # 0.  Begin:
F4_Begin:
    # 1.   t1 = i - 1
	movq %rsi,%rax
	subq $1,%rax
    # 2.   return t1
	addq $8,%rsp
	ret
    # 3.  End:
F4_End:
