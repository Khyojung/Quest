package com.example.hyojung.quest.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.hyojung.quest.Queries.QuestQuery;
import com.example.hyojung.quest.R;

import java.util.ArrayList;

import static com.example.hyojung.quest.Activity.MainActivity.CHAT_TEST;

public class ViewQuest extends AppCompatActivity {

    final public static int QUEST_DESTROYED = 10, QUEST_REQUEST_CANCELED = 11, QUEST_ACCEPTED = 12,
            QUEST_COMPLETED = 13, BACK_PRESSED = 13;

    long viewerId;
    QuestQuery viewingEntry;
    TextView title, area, reward, comment;
    Button questDestroyButton, questCompleteButton, chatroomButton, submitCancelButton, acceptButton, backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quest_view);

        title = (TextView)findViewById(R.id.text_view_title);
        area = (TextView)findViewById(R.id.text_view_area);
        reward = (TextView)findViewById(R.id.text_view_reward);
        comment = (TextView)findViewById(R.id.text_view_comment);

        questDestroyButton = (Button)findViewById(R.id.button_destroyQuest);
        questCompleteButton = (Button)findViewById(R.id.button_completeQuest);
        chatroomButton = (Button)findViewById(R.id.button_chatRoom);
        submitCancelButton = (Button)findViewById(R.id.button_cancel_request);
        acceptButton = (Button)findViewById(R.id.button_accept);
        backButton = (Button)findViewById(R.id.button_view_exit);

        Intent viewIntent = getIntent();
        viewerId = viewIntent.getLongExtra("viewerId", -1);
        viewingEntry = (QuestQuery)viewIntent.getSerializableExtra("entry");
        ArrayList<String> entryInfo = viewingEntry.getQuestInfo();
        title.setText(entryInfo.get(0));
        area.setText(entryInfo.get(1));
        reward.setText(entryInfo.get(2));
        comment.setText(entryInfo.get(3));

        this.setButtonsVisiblitiy();

        questDestroyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {           // 거래 파기
                Intent intent = new Intent();
                viewingEntry.setCanceled();
                intent.putExtra("resultUpdate", viewingEntry);
                setResult(QUEST_DESTROYED, intent);
                finish();
            }
        });

        questCompleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {           // 거래 완료
                Intent intent = new Intent();
                viewingEntry.setCompleted();
                intent.putExtra("resultUpdate", viewingEntry);
                setResult(QUEST_COMPLETED, intent);
                finish();
            }
        });

        chatroomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {           // 채팅방
                Intent intent = new Intent();
                Intent chatIntent = new Intent(ViewQuest.this, ChatRoom.class);
                chatIntent.putExtra("myId", viewerId);
                chatIntent.putExtra("otherId", (long) 123123);
                startActivityForResult(chatIntent, CHAT_TEST);
            }
        });

        submitCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {           // 요청 취소
                Intent intent = new Intent();
                viewingEntry.setCanceled();
                intent.putExtra("resultUpdate", viewingEntry);
                setResult(QUEST_REQUEST_CANCELED, intent);
                finish();
            }
        });

        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {                  // 수락 버튼
                Intent intent = new Intent();
                viewingEntry.setAcceptor(viewerId);
                intent.putExtra("resultUpdate", viewingEntry);
                setResult(QUEST_ACCEPTED, intent);
                finish();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
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
        if (requestCode == CHAT_TEST && resultCode == ChatRoom.EXIT) {

        }
    }

    public void setButtonsVisiblitiy() {
        if (viewingEntry.getState() != QuestQuery.ACCEPTED) {      // 진행중인 거래가 아닌경우 거래 파기버튼 숨기기
            questDestroyButton.setEnabled(false);
            questDestroyButton.setVisibility(Button.GONE);
            questCompleteButton.setEnabled(false);
            questCompleteButton.setVisibility(Button.GONE);
            chatroomButton.setEnabled(false);
            chatroomButton.setVisibility(Button.GONE);
        }
        if (viewingEntry.getRequester() == viewerId) {          // 해당 거래를 해당 거래 요청자가 보는 경우 수락 버튼 숨기기
            acceptButton.setEnabled(false);
            acceptButton.setVisibility(Button.GONE);
            if (viewingEntry.getState() >= QuestQuery.ACCEPTED) {    // 수락되거나 완료된 거래이면 요청 취소버튼 숨기기
                submitCancelButton.setEnabled(false);
                submitCancelButton.setVisibility(Button.GONE);
            }
        }
        else {                                                  // 해당 거래를 다른 사람이 보는 경우 요청 취소버튼 숨기기
            submitCancelButton.setEnabled(false);
            submitCancelButton.setVisibility(Button.GONE);
            if (viewingEntry.getState() >= QuestQuery.ACCEPTED) {         // 이미 매칭된 퀘스트인 경우 수락 버튼 숨기기
                acceptButton.setEnabled(false);
                acceptButton.setVisibility(Button.GONE);
            }
        }
    }

    @Override
    public void onBackPressed() {
        setResult(BACK_PRESSED);
        finish();
    }
}
