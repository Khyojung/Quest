package com.example.hyojung.quest.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.hyojung.quest.R;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

public class AddQuest extends AppCompatActivity {

    final public static int QUEST_ADDED = 10, BACK_PRESSED = 11, PLACE_PICKER_REQUEST = 20;
    EditText inputTitle, inputReward, inputComment;
    TextView textPoint;
    double latitude, longitude;
    Button addPointButton, submitButton, cancelButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quest_add);

        inputTitle = (EditText)findViewById(R.id.textbox_add_title);
        textPoint = (TextView)findViewById(R.id.text_point);
        inputReward = (EditText)findViewById(R.id.textbox_add_reward);
        inputComment = (EditText)findViewById(R.id.textbox_add_comment);
        addPointButton = (Button)findViewById(R.id.button_addPoint);
        submitButton = (Button)findViewById(R.id.button_add_submit);
        cancelButton = (Button)findViewById(R.id.button_add_exit);

        addPointButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlacePicker.IntentBuilder intentBuilder = new PlacePicker.IntentBuilder();
                try {
                    Intent intent = intentBuilder.build(AddQuest.this);
                    startActivityForResult(intent,PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
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
        if (requestCode == PLACE_PICKER_REQUEST && resultCode == RESULT_OK) {
            final Place place = PlacePicker.getPlace(this, data);
            final CharSequence name = place.getName();
            final CharSequence address = place.getAddress();
            String attributions = (String) place.getAttributions();
            if (attributions == null) {
                attributions = "";
            }
            this.latitude = place.getLatLng().latitude;
            this.longitude = place.getLatLng().longitude;
            textPoint.setText(name);
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        setResult(BACK_PRESSED, intent);
        finish();
    }
}
