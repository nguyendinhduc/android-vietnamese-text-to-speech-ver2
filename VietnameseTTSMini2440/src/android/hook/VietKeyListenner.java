package android.hook;

import android.text.Editable;
import android.text.InputFilter.LengthFilter;
import android.view.KeyEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import java.util.Properties;
import java.text.BreakIterator;

import android.content.Context;
import android.hook.VietKeyInput;


public class VietKeyListenner {

	private static boolean VietModeOn = true;//bat che do go tieng viet 
	private static InputMethods selectedInputMethod = InputMethods.Telex;// kieu go mac dinh la Telex, co the seto cac truogn hop khac
	private static InputMethod inputMethod = InputMethodFactory.createInputMethod(selectedInputMethod);
	private static boolean smartMarkOn = true;// cho phep bo dau o cuoi tu ngoai cach bo dau thong thuong
	private static boolean repeatKeyConsumed;// bo dau tu dong
	private static int start;
	private static int end;
	private static String curWord;
	private static String vietWord;
    private static char curChar;
	private static char vietChar;
    private static char keyChar;
	private static char accent;
	private static Properties macroMap;// bang viet tat
	
	private static final char ESCAPE_CHAR = '\\';
    private static final String SHIFTING_CHARS = "cmnpt"; 
    private static final String VOWELS = "aeiouy";// cac nguyen am co ban
    private static final String NON_ACCENTS = "!@#$%&)_={}[]|:;/>,";// cac ki tu vo nghia
    private static final int MODIFIER_MASK = KeyEvent.META_ALT_ON | KeyEvent.META_SHIFT_ON | KeyEvent.META_SYM_ON;
    private static final BreakIterator boundary = BreakIterator.getWordInstance();
  
	public  void setVietModeEnabled(final boolean mode) {
        VietModeOn = mode;
    }
	
	//chon cac phuong an nhap  
	public void setInputMethod(final InputMethods method) {
        selectedInputMethod = method;
        inputMethod = InputMethodFactory.createInputMethod(selectedInputMethod);
    } 
	
	//tra ve phuong thuc nhap, mac dinh la Telex
	public InputMethods getInputMethod() {
        return selectedInputMethod;
    } 
	
	//ham bo dau thong minh 
	public void setSmartMark(final boolean smartMark) {
        smartMarkOn = smartMark;
    }
	
	//bo dau kieu co dien
	public void setDiacriticsPosClassic(final boolean classic) {    
        VietKeyInput.setDiacriticsPosClassic(classic);
    }
	
	public static void setMacroMap(final Properties shortHandMap) {
	    macroMap = shortHandMap;
	}
	
	public void consumeRepeatKey(final boolean mode) {
	        repeatKeyConsumed = mode;
	}
		
	private static char getAccentMark(char keyChar, char curChar, String curWord) {
	        return inputMethod.getAccentMark(keyChar, curChar, curWord);
	}
	
	private static String getCurrentWord(int pos, String source) {
		//BreakIterator boundary = BreakIterator.getWordInstance();
        boundary.setText(source);
        end = boundary.following(pos-1);
        start = boundary.previous();
        end = pos; // fine-tune word end
        return source.substring(start, end);
	}
	
