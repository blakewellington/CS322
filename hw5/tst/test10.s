	.text
	.globl _class_Body
_class_Body:
	.quad _Body_go
	.globl _class_Body2
_class_Body2:
	.quad _Body2_value
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
    # (b)
# Allocation map
# t1	%rbx
# t2	%rax
# t3	%rax
# t4	%rax
# b	%rdi
	.p2align 4,0x90
	.globl _Body_go
_Body_go:
	pushq %rbx
    # 0.  Begin:
F3_Begin:
    # 1.   t1 = call _malloc(12)
	movq $12,%rdi
	call _malloc
	movq %rax,%rbx
    # 2.   [t1]:P = _class_Body2
	leaq _class_Body2(%rip),%r10
	movq %r10,(%rbx)
    # 3.   call _init0_Body2(t1)
	movq %rbx,%rdi
	call _init0_Body2
    # 4.   b = t1
	movq %rbx,%rdi
    # 5.   t2 = [b]:P
	movq (%rdi),%rax
    # 6.   t3 = [t2]:P
	movq (%rax),%rax
    # 7.   t4 = call * t3(b, true)
	movq $1,%rsi
	call * %rax
    # 8.   return t4
	popq %rbx
	ret
    # 9.  End:
F3_End:
    # _init0_Body2 (obj)
# Allocation map
# obj	%rdi
	.p2align 4,0x90
	.globl _init0_Body2
_init0_Body2:
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
    # _init_Body2 (obj, i)
# Allocation map
# obj	%rdi
# i	%rsi
	.p2align 4,0x90
	.globl _init_Body2
_init_Body2:
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
    # _Body2_value (obj, cond)
    # (j, k)
# Allocation map
# t1	%rcx
# obj	%rdi
# j	%rax
# k	%rcx
# cond	%rsi
	.p2align 4,0x90
	.globl _Body2_value
_Body2_value:
	subq $8,%rsp
    # 0.  Begin:
F6_Begin:
    # 1.   8[obj]:I = 5
	movl $5,8(%rdi)
    # 2.   j = 6
	movq $6,%rax
    # 3.   if cond == false goto L0
	cmpq $0,%rsi
	je F6_L0
    # 4.   t1 = 8[obj]:I
	movslq 8(%rdi),%rcx
    # 5.   k = t1
    # 6.   goto L1
	jmp F6_L1
    # 7.  L0:
F6_L0:
    # 8.   k = j
	movq %rax,%rcx
    # 9.  L1:
F6_L1:
    # 10.  return k
	movq %rcx,%rax
	addq $8,%rsp
	ret
    # 11. End:
F6_End:
