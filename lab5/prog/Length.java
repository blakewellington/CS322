class Length {
  public static void main(String[] x) {
    // initialize array
    int[] a = new int[4];
    a[0] = 5;  
    a[1] = 3;  
    a[2] = 6;  
    a[3] = 0;

    // compute array length
    int i = 0;
    while (a[i] != 0) {
      i = i + 1;
    }
    System.out.println(i);
  }
}

