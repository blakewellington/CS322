	.text
	.globl _class_A
_class_A:
	.quad _A_back
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
    # 5.   t2 = [a]:P
	movq (%rdi),%rax
    # 6.   t3 = 8[t2]:P
	movq 8(%rax),%rax
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
    # _A_back (obj, a)
# Allocation map
# a	%rax
	.p2align 4,0x90
	.globl _A_back
_A_back:
	subq $8,%rsp
	movq %rsi,%rax
    # 0.  Begin:
F3_Begin:
    # 1.   return a
	addq $8,%rsp
	ret
    # 2.  End:
F3_End:
    # _A_go (obj)
# Allocation map
# t1	%rax
# t2	%rax
# t3	%rax
# obj	%rdi
	.p2align 4,0x90
	.globl _A_go
_A_go:
	subq $8,%rsp
    # 0.  Begin:
F4_Begin:
    # 1.   t1 = [obj]:P
	movq (%rdi),%rax
    # 2.   t2 = [t1]:P
	movq (%rax),%rax
    # 3.   t3 = call * t2(obj, 2)
	movq $2,%rsi
	call * %rax
    # 4.   return t3
	addq $8,%rsp
	ret
    # 5.  End:
F4_End:
