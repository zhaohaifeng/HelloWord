package com.impulse.study.helloworld;

import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.engine.options.EngineOptions;
import org.anddev.andengine.engine.options.EngineOptions.ScreenOrientation;
import org.anddev.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.text.ChangeableText;
import org.anddev.andengine.entity.util.FPSLogger;
import org.anddev.andengine.opengl.font.Font;
import org.anddev.andengine.opengl.texture.Texture;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.ui.activity.BaseGameActivity;

import android.os.Bundle;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Menu;

public class MainActivity extends BaseGameActivity {
    private static final int CAMERA_WIDTH = 320;
    private static final int CAMERA_HEIGHT = 480;
    private Camera andCamera;
    private Texture myFontTexture;
    private Font myFont;
    public void onLoadComplete() {
    }
    public Engine onLoadEngine() {
        // ���������
        this.andCamera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
        // ����Engine��ȫ����ʾ���ֻ�����Ϊ����������������
        return new Engine(new EngineOptions(true, ScreenOrientation.PORTRAIT,
                new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT),
                this.andCamera));
    }
    public void onLoadResources() {
        // ����һ������������ʾ����
        this.myFontTexture = new Texture(256, 256, TextureOptions.DEFAULT);
        // ��������
        this.myFont = new Font(this.myFontTexture, Typeface.create(
                Typeface.DEFAULT, Typeface.BOLD), 32, true, Color.WHITE);
        // ע�������������
        this.mEngine.getTextureManager().loadTexture(this.myFontTexture);
        this.mEngine.getFontManager().loadFont(this.myFont);
    }
    public Scene onLoadScene() {
        // ������������������Layer����Ϊ1
        final Scene scene = new Scene(1);
        // ʹ�ÿ��Ա�����ݵ�ChangeableText��ʾFPS(���ĸ���Text������ı���ʾ����)��λ����15,5,
        // ����ΪmyFont�����涨�ģ����������ʾ5���ַ�(��������ʾ�����ַ���ʵ�ʾ�����ʾ������
        // AndEngine�����Զ����䣬�����Գ�ʼ��ʱ������ַ������㡭��)
        final ChangeableText text = new ChangeableText(5, 5, this.myFont,
                "0.0", 5);
        // ע��FPS����
        this.mEngine.registerUpdateHandler(new FPSLogger() {
            protected void onHandleAverageDurationElapsed(final float pFPS) {
                super.onHandleAverageDurationElapsed(pFPS);
                // �������ݵ�ChangeableText
                text.setText("" + pFPS);
            }
        });
        // ��ChangeableTextע�볡��
        scene.attachChild(text);
        // ����������������ͼ����Ϊ1
        return scene;
    }
}
