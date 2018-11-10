package com.example.hyojung.quest.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.example.hyojung.quest.R;

public class ModifyUserInfo extends AppCompatActivity {

    final public static int DEFAULT = 0, USE_SPARE_NICKNAME = 1, USE_SPARE_PROFILE = 2;


    TextView kakao_name, spare_name;
    EditText edit_nickname;
    Switch useSpareNickname, useSpareProfileImage;
    ImageView image_kakaoProfile, image_spareProfile;
    Button button_submitName, button_selectImage, button_back;
    int result = 0;

    @Override
    protected void onCreate(Bundle savedInstancedState) {
        super.onCreate(savedInstancedState);
        setContentView(R.layout.modify_user_info);

        Intent intent = getIntent();

        kakao_name = (TextView)findViewById(R.id.text_current_kakao_name);
        spare_name = (TextView)findViewById(R.id.text_current_spare_name);
        edit_nickname = (EditText)findViewById(R.id.textbox_edit_nickname);
        useSpareNickname = (Switch)findViewById(R.id.switch_sparename);
        useSpareProfileImage = (Switch)findViewById(R.id.switch_spareimage);
        image_kakaoProfile = (ImageView)findViewById(R.id.image_kakao_profile);
        image_spareProfile = (ImageView)findViewById(R.id.image_spare_profile);
        button_submitName = (Button)findViewById(R.id.button_submit_name);
        button_selectImage = (Button)findViewById(R.id.button_select_image);
        button_back = (Button)findViewById(R.id.button_modify_info_exit);

        this.buttonOnClickListenerInit();
    }

    private void buttonOnClickListenerInit() {
        button_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                switch (result) {
                    case DEFAULT:
                        break;
                    case USE_SPARE_NICKNAME:
                        break;
                    case USE_SPARE_PROFILE:
                        break;
                    case USE_SPARE_NICKNAME + USE_SPARE_PROFILE:
                        break;
                        default:
                }
                setResult(result);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        setResult(result);
        finish();
    }
}
