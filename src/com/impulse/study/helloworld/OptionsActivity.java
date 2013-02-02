/**
 * 
 */
package com.impulse.study.helloworld;

import javax.microedition.khronos.opengles.GL10;

import org.andengine.engine.Engine;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.modifier.ScaleAtModifier;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.scene.menu.MenuScene.IOnMenuItemClickListener;
import org.andengine.entity.scene.menu.item.IMenuItem;
import org.andengine.entity.scene.menu.item.TextMenuItem;
import org.andengine.entity.scene.menu.item.decorator.ColorMenuItemDecorator;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.util.FPSLogger;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.Texture;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.texture.region.TextureRegionFactory;
import org.andengine.ui.IGameInterface;
import org.andengine.ui.activity.BaseGameActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.util.color.Color;

/**
 * @author zhaohaifeng
 * 
 */
public class OptionsActivity extends SimpleBaseGameActivity implements
		IOnMenuItemClickListener {

	private static final int CAMERA_WIDTH = 480;
	private static final int CAMERA_HEIGHT = 320;

	protected static final int MENU_MUSIC = 0;
	protected static final int MENU_EFFECTS = MENU_MUSIC + 1;
	protected static final int MENU_WAV = MENU_EFFECTS + 1;

	protected Camera mCamera;

	protected Scene mMainScene;
	protected Handler mHandler;

	private BitmapTextureAtlas mMenuBackTexture;
	private ITextureRegion mMenuBackTextureRegion;

	protected MenuScene mOptionsMenuScene;
	private TextMenuItem mTurnMusicOff, mTurnMusicOn;
	private TextMenuItem mTurnEffectsOff, mTurnEffectsOn;

	private TextMenuItem mWAV;

	private IMenuItem musicMenuItem;
	private IMenuItem effectsMenuItem;
	private IMenuItem WAVMenuItem;

	private Texture mFontTexture;
	private Font mFont;

	private SharedPreferences audioOptions;
	private SharedPreferences.Editor audioEditor;



	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.andengine.entity.scene.menu.MenuScene.IOnMenuItemClickListener
	 * #onMenuItemClicked(org.andengine.entity.scene.menu.MenuScene,
	 * org.andengine.entity.scene.menu.item.IMenuItem, float, float)
	 */
	public boolean onMenuItemClicked(MenuScene pMenuScene, IMenuItem pMenuItem,
			float pMenuItemLocalX, float pMenuItemLocalY) {
		switch (pMenuItem.getID()) {
		case MENU_MUSIC:
			if (audioOptions.getBoolean("musicOn", true)) {
				audioEditor.putBoolean("musicOn", false);
				// if(StartActivity.mMusic.isPlaying()){
				StartActivity.mMusic.pause();
				// }
			} else {
				audioEditor.putBoolean("musicOn", true);
				StartActivity.mMusic.resume();
			}
			audioEditor.commit();
            this.mOptionsMenuScene.clearChildScene();
			createOptionsMenuScene();

			mMainScene.setChildScene(mOptionsMenuScene);
			return true;
		case MENU_EFFECTS:
			if(audioOptions.getBoolean("effectsOn", true)){
				audioEditor.putBoolean("effectsOn", false);
			}else{
				audioEditor.putBoolean("effectsOn", true);
			}
			audioEditor.commit();
            this.mOptionsMenuScene.clearChildScene();
			createOptionsMenuScene();

			mMainScene.setChildScene(mOptionsMenuScene);
			return true;
		case MENU_WAV:
			mMainScene
					.registerEntityModifier(new ScaleModifier(1.0f, 1.0f, 0f));
			mOptionsMenuScene.registerEntityModifier(new ScaleModifier(1.0f,
					1.0f, 0f));
			mHandler.postDelayed(mLaunchWAVTask, 1000);
			return true;
		default:
			return false;
		}
	}

	protected void createOptionsMenuScene() {
		this.mOptionsMenuScene = new MenuScene(this.mCamera);
        if (musicMenuItem != null) {
            musicMenuItem.setParent(null);
        }
        if (effectsMenuItem != null) {

            effectsMenuItem.setParent(null);
        }
        if (WAVMenuItem != null) {

            WAVMenuItem.setParent(null);
        }
        if (audioOptions.getBoolean("musicOn", true)) {
			musicMenuItem = new ColorMenuItemDecorator(mTurnMusicOff, new Color(0.5f,
					0.5f, 0.5f),new org.andengine.util.color.Color( 1.0f, 0.0f, 0.0f));
		} else {
			musicMenuItem = new ColorMenuItemDecorator(mTurnMusicOn, new Color(0.5f,
					0.5f, 0.5f),new Color( 1.0f, 0.0f, 0.0f));
		}
		musicMenuItem.setBlendFunction(GL10.GL_SRC_ALPHA,
				GL10.GL_ONE_MINUS_SRC_ALPHA);
		this.mOptionsMenuScene.addMenuItem(musicMenuItem);

		if (audioOptions.getBoolean("effectsOn", true)) {
			effectsMenuItem = new ColorMenuItemDecorator(mTurnEffectsOff, new Color(0.5f,
					0.5f, 0.5f),new Color( 1.0f, 0.0f, 0.0f));
		} else {
			effectsMenuItem = new ColorMenuItemDecorator(mTurnEffectsOn, new Color(0.5f,
					0.5f, 0.5f),new Color( 1.0f, 0.0f, 0.0f));
		}
		effectsMenuItem.setBlendFunction(GL10.GL_SRC_ALPHA,
				GL10.GL_ONE_MINUS_SRC_ALPHA);
		this.mOptionsMenuScene.addMenuItem(effectsMenuItem);

		WAVMenuItem = new ColorMenuItemDecorator(mWAV, new Color(0.5f, 0.5f, 0.5f),new Color( 1.0f,
				0f, 0f));
		WAVMenuItem.setBlendFunction(GL10.GL_SRC_ALPHA,
				GL10.GL_ONE_MINUS_SRC_ALPHA);
		this.mOptionsMenuScene.addMenuItem(WAVMenuItem);

		this.mOptionsMenuScene.buildAnimations();
		this.mOptionsMenuScene.setBackgroundEnabled(false);
		this.mOptionsMenuScene.setOnMenuItemClickListener(this);
	}

	private Runnable mLaunchWAVTask = new Runnable() {
		public void run() {
			Intent myIntent = new Intent(OptionsActivity.this,
					WAVAcitivity.class);
			OptionsActivity.this.startActivity(myIntent);
		}
	};

	@Override
	public void onPauseGame() {
		super.onPauseGame();
		StartActivity.mMusic.pause();
	}

    @Override
    public EngineOptions onCreateEngineOptions() {
        mHandler = new Handler();
        this.mCamera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);

        audioOptions = getSharedPreferences("audio", MODE_PRIVATE);
        audioEditor = audioOptions.edit();

        return new EngineOptions(true, ScreenOrientation.LANDSCAPE_FIXED,
                new FillResolutionPolicy(), this.mCamera);
    }

    @Override
    protected void onCreateResources() {
        this.mFontTexture = new BitmapTextureAtlas(this.getTextureManager(),256, 256,
                TextureOptions.BILINEAR_PREMULTIPLYALPHA);

        FontFactory.setAssetBasePath("font/");
        this.mFont = FontFactory.createFromAsset(this.getFontManager(),this.mFontTexture,this.getAssets(),
                "yajian.otf", 32, true, android.graphics.Color.WHITE);

        this.mEngine.getTextureManager().loadTexture(this.mFontTexture);
        this.mEngine.getFontManager().loadFont(mFont);

        this.mMenuBackTexture = new BitmapTextureAtlas(this.getTextureManager(),512, 512,
                TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        this.mMenuBackTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
                this.mMenuBackTexture, this,
                "gfx/OptionsMenu/OptionsMenuBk.png", 0, 0);
        this.mEngine.getTextureManager().loadTexture(this.mMenuBackTexture);

        mTurnMusicOn = new TextMenuItem(MENU_MUSIC, mFont, "Turn Music On", this.getVertexBufferObjectManager());
        mTurnMusicOff = new TextMenuItem(MENU_MUSIC, mFont, "Turn Music Off", this.getVertexBufferObjectManager());
        mTurnEffectsOn = new TextMenuItem(MENU_EFFECTS, mFont,
                "Turn Effects On", this.getVertexBufferObjectManager());
        mTurnEffectsOff = new TextMenuItem(MENU_EFFECTS, mFont,
                "Turn Effects Off", this.getVertexBufferObjectManager());

        mWAV = new TextMenuItem(MENU_WAV, mFont, "What a Vampire", this.getVertexBufferObjectManager());
    }

    @Override
    protected Scene onCreateScene() {
        this.mEngine.registerUpdateHandler(new FPSLogger());

        this.createOptionsMenuScene();

        final float centerX = (CAMERA_WIDTH - this.mMenuBackTextureRegion
                .getWidth()) / 2;
        final float centerY = (CAMERA_HEIGHT - this.mMenuBackTextureRegion
                .getHeight()) / 2;

        this.mMainScene = new Scene();
        final Sprite menuBack = new Sprite(centerX, centerY,
                this.mMenuBackTextureRegion,this.getVertexBufferObjectManager());
        mMainScene.attachChild(menuBack);
        mMainScene.setChildScene(mOptionsMenuScene);
        return mMainScene;
    }


    @Override
	public void onResumeGame() {
		super.onResumeGame();
		if (audioOptions.getBoolean("musicOn", false)) {
			StartActivity.mMusic.resume();
		}
		mMainScene.registerEntityModifier(new ScaleAtModifier(0.5f, 0f, 1f,
				CAMERA_WIDTH / 2, CAMERA_HEIGHT / 2));
		mOptionsMenuScene.registerEntityModifier(new ScaleAtModifier(0.5f, 0f,
				1f, CAMERA_WIDTH / 2, CAMERA_HEIGHT / 2));
	}

}
