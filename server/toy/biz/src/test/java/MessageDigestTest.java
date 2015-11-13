import org.junit.Test;

import com.qihao.shared.base.utils.MD5Algorithm;

public class MessageDigestTest {

	@Test
	public void testMD(){
		String str = "123456";
		try {
			System.out.println(MD5Algorithm.digest(str, "1f8b638841b06b291d41"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
