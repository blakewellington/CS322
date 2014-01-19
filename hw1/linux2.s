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




	movl	(%edi), %eax	# put the width into %eax
	addq	$4, %rdi		
	movl	(%edi), %ebx	# put the height into %ebx
	mull	%ebx		# multiply h x w and store in %eax
	movl	(%eax), %ecx	# The number of pixels is stored in %ecx.
#	movl	$0, %edx	# Initialize the count

# Variables:

# Example:

#        movl    $0, %eax        # initialize length count in eax
#        jmp     test
#loop:   incl    %eax            # increment count
#        addq    $4, %rdi        # and move to next array element
#
#test:   movl    (%rdi), %ecx    # load array element
#        cmpl    $0, %ecx        # test for end of array
#        jne     loop            # repeat if we're not done ...

#top:
# count %edx
#	cmpl	%edx, %ecx	# Compare count to the number of pixels
#	je	done		# If count == pixels, jump to done
#	incl	%edx		# increment count
#	jmp top			# loop

#done:

	
### This is where your code ends ...

	ret
