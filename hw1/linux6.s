	.file	"linux.s"
        .text
	.globl	f
f:

### This is where your code begins ...
########################################################################
# 
# Program 6: If the two input images have the same width and height,
# 	   then the first image should be modified to contain a
# 	   "merged" version of the two images.  Each pixel in the
# 	   modified image will be the same as the corresponding pixel
# 	   in either the first or the second image; the pixel from the
# 	   first image will only be used if the pixel from the second
# 	   image is a space.   For example, given the following pair
# 	   of images:
# 
#               abcdef          A CD F
#               ghijkl   and     H  K 
#               mnopqr          M OP R
#
#          then the first image would be modified to contain:
#
#               AbCDeF
#               gHijKl
#               MnOPqR
#
#	   In addition, the final output (in eax) should contain the
#	   number of characters/pixels that have been copied across
#	   from the second image (i.e., the number of non-space
#	   characters in the second image).  For the example above,
#	   the final result would be 10.
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
# If they are the same...
	addq	$4, %rdi	# Fetch the next pixel from IMG1
	addq	$4, %rsi	# Fetch the next pixel from IMG2
# Calculate the total number of pixels in the image
	movl	%ebx, %eax	# Put the width into %eax
	mull	%ecx 		# Number of pixels in image 1 stored in %eax
	movl	%eax, %ebx	# number of pixels of both images in %ebx

	movl	$0, %r8d	# Initialize the index counter (%r8d)
	movl	$0, %ecx	# Initialize the replaced-pixel counter (%ecx)

# Iterate through the pixels of both images
	jmp	test
loop:
	incl	%r8d		# increment the counter
	addq	$4, %rdi	# Fetch the next pixel from IMG1
	addq	$4, %rsi	# Fetch the next pixel from IMG2

# If the pixel in image2 is not a space (ASCII 32) don't overwrite image1 pixel
        movl    (%rsi), %edx    # load a pixel from IMG2
        cmpl    $32, %edx       # Test for a space (ASCII 32)
        je      test            # If it is a space, go to next pixel

# Else, overwrite it
        incl    %ecx            # Else increment replaced-pixel counter
	movl	%edx, (%edi)	# Overwrite the pixel of IMG1 with IMG2

test:
	cmpl	%ebx, %r8d	# Test for more pixels to change
	jle	loop		# loop
	jmp	end		# Go to the end
	

notsame:
	movl	$0, %eax	# Return a zero since they are not the same size

end:
### This is where your code ends ...

	ret