	public static boolean setKey(EditText edit , int keyCode, KeyEvent event)
	  {	
		//neu ko chon tieng Viet bo luon
		if (!VietModeOn) return false; 
		        		
		//lay ve vi tri cua con tro
		int CaretPos = edit.getSelectionStart();
		if (CaretPos==0 ||(event.isModifierKey( MODIFIER_MASK))) return false;
        
	   
		//doc noi dung cua toan bo file, lay ra ki tu tai vi tri cua con tro tru di mot 
		String doc = edit.getText().toString();
		try{
			//ki tu truoc khi go( vi tri hien tai cua con tro khi an
			curChar = doc.charAt(CaretPos-1);
		}
		catch (Exception exc){
			System.err.println(exc.getMessage());
		}
		
		//neu vi tri truoc cua con tro la khoang trong thi tra ve luon		
		if(curChar!=ESCAPE_CHAR && !Character.isLetter(curChar)) return false;
		
		//lay ki tu nhap vao
		keyChar = event.getDisplayLabel();
		
		//su ly viet tat , neu tim dc tu viet tat pgu hop trong bang tu viet tat thi thay the no bang tu 
		//tuong ung trong bang viet tat
		if(keyChar == ' ' && macroMap!=null )
		{
			try{
				String key = getCurrentWord(CaretPos, doc);
				if (macroMap.containsKey(key)) {
					//chon duoc ki tu  trong bang viet tat
                    String value = (String) macroMap.getProperty(key);                    
                    //thuc hien chuyen viet tat thanh tuong ung voi tu that
                    edit.getText().replace(start, end, value);
                    return true;				
				}
			}
			catch(Exception exc){
				System.err.println(exc.getMessage());
			}
			
		}
		
		
		// bo qua cac ki tu khong phai la dau de go cho nhanh
		if (Character.isWhitespace(keyChar) || NON_ACCENTS.indexOf(keyChar) != -1 || keyChar == '\b') {
            return false; 
        }
		
		
		try {
			//doc tu hien tai dang co truoc khi ki tu tiep theo duoc go vao
            curWord = getCurrentWord(CaretPos, doc);
        } catch (Exception exc) {
            System.err.println(exc.getMessage());
        }
		
		// Shift the accent to the second vowel in a two-consecutive-vowel sequence, if applicable
		
		if (smartMarkOn) {
            if (curWord.length() >= 2 &&(SHIFTING_CHARS.indexOf(Character.toLowerCase(keyChar)) >= 0 ||VOWELS.indexOf(Character.toLowerCase(keyChar)) >= 0 )) {
                try  {
                    String newWord;
                    // special case for "qu" and "gi"
                    if (curWord.length() == 2 && VOWELS.indexOf(Character.toLowerCase(keyChar)) >= 0 && (curWord.toLowerCase().startsWith("q") || curWord.toLowerCase().startsWith("g") )) {
                        newWord = VietKeyInput.shiftAccent(curWord+keyChar, keyChar);
                        if (!newWord.equals(curWord+keyChar)) {
                            edit.getText().replace(start, end, newWord);                         
                            return true;
                        }
                    }

                    newWord = VietKeyInput.shiftAccent(curWord, keyChar);
                    if (!newWord.equals(curWord)) {
                    	edit.getText().replace(start, end, newWord);
                        curWord = newWord;
                    }
                } catch (StringIndexOutOfBoundsException exc)  {
                    System.err.println("Caret out of bound! (For Shifting Marks)");
                }
            } 
        }
		accent = getAccentMark(keyChar, curChar, curWord);
		
		try{
            if (Character.isDigit(accent))  {
                if (smartMarkOn)  {
                    vietWord = (curChar == ESCAPE_CHAR) ?String.valueOf(keyChar) : VietKeyInput.toVietWord(curWord, accent);
                    if (!vietWord.equals(curWord)) {
                    	//thay thế kí tự tương ứng trong bảng unicode dựng sẵn
                    	edit.getText().replace(start, end, vietWord);

                        if (!VietKeyInput.isAccentRemoved() || repeatKeyConsumed) {
                            // bỏ qua do dấu câu được đánh không phải do phím '0'
                        	return true;
                        }
                    }
                } else {
                    vietChar = (curChar == ESCAPE_CHAR)? keyChar: VietKeyInput.toVietChar(curChar, accent);
                    if (vietChar != curChar) {
                    	edit.getText().replace(CaretPos-1, CaretPos, String.valueOf(vietChar));

                        if (!VietKeyInput.isAccentRemoved() || repeatKeyConsumed) {
                            // accent removed by repeat key, not '0' key
                           return true;
                        }
                    }
                }
            }
            
            else if (accent != '\0') {
                char phanChar = (curChar == ESCAPE_CHAR)? keyChar: accent;
                edit.getText().replace(CaretPos-1, CaretPos, String.valueOf(phanChar));
                return true;
            }
        }
        catch (Exception exc)  {
            System.err.println("Caret out of bound!");
        }
		return false;
		} 
	
