	.text
	.globl _class_A
_class_A:
	.quad _A_selectionSort
    # _main ()
    # (a, numbers, cnt)
# Allocation map
# t1	%rbx
# t2	%rax
# t3	%rax
# t4	%rcx
# cnt	%r12
# t5	%rcx
# numbers	%rbp
# t6	%rcx
# t7	%rcx
# a	%rbx
# t8	%rcx
# t9	%rcx
# t10	%rcx
# t11	%rdi
# t12	%rcx
	.p2align 4,0x90
	.globl _main
_main:
	pushq %rbx
	pushq %rbp
	pushq %r12
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
    # 5.   t2 = call _malloc(40)
	movq $40,%rdi
	call _malloc
    # 6.   numbers = t2
	movq %rax,%rbp
    # 7.   cnt = 0
	movq $0,%r12
    # 8.  L0:
F0_L0:
    # 9.   if cnt >= 10 goto L1
	cmpq $10,%r12
	jge F0_L1
    # 10.  t3 = 10 - cnt
	movq $10,%rax
	subq %r12,%rax
    # 11.  t4 = cnt * 4
	movq %r12,%rcx
	imulq $4,%rcx
    # 12.  t5 = numbers + t4
	movq %rcx,%r10
	movq %rbp,%rcx
	addq %r10,%rcx
    # 13.  [t5]:I = t3
	movl %eax,(%rcx)
    # 14.  t6 = cnt + 1
	movq %r12,%rcx
	addq $1,%rcx
    # 15.  cnt = t6
	movq %rcx,%r12
    # 16.  goto L0
	jmp F0_L0
    # 17. L1:
F0_L1:
    # 18.  t7 = [a]:P
	movq (%rbx),%rcx
    # 19.  t8 = [t7]:P
	movq (%rcx),%rcx
    # 20.  call * t8(a, numbers, cnt)
	movq %rbx,%rdi
	movq %rbp,%rsi
	movq %r12,%rdx
	call * %rcx
    # 21.  call _printStr("Your numbers in sorted order are:")
	leaq _S0(%rip),%rdi
	call _printStr
    # 22.  cnt = 0
	movq $0,%r12
    # 23. L2:
F0_L2:
    # 24.  if cnt >= 10 goto L3
	cmpq $10,%r12
	jge F0_L3
    # 25.  t9 = cnt * 4
	movq %r12,%rcx
	imulq $4,%rcx
    # 26.  t10 = numbers + t9
	movq %rcx,%r10
	movq %rbp,%rcx
	addq %r10,%rcx
    # 27.  t11 = [t10]:I
	movslq (%rcx),%rdi
    # 28.  call _printInt(t11)
	call _printInt
    # 29.  t12 = cnt + 1
	movq %r12,%rcx
	addq $1,%rcx
    # 30.  cnt = t12
	movq %rcx,%r12
    # 31.  goto L2
	jmp F0_L2
    # 32. L3:
F0_L3:
    # 33.  return 
	popq %r12
	popq %rbp
	popq %rbx
	ret
    # 34. End:
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
    # _A_selectionSort (obj, A, count)
    # (temp, i, j, k)
# Allocation map
# t1	%rax
# t2	%rdi
# t3	%rdi
# count	%rdx
# t4	%rdi
# A	%rsi
# t5	%r8
# t6	%r8
# t7	%r8
# t8	%r8
# t9	%rcx
# t10	%rcx
# t11	%rcx
# t12	%r8
# j	%rcx
# t13	%r8
# k	%rdx
# t14	%r8
# t15	%rdx
# i	%rax
# t17	%rdx
# t16	%rdx
# t19	%rdx
# t18	%rdx
# temp	%rcx
	.p2align 4,0x90
	.globl _A_selectionSort
_A_selectionSort:
	subq $8,%rsp
    # 0.  Begin:
F3_Begin:
    # 1.   t1 = count - 1
	movq %rdx,%rax
	subq $1,%rax
    # 2.   i = t1
    # 3.  L4:
F3_L4:
    # 4.   if i < 0 goto L5
	cmpq $0,%rax
	jl F3_L5
    # 5.   j = 0
	movq $0,%rcx
    # 6.   k = 0
	movq $0,%rdx
    # 7.  L6:
F3_L6:
    # 8.   if j > i goto L7
	cmpq %rax,%rcx
	jg F3_L7
    # 9.   t2 = j * 4
	movq %rcx,%rdi
	imulq $4,%rdi
    # 10.  t3 = A + t2
	movq %rdi,%r10
	movq %rsi,%rdi
	addq %r10,%rdi
    # 11.  t4 = [t3]:I
	movslq (%rdi),%rdi
    # 12.  t5 = k * 4
	movq %rdx,%r8
	imulq $4,%r8
    # 13.  t6 = A + t5
	movq %r8,%r10
	movq %rsi,%r8
	addq %r10,%r8
    # 14.  t7 = [t6]:I
	movslq (%r8),%r8
    # 15.  if t4 <= t7 goto L8
	cmpq %r8,%rdi
	jle F3_L8
    # 16.  k = j
	movq %rcx,%rdx
    # 17. L8:
F3_L8:
    # 18.  t8 = j + 1
	movq %rcx,%r8
	addq $1,%r8
    # 19.  j = t8
	movq %r8,%rcx
    # 20.  goto L6
	jmp F3_L6
    # 21. L7:
F3_L7:
    # 22.  if k == i goto L9
	cmpq %rax,%rdx
	je F3_L9
    # 23.  t9 = k * 4
	movq %rdx,%rcx
	imulq $4,%rcx
    # 24.  t10 = A + t9
	movq %rcx,%r10
	movq %rsi,%rcx
	addq %r10,%rcx
    # 25.  t11 = [t10]:I
	movslq (%rcx),%rcx
    # 26.  temp = t11
    # 27.  t12 = i * 4
	movq %rax,%r8
	imulq $4,%r8
    # 28.  t13 = A + t12
	movq %r8,%r10
	movq %rsi,%r8
	addq %r10,%r8
    # 29.  t14 = [t13]:I
	movslq (%r8),%r8
    # 30.  t15 = k * 4
	imulq $4,%rdx
    # 31.  t16 = A + t15
	movq %rdx,%r10
	movq %rsi,%rdx
	addq %r10,%rdx
    # 32.  [t16]:I = t14
	movl %r8d,(%rdx)
    # 33.  t17 = i * 4
	movq %rax,%rdx
	imulq $4,%rdx
    # 34.  t18 = A + t17
	movq %rdx,%r10
	movq %rsi,%rdx
	addq %r10,%rdx
    # 35.  [t18]:I = temp
	movl %ecx,(%rdx)
    # 36. L9:
F3_L9:
    # 37.  t19 = i - 1
	movq %rax,%rdx
	subq $1,%rdx
    # 38.  i = t19
	movq %rdx,%rax
    # 39.  goto L4
	jmp F3_L4
    # 40. L5:
F3_L5:
    # 41.  return 
	addq $8,%rsp
	ret
    # 42. End:
F3_End:
_S0:
	.asciz "Your numbers in sorted order are:"
