package android.hook;

/**
 * lớp quy định các kiểu gõ thông qua biến đàu vào
 * dùng để xác định kiểu gõ cho hiển thị trên văn bản
 * 
 * @author LamPT
 *
 */
public class InputMethodFactory {
	 /**
	 * @param inputMethod là kiều nhập được xác định từ người dùng
	 * 
	 * @return phương thức nhập được khởi tạo tương ứng
	 */
	public static InputMethod createInputMethod(InputMethods inputMethod) {
	        InputMethod im;
	        
	        if (inputMethod == InputMethods.VNI) {
	            im = new VniIM();
	        } else if (inputMethod == InputMethods.VIQR) {
	            im = new ViqrIM();
	        } else if (inputMethod == InputMethods.Auto) {
	            im = new AutoIM();        
	        } else {
	            im = new TelexIM();
	        }
	        
	        return im;
	    }

}
