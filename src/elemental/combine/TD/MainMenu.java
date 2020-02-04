package elemental.combine.TD;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainMenu extends Activity
{

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		Button cont = (Button) findViewById(R.id.Continue);
		Button newGame = (Button) findViewById(R.id.New_Game);
		Button upgrades = (Button) findViewById(R.id.Upgrades);
		Button options = (Button) findViewById(R.id.Options);
		Button about = (Button) findViewById(R.id.About);
		
		cont.setOnClickListener(new View.OnClickListener() 
		{
			SharedPreferences getSetting = getSharedPreferences("game_settings", 0);
	    	final SharedPreferences.Editor editor = getSetting.edit();
			public void onClick(View opt) 
			{
				// TODO Auto-generated method stub
				editor.putBoolean("isNewGame", false);
				editor.commit();
				startActivity(new Intent("elemental.combine.TD.MapGame"));
			}
		});
		newGame.setOnClickListener(new View.OnClickListener() 
		{
			public void onClick(View opt) 
			{
				// TODO Auto-generated method stub
				startActivity(new Intent("elemental.combine.TD.NG"));
			}
		});
		upgrades.setOnClickListener(new View.OnClickListener() 
		{
			
			public void onClick(View opt) {
				// TODO Auto-generated method stub
				startActivity(new Intent("elemental.combine.TD.Upgrades"));
			}
		});
		options.setOnClickListener(new View.OnClickListener() 
		{
			
			public void onClick(View opt) {
				// TODO Auto-generated method stub
				startActivity(new Intent("elemental.combine.TD.Options"));
			}
		});
		about.setOnClickListener(new View.OnClickListener() 
		{
			
			public void onClick(View opt) {
				// TODO Auto-generated method stub
				startActivity(new Intent("elemental.combine.TD.About"));
			}
		});
	}
}
