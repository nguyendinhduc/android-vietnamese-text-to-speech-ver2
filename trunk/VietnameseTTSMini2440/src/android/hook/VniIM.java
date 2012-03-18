package android.hook;

public class VniIM implements InputMethod {  
    @Override
    public char getAccentMark(char keyChar, char curChar, String curWord) {
        char accent = '\0';
        
        if (Character.isDigit(keyChar)) {
            accent = keyChar;
        }
        
        return accent;
    }
}
