/**
 * 
 */
package com.impulse.study.helloworld;

import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

import javax.microedition.khronos.opengles.GL10;

import android.os.Looper;
import org.andengine.audio.sound.Sound;
import org.andengine.audio.sound.SoundFactory;
import org.andengine.engine.Engine;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.AlphaModifier;
import org.andengine.entity.modifier.IEntityModifier;
import org.andengine.entity.modifier.MoveModifier;
import org.andengine.entity.modifier.MoveXModifier;
import org.andengine.entity.modifier.MoveYModifier;
import org.andengine.entity.modifier.ParallelEntityModifier;
import org.andengine.entity.modifier.RotationAtModifier;
import org.andengine.entity.modifier.RotationModifier;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.modifier.SequenceEntityModifier;
import org.andengine.entity.particle.ParticleSystem;
import org.andengine.entity.particle.SpriteParticleSystem;
import org.andengine.entity.particle.emitter.CircleParticleEmitter;
import org.andengine.entity.particle.initializer.AlphaParticleInitializer;
import org.andengine.entity.particle.initializer.ColorParticleInitializer;
import org.andengine.entity.particle.initializer.RotationParticleInitializer;
import org.andengine.entity.particle.initializer.VelocityParticleInitializer;
import org.andengine.entity.particle.modifier.ExpireParticleInitializer;
import org.andengine.entity.particle.modifier.OffCameraExpireParticleModifier;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.util.FPSLogger;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.texture.Texture;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.atlas.bitmap.BuildableBitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.source.IBitmapTextureAtlasSource;
import org.andengine.opengl.texture.atlas.buildable.BuildableTextureAtlas;
import org.andengine.opengl.texture.atlas.buildable.builder.BlackPawnTextureAtlasBuilder;
import org.andengine.opengl.texture.atlas.buildable.builder.ITextureAtlasBuilder;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.texture.region.TextureRegionFactory;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.ui.IGameInterface;
import org.andengine.ui.activity.BaseGameActivity;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.util.debug.Debug;
import org.andengine.util.modifier.IModifier;
import org.andengine.util.modifier.IModifier.IModifierListener;
import org.andengine.util.modifier.ease.EaseQuadOut;
import org.andengine.entity.modifier.IEntityModifier.IEntityModifierListener;

import android.content.SharedPreferences;
import android.os.Handler;
import android.text.style.BulletSpan;
import android.widget.Toast;

/**
 * @author zhaohaifeng
 * 
 */
public class Level1Activity extends SimpleBaseGameActivity {

	private static final int CAMERA_WIDTH = 480;

	private static final int CAMERA_HEIGHT = 320;
	private String tag = "Level1Activity";

	protected Camera mCamera;
	protected Scene mMainScene;

	private BitmapTextureAtlas mLevel1BackTexture;
	private BitmapTextureAtlas mPigTexture;
	private BuildableBitmapTextureAtlas mObstacleBoxTexture;
	private TextureRegion mBoxTextureRegion;
	private TextureRegion mLevel1BackTextureRegion;
	private TextureRegion mBulletTextureRegion;
	private TextureRegion mCrossTextureRegion;
	private TextureRegion mHatchetTextureRegion;

	private TiledTextureRegion mPigTiledTextureRegion;

	private AnimatedSprite[] asprVamp = new AnimatedSprite[10];
	private int nVamp;
	Random gen;

	private Handler mHandler;

	private BitmapTextureAtlas mParticleTexture;
	private ITextureRegion mParticleTextureRegion;
	private ParticleSystem particleSystem;
	private CircleParticleEmitter particleEmitter;

	private Sound mExploSound, mGunshotSound, mWhiffieSound;
	private SharedPreferences audioOptions;

	private Sprite bullet,hatchet;

