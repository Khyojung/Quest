package com.example.hyojung.quest;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class ViewQuest extends AppCompatActivity {

    final static int QUEST_CANCELED = 0, QUEST_REQUEST_CANCELED = 1, QUEST_RESPONDED = 2, QUEST_RESPOND_CANCELED = 3;

    long viewerId;
    QuestEntry viewingEntry;
    TextView title, area, reward, comment;
    Button questCancelButton, submitCancelButton, respondButton, respondCancelButton, backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quest_add);
        title = (TextView)findViewById(R.id.text_view_title);
        area = (TextView)findViewById(R.id.text_view_area);
        reward = (TextView)findViewById(R.id.text_view_reward);
        comment = (TextView)findViewById(R.id.text_view_comment);
        questCancelButton = (Button)findViewById(R.id.button_cancel_quest);
        submitCancelButton = (Button)findViewById(R.id.button_cancel_request);
        respondButton = (Button)findViewById(R.id.button_add_exit);
        respondCancelButton = (Button)findViewById(R.id.button_cancel_respond);
        backButton = (Button)findViewById(R.id.button_add_exit);

        Intent viewIntent = getIntent();
        long viewerId = viewIntent.getLongExtra("viewerId", -1);
        viewingEntry = (QuestEntry)viewIntent.getSerializableExtra("entry");
        this.setButtonsVisiblitiy();

        questCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {           // 거래 파기
                Intent intent = new Intent();
                setResult(QUEST_CANCELED, intent);
                finish();
            }
        });

        submitCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {           // 요청 취소
                Intent intent = new Intent();
                setResult(QUEST_REQUEST_CANCELED, intent);
                finish();
            }
        });

        respondButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {                  // 응답 버튼
                Intent intent = new Intent();
                //intent.putExtra("Respondent", )
                setResult(QUEST_RESPONDED, intent);
                finish();
            }
        });

        respondCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {            // 응답 취소 버튼
                Intent intent = new Intent();
                //intent.putExtra("Respondent", )
                setResult(QUEST_RESPOND_CANCELED, intent);
                finish();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                setResult(RESULT_CANCELED, intent);
                finish();
            }
        });
    }

    public void setButtonsVisiblitiy() {
        if (viewingEntry.getState() != QuestEntry.ACCEPTED) {      // 진행중인 거래가 아닌경우 거래 취소버튼 숨기기
            questCancelButton.setEnabled(false);
            questCancelButton.setVisibility(Button.INVISIBLE);
        }
        if (viewingEntry.getRequester() == viewerId) {          // 해당 거래를 해당 거래 요청자가 보는 경우 응답, 응답 취소 버튼 숨기기
            respondButton.setEnabled(false);
            respondButton.setVisibility(Button.INVISIBLE);
            respondCancelButton.setEnabled(false);
            respondCancelButton.setVisibility(Button.INVISIBLE);
            if (viewingEntry.getState() == QuestEntry.COMPLETED) {     // 이미 완료된 거래를 보는 경우 요청 취소버튼 숨기기
                submitCancelButton.setEnabled(false);
                submitCancelButton.setVisibility(Button.INVISIBLE);
            }
        }
        else {                                                  // 해당 거래를 다른 사람이 보는 경우 요청 취소버튼 숨기기
            submitCancelButton.setEnabled(false);
            submitCancelButton.setVisibility(Button.INVISIBLE);
            if (viewingEntry.getState() >= QuestEntry.ACCEPTED) {         // 이미 매칭된 퀘스트인 경우 응답 버튼 숨기기
                respondButton.setEnabled(false);
                respondButton.setVisibility(Button.INVISIBLE);
            }
            if (!viewingEntry.getRespondent().contains(viewerId)) {         // 자신이 응답한 거래가 아닌 경우 응답 취소버튼 숨기기
                respondCancelButton.setEnabled(false);
                respondCancelButton.setVisibility(Button.INVISIBLE);
            }
        }
    }
}
