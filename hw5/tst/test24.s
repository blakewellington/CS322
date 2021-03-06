	.text
	.globl _class_A
_class_A:
	.quad _A_foo
	.globl _class_B
_class_B:
	.quad _B_foo
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
    # 5.   t2 = call _malloc(12)
	movq $12,%rdi
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
    # 10.  t4 = [t3]:P
	movq (%rax),%rax
    # 11.  t5 = call * t4(a, 1)
	movq %rbx,%rdi
	movq $1,%rsi
	call * %rax
    # 12.  i = t5
	movq %rax,%r12
    # 13.  t6 = [b]:P
	movq (%rbp),%rax
    # 14.  t7 = [t6]:P
	movq (%rax),%rax
    # 15.  t8 = call * t7(b, 1)
	movq %rbp,%rdi
	movq $1,%rsi
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
    # 1.   8[obj]:I = 0
	movl $0,8(%rdi)
    # 2.   return 
	addq $8,%rsp
	ret
    # 3.  End:
F1_End:
    # _init_A (obj, i)
# Allocation map
# obj	%rdi
# i	%rsi
	.p2align 4,0x90
	.globl _init_A
_init_A:
	subq $8,%rsp
    # 0.  Begin:
F2_Begin:
    # 1.   8[obj]:I = i
	movl %esi,8(%rdi)
    # 2.   return 
	addq $8,%rsp
	ret
    # 3.  End:
F2_End:
    # _A_foo (obj, i)
    # (y)
# Allocation map
# t1	%rax
# i	%rsi
	.p2align 4,0x90
	.globl _A_foo
_A_foo:
	subq $8,%rsp
    # 0.  Begin:
F3_Begin:
    # 1.   t1 = i + 1
	movq %rsi,%rax
	addq $1,%rax
    # 2.   return t1
	addq $8,%rsp
	ret
    # 3.  End:
F3_End:
    # _init0_B (obj)
# Allocation map
# obj	%rdi
	.p2align 4,0x90
	.globl _init0_B
_init0_B:
	subq $8,%rsp
    # 0.  Begin:
F4_Begin:
    # 1.   8[obj]:I = 0
	movl $0,8(%rdi)
    # 2.   return 
	addq $8,%rsp
	ret
    # 3.  End:
F4_End:
    # _init_B (obj, i)
# Allocation map
# obj	%rdi
# i	%rsi
	.p2align 4,0x90
	.globl _init_B
_init_B:
	subq $8,%rsp
    # 0.  Begin:
F5_Begin:
    # 1.   8[obj]:I = i
	movl %esi,8(%rdi)
    # 2.   return 
	addq $8,%rsp
	ret
    # 3.  End:
F5_End:
    # _B_foo (obj, i)
# Allocation map
# i	%rax
	.p2align 4,0x90
	.globl _B_foo
_B_foo:
	subq $8,%rsp
	movq %rsi,%rax
    # 0.  Begin:
F6_Begin:
    # 1.   return i
	addq $8,%rsp
	ret
    # 2.  End:
F6_End:
