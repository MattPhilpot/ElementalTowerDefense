package elemental.combine.TD;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.TextView;

public class AddOptions extends Activity 
{

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.additional_options);
		
		final CheckBox vibrateOption = (CheckBox)findViewById(R.id.vibrate_option);
		
		final TextView vibrateText = (TextView)findViewById(R.id.vibrate_text);
		
		SharedPreferences getSetting = getSharedPreferences("game_settings", 0);
		final SharedPreferences.Editor editor = getSetting.edit();
	
		final int VibeSetting = getSetting.getInt("Vibration_Setting", 0);
		
		//loading saved settings from last time
		if(VibeSetting==1)
		{
			vibrateText.setText("Device will vibrate on keypress");
			vibrateOption.setChecked(true);
		}
		else if(VibeSetting==0)
		{
			vibrateText.setText("Device won't vibrate on keypress");
			vibrateOption.setChecked(false);
		}
			
		//finish loading settings	
		
		vibrateOption.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
		{
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
		    {
				if (isChecked)
		        {
					vibrateText.setText("Device will vibrate on keypress");
					editor.putInt("Vibration_Setting", 1);
					editor.commit();
		        }
				else
				{
					vibrateText.setText("Device won't vibrate on keypress");
					editor.putInt("Vibration_Setting", 0);
					editor.commit();
				}
		    }
		});
		
		
	}
}
