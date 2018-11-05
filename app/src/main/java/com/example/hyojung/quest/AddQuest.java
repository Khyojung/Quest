package com.example.hyojung.quest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class AddQuest extends AppCompatActivity {

    EditText inputTitle, inputArea, inputReward, inputContext;
    Button submitButton, cancelButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quest_add);
        Intent intent = getIntent();

        inputTitle = (EditText)findViewById(R.id.textbox_add_title);
        inputArea = (EditText)findViewById(R.id.textbox_add_area);
        inputReward = (EditText)findViewById(R.id.textbox_add_context);
        inputContext = (EditText)findViewById(R.id.textbox_add_context);
        submitButton = (Button)findViewById(R.id.button_add_submit);
        cancelButton = (Button)findViewById(R.id.button_add_exit);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("Titie", inputTitle.getText().toString());
                intent.putExtra("Area", inputArea.getText().toString());
                intent.putExtra("Reward", inputReward.getText().toString());
                intent.putExtra("Info", inputContext.getText().toString());
                setResult(Activity.RESULT_OK, intent);
                startActivity(intent);
                finish();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                setResult(Activity.RESULT_CANCELED, intent);
                finish();
            }
        });

    }
}
