package com.example.yolo_fighter;

import com.google.android.gms.games.multiplayer.Participant;
import com.google.android.gms.games.multiplayer.realtime.Room;
import com.google.example.games.basegameutils.GameHelper;

import android.content.Context;
import android.media.SoundPool;
import android.view.Display;

import java.util.ArrayList;

public class YoloEngine {
	
	public static boolean test = false;
	
	public static SoundPool sp;	
	public static int[] SoundInd;
	public static float Volume = 1;
	public static final int GAME_THREAD_FSP_SLEEP = (1000/60);
	public static int GAME_SKIPED_FRAMES;
	public static final int MENU_BUTTON_ALPAH = 0;
	public static final int BACKGROUND = R.drawable.level_01_1;
	public static final int PLAYER_TEXTURE = R.drawable.player_sprite;
	public static final int OPPONENT_TEXTURE = R.drawable.opponent_sprite;
	public static final int BUTTON_TEXTURE = R.drawable.button_sprite;
	public static final int WEAPON_SPRITE = R.drawable.weapon_sprite;
	public static final int LIVE_BAR_0 = R.drawable.ramka;
	public static final int LIVE_BAR_1 = R.drawable.pasek_srodek;
	public static YoloPlayer[] TeamAB = {new YoloPlayer(1000f, 1000f, false, 666),new YoloPlayer(1000f, 1000f, false, 666),new YoloPlayer(1000f, 1000f, false, 666),new YoloPlayer(1000f, 1000f, false, 666)};//new YoloPlayer[4];
	public static boolean TeamA = false, TeamB = true;
	public static int MyID =0;
	public static int TeamSize = 2;
	
	public static final int POISON_SKILL = R.drawable.skill_sprite_poison;
	public static final int ARCHER_SPRITE = R.drawable.archer_sprite;
	public static final int WARRIOR_SPRITE = R.drawable.warrior_sprite;
	public static final int MUMMY_SPRITE = R.drawable.mummy_sprite;
	public static final int HAND_SPRITE = R.drawable.hand_sprite;
	public static final int BARREL_SPRITE = R.drawable.barrel_sprite;
	public static final int TOWER_SPRITE = R.drawable.tower_sprite;
	public static final int WALL_SPRITE = R.drawable.wall_sprite;
	public static final int LIVE_DRAIN_SPRITE = R.drawable.live_drain_sprite;
	public static final int RESURECTION_SPRITE = R.drawable.resurection_sprite;
	public static final int SPIKES_SPRITE = R.drawable.spikes_sprite;
	public static final int SLOW_DOWN_SPRITE = R.drawable.slow_down_sprite;
	
	public static final int TRAP_SPRITE = R.drawable.trap_sprite;
	public static final int HEAL_SPRITE = R.drawable.heal_sprite;
	public static final int HEAL_LONG_SPRITE = R.drawable.heal_long_sprite;
	public static final int HEALLONG_SPRITE = R.drawable.heallong_sprite;
	public static final int HEAL_LONG_RAD_SPRITE = R.drawable.heal_long_rad_sprite ;
	public static final int SHOCK_WAVE_SPRITE = R.drawable.shockwave_sprite;
	public static final int ICICLE_SPRITE = R.drawable.icicle_sprite;
	public static final int THUNDER_H_SPRITE = R.drawable.tunder_h_sprite;
	public static final int THUNDER_V_SKILL = R.drawable.skill_sprite_tunder;
	public static final int FOG_SPRITE = R.drawable.fog_s;
	public static final int DENIAL_SPRITE = R.drawable.denial_sprite;
	public static final int STAMINA_THIEF_SPRITE =0 ;
	
	public static final int FIRE_BALL_SPRITE = R.drawable.fireball_sprite;
	public static final int TELE_SPRITE = R.drawable.teleportacja_sprite;
	public static final int LAVA_SPRITE = R.drawable.lava_sprite;
	public static final int BUFF_EFFECE_SPRITE = R.drawable.buff_effect_sprite;
	public static final int THIEF_SPRITE = R.drawable.thief_sprite;
	public static final int STAMINA_SPRITE  = R.drawable.stamina_sprite;
	public static final int EARTHWAVE_SPRITE = R.drawable.earth_wave_sprite;
	
	public static int[] spriteSheets = new int[42];
	public static boolean[] sprite_load = new boolean[44];
	//----------------SCALE----------------------
	public static float TX = 100;//pixele
	public static float TY = 100;
	public static float Y_DDROP = 0.25f;
	public static float TEXTURE_SIZE_X = TX;
	public static float TEXTURE_SIZE_Y = TY;
	public static float MAX_VALUE_PLAYER_SPEED = 100;
	public static float MIN_VALUE_TO_CLIMB = 30;
	public static float MOVE_SIZE_Y = 50;
	public static float MOVE_X = 125;
	public static float MOVE_Y = 50;
	public static float BUTTON_JUMP_SIZE = 100;
	public static float LIVE_BAR_SIZE = 350;
    public static float LEVEL_scale =0.75f;
	public static float LEVEL_X = 2400;
	public static float LEVEL_Y = 1440;
	public static float LEVEL_SIZE_X;
	public static float LEVEL_SIZE_Y;
	public static float GAME_PROJECTION_Y = 10f;
	public static float GAME_PROJECTION_X;
	public static final float GAME_METER = (1/GAME_PROJECTION_Y)/80;
	public static final float GAME_ACCELERATION = 10 * GAME_METER;
	public static final float GAME_GROUND_FRICTION = GAME_ACCELERATION;
	public static final float GAME_AIR_FRICTION = GAME_ACCELERATION/4;
	public static float LIFE_BAR_Y = TY/10f;
	public static float r1 =1;
	public static float r2 =5;
	public static float r3 =5;
	//--------------------------------------------
	//-----------------SKILL----------------------
	public static int SKILL_ID = 0;
	public static int SKILL1_COOLDOWN = 16;
	public static int SKILL2_COOLDOWN = 104;
	public static int SKILL3_COOLDOWN = 208;
	
	//Multislayer SEND XXX
	public static int SkillSprite1 = 0; //MiHu baza danych
	public static int SkillSprite2 = 0; //MiHu baza danych
	public static int SkillSprite3 = 0; //MiHu baza danych
	
	public static int animationSlowdown2 = 0;
	public static float animationDuration2 = 0f;
	public static int animationSlowdown3 = 10;
	public static float animationDuration3 = 1f;
	
	public static float SKILL_X;
	public static float SKILL_Y;
	
	public static boolean is = false;
	public static int usedSkill;
	
	public static final int ARCHER_STAND=0,	ARCHER_WALK=1,	ARCHER_FIRE=2,		ARCHER_HURT=3,	ARCHER_DYING=4,	ARCHER_NULL = 100;
	public static final int WARRIOR_STAND=0,WARRIOR_WALK=1,	WARRIOR_ATTACK=2,	WARRIOR_HURT=3,	WARRIOR_DYING=4,WARRIOR_NULL = 100;
	public static final int MUMMY_STAND=0,	MUMMY_WALK=1,	MUMMY_ATTACK=2,		MUMMY_HURT=3,	MUMMY_DYING=4,	MUMMY_NULL = 100;
	public static final int HAND_STAND=0,	HAND_ATTACK=1,	HAND_HURT=3,		HAND_DYING=4,	HAND_NULL = 100;
	public static final int BARREL_STAND=0,	BARREL_WALK=1,	BARREL_ATTACK = 4;
	public static final int TOWER_STAND=0,	TOWER_NEW =1,	TOWER_FIRE=2,		TOWER_DYING =4,	WALL_NEW =1,	WALL_STAND =0;
	public static final int TRAP_STAND=0,	TRAP_ATTACK = 4,WARMTH_STAND=1,		WARMTH_ATACK=2, WARMTH_DYING=4;
	public static float ARCHER_SPEED = 0.0625f;
	public static float WARRIOR_SPEED= 0.0625f;
	public static float MUMMY_SPEED  = 0.03125f;
	public static float BARREL_SPEED = 0.125f;
	
	
	public static int flyingDuration = 300;
	public static int InvincibleDuration = 300;
	public static int defDuration = 300;
	public static int denialDuration = 60;
	public static int icicleDuration = 150;
	public static int thunderDuration = 150;
	public static int healingDuration = 300;
	public static int buffDuration = 300;
	//--------------------------------------------
	
	public static boolean isClasic = false;
	public static boolean isCrouch_prest = false;
	public static boolean isClimbing = false;
	public static boolean isSkillPressed = false;

	public static boolean canClimb = false;
	
	
	public static final float PLAYER_CLIMBING_SPEED = 0.1f;
	
	public static Display display;
	public static float display_x,xdpi; 
	public static float display_y,ydpi=LEVEL_scale; 

	
	
	// ------------------------- Multislayer BEGIN -----------------------
	
	public static Room mRoom;
	public static GameHelper mHelper;

    public static String playerParticipantID;

    public static int IDTracer =0;


	public static boolean MULTI_ACTIVE = false;

    public static ArrayList<Participant> participants = null; // lista graczy, posortowana, ale moze zawierac nieaktywnych

	public static int UPDATE_FREQ = 100;
	public static int MULTI_STEPS = 6*UPDATE_FREQ/100;

    public static ArrayList<String> opponents = new ArrayList<String>(2);

   	
	public static YoloMultislayer mMultislayer = new YoloMultislayer();
	
	// ------------------------- Multislayer END -------------------------
	
//------------------------------Menu----------------------------------------------------------------------	
   public static int whichLayout = 0;
//-------------------Player Sats & Info-----------------------------------------------------------------	
	public static YoloPlayerInfo currentPlayerInfo;	
	
	public static int ST1Cost = 0;
	public static int ST2Cost = 0;
	public static int ST3Cost = 0;
	public static int ST4Cost = 0;
	
	public static Context context;



}
