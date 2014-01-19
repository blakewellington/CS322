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

# Program 1: Number of pixels in the images.
# Just grab the contents of %rdi and %rdi+4 and multiply them together.

	movl	(%edi), %eax	# put the width into %eax
	addq	$4, %rdi		
	movl	(%edi), %ebx	# put the height into %ebx
	mull	%ebx		# multiply h x w and store in %eax

# Store the first result
	movl	%eax, %ebx	# First area is stored in %ebx

# Then repeat for %rsi
	movl	(%esi), %eax	# put the width into %eax
	addq	$4, %rsi		
	movl	(%esi), %ecx	# put the height into %ecx
	mull	%ecx		# multiply h x w and store in %eax

# Store the second result
	movl	%eax, %ecx	# First area is stored in %ebx

# Then add them together
	addl	%ecx, %ebx	# Add the two areas together
	movl	%ebx, %eax	# move the result to %eax for use in the return
### This is where your code ends ...

	ret
