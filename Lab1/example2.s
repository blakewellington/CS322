	.file	"example2.s"
	.text
	.globl	f
f:
### This is where your code begins ...

	movl	$0, %eax		# initialze length
	movl	$0, %edx		# initialize max
top:
	movl	(%rdi), %esi		# fetch current element
	cmpl	$0, %esi		# Loop while not zero
	je	done
	cmpl	%edx, %esi		# compare new element to max
	jg	larger
	incl	%eax			# increment length
	addq	$4, %rdi		# increment the pointer
	jmp	top			# loop

larger:
	movl	%esi, %edx		# update max
	jmp 	top

done:
	movl	%edx, %eax		# move the max to the return register
### This is where your code ends ...

	ret
