/**
 * 
 */
package com.impulse.study.helloworld;

import javax.microedition.khronos.opengles.GL10;

import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.engine.options.EngineOptions;
import org.anddev.andengine.engine.options.EngineOptions.ScreenOrientation;
import org.anddev.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
import org.anddev.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.scene.menu.MenuScene;
import org.anddev.andengine.entity.scene.menu.MenuScene.IOnMenuItemClickListener;
import org.anddev.andengine.entity.scene.menu.item.IMenuItem;
import org.anddev.andengine.entity.scene.menu.item.TextMenuItem;
import org.anddev.andengine.entity.scene.menu.item.decorator.ColorMenuItemDecorator;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.entity.util.FPSLogger;
import org.anddev.andengine.opengl.font.Font;
import org.anddev.andengine.opengl.font.FontFactory;
import org.anddev.andengine.opengl.texture.Texture;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.region.TextureRegion;
import org.anddev.andengine.opengl.texture.region.TextureRegionFactory;
import org.anddev.andengine.ui.activity.BaseGameActivity;

import android.graphics.Color;
import android.os.Handler;

/**
 * @author zhaohaifeng
 * 
 */
public class OptionsActivity extends BaseGameActivity implements
		IOnMenuItemClickListener {

	private static final int CAMERA_WIDTH = 480;
	private static final int CAMERA_HEIGHT = 320;

	protected static final int MENU_MUSIC = 0;
	protected static final int MENU_EFFECTS = MENU_MUSIC + 1;

	protected Camera mCamera;

	protected Scene mMainScene;
	protected Handler mHandler;

	private Texture mMenuBackTexture;
	private TextureRegion mMenuBackTextureRegion;

	protected MenuScene mOptionsMenuScene;
	private TextMenuItem mTurnMusicOff, mTurnMusicOn;
	private TextMenuItem mTurnEffectsOff, mTurnEffectsOn;
	private IMenuItem musicMenuItem;
	private IMenuItem effectsMenuItem;

	private Texture mFontTexture;
	private Font mFont;

	public boolean isMusicOn = true;
	public boolean isEffectsOn = true;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.anddev.andengine.ui.IGameInterface#onLoadEngine()
	 */
	public Engine onLoadEngine() {
		mHandler = new Handler();
		this.mCamera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
		return new Engine(new EngineOptions(true, ScreenOrientation.LANDSCAPE,
				new FillResolutionPolicy(), this.mCamera));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.anddev.andengine.ui.IGameInterface#onLoadResources()
	 */
	public void onLoadResources() {
		this.mFontTexture = new Texture(256, 256,
				TextureOptions.BILINEAR_PREMULTIPLYALPHA);

		FontFactory.setAssetBasePath("font/");
		this.mFont = FontFactory.createFromAsset(this.mFontTexture, this,
				"yajian.otf", 32, true, Color.WHITE);

		this.mEngine.getTextureManager().loadTexture(this.mFontTexture);
		this.mEngine.getFontManager().loadFont(mFont);

		this.mMenuBackTexture = new Texture(512, 512,
				TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.mMenuBackTextureRegion = TextureRegionFactory.createFromAsset(
				this.mMenuBackTexture, this,
				"gfx/OptionsMenu/OptionsMenuBk.png", 0, 0);
		this.mEngine.getTextureManager().loadTexture(this.mMenuBackTexture);

		mTurnMusicOn = new TextMenuItem(MENU_MUSIC, mFont, "Turn Music On");
		mTurnMusicOff = new TextMenuItem(MENU_MUSIC, mFont, "Turn Music Off");
		mTurnEffectsOn = new TextMenuItem(MENU_EFFECTS, mFont,
				"Turn Effects On");
		mTurnEffectsOff = new TextMenuItem(MENU_EFFECTS, mFont,
				"Turn Effects Off");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.anddev.andengine.ui.IGameInterface#onLoadScene()
	 */
	public Scene onLoadScene() {
		this.mEngine.registerUpdateHandler(new FPSLogger());
		
		this.createOptionsMenuScene();

		final int centerX = (CAMERA_WIDTH - this.mMenuBackTextureRegion
				.getWidth()) / 2;
		final int centerY = (CAMERA_HEIGHT - this.mMenuBackTextureRegion
				.getHeight()) / 2;

		this.mMainScene = new Scene(1);
		final Sprite menuBack = new Sprite(centerX, centerY,
				this.mMenuBackTextureRegion);
		mMainScene.getLastChild().attachChild(menuBack);
		mMainScene.setChildScene(mOptionsMenuScene);
		return mMainScene;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.anddev.andengine.ui.IGameInterface#onLoadComplete()
	 */
	public void onLoadComplete() {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.anddev.andengine.entity.scene.menu.MenuScene.IOnMenuItemClickListener
	 * #onMenuItemClicked(org.anddev.andengine.entity.scene.menu.MenuScene,
	 * org.anddev.andengine.entity.scene.menu.item.IMenuItem, float, float)
	 */
	public boolean onMenuItemClicked(MenuScene pMenuScene, IMenuItem pMenuItem,
			float pMenuItemLocalX, float pMenuItemLocalY) {
		switch (pMenuItem.getID()) {
		case MENU_MUSIC:
			if (isMusicOn) {
				isMusicOn = false;
			} else {
				isMusicOn = true;
			}
			createOptionsMenuScene();
			mMainScene.clearChildScene();
			mMainScene.setChildScene(mOptionsMenuScene);
			return true;
		case MENU_EFFECTS:
			if (isEffectsOn) {
				isEffectsOn = false;
			} else {
				isEffectsOn = true;
			}
			createOptionsMenuScene();
			mMainScene.clearChildScene();
			mMainScene.setChildScene(mOptionsMenuScene);
			return true;
		default:
			return false;
		}
	}

	protected void createOptionsMenuScene() {
		this.mOptionsMenuScene = new MenuScene(this.mCamera);
		
		if(isMusicOn){
			musicMenuItem = new ColorMenuItemDecorator(mTurnMusicOn, 0.5f, 0.5f, 0.5f, 1.0f, 0.0f, 0.0f);
		}else{
			musicMenuItem = new ColorMenuItemDecorator(mTurnMusicOn, 0.5f, 0.5f, 0.5f, 1.0f, 0.0f, 0.0f);
		}
		musicMenuItem.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		this.mOptionsMenuScene.addMenuItem(musicMenuItem);
		
		if(isEffectsOn){
			effectsMenuItem = new ColorMenuItemDecorator(mTurnEffectsOff, 0.5f, 0.5f, 0.5f, 1.0f, 0.0f, 0.0f);
		}else{
			effectsMenuItem = new ColorMenuItemDecorator(mTurnEffectsOn, 0.5f, 0.5f, 0.5f, 1.0f, 0.0f, 0.0f);
		}
		effectsMenuItem.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		this.mOptionsMenuScene.addMenuItem(effectsMenuItem);
		this.mOptionsMenuScene.buildAnimations();
		this.mOptionsMenuScene.setBackgroundEnabled(false);
		this.mOptionsMenuScene.setOnMenuItemClickListener(this);
	}

}
