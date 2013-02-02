package com.impulse.study.helloworld;

import javax.microedition.khronos.opengles.GL10;

import android.os.Looper;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
import org.andengine.entity.modifier.ScaleAtModifier;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.scene.menu.MenuScene.IOnMenuItemClickListener;
import org.andengine.entity.scene.menu.animator.SlideMenuAnimator;
import org.andengine.entity.scene.menu.item.IMenuItem;
import org.andengine.entity.scene.menu.item.SpriteMenuItem;
import org.andengine.entity.scene.menu.item.TextMenuItem;
import org.andengine.entity.scene.menu.item.decorator.ColorMenuItemDecorator;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.util.FPSLogger;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.font.IFont;
import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.ITextureRegion;

import android.os.Handler;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.KeyEvent;
import android.widget.Toast;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.util.color.Color;

import java.security.KeyStore;

public class MainMenuActivity extends SimpleBaseGameActivity implements
        IOnMenuItemClickListener {
    private static final int CAMERA_WIDTH = 480;
    private static final int CAMERA_HEIGHT = 320;

    protected static final int MENU_ABOUT = 0;
    protected static final int MENU_QUIT = MENU_ABOUT + 1;
    protected static final int MENU_PLAY = 10;
    protected static final int MENU_SCORES = MENU_PLAY + 1;
    protected static final int MENU_OPTIONS = MENU_SCORES + 1;
    protected static final int MENU_HELP = MENU_OPTIONS + 1;
    protected static final int MENU_DEMO = MENU_HELP + 1;

    protected Camera mCamera;
    protected Scene mMainScene;

    private BitmapTextureAtlas mMenuBackTexture;
    private ITextureRegion mMenuBackTextureRegion;

    protected MenuScene mStaticMenuScene, mPopUpMenuScene;

    private BitmapTextureAtlas mPopUpTexture;
    private ITexture mFontTexture;
    private IFont mFont;
    protected ITextureRegion mPopUpAboutTextureRegion;
    protected ITextureRegion mPopUpQuitTextureRegion;
    protected ITextureRegion mMenuPlayTextureRegion;
    protected ITextureRegion mMenuScoresTextureRegion;
    protected ITextureRegion mMenuOptionsTextureRegion;
    protected ITextureRegion mMenuHelpTextureRegion;
    private boolean popupDisplayed;

    private SharedPreferences audioOptions;

    protected Handler mHandler;


    @Override
    public EngineOptions onCreateEngineOptions() {
        mHandler = new Handler();
        this.mCamera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);

        audioOptions = getSharedPreferences("audio", MODE_PRIVATE);

        return new EngineOptions(true, ScreenOrientation.LANDSCAPE_FIXED,
                new FillResolutionPolicy(),
                this.mCamera);
    }

    @Override
    protected void onCreateResources() {
        this.mFontTexture = new BitmapTextureAtlas(this.getTextureManager(), 256, 256,
                TextureOptions.BILINEAR_PREMULTIPLYALPHA);

        FontFactory.setAssetBasePath("font/");
        this.mFont = FontFactory.createFromAsset(this.getFontManager(), mFontTexture, this.getAssets(),
                "yajian.otf", 32f, true, android.graphics.Color.YELLOW);
        mFont.load();
//		this.mEngine.getTextureManager().loadTexture(this.mFontTexture);
//		this.mEngine.getFontManager().loadFont(this.mFont);

        this.mMenuBackTexture = new BitmapTextureAtlas(this.getTextureManager(), 512, 512,
                TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        this.mMenuBackTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
                this.mMenuBackTexture, this, "gfx/MainMenu/MainMenuBk.png", 0,
                0);
        this.mEngine.getTextureManager().loadTexture(this.mMenuBackTexture);

        this.mPopUpTexture = new BitmapTextureAtlas(this.getTextureManager(), 512, 512,
                TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        this.mPopUpAboutTextureRegion = BitmapTextureAtlasTextureRegionFactory
                .createFromAsset(this.mPopUpTexture, this,
                        "gfx/MainMenu/About_button.png", 0, 0);
        this.mPopUpQuitTextureRegion = BitmapTextureAtlasTextureRegionFactory
                .createFromAsset(this.mPopUpTexture, this,
                        "gfx/MainMenu/Quit_button.png", 0, 50);
        this.mEngine.getTextureManager().loadTexture(this.mPopUpTexture);
        popupDisplayed = false;
    }

    @Override
    protected Scene onCreateScene() {
        this.mEngine.registerUpdateHandler(new FPSLogger());

        this.createStaticMenuScene();
        this.createPopUpMenuScene();

        final float centerX = (CAMERA_WIDTH - this.mMenuBackTextureRegion
                .getWidth()) / 2;
        final float centerY = (CAMERA_HEIGHT - this.mMenuBackTextureRegion
                .getHeight()) / 2;

        this.mMainScene = new Scene();

        final Sprite menuBack = new Sprite(centerX, centerY,
                this.mMenuBackTextureRegion, this.getVertexBufferObjectManager());
        mMainScene.attachChild(menuBack);
        mMainScene.setChildScene(mStaticMenuScene);
        return this.mMainScene;
    }


    @Override
    public void onResumeGame() {
        super.onResumeGame();

        if (audioOptions.getBoolean("musicOn", true)) {
            StartActivity.mMusic.resume();
        }

        mMainScene.registerEntityModifier(new ScaleAtModifier(0.5f, 0.0f, 1.0f, CAMERA_WIDTH / 2, CAMERA_HEIGHT / 2));
    }

    @Override
    public boolean onKeyDown(final int pKeyCode, final KeyEvent pEvent) {
        if (pKeyCode == KeyEvent.KEYCODE_MENU
                && pEvent.getAction() == KeyEvent.ACTION_DOWN) {
            if (popupDisplayed) {
                this.mPopUpMenuScene.back();
                mMainScene.setChildScene(mStaticMenuScene);
                popupDisplayed = false;
            } else {
                this.mMainScene.setChildScene(this.mPopUpMenuScene, false,
                        true, true);
                popupDisplayed = true;
            }
            return true;
        } else {
            return super.onKeyDown(pKeyCode, pEvent);
        }
    }

    @Override
    public boolean onMenuItemClicked(MenuScene pMenuScene, IMenuItem pMenuItem,
                                     final float pMenuItemLocalX, final float pMenuItemLocalY) {

        switch (pMenuItem.getID()) {
            case MENU_ABOUT:
                showToast("Abount selected");
                return true;
            case MENU_QUIT:
                this.finish();
                return true;
            case MENU_PLAY:
//			Toast.makeText(MainMenuActivity.this, "Play selected",
//					Toast.LENGTH_SHORT).show();
                mMainScene.registerEntityModifier(new ScaleModifier(1.0f, 1.0f, 0.0f));
                this.runOnUiThread(mLaunchLevel1Task);
                return true;
            case MENU_SCORES:
                showToast("Scores selected");
                return true;
            case MENU_OPTIONS:

                mMainScene.registerEntityModifier(new ScaleModifier(1.0f, 1.0f, 0.0f));
                mStaticMenuScene.registerEntityModifier(new ScaleModifier(1.0f, 1.0f, 0.0f));
                mHandler.postDelayed(mLaunchOptionsTask, 1000);

//			Toast.makeText(this, "Options selected", Toast.LENGTH_SHORT).show();
                return true;
            case MENU_HELP:
                showToast("Help selected");
                return true;
            case MENU_DEMO:
                mHandler.post(mLaunchDemolitionTask);
                return true;
            default:
                return false;
        }
    }

    protected void createStaticMenuScene() {
        this.mStaticMenuScene = new MenuScene(this.mCamera);
        final IMenuItem playMenuItem = new ColorMenuItemDecorator(
                new TextMenuItem(MENU_PLAY, mFont, "Play Game", getVertexBufferObjectManager()), new Color(0.5f, 0.5f, 0.5f), new Color(1.0f, 0.0f, 0.0f));
        playMenuItem.setBlendFunction(GL10.GL_SRC_ALPHA,
                GL10.GL_ONE_MINUS_SRC_ALPHA);
        this.mStaticMenuScene.addMenuItem(playMenuItem);

        final IMenuItem scoresMenuItem = new ColorMenuItemDecorator(
                new TextMenuItem(MENU_SCORES, mFont, "Scores", getVertexBufferObjectManager()), new Color(0.5f, 0.5f,
                0.5f), new Color(1.0f, 1.0f, 0.0f));
        scoresMenuItem.setBlendFunction(GL10.GL_SRC_ALPHA,
                GL10.GL_ONE_MINUS_SRC_ALPHA);
        this.mStaticMenuScene.addMenuItem(scoresMenuItem);

        final IMenuItem optionsMenuItem = new ColorMenuItemDecorator(
                new TextMenuItem(MENU_OPTIONS, mFont, "Options", getVertexBufferObjectManager()), new Color(0.5f, 0.5f,
                0.5f), new Color(1.0f, 0.0f, 0.0f));
        optionsMenuItem.setBlendFunction(GL10.GL_SRC_ALPHA,
                GL10.GL_ONE_MINUS_SRC_ALPHA);
        this.mStaticMenuScene.addMenuItem(optionsMenuItem);

        final IMenuItem helpMenuItem = new ColorMenuItemDecorator(
                new TextMenuItem(MENU_HELP, mFont, "Help", getVertexBufferObjectManager()), new Color(0.5f, 0.5f, 0.5f),
                new Color(1.5f, 0.0f, 0.0f));
        helpMenuItem.setBlendFunction(GL10.GL_SRC_ALPHA,
                GL10.GL_ONE_MINUS_SRC_ALPHA);
        this.mStaticMenuScene.addMenuItem(helpMenuItem);

        final IMenuItem demolitionMenuItem = new ColorMenuItemDecorator(
                new TextMenuItem(MENU_DEMO, mFont, "demo", getVertexBufferObjectManager()), new Color(0.5f, 0.5f, 0.5f),
                new Color(1.5f, 0.0f, 0.0f));
        demolitionMenuItem.setBlendFunction(GL10.GL_SRC_ALPHA,
                GL10.GL_ONE_MINUS_SRC_ALPHA);
        this.mStaticMenuScene.addMenuItem(demolitionMenuItem);

        this.mStaticMenuScene.buildAnimations();

        this.mStaticMenuScene.setBackgroundEnabled(false);
        this.mStaticMenuScene.setOnMenuItemClickListener(this);
    }

    protected void createPopUpMenuScene() {
        this.mPopUpMenuScene = new MenuScene(this.mCamera);

        final SpriteMenuItem aboutMenuItem = new SpriteMenuItem(MENU_ABOUT,
                this.mPopUpAboutTextureRegion, getVertexBufferObjectManager());
        aboutMenuItem.setBlendFunction(GL10.GL_SRC_ALPHA,
                GL10.GL_ONE_MINUS_SRC_ALPHA);
        this.mPopUpMenuScene.addMenuItem(aboutMenuItem);

        final SpriteMenuItem quitMenuItem = new SpriteMenuItem(MENU_QUIT,
                this.mPopUpQuitTextureRegion, getVertexBufferObjectManager());
        quitMenuItem.setBlendFunction(GL10.GL_SRC_ALPHA,
                GL10.GL_ONE_MINUS_SRC_ALPHA);
        this.mPopUpMenuScene.addMenuItem(quitMenuItem);

        this.mPopUpMenuScene.setMenuAnimator(new SlideMenuAnimator());

        this.mPopUpMenuScene.buildAnimations();

        this.mPopUpMenuScene.setBackgroundEnabled(false);
        this.mPopUpMenuScene.setOnMenuItemClickListener(this);

    }

    private Runnable mLaunchLevel1Task = new Runnable() {
        public void run() {
            Intent myIntent = new Intent(MainMenuActivity.this, Level1Activity.class);
            MainMenuActivity.this.startActivity(myIntent);
        }
    };

    private Runnable mLaunchOptionsTask = new Runnable() {

        public void run() {
            Intent myIntent = new Intent(MainMenuActivity.this, OptionsActivity.class);
            MainMenuActivity.this.startActivity(myIntent);
        }
    };

    private Runnable mLaunchDemolitionTask = new Runnable() {
        @Override
        public void run() {
            Intent myIntent = new Intent(MainMenuActivity.this, Demolition.class);
            MainMenuActivity.this.startActivity(myIntent);
        }
    };

    private void showToast(final String toast) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                Toast.makeText(MainMenuActivity.this, toast,
                        Toast.LENGTH_SHORT).show();
                Looper.loop();
            }
        }).start();

    }

	@Override
	public void onPauseGame(){
		super.onPauseGame();
		StartActivity.mMusic.pause();
	}
}
