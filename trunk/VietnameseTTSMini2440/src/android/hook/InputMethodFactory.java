package android.hook;

public class InputMethodFactory {
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
