package elemental.combine.TD;


import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Matrix.ScaleToFit;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Vibrator;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
 


public class MapGame extends Activity 
{
	int wave, lastWave;
	private theThread _thread;
	private Handler handler;
	int totalAlive;
	int timeLeft;
	long lastTime;
	long firstTime;
	long interestTime;
	int scoreMoney;
	int interestCountdown;
	int whichClicked;
	int livesLeft;
	boolean gamePaused;
	int totalScore;
	int currentAir = 0, currentWater = 0, currentEarth = 0, currentFire = 0;
	int continuousMode, selectElement;
	int nextElement;
	int vibeSet;
	boolean newGame;
	
	
	Vibrator vibe;
	private ArrayList<GraphicObject> _graphics = new ArrayList<GraphicObject>();
	private List<Baddies> _badGuys = new ArrayList<Baddies>();
	SharedPreferences getSetting;
	SharedPreferences.Editor editor;
	
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {	
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(new Panel(this));
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
       
        gamePaused = false;
        wave = 0;
        totalAlive = 0;
        timeLeft = 5;
        firstTime = 0;
        interestTime = 0;
        scoreMoney = 70;
        interestCountdown = 35;
        livesLeft = 50;
        lastWave = 0;
        totalScore = 0;
        getSetting = getSharedPreferences("game_settings", 0);
    	editor = getSetting.edit();
    	nextElement = 0;
    	
    	newGame = getSetting.getBoolean("isNewGame", true);
    	continuousMode = getSetting.getInt("Continuous_Waves", 0);
    	selectElement = getSetting.getInt("Element_Order", 0);
    	vibeSet = getSetting.getInt("Vibration_Setting", 0);
    	/*
    	try 
		{
    		FileInputStream inStream = new FileInputStream("TowerList.dat");
    		ObjectInputStream objectInStream = new ObjectInputStream(inStream);
    		int Count = objectInStream.readInt(); // Get the number of regions
    		for (int c=0; c < Count; c++)
    		    _graphics.add((GraphicObject) objectInStream.readObject());
    		objectInStream.close();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		*/
    }
 
    @Override
    public void onPause()
    {
    	super.onPause();
 	
    	
		try 
		{
			FileOutputStream outStream = new FileOutputStream("TowerList.dat");
			ObjectOutputStream objectOutStream;
			objectOutStream = new ObjectOutputStream(outStream);
			objectOutStream.writeInt(_graphics.size()); // Save size first
	    	for(GraphicObject graphic : _graphics)
	    	    objectOutStream.writeObject(graphic);
	    	objectOutStream.close();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}

    	
    	
    	gamePaused = true;
    	finish();
    }
    
   
	@Override
    public void onResume()
    {
    	super.onResume();
    	
    	
    	
    	gamePaused = false;
    }
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.menu, menu);
	    return true;
	}
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
	    // Handle item selection
		if(selectElement == 0)
		{
		    switch (item.getItemId()) 
		    {
		    case R.id.spawnAir:
		        nextElement = 2;
		        return true;
		    case R.id.spawnWater:
		        nextElement = 3;
		        return true;
		    case R.id.spawnEarth:
		        nextElement = 4;
		        return true;
		    case R.id.spawnFire:
		    	nextElement = 5;
		    	return true;      
		    }
		}
		return super.onOptionsItemSelected(item);
	}
	
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  
    {
    	
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) 
        {
        	
        	final Dialog dialog = new Dialog(MapGame.this);
        	dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        	dialog.setContentView(R.layout.exit_screen);
        	dialog.setCancelable(true);
        	
        	Button option1 = (Button) dialog.findViewById(R.id.exit);
        	Button option2 = (Button) dialog.findViewById(R.id.exitandsave);
        	Button option3 = (Button) dialog.findViewById(R.id.cancel);
        	
        	option1.setOnClickListener(new View.OnClickListener() 
    		{
      			public void onClick(View opt) 
    			{
      				dialog.dismiss();
      				startActivity(new Intent("elemental.combine.TD.NG"));
    			}
    		});
        	
        	option2.setOnClickListener(new View.OnClickListener() 
    		{
      			public void onClick(View opt) 
    			{
      				dialog.dismiss();
      				startActivity(new Intent("elemental.combine.TD.MM"));
    			}
    		});
        	
        	option3.setOnClickListener(new View.OnClickListener() 
    		{
      			public void onClick(View opt) 
    			{
      				dialog.dismiss();
      				gamePaused = false;
    			}
    		});
        	
        	dialog.setOnDismissListener(new OnDismissListener() 
        	{ 
        	    public void onDismiss(DialogInterface dialog) 
        	    { 
        	    	gamePaused = false;
        	    } 
        	}); 
        	
        	dialog.show();
        	gamePaused = true;
            return true;
        }
       
        
        return super.onKeyDown(keyCode, event);
    }
    
    
    
    
    
    class Panel extends SurfaceView implements SurfaceHolder.Callback
    {

    	private GraphicObject _currentGraphic = null;
    	private Baddies _currentBad = null;
    	private Map<Integer, Bitmap> _bitmapCache = new HashMap<Integer, Bitmap>();
    	Display dm = getWindowManager().getDefaultDisplay();
    	Dialog towerDialog;
    	Bitmap bitmap;
    	SharedPreferences getSetting = getSharedPreferences("game_settings", 0);
    	final SharedPreferences.Editor editor = getSetting.edit();
    	List<GraphicObject> _graphicObject = new ArrayList<GraphicObject>();
    	double gameDiff = (double)getSetting.getInt("Game_Difficulty", 2);
		boolean newObject = false;
    	int width = dm.getWidth();
    	int height = dm.getHeight();
    	double heightRatio = (double) height / 480.0;
    	double widthRatio = (double) width / 800.0;
    	int towerLocation[][] = new int[width][height];
    				//		0		1		2		3		4		5		6		7		8		9		10		11		12		13		14		15
        int point[][] = {	{0,		84,		84,		400,	400,	175,	175,	610,	610,	496,	496,	705,	705,	80,		80,		0}, 	// X -- 0
        					{330,	330,	450,	450,	334,	334,	190,	190,	340,	340,	450,	450,	76,		76,		235,	235}}; 	// Y -- 1
    	int aoeArray[] = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    	int badHealth[] = {5,6,7,8,10,11,13,16,19,22,26,31,36,42,49,57,67,79,92,108,126,147,172,201,236,276,323,378,442,517,605,707,828,968,1133,1326,1551,1815,2123,2484,2882,3372,3945,4615,5400,6318,7392,8648,10119,11839};
    	int badBounty[] = {1,1,1,1,2, 2, 2, 2, 2, 3, 3, 3, 3, 4, 4, 5, 5, 6, 6, 7,  7,  8,  9,  10, 11, 12, 13, 14, 16, 17, 19, 21, 23, 26, 28,  31,  34,  37,  41,  45,  50,  55,  60,  66,  73,  80,  88,  97,  107,  117};
    	int towerStartwide = width - (width/13);
    	int towerEndwide = width;
    	int towerOneStarthigh = (height/100)*5;
    	int startingHigh = height - towerOneStarthigh;
    	int towerOneEndhigh = towerOneStarthigh + (startingHigh/6);
    	int towerTwoEndhigh = towerOneEndhigh + (startingHigh/6);
    	int towerThreeEndhigh = towerTwoEndhigh + (startingHigh/6);
    	int towerFourEndhigh = towerThreeEndhigh + (startingHigh/6);
    	int towerFiveEndhigh = towerFourEndhigh + (startingHigh/6);
    	int towerSixEndhigh = towerFiveEndhigh + (startingHigh/6);
    	int srcX, srcY, d, i, j;
    	double health;
    	int resistType, damageType;
    	long time;
    	int aoeX, aoeY, aoeRange;
    	double temp;
    	double temp2;
    	double temp4;
    	double temp5;
        int xTower, yTower;
        double damage;
        boolean otherThing;
        int lastHit;
        int targetMode;
        Random random = new Random();
        float temp3;
        int currentResist, currentAttribute;
        boolean canPlaceThere, legalSpot;
        
        //2 = air
		//3 = water
		//4 = earth
		//5 = fire
        							/*		air		water	earth	fire*/
        int currentElementsOwned[][] = {{	1,1,1,1,	2,2,2,2,	3,3,3,3},
        							   {	0,0,0,0,	0,0,0,0,	0,0,0,0}};
        
        String scoreTop, scoreTopTwo, scoreTopThree, interestCounter, moneyMoney, livesLeftScore, totalScoreTop, levelAir, levelWater, levelEarth, levelFire;;
    	GraphicObject Gsprite, placingSprite;
        Baddies Bsprite;
        Baddies aoeSprite;
        GraphicObject.Coordinates coords;
        Baddies.Coordinates badCoords, badCoordsAoe;
        GraphicObject.towerType towerType;
    	
        Rect src = new Rect(0,0,0,0);
        Rect dst = new Rect(0,0,0,0);
        
        

		public Panel(Context context) 
    	{
            super(context);
            fillBitmapCache();
            getHolder().addCallback(this);
            handler = new Handler();
            _thread = new theThread(getHolder(), this, handler);
            vibe = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
  //          handler = new Handler();
            setFocusable(true);
            
            for(int iiii = 0; iiii < 16; iiii++)
            {
            	point[0][iiii] = (int) ((double)point[0][iiii] * widthRatio);
            	point[1][iiii] = (int) ((double)point[1][iiii] * heightRatio);
            }
        }
		
		private void createBads()
		{
			wave++;
			if(continuousMode == 1)
			{
				timeLeft += 20;
			}
			else if(continuousMode == 0)
			{
				timeLeft += 35;
			}
			temp = random.nextInt(5);
			temp2 = random.nextInt(5);

			if(wave == 1)
			{
				_badGuys.add(createBad(R.drawable.bad2, 440*2, 0, temp, temp2, false, 0));
				_badGuys.add(createBad(R.drawable.bad2, 400*2, 1, temp, temp2, false, 0));
				_badGuys.add(createBad(R.drawable.bad2, 360*2, 2, temp, temp2, false, 0));
				_badGuys.add(createBad(R.drawable.bad2, 320*2, 3, temp, temp2, false, 0));
				_badGuys.add(createBad(R.drawable.bad2, 280*2, 4, temp, temp2, false, 0));
				_badGuys.add(createBad(R.drawable.bad2, 240*2, 5, temp, temp2, false, 0));
				_badGuys.add(createBad(R.drawable.bad2, 200*2, 6, temp, temp2, false, 0));
				_badGuys.add(createBad(R.drawable.bad2, 160*2, 7, temp, temp2, false, 0));
				_badGuys.add(createBad(R.drawable.bad2, 120*2, 8, temp, temp2, false, 0));
				_badGuys.add(createBad(R.drawable.bad2, 80*2, 9, temp, temp2, false, 0));
			}
			else if(wave > 1)
			{
				_badGuys.add(createBad(R.drawable.bad2, 440, 0, temp, temp2, false, 0));
				_badGuys.add(createBad(R.drawable.bad2, 400, 1, temp, temp2, false, 0));
				_badGuys.add(createBad(R.drawable.bad2, 360, 2, temp, temp2, false, 0));
				_badGuys.add(createBad(R.drawable.bad2, 320, 3, temp, temp2, false, 0));
				_badGuys.add(createBad(R.drawable.bad2, 280, 4, temp, temp2, false, 0));
				_badGuys.add(createBad(R.drawable.bad2, 240, 5, temp, temp2, false, 0));
				_badGuys.add(createBad(R.drawable.bad2, 200, 6, temp, temp2, false, 0));
				_badGuys.add(createBad(R.drawable.bad2, 160, 7, temp, temp2, false, 0));
				_badGuys.add(createBad(R.drawable.bad2, 120, 8, temp, temp2, false, 0));
				_badGuys.add(createBad(R.drawable.bad2, 80, 9, temp, temp2, false, 0));
			}
			if(wave % 6 == 0)
			{
				boolean repeater = true;
				int user;
				while(repeater)
				{
					temp4 = random.nextInt(12); //randomly selecting boss element
					temp5 = temp4 % 4;
					user = currentElementsOwned[0][(int)temp4];
					if(nextElement != 0)
					{
						switch(nextElement)
						{
						case 2:
							_badGuys.add(createBad(R.drawable.boss_air, 200, 10, 1.0, 0.0, true, currentAir));
							if(currentAir == 0)
								currentElementsOwned[1][0] = 1;
							if(currentAir == 1)
								currentElementsOwned[1][4] = 1;
							if(currentAir == 2)
								currentElementsOwned[1][8] = 1;	
							repeater = false;
							break;
						case 3:
							_badGuys.add(createBad(R.drawable.boss_water, 200, 10, 2.0, 0.0, true, currentWater));
							currentElementsOwned[1][(int)temp4] = 1;
							if(currentWater == 0)
								currentElementsOwned[1][1] = 1;
							if(currentWater == 1)
								currentElementsOwned[1][5] = 1;
							if(currentWater == 2)
								currentElementsOwned[1][9] = 1;
							repeater = false;
							break;
						case 4:
							_badGuys.add(createBad(R.drawable.boss_earth, 200, 10, 3.0, 0.0, true, currentEarth));
							currentElementsOwned[1][(int)temp4] = 1;
							if(currentEarth == 0)
								currentElementsOwned[1][2] = 1;
							if(currentEarth == 1)
								currentElementsOwned[1][6] = 1;
							if(currentEarth == 2)
								currentElementsOwned[1][10] = 1;
							repeater = false;
							break;
						case 5:
							_badGuys.add(createBad(R.drawable.boss_fire, 200, 10, 4.0, 0.0, true, currentFire));
							currentElementsOwned[1][(int)temp4] = 1;
							if(currentFire == 0)
								currentElementsOwned[1][3] = 1;
							if(currentFire == 1)
								currentElementsOwned[1][7] = 1;
							if(currentFire == 2)
								currentElementsOwned[1][11] = 1;
							repeater = false;
							break;
						}
						
					}
					else if(currentElementsOwned[1][(int)temp4] == 0)
					{
						switch((int) temp5)
						{
						case 0:
							if(user == currentAir+1)
							{
								_badGuys.add(createBad(R.drawable.boss_air, 200, 10, 1.0, 0.0, true, currentAir));
								currentElementsOwned[1][(int)temp4] = 1;
								repeater = false;
							}
							break;
						case 1:
							if(user == currentWater+1)
							{
								_badGuys.add(createBad(R.drawable.boss_water, 200, 10, 2.0, 0.0, true, currentWater));
								currentElementsOwned[1][(int)temp4] = 1;
								repeater = false;
							}
							break;
						case 2:
							if(user == currentEarth+1)
							{
								_badGuys.add(createBad(R.drawable.boss_earth, 200, 10, 3.0, 0.0, true, currentEarth));
								currentElementsOwned[1][(int)temp4] = 1;
								repeater = false;
							}
							break;
						case 3:
							if(user == currentFire+1)
							{
								_badGuys.add(createBad(R.drawable.boss_fire, 200, 10, 4.0, 0.0, true, currentFire));
								currentElementsOwned[1][(int)temp4] = 1;
								repeater = false;
							}
							break;
						}
					}		
				}
				nextElement = 0;
			}
		}
		
		// 0 = Fast - Every 3 seconds, this creep moves 2.5x faster for 1 second
		// 1 = Healing - This creep heals nearby creeps for 20% of Max HP when killed.
		// 2 = Mechanical - Every 12 seconds, this creep becomes invulnerable for 3 seconds.
		// 3 = Undead - This creep will revive after 3 seconds with 33% of Max HP when killed.
		// 4 = no attribute
		
		private Baddies createBad(int resource, int x, int y, double resist, double attribute, boolean boss, int bossLevel)
		{
			totalAlive++;
			Bitmap bmp = null;
			bmp = BitmapFactory.decodeResource(getResources(), resource);
			return new Baddies(bmp, this, (int)((0.0 - (double)x)*widthRatio), (int)(330.0*heightRatio), y, 2, (int)resist, (int)attribute, badHealth[wave-1], badBounty[wave-1], boss, bossLevel, widthRatio, heightRatio);
		}
		
		private void fillBitmapCache() 
		{
	        _bitmapCache.put(R.drawable.icon, BitmapFactory.decodeResource(getResources(), R.drawable.icon));
	        _bitmapCache.put(R.drawable.lvlone, BitmapFactory.decodeResource(getResources(), R.drawable.lvlone));
		}
		
		
		
		@Override
		public boolean onTouchEvent(final MotionEvent event)
		{
			
			handler.post(new Runnable()
			{

				public void run() 
				{
					int x = (int) event.getX();
					int y = (int) event.getY();
							
			        GraphicObject graphic = null;
			   
			        if (event.getAction() == MotionEvent.ACTION_DOWN) 
			        {
			        	if(x >=towerStartwide  && x <= towerEndwide)
			        	{
			        		if(y > towerOneStarthigh && y < towerOneEndhigh)
			        		{
			        			newObject = true;
			        			graphic = new GraphicObject(BitmapFactory.decodeResource(getResources(), R.drawable.laser_base), System.currentTimeMillis(), 120, 1500, 2, false, 0, (int)event.getX(), (int)event.getY(), 7, 13, widthRatio, heightRatio);
			        			graphic.getTowerType().setType(0);
			        			_currentGraphic = graphic;
			        		}
			        		if(y > towerOneEndhigh && y < towerTwoEndhigh)
			        		{
			        			newObject = true;
			        			graphic = new GraphicObject(BitmapFactory.decodeResource(getResources(), R.drawable.grenade_lvl1_base), System.currentTimeMillis(), 100, 1500, 1, false, 0, (int)event.getX(), (int)event.getY(), 7, 13, widthRatio, heightRatio);
			        			graphic.getTowerType().setType(1);		        			
			        			_currentGraphic = graphic;
			        		}
			        		if(y > towerTwoEndhigh && y < towerThreeEndhigh && currentAir > 0)
			        		{
			        			newObject = true;
			        			graphic = new GraphicObject(BitmapFactory.decodeResource(getResources(), R.drawable.air_lvl1_base), System.currentTimeMillis(), 220, 660, 4, false, 1, (int)event.getX(), (int)event.getY(), 50, 125, widthRatio, heightRatio);
			        			graphic.getTowerType().setType(2);		        			
			        			_currentGraphic = graphic;
			        		}	
			        		if(y > towerThreeEndhigh && y < towerFourEndhigh && currentWater > 0)
			        		{
			        			newObject = true;
			        			graphic = new GraphicObject(BitmapFactory.decodeResource(getResources(), R.drawable.grenade_lvl1_base), System.currentTimeMillis(), 120, 660, 5, false, 2, (int)event.getX(), (int)event.getY(), 50, 125, widthRatio, heightRatio);
			        			graphic.getTowerType().setType(3);		        			
			        			_currentGraphic = graphic;
			        		}	
			        		if(y > towerFourEndhigh && y < towerFiveEndhigh && currentEarth > 0)
			        		{
			        			newObject = true;
			        			graphic = new GraphicObject(BitmapFactory.decodeResource(getResources(), R.drawable.earth_lvl1_base), System.currentTimeMillis(), 110, 1000, 2, false, 3, (int)event.getX(), (int)event.getY(), 50, 125, widthRatio, heightRatio);
			        			graphic.getTowerType().setType(4);		        			
			        			_currentGraphic = graphic;
			        		}	
			        		if(y > towerFiveEndhigh && currentFire > 0)
			        		{
			        			newObject = true;
			        			graphic = new GraphicObject(BitmapFactory.decodeResource(getResources(), R.drawable.fire_lvl1_base), System.currentTimeMillis(), 100, 310, 1, false, 4, (int)event.getX(), (int)event.getY(), 50, 125, widthRatio, heightRatio);
			        			graphic.getTowerType().setType(5);		        			
			        			_currentGraphic = graphic;
			        		}
			        	}		        	
			        	else
			        	{
			        		newObject = false;
			        	}
			        	
			        	//AIR - pentagon
			        	//WATER - sphere
			        	//EARTH - octagon
			        	//FIRE - triangle
			/*        	
			        	Fire/Earth � Forge, increases nearby tower damage and fire rate.
			        	Fire/Water � Vapor, slow projectile that affects a large area upon impact.
			        	Fire/Air � Lightning, lightning bolt will chain an additional three targets after first.
			        	Earth/Water � Selerity, deals damage based on distance to target 
			        	Earth/Air � Quark, rotating laser that can go slower for more damage, or vise versa
			        	Water/Air � Jet, heavy hitting projectile that slows its target. Cannot attack same target twice. 
	
			        	 
	
			        	Fire/Earth/Air � Haste, every attack increases tower's attack speed
			        	Fire/Earth/Water � no name yet, directional aoe flamethrower, deals a stacking DoT to targets in front of its cone of fire.
			        	Earth/Water/Air � Hail, med-high damage tower that can attack up to three targets at the same time.
			        	
			*/        	
			        	
						for(int i = _graphics.size()-1; i >= 0; i--) 
						{ 
		                    final GraphicObject sprite = _graphics.get(i); 
		                    towerType = sprite.getTowerType();
		    	            coords = sprite.getCoordinates();
		                    //0 = laser
		                    //1 = grenade
		            		//2 = air
		            		//3 = water
		            		//4 = earth
		            		//5 = fire
		                    
		                    if (sprite.isCollision(event.getX(),event.getY())) 
		                    { 	
		                    	final Dialog dialog = new Dialog(MapGame.this);
		                    	dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		                    	dialog.setContentView(R.layout.popuptest);
		                    	dialog.setCancelable(true);
		                    	dialog.setCanceledOnTouchOutside(true);
		                    	ImageButton option1 = (ImageButton) dialog.findViewById(R.id.popupButton0);
		                    	ImageButton option2 = (ImageButton) dialog.findViewById(R.id.popupButton1);
		                    	ImageButton option3 = (ImageButton) dialog.findViewById(R.id.popupButton2);
		                    	ImageButton option4 = (ImageButton) dialog.findViewById(R.id.popupButton3);
		                    	ImageButton option5 = (ImageButton) dialog.findViewById(R.id.popupButton4);
		                    	ImageButton option6 = (ImageButton) dialog.findViewById(R.id.popupButton5);
		                    	final TextView towerlevel = (TextView) dialog.findViewById(R.id.towerLevel);
		                    	final TextView towertype = (TextView) dialog.findViewById(R.id.towerType);
		                    	final TextView towerrange = (TextView) dialog.findViewById(R.id.towerRange);
		                    	final TextView towerdamage = (TextView) dialog.findViewById(R.id.towerDamage);
		                    	final TextView towerrate = (TextView) dialog.findViewById(R.id.towerRate);
		                    	final TextView towercost = (TextView) dialog.findViewById(R.id.towerCost);
		                    	dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
		                    	dialog.getWindow().setGravity(300);
		                    	
		                    	
		                    	//0 = laser
			                    //1 = grenade
			            		//2 = air
			            		//3 = water
			            		//4 = earth
			            		//5 = fire
		                    	switch(sprite.getTowerType().getType())
		                    	{
		                    	case 0:
		                    		towerlevel.setText(Integer.toString(sprite.getTowerType().getLevel()+1) + ' ');
		                    		towertype.setText("Laser");
		                    		towerrange.setText(Integer.toString(sprite.getTowerType().getRange()));
		                    		towerdamage.setText(Integer.toString(sprite.getTowerType().getDamage()));
		                    		towerrate.setText(Double.toString(sprite.getTowerType().getFireRate()/1000.0));
		                    		towercost.setText(Integer.toString(sprite.getTowerType().getUpgradeCost()));
		                    		option2.setBackgroundResource(R.drawable.selector_not_allowed);
		                    		option3.setBackgroundResource(R.drawable.selector_not_allowed);
		                    		option4.setBackgroundResource(R.drawable.selector_not_allowed);
		                    		break;
		                    	case 1:
		                    		towerlevel.setText(Integer.toString(sprite.getTowerType().getLevel()+1) + ' ');
		                    		towertype.setText("Grenade");
		                    		towerrange.setText(Integer.toString(sprite.getTowerType().getRange()));
		                    		towerdamage.setText(Integer.toString(sprite.getTowerType().getDamage()));
		                    		towerrate.setText(Double.toString(sprite.getTowerType().getFireRate()/1000.0));
		                    		towercost.setText(Integer.toString(sprite.getTowerType().getUpgradeCost()));
		                    		option2.setBackgroundResource(R.drawable.selector_not_allowed);
		                    		option3.setBackgroundResource(R.drawable.selector_not_allowed);
		                    		option4.setBackgroundResource(R.drawable.selector_not_allowed);
		                    		break;
		                    	case 2:
		                    		towerlevel.setText(Integer.toString(sprite.getTowerType().getLevel()+1) + ' ');
		                    		towertype.setText("Wind");
		                    		towerrange.setText(Integer.toString(sprite.getTowerType().getRange()));
		                    		towerdamage.setText(Integer.toString(sprite.getTowerType().getDamage()));
		                    		towerrate.setText(Double.toString(sprite.getTowerType().getFireRate()/1000.0));
		                    		towercost.setText(Integer.toString(sprite.getTowerType().getUpgradeCost()));
		                    		option2.setBackgroundResource(R.drawable.selector_earth_and_air);
		                    		option3.setBackgroundResource(R.drawable.selector_fire_and_air);
		                    		option4.setBackgroundResource(R.drawable.selector_water_and_air);
		                    		break;
		                    	case 3:
		                    		towerlevel.setText(Integer.toString(sprite.getTowerType().getLevel()+1) + ' ');
		                    		towertype.setText("Water");
		                    		towerrange.setText(Integer.toString(sprite.getTowerType().getRange()));
		                    		towerdamage.setText(Integer.toString(sprite.getTowerType().getDamage()));
		                    		towerrate.setText(Double.toString(sprite.getTowerType().getFireRate()/1000.0));
		                    		towercost.setText(Integer.toString(sprite.getTowerType().getUpgradeCost()));
		                    		option2.setBackgroundResource(R.drawable.selector_earth_and_water);
		                    		option3.setBackgroundResource(R.drawable.selector_fire_and_water);
		                    		option4.setBackgroundResource(R.drawable.selector_water_and_air);
		                    		break;
		                    	case 4:
		                    		towerlevel.setText(Integer.toString(sprite.getTowerType().getLevel()+1) + ' ');
		                    		towertype.setText("Earth");
		                    		towerrange.setText(Integer.toString(sprite.getTowerType().getRange()));
		                    		towerdamage.setText(Integer.toString(sprite.getTowerType().getDamage()));
		                    		towerrate.setText(Double.toString(sprite.getTowerType().getFireRate()/1000.0));
		                    		towercost.setText(Integer.toString(sprite.getTowerType().getUpgradeCost()));
		                    		option2.setBackgroundResource(R.drawable.selector_earth_and_air);
		                    		option3.setBackgroundResource(R.drawable.selector_earth_and_water);
		                    		option4.setBackgroundResource(R.drawable.selector_fire_and_earth);
		                    		break;
		                    	case 5:
		                    		towerlevel.setText(Integer.toString(sprite.getTowerType().getLevel()+1) + ' ');
		                    		towertype.setText("Fire");
		                    		towerrange.setText(Integer.toString(sprite.getTowerType().getRange()));
		                    		towerdamage.setText(Integer.toString(sprite.getTowerType().getDamage()));
		                    		towerrate.setText(Double.toString(sprite.getTowerType().getFireRate()/1000.0));
		                    		towercost.setText(Integer.toString(sprite.getTowerType().getUpgradeCost()));
		                    		option2.setBackgroundResource(R.drawable.selector_fire_and_air);
		                    		option3.setBackgroundResource(R.drawable.selector_fire_and_earth);
		                    		option4.setBackgroundResource(R.drawable.selector_fire_and_water);
		                    		break;
		                    	}
		                    	
		                    	option1.setOnClickListener(new View.OnClickListener() 
		                		{
		                    		
		                  			public void onClick(View opt) 
		                			{
		                  				if(whichClicked!=1)
		                  				{
	//	                  					towerlevel.setText(Integer.toString(sprite.getTowerType().getLevel()+1) + " (+1) ");
		                  					switch(sprite.getTowerType().getType())
		                  					{
		                  					case 0:
		                  						if(sprite.getTowerType().getLevel() == 0)
		                  						{
		            	                    		towerdamage.setText(Integer.toString(sprite.getTowerType().getDamage()) + " (+4)");
		            	                    		towercost.setText(Integer.toString(sprite.getTowerType().getUpgradeCost()));
		            	                    		sprite.getTowerType().setUpgrade(true);
		                  						}
		                  						if(sprite.getTowerType().getLevel() == 1)
		                  						{
		            	                    		towerdamage.setText(Integer.toString(sprite.getTowerType().getDamage()) + " (+12)");
		            	                    		towercost.setText(Integer.toString(sprite.getTowerType().getUpgradeCost()));
		            	                    		sprite.getTowerType().setUpgrade(true);
		                  						}		
		                  						break;
		                  						
		                  					case 1:
		                  						if(sprite.getTowerType().getLevel() == 0)
		                  						{
		            	                    		towerdamage.setText(Integer.toString(sprite.getTowerType().getDamage()) + " (+2)");
		            	                    		towercost.setText(Integer.toString(sprite.getTowerType().getUpgradeCost()));
		            	                    		sprite.getTowerType().setUpgrade(true);
		                  						}
		                  						if(sprite.getTowerType().getLevel() == 1)
		                  						{
		            	                    		towerdamage.setText(Integer.toString(sprite.getTowerType().getDamage()) + " (+6)");
		            	                    		towercost.setText(Integer.toString(sprite.getTowerType().getUpgradeCost()));
		            	                    		sprite.getTowerType().setUpgrade(true);
		                  						}		
		                  						break;
		                  					
		                  					case 2:
		                  						if(sprite.getTowerType().getLevel() == 0 && currentAir > 1)
		                  						{
		            	                    		towerdamage.setText(Integer.toString(sprite.getTowerType().getDamage()) + " (+16)");
		            	                    		towercost.setText(Integer.toString(sprite.getTowerType().getUpgradeCost()));
		            	                    		sprite.getTowerType().setUpgrade(true);
		                  						}
		                  						if(sprite.getTowerType().getLevel() == 1 && currentAir > 2)
		                  						{
		            	                    		towerdamage.setText(Integer.toString(sprite.getTowerType().getDamage()) + " (+80)");
		            	                    		towercost.setText(Integer.toString(sprite.getTowerType().getUpgradeCost()));
		            	                    		sprite.getTowerType().setUpgrade(true);
		                  						}		
		                  						break;
		                  						
		                  					case 3:
		                  						if(sprite.getTowerType().getLevel() == 0 && currentWater > 1)
		                  						{
		            	                    		towerdamage.setText(Integer.toString(sprite.getTowerType().getDamage()) + " (+20)");
		            	                    		towercost.setText(Integer.toString(sprite.getTowerType().getUpgradeCost()));
		            	                    		sprite.getTowerType().setUpgrade(true);
		                  						}
		                  						if(sprite.getTowerType().getLevel() == 1 && currentWater > 2)
		                  						{
		            	                    		towerdamage.setText(Integer.toString(sprite.getTowerType().getDamage()) + " (+100)");
		            	                    		towercost.setText(Integer.toString(sprite.getTowerType().getUpgradeCost()));
		            	                    		sprite.getTowerType().setUpgrade(true);
		                  						}		
		                  						break;
		                  						
		                  					case 4:
		                  						if(sprite.getTowerType().getLevel() == 0 && currentEarth > 1)
		                  						{
		            	                    		towerdamage.setText(Integer.toString(sprite.getTowerType().getDamage()) + " (+8)");
		            	                    		towercost.setText(Integer.toString(sprite.getTowerType().getUpgradeCost()));
		            	                    		sprite.getTowerType().setUpgrade(true);
		                  						}
		                  						if(sprite.getTowerType().getLevel() == 1 && currentEarth > 2)
		                  						{
		            	                    		towerdamage.setText(Integer.toString(sprite.getTowerType().getDamage()) + " (+40)");
		            	                    		towercost.setText(Integer.toString(sprite.getTowerType().getUpgradeCost()));
		            	                    		sprite.getTowerType().setUpgrade(true);
		                  						}		
		                  						break;
		                  						
		                  					case 5:
		                  						if(sprite.getTowerType().getLevel() == 0 && currentFire > 1)
		                  						{
		            	                    		towerdamage.setText(Integer.toString(sprite.getTowerType().getDamage()) + " (+4)");
		            	                    		towercost.setText(Integer.toString(sprite.getTowerType().getUpgradeCost()));
		            	                    		sprite.getTowerType().setUpgrade(true);
		                  						}
		                  						if(sprite.getTowerType().getLevel() == 1 && currentFire > 2)
		                  						{
		            	                    		towerdamage.setText(Integer.toString(sprite.getTowerType().getDamage()) + " (+20)");
		            	                    		towercost.setText(Integer.toString(sprite.getTowerType().getUpgradeCost()));
		            	                    		sprite.getTowerType().setUpgrade(true);
		                  						}		
		                  						break;
		                  					}
		                  					if(vibeSet == 1)
		                  					{
		                  						vibe.vibrate(50);
		                  					}
		                  					whichClicked = 1;
		                  				}
	//	                  				sprite.getTowerType().addLvl();
		
		                			}
		                		});
		                    	
		                    	option2.setOnClickListener(new View.OnClickListener() 
		                		{
		                    		
		                  			public void onClick(View opt) 
		                			{
	
		                			}
		                		});
		                    	
		                    	option3.setOnClickListener(new View.OnClickListener() 
		                		{
		                    		
		                  			public void onClick(View opt) 
		                			{
	
		                			}
		                		});
		                    	
		                    	option4.setOnClickListener(new View.OnClickListener() 
		                		{
		                    		
		                  			public void onClick(View opt) 
		                			{
	
		                			}
		                		});
		                    	
		                    	option5.setOnClickListener(new View.OnClickListener() 
		                		{	
		                  			public void onClick(View opt) 
		                			{  
		                  				if(sprite.getTowerType().getUpgradeCost() <= scoreMoney && sprite.getTowerType().canUpgrade())
	              						{
			                  				switch(sprite.getTowerType().getType())
		                  					{
		                  					case 0:
		                  						scoreMoney -= sprite.getTowerType().getUpgradeCost();
		                  						sprite.getTowerType().addLvl(4, 12, 37);
		                  						towerlevel.setText(Integer.toString(sprite.getTowerType().getLevel()+1) + " ");
		                  						towerdamage.setText(Integer.toString(sprite.getTowerType().getDamage()));
		                  						towercost.setText(Integer.toString(sprite.getTowerType().getUpgradeCost()));
		                  						whichClicked = 0;
		                  						break;
		                  						
		                  					case 1:
		                  						scoreMoney -= sprite.getTowerType().getUpgradeCost();
		                  						sprite.getTowerType().addLvl(2, 6, 37);
		                  						towerlevel.setText(Integer.toString(sprite.getTowerType().getLevel()+1) + " ");
		                  						towerdamage.setText(Integer.toString(sprite.getTowerType().getDamage()));
		                  						towercost.setText(Integer.toString(sprite.getTowerType().getUpgradeCost()));
		                  						whichClicked = 0;
		                  						break;
			                  				
		                  					case 2:
		                  						scoreMoney -= sprite.getTowerType().getUpgradeCost();
		                  						sprite.getTowerType().addLvl(16, 80, 500);
		                  						towerlevel.setText(Integer.toString(sprite.getTowerType().getLevel()+1) + " ");
		                  						towerdamage.setText(Integer.toString(sprite.getTowerType().getDamage()));
		                  						towercost.setText(Integer.toString(sprite.getTowerType().getUpgradeCost()));
		                  						whichClicked = 0;
		                  						break;
		                  						
		                  					case 3:
		                  						scoreMoney -= sprite.getTowerType().getUpgradeCost();
		                  						sprite.getTowerType().addLvl(20, 100, 500);
		                  						towerlevel.setText(Integer.toString(sprite.getTowerType().getLevel()+1) + " ");
		                  						towerdamage.setText(Integer.toString(sprite.getTowerType().getDamage()));
		                  						towercost.setText(Integer.toString(sprite.getTowerType().getUpgradeCost()));
		                  						whichClicked = 0;
		                  						break;
		                  						
		                  					case 4:
		                  						scoreMoney -= sprite.getTowerType().getUpgradeCost();
		                  						sprite.getTowerType().addLvl(8, 40, 500);
		                  						towerlevel.setText(Integer.toString(sprite.getTowerType().getLevel()+1) + " ");
		                  						towerdamage.setText(Integer.toString(sprite.getTowerType().getDamage()));
		                  						towercost.setText(Integer.toString(sprite.getTowerType().getUpgradeCost()));
		                  						whichClicked = 0;
		                  						break;
		                  						
		                  					case 5:	
		                  						scoreMoney -= sprite.getTowerType().getUpgradeCost();
		                  						sprite.getTowerType().addLvl(4, 20, 500);
		                  						towerlevel.setText(Integer.toString(sprite.getTowerType().getLevel()+1) + " ");
		                  						towerdamage.setText(Integer.toString(sprite.getTowerType().getDamage()));
		                  						towercost.setText(Integer.toString(sprite.getTowerType().getUpgradeCost()));
		                  						whichClicked = 0;
		                  						break;
		                  					}
			                  				if(vibeSet == 1)
			                  				{
		                  						vibe.vibrate(50);
			                  				}
			                  				sprite.getTowerType().setUpgrade(false);
	              						}
		                			}
		                		});
		                    	
		                    	option6.setOnClickListener(new View.OnClickListener() 
		                		{	
		                  			public void onClick(View opt) 
		                			{ 
		                  				scoreMoney += (int)(.7 * (double)sprite.getTowerType().getTotalCost());
		                  				_graphicObject.add(sprite);
		                  				if(vibeSet == 1)
		                  				{
	                  						vibe.vibrate(50);
		                  				}
		                  				dialog.dismiss();
		                			}
		                		});
		                    	
		                    	dialog.setOnDismissListener(new OnDismissListener() 
		                    	{ 
		                    	    public void onDismiss(DialogInterface dialog) 
		                    	    { 
		                    	    	sprite.getTowerType().setDisplayRange(false);
	//	                    	        sprite.getTowerType().updateBitmap();
	//	                    	        sprite.getCoordinates().updateOffset();
		                    	    } 
		                    	}); 
		                    	
		                    	sprite.getTowerType().setDisplayRange(true);
		                    	sprite.getTowerType().updateBitmap();
		                    	dialog.show();
		                    	
		                    } 
						} 
			            
			        } 
			        else if (event.getAction() == MotionEvent.ACTION_MOVE && newObject == true) 
			        {
			        	_currentGraphic.getCoordinates().setX((int) event.getX());
			            _currentGraphic.getCoordinates().setY((int) event.getY());
			            
	  
					//						0		1		2		3		4		5		6		7		8		9		10		11		12		13		14		15
			//            int point[][] = {	{0,		84,		84,		400,	400,	175,	175,	610,	610,	496,	496,	705,	705,	80,		80,		0}, 	// X -- 0
			 //           					{330,	330,	450,	450,	334,	334,	190,	190,	340,	340,	450,	450,	76,		76,		235,	235}}; 	// Y -- 1
			        	
			        	if(event.getX() > point[0][0]-30 && event.getX() < point[0][1]+30 && event.getY() > point[1][0]-30 && event.getY() < point[1][1]+30 ||
			        	   event.getX() > point[0][1]-30 && event.getX() < point[0][2]+30 && event.getY() > point[1][1]-30 && event.getY() < point[1][2]+30 ||	
			        	   event.getX() > point[0][2]-30 && event.getX() < point[0][3]+30 && event.getY() > point[1][2]-30 && event.getY() < point[1][3]+30 ||
			        	   event.getX() > point[0][3]-30 && event.getX() < point[0][4]+30 && event.getY() < point[1][3]+30 && event.getY() > point[1][4]-30 ||
			        	   event.getX() < point[0][4]+30 && event.getX() > point[0][5]-30 && event.getY() > point[1][4]-30 && event.getY() < point[1][5]+30 ||
			        	   event.getX() > point[0][5]-30 && event.getX() < point[0][6]+30 && event.getY() < point[1][5]+30 && event.getY() > point[1][6]-30 ||
			        	   event.getX() > point[0][6]-30 && event.getX() < point[0][7]+30 && event.getY() > point[1][6]-30 && event.getY() < point[1][7]+30 ||
			        	   event.getX() > point[0][7]-30 && event.getX() < point[0][8]+30 && event.getY() > point[1][7]-30 && event.getY() < point[1][8]+30 ||
			        	   event.getX() < point[0][8]+30 && event.getX() > point[0][9]-30 && event.getY() > point[1][8]-30 && event.getY() < point[1][9]+30 ||
			        	   event.getX() > point[0][9]-30 && event.getX() < point[0][10]+30 && event.getY() > point[1][9]-30 && event.getY() < point[1][10]+30 ||
			        	   event.getX() > point[0][10]-30 && event.getX() < point[0][11]+30 && event.getY() > point[1][10]-30 && event.getY() < point[1][11]+30 ||
			        	   event.getX() > point[0][11]-30 && event.getX() < point[0][12]+30 && event.getY() < point[1][11]+30 && event.getY() > point[1][12]-30 ||
			        	   event.getX() < point[0][12]+30 && event.getX() > point[0][13]-30 && event.getY() > point[1][12]-30 && event.getY() < point[1][13]+30 ||
			        	   event.getX() > point[0][13]-30 && event.getX() < point[0][14]+30 && event.getY() > point[1][13]-30 && event.getY() < point[1][14]+30 ||
			        	   event.getX() < point[0][14]+30 && event.getX() > point[0][15]-30 && event.getY() > point[1][14]-30 && event.getY() < point[1][15]+30)
			        	{
			        		legalSpot = false;
			        	}
			        	else
			        	{
			        		legalSpot = true;
			        	}
			        	
			        	if(!_graphics.isEmpty())
			        	{
				        	for(int fff = _graphics.size()-1; x >= 0; x--)
				        	{
				        		placingSprite = _graphics.get(fff); 
					            
				        		if(Math.abs(placingSprite.getCoordinates().getX() - event.getX()) < (int)(60.0*widthRatio) && Math.abs(placingSprite.getCoordinates().getY() - event.getY()) < (int)(60.0*heightRatio))
				        		{
				        			canPlaceThere = false;
				        		}
				        		else
				        			canPlaceThere = true;
				        	}
			        	}
			        	else if(_graphics.isEmpty())
			        	{
			        		canPlaceThere = true;
			        	}
			        } 
			        else if (event.getAction() == MotionEvent.ACTION_UP && newObject == true) 
			        {
			        	canPlaceThere = true;
	/*		        	Context context = getApplicationContext();
	                	CharSequence text = "";
	                	switch(_currentGraphic.getTowerType().getType()) //800x480
	                	{
	                	case 0:
	                		text = "Tower Type " + "Laser placed at \n" + _currentGraphic.getCoordinates().getX() + " X-value \n" + _currentGraphic.getCoordinates().getY() + " Y-value";
	                		break;
	                	case 1:
	                		text = "Tower Type " + "Grenade placed at \n" + _currentGraphic.getCoordinates().getX() + " X-value \n" + _currentGraphic.getCoordinates().getY() + " Y-value";
	                		break;
	                	}
	                	
	                	int duration = Toast.LENGTH_SHORT;
	                	
	                	Toast toast = Toast.makeText(context, text, duration);
	                	toast.setGravity(Gravity.BOTTOM|Gravity.LEFT, 25, 25);
	                	toast.show();
	  */              	
			        	if(!_graphics.isEmpty())
			        	{
				        	for(int fff = _graphics.size()-1; x >= 0; x--)
				        	{
				        		placingSprite = _graphics.get(fff); 
					            
				        		if(Math.abs(placingSprite.getCoordinates().getX() - event.getX()) < (int)(60.0*widthRatio) && Math.abs(placingSprite.getCoordinates().getY() - event.getY()) < (int)(60.0*heightRatio))
				        		{
				        			canPlaceThere = false;
				        		}
				        		else
				        			canPlaceThere = true;
				        	}
			        	}
			        	else if(_graphics.isEmpty())
			        	{
			        		canPlaceThere = true;
			        	}
			        	
			        
								if(event.getX() < towerStartwide && _currentGraphic.getTowerType().getCost() <= scoreMoney && canPlaceThere && event.getY() > (towerOneStarthigh*3) && legalSpot)
					        	{
					        		_currentGraphic.getTowerType().updateBitmap();
					        		_currentGraphic.getTowerType().setDisplayRange(false);
					        		scoreMoney -= _currentGraphic.getTowerType().getCost();
					        		_graphics.add(_currentGraphic);
					        	}
					        	else if(_currentGraphic.getTowerType().getCost() > scoreMoney)
					        	{
					        		Context context = getApplicationContext();
				                	CharSequence text = "";
				                	text = "Not Enough Money";
				                	int duration = Toast.LENGTH_SHORT;
				                	Toast toast = Toast.makeText(context, text, duration);
				                	toast.setGravity(Gravity.BOTTOM|Gravity.LEFT, 25, 25);
				                	toast.show();
					        	}
							
			        	_currentGraphic = null;
			            newObject = false;
			        }	
				}
				
			});
					
				
				
			
			return true;
		}
		
        @Override
        public void onDraw(Canvas canvas) 
        {
        	lastTime = System.currentTimeMillis();
        	canvas.drawBitmap(Bitmap.createScaledBitmap(_bitmapCache.get(R.drawable.lvlone), width, height, true), 0, 0, null);
            health =  200;
            List<Baddies> _badIderator = new ArrayList<Baddies>();
 //           List<GraphicObject> _graphicObject = new ArrayList<GraphicObject>();
            temp3 = firstTime+1000;
            
            if(lastTime >= firstTime + 1000)
            {
            	timeLeft--;
            	firstTime = lastTime;
            }
            if(lastTime >= interestTime + 1000)
            {
            	interestCountdown--;
            	interestTime = lastTime;
            	if(interestCountdown == -1)
                {
                	scoreMoney = (int)((double)scoreMoney * 1.03);
                	interestCountdown = 15;
                }
            }
            
            scoreTop = "Wave " + wave + "/50";
            scoreTopTwo = "Enemies Left: " + totalAlive;
            scoreTopThree = "Time To Next Wave: " + timeLeft;
            interestCounter = "Interest: " + interestCountdown;
            moneyMoney = "$" + scoreMoney;
            livesLeftScore = "Lives Left: " + livesLeft;
            totalScoreTop = "Score: " + totalScore;
            levelAir = "" + currentAir;
            levelWater = "" + currentWater;
            levelFire = "" + currentFire;
            levelEarth = "" + currentEarth;
            
            
            
            Paint paint = new Paint(); 

            paint.setColor(Color.WHITE); 
            paint.setTextSize(20); 
            canvas.drawText(scoreTop, width/7, 20, paint); 
            canvas.drawText(scoreTopTwo, width/7, 40, paint);
            canvas.drawText(scoreTopThree, (width/8)*3, 20, paint);
            canvas.drawText(livesLeftScore, (width/8)*3, 40, paint);
            canvas.drawText(interestCounter, (width/16)*11, 20, paint);
            canvas.drawText(moneyMoney, (width/16)*14, 20, paint);
            canvas.drawText(totalScoreTop, (width/32)*19, 40, paint);
            
            
            paint.setColor(Color.WHITE);
            canvas.drawText(levelAir, (width/32)*25, 40, paint);
            paint.setColor(Color.CYAN);
            canvas.drawText(levelWater, (width/32)*26, 40, paint);
            paint.setColor(Color.argb(255, 221, 122, 38));
            canvas.drawText(levelEarth, (width/32)*27, 40, paint);
            paint.setColor(Color.RED);
            canvas.drawText(levelFire, (width/32)*28, 40, paint);
            // 0 = Fast - Every 3 seconds, this creep moves 2.5x faster for 1 second
    		// 1 = Healing - This creep heals nearby creeps for 20% of Max HP when killed.
    		// 2 = Mechanical - Every 12 seconds, this creep becomes invulnerable for 3 seconds.
    		// 3 = Undead - This creep will revive after 3 seconds with 33% of Max HP when killed.
    		// 4 = no attribute
            //0 = Composite, 1 = Air, 2 = Water, 3 = Earth, 4 = Fire
            switch((int)temp)
            {
            case 0:
            	canvas.drawBitmap(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.resist_composite), (int)(40.0*widthRatio), (int)(40.0*heightRatio), true), (int)(3.0*widthRatio), (int)(2.0*heightRatio), null);
            	break;
            case 1:
            	canvas.drawBitmap(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.resist_air), (int)(40.0*widthRatio), (int)(40.0*heightRatio), true), (int)(3.0*widthRatio), (int)(2.0*heightRatio), null);
            	break;
            case 2:
            	canvas.drawBitmap(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.resist_water), (int)(40.0*widthRatio), (int)(40.0*heightRatio), true), (int)(3.0*widthRatio), (int)(2.0*heightRatio), null);
            	break;
            case 3:
            	canvas.drawBitmap(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.resist_earth), (int)(40.0*widthRatio), (int)(40.0*heightRatio), true), (int)(3.0*widthRatio), (int)(2.0*heightRatio), null);
            	break;
            case 4:
            	canvas.drawBitmap(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.resist_fire), (int)(40.0*widthRatio), (int)(40.0*heightRatio), true), (int)(3.0*widthRatio), (int)(2.0*heightRatio), null);
            	break;
            }
            
            
            // 0 = Fast - Every 3 seconds, this creep moves 2.5x faster for 1 second
    		// 1 = Healing - This creep heals nearby creeps for 20% of Max HP when killed.
    		// 2 = Mechanical - Every 12 seconds, this creep becomes invulnerable for 3 seconds.
    		// 3 = Undead - This creep will revive after 3 seconds with 33% of Max HP when killed.
    		// 4 = no attribute

            switch((int)temp2)
            {
            case 0:
            	canvas.drawBitmap(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.attribute_speed), (int)(40.0*widthRatio), (int)(40.0*heightRatio), true),(int)(50.0*widthRatio), (int)(2.0*heightRatio), null);
            	break;
            case 1:
            	canvas.drawBitmap(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.attribute_healing), (int)(40.0*widthRatio), (int)(40.0*heightRatio), true),(int)(50.0*widthRatio), (int)(2.0*heightRatio), null);
            	break;
            case 2:
            	canvas.drawBitmap(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.attribute_invulnerable), (int)(40.0*widthRatio), (int)(40.0*heightRatio), true),(int)(50.0*widthRatio), (int)(2.0*heightRatio), null);
            	break;
            case 3:
            	canvas.drawBitmap(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.attribute_undead), (int)(40.0*widthRatio), (int)(40.0*heightRatio), true),(int)(50.0*widthRatio), (int)(2.0*heightRatio), null);
            	break;
            case 4:
            	canvas.drawBitmap(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.button_not_allowed), (int)(40.0*widthRatio), (int)(40.0*heightRatio), true),(int)(50.0*widthRatio), (int)(2.0*heightRatio), null);
            	break;
            }
            
            
            for(d = _graphics.size()-1; d >= 0; d--)
            {
            	Gsprite = _graphics.get(d); 
	            towerType = Gsprite.getTowerType();
	            coords = Gsprite.getCoordinates();
	            damage = towerType.getDamage();
	            if(towerType.BitmapShowing())
	            {
	            	xTower = coords.getX() - coords.getXoffset();
	            	yTower = coords.getY() - coords.getYoffset();
	            }
	            else
	            {
	            	
	            	xTower = coords.getX() - coords.getXoffset();
	            	yTower = coords.getY() - coords.getYoffset();
	            }
	            
	            towerType.setTarget(-1);
	            targetMode = 0;
            	for(i = _badGuys.size()-1; i >= 0; i--)	          
	            {
            		Bsprite = _badGuys.get(i); 
	            	badCoords = Bsprite.getCoordinates();
	            	
            		if(badCoords.getHealth() < health && badCoords.inRange(xTower, yTower, towerType.getRange()) && badCoords.isTargetable() && targetMode == 0)
            		{
            			health = badCoords.getHealth();
            			towerType.setTarget(badCoords.getNumber(), badCoords.getX() + badCoords.getXoffset(), badCoords.getY() + badCoords.getYoffset());
            		}
            		else if(badCoords.getNumber() > towerType.getTarget() && badCoords.inRange(xTower, yTower, towerType.getRange()) && badCoords.isTargetable())
            		{
            			health = badCoords.getHealth();
            			towerType.setTarget(badCoords.getNumber(), badCoords.getX() + badCoords.getXoffset(), badCoords.getY() + badCoords.getYoffset());
            		}
      
	            }
            	health = 400;
	           
	            for(i = _badGuys.size()-1; i >= 0; i--)
	            {	
	            	
	            	Bsprite = _badGuys.get(i); 
	            	badCoords = Bsprite.getCoordinates();
	            	if(badCoords.inRange(xTower, yTower, towerType.getRange(), towerType.getTarget()))
	            	{
	            		time = System.currentTimeMillis();
	            		towerType.setReady(true);
	            		if(time > towerType.getLastFired() + towerType.getFireRate())
	            		{
	            			towerType.setLastFired(time);
	            			otherThing = true;
	            			
	            			
	/*\/\/\/\/\/\/\/\/\/\/\/\/\/\/\*/          			
	/*           			
	            			_projectile.getCoordinates().setXstart(xTower);
	            			_projectile.getCoordinates().setYstart(yTower);
	            			_projectile.getCoordinates().setXend(badCoords.getX());
	            			_projectile.getCoordinates().setYend(badCoords.getY());
	            			_projectile.getCoordinates().setDirectionForLead(badCoords.getDirection());
	            			_projectile.getCoordinates().setSpeed(badCoords.getSpeed());
	  */          			
	/*\/\/\/\/\/\/\/\/\/\/\/\/\/\/\*/
	            			
	            			//	3.		.2.		.4.	   .1. 	  .3
	            			//earth  > water > fire > air > earth

	            			
	            			//resist! 0 = Composite, 1 = Air, 2 = Water, 3 = Earth, 4 = Fire
	            			

	            			switch(badCoords.getResistType())
	            			{
	            			case 0:
	            				badCoords.setHealth(((badCoords.getHealth() - (damage*(1.00 - (gameDiff/10)))))*.85);
	            				break;
	            			case 1:
	            				if(towerType.getDamageType() == 4) //fire -> air
	            				{
	            					badCoords.setHealth(badCoords.getHealth() - (damage * (1.00 - (gameDiff/10))*2));
	            				}
	            				else if(towerType.getDamageType() == 3) //earth -> air
	            				{
	            					badCoords.setHealth(badCoords.getHealth() - (damage * (1.00 - (gameDiff/10))*.5));
	            				}
	            				else
	            				{
	            					badCoords.setHealth(badCoords.getHealth() - (damage * (1.00 - (gameDiff/10))));
	            				}
	            				break;
	            			case 2:
	            				if(towerType.getDamageType() == 3) //earth -> water
	            				{
	            					badCoords.setHealth(badCoords.getHealth() - (damage * (1.00 - (gameDiff/10))*2));
	            				}
	            				else if(towerType.getDamageType() == 4) //fire -> water
	            				{
	            					badCoords.setHealth(badCoords.getHealth() - (damage * (1.00 - (gameDiff/10))*.5));
	            				}
	            				else
	            				{
	            					badCoords.setHealth(badCoords.getHealth() - (damage * (1.00 - (gameDiff/10))));	
	            				}
	            				break;
	            			case 3:
	            				if(towerType.getDamageType() == 1) //air -> earth
	            				{
	            					badCoords.setHealth(badCoords.getHealth() - (damage * (1.00 - (gameDiff/10))*2));
	            				}
	            				else if(towerType.getDamageType() == 2) // water -> earth
	            				{
	            					badCoords.setHealth(badCoords.getHealth() - (damage * (1.00 - (gameDiff/10))*.5));
	            				}
	            				else
	            				{
	            					badCoords.setHealth(badCoords.getHealth() - (damage * (1.00 - (gameDiff/10))));	
	            				}
	            				break;
	            			case 4:
	            				if(towerType.getDamageType() == 2) // water -> fire
	            				{
	            					badCoords.setHealth(badCoords.getHealth() - (damage * (1.00 - (gameDiff/10))*2));		
	            				}
	            				else if(towerType.getDamageType() == 1) // air -> fire
	            				{
	            					badCoords.setHealth(badCoords.getHealth() - (damage * (1.00 - (gameDiff/10))*.5));
	            				}
	            				else
	            				{
	            					badCoords.setHealth(badCoords.getHealth() - (damage * (1.00 - (gameDiff/10))));	
	            				}
	            				break;
	            			} 

	//            			badCoords.setHealth(((badCoords.getHealth() - (damage*(1-(gameDiff/10))))));
	            			Bsprite.drawBang(canvas, towerType.getType());
	            			
	            			towerType.setReady(false);
	            		}	
	            	}
	            	
	            	if(badCoords.getNumber() != towerType.getTarget() && badCoords.inRange(towerType.getTargetX(), towerType.getTargetY(), (double)towerType.getAoeRange(), towerType.AttackisAoe()) && otherThing )

	           		{
	            		switch(badCoords.getResistType())
            			{
            			case 0:
            				badCoords.setHealth(((badCoords.getHealth() - (damage*(1.00 - (gameDiff/10)))))*.85);
            				break;
            			case 1:
            				if(towerType.getDamageType() == 4) //fire -> air
            				{
            					badCoords.setHealth(badCoords.getHealth() - (damage * (1.00 - (gameDiff/10))*2));
            				}
            				else if(towerType.getDamageType() == 3) //earth -> air
            				{
            					badCoords.setHealth(badCoords.getHealth() - (damage * (1.00 - (gameDiff/10))*.5));
            				}
            				else
            				{
            					badCoords.setHealth(badCoords.getHealth() - (damage * (1.00 - (gameDiff/10))));
            				}
            				break;
            			case 2:
            				if(towerType.getDamageType() == 3) //earth -> water
            				{
            					badCoords.setHealth(badCoords.getHealth() - (damage * (1.00 - (gameDiff/10))*2));
            				}
            				else if(towerType.getDamageType() == 4) //fire -> water
            				{
            					badCoords.setHealth(badCoords.getHealth() - (damage * (1.00 - (gameDiff/10))*.5));
            				}
            				else
            				{
            					badCoords.setHealth(badCoords.getHealth() - (damage * (1.00 - (gameDiff/10))));	
            				}
            				break;
            			case 3:
            				if(towerType.getDamageType() == 1) //air -> earth
            				{
            					badCoords.setHealth(badCoords.getHealth() - (damage * (1.00 - (gameDiff/10))*2));
            				}
            				else if(towerType.getDamageType() == 2) // water -> earth
            				{
            					badCoords.setHealth(badCoords.getHealth() - (damage * (1.00 - (gameDiff/10))*.5));
            				}
            				else
            				{
            					badCoords.setHealth(badCoords.getHealth() - (damage * (1.00 - (gameDiff/10))));	
            				}
            				break;
            			case 4:
            				if(towerType.getDamageType() == 2) // water -> fire
            				{
            					badCoords.setHealth(badCoords.getHealth() - (damage * (1.00 - (gameDiff/10))*2));		
            				}
            				else if(towerType.getDamageType() == 1) // air -> fire
            				{
            					badCoords.setHealth(badCoords.getHealth() - (damage * (1.00 - (gameDiff/10))*.5));
            				}
            				else
            				{
            					badCoords.setHealth(badCoords.getHealth() - (damage * (1.00 - (gameDiff/10))));	
            				}
            				break;
            			} 
	   //        			badCoords.setHealth(((badCoords.getHealth() - (damage*(1-(gameDiff/10))))));
	           			Bsprite.drawBang(canvas, towerType.getType());
	           			otherThing = false;
	           		}
	            }  
            }
            
            for (GraphicObject graphic : _graphics) 
            {
                bitmap = graphic.getGraphic();
                coords = graphic.getCoordinates();
                towerType = graphic.getTowerType();
                towerType.drawRange(canvas);
                canvas.drawBitmap(bitmap, coords.getGraphicX(), coords.getGraphicY(), null);
                towerType.drawTop(canvas);
            }
         
            for(Baddies graphic : _badGuys)
            {
            	bitmap = graphic.getGraphic();
            	badCoords = graphic.getCoordinates();
            	badCoords.updateCoord();
            	if(badCoords.reachedTheEnd())
            	{
            		livesLeft--;
            		badCoords.resetTheEnd();
            	}
            	
            	graphic.drawHealthBar(canvas);
            	srcX = badCoords.getSrcX();
            	srcY = badCoords.getSrcY();
            	src.set(srcX, srcY, srcX + (badCoords.getWidth()/3), srcY + (badCoords.getHeight()/4));
            	dst.set(badCoords.getX(), badCoords.getY(), badCoords.getX() + (badCoords.getWidth()/3), badCoords.getY() + (badCoords.getHeight()/4));
            		
            	for(int i = _badGuys.size()-1; i >= 0; i--)
                {
            		
                	if(badCoords.getHealth() <= 0.0)
                	{
                		for(d = _graphics.size()-1; d >= 0; d--)
                        {
                			GraphicObject spritess = _graphics.get(d);
                			towerType = spritess.getTowerType();
                			if(towerType.getTarget() == badCoords.getNumber())
                			{
                				towerType.setTarget(-1);	
                			}
                        }
                		if(!badCoords.alreadyCounted())
                		{
                			totalAlive--;
                			badCoords.setCounted(true);
                			scoreMoney += badCoords.getBounty();
                			totalScore += badCoords.getBounty() * 2;
                			if(badCoords.isBoss())
                			{
                				switch(badCoords.getResistType())
                				{
                				case 1:
                					currentAir++;
                					break;
                				case 2:
                					currentWater++;
                					break;
                				case 3:
                					currentEarth++;
                					break;
                				case 4:
                					currentFire++;
                					break;
                				}
                			}
                		}
                		_badIderator.add(graphic);              		
                	}
                }
            	
/*            	if(badCoords.getHealth() <= 0)
            	{
            		dead++;
            	}
            	if(totalAlive == dead)
            	{	
                   	_badGuys = null;
                   	createBads();
            	}
  */          	
            	canvas.drawBitmap(bitmap, src, dst, null);
            }
            
            // draw current graphic at last...
            if (_currentGraphic != null) 
            {
                bitmap = _currentGraphic.getGraphic();
                coords = _currentGraphic.getCoordinates();
                _currentGraphic.getTowerType().drawRange(canvas);
                _currentGraphic.getTowerType().drawSpot(canvas, legalSpot, canPlaceThere);
                //canvas.drawBitmap(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.range_for_tower2), towerType.getRange()*2, towerType.getRange()*2, true), coords.getX() - towerType.getRange(), coords.getY() - towerType.getRange(), null);
                canvas.drawBitmap(bitmap, coords.getGraphicX(), coords.getGraphicY(), null);
                _currentGraphic.getTowerType().drawTop(canvas);
            };
            if(_currentBad != null)
            {
            	bitmap = _currentBad.getGraphic();
                badCoords = _currentBad.getCoordinates(); 
               	canvas.drawBitmap(bitmap, badCoords.getX(), badCoords.getY(), null);
            };
            _badGuys.removeAll(_badIderator);
            
            
            if(timeLeft <= 0)
            {
            	createBads();
            }
            
            else if(totalAlive == 0)
            {
            	if(continuousMode == 1)
            	{
            		timeLeft = 0;
            		
            	}
            	else if(timeLeft > 10 && continuousMode == 0)
            		timeLeft = 10;
            }
            if(livesLeft <= 0)
            {
            	startActivity(new Intent("elemental.combine.TD.MM"));
  //          	KeyEvent event =;
  //     		onGameOver();
  //          	onKeyDown(111, event );  
  //         	dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN,KeyEvent.KEYCODE_BACK));
            	
            }
           _graphics.removeAll(_graphicObject); 
           
           
        }

		public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) 
		{
			// TODO Auto-generated method stub
			
		}

		public void surfaceCreated(SurfaceHolder holder) 
		{
			createBads();
			_thread.setRunning(true);
		    _thread.start();
		}

		public void surfaceDestroyed(SurfaceHolder holder) 
		{
		    boolean retry = true;
		    _thread.setRunning(false);
		    while (retry) 
		    {
		        try 
		        {
		            _thread.join();
		            retry = false;
		        } 
		        catch (InterruptedException e) 
		        {
		            // we will try it again and again...
		        }
		    }
		}
		
		public boolean preOccupied(int x, int y)
		{
			return true;
		}
    }
    
    class theThread extends Thread 
    {
        private SurfaceHolder _surfaceHolder;
        private Panel _panel;
        private boolean _run = false;
        private Handler _handler;
        public theThread(SurfaceHolder surfaceHolder, Panel panel, Handler handler) 
        {
            _surfaceHolder = surfaceHolder;
            _panel = panel;
            _handler = handler;
        }
     

		public void setRunning(boolean run) 
        {
            _run = run;
        }
        
        public SurfaceHolder getSurfaceHolder() 
        {
            return _surfaceHolder;
        }
     
        @Override
        public void run() 
        {
        	Canvas c;
            while (_run) 
            {
            	while(_run && gamePaused)
            	{
            		try
            		{
            			Thread.sleep(100);
            		}
            		catch (InterruptedException e)
            		{
            			
            		}
            	}
            	
                c = null;
                try 
                {
      //          	Looper.prepare();
                	
    //            	Looper.loop();
                    c = _surfaceHolder.lockCanvas(null);
                    synchronized (_surfaceHolder) 
                    {
                        _panel.onDraw(c);
                    }
                    
                    
                } 
                catch (Exception e) 
                {
					e.printStackTrace();
				}
                finally 
                {
                    // do this in a finally so that if an exception is thrown
                    // during the above, we don't leave the Surface in an
                    // inconsistent state
                    if (c != null) 
                    {
                        _surfaceHolder.unlockCanvasAndPost(c);
                    }
                }
            }
        }
    }
    

    
/*/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\GraphicObject (The Towers)/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\*/
    
    class GraphicObject implements Serializable
    {
    	private Bitmap _map = BitmapFactory.decodeResource(getResources(), R.drawable.range_for_tower2);
    	private Bitmap _goodSpot = BitmapFactory.decodeResource(getResources(), R.drawable.spot_is_allowed);
    	private Bitmap _badSpot = BitmapFactory.decodeResource(getResources(), R.drawable.spot_not_allowed);
		private Bitmap _bitmap;
		private Bitmap _bmpGun;
        private Coordinates _coordinates;
        private towerType _type;
        private int width, mapWidth;
        private int height, mapHeight;
        private int currentDegree;
        private boolean attackIsAoe;
        private int AoeRange;
        private int x, graphicX;
        private int y, graphicY;
        private int offsetXreg, offsetXmap, offsetYreg, offsetYmap, offsetX, offsetY;
        private int currentFrame;
        private int range;
        private int damage;
        private int numTargets;
        private int targetNum;
        private long fireRate;
        private long lastFired;
        private int towerLvl;
        private int targetX, targetY;
        private int elementType;  //0 = Composite, 1 = Air, 2 = Water, 3 = Earth, 4 = Fire
        private boolean bitmapShowing;
        private boolean readyToFire;
        private boolean clickedOnce;
        private int clickedType;
        private int cost, totalCost, upgradeCost;
        private boolean displayRange;
        private boolean canUpgrade;
        private double widthRatio;
        private double heightRatio;
     
        
        //0 = laser tower
		//1 = grenade tower
		//2 = air
		//3 = water
		//4 = earth
		//5 = fire
        public GraphicObject(Bitmap bitmap, long lastFired, int range, int fireRate, int damage, boolean bitmapShowing, int elementType, int x, int y, int cost, int upgradeCost, double widthRatio, double heightRatio) 
        {
            _bitmap = bitmap;
            _coordinates = new Coordinates();
            _type = new towerType();
            this.width = bitmap.getWidth();
            this.height = bitmap.getHeight();
            this.x = x;
            this.y = y;       
            this.widthRatio = widthRatio;
            this.heightRatio = heightRatio;
            this.cost = cost;
            totalCost = cost;
            this.upgradeCost = upgradeCost;
            offsetXreg = width/2;
            offsetYreg = height/2;
            graphicX = x - offsetXreg;
            graphicY = y - offsetYreg;
            _coordinates.updateXY();
            mapWidth = 0;
            mapHeight = 0;
            this.range = (int) ((double)range * ((widthRatio+heightRatio)*.5));
            this.damage = damage;
            numTargets = 1;
            targetNum = -1;
            this.fireRate = fireRate;
            this.lastFired = lastFired;
            towerLvl = 0;
            this.bitmapShowing = bitmapShowing;
            this.elementType = elementType;
            currentDegree = 0;
            targetX = 0;
            targetY = 0;
            currentFrame = 0;
            readyToFire = true;
            attackIsAoe = false;
            clickedOnce = false;
            clickedType = 0;
            displayRange = true;
            canUpgrade = false;
        }
     
        public boolean isCollision(float x2, float y2) 
        {
        	if(x2 >= _coordinates.getX() - _type.getWidth()/2 &&
        		x2 <= _coordinates.getX() + _type.getWidth()/2 &&
        		y2 >= _coordinates.getY() - _type.getHeight()/2 &&
        		y2 <= _coordinates.getY() + _type.getHeight()/2)
        		return true;
        	else
        		return false;
        	
 //       	return x2 > x && x2 < x + width && y2 > y && y2 < y + height;
		}
        
        public Bitmap getGraphic() 
        {
            return _bitmap;
        }
     
        public Coordinates getCoordinates()
        {
            return _coordinates;
        }
        
        public towerType getTowerType()
        {
        	return _type;
        }
    	
    	
    	public class Coordinates 
    	{
    		private int _x = 100;
    		private int _y = 0;
    	     
            public void updateWithMapHeight() 
            {
				mapHeight = _bitmap.getHeight();
				offsetYmap = mapHeight/2;
				offsetY = (mapHeight/2) - (height/2); 
			}

			public int getGraphicX() 
			{
				if(bitmapShowing)
					return graphicX - offsetX;
				else
					return graphicX;
			}

			public int getGraphicY() 
			{
				if(bitmapShowing)
					return graphicY - offsetY;
				else
					return graphicY;
			}

			private void updateXY() 
			{
				_x = x;
				_y = y;
			}


			
			public int getXoffset()
			{
            	return offsetXreg;
			}
			
			public int getYoffset()
			{
            	return offsetYreg;
			}
			
            public int getX() 
            {
            	return x;
            } 
            
          
			public void setX(int value) 
            {
				_x = value;
				x = _x;
				graphicX = x - offsetXreg;
            }
     
            public int getY() 
            {
            	return y;
            }
     
            public void setY(int value) 
            {
            	_y = value;
                y = _y;
                graphicY = y - offsetYreg;
            }
    
            public String toString() 
            {
                return "Coordinates: (" + _x + "/" + _y + ")";
            }          
        }
    	
    	public class towerType
    	{
    		private int towerType;
    		//0 = laser tower
    		//1 = grenade tower
    		//2 = air
    		//3 = water
    		//4 = earth
    		//5 = fire
    		
    		public void setDisplayRange(boolean x)
    		{
    			displayRange = x;
    		}
    		
    		
    		public boolean getClicked()
    		{
    			return clickedOnce;
    		}
    		
    		public int getCost() 
    		{
				return cost;
			}
    		
    		public int getTotalCost()
    		{
    			return totalCost;
    		}
    		
    		public int getUpgradeCost()
    		{
    			return upgradeCost;
    		}

			public int getClickedType() 
    		{
				return clickedType;
			}

			public void setClicked(boolean b, int i)
    		{
    			clickedOnce = b;
    			clickedType = i;
    		}
    		
    		
    		public int getDamageType()
    		{
    			return elementType;
    		}
    		
    		public boolean BitmapShowing() 
    		{
				return bitmapShowing;
			}
    		
    		public boolean AttackisAoe()
    		{
    			return attackIsAoe;
    		}
    		
    		public int getAoeRange()
    		{
    			return AoeRange;
    		}

			private void setDegree()
    		{
    			if(targetX == 0 && targetY == 0)
    				currentDegree = 0;
    			else
    				currentDegree = (int) Math.toDegrees(Math.atan2((double)(targetY - y), (double)(targetX - x))) + 90;		
    		}
    		
    		public void setReady(boolean ready)
    		{
    			readyToFire = ready;
    		}
    		
    		//2 = air
    		//3 = water
    		//4 = earth
    		//5 = fire
    		
    		public void drawRange(Canvas canvas)
    		{
    			if(displayRange)
    				canvas.drawBitmap(Bitmap.createScaledBitmap(_map, range*2, range*2, true), x - range, y - range, null);
    		}
    		
    		public void drawSpot(Canvas canvas, boolean bool, boolean bool2)
    		{
    			if(bool && bool2)
    				canvas.drawBitmap(Bitmap.createScaledBitmap(_goodSpot, range, range, true), x - range/2, y - range/2, null);
    			else if(!bool || !bool2)
    				canvas.drawBitmap(Bitmap.createScaledBitmap(_badSpot, range, range, true), x - range/2, y - range/2, null);
    		}
    		
			public void drawTop(Canvas canvas) 
    		{
				Bitmap bmp;
				Matrix matrix;
        		switch(towerType)
        		{
        		case 0:
        			matrix = new Matrix();
        			setDegree();
        			matrix.setRotate(currentDegree, width/2, height/2);
        			matrix.postTranslate(x - offsetXreg, y - offsetYreg);
        			
        			if(!readyToFire || currentFrame < 6)
        				currentFrame = ++currentFrame % 7;
        			
        			if(targetNum == -1)
        				canvas.drawBitmap(Bitmap.createBitmap(_bmpGun, 0, 6 * (_bmpGun.getHeight()/7), _bmpGun.getWidth(), (_bmpGun.getHeight()/7)), matrix, new Paint());
        			else
        				canvas.drawBitmap(Bitmap.createBitmap(_bmpGun, 0, currentFrame * (_bmpGun.getHeight()/7), _bmpGun.getWidth(), (_bmpGun.getHeight()/7)), matrix, new Paint());
        			
        			break;
        			
        		case 1:
        			bmp = BitmapFactory.decodeResource(getResources(), R.drawable.grenade_turret_frames);
        			matrix = new Matrix();
        			setDegree();
        			matrix.setRotate(currentDegree, width/2, height/2);
        			matrix.postTranslate(x - offsetXreg, y - offsetYreg);
        			
        			if(!readyToFire || currentFrame < 6)
        				currentFrame = ++currentFrame % 7;
        			
        			if(targetNum == -1)
        				canvas.drawBitmap(Bitmap.createBitmap(bmp, 0, 6 * (bmp.getHeight()/7), bmp.getWidth(), (bmp.getHeight()/7)), matrix, new Paint());
        			else
        				canvas.drawBitmap(Bitmap.createBitmap(bmp, 0, currentFrame * (bmp.getHeight()/7), bmp.getWidth(), (bmp.getHeight()/7)), matrix, new Paint());
        			
        			break;
        			
        		case 2:
        			bmp = BitmapFactory.decodeResource(getResources(), R.drawable.air_turret_frames);
        			matrix = new Matrix();
        			setDegree();
        			matrix.setRotate(currentDegree, width/2, height/2);
        			matrix.postTranslate(x - offsetXreg, y - offsetYreg);
        			
        			if(targetNum == -1)
        				canvas.drawBitmap(Bitmap.createBitmap(bmp, 0, 6 * (bmp.getHeight()/7), bmp.getWidth(), (bmp.getHeight()/7)), matrix, new Paint());
        			else
        				canvas.drawBitmap(Bitmap.createBitmap(bmp, 0, currentFrame * (bmp.getHeight()/7), bmp.getWidth(), (bmp.getHeight()/7)), matrix, new Paint());
        			
        			break;
        			
        		case 3:
        			matrix = new Matrix();
        			setDegree();
        			matrix.setRotate(currentDegree, width/2, height/2);
        			matrix.postTranslate(x - offsetXreg, y - offsetYreg);
        			
        			if(targetNum == -1)
        				canvas.drawBitmap(Bitmap.createBitmap(_bmpGun, 0, 6 * (_bmpGun.getHeight()/7), _bmpGun.getWidth(), (_bmpGun.getHeight()/7)), matrix, new Paint());
        			else
        				canvas.drawBitmap(Bitmap.createBitmap(_bmpGun, 0, currentFrame * (_bmpGun.getHeight()/7), _bmpGun.getWidth(), (_bmpGun.getHeight()/7)), matrix, new Paint());
        			
        			break;
        			
        		case 4:
        			bmp = BitmapFactory.decodeResource(getResources(), R.drawable.earth_turret_frames);
        			matrix = new Matrix();
        			setDegree();
        			matrix.setRotate(currentDegree, width/2, height/2);
        			matrix.postTranslate(x - offsetXreg, y - offsetYreg);
        			
        			if(targetNum == -1)
        				canvas.drawBitmap(Bitmap.createBitmap(bmp, 0, 6 * (bmp.getHeight()/7), bmp.getWidth(), (bmp.getHeight()/7)), matrix, new Paint());
        			else
        				canvas.drawBitmap(Bitmap.createBitmap(bmp, 0, currentFrame * (bmp.getHeight()/7), bmp.getWidth(), (bmp.getHeight()/7)), matrix, new Paint());
        			
        			break;
        			
        		case 5:
        			bmp = BitmapFactory.decodeResource(getResources(), R.drawable.fire_turret_frames);
        			matrix = new Matrix();
        			setDegree();
        			matrix.setRotate(currentDegree, width/2, height/2);
        			matrix.postTranslate(x - offsetXreg, y - offsetYreg);
        			
        			if(targetNum == -1)
        				canvas.drawBitmap(Bitmap.createBitmap(bmp, 0, 6 * (bmp.getHeight()/7), bmp.getWidth(), (bmp.getHeight()/7)), matrix, new Paint());
        			else
        				canvas.drawBitmap(Bitmap.createBitmap(bmp, 0, currentFrame * (bmp.getHeight()/7), bmp.getWidth(), (bmp.getHeight()/7)), matrix, new Paint());
        			
        			break;
        			
        		}
        		
        		
			}

			public void setType(int type)
    		{
    			towerType = type;

    			switch(type)
    			{
    			case 0:
    				_bmpGun = BitmapFactory.decodeResource(getResources(), R.drawable.laser_lvl1);
    				break;
    			case 1:
    				attackIsAoe = true;
    				AoeRange = 60;
    				break;
    			case 3:
    				_bmpGun = BitmapFactory.decodeResource(getResources(), R.drawable.turret_lvl1_water_frames);
    				break;
    			case 4:
    				attackIsAoe = true;
    				AoeRange = 90;
    				break;
    			case 5:
    				attackIsAoe = true;
    				AoeRange = 90;
    				break;
    			}
    		}
    		

			public boolean canUpgrade()
			{
				return canUpgrade;
			}
			
			public void setUpgrade(boolean xxx)
			{
				canUpgrade = xxx;
			}
    		
    		public void addLvl(int a, int b, int cost)
    		{
    			totalCost += upgradeCost;
    			upgradeCost = cost;
    			if(towerLvl < 2)
    				towerLvl+=1;
    			if(towerLvl == 1)
    			{
    				damage += a;
    				
    			}
    			else if(towerLvl == 2)
    			{
    				damage += b;
    				upgradeCost = 0;
    			}
    			
    			updateBitmap();
    		}
    		
    		public void subLvl()
    		{
    			if(towerLvl > 0)
    				towerLvl-=1;
    			updateBitmap();
    		}
    		
    		public int getWidth()
    		{
    			if(bitmapShowing)
    				return mapWidth;
    			else
    				return width;
    		}
    		
    		public int getHeight()
    		{
    			if(bitmapShowing)
    				return mapHeight;
    			else
    				return height;
    		}
    		
    		public void updateBitmap()
    		{
    			
    			
    			if(towerLvl == 0)
    			{
    				switch(towerType)
    				{
    				case 0:
    					_bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.laser_base);
    					_bmpGun = BitmapFactory.decodeResource(getResources(), R.drawable.laser_lvl1);
    					break;
    				case 1:
    					_bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.grenade_lvl1_base);
    					break;
    				case 2:
    					_bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.air_lvl1_base);
    					break;
    				case 3:
    					_bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.grenade_lvl1_base);
    					_bmpGun = BitmapFactory.decodeResource(getResources(), R.drawable.turret_lvl1_water_frames);
    					break;
    				case 4:
    					_bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.earth_lvl1_base);
    					break;
    				case 5:
    					_bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.fire_lvl1_base);
    					break;
    				}
    			}
    			if(towerLvl == 1)
    			{
    				switch(towerType)
    				{
    				case 0:
    					_bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.laser_base);
    					_bmpGun = BitmapFactory.decodeResource(getResources(), R.drawable.laser_lvl2);
    					break;
    				case 1:
    					_bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.grenade_lvl2_base);
    					break;
    				case 2:
    					_bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.air_lvl2_base);
    					break;
    				case 3:
    					_bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.grenade_lvl2_base);
    					_bmpGun = BitmapFactory.decodeResource(getResources(), R.drawable.turret_lvl2_water_frames);
    					break;
    				case 4:
    					_bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.earth_lvl2_base);
    					break;
    				case 5:
    					_bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.fire_lvl2_base);
    					break;
    				}
    			}
    			if(towerLvl == 2)
    			{
    				switch(towerType)
    				{
    				case 0:
    					_bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.laser_base);
    					_bmpGun = BitmapFactory.decodeResource(getResources(), R.drawable.laser_lvl3);
    					break;
    				case 1:
    					_bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.grenade_lvl3_base);
    					break;
    				case 2:
    					_bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.air_lvl3_base);
    					break;
    				case 3:
    					_bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.grenade_lvl3_base);
    					_bmpGun = BitmapFactory.decodeResource(getResources(), R.drawable.turret_lvl3_water_frames);
    					break;
    				case 4:
    					_bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.earth_lvl3_base);
    					break;
    				case 5:
    					_bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.fire_lvl3_base);
    					break;
    				}
    			}
    			bitmapShowing = false;
//    			offsetYmap += offsetY;
//    			offsetXmap += offsetX;
//    			_coordinates.updateOffset();
    		}
    		
	
			public int getType()
    		{
    			return towerType;
    		}
    		
    		
    		public int getLevel()
    		{
    			return towerLvl;
    		}
    		
    		public int getRange()
    		{
    			return range;
    		}
    		
    		public int getDamage()
    		{
    			return damage;
    		}
    		
    		public int getTarget()
    		{
    			return targetNum;
    		}
    		
    		public void setTarget(int xx, int i, int j)
    		{
    			targetNum = xx;
    			targetX = i;
    			targetY = j;
    		}
    		
    		public int getTargetX()
    		{
    			return targetX;
    		}
    		
    		public int getTargetY()
    		{
    			return targetY;
    		}
    		
    		public void setTarget(int xx) 
    		{
				targetNum = xx;
			}
    		
    		public long getFireRate()
    		{
    			return fireRate;
    		}
    		
    		public long getLastFired() 
    		{
				return lastFired;
			}
    		
			public void setLastFired(long time) 
			{
				lastFired = time;
			}
			
			
			
    	}
    }
    
    
    /*\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/Class for BAD GUYS/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/*/
    
    class Baddies
    {
    	private Map<Integer, Bitmap> _bitmapCache = new HashMap<Integer, Bitmap>();
    	private static final int BMP_ROWS = 4; 
        private static final int BMP_COLUMNS = 3;
        private boolean revived;
    	private Bitmap _bitmap;
    	private Bitmap _bang;
    	private int width;
    	private int height;
    	private double x;
        private double y;
        private Coordinates _coordinates;
        private int current_location;
        private int direction;
        //direction = 0 up, 1 left, 2 down, 3 right;
        int[] DIRECTION_TO_ANIMATION_MAP = { 3, 1, 0, 2 };
        private int Speed;
        private Panel panel;
        private double health;
        private int badNumber;
        private long currentTime;
        private double tmp;
        private int tmp2;
        private double tmp3;
        private long lastTimeEnded;
        private int attState;
        private double maxHealth;
        private boolean targetable;
        private boolean invulnerable;
        private boolean alreadyCounted;
        private int bounty;
        private boolean reachedTheEnd;
        private boolean boss;
        private int bossLevel;
        private double widthRatio;
        private double heightRatio;
        
        // 0 = Fast - Every 3 seconds, this creep moves 2.5x faster for 1 second
		// 1 = Healing - This creep heals nearby creeps for 20% of Max HP when killed.
		// 2 = Mechanical - Every 12 seconds, this creep becomes invulnerable for 3 seconds.
		// 3 = Undead - This creep will revive after 3 seconds with 33% of Max HP when killed.
        
        private boolean startAttributing;
        private int Attribute;
        private int ResistType; //0 = Composite, 1 = Air, 2 = Water, 3 = Earth, 4 = Fire
        int currentFrame = 0;
        //					0		1		2		3		4		5		6		7		8		9		10		11		12		13		14		15
        int point[][] = {	{0,		84,		84,		400,	400,	175,	175,	610,	610,	496,	496,	705,	705,	80,		80,		0}, 	// X -- 0
        					{330,	330,	450,	450,	334,	334,	190,	190,	340,	340,	450,	450,	76,		76,		235,	235}, 	// Y -- 1
        					{3,		2,		3,		0,		1,		0,		3,		2,		1,		2,		3,		0,		1,		2, 		1,		2}};	// Direction
  	
    	public Baddies(Bitmap bitmap, Panel panel, int x, int y, int badNumber, int Speed, int ResistType, int Attribute, int health, int bounty, boolean boss, int bossLevel, double widthRatio, double heightRatio)
    	{
    		this.panel = panel;
    		_bitmap = bitmap;
    		_coordinates = new Coordinates();
    		this.width = bitmap.getWidth();
            this.height = bitmap.getHeight();
            current_location = 1;
            direction = 3;
            _coordinates.setX(x);
            _coordinates.setY(y);
            this.widthRatio = widthRatio;
            this.heightRatio = heightRatio;
            this.boss = boss;
        	this.ResistType = ResistType; 
            if(boss)
            {
            	this.bounty = bounty*3;

            	this.Speed = 3;
            	this.bossLevel = bossLevel;
            	switch(bossLevel)
            	{
            	case 0:
            		this.health = 125;
            		maxHealth = this.health;
            		break;
            	case 1:
            		this.health = 425;
            		maxHealth = this.health;
            		break;
            	case 2:
            		this.health = 1575;
            		maxHealth = this.health;
            		break;
            	}
            }
            else if(!boss)
            {
            	this.bounty = bounty;
            	this.health = health;
            	maxHealth = this.health;
            	this.Speed = Speed;

            }
            
            this.badNumber = badNumber;
            this.Attribute = Attribute;
            startAttributing = false;
            attState = 0;
            lastTimeEnded = System.currentTimeMillis() - 12000;
            targetable = true;
            if(Attribute == 3)
            	revived = false;
            else
            	revived = true;
            invulnerable = false;
            _bitmapCache.put(R.drawable.health100, BitmapFactory.decodeResource(getResources(), R.drawable.health100));
            alreadyCounted = false;
            reachedTheEnd = false;
            
            for(int i = 0; i < 16; i++)
            {
            	point[0][i] = (int) ((double)point[0][i] * widthRatio);
            	point[1][i] = (int) ((double)point[1][i] * heightRatio);
            }
            
    	}
    	
    	public void drawHealthBar(Canvas canvas)
    	{
//       	canvas.drawBitmap(Bitmap.createScaledBitmap(_bitmapCache.get(R.drawable.lvlone), width, height, true), 0, 0, null);
//    		canvas.scale(20, 100);
    		
    		tmp = health / maxHealth;
    		tmp2 = (int) (33.0 * tmp);
    		
    		if(tmp2 <= 0)
    			tmp2 = 1;
       		canvas.drawBitmap(Bitmap.createScaledBitmap(_bitmapCache.get(R.drawable.health100), tmp2, 4, true), _coordinates.getX(), _coordinates.getY() - 6, null);   		
    		
    	}
    	
    	public void drawBang(Canvas canvas, int towerType)
    	{
 //   		canvas.drawBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.bang), _coordinates.getX()-40, _coordinates.getY()-40, null);
    		if(startAttributing == false && !boss)
    		{
    			startAttributing = true;
    		}
    		
    		switch(towerType)
    		{
    		case 0:
    			canvas.drawBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.banglaser), _coordinates.getX()-((width/BMP_COLUMNS)/2), _coordinates.getY()-((height/BMP_ROWS)/2), null);
    			break;
    		case 1:
    			canvas.drawBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.bang), _coordinates.getX()-((width/BMP_COLUMNS)/2), _coordinates.getY()-((height/BMP_ROWS)/2), null);
    			break;
    		case 2:
    			canvas.drawBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.bangair), _coordinates.getX()-((width/BMP_COLUMNS)/2), _coordinates.getY()-((height/BMP_ROWS)/2), null);
    			break;
    		case 3:
    			canvas.drawBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.bangwater), _coordinates.getX()-((width/BMP_COLUMNS)/2), _coordinates.getY()-((height/BMP_ROWS)/2), null);
    			break;
    		case 4:
    			canvas.drawBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.bangearth), _coordinates.getX()-((width/BMP_COLUMNS)/2), _coordinates.getY()-((height/BMP_ROWS)/2), null);
    			break;
    		case 5:
    			canvas.drawBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.bangfire), _coordinates.getX()-((width/BMP_COLUMNS)/2), _coordinates.getY()-((height/BMP_ROWS)/2), null);
    			break;
    		}
    	}
    	
    
		public Bitmap getGraphic() 
        {
            return _bitmap;
        }
        
        public int get_location()
        {
        	return current_location;
        }
    	
        public Coordinates getCoordinates()
        {
            return _coordinates;
        }
    	
    	public class Coordinates 
    	{
    		private int _x = 0;
    		private int _y = 340;
    		private int _distance = 0;
    	     
            public int getX() 
            {
            	return _x + _bitmap.getWidth() / 6;
            }    
            
            public boolean isBoss()
        	{
        		return boss;
        	}
            
            public void setCounted(boolean b) 
            {
				alreadyCounted = b;
			}

            public int getBounty()
            {
            	return bounty;
            }
			public boolean alreadyCounted() 
            {
				return alreadyCounted;
			}

			public int getXoffset()
            {
            	return _bitmap.getWidth() / 6;
            }
            
            public void setX(int value) 
            {
                _x = value - _bitmap.getWidth() / 6;
                x = _x;
            }
     
            public int getY() 
            {
            	return _y + _bitmap.getHeight() / 8;
                
            }
            
            public int getYoffset()
            {
            	return _bitmap.getHeight() / 8;
            }
     
            public void setY(int value) 
            {
                _y = value - _bitmap.getHeight() / 8;
                y = _y;
            }
            
			public void setSpeed(int i) 
            {
				Speed = i;
			}

			// 0 = Fast - Every 3 seconds, this creep moves 2.5x faster for 1 second
			// 1 = Healing - This creep heals nearby creeps for 20% of Max HP when killed.
			// 2 = Mechanical - Every 12 seconds, this creep becomes invulnerable for 3 seconds.
			// 3 = Undead - This creep will revive after 3 seconds with 33% of Max HP when killed.
			// 4 = no attribute
            
            public void AttributeUpdate() 
            {
				if(startAttributing)
				{
					currentTime = System.currentTimeMillis();
					switch(Attribute)
					{
					case 0:
						if(currentTime > lastTimeEnded + 3000 && attState == 0)
						{
							_bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.bad2_speed);
							lastTimeEnded = currentTime;
							Speed = 6;
							attState = 1;	
						}
						if(currentTime > lastTimeEnded + 1000 && attState == 1)
						{
							_bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.bad2);
							lastTimeEnded = currentTime;
							Speed  = 2;
							attState = 0;
						}
						break;
					case 1:
						if(currentTime > lastTimeEnded + 20000 && attState == 0 && health < maxHealth)
						{
							lastTimeEnded = currentTime;
							health += health * (maxHealth * .2);
						}
						if(health > maxHealth)
							health = maxHealth;
						break;
					case 2:
						if(currentTime > lastTimeEnded + 12000 && attState == 0)
						{
							_bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.bad2_invulnerable);
							lastTimeEnded = currentTime;
							invulnerable = true;
					//		targetable = false;
							attState = 1;	
						}
						if(currentTime > lastTimeEnded + 3000 && attState == 1)
						{
							_bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.bad2);
							lastTimeEnded = currentTime;
							invulnerable = false;
					//		targetable = true;
							attState = 0;
						}
						break;
					case 3:
						if(targetable == false && revived == false && attState == 0)
						{
							_bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.bad2_undead);
							Speed = 0;
							lastTimeEnded = currentTime;
							attState = 1;
						}
						if(currentTime > lastTimeEnded + 3000 && attState == 1)
						{
							_bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.bad2);
							health = (maxHealth * 0.3);
							targetable = true;
							Speed = 2;
							revived = true;
							attState = 0;
						}
						break;
					}
				}
				
			}

            public int getAttribute()
            {
            	return Attribute;
            }
            
            public void setTargetable(boolean x)
            {
            	targetable = x;
            }
            
            public boolean isTargetable()
            {
            	return targetable;
            }
            
			public int getSpeed()
            {
            	return Speed;
            }
            
            public boolean maxHealth() 
            {
				if(health == maxHealth)
					return true;
				else
					return false;
			}
			     
            public String toString() {
                return "Coordinates: (" + _x + "/" + _y + ")";
            }
            
            public int getDirection()
            {
            	return direction;
            }
            
            public int getWidth()
            {
            	return width;
            }
            
            public int getHeight()
            {
            	return height;
            }
            
            public void setDirection(int value)
            {
            	direction = value;
            }
            
            public double getHealth()
            {
            	return health;
            }
            
            public int getResistType()
            {
            	return ResistType;
            }
            
            public void setHealth(double x)
            {
            	if(!invulnerable  && _x > 0)
            		health = x;
            	if(Attribute == 3 && health <= 0 && revived == false)
            	{
            		targetable = false;
            		health = 1;
            	}
            }
            
            public int getNumber()
            {
            	return badNumber;
            }
            
            private int getAnimationRow()
            {
            	//direction = 0 up, 1 left, 2 down, 3 right;
            	int x = 0;
            	switch(direction)
            	{
            	case 0:
            		x = 3;
            		break;
            	case 1:
            		x = 1;
            		break;
            	case 2:
            		x = 0;
            		break;
            	case 3:
            		x = 2;
            		break;
            	}
            	return x;
            }
            
            public int getSrcX()
            {
            	return (currentFrame * (width/BMP_COLUMNS)); 
            }
            
            public int getSrcY()
            {
            	return (getAnimationRow() * (height/BMP_ROWS));
            }
            
            public boolean inRange(double xx, double yy, double range)
            {
            	double distance = Math.sqrt(Math.pow(Math.abs(xx - x),2) + Math.pow(Math.abs(yy - y), 2));
            	if(distance <= range)
            		return true;
            	else
            		return false;
            }
            
            public boolean inRange(double xx, double yy, double range, int target)
            {
            	double distance = Math.sqrt(Math.pow(Math.abs(xx - x),2) + Math.pow(Math.abs(yy - y), 2));
            	if(distance <= range && target == badNumber)
            		return true;
            	else
            		return false;
            }
            
            public boolean inRange(double xx, double yy, double range, boolean Aoeable)
            {
            	double distance = Math.sqrt(Math.pow(Math.abs(xx - x),2) + Math.pow(Math.abs(yy - y), 2));
            	if(distance <= range && Aoeable == true)
            		return true;
            	else
            		return false;
            }
            
            public boolean reachedTheEnd()
            {
            	return reachedTheEnd;
            }
            
            public void resetTheEnd()
            {
            	reachedTheEnd = false;
            }
            
            public void updateCoord()
            {
            	AttributeUpdate();
                //direction = 0 up, 1 left, 2 down, 3 right;
            	currentFrame = ++currentFrame % BMP_COLUMNS;
            	
            	switch(direction)
            	{
            	case 0: //up
            		_y -= Speed;
            		y = _y;
            		if(_y <= point[1][current_location]-40)
                   	{
                   		direction = point[2][current_location];
                   		
                   		_distance = (point[1][current_location]-40) - _y;
            			_y += _distance;
            			y = _y;
                   		if(direction == 1)
                   		{
                   			_x -= _distance;
                   			x = _x;
                   		}
                   		else
                   		{
                   			_x += _distance;
                   			x = _x;
                   		}
                   		
                   		current_location++;
                   	}
            		break;
            	case 1: //left
            		_x -= Speed;
            		x = _x;
            		if(_x <= point[0][current_location]-40)
                   	{
                   		direction = point[2][current_location];
                   		
                   		_distance = (point[0][current_location]-40) - _x;
            			_x += _distance;
            			x = _x;
                   		if(direction == 0)
                   		{
                   			_y -= _distance;
                   			y = _y;
                   		}
                   		else
                   		{
                   			_y += _distance;
                   			y = _y;
                   		}
                   		
                   		current_location++;
                   	}
            		
           			if(current_location == 16)
           			{
           				_x = -40;
           				_y = 300;
           				x = _x;
           				y = _y;
           				current_location = 1;
           				direction = 3;
           				reachedTheEnd = true;
           			}
            		break;
            	case 2: //down
           			_y += Speed;
           			y = _y;
           			if(_y >= point[1][current_location]-40)
                   	{
                   		direction = point[2][current_location];
                   		
                   		_distance = _y - (point[1][current_location]-40);
            			_y -= _distance;
            			y = _y;
                   		if(direction == 1)
                   		{
                   			_x -= _distance;
                   			x = _x;
                   		}
                   		else
                   		{
                   			_x += _distance;
                   			x = _x;
                   		}
                   		
                   		current_location++;
                   	}
           			break;
           		case 3:	//right
           			_x += Speed;
           			x = _x;
           			if(_x >= point[0][current_location]-40)
                   	{
                   		direction = point[2][current_location];
                   		
                   		_distance = _x - (point[0][current_location]-40);
            			_x -= _distance;
            			x = _x;
                   		if(direction == 0)
                   		{
                   			_y -= _distance;
                   			y = _y;
                   		}
                   		else
                   		{
                   			_y += _distance;
                   			y = _y;
                   		}
                   		
                   		current_location++;
                   	}
           			break;
            	}
            }
        }
    }
    
    
    /*\/\/Class for PROJECTILES\/\/*/
    
    class Projectile
    {
    	private int position;
    	private int XstartLocation;
    	private int YstartLocation;
    	private int XendLocation;
    	private int YendLocation;
    	private int Xdistance;
    	private int Ydistance;
    	private int speed;
    	private int lead;
    	private Bitmap _bitmap;
    	private int _direction;
    	Coordinates _coordinates;
    	
    	public Projectile(Bitmap bitmap, int XstartLocation, int YstartLocation, int XendLocation, int YendLocation, int speed)
    	{
    		_bitmap = bitmap;
    		this.XstartLocation = XstartLocation;
    		this.YstartLocation = YstartLocation;
    		this.XendLocation = XendLocation;
    		this.YendLocation = YendLocation;
    		this.speed = speed;
    		position = 0;
    	}
    	
        public Coordinates getCoordinates()
        {
            return _coordinates;
        }
    	
    	public class Coordinates 
    	{
    		private int _x = 0;
    		private int _y = 340;
    	     
    		/*\/\/ Projectile Start Coordinates \/\/\*/		
    		
            public int getXstart() 
            {
                return XstartLocation + _bitmap.getWidth() / 2;
            }     
            
            public void setSpeed(int x)
            {
            	speed = x;
            }

			public void setXstart(int value) 
            {
                _x = value - _bitmap.getWidth() / 2;
                XstartLocation = _x;
            }
     
            public int getYstart() 
            {
                return YstartLocation + _bitmap.getHeight() / 2;
            }
     
            public void setYstart(int value) 
            {
                _y = value - _bitmap.getHeight() / 2;
                YstartLocation = _y;
            }

            /*\/\/ Projectile End Coordinates \/\/\*/	
            
            public int getXend() 
            {
                return XendLocation + _bitmap.getWidth() / 2;
            }     

			public void setXend(int value) 
            {
                _x = value - _bitmap.getWidth() / 2;
                XendLocation = _x;
            }
     
            public int getYend() 
            {
                return YendLocation + _bitmap.getHeight() / 2;
            }
     
            public void setYend(int value) 
            {
                _y = value - _bitmap.getHeight() / 2;
                YendLocation = _y;
            }
            
            
            public void setDirectionForLead(int direction)
            {
            	//direction = 0 up, 1 left, 2 down, 3 right;
            	_direction = direction;
            }
            
            public void leadTarget()
            {
            	//direction = 0 up, 1 left, 2 down, 3 right;
            	lead = speed*2;
            	switch(_direction)
            	{
            	case 0:
            		XendLocation -= lead;
            		break;
            	case 1:
            		YendLocation -= lead;
            		break;
            	case 2:
            		XendLocation += lead;
            		break;
            	case 3:
            		YendLocation +=lead;
            		break;
            	}
            	updateDistances();
            }
            
            private void updateDistances()
            {
            	Xdistance = Math.abs(XstartLocation - XendLocation);
            	Ydistance = Math.abs(YstartLocation - YendLocation);
            }
            
            public void updateCoordinates()
            {
            	
            }
    	}
    }
}