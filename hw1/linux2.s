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

# Program 2: Number of spaces in the image.
# From the previous program, we know the number of pixels in the image.
# To calculate the number of spaces, we can loop through the pixels
# in the image and test to see if it is a space (ascii 32).
#
# Procedure:
#   read a memory location into a register
#   compare that register to 32
#   if it is 32, jump to increment
#   else loop to look at next memory location

########################################################################
#                   Program 1
########################################################################

# Just grab the contents of %rdi and %rdi+4 and multiply them together.

        movl    (%edi), %eax    # put the width into %eax
        addq    $4, %rdi
        movl    (%edi), %ebx    # put the height into %ebx
        mull    %ebx            # multiply h x w and store in %eax

# Store the first result
        movl    %eax, %ebx      # First area is stored in %ebx

# Then repeat for %rsi
        movl    (%esi), %eax    # put the width into %eax
        addq    $4, %rsi
        movl    (%esi), %ecx    # put the height into %ecx
        mull    %ecx            # multiply h x w and store in %eax

# Store the second result
        movl    %eax, %ecx      # Second area is stored in %r15d

# Then add them together
        addl    %ebx, %eax      # Add the two areas together
        movl    %ebx, %eax      # move the result to %eax for use in the return
break:
# At this point,
#	%ebx contains the number of pixels in image 1
# 	%ecx contains the number of pixels in image 2

########################################################################
# Now, loop through the pixels looking for spaces (ASCII 32)
# Image #1
	movl	$0, %r8d	# Initialize the index loop variable (%r8d)
	movl	$0, %r9d	# Initialize the count of spaces (%r9d)
	jmp	test		# Jump to the test section

loop:
	incl	%r8d		# increment the index loop variable
	addq	$4, %rdi	# move to the next pixel
	movl	(%rdi), %edx	# load a pixel from memory
	cmpl	$32, %edx	# Test for a space (ASCII 32)
	jne	test		# repeat if we're not done
	incl	%r9d		# Else increment the space count

test:
	cmpl	%ebx, %r8d	# Test for end of array
	jl	loop		# If pixel is not a space, go to the next one
	movl %r9d, %eax		# Put the number of spaces into the %eax for return
	
# Image #2
	movl	$0, %r8d	# Initialize the index loop variable (%r8d)
	movl	$0, %r9d	# Initialize the count of spaces (%r9d)
	jmp	test2		# Jump to the test section

loop2:
	incl	%r8d		# increment the index loop variable
	addq	$4, %rsi	# move to the next pixel
	movl	(%rsi), %edx	# load a pixel from memory
	cmpl	$32, %edx	# Test for a space (ASCII 32)
	jne	test2		# repeat if we're not done
	incl	%r9d		# Else increment the space count

test2:
	cmpl	%ecx, %r8d	# Test for end of array
	jl	loop2		# If pixel is not a space, go to the next one

	addl %eax, %r9d
	
### This is where your code ends ...

	ret
