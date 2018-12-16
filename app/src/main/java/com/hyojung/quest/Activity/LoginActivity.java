package com.hyojung.quest.Activity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.hyojung.quest.GlobalApplication;
import com.hyojung.quest.Queries.LoginQuery;
import com.hyojung.quest.JSON.JSONSendTask;
import com.example.hyojung.quest.R;
import com.kakao.auth.AuthType;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;
import com.kakao.util.exception.KakaoException;

import java.security.MessageDigest;

public class LoginActivity extends AppCompatActivity {

    SessionCallback callback;
    LoginActivity instance;
    Button button_adminAccess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        GlobalApplication.setCurrentActivity(this);
        instance = this;
        callback = new SessionCallback();
        Session.getCurrentSession().addCallback(callback);
        Session.getCurrentSession().open(AuthType.KAKAO_LOGIN_ALL, LoginActivity.this);

        button_adminAccess = (Button)findViewById(R.id.button_admin_login);
        button_adminAccess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginQuery clientLoginQuery = new LoginQuery(0, "관리자", "관리자");
                Intent intent = new Intent(instance, MainActivity.class);
                intent.putExtra("kakaoID", (long)0);
                intent.putExtra("kakaoNickName", "관리자");
                intent.putExtra("kakaoProfileImage", "관리자");
                JSONSendTask jsonTask = new JSONSendTask(clientLoginQuery);
                startActivity(intent);
                finish();
            }
        });
    }

    private class SessionCallback implements ISessionCallback {
        //로그인 성공
        @Override
        public void onSessionOpened() {
            UserManagement.requestMe(new MeResponseCallback() {
                @Override
                public void onSessionClosed(ErrorResult errorResult) {

                }

                @Override
                public void onNotSignedUp() {

                }

                @Override
                public void onSuccess(final UserProfile userProfile) {
                    LoginQuery clientLoginQuery = new LoginQuery(userProfile.getId(), userProfile.getNickname(), userProfile.getProfileImagePath());
                    JSONSendTask jsonTask = new JSONSendTask(clientLoginQuery);
                    Intent intent = new Intent(instance, MainActivity.class);
                    intent.putExtra("kakaoID", userProfile.getId());
                    intent.putExtra("kakaoNickName", userProfile.getNickname());
                    intent.putExtra("kakaoProfileImage", userProfile.getProfileImagePath());
                    startActivity(intent);
                    finish();
                }

                @Override
                public void onFailure(ErrorResult errorResult) {
                    Toast.makeText(instance, "로그인 실패", Toast.LENGTH_LONG).show();
                }
            });
        }

        @Override
        public void onSessionOpenFailed(KakaoException exception) {
            Toast.makeText(instance, "로그아웃", Toast.LENGTH_LONG).show();
        }
    }

    // 해시값 구하는 코드
    public void getHashKey() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md;
                md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String something = new String(Base64.encode(md.digest(), 0));
                Log.e("Hash key", something);
            }
        } catch (Exception e) {
            Log.e("name not found", e.toString());
        }
    }
}
