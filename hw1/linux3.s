	.file	"linux.s"
        .text
	.globl	f
f:

### This is where your code begins ...

### General Notes to myself about this code:
# The data starts in (%rdi) and continues on in 4 byte increments.
# The first datum is the width of the image: (%rdi)
# The second datum is the height of the image: 4 bytes later.
# I can read the second datum by adding 4 to the %rdi register.
# Subsequent characters can be read similarly by incrementing the %rdi register:
# 	addq	$4, %rdi

########################################################################
#                   Program 3
# 
# Return 1 and rotate image 1 180 degrees.
#
########################################################################

        movl    (%edi), %ebx    # put the width into %ebx
        movl    4(%edi), %ecx    # put the height into %ecx
	movl	%ecx, %eax
	mull	%ebx		# Multiply the h x w and store in %eax
	movl	%eax, %ebx

	movl	$1, %r8d	# Initialize the pixel counter

	addq	$8, %rdi	# set the %rdi pointer to the first pixel
	movl	%edi, %r9d	# Store the beginning memory location in %r9d

# Push all the pixels onto the stack
	jmp	pushtest

pushloop:
	incl	%r8d		# increment the pixel counter
	addq	$4, %rdi	# move to the next pixel
	pushq	(%rdi)		# Push a pixel onto the stack

pushtest:
	cmpl	%ebx, %r8d	# Test if end of loop
	jl	pushloop	# loop

# Pop all values off the stack and back into memory
	movl	$1, %r8d	# Initialize the pixel counter
	movl	%r9d, %edi	# reset %rdi to point to the first memory loc.
	jmp	poptest

poploop:
	popq	(%rdi)		# Pop a pixel off the stack
	incl	%r8d		# increment the pixel counter
	addq	$4, %rdi	# move to the next pixel

poptest:
	cmpl	%eax, %r8d	# Test if end of loop
	jl	poploop		# loop


	movl	$1, %eax
### This is where your code ends ...

	ret
