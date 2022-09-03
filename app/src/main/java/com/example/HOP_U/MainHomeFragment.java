package com.example.HOP_U;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import me.relex.circleindicator.CircleIndicator3;

public class MainHomeFragment extends Fragment {

    private ViewPager2 mPager;
    private FragmentStateAdapter pagerAdapter;
    private int num_page = 3;
    private CircleIndicator3 mIndicator;
    private Button cheeringBtn;
    private EditText cheeringEdit;
    private TextView cheeringMore;
    private TextView characterLevel;
    private TextView nextLevel;
    private ImageView character;

    private int totalPoint;
    private int pointDiffer;
    private String level;

    //최근 소식 리사이클러뷰
    private RecyclerAdapter adapter;
    private LinearLayout characterContent;

    //신고 플로팅 버튼
    FloatingActionButton fab;
    BottomNavigationView bottomNavigationView;

    private RecyclerView cheeringRecyclerView = null;
    private CheeringAdapter cheeringAdapter = null;
    private ArrayList<CheeringData> cheeringListRecycler;
    private ArrayList<CheeringData> cheeringListRecyclerMore;
    private CheeringRecyclerDeco cheeringRecyclerDeco;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private UserData userData;

    private List<Map<String, Object>> finalCheeringList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.fragment_main_home, container, false);
        /**
         * 가로 슬라이드 뷰 Fragment
         */
//ViewPager2
        mPager = view.findViewById(R.id.viewpager);
        cheeringBtn = view.findViewById(R.id.edit_cheering_btn);
        cheeringEdit = view.findViewById(R.id.edit_cheering);
        cheeringMore = view.findViewById(R.id.cheeringMore);
        characterLevel = view.findViewById(R.id.character_level);
        nextLevel = view.findViewById(R.id.next_level);
        characterContent = view.findViewById(R.id.character_content);
        character = view.findViewById(R.id.character_image);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        userData = ((MainActivity)MainActivity.context_main).userData;

        cheeringRecyclerView = view.findViewById(R.id.cheeringRecycler);
        cheeringListRecycler = new ArrayList<>();
        cheeringListRecyclerMore = new ArrayList<>();
        cheeringAdapter = new CheeringAdapter(cheeringListRecycler);
        cheeringRecyclerView.setAdapter(cheeringAdapter);
        cheeringRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));

        cheeringRecyclerDeco = new CheeringRecyclerDeco(getContext());
        cheeringRecyclerView.addItemDecoration(cheeringRecyclerDeco);

//Adapter
        pagerAdapter = new MyAdapter(getActivity(), num_page);
        mPager.setAdapter(pagerAdapter);
//Indicator
        mIndicator = view.findViewById(R.id.indicator);
        mIndicator.setViewPager(mPager);
        mIndicator.createIndicators(num_page, 0);
//ViewPager Setting
        mPager.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
