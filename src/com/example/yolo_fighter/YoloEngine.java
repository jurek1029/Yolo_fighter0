package com.example.yolo_fighter;

import com.google.android.gms.games.multiplayer.realtime.Room;
import com.google.example.games.basegameutils.GameHelper;

import android.content.Context;
import android.view.Display;

public class YoloEngine {

	public static final int GAME_THREAD_FSP_SLEEP = (1000/60);
	public static final int MENU_BUTTON_ALPAH = 0;
	public static final int BACKGROUND = R.drawable.level_01_1;
	public static final int PLAYER_TEXTURE = R.drawable.player_sprite;
	public static final int OPPONENT_TEXTURE = R.drawable.opponent_sprite; //	Multislayer ZMIENIĆ NA ODDZIELNĄ
	public static final int MOVE_TEXTURE = R.drawable.move_1;
	public static final int MOVE_BALL_TEXTURE =R.drawable.move_ball_1;
	public static final int MOVE_TEXTURE_1 = R.drawable.back;
	public static final int MOVE_BALL_TEXTURE_1 = R.drawable.ball;
	public static final int BUTTON_TEXTURE = R.drawable.button_sprite;
	public static final int WEAPON_SPRITE = R.drawable.weapon_sprite;
	public static final int LIVE_BAR_0 = R.drawable.ramka;
	public static final int LIVE_BAR_1 = R.drawable.pasek_srodek;
	public static final int POISON_SKILL = R.drawable.skill_sprite_poison;
	public static final int THUNDER_SKILL = R.drawable.skill_sprite_tunder;
	public static final int ARCHER_SPRITE = R.drawable.archer_sprite;
	
	public static float MAX_VALUE_PLAYER_SPEED = 100;
	public static float MIN_VALUE_TO_CLIMB = 30;
	public static float MOVE_SIZE_Y = 50;
	public static float MOVE_X = 125;
	public static float MOVE_Y = 50;
	public static float BUTTON_JUMP_SIZE = 100;
	public static float LIVE_BAR_SIZE = 350;
	public static float LEVEL_X = 2400;
	public static float LEVEL_Y = 1440;
	public static float LEVEL_SIZE_X;
	public static float LEVEL_SIZE_Y;
	//--------------------------------------------
	public static int SKILL1_COOLDOWN = 10;
	public static int SKILL2_COOLDOWN = 100;
	public static int SKILL3_COOLDOWN = 200;
	
	public static float SKILL_X;
	public static float SKILL_Y;
	
	public static boolean is = false;
	public static int usedSkill;
	
	public static final int ARCHER_STAND=0,ARCHER_WALK=1,ARCHER_FIRE=2,ARCHER_NOPE = 100;
	public static float ARCHER_SPEED = 0.0625f;
	//--------------------------------------------
	
	
	public static boolean isClasic = false;
	public static boolean isMoving = false;
	public static boolean isShoting = false;
	public static boolean isJumping = false;
	public static boolean isPlayerLeft = false;
	public static boolean isCrouch = false;
	public static boolean isCrouch_prest = false;
	public static boolean isClimbingUp = false;
	public static boolean isClimbingDown = false;
	public static boolean isClimbing = false;
	public static boolean isUsingSkill = false;
	public static boolean isSkillPressed = false;
	public static boolean canSkill1 = true;
	public static boolean canSkill2 = true;
	public static boolean canSkill3 = true;
	public static boolean canClimb = false;
	public static boolean canMove = true;
	
	public static final float GAME_PROJECTION_Y = 10f;
	public static float GAME_PROJECTION_X;
	public static final float GAME_METER = (1/GAME_PROJECTION_Y)/80;
	public static final float GAME_ACCELERATION = 10 * GAME_METER;
	public static final float GAME_GROUND_FRICTION = GAME_ACCELERATION;
	public static final float GAME_AIR_FRICTION = GAME_ACCELERATION/4;
	public static final float PLAYER_SIZE = 1;
	
	public static final int PLAYER_BULLET_FREQUENCY = 10; 
	public static final float PLAYER_LIVE_MAX = 100;
	public static final float PLAYER_CLIMBING_SPEED = 0.1f;
	
	public static float Player_x =3f;
	public static float Player_y =5f;
	
	public static float Player_vy =0;
	public static float Player_vx = 0f;
	public static float PlayerLive = 100;
	public static boolean isPlayerPoisoned = false;
	
	public static Display display;
	public static int display_x; 
	public static int display_y; 
	
	
	//Multislayer SEND XXX
	public static int SkillSprite1 = 4; //MiHu baza danych
	public static int SkillSprite2 = 5; //MiHu baza danych
	public static int SkillSprite3 = 6; //MiHu baza danych
	
	// ------------------------- Multislayer BEGIN -----------------------
	
	public static Room mRoom;
	public static GameHelper mHelper;
	public static int changesMade[] = new int[4];

	public static boolean MULTI_ACTIVE = false;

	public static int UPDATE_FREQ = 100;
	public static int MULTI_STEPS = 6*UPDATE_FREQ/100;

	public static int opponentsNo = 0;
	public static String opponents[] = new String[4];
	
	public static float Opponents_x[] = new float[4];
	public static float Opponents_y[] = new float[4];

	public static boolean Opponent_isCrouched[] = new boolean[4];
	
	public static boolean[] sprite_load = new boolean[30];
	
	public static YoloMultislayer mMultislayer = new YoloMultislayer();
	
	// ------------------------- Multislayer END -------------------------
	
	
	
//-----------------------------------------------------------------------------------------------------------	
   public static int whichLayout = 0;
//-------------------Player Sats & Info-----------------------------------------------------------------	
	public static YoloPlayerInfo currentPlayerInfo;	
	
	public static Context context;
	
}
