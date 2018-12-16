package com.hyojung.quest.Activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.hyojung.quest.BitmapMaker;
import com.example.hyojung.quest.R;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class ModifyUserInfo extends AppCompatActivity {

    public static final int DEFAULT = 0, USE_SPARE_NICKNAME = 8, USE_SPARE_PROFILE = 16;
    private final int GALLERY_REQUEST = 20;

    TextView kakao_name, spare_name;
    EditText edit_nickname;
    Switch switchSpareNickname, switchSpareProfileImage;

    Bitmap bitmap_spare = null;
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
        switchSpareNickname = (Switch)findViewById(R.id.switch_sparename);
        switchSpareProfileImage = (Switch)findViewById(R.id.switch_spareimage);
        image_kakaoProfile = (ImageView)findViewById(R.id.image_kakao_profile);
        image_spareProfile = (ImageView)findViewById(R.id.image_spare_profile);
        button_submitName = (Button)findViewById(R.id.button_submit_name);
        button_selectImage = (Button)findViewById(R.id.button_select_image);
        button_back = (Button)findViewById(R.id.button_modify_info_exit);

        bitmap_spare = BitmapMaker.resizeForProfile(BitmapFactory.decodeResource(getResources(), R.drawable.profile));
        image_kakaoProfile.setImageBitmap(BitmapMaker.byteArrayToBitmap(intent.getByteArrayExtra("bitmap")));
        image_spareProfile.setImageBitmap(bitmap_spare);
        this.buttonOnClickListenerInit();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Uri selectedImageUri = data.getData();
            try {
                InputStream imageStream = getContentResolver().openInputStream(selectedImageUri);
                bitmap_spare = BitmapFactory.decodeStream(imageStream);
                bitmap_spare = BitmapMaker.resizeForProfile(bitmap_spare);
                image_spareProfile.setImageBitmap(bitmap_spare);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private void buttonOnClickListenerInit() {
        button_submitName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spare_name.setText(edit_nickname.getText().toString());
            }
        });

        button_selectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent imageSelect = new Intent(Intent.ACTION_PICK);
                imageSelect.setType("image/*");
                startActivityForResult(imageSelect, GALLERY_REQUEST);
            }
        });

        button_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishWithIntent();
            }
        });
    }

    public void finishWithIntent() {
        Intent intent = new Intent();
        if (switchSpareNickname.isChecked()) {
            result += USE_SPARE_NICKNAME;
            intent.putExtra("spareName", spare_name.getText().toString());
        }
        if (switchSpareProfileImage.isChecked()) {
            result += USE_SPARE_PROFILE;
            intent.putExtra("spareBitmap", BitmapMaker.BitmapToByteArray(bitmap_spare));
        }
        result = result == 0 ? DEFAULT : result;
        setResult(result, intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        this.finishWithIntent();
    }
}
