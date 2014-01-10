	.file	"example1.s"
	.text
	.globl	f
f:
### This is where your code begins ...

	movl	$0, %eax
top:
	movl	(%rdi), %esi
	cmpl	$0, %esi
	je	done
	incl	%eax
	addq	$4, %rdi
	jmp	top

done:
### This is where your code ends ...

	ret
