	.file	"example1.s"
	.text
	.globl	f
f:
### This is where your code begins ...

	movl	$1, %ecx 	# Initialize the length
	movl	(%rdi), %eax 	# Initialize the sum
top:
	addq	$4, %rdi	# Increment the pointer
	movl	(%rdi), %edx	# Fetch the current element into %edx
	cmpl	$0, %edx	# check for 0 (end of array)
	je	done

	incl	%ecx		# Increment the length
	addl	%edx, %eax	# Add the element to the sum
	jmp	top		# loop

done:
	cltd			# Convert long to double
	idivl	%ecx		# Divide the sum by the length
### This is where your code ends ...

	ret