	private Runnable mStartVamp = new Runnable() {
		public void run() {
			int i = nVamp++;

            if (Level1Activity.this.mEngine == null) {
                return;
            }
            Scene scene = Level1Activity.this.mEngine.getScene();
			float startY = gen.nextFloat() * (CAMERA_HEIGHT - 50f);
			asprVamp[i] = new AnimatedSprite(CAMERA_WIDTH - 30f, startY,
					mPigTiledTextureRegion.deepCopy(),getVertexBufferObjectManager()) {
				@Override
				public boolean onAreaTouched(final TouchEvent pAreaTouchEvent,
						final float pTouchAreaLocalX,
						final float pTouchAreaLocalY) {
					switch (pAreaTouchEvent.getAction()) {
					case TouchEvent.ACTION_DOWN:
						for (int j = 0; j < nVamp; j++) {
							if ((Math.abs(asprVamp[j].getX()
									+ (asprVamp[j].getWidth()) / 2)
									- pAreaTouchEvent.getX() < 10.0f)
									&& (Math.abs(asprVamp[j].getY()
											+ asprVamp[j].getHeight()
											- pAreaTouchEvent.getY()) < 10.0f)) {
								particleEmitter.setCenter(
										asprVamp[j].getX()
												+ (asprVamp[j].getWidth()) / 2,
										asprVamp[j].getY()
												+ asprVamp[j].getHeight() / 2);
								particleSystem.setParticlesSpawnEnabled(true);
								mHandler.postDelayed(mEndPESpawn, 3000);
								asprVamp[j].clearEntityModifiers();
								asprVamp[j]
										.registerEntityModifier(new AlphaModifier(
												1.0f, 1.0f, 0.0f));
								asprVamp[j].setPosition(CAMERA_WIDTH,
										gen.nextFloat() * CAMERA_HEIGHT);
								playSound(mExploSound);
								break;
							}
						}
					}
					return true;
				}
			};
			final long[] frameDurations = new long[4];
			Arrays.fill(frameDurations, 500);
			asprVamp[i].animate(frameDurations, 4, 7, true);
			asprVamp[i].registerEntityModifier(new SequenceEntityModifier(
					new AlphaModifier(5f, 0f, 1f), new MoveModifier(60f,
							asprVamp[i].getX(), 30f, asprVamp[i].getY(),
							CAMERA_HEIGHT / 2)));

			scene.attachChild(asprVamp[i]);

			if (nVamp < 10) {
				mHandler.postDelayed(mStartVamp, 5000);
			}
			scene.registerTouchArea(asprVamp[i]);
		}
	};

	private Runnable mEndPESpawn = new Runnable() {
		public void run() {
			particleSystem.setParticlesSpawnEnabled(false);
		}
	};
    private SequenceEntityModifier pEntityModifier;

    @Override
    public EngineOptions onCreateEngineOptions() {
        audioOptions = getSharedPreferences("audio", MODE_PRIVATE);
        this.mCamera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
        EngineOptions eo = new EngineOptions(true, ScreenOrientation.LANDSCAPE_FIXED,
                new FillResolutionPolicy(), this.mCamera);
        eo.getAudioOptions().setNeedsMusic(true);
        eo.getAudioOptions().setNeedsSound(true);
        return eo;
    }

