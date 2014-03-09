	.text
	.globl _class_Body
_class_Body:
	.quad _Body_foo
    # _main ()
    # (i, b)
# Allocation map
# t1	%rbx
# t2	%rax
# t3	%rax
# t4	%rax
# b	%rdi
# i	%rax
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
    # 8.   i = t4
    # 9.   call _printInt(i)
	movq %rax,%rdi
	call _printInt
    # 10.  return 
	popq %rbx
	ret
    # 11. End:
F0_End:
    # _init0_Body (obj)
# Allocation map
# obj	%rdi
	.p2align 4,0x90
	.globl _init0_Body
_init0_Body:
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
    # _init_Body (obj, x)
# Allocation map
# obj	%rdi
# x	%rsi
	.p2align 4,0x90
	.globl _init_Body
_init_Body:
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
    # _Body_foo (obj)
# Allocation map
# t1	%rax
# obj	%rdi
	.p2align 4,0x90
	.globl _Body_foo
_Body_foo:
	subq $8,%rsp
    # 0.  Begin:
F3_Begin:
    # 1.   8[obj]:I = 1
	movl $1,8(%rdi)
    # 2.   t1 = 8[obj]:I
	movslq 8(%rdi),%rax
    # 3.   return t1
	addq $8,%rsp
	ret
    # 4.  End:
F3_End:
