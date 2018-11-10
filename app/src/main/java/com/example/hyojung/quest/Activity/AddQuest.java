package com.example.hyojung.quest.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.hyojung.quest.R;

public class AddQuest extends AppCompatActivity {

    final public static int QUEST_ADDED = 10, BACK_PRESSED = 11;
    EditText inputTitle, inputArea, inputReward, inputComment;
    Button submitButton, cancelButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quest_add);

        inputTitle = (EditText)findViewById(R.id.textbox_add_title);
        inputArea = (EditText)findViewById(R.id.textbox_add_area);
        inputReward = (EditText)findViewById(R.id.textbox_add_reward);
        inputComment = (EditText)findViewById(R.id.textbox_add_comment);
        submitButton = (Button)findViewById(R.id.button_add_submit);
        cancelButton = (Button)findViewById(R.id.button_add_exit);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("Title", inputTitle.getText().toString());
                intent.putExtra("Area", inputArea.getText().toString());
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
    public void onBackPressed() {
        Intent intent = new Intent();
        setResult(BACK_PRESSED, intent);
        finish();
    }
}
