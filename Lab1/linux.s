	.file	"example1.s"
	.text
	.globl	f
f:
### This is where your code begins ...

	movl	$0, %eax	# initialize length to 0
top:	
	movl	(%rdi), %esi	# fetch current element
	cmpl	$0, %esi	# check for end of array (containing a 0)
	je	done		
	incl	%eax		# increment count
	addq	$4, %rdi	# advance current pointer
	jmp	top		# loop
done:
### This is where your code ends ...
	ret
