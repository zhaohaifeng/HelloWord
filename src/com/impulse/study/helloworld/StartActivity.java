package com.impulse.study.helloworld;

import java.io.IOException;

import android.os.Looper;
import org.andengine.audio.music.Music;
import org.andengine.audio.music.MusicFactory;
import org.andengine.engine.Engine;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.scene.background.modifier.LoopBackgroundModifier;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.util.FPSLogger;
import org.andengine.opengl.texture.Texture;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.bitmap.BitmapTexture;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.texture.region.TextureRegionFactory;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.ui.IGameInterface;
import org.andengine.ui.activity.BaseGameActivity;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.util.debug.Debug;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;

public class StartActivity extends SimpleBaseGameActivity {

	private static final int CAMERA_WIDTH = 480;
	private static final int CAMERA_HEIGHT = 320;

	private Camera mCamera;
	private BitmapTextureAtlas mTexture;
	private ITextureRegion mSplashTextureRegion;

	static protected Music mMusic;
	private SharedPreferences audioOptions;
	private SharedPreferences.Editor audioEditor;
    private Handler mHandler;

	@Override
	public void onPauseGame(){
		super.onPauseGame();
		StartActivity.mMusic.pause();
	}

    @Override
    public EngineOptions onCreateEngineOptions() {
        mHandler = new Handler();
        this.mCamera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);

        audioOptions = getSharedPreferences("audio", MODE_PRIVATE);
        audioEditor = audioOptions.edit();
        if(!audioOptions.contains("musicOn")){
            audioEditor.putBoolean("musicOn", true);
            audioEditor.putBoolean("effectsOn", true);
            audioEditor.commit();
        }
        EngineOptions eo = new EngineOptions(true,
                ScreenOrientation.LANDSCAPE_FIXED, new FillResolutionPolicy(), this.mCamera);
        eo.getAudioOptions().setNeedsMusic(true);
        return eo;
    }

    @Override
    protected void onCreateResources() {
        this.mTexture = new BitmapTextureAtlas(this.getTextureManager(),512, 512,TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        this.mSplashTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mTexture, this, "gfx/background.jpg", 0, 0);
        this.mEngine.getTextureManager().loadTexture(this.mTexture);

        MusicFactory.setAssetBasePath("mfx/");
        try{
            StartActivity.mMusic = MusicFactory.createMusicFromAsset(this.mEngine.getMusicManager(), getApplicationContext(), "background_music.ogg");
            StartActivity.mMusic.setLooping(true);
        }catch (final IOException e){
            Debug.e(e);
        }
    }

    @Override
    protected Scene onCreateScene() {
        this.mEngine.registerUpdateHandler(new FPSLogger());
        final Scene scene = new Scene();
        scene.setBackground(new Background(0, 0, 0));
        final float centerX = (CAMERA_WIDTH - this.mSplashTextureRegion.getWidth())/2;
        final float centerY = (CAMERA_HEIGHT - this.mSplashTextureRegion.getHeight())/2;
        final Sprite splash = new Sprite(centerX,centerY,this.mSplashTextureRegion,this.getVertexBufferObjectManager());
        scene.attachChild(splash);

        mMusic.play();
        if(!audioOptions.getBoolean("musicOn", false)){
            mMusic.pause();
        }
        return scene;
    }
//
    @Override
	public void onResumeGame() {
        super.onResumeGame();
//        if (this.mEngine != null) {
//
//        }
        if (audioOptions.getBoolean("musicOn", true)) {
            if (!StartActivity.mMusic.isReleased()&&!StartActivity.mMusic.isPlaying()) {
                StartActivity.mMusic.resume();
            }
        }
//        mHandler.postDelayed(mLaunchTask, 3000);
    }

    @Override
	public void onGameCreated() {
        super.onGameCreated();
//        this.runOnUiThread(mLaunchTask);
		mHandler.postDelayed(mLaunchTask, 3000);
//        Intent myIntent = new Intent(this,MainMenuActivity.class);
//        StartActivity.this.startActivity(myIntent);
	}

	
	private Runnable mLaunchTask = new Runnable(){
		public void run(){
			Intent myIntent = new Intent(StartActivity.this,MainMenuActivity.class);
			StartActivity.this.startActivity(myIntent);
		}
	};

}
