	.file	"linux.s"
        .text
	.globl	f
f:

### This is where your code begins ...
########################################################################
# 
# Program 5: If the two input images have the same width and height,
#            then the result should be 1 and the first image should
#            be overwritten with a copy of the second image.
#
########################################################################

#       IMAGE 1
        movl    (%edi), %ebx    # put the width into %ebx
        movl    4(%edi), %ecx   # put the height into %ecx

# 	IMAGE 2
	movl	(%esi), %r8d	# put the width into %r8d
	movl	4(%esi), %r9d	# put the height into %r9d

# Check that the widths are the same
	cmpl	%ebx, %r8d	# Check the widths
	jne	notsame
	cmpl	%ecx, %r9d	# Check the heights
	jne	notsame

	addq	$4, %rdi
	addq	$4, %rsi
	movl	%ebx, %eax	# Put the width into %eax
	mull	%ecx 		# Number of pixels in image 1 stored in %eax
	movl	%eax, %ebx	# number of pixels of both images in %ebx

	movl	$0, %r8d	# Initialize the index counter
	movl	$1, %eax	# Set return value to 1 
	jmp	test

loop:
	incl	%r8d		# increment the counter
	addq	$4, %rdi
	addq	$4, %rsi
	movl    (%rsi), %r9d    # load a pixel from memory
#	movl	(%rsi), %edi	# Overwrite the pixel of IMG1 with IMG2
bp:
#	movl	%esi, (%edi)	# Overwrite the pixel of IMG1 with IMG2
	movl	%r9d, (%edi)	# Overwrite the pixel of IMG1 with IMG2

test:
	cmpl	%ebx, %r8d	# Test for more pixels to change
	jle	loop		# loop
	jmp	end		# Go to the end
	

notsame:
	movl	$0, %eax	# Return a zero since they are not the same size

end:
### This is where your code ends ...

	ret
