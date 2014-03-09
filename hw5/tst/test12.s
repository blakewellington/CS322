	.text
	.globl _class_Body
_class_Body:
	.quad _Body_go
    # _main ()
    # (b)
# Allocation map
# t1	%rbx
# t2	%rax
# t3	%rax
# t4	%rax
# b	%rdi
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
    # 2.   [t1]:P = _class_Body
	leaq _class_Body(%rip),%r10
	movq %r10,(%rbx)
    # 3.   call _init0_Body(t1)
	movq %rbx,%rdi
	call _init0_Body
    # 4.   b = t1
	movq %rbx,%rdi
    # 5.   t2 = [b]:P
	movq (%rdi),%rax
    # 6.   t3 = [t2]:P
	movq (%rax),%rax
    # 7.   t4 = call * t3(b)
	call * %rax
    # 8.   call _printInt(t4)
	movq %rax,%rdi
	call _printInt
    # 9.   return 
	popq %rbx
	ret
    # 10. End:
F0_End:
    # _init0_Body (obj)
# Allocation map
	.p2align 4,0x90
	.globl _init0_Body
_init0_Body:
	subq $8,%rsp
    # 0.  Begin:
F1_Begin:
    # 1.   return 
	addq $8,%rsp
	ret
    # 2.  End:
F1_End:
    # _init_Body (obj)
# Allocation map
	.p2align 4,0x90
	.globl _init_Body
_init_Body:
	subq $8,%rsp
    # 0.  Begin:
F2_Begin:
    # 1.   return 
	addq $8,%rsp
	ret
    # 2.  End:
F2_End:
    # _Body_go (obj)
    # (a)
# Allocation map
# t1	%rax
# t2	%rcx
# t3	%rcx
# t4	%rcx
# t5	%rcx
# t6	%rcx
# t7	%rcx
# a	%rax
# t8	%rcx
	.p2align 4,0x90
	.globl _Body_go
_Body_go:
	subq $8,%rsp
    # 0.  Begin:
F3_Begin:
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
    # 5.   [t3]:I = 1
	movl $1,(%rcx)
    # 6.   t4 = 1 * 4
	movq $1,%rcx
	imulq $4,%rcx
    # 7.   t5 = a + t4
	movq %rcx,%r10
	movq %rax,%rcx
	addq %r10,%rcx
    # 8.   [t5]:I = 2
	movl $2,(%rcx)
    # 9.   t6 = 1 * 4
	movq $1,%rcx
	imulq $4,%rcx
    # 10.  t7 = a + t6
	movq %rcx,%r10
	movq %rax,%rcx
	addq %r10,%rcx
    # 11.  t8 = [t7]:I
	movslq (%rcx),%rcx
    # 12.  return t8
	movq %rcx,%rax
	addq $8,%rsp
	ret
    # 13. End:
F3_End:
