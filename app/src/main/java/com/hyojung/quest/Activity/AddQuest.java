package com.hyojung.quest.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.hyojung.quest.R;

public class AddQuest extends AppCompatActivity {

    final public static int QUEST_ADDED = 10, BACK_PRESSED = 11, AREA_REQUEST = 1;
    EditText inputTitle, inputReward, inputComment;
    EditText textPoint;
    double latitude, longitude;
    Button addPointButton, submitButton, cancelButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quest_add);

        inputTitle = (EditText)findViewById(R.id.textbox_add_title);
        textPoint = (EditText)findViewById(R.id.text_point);
        inputReward = (EditText)findViewById(R.id.textbox_add_reward);
        inputComment = (EditText)findViewById(R.id.textbox_add_comment);
        addPointButton = (Button)findViewById(R.id.button_addPoint);
        submitButton = (Button)findViewById(R.id.button_add_submit);
        cancelButton = (Button)findViewById(R.id.button_add_exit);

        addPointButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddQuest.this, AddPositionActivity.class);
                startActivityForResult(intent, AREA_REQUEST);
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("Title", inputTitle.getText().toString());
                intent.putExtra("Place", textPoint.getText().toString());
                intent.putExtra("Position", new double[] {latitude, longitude});
                intent.putExtra("Reward", inputReward.getText().toString());
                intent.putExtra("Comment", inputComment.getText().toString());
                setResult(QUEST_ADDED, intent);
                finish();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                setResult(BACK_PRESSED, intent);
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == AREA_REQUEST) {
            if (resultCode == AddPositionActivity.AREA_SELECTED) {
                this.latitude = data.getDoubleExtra("latitude", 0);
                this.longitude = data.getDoubleExtra("longitude", 0);
                textPoint.setText(data.getStringExtra("areaName"));
            }
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        setResult(BACK_PRESSED, intent);
        finish();
    }
}