/**
 * 이 부분 조정하여 처음 시작하는 이미지 설정.
 * 2000장 생성하였으니 현재위치 1002로 설정하여
 * 좌 우로 슬라이딩 할 수 있게 함. 거의 무한대로
 */
        mPager.setCurrentItem(999); //시작 지점
        mPager.setOffscreenPageLimit(3); //최대 이미지 수
        mPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                if (positionOffsetPixels == 0) {
                    mPager.setCurrentItem(position);
                }
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                mIndicator.animatePageSelected(position % num_page);
            }
        });

        loadCheering();
        setMainLevel();

        List<Map<String, Object>> cheeringList = null;
        cheeringList = new ArrayList<>();

        finalCheeringList = cheeringList;

        cheeringBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.collection("cheer")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        Map<String, Object> data = document.getData();
                                        Optional result = finalCheeringList.stream().filter(x -> Objects.equals(x.get("uId"), user.getUid())).findFirst();
                                        if (!result.isPresent()) {
                                            finalCheeringList.add(data);
                                        }
                                    }

                                    Optional result = finalCheeringList.stream().filter(x -> Objects.equals(x.get("uId"), user.getUid())).findFirst();
                                    if (!result.isPresent()) {

                                        CheeringData cheeringData = new CheeringData(userData.userName,
                                                cheeringEdit.getText().toString(),
                                                user.getUid());

                                        db.collection("cheer")
                                                .document(user.getUid()).set(cheeringData);

                                        db.collection("users")
                                                .document(userData.getUid())
                                                .update("point", userData.getPoint()+5);
                                        userData.setPoint(userData.getPoint()+5);

                                        loadCheering();
                                    } else {
                                        Toast.makeText(getContext(), "이미 작성하셨습니다.", Toast.LENGTH_SHORT).show();
                                    }
                                    cheeringEdit.setText("");
                                } else {
                                    Log.w(TAG, "Error getting documents.", task.getException());
                                }
                            }
                        });
            }
        });

        cheeringMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cheeringAdapter = new CheeringAdapter(cheeringListRecyclerMore);
                cheeringAdapter.notifyDataSetChanged();
                cheeringRecyclerView.setAdapter(cheeringAdapter);
                cheeringMore.setVisibility(View.GONE);
            }
        });

        //가로 스크롤뷰
        init(view);
        getData();

        //신고 플로팅 버튼
        fab = (FloatingActionButton) view.findViewById(R.id.fab_btn);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CustomDialog dialog = new CustomDialog(getContext());
                dialog.callDialog();
                //Intent intent = new Intent(getApplicationContext(), make_group.class); //그룹 만들기 화면으로 연결
                //startActivity(intent); //액티비티 열기
            }
        });

        return view;
    }

    //가로 스크롤뷰
    private void init(ViewGroup viewGroup) {
        RecyclerView recyclerView = viewGroup.findViewById(R.id.recyclerView);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);

        adapter = new RecyclerAdapter();
        recyclerView.setAdapter(adapter);
    }
    //가로 스크롤뷰
    private void getData() {
        // 임의의 데이터입니다.

        List<Integer> listResId = Arrays.asList(
                R.drawable.new1,
                R.drawable.new2,
                R.drawable.new3
        );
        for (int i = 0; i < listResId.size(); i++) {
            // 각 List의 값들을 data 객체에 set 해줍니다.
            Data data = new Data();
            data.setResId(listResId.get(i));

            // 각 값이 들어간 data를 adapter에 추가합니다.
            adapter.addItem(data);
        }

        // adapter의 값이 변경되었다는 것을 알려줍니다.
        adapter.notifyDataSetChanged();
    }

    private void addItem(String name, String cheer, String uid) {
        CheeringData item = new CheeringData(name, cheer, uid);
        cheeringListRecycler.add(item);
    }

    private void addItemMore(String name, String cheer, String uid) {
        CheeringData item = new CheeringData(name, cheer, uid);
        cheeringListRecyclerMore.add(item);
    }

    private void loadCheering(){
        try {
            cheeringListRecycler.clear();
            db.collection("cheer")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Map<String, Object> data = document.getData();
                                    if (cheeringListRecycler.size() < 3){
                                        addItem(data.get("name").toString(),
                                                data.get("cheer").toString(),
                                                data.get("uId").toString());
                                    }
                                    addItemMore(data.get("name").toString(),
                                            data.get("cheer").toString(),
                                            data.get("uId").toString());
                                }
                                cheeringAdapter.notifyDataSetChanged();
                                if (cheeringListRecyclerMore.size() > 3) {
                                    cheeringMore.setVisibility(View.VISIBLE);
                                }else{
                                    cheeringMore.setVisibility(View.GONE);
                                }
                            } else {
                                Log.w(TAG, "Error getting documents.", task.getException());
                            }
                        }
                    });

        }catch (IndexOutOfBoundsException e){
        }
    }



    private void setMainLevel(){

        final int Level1standard = 10;
        final int Level2standard = 20;
        final int Level3standard = 30;
        final int Level4standard = 40;
        final int Level5standard = 50;
        final int Level6standard = 60;
        final int Level7standard = 70;
        final int Level8standard = 80;

        if (userData.getPoint() >= 0 & userData.getPoint() <= Level1standard){

            pointDiffer = Level1standard - userData.getPoint();

            level = "알 1";

            character.setImageResource(R.drawable.level_default);
            characterLevel.setText(level+"단계");
            nextLevel.setText("다음 단계까지 "+ pointDiffer +" point 남았어요!");
        } else if (userData.getPoint() > Level1standard & userData.getPoint() <= Level2standard){

            pointDiffer = Level2standard - userData.getPoint();

            level = "탄생 2";

            character.setImageResource(R.drawable.level2);
            characterLevel.setText(level+"단계");
            nextLevel.setText("다음 단계까지 "+ pointDiffer +" point 남았어요!");

        } else if (userData.getPoint() > Level2standard & userData.getPoint() <= Level3standard){

            pointDiffer = Level3standard - userData.getPoint();

            level = "아기 3";

            character.setImageResource(R.drawable.level3);
            characterLevel.setText(level+"단계");
            nextLevel.setText("다음 단계까지 "+ pointDiffer +" point 남았어요!");

        } else if (userData.getPoint() > Level3standard & userData.getPoint() <= Level4standard){

            pointDiffer = Level4standard - userData.getPoint();

            level = "어린이 4";

            character.setImageResource(R.drawable.level4);
            characterLevel.setText(level+"단계");
            nextLevel.setText("다음 단계까지 "+ pointDiffer +" point 남았어요!");

        } else if (userData.getPoint() > Level4standard & userData.getPoint() <= Level5standard){

            pointDiffer = Level5standard - userData.getPoint();

            level = "청소년 5";

            character.setImageResource(R.drawable.level5);
            characterLevel.setText(level+"단계");
            nextLevel.setText("다음 단계까지 "+ pointDiffer +" point 남았어요!");

        } else if (userData.getPoint() > Level5standard & userData.getPoint() <= Level6standard){

            pointDiffer = Level6standard - userData.getPoint();

            level = "청년 6";

            character.setImageResource(R.drawable.level6);
            characterLevel.setText(level+"단계");
            nextLevel.setText("다음 단계까지 "+ pointDiffer +" point 남았어요!");

        } else if (userData.getPoint() > Level6standard & userData.getPoint() <= Level7standard){

            pointDiffer = Level7standard - userData.getPoint();

            level = "성인 7";

            character.setImageResource(R.drawable.level7);
            characterLevel.setText(level+"단계");
            nextLevel.setText("다음 단계까지 "+ pointDiffer +" point 남았어요!");

        } else if (userData.getPoint() > Level7standard & userData.getPoint() <= Level8standard){
            totalPoint = Level8standard - Level7standard;
            Double pointRatio = (Double.valueOf(userData.getPoint()-Level7standard)/totalPoint);
            pointDiffer = Level8standard - userData.getPoint();

            if (pointRatio > 10){

                pointDiffer = 0;
            }

            level = "가디언 8";

            character.setImageResource(R.drawable.level8);
            characterLevel.setText(level+"단계");
            nextLevel.setText("다음 단계까지 "+ pointDiffer +" point 남았어요!");
        }

    }
}

