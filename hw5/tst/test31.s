	.text
	.globl _class_A
_class_A:
	.quad _A_f
	.quad _A_g
	.globl _class_B
_class_B:
	.quad _B_f
	.quad _A_g
    # _main ()
    # (a, b, i, j)
# Allocation map
# t1	%rbx
# t2	%rbp
# t3	%rax
# t4	%rax
# b	%rbp
# t5	%rax
# t6	%rax
# t7	%rax
# a	%rbx
# t8	%rax
# j	%r13
# i	%r12
	.p2align 4,0x90
	.globl _main
_main:
	pushq %rbx
	pushq %rbp
	pushq %r12
	pushq %r13
	subq $8,%rsp
    # 0.  Begin:
F0_Begin:
    # 1.   t1 = call _malloc(16)
	movq $16,%rdi
	call _malloc
	movq %rax,%rbx
    # 2.   [t1]:P = _class_B
	leaq _class_B(%rip),%r10
	movq %r10,(%rbx)
    # 3.   call _init0_B(t1)
	movq %rbx,%rdi
	call _init0_B
    # 4.   a = t1
    # 5.   t2 = call _malloc(16)
	movq $16,%rdi
	call _malloc
	movq %rax,%rbp
    # 6.   [t2]:P = _class_B
	leaq _class_B(%rip),%r10
	movq %r10,(%rbp)
    # 7.   call _init0_B(t2)
	movq %rbp,%rdi
	call _init0_B
    # 8.   b = t2
    # 9.   t3 = [a]:P
	movq (%rbx),%rax
    # 10.  t4 = 8[t3]:P
	movq 8(%rax),%rax
    # 11.  t5 = call * t4(a, 2)
	movq %rbx,%rdi
	movq $2,%rsi
	call * %rax
    # 12.  i = t5
	movq %rax,%r12
    # 13.  t6 = [b]:P
	movq (%rbp),%rax
    # 14.  t7 = 8[t6]:P
	movq 8(%rax),%rax
    # 15.  t8 = call * t7(b, 2)
	movq %rbp,%rdi
	movq $2,%rsi
	call * %rax
    # 16.  j = t8
	movq %rax,%r13
    # 17.  call _printInt(i)
	movq %r12,%rdi
	call _printInt
    # 18.  call _printInt(j)
	movq %r13,%rdi
	call _printInt
    # 19.  return 
	addq $8,%rsp
	popq %r13
	popq %r12
	popq %rbp
	popq %rbx
	ret
    # 20. End:
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
    # 1.   8[obj]:I = 1
	movl $1,8(%rdi)
    # 2.   return 
	addq $8,%rsp
	ret
    # 3.  End:
F1_End:
    # _init_A (obj, x)
# Allocation map
# obj	%rdi
# x	%rsi
	.p2align 4,0x90
	.globl _init_A
_init_A:
	subq $8,%rsp
    # 0.  Begin:
F2_Begin:
    # 1.   8[obj]:I = x
	movl %esi,8(%rdi)
    # 2.   return 
	addq $8,%rsp
	ret
    # 3.  End:
F2_End:
    # _A_f (obj, i)
# Allocation map
# t1	%rax
# i	%rsi
	.p2align 4,0x90
	.globl _A_f
_A_f:
	subq $8,%rsp
    # 0.  Begin:
F3_Begin:
    # 1.   t1 = i - 1
	movq %rsi,%rax
	subq $1,%rax
    # 2.   return t1
	addq $8,%rsp
	ret
    # 3.  End:
F3_End:
    # _A_g (obj, i)
# Allocation map
# t1	%rax
# t2	%rax
# t3	%rax
# obj	%rdi
# i	%rsi
	.p2align 4,0x90
	.globl _A_g
_A_g:
	subq $8,%rsp
    # 0.  Begin:
F4_Begin:
    # 1.   t1 = [obj]:P
	movq (%rdi),%rax
    # 2.   t2 = [t1]:P
	movq (%rax),%rax
    # 3.   t3 = call * t2(obj, i)
	call * %rax
    # 4.   return t3
	addq $8,%rsp
	ret
    # 5.  End:
F4_End:
    # _init0_B (obj)
# Allocation map
# obj	%rdi
	.p2align 4,0x90
	.globl _init0_B
_init0_B:
	subq $8,%rsp
    # 0.  Begin:
F5_Begin:
    # 1.   12[obj]:I = 2
	movl $2,12(%rdi)
    # 2.   return 
	addq $8,%rsp
	ret
    # 3.  End:
F5_End:
    # _init_B (obj, x)
# Allocation map
# obj	%rdi
# x	%rsi
	.p2align 4,0x90
	.globl _init_B
_init_B:
	subq $8,%rsp
    # 0.  Begin:
F6_Begin:
    # 1.   12[obj]:I = x
	movl %esi,12(%rdi)
    # 2.   return 
	addq $8,%rsp
	ret
    # 3.  End:
F6_End:
    # _B_f (obj, i)
# Allocation map
# i	%rax
	.p2align 4,0x90
	.globl _B_f
_B_f:
	subq $8,%rsp
	movq %rsi,%rax
    # 0.  Begin:
F7_Begin:
    # 1.   return i
	addq $8,%rsp
	ret
    # 2.  End:
F7_End:
