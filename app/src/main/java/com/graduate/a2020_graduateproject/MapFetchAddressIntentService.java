package com.graduate.a2020_graduateproject;

import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.text.TextUtils;
import android.widget.Toast;
import androidx.annotation.Nullable;
import com.google.android.gms.maps.model.LatLng;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

///주소 검색 서비스 클래스
public class MapFetchAddressIntentService extends IntentService {

    private ResultReceiver receiver;
    public MapFetchAddressIntentService() {
        super("MapFetchAddressIntentService");
    }

    ///IntentService class는 background thread에서 동작할 수 있도록 함.->시간이 오래걸려도 UI에 영향을 미치지 않음.

    private void deliverResultToReceiver(int resultCode, String message){
        System.out.println("resultCode= "+resultCode+" message= "+ message);
        Bundle bundle = new Bundle();
        bundle.putString(MapConstants.RESULT_DATA_KEY, message);
        System.out.println("MapConstants.RESULT " +MapConstants.RESULT_DATA_KEY);
        receiver.send(resultCode, bundle);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        LatLng location=intent.getParcelableExtra(MapConstants.LOCATION_DATA_EXTRA);
        receiver=intent.getParcelableExtra(MapConstants.RECEIVER);

        //locale-> 주소 결과를 지역에 맞게 가져올 수 있음
        Geocoder geocoder=new Geocoder(this, Locale.getDefault());

        String errorMessage="";

        if(intent==null){
            return;
        }

        List<Address> addresses=null;

        try{
            addresses=geocoder.getFromLocation(
                    location.latitude,
                    location.longitude,
                    1 //주소 1개만 받음
            );
        } catch (IOException e) {
            errorMessage="try address 부분 오류";

            Toast.makeText(getApplicationContext(),errorMessage, Toast.LENGTH_LONG).show();

        } catch (IllegalArgumentException illegalArgumentException) {
                errorMessage="invalid latitude of longitude values";            //
            Toast.makeText(getApplicationContext(),errorMessage + ". " + "Latitude = " + location.latitude +", Longitude = " + location.longitude, Toast.LENGTH_LONG).show();
        }

        //주소를 찾지 못한 경우
        if (addresses == null || addresses.size()  == 0) {
            if (errorMessage.isEmpty()) {

                Toast.makeText(getApplicationContext(),errorMessage , Toast.LENGTH_LONG).show();
                System.out.println("주소를 찾지 못한 경우 "+errorMessage);
            }
            deliverResultToReceiver(MapConstants.FAILURE_RESULT, errorMessage);
        }
        else { //찾은 경우
            System.out.println("주소를 찾은 경우");
            Address address = addresses.get(0);
            ArrayList<String> addressFragments = new ArrayList<>();

            for(int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                addressFragments.add(address.getAddressLine(i));
                System.out.println("address.getAddressLine "+address.getAddressLine(i));
            }

            System.out.println("MapConstants.SUCCESS RESULT"+ MapConstants.SUCCESS_RESULT);
            deliverResultToReceiver(MapConstants.SUCCESS_RESULT,
                    TextUtils.join(System.getProperty("line.separator"),
                            addressFragments));

            //System.getProperty("line.separator") //현재 OS에 맞는 줄바꿈
        }
    }
}
