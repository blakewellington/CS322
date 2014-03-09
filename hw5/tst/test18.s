	.text
	.globl _class_A
_class_A:
	.quad _A_go
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
    # 1.   t1 = call _malloc(16)
	movq $16,%rdi
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
    # 7.   t4 = call * t3(a)
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
    # 1.   8[obj]:P = 0
	movq $0,8(%rdi)
    # 2.   return 
	addq $8,%rsp
	ret
    # 3.  End:
F1_End:
    # _init_A (obj, a)
# Allocation map
# a	%rsi
# obj	%rdi
	.p2align 4,0x90
	.globl _init_A
_init_A:
	subq $8,%rsp
    # 0.  Begin:
F2_Begin:
    # 1.   8[obj]:P = a
	movq %rsi,8(%rdi)
    # 2.   return 
	addq $8,%rsp
	ret
    # 3.  End:
F2_End:
    # _A_go (obj)
    # (b)
# Allocation map
# t1	%rax
# t2	%rax
# t3	%rax
# b	%rbp
# t4	%rcx
# t5	%rcx
# t6	%rcx
# t7	%rdx
# t8	%rdx
# t9	%rdx
# t10	%rdx
# t11	%rdx
# t12	%rdx
# t13	%rdx
# t14	%rsi
# t15	%rsi
# t17	%rsi
# t16	%rdi
# t19	%rdi
# t18	%rsi
# t21	%rdi
# t20	%rsi
# t23	%rdi
# t22	%rdi
# obj	%rbx
	.p2align 4,0x90
	.globl _A_go
_A_go:
	pushq %rbx
	pushq %rbp
	subq $8,%rsp
	movq %rdi,%rbx
    # 0.  Begin:
F3_Begin:
    # 1.   t1 = call _malloc(8)
	movq $8,%rdi
	call _malloc
    # 2.   8[obj]:P = t1
	movq %rax,8(%rbx)
    # 3.   t2 = call _malloc(8)
	movq $8,%rdi
	call _malloc
    # 4.   b = t2
	movq %rax,%rbp
    # 5.   t3 = 8[obj]:P
	movq 8(%rbx),%rax
    # 6.   t4 = 0 * 4
	movq $0,%rcx
	imulq $4,%rcx
    # 7.   t5 = t3 + t4
	movq %rcx,%r10
	movq %rax,%rcx
	addq %r10,%rcx
    # 8.   [t5]:I = 1
	movl $1,(%rcx)
    # 9.   t6 = 8[obj]:P
	movq 8(%rbx),%rcx
    # 10.  t7 = 1 * 4
	movq $1,%rdx
	imulq $4,%rdx
    # 11.  t8 = t6 + t7
	movq %rdx,%r10
	movq %rcx,%rdx
	addq %r10,%rdx
    # 12.  [t8]:I = 2
	movl $2,(%rdx)
    # 13.  t9 = 0 * 4
	movq $0,%rdx
	imulq $4,%rdx
    # 14.  t10 = b + t9
	movq %rdx,%r10
	movq %rbp,%rdx
	addq %r10,%rdx
    # 15.  [t10]:I = 3
	movl $3,(%rdx)
    # 16.  t11 = 1 * 4
	movq $1,%rdx
	imulq $4,%rdx
    # 17.  t12 = b + t11
	movq %rdx,%r10
	movq %rbp,%rdx
	addq %r10,%rdx
    # 18.  [t12]:I = 4
	movl $4,(%rdx)
    # 19.  t13 = 8[obj]:P
	movq 8(%rbx),%rdx
    # 20.  t14 = 1 * 4
	movq $1,%rsi
	imulq $4,%rsi
    # 21.  t15 = t13 + t14
	movq %rsi,%r10
	movq %rdx,%rsi
	addq %r10,%rsi
    # 22.  t16 = [t15]:I
	movslq (%rsi),%rdi
    # 23.  call _printInt(t16)
	call _printInt
    # 24.  t17 = 1 * 4
	movq $1,%rsi
	imulq $4,%rsi
    # 25.  t18 = b + t17
	movq %rsi,%r10
	movq %rbp,%rsi
	addq %r10,%rsi
    # 26.  t19 = [t18]:I
	movslq (%rsi),%rdi
    # 27.  call _printInt(t19)
	call _printInt
    # 28.  t20 = 8[obj]:P
	movq 8(%rbx),%rsi
    # 29.  t21 = 0 * 4
	movq $0,%rdi
	imulq $4,%rdi
    # 30.  t22 = t20 + t21
	movq %rdi,%r10
	movq %rsi,%rdi
	addq %r10,%rdi
    # 31.  t23 = [t22]:I
	movslq (%rdi),%rdi
    # 32.  return t23
	movq %rdi,%rax
	addq $8,%rsp
	popq %rbp
	popq %rbx
	ret
    # 33. End:
F3_End:
