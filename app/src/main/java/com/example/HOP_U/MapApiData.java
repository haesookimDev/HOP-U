package com.example.HOP_U;

import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MapApiData {

    String apiUrl = "https://openapi.gg.go.kr/YoungBoyAndGirsWelfareConsult";
    String apiKey = "c7b968c4349e43bfbc55a7f60a7015c8";

    public ArrayList<MapData> getData() {
        //return과 관련된 부분
        ArrayList<MapData> dataArr = new ArrayList<MapData>();

        //네트워킹 작업은 메인스레드에서 처리하면 안된다. 따로 스레드를 만들어 처리하자
        Thread t = new Thread() {
            @Override
            public void run() {
                try {

                    //url과 관련된 부분
                    String fullurl = apiUrl + "?Key=" + apiKey;
                    URL url = new URL(fullurl);
                    InputStream is = url.openStream();

                    //xmlParser 생성
                    XmlPullParserFactory xmlFactory = XmlPullParserFactory.newInstance();
                    XmlPullParser parser = xmlFactory.newPullParser();
                    parser.setInput(is,"utf-8");

                    //xml과 관련된 변수들
                    boolean bName = false, bLat = false, bLong = false, bAdr=false, bnumber=false;
                    String name = "", latitude = "", longitude = "", adress="", number="";

                    //본격적으로 파싱
                    while(parser.getEventType() != XmlPullParser.END_DOCUMENT) {
                        int type = parser.getEventType();
                        MapData data = new MapData();

                        //태그 확인
                        if(type == XmlPullParser.START_TAG) {
                            if (parser.getName().equals("CONSLTN_CENTER_OPERT_GRP_NM")) {
                                bName = true;
                            }
                            else if (parser.getName().equals("REFINE_WGS84_LAT")) {
                                bLat = true;
                            }
                            else if (parser.getName().equals("REFINE_WGS84_LOGT")) {
                                bLong = true;
                            }
                            else if (parser.getName().equals("REFINE_ROADNM_ADDR")) {
                                bAdr = true;
                            }
                            else if (parser.getName().equals("CENTER_TELNO")){
                                bnumber = true;
                            }
                        }
                        //내용 확인
                        else if(type == XmlPullParser.TEXT) {
                            //Log.i("TAG","내용확인 했지롱");
                            if (bName) {
                                name = parser.getText();
                                bName = false;
                            } else if (bLat) {
                                latitude = parser.getText();
                                bLat = false;
                            } else if (bLong) {
                                longitude = parser.getText();
                                bLong = false;
                            } else if (bAdr) {
                                adress = parser.getText();
                                bAdr = false;
                            } else if (bnumber){
                                number = parser.getText();
                                bnumber = false;
                            }
                        }
                        //내용 다 읽었으면 데이터 추가
                        else if (type == XmlPullParser.END_TAG && parser.getName().equals("row")) {
                            data.setName(name);
                            data.setLatitude(Double.valueOf(latitude));
                            data.setLongitude(Double.valueOf(longitude));
                            data.setAdress(adress);
                            data.setNumber(number);
                            Log.i("TAG",""+data.toString());

                            dataArr.add(data);
                        }

                        type = parser.next();
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

        };
        try {
            t.start();
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return dataArr;
    }
}
