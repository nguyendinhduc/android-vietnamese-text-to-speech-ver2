package android.hook;

import android.content.Context;
import android.view.KeyEvent;
import android.widget.EditText;

public class VietText extends EditText {

	public VietText(Context context) {
		super(context);
		
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		 return VietKeyListenner.setKey(this, keyCode,event);
	}
	

}
