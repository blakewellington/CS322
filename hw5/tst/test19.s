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
    # 1.   t1 = call _malloc(13)
	movq $13,%rdi
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
    # 1.   8[obj]:B = false
	movb $0,8(%rdi)
    # 2.   9[obj]:I = 0
	movl $0,9(%rdi)
    # 3.   return 
	addq $8,%rsp
	ret
    # 4.  End:
F1_End:
    # _init_A (obj, b, i)
# Allocation map
# b	%rsi
# obj	%rdi
# i	%rdx
	.p2align 4,0x90
	.globl _init_A
_init_A:
	subq $8,%rsp
    # 0.  Begin:
F2_Begin:
    # 1.   8[obj]:B = b
	movb %sil,8(%rdi)
    # 2.   9[obj]:I = i
	movl %edx,9(%rdi)
    # 3.   return 
	addq $8,%rsp
	ret
    # 4.  End:
F2_End:
    # _A_go (obj)
    # (a)
# Allocation map
# t1	%rax
# t2	%rax
# t3	%rcx
# t4	%rdx
# t5	%rdx
# t6	%rdx
# t7	%rdx
# t8	%rdx
# t9	%rsi
# t10	%rsi
# t11	%rsi
# t12	%rbp
# t13	%rsi
# t14	%rsi
# t15	%rsi
# t17	%rsi
# t16	%rdi
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
    # 1.   t1 = call _malloc(16)
	movq $16,%rdi
	call _malloc
    # 2.   a = t1
    # 3.   t2 = 1 < 2
	movq $2,%r10
	movq $1,%r11
	cmpq %r10,%r11
	setl %al
    # 4.   t3 = 3 > 4
	movq $4,%r10
	movq $3,%r11
	cmpq %r10,%r11
	setg %cl
    # 5.   t4 = 7 * 8
	movq $7,%rdx
	imulq $8,%rdx
    # 6.   t5 = 6 + t4
	movq %rdx,%r10
	movq $6,%rdx
	addq %r10,%rdx
    # 7.   t6 = 5 == t5
	movq $5,%r11
	cmpq %rdx,%r11
	sete %dl
    # 8.   t7 = t3 && t6
	movq %rdx,%r10
	movq %rcx,%rdx
	andq %r10,%rdx
    # 9.   t8 = t2 || t7
	movq %rdx,%r10
	movq %rax,%rdx
	orq %r10,%rdx
    # 10.  t9 = !true
	movq $1,%rsi
	notq %rsi
    # 11.  t10 = t8 || t9
	movq %rsi,%r10
	movq %rdx,%rsi
	orq %r10,%rsi
    # 12.  8[obj]:B = t10
	movb %sil,8(%rbx)
    # 13.  t11 = 8 - 7
	movq $8,%rsi
	subq $7,%rsi
    # 14.  t12 = t11 + 6
	movq %rsi,%rbp
	addq $6,%rbp
    # 15.  t13 = 5 * 4
	movq $5,%rsi
	imulq $4,%rsi
    # 16.  t14 = t13 / 2
	movq $2,%r10
	movq %rsi,%rax
	cqto
	idivq %r10
	movq %rax,%rsi
    # 17.  t15 = t12 + t14
	movq %rsi,%r10
	movq %rbp,%rsi
	addq %r10,%rsi
    # 18.  9[obj]:I = t15
	movl %esi,9(%rbx)
    # 19.  t16 = 8[obj]:B
	movzbq 8(%rbx),%rdi
    # 20.  call _printBool(t16)
	call _printBool
    # 21.  t17 = 9[obj]:I
	movslq 9(%rbx),%rsi
    # 22.  return t17
	movq %rsi,%rax
	addq $8,%rsp
	popq %rbp
	popq %rbx
	ret
    # 23. End:
F3_End:
