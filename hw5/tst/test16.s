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
    # _A_go (obj)
    # (a, b, c, x)
# Allocation map
# t1	%rcx
# t2	%rcx
# t3	%rcx
# b	%rcx
# c	%rcx
# a	%rax
# x	%rcx
	.p2align 4,0x90
	.globl _A_go
_A_go:
	subq $8,%rsp
    # 0.  Begin:
F3_Begin:
    # 1.   a = true
	movq $1,%rax
    # 2.   t1 = !a
	movq %rax,%rcx
	notq %rcx
    # 3.   b = t1
    # 4.   t2 = a && b
	movq %rcx,%r10
	movq %rax,%rcx
	andq %r10,%rcx
    # 5.   t3 = t2 || a
	orq %rax,%rcx
    # 6.   c = t3
    # 7.   if c == false goto L0
	cmpq $0,%rcx
	je F3_L0
    # 8.   x = 1
	movq $1,%rcx
    # 9.   goto L1
	jmp F3_L1
    # 10. L0:
F3_L0:
    # 11.  x = 0
	movq $0,%rcx
    # 12. L1:
F3_L1:
    # 13.  return x
	movq %rcx,%rax
	addq $8,%rsp
	ret
    # 14. End:
F3_End: