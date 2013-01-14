package com.impulse.study.helloworld;

import java.io.IOException;

import org.anddev.andengine.audio.music.Music;
import org.anddev.andengine.audio.music.MusicFactory;
import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.engine.options.EngineOptions;
import org.anddev.andengine.engine.options.EngineOptions.ScreenOrientation;
import org.anddev.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
import org.anddev.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.entity.util.FPSLogger;
import org.anddev.andengine.opengl.texture.Texture;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.region.TextureRegion;
import org.anddev.andengine.opengl.texture.region.TextureRegionFactory;
import org.anddev.andengine.ui.activity.BaseGameActivity;
import org.anddev.andengine.util.Debug;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;

public class StartActivity extends BaseGameActivity {

	private static final int CAMERA_WIDTH = 480;
	private static final int CAMERA_HEIGHT = 320;

	private Camera mCamera;
	private Texture mTexture;
	private TextureRegion mSplashTextureRegion;
	private Handler mHandler;

	static protected Music mMusic;
	private SharedPreferences audioOptions;
	private SharedPreferences.Editor audioEditor;

	public Engine onLoadEngine() {
		mHandler = new Handler();
		this.mCamera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
		
		audioOptions = getSharedPreferences("audio", MODE_PRIVATE);
		audioEditor = audioOptions.edit();
		if(!audioOptions.contains("musicOn")){
			audioEditor.putBoolean("musicOn", true);
			audioEditor.putBoolean("effectsOn", true);
			audioEditor.commit();
		}
		Engine engine = new Engine(new EngineOptions(true,
				ScreenOrientation.LANDSCAPE, new FillResolutionPolicy(), this.mCamera).setNeedsMusic(true));
		return engine;
	}

	public void onLoadResources() {
		this.mTexture = new Texture(512, 512,TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.mSplashTextureRegion = TextureRegionFactory.createFromAsset(this.mTexture, this, "gfx/background.jpg",0,0);
		this.mEngine.getTextureManager().loadTexture(this.mTexture);
		
		MusicFactory.setAssetBasePath("mfx/");
		try{
			StartActivity.mMusic = MusicFactory.createMusicFromAsset(this.mEngine.getMusicManager(), getApplicationContext(), "background_music.ogg");
			StartActivity.mMusic.setLooping(true);
		}catch (final IOException e){
			Debug.e(e);
		}
	}

	public Scene onLoadScene() {
		this.mEngine.registerUpdateHandler(new FPSLogger());
		final Scene scene = new Scene(1);
		final int centerX = (CAMERA_WIDTH - this.mSplashTextureRegion.getWidth())/2;
		final int centerY = (CAMERA_HEIGHT - this.mSplashTextureRegion.getHeight())/2;
		final Sprite splash = new Sprite(centerX,centerY,this.mSplashTextureRegion);
		scene.getLastChild().attachChild(splash);
		
		mMusic.play();
		if(!audioOptions.getBoolean("musicOn", false)){
			mMusic.pause();
		}
		return scene;
	}
	
	@Override
	public void onPauseGame(){
		super.onPauseGame();
		StartActivity.mMusic.pause();
	}
	
	@Override
	public void onResumeGame(){
		super.onResumeGame();
		if(audioOptions.getBoolean("musicOn", true)){
			StartActivity.mMusic.resume();
		}
		mHandler.postDelayed(mLaunchTask, 3000);
	}

	public void onLoadComplete() {
		mHandler.postDelayed(mLaunchTask, 3000);
	}

	
	private Runnable mLaunchTask = new Runnable(){
		public void run(){
			Intent myIntent = new Intent(StartActivity.this,MainMenuActivity.class);
			StartActivity.this.startActivity(myIntent);
		}
	};
}
