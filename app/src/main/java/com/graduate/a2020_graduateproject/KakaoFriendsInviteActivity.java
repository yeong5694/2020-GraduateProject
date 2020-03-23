package com.graduate.a2020_graduateproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.kakao.kakaolink.v2.KakaoLinkResponse;
import com.kakao.kakaolink.v2.KakaoLinkService;
import com.kakao.message.template.ButtonObject;
import com.kakao.message.template.ContentObject;
import com.kakao.message.template.FeedTemplate;
import com.kakao.message.template.LinkObject;
import com.kakao.message.template.SocialObject;
import com.kakao.message.template.TextTemplate;
import com.kakao.network.ErrorResult;
import com.kakao.network.callback.ResponseCallback;
import com.kakao.util.helper.log.Logger;

import java.util.HashMap;
import java.util.Map;

public class KakaoFriendsInviteActivity extends AppCompatActivity {

    private Button redirect_successLogin_btn;
    private String KAKAO_BASE_LINK = "https://developers.kakao.com"; // 나중에 playStore 로 연결


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kakao_friends);

        redirect_successLogin_btn = (Button)findViewById(R.id.redirect_successLogin_btn);
        redirect_successLogin_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent intent = new Intent(KakaoFriendsInviteActivity.this, SuccessLoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // 템플릿 생성
        /*
        TextTemplate params = TextTemplate.newBuilder("Text", LinkObject.newBuilder().setWebUrl("https://developers.kakao.com")
                .setMobileWebUrl("https://developers.kakao.com").build())
                .setButtonTitle("This is button").build();
*/
        FeedTemplate params = FeedTemplate
                .newBuilder(ContentObject.newBuilder("Sharing Trips",
                        "http://mud-kage.kakao.co.kr/dn/NTmhS/btqfEUdFAUf/FjKzkZsnoeE4o19klTOVI1/openlink_640x640s.jpg",
                        LinkObject.newBuilder().setWebUrl(KAKAO_BASE_LINK) //
                                .setMobileWebUrl(KAKAO_BASE_LINK).build()) //
                        .setDescrption("수정 코드 : xxx-xxx-xxx")
                        .build())
                .addButton(new ButtonObject("앱에서 보기", LinkObject.newBuilder()
                        .setWebUrl(KAKAO_BASE_LINK) //
                        .setMobileWebUrl(KAKAO_BASE_LINK) //
                        .setAndroidExecutionParams("key1=value1")
                        .build()))
                .build();

        // 기본적으로 구현해야 할 것
        Map<String, String> serverCallbackArgs = new HashMap<String, String>();
        serverCallbackArgs.put("user_id", "${current_user_id}");
        serverCallbackArgs.put("product_id", "${shared_product_id}");

        KakaoLinkService.getInstance().sendDefault(this, params, serverCallbackArgs, new ResponseCallback<KakaoLinkResponse>() {
            @Override
            public void onFailure(ErrorResult errorResult) {
                Logger.e(errorResult.toString());
            }

            @Override
            public void onSuccess(KakaoLinkResponse result) {
                // 템플릿 밸리데이션과 쿼터 체크가 성공적으로 끝남. 톡에서 정상적으로 보내졌는지 보장은 할 수 없다.
                // 전송 성공 유무는 서버콜백 기능을 이용하여야 한다.
            }
        });
    }



}