	public static void setKey2(Editable s)
	  {	
		//neu ko chon tieng Viet bo luon
		if (!VietModeOn) return; 
		        		
		//lay ve vi tri cua con tro
		int CaretPos = s.length()-1;
		if (CaretPos<=0) return ;
		//chon<=0 de phong truong hop khi co mot ki tu va an xoa thi doc =null => loi
      
	   
		//doc noi dung cua toan bo file
		//do su kien xay ra khi text da thay doi nen doc phai bo di ki tu cuoi
		String doc = s.toString();
		doc = doc.substring(0, CaretPos);
		
		//ki tu truoc khi go( vi tri hien tai cua con tro khi an
		try{
			
			curChar = doc.charAt(CaretPos-1);
		}
		catch (Exception exc){
			System.err.println(exc.getMessage());
		}
		
		//		
		if(curChar!=ESCAPE_CHAR && !Character.isLetter(curChar)) return;
		
		//lay ki tu nhap vao
		keyChar = s.charAt(s.length()-1);
		
		//su ly viet tat , neu tim dc tu viet tat phu hop trong bang tu viet tat thi thay the no bang tu 
		//tuong ung trong bang viet tat
		if(keyChar == ' ' && macroMap!=null )
		{
			try{
				String key = getCurrentWord(CaretPos, doc);
				if (macroMap.containsKey(key)) {
					//chon duoc ki tu  trong bang viet tat
                  String value = (String) macroMap.getProperty(key);                    
                  //thuc hien chuyen viet tat thanh tuong ung voi tu that,
                  //+1 de 
                  s.replace(start, end+1, value);
                  
				}
			}
			catch(Exception exc){
				System.err.println(exc.getMessage());
			}
			
		}
		
		
		// bo qua cac ki tu khong phai la dau de go cho nhanh
		if (Character.isWhitespace(keyChar) || NON_ACCENTS.indexOf(keyChar) != -1 || keyChar == '\b') {
          return; 
		}
		
		
		try {
			//doc tu hien tai dang co truoc khi ki tu tiep theo duoc go vao
          curWord = getCurrentWord(CaretPos, doc);
		} catch (Exception exc) {
          System.err.println(exc.getMessage());
		}
		
		// Shift the accent to the second vowel in a two-consecutive-vowel sequence, if applicable
		
		if (smartMarkOn) {
          if (curWord.length() >= 2 &&(SHIFTING_CHARS.indexOf(Character.toLowerCase(keyChar)) >= 0 ||VOWELS.indexOf(Character.toLowerCase(keyChar)) >= 0 )) {
              try  {
                  String newWord;
                  // special case for "qu" and "gi"
                  if (curWord.length() == 2 && VOWELS.indexOf(Character.toLowerCase(keyChar)) >= 0 && (curWord.toLowerCase().startsWith("q") || curWord.toLowerCase().startsWith("g") )) {
                      newWord = VietKeyInput.shiftAccent(curWord+keyChar, keyChar);                  
                      s.replace(start, end+1, newWord);
                       
                      
                      if (!newWord.equals(curWord+keyChar)) {                                             
                         //bo qua bam phim
                    		//e.sonsume
                    	 return;
                      }
                  }

                  newWord = VietKeyInput.shiftAccent(curWord, keyChar);
                  if (!newWord.equals(curWord)) {
                  	s.replace(start, end, newWord);
                      curWord = newWord;
                  }
              } catch (StringIndexOutOfBoundsException exc)  {
                  System.err.println("Caret out of bound! (For Shifting Marks)");
              }
          } 
      }
		accent = getAccentMark(keyChar, curChar, curWord);
		
		try{
          if (Character.isDigit(accent))  {
              if (smartMarkOn)  {
                  vietWord = (curChar == ESCAPE_CHAR) ?String.valueOf(keyChar) : VietKeyInput.toVietWord(curWord, accent);
                  if (!vietWord.equals(curWord)) {
                  	//thay thế kí tự tương ứng trong bảng unicode dựng sẵn
                	  s.replace(start, end, vietWord);
                	  s.replace(end, end+1,"");

                      if (!VietKeyInput.isAccentRemoved() || repeatKeyConsumed) {
                          // bỏ qua do dấu câu được đánh không phải do phím '0'
                    		//e.sonsume
                    	  return;
                      }
                  }
              } else {
                  vietChar = (curChar == ESCAPE_CHAR)? keyChar: VietKeyInput.toVietChar(curChar, accent);
                  if (vietChar != curChar) {
                	  s.replace(CaretPos-1, CaretPos+1, String.valueOf(vietChar));
                	 

                      if (!VietKeyInput.isAccentRemoved() || repeatKeyConsumed) {
                          // accent removed by repeat key, not '0' key
                    		//e.sonsume
                    	  return;
                      }
                  }
              }
          }
          else if (accent != '\0') {
              char phanChar = (curChar == ESCAPE_CHAR)? keyChar: accent;
              s.replace(CaretPos-1, CaretPos+1, String.valueOf(phanChar));
             	//e.sonsume
              
              return;
          }
      }
      catch (Exception exc)  {
          System.err.println("Caret out of bound!");
      }
		}

