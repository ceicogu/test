import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


public class TestRef { 

    public static void main(String args[]) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException { 
        Foo foo = new Foo("这个一个Foo对象！"); 
        Class clazz = foo.getClass(); 
        Method m1 = clazz.getDeclaredMethod("outInfo"); 
        Method m2 = clazz.getDeclaredMethod("setMsg", String.class); 
        Method m3 = clazz.getDeclaredMethod("getMsg"); 
        Method m4 = clazz.getDeclaredMethod("setId", Long.class); 
        Method m5 = clazz.getDeclaredMethod("getId"); 
        m1.invoke(foo); 
        m2.invoke(foo, "重新设置msg信息！"); 
        
        String msg = (String) m3.invoke(foo); 
        System.out.println(msg); 
        m4.invoke(foo, 102L);
        Long id = (Long) m5.invoke(foo); 
        System.out.println(id); 
    } 
} 

class Foo { 
    private String msg; 
    private Long id;
    
    public Foo(String msg) { 
        this.msg = msg; 
    } 

    public void setMsg(String msg) { 
        this.msg = msg; 
    } 

    public String getMsg() { 
        return msg; 
    } 

    public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void outInfo() { 
        System.out.println("这是测试Java反射的测试类"); 
    } 
}