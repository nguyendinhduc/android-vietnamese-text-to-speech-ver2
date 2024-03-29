package android.hook;

/**
 * lớp quy định cách thức nhập liệu cho kiểu gõ VNI
 * các kí tự nhập vào là kiểu nhập chuẩn của lớp VietInput
 * @author LamPT
 *
 */
public class VniIM implements InputMethod { 
	/** 
	 * ghi đè từ phương thức của interface InputMethod
	 * hàm nhập kí tự dấu tương ứng kiểu gõ VNI
	 * 
	 * @keyChar kí tự nhập vào
	 * @curChar kí tự tại vị trí hiện tại của con trỏ
	 * @curWord từ xác định tại vị trí hiện tại của con trỏ
	 * @return trả về kí tự dấu tương ứng với kiểu VNI
	 */
    @Override
    public char getAccentMark(char keyChar, char curChar, String curWord) {
        char accent = '\0';
        
        if (Character.isDigit(keyChar)) {
            accent = keyChar;
        }
        
        return accent;
    }
}
