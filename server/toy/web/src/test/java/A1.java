
public class A1 {

	public static void main(String[] args) {
		 boolean b1 =  Integer.valueOf(127)==Integer.valueOf(127); // true
         boolean b2 = Integer.valueOf(128)==Integer.valueOf(128) ; // false

         boolean b3 =  Integer.valueOf(-128)==Integer.valueOf(-128); // true
         boolean b4 = Integer.valueOf(-129)==Integer.valueOf(-129) ; // false
         
         boolean c1 = new Integer(127) == Integer.valueOf(127);
         boolean c2 = new Integer(128) == 128;
         boolean c3 = 128 == Integer.valueOf(128);
         
	}

}
