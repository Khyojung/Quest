package com.example.hyojung.quest.Activity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.example.hyojung.quest.Queries.LoginQuery;
import com.example.hyojung.quest.JSON.JSONSendTask;
import com.example.hyojung.quest.R;
import com.kakao.auth.AuthType;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;
import com.kakao.util.exception.KakaoException;

import java.security.MessageDigest;

public class LoginActivity extends AppCompatActivity {

    SessionCallback callback;
    LoginActivity instance;

    final public static int LOGIN_SUCCESS = 0, LOGIN_FAILURE = 1, LOGOUT = 2, EXIT = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        instance = this;
        callback = new SessionCallback();
        Session.getCurrentSession().addCallback(callback);
        Session.getCurrentSession().open(AuthType.KAKAO_LOGIN_ALL, LoginActivity.this);
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
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra("kakaoID", userProfile.getId());
                    intent.putExtra("kakaoNickName", userProfile.getNickname());
                    intent.putExtra("kakaoProfileImage", userProfile.getProfileImagePath());
                    startActivityForResult(intent, LOGIN_SUCCESS);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == LOGIN_SUCCESS) {
            if (resultCode == LOGOUT) {
                this.logout();
            } else if (resultCode == EXIT) {
                finish();
            }
        }
    }

    public void logout() {
        UserManagement.requestLogout(new LogoutResponseCallback() {
            @Override
            public void onCompleteLogout() {

            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        finish();
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