	public static void setKey3(EditText s)
	  {	
		//neu ko chon tieng Viet bo luon
		if (!VietModeOn) return; 
		        		
		//lay ve vi tri cua con tro
		int CaretPos = s.length()-1;
		if (CaretPos<=0) return ;
		//chon<=0 de phong truong hop khi co mot ki tu va an xoa thi doc =null => loi
    
	   
		//doc noi dung cua toan bo file
		//do su kien xay ra khi text da thay doi nen doc phai bo di ki tu cuoi
		String doc = s.getText().toString();
		doc = doc.substring(0, CaretPos);
		
		//ki tu truoc khi go( vi tri hien tai cua con tro khi an
		try{
			
			curChar = doc.charAt(CaretPos-1);
		}
		catch (Exception exc){
			System.err.println(exc.getMessage());
		}
		
		//		
		if(curChar!=ESCAPE_CHAR && !Character.isLetter(curChar)) return;
		
		//lay ki tu nhap vao
		keyChar = s.getText().charAt(s.length()-1);
		
		//su ly viet tat , neu tim dc tu viet tat phu hop trong bang tu viet tat thi thay the no bang tu 
		//tuong ung trong bang viet tat
		if(keyChar == ' ' && macroMap!=null )
		{
			try{
				String key = getCurrentWord(CaretPos, doc);
				if (macroMap.containsKey(key)) {
					//chon duoc ki tu  trong bang viet tat
                String value = (String) macroMap.getProperty(key);                    
                //thuc hien chuyen viet tat thanh tuong ung voi tu that,
                //+1 de 
                
                s.getText().replace(start, end+1, value);
                
				}
			}
			catch(Exception exc){
				System.err.println(exc.getMessage());
			}
			
		}
		
		
		// bo qua cac ki tu khong phai la dau de go cho nhanh
		if (Character.isWhitespace(keyChar) || NON_ACCENTS.indexOf(keyChar) != -1 || keyChar == '\b') {
        return; 
		}
		
		
		try {
			//doc tu hien tai dang co truoc khi ki tu tiep theo duoc go vao
        curWord = getCurrentWord(CaretPos, doc);
		} catch (Exception exc) {
        System.err.println(exc.getMessage());
		}
		
		// Shift the accent to the second vowel in a two-consecutive-vowel sequence, if applicable
		
		if (smartMarkOn) {
        if (curWord.length() >= 2 &&(SHIFTING_CHARS.indexOf(Character.toLowerCase(keyChar)) >= 0 ||VOWELS.indexOf(Character.toLowerCase(keyChar)) >= 0 )) {
            try  {
                String newWord;
                // special case for "qu" and "gi"
                if (curWord.length() == 2 && VOWELS.indexOf(Character.toLowerCase(keyChar)) >= 0 && (curWord.toLowerCase().startsWith("q") || curWord.toLowerCase().startsWith("g") )) {
                    newWord = VietKeyInput.shiftAccent(curWord+keyChar, keyChar);                  
                    s.getText().replace(start, end+1, newWord);
                     
                    
                    if (!newWord.equals(curWord+keyChar)) {                                             
                       //bo qua bam phim
                  		//e.sonsume
                  	 return;
                    }
                }

                newWord = VietKeyInput.shiftAccent(curWord, keyChar);
                if (!newWord.equals(curWord)) {
                	s.getText().replace(start, end, newWord);
                    curWord = newWord;
                }
            } catch (StringIndexOutOfBoundsException exc)  {
                System.err.println("Caret out of bound! (For Shifting Marks)");
            }
        } 
    }
		accent = getAccentMark(keyChar, curChar, curWord);
		
		try{
        if (Character.isDigit(accent))  {
            if (smartMarkOn)  {
                vietWord = (curChar == ESCAPE_CHAR) ?String.valueOf(keyChar) : VietKeyInput.toVietWord(curWord, accent);
                if (!vietWord.equals(curWord)) {
                	//thay thế kí tự tương ứng trong bảng unicode dựng sẵn
              	  s.getText().replace(start, end, vietWord);
              	  s.getText().replace(end, end+1,"");

                    if (!VietKeyInput.isAccentRemoved() || repeatKeyConsumed) {
                        // bỏ qua do dấu câu được đánh không phải do phím '0'
                  		//e.sonsume
                  	  return;
                    }
                }
            } else {
                vietChar = (curChar == ESCAPE_CHAR)? keyChar: VietKeyInput.toVietChar(curChar, accent);
                if (vietChar != curChar) {
              	  s.getText().replace(CaretPos-1, CaretPos+1, String.valueOf(vietChar));
              	 

                    if (!VietKeyInput.isAccentRemoved() || repeatKeyConsumed) {
                        // accent removed by repeat key, not '0' key
                  		//e.sonsume
                  	  return;
                    }
                }
            }
        }
        else if (accent != '\0') {
            char phanChar = (curChar == ESCAPE_CHAR)? keyChar: accent;
            s.getText().replace(CaretPos-1, CaretPos+1, String.valueOf(phanChar));
           	//e.sonsume
            
            return;
        }
    }
    catch (Exception exc)  {
        System.err.println("Caret out of bound!");
    }
		}
	}        

	


	

	

	