    @Override
    protected void onCreateResources() {
        mHandler = new Handler(Looper.getMainLooper());
        gen = new Random();

        BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/Level1/");
        mLevel1BackTexture = new BitmapTextureAtlas(this.getTextureManager(),512, 512,
                TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        mLevel1BackTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
                this.mLevel1BackTexture, this, "Level1Bk.png", 0, 0);
        mEngine.getTextureManager().loadTexture(this.mLevel1BackTexture);

        mObstacleBoxTexture = new BuildableBitmapTextureAtlas(this.getTextureManager(),512, 256,
                TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        mBoxTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
                mObstacleBoxTexture, this, "Obstaclebox.png");
        mBulletTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
                mObstacleBoxTexture, this, "Bullet.png");
        mCrossTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
                mObstacleBoxTexture, this, "Cross.png");
        mHatchetTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
                mObstacleBoxTexture, this, "Hatchet.png");

        try {
            mObstacleBoxTexture.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
            this.mObstacleBoxTexture.load();
        } catch (ITextureAtlasBuilder.TextureAtlasBuilderException e) {
            e.printStackTrace();
        }

        this.mEngine.getTextureManager().loadTexture(this.mObstacleBoxTexture);

        mPigTexture = new BitmapTextureAtlas(this.getTextureManager(),256, 256, TextureOptions.DEFAULT);

        mPigTiledTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(
                mPigTexture, this.getApplicationContext(), "pig.png", 0,0,4, 4);
        mEngine.getTextureManager().loadTexture(this.mPigTexture);

        BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/particles/");
        mParticleTexture = new BitmapTextureAtlas(this.getTextureManager(),32, 32,
                TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        mParticleTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
                this.mParticleTexture, this, "particle_fire.png", 0, 0);
        mEngine.getTextureManager().loadTexture(mParticleTexture);

        SoundFactory.setAssetBasePath("mfx/");
        try {
            this.mExploSound = SoundFactory.createSoundFromAsset(
                    this.mEngine.getSoundManager(), getApplicationContext(),
                    "exploSound.ogg");
            this.mGunshotSound = SoundFactory.createSoundFromAsset(
                    this.mEngine.getSoundManager(), getApplicationContext(),
                    "gunshot.ogg");
            this.mWhiffieSound = SoundFactory.createSoundFromAsset(
                    this.getSoundManager(), getApplicationContext(),
                    "whiffle.ogg");
        } catch (final IOException e) {
            Debug.e(e);
        }
    }

    @Override
    protected Scene onCreateScene() {
        this.mEngine.registerUpdateHandler(new FPSLogger());
        final Scene scene = new Scene();

        final float centerX = (CAMERA_WIDTH - mLevel1BackTextureRegion.getWidth()) / 2;
        final float centerY = (CAMERA_HEIGHT - mLevel1BackTextureRegion
                .getHeight()) / 2;

        final Sprite background = new Sprite(centerX, centerY,
                mLevel1BackTextureRegion,this.getVertexBufferObjectManager());
        scene.attachChild(background);
        final Sprite obstacleBox = new Sprite(20.0f,
                mBoxTextureRegion.getHeight() + 50f, mBoxTextureRegion,this.getVertexBufferObjectManager());
        scene.attachChild(obstacleBox);
        bullet = new Sprite(20.0f, CAMERA_HEIGHT - 50f, mBulletTextureRegion,this.getVertexBufferObjectManager()) {
            @Override
            // pTouchAreaLocalX，pTouchAreaLocalY在这个sprite中的相当位置（左上角为（0，0））
            public boolean onAreaTouched(final TouchEvent pAreaTouchEvent,
                                         final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
                switch (pAreaTouchEvent.getAction()) {
                    case TouchEvent.ACTION_DOWN:
//					Toast.makeText(Level1Activity.this, "Sprite touch Down",
//							Toast.LENGTH_SHORT).show();
                        break;
                    case TouchEvent.ACTION_UP:
//					Toast.makeText(Level1Activity.this, "Sprite touch UP",
//							Toast.LENGTH_SHORT).show();
                        fireBullet(pAreaTouchEvent.getX(), pAreaTouchEvent.getY());
                        break;
                    case TouchEvent.ACTION_MOVE:
                        this.setPosition(pAreaTouchEvent.getX() - this.getWidth()
                                / 2, pAreaTouchEvent.getY() - this.getHeight() / 2);
                        break;
                }
                return true;
            }
        };
        bullet.registerEntityModifier(new SequenceEntityModifier(
                new ParallelEntityModifier(new MoveYModifier(3, 0f,
                        CAMERA_HEIGHT - 100f, EaseQuadOut.getInstance()),
                        new AlphaModifier(3, 0f, 1f), new ScaleModifier(3,
                        0.5f, 1.0f), new RotationModifier(3, 0, 360))));
        scene.attachChild(bullet);
        final Sprite cross = new Sprite(bullet.getX() + 50f,
                CAMERA_HEIGHT - 100.0f, mCrossTextureRegion,this.getVertexBufferObjectManager()) {
            @Override
            public boolean onAreaTouched(final TouchEvent pAreaTouchEvent,
                                         final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
                switch (pAreaTouchEvent.getAction()) {
                    case TouchEvent.ACTION_DOWN:
//					Toast.makeText(Level1Activity.this, "Sprite touch Down",
//							Toast.LENGTH_SHORT).show();
                        break;
                    case TouchEvent.ACTION_UP:
//					Toast.makeText(Level1Activity.this, "Sprite touch UP",
//							Toast.LENGTH_SHORT).show();
                        throwHatchet(pAreaTouchEvent.getX(), pAreaTouchEvent.getY());
                        break;
                    case TouchEvent.ACTION_MOVE:
                        this.setPosition(pAreaTouchEvent.getX() - this.getWidth()
                                / 2, pAreaTouchEvent.getY() - this.getHeight() / 2);
                        break;
                }
                return true;
            }
        };
        cross.registerEntityModifier(new SequenceEntityModifier(
                new ParallelEntityModifier(new MoveYModifier(4, 0.0f,
                        CAMERA_HEIGHT - 100.0f, EaseQuadOut.getInstance()),
                        new AlphaModifier(4, 0.0f, 1.0f), new ScaleModifier(4,
                        0.f, 1.0f), new RotationModifier(2, 0, 360))));
        cross.registerEntityModifier(new AlphaModifier(10.0f, 0.0f, 1.0f));
        scene.attachChild(cross);
        hatchet = new Sprite(cross.getX() + 50.0f,
                CAMERA_HEIGHT - 100.0f, mHatchetTextureRegion,this.getVertexBufferObjectManager()) {
            @Override
            public boolean onAreaTouched(final TouchEvent pAreaTouchEvent,
                                         final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
                switch (pAreaTouchEvent.getAction()) {
                    case TouchEvent.ACTION_DOWN:
//                        Toast.makeText(Level1Activity.this, "Sprite touch Down",
//                                Toast.LENGTH_SHORT).show();
                        break;
                    case TouchEvent.ACTION_UP:
//                        Toast.makeText(Level1Activity.this, "Sprite touch UP",
//                                Toast.LENGTH_SHORT).show();
                        break;
                    case TouchEvent.ACTION_MOVE:
                        this.setPosition(pAreaTouchEvent.getX() - this.getWidth()
                                / 2, pAreaTouchEvent.getY() - this.getHeight() / 2);
                        break;
                }
                return true;
            }
        };

////        hatchet.registerEntityModifier(pEntityModifier);
//        hatchet.registerEntityModifier(new AlphaModifier(15f, 0.0f, 1.0f));
//        scene.attachChild(hatchet);
//        scene.registerEntityModifier(new AlphaModifier(10, 0.0f, 1.0f));

        hatchet.registerEntityModifier(new SequenceEntityModifier(
                new ParallelEntityModifier(new MoveYModifier(5, 0.0f,
                        CAMERA_HEIGHT - 100.0f, EaseQuadOut.getInstance()),
                        new AlphaModifier(5, 0.0f, 1.0f), new ScaleModifier(5,
                        0.5f, 1.0f)), new RotationModifier(2, 0, 360)));
        // hatchet.registerEntityModifier(new AlphaModifier(15f, 0.0f, 1.0f));
        scene.getLastChild().attachChild(hatchet);
        scene.registerEntityModifier(new AlphaModifier(10, 0.0f, 1.0f));

        // 注册触摸事件
        scene.setTouchAreaBindingOnActionDownEnabled(true);
        scene.setTouchAreaBindingOnActionMoveEnabled(true);
        scene.registerTouchArea(bullet);
        scene.registerTouchArea(cross);
        scene.registerTouchArea(hatchet);

        mHandler.postDelayed(mStartVamp, 5000);
        particleEmitter = new CircleParticleEmitter(CAMERA_WIDTH * 0.5f,
                CAMERA_HEIGHT * 0.5f + 20, 40);
        particleSystem = new SpriteParticleSystem(particleEmitter, 50, 50, 300,
                this.mParticleTextureRegion,this.getVertexBufferObjectManager());

        particleSystem.addParticleInitializer(new ColorParticleInitializer(1, 0, 0));
        particleSystem.addParticleInitializer(new AlphaParticleInitializer(0));
        particleSystem.addParticleInitializer(new ExpireParticleInitializer<Sprite>(2,4));
//		particleSystem.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE);
//        particleSystem.addParticleInitializer(new VelocityParticleInitializer(-2, 2,
//                -2, -2));
        particleSystem
                .addParticleInitializer(new RotationParticleInitializer(0f, 360f));

        particleSystem
                .addParticleModifier(new org.andengine.entity.particle.modifier.ScaleParticleModifier(
                        1f, 2f, 0, 5));
        particleSystem
                .addParticleModifier(new org.andengine.entity.particle.modifier.ColorParticleModifier(
                        1, 1, 0, 0.5f, 0, 0, 0, 3));
        particleSystem
                .addParticleModifier(new org.andengine.entity.particle.modifier.ColorParticleModifier(
                        1, 1, 0.5f, 1, 0, 1, 2, 4));
        particleSystem
                .addParticleModifier(new org.andengine.entity.particle.modifier.AlphaParticleModifier(
                        0, 1, 0, 1));
        particleSystem
                .addParticleModifier(new org.andengine.entity.particle.modifier.AlphaParticleModifier(
                        1, 0, 3, 4));


        particleSystem.setParticlesSpawnEnabled(false);
        scene.attachChild(particleSystem);

        return scene;
    }


    @Override
	public void onPauseGame() {
		super.onPauseGame();
		mGunshotSound.pause();
		mExploSound.pause();
	}

	private void playSound(Sound mSound) {
		if (audioOptions.getBoolean("effectsOn", false)) {
			mSound.play();
		}
	}

	private void fireBullet(float pX, float pY) {
		this.bullet.registerEntityModifier(new SequenceEntityModifier(
				new IEntityModifierListener() {

                    @Override
                    public void onModifierStarted(IModifier<IEntity> iEntityIModifier, IEntity iEntity) {
                        //To change body of implemented methods use File | Settings | File Templates.
                    }

                    @Override
                    public void onModifierFinished(
							IModifier<IEntity> pModifier, IEntity pItem) {
						Level1Activity.this.runOnUiThread(new Runnable() {
							
							public void run() {
								bullet.setVisible(false);
								bullet.setPosition(0, 0);
							}
						});
					}
				}, new RotationModifier(0.5f, 0f, 90f), new MoveXModifier(0.5f,
						pX, CAMERA_WIDTH), new AlphaModifier(0.1f, 1.0f, 0f)));
		mHandler.postDelayed(mPlayGunshot, 500);
	}
	
	private void throwHatchet(float pX,float pY){
		hatchet.registerEntityModifier(new ParallelEntityModifier(new IEntityModifierListener() {

            @Override
            public void onModifierStarted(IModifier<IEntity> iEntityIModifier, IEntity iEntity) {
                //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
				Level1Activity.this.runOnUiThread(new Runnable() {
					
					public void run() {
						hatchet.setVisible(false);
						hatchet.setPosition(0, 0);
					}
				});
			}
		}, new RotationAtModifier(5f, 0f, 5f*360f, 20f, 20f),new MoveXModifier(5f, pX, CAMERA_WIDTH)));
		playSound(mWhiffieSound);
	}
	
	private Runnable mPlayGunshot = new Runnable() {
		
		public void run() {
			playSound(mGunshotSound);
		}
	};

}
