package android.hook;

import java.util.regex.*;

public class VietKeyInput {

    private VietKeyInput() {}
    
    // Viet Unicode Map
    private static final char[][] UNI_DATA = {     
        { 'â',  'a',  'ă',  'ê',  'e',  'i',  'ô',  'o',  'ơ',  'u',  'ư',  'y',  'Â',  'A',  'Ă',  'Ê',  'E',  'I',  'Ô',  'O',  'Ơ',  'U',  'Ư',  'Y',  'd',  'D'},
        { 'ấ',  'á',  'ắ',  'ế',  'é',  'í',  'ố',  'ó',  'ớ',  'ú',  'ứ',  'ý',  'Ấ',  'Á',  'Ắ',  'Ế',  'É',  'Í',  'Ố',  'Ó',  'Ớ',  'Ú',  'Ứ',  'Ý',  'đ',  'Đ'},
        { 'ầ',  'à',  'ằ',  'ề',  'è',  'ì',  'ồ',  'ò',  'ờ',  'ù',  'ừ',  'ỳ',  'Ầ',  'À',  'Ằ',  'Ề',  'È',  'Ì',  'Ồ',  'Ò',  'Ờ',  'Ù',  'Ừ',  'Ỳ'},
        { 'ẩ',  'ả',  'ẳ',  'ể',  'ẻ',  'ỉ',  'ổ',  'ỏ',  'ở',  'ủ',  'ử',  'ỷ',  'Ẩ',  'Ả',  'Ẳ',  'Ể',  'Ẻ',  'Ỉ',  'Ổ',  'Ỏ',  'Ở',  'Ủ',  'Ử',  'Ỷ'},
        { 'ẫ',  'ã',  'ẵ',  'ễ',  'ẽ',  'ĩ',  'ỗ',  'õ',  'ỡ',  'ũ',  'ữ',  'ỹ',  'Ẫ',  'Ã',  'Ẵ',  'Ễ',  'Ẽ',  'Ĩ',  'Ỗ',  'Õ',  'Ỡ',  'Ũ',  'Ữ',  'Ỹ'},
        { 'ậ',  'ạ',  'ặ',  'ệ',  'ẹ',  'ị',  'ộ',  'ọ',  'ợ',  'ụ',  'ự',  'ỵ',  'Ậ',  'Ạ',  'Ặ',  'Ệ',  'Ẹ',  'Ị',  'Ộ',  'Ọ',  'Ợ',  'Ụ',  'Ự',  'Ỵ'}
    };
    
    private static final char ddc = 'Đ';
    private static final char DDc = 'đ';
    private static final char uMoc = 'ư';
    private static final char UMoc = 'Ư';
    private static final char oMoc = 'ơ';
    
    private static final String CMNPT = "cmnpt";
    private static final String DDDD = "DdĐđ";
    private static final String EIOUY = "eiouy";
    private static final String AEOY = "aeoy";   

    // dau duoc xoa tu dong, khong phai khi nhan phim0 de bo dau
    private static boolean accentRemoved; 
    private static boolean diacriticsPosClassicOn;
  
    private static final Pattern vowelpat2 = Pattern.compile("[iy]",Pattern.CASE_INSENSITIVE);
    private static final Pattern xconso  = Pattern.compile("[bcfghjklmnpqrstvwxz0-9]", Pattern.CASE_INSENSITIVE);   //consonants and numbers only
    private static final Pattern vconso2 = Pattern.compile("[bcdfghjklmnpqrstvwxyz0-9]", Pattern.CASE_INSENSITIVE); //consonants and numbers only
    private static final Pattern alpha = Pattern.compile("[a-z]",Pattern.CASE_INSENSITIVE);
    private static final Pattern dblconso = Pattern.compile(".h$|.g$", Pattern.CASE_INSENSITIVE);
    private static final Pattern endDoubleVowels = Pattern.compile(".*oa$|.*oe$|.*uy$", Pattern.CASE_INSENSITIVE);


    //ham tong hop nguyen am
    private static char composeVowel(final int j, final int acc, final int i) {
        char composedvowel;

        if (acc<6) {
            composedvowel = UNI_DATA[acc][j];
        } else if (acc==9 && j>23) {
            composedvowel = UNI_DATA[1][j];
        } else {
            int newpos = j; // character base
            
            if (acc==6) {
                newpos = (j==1||j==4||j==7||j==13||j==16||j==19)? j-1 : ((j==2||j==8||j==14||j==20)? j-2 : j);
            } else if (acc==7) {
                newpos = (j==7||j==9||j==19||j==21)? newpos=j+1 : ((j==6||j==18)? j+2: j);
            } else if (acc==8) {
                newpos = (j==1||j==13)? j+1 : ((j==0||j==12)? j+2 : j);
            }
            
            composedvowel = UNI_DATA[i][newpos];
        }
        return composedvowel;
    }
    
    /**
     * Ham xoa dau tren cac ki tu .
     *
     * la qua trinh dao nguoc cua ham tong hop nguyen am composeVowel()
     * su dung mang o tren de chuyen doi cac ki tu
     */
    private static char removeAccent(final int j, final int acc, final int i) {
        
    	char resultVal;//gia tri tra ve 

        if (acc<6) {
            resultVal = UNI_DATA[0][j];
        } else if (acc==9 && j>23) {
            resultVal = UNI_DATA[0][j];
        } else {
            int newPos = j;
            
            if (acc == 6 && (j==0||j==3||j==6||j==12||j==15||j==18)) {
                newPos = j+1;
            } else if (acc == 7 && (j==8||j==10||j==20||j==22)) {
                newPos = j-1;
            } else if (acc == 8 && (j==2||j==14)) {
                newPos = j-1;
            }
            
            resultVal = UNI_DATA[i][newPos];
        }
        return resultVal;
    }
    
    /**
     * Tao mot ki tu Tieng Viet .
     *
     * @param curChar la ki tu nam tai vi ri con tro
     * @param accentKey la ki tu bieu dien dau, tu '0' - '9'
     * @return the result Vietnamese character
     */
    public static char toVietChar(final char curChar, final char accentKey) {
        return toVietChar(curChar, (int) accentKey - '0');
    }
    
    /**
     * Composes a Vietnamese character.
     *
     * @param curChar the character at current caret position
     * @param accentIndex the code point value of the accent key, ranging from \u0030 - \u0039
     * @return the result Vietnamese character
     */    
    public static char toVietChar(final char curChar, final int accentIndex) {
        char vietChar;
        accentRemoved = false;
        for (int i = 0; i < UNI_DATA.length; i++)  {
            for (int j = 0; j < UNI_DATA[i].length; j++)  {   
                if (UNI_DATA[i][j] == curChar) {    // found a match
                    if (DDDD.indexOf(curChar) != -1 && accentIndex != 0 && accentIndex != 9) {
                        return curChar;
                    }                    
                    vietChar = composeVowel(j,accentIndex,i); // return new wowel
                    
                    if (vietChar == curChar) {
                        if (accentIndex != 0) {
                            // accent removed by repeat key?
                            accentRemoved = true;
                        }
                        vietChar = removeAccent(j,accentIndex,i);
                        vietChar = curChar; // strip the first (top) combining diacritics
                        
                        if (vietChar == curChar && accentIndex == 0) {
                            vietChar = curChar;
                        }
                    }
                    
                    return vietChar;
                }
            }
        }
        return curChar; // no valid combination, return original
    }
    
    /**
     * Composes a Vietnamese word.
     *
     * @param curWord the word at current caret position
     * @param accentKey accent key, ranging from '0' - '9'
     * @return the result Vietnamese word
     */    
    public static String toVietWord(final String curWord, final char accentKey) {
        return toVietWord(curWord, (int) accentKey - '0');
    }
    
    /**
     * Composes a Vietnamese word.
     *
     * @param curWord the word at current caret position
     * @param accentIndex the code point value of the accent key, ranging from \u0030 - \u0039
     * @return the result Vietnamese word
     */    
    public static String toVietWord(final String curWord, final int accentIndex) {
        final int wl = curWord.length();    // do dai cua tu 
        char cp[] = curWord.toCharArray();
        String wordNew = null;
        String lowCase = curWord.toLowerCase();

        // tu bat dau voi D hoac d
        if (accentIndex==9 && DDDD.indexOf(cp[0]) != -1) {
            cp[0] = toVietChar(curWord.charAt(0), accentIndex);
            wordNew = String.valueOf(cp);
            return wordNew;
        }
        
        // if wordlength >= 8 or non-alphanumeric, skip
        if (wl<8 && Character.isLetterOrDigit(cp[wl-1])) {
            
            //if the curWord is ended by 'h' or 'g'
            if (wl>2 && dblconso.matcher(curWord.substring(wl-2)).lookingAt()) {
                //add the 7 accent for both 'uo'
                if (wl>3) {
                   fix_uo(4, wl, accentIndex, cp);
                   wordNew = curWord.substring(0,wl-4) + cp[wl-4] + cp[wl-3] + cp[wl-2] + cp[wl-1];
                } else if (wl==3) {
                   wordNew = "" + toVietChar(cp[0], accentIndex) + cp[wl-2] + cp[wl-1];
                }
            }
            
            else if (wl>=3 && (CMNPT.indexOf(Character.toLowerCase(curWord.charAt(wl-1))) >= 0
                    || ( (lowCase.charAt(wl-1)=='i' || lowCase.charAt(wl-1)=='u') && (lowCase.charAt(wl-3)=='u' || lowCase.charAt(wl-3)==uMoc) ) ) ) {
                fix_uo(3, wl, accentIndex, cp);
                wordNew = curWord.substring(0,wl-3) + cp[wl-3] + cp[wl-2] + cp[wl-1];
            }
                       
            // cases when word start with 'qu' or 'gi' and has only 3 letter
            // add accent mark to the last char
            else if (wl==3 && (lowCase.startsWith("qu") || lowCase.startsWith("gi"))){
                wordNew = curWord.substring(0,wl-1) + toVietChar(cp[wl-1], accentIndex);
            }
            
            else if (wl>1 && accentIndex==6 && (Character.toLowerCase(cp[wl-2]) == 'u' || Character.toLowerCase(cp[wl-2]) == 'i' || Character.toLowerCase(cp[wl-2]) == 'y')) {
                wordNew = shiftAccent(curWord, (char) (accentIndex + '0'));
                cp = wordNew.toCharArray();
                wordNew = wordNew.substring(0,wl-2) + cp[wl-2] + toVietChar(cp[wl-1], accentIndex);
            }
            
            if (wordNew != null) {
                if (accentIndex == 0 && wordNew.equals(curWord)) {
                    wordNew = curWord.replace('\u0111', 'd').replace('\u0110', 'D');
                }
                return wordNew;
            }
            
            if (wl>1) {
                //fix tru72+o7 and similar bugs
                //check all combinations of u and u*
                for (int i = 0; i < UNI_DATA.length; i++) {
                    if (cp[wl-2]==UNI_DATA[i][10] || cp[wl-2]==UNI_DATA[i][22] ||
                        cp[wl-2]==UNI_DATA[i][9] || cp[wl-2]==UNI_DATA[i][21]) {
                        if (Character.toLowerCase(cp[wl-1])=='o') {
                            cp[wl-2] = toVietChar(cp[wl-2], i); //reset u                        
                            cp[wl-1] = toVietChar(cp[wl-1], accentIndex); //set o
                            //add into o the accent used to be in u
                            if (i!=0) {
                                cp[wl-1] = toVietChar(cp[wl-1], i);
                                //we removed accent in character 'u' but we don't want it to be
                                //append at the end of word so we set
                                accentRemoved = false;
                            }
                            return curWord.substring(0,wl-2) + cp[wl-2] + cp[wl-1];
                        }
                    }
                }
            }
            
            if (wl>1  && !(cp[wl-2]==ddc||cp[wl-2]==DDc)&&(!vconso2.matcher(String.valueOf(cp[wl-2])).lookingAt()) && alpha.matcher("" + cp[wl-1]).lookingAt()) {          
                if (wl>2  && accentIndex==7 && Character.toLowerCase(cp[wl-3])=='u') {
                    wordNew = curWord.substring(0,wl-3) + toVietChar(cp[wl-3], accentIndex) + toVietChar(cp[wl-2], accentIndex) + cp[wl-1];
                } else if (wl>2  && accentIndex==6 && Character.toLowerCase(cp[wl-3]) == 'u') {
                    if (cp[wl-3]==uMoc) cp[wl-3] = 'u';
                    if (cp[wl-3]==UMoc) cp[wl-3] = 'U';
                    wordNew = curWord.substring(0,wl-3)  + cp[wl-3] + toVietChar(cp[wl-2], accentIndex) + cp[wl-1];
                } else if ((accentIndex==6 || accentIndex==7)&&(vowelpat2.matcher(String.valueOf(cp[wl-2])).lookingAt())) {
                    wordNew = curWord.substring(0,wl-1) + toVietChar(cp[wl-1], accentIndex);
                } else if (accentIndex==8 && (!vconso2.matcher(String.valueOf(cp[wl-1])).lookingAt())) {
                    if (Character.toLowerCase(cp[wl-2]) == 'i' || Character.toLowerCase(cp[wl-2]) == 'u' || Character.toLowerCase(cp[wl-2]) == 'o') {
                        wordNew = shiftAccent(curWord, (char) (accentIndex + '0'));
                        cp = wordNew.toCharArray();
                    } else {
                        wordNew = curWord;
                    }
                    wordNew = wordNew.substring(0,wl-1) + toVietChar(cp[wl-1], accentIndex);
                   
                } else if (wl>2) {
                    // fix the correct accent at "lo'a'n, to'a'n"
                    String temp = curWord.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");

                    if (wl>3) {
                        if (diacriticsPosClassicOn || !endDoubleVowels.matcher(temp).lookingAt()) {
                            wordNew = curWord.substring(0,wl-3) + toVietChar(cp[wl-3], 0) +
                                toVietChar(cp[wl-2], accentIndex) + cp[wl-1];
                        } else {
                            wordNew = curWord.substring(0,wl-3) + toVietChar(cp[wl-3], 0) + cp[wl-2] +
                                toVietChar(cp[wl-1], accentIndex);                            
                        }
                    } else {
                        char tp = (DDDD.indexOf(cp[0]) != -1) ? cp[wl-3] : toVietChar(cp[wl-3], 0);
                        if (diacriticsPosClassicOn || !endDoubleVowels.matcher(temp).lookingAt()) {
                            wordNew = curWord.substring(0,wl-3) + tp + 
                                toVietChar(cp[wl-2], accentIndex) + cp[wl-1];
                        } else {
                            wordNew = curWord.substring(0,wl-3) + tp + cp[wl-2] +
                                toVietChar(cp[wl-1], accentIndex);                            
                        }
                            
                    }    
                } else {
                    String temp = curWord.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");

                    if (diacriticsPosClassicOn || !endDoubleVowels.matcher(temp).lookingAt()) {
                        wordNew = curWord.substring(0,wl-2) + toVietChar(cp[wl-2], accentIndex) + cp[wl-1];
                    } else {
                        wordNew = curWord.substring(0,wl-1) + toVietChar(cp[wl-1], accentIndex);
                    }
                }
            }
            
            // all other cases are dealed here
            // fix removing the accent of 1-char words
            else if (!xconso.matcher(String.valueOf(cp[wl-1])).lookingAt()) {  //vowel at last char
                wordNew = curWord.substring(0,wl-1) + toVietChar(cp[wl-1], accentIndex);
            }
            
            if (wordNew != null) {
                if (accentIndex == 0 && wordNew.equals(curWord)) {
                    wordNew = curWord.replace('\u0111', 'd').replace('\u0110', 'D');
                }
                return wordNew;
            }
        }
        return curWord;  // no valid combination, return original
    }
    
    /**
    * Resolves the accent for "uo" cases
    * (originally written by Q. Le)
    */    
    private static void fix_uo(int x, int wl, int accentIndex, char[] cp){      
        if (accentIndex==7 && Character.toLowerCase(cp[wl-x]) == 'u') {
            // always change both characters
            cp[wl-x+1] = toVietChar(cp[wl-x+1], accentIndex);
            cp[wl-x] = toVietChar(cp[wl-x], accentIndex);
            
            // then fix for each in the for cases:  'uo'    ->'u*o*'
            //                  'u*o*'  ->'uo'
            //                  'u*o'   ->'u*o*'
            //                  'uo*'   ->'u*o*'
            for (int i = 0; i < UNI_DATA.length; i++){
                //case uo* (o* with '`?~...)
                if (cp[wl-x+1] == UNI_DATA[i][7] || cp[wl-x+1] == UNI_DATA[i][19]){
                    if(cp[wl-x]==uMoc || cp[wl-x]==UMoc){
                        cp[wl-x+1] = toVietChar(cp[wl-x+1], accentIndex);
                        accentRemoved = false;
                        break;
                    }
                } else if (cp[wl-x+1] == UNI_DATA[i][8] || cp[wl-x+1] == UNI_DATA[i][20]){
                    //case u*o
                    if(Character.toLowerCase(cp[wl-x]) =='u'){
                        cp[wl-x] = toVietChar(cp[wl-x], accentIndex);
                        accentRemoved = false;
                        break;
                    }
                }
            }
        } else if(accentIndex == 6){
            cp[wl-x+1] = toVietChar(cp[wl-x+1], 6);
            if(cp[wl-x] == uMoc || cp[wl-x] == UMoc){
                cp[wl-x] = toVietChar(cp[wl-x], 7);
                accentRemoved = false;
            }
            //other accents
        } else {
            if (accentIndex == 0 && (Character.toLowerCase(cp[wl-x+1]) == oMoc || Character.toLowerCase(cp[wl-x+1]) == 'o')) {
                cp[wl-x] = toVietChar(cp[wl-x], accentIndex);
            }
            cp[wl-x+1] = toVietChar(cp[wl-x+1], accentIndex);
        }
    }
    
    /**
     * Shifts the accent mark to the correct vowel in a multiple-vowel sequence,
     * as in t\u00f2a + n -> to\u00e0n.
     *
     * @param curWord the word at current caret position
     * @param key the key input
     * @return the new word if shifting occurs<br>
     *         <tt>curWord</tt>, otherwise
     */
    public static String shiftAccent(final String curWord, final char key) {
        char[] cp = curWord.toCharArray();
        int wl = cp.length;
        String newWord = curWord;
       
        char ch1 = Character.toLowerCase(cp[wl-1]);
        char ch2 = Character.toLowerCase(cp[wl-2]);
        
        if (curWord.length() == 3 && (curWord.toLowerCase().startsWith("qu") || curWord.toLowerCase().startsWith("gi"))) {
            for (int i = 1; i < UNI_DATA.length; i++){
                if (ch2 == UNI_DATA[i][5] || ch2 == UNI_DATA[i][9]) {
                    newWord = curWord.substring(0,wl-2) + toVietChar(cp[wl-2],0) + toVietChar(cp[wl-1],i);
                    break;
                }
            }            
        }
        // 
        else if (EIOUY.indexOf(Character.toLowerCase(key)) != -1) {
            if (ch1 == 'a' || ch1 == 'o' || ch1 == oMoc || Character.toLowerCase(key) == 'e' || Character.toLowerCase(key) == 'u' || (ch1 == 'e' && Character.toLowerCase(key) == 'o') ) {
                for (int i = 1; i < UNI_DATA.length; i++){
                    if (ch2 == UNI_DATA[i][5] || ch2 == UNI_DATA[i][7] || ch2 == UNI_DATA[i][9] || ch2 == UNI_DATA[i][10] || ch2 == UNI_DATA[i][11]) {
                        newWord = curWord.substring(0,wl-2) + toVietChar(cp[wl-2],0) + toVietChar(cp[wl-1],i);
                        break;
                    }
                }
            }     
        }
        
        // Consonants and ^(
        else if (CMNPT.indexOf(key) != -1 || key == '6' || key == '8') {
            if (AEOY.indexOf(ch1) != -1) {
                for (int i = 1; i < UNI_DATA.length; i++) {
                    if (ch2 == UNI_DATA[i][5] || ch2 == UNI_DATA[i][7] || ch2 == UNI_DATA[i][9] || ch2 == UNI_DATA[i][11] ) {
                        newWord = curWord.substring(0,wl-2) + toVietChar(cp[wl-2],0) + toVietChar(cp[wl-1],i);
                        break;
                    }
                }
            }
        }
        
        return newWord;
    }
    
    /**
     * Determines the correct accent for Telex mode.
     *
     * @param curWord the word at current caret position
     * @param key the key input
     * @param accent the accent key for Telex
     * @return the correct accent: '6', '7', or '8'; '\0' for invalid accent input
     */
    public static char getAccentInTelex(final String curWord, final char key, char accent) {
        if (accent == '6') {
            accent = '\0';
        }
        
        OutOfLoop:
        for (int i=0; i < curWord.length(); i++) {
            char tmp = curWord.charAt(i);
            for (int j = 0; j < UNI_DATA.length; j++){
                if (accent == '7' || Character.toLowerCase(key) == 'a') {
                    for (int k=0; k<3; k++) {
                        //find if there's a character in the word that's also in the first
                        //three columns of the UNI_DATA matrix
                        if (tmp==UNI_DATA[j][k] || tmp==UNI_DATA[j][k+12]){
                            accent = (accent=='7')?'8':'6';
                            break OutOfLoop;
                        }
                    }
                } else if (Character.toLowerCase(key) == 'o') {
                    for (int k=6; k<9; k++){
                        if (tmp==UNI_DATA[j][k] || tmp==UNI_DATA[j][k+12]){
                            accent = '6';
                            break OutOfLoop;
                        }
                    }
                } else { //when key == 'e'
                    for (int k=3; k<5; k++){
                        if (tmp==UNI_DATA[j][k] || tmp==UNI_DATA[j][k+12]){
                            accent = '6';
                            break OutOfLoop;
                        }
                    }
                }
            }
        }
        return accent;
    }
    
    /**
     * Determines if accent is removed by repeating accent key, not by designated accent-removing key.
     *
     * @return true if accent is removed by repeating accent key<br>
     *         false if accent is removed by VNI '0' key, VIQR '-' key, or Telex 'z' key 
     */    
    public static boolean isAccentRemoved() {
        return accentRemoved;
    }

    /**
     * Sets the diacritics position to follow the classic style.
     *
     * @param classic true for classic (\u00f2a, \u00f2e, \u00fay); false for modern (o\u00e0, o\u00e8, u\u00fd)
     */
    public static void setDiacriticsPosClassic(final boolean classic) {    
       diacriticsPosClassicOn = classic;
    }
    
}
