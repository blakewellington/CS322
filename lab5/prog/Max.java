class Max {
  public static void main(String[] x) {
    // initialize array
    int[] a = new int[4];
    a[0] = 5;  
    a[1] = 3;  
    a[2] = 6;  
    a[0] = 0;

    // return largest element
    int max = a[0];
    int i = 1;
    while (a[i] != 0) {
      if (a[i] > max)
	max = a[i];
      i = i + 1;
    }
    // return max;
  }
}
