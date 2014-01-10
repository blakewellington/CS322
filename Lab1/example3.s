	.file	"example3.s"
	.text
	.globl	f
f:
### This is where your code begins ...

	movl	$0, %eax		# initialze index
	movl	$0, %edx		# initialize max
	movl 	$0, %ecx		# %ecx will contain the index of max
top:
	movl	(%rdi), %esi		# fetch current element
	cmpl	$0, %esi		# Loop while not zero
	je	done
	cmpl	%edx, %esi		# compare new element to max
	jg	larger
	addq	$4, %rdi		# increment the pointer
	inc	%ecx			# increment the index
	jmp	top			# loop

larger:
	movl	%esi, %edx		# update max
	movl	%ecx, %eax
	jmp 	top

done:
### This is where your code ends ...

	ret
