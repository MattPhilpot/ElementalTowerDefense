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

public class NewGame extends Activity 
{

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_game);
		
		
		SeekBar Difficulty = (SeekBar) findViewById(R.id.Difficulty);
		Button startGame = (Button) findViewById(R.id.Start_Game);
		Button goBack = (Button) findViewById(R.id.Go_back_from_newGame);
		Button addOption = (Button) findViewById(R.id.Add_Options);
		
		final CheckBox EleOrder = (CheckBox)findViewById(R.id.option1);
		final CheckBox ChaosMode = (CheckBox)findViewById(R.id.option2);
		final CheckBox Continuous = (CheckBox)findViewById(R.id.option3);
		
		final TextView progres = (TextView)findViewById(R.id.seekbarValue);
		final TextView eleOrder = (TextView)findViewById(R.id.ele_order);
		final TextView chaosMode = (TextView)findViewById(R.id.chaos_mode);
		final TextView contMode = (TextView)findViewById(R.id.continuous);
		
		SharedPreferences getSetting = getSharedPreferences("game_settings", 0);
		final SharedPreferences.Editor editor = getSetting.edit();
		
		final int EleState = getSetting.getInt("Element_Order", 0);
		final int ChaosState = getSetting.getInt("Chaos_Mode", 0);
		final int ContState = getSetting.getInt("Continuous_Waves", 0);
		final int GameDiff = getSetting.getInt("Game_Difficulty", 2);
		
		//loading saved settings from last time
		if(EleState==1)
		{
			eleOrder.setText("Elements are chosen at random to appear");
			EleOrder.setChecked(true);
		}
		else if(EleState==0)
		{
			eleOrder.setText("Player chooses element to appear");
			EleOrder.setChecked(false);
		}
		
		if(ChaosState==1)
		{
			chaosMode.setText("Random wave order");
			ChaosMode.setChecked(true);
		}
		else if(ChaosState==0)
		{
			chaosMode.setText("Normal wave order");
			ChaosMode.setChecked(false);
		}
		
		if(ContState==1)
		{
			contMode.setText("No time between waves");
			Continuous.setChecked(true);
		}
		else if(ContState==0)
		{
			contMode.setText("Normal time between waves");
			Continuous.setChecked(false);
		}
		Difficulty.setProgress(GameDiff);
		switch(GameDiff)
		{
			case 0: 
				progres.setText("Enemies take normal damage");
				
				break;
			case 1:
				progres.setText("Enemies take 10% less damage");
				break;
			case 2:
				progres.setText("Enemies take 20% less damage");
				break;
			case 3:
				progres.setText("Enemies take 30% less damage");
				break;
			case 4:
				progres.setText("Enemies take 40% less damage");
				break;
		}
		
		//finish loading settings

		Difficulty.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() 
		{
			
			public void onStopTrackingTouch(SeekBar Difficulty) 
			{
				// TODO Auto-generated method stub
				
			}
			
			public void onStartTrackingTouch(SeekBar Difficulty) 
			{
				// TODO Auto-generated method stub
				
			}
			
			public void onProgressChanged(SeekBar Difficulty, int progress, boolean arg2) 
			{
				switch(progress)
				{
					case 0: 
						progres.setText("Enemies take normal damage");
						editor.putInt("Game_Difficulty", 0);
						editor.commit();
						break;
					case 1:
						progres.setText("Enemies take 10% less damage");
						editor.putInt("Game_Difficulty", 1);
						editor.commit();
						break;
					case 2:
						progres.setText("Enemies take 20% less damage");
						editor.putInt("Game_Difficulty", 2);
						editor.commit();
						break;
					case 3:
						progres.setText("Enemies take 30% less damage");
						editor.putInt("Game_Difficulty", 3);
						editor.commit();
						break;
					case 4:
						progres.setText("Enemies take 40% less damage");
						editor.putInt("Game_Difficulty", 4);
						editor.commit();
						break;
				}
			}
		});
		
		EleOrder.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
		{
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
		    {
				if (isChecked)
		        {
					eleOrder.setText("Elements are chosen at random to appear");
					editor.putInt("Element_Order", 1);
					editor.commit();
		        }
				else
				{
					eleOrder.setText("Player chooses element to appear");
					editor.putInt("Element_Order", 0);
					editor.commit();
				}
		    }
		});
		
		ChaosMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
		{
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
		    {
				if (isChecked)
		        {
					chaosMode.setText("Random wave order");
					editor.putInt("Chaos_Mode", 1);
					editor.commit();
		        }
				else
				{
					chaosMode.setText("Normal wave order");
					editor.putInt("Chaos_Mode", 0);
					editor.commit();
				}
		    }
		});
		
		Continuous.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
		{
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
		    {
				if (isChecked)
		        {
					contMode.setText("No time between waves");
					editor.putInt("Continuous_Waves", 1);
					editor.commit();
		        }
				else
				{
					contMode.setText("Normal time between waves");
					editor.putInt("Continuous_Waves", 0);
					editor.commit();
				}
		    }
		});
		
		startGame.setOnClickListener(new View.OnClickListener() 
		{
			SharedPreferences getSetting = getSharedPreferences("game_settings", 0);
	    	final SharedPreferences.Editor editor = getSetting.edit();
			public void onClick(View opt) {
				// TODO Auto-generated method stub
				editor.putBoolean("isNewGame", true);
				editor.commit();
				startActivity(new Intent("elemental.combine.TD.MapGame"));
			}
		});
		
		goBack.setOnClickListener(new View.OnClickListener() 
		{
			
			public void onClick(View opt) {
				// TODO Auto-generated method stub
				startActivity(new Intent("elemental.combine.TD.MM"));
			}
		});
		
		addOption.setOnClickListener(new View.OnClickListener() 
		{
			
			public void onClick(View opt) {
				// TODO Auto-generated method stub
				startActivity(new Intent("elemental.combine.TD.AddOptions"));
			}
		});
	}
}
