package com.example.shoujixiufu;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shoujixiufu.adapter.CaseAdapter;
import com.example.shoujixiufu.model.CaseModel;

import java.util.ArrayList;
import java.util.List;

public class CasesActivity extends AppCompatActivity implements CaseAdapter.OnCaseClickListener {

    private RecyclerView casesRecyclerView;
    private CaseAdapter caseAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cases);

        // 初始化RecyclerView
        casesRecyclerView = findViewById(R.id.cases_recycler_view);
        casesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        
        // 创建并设置适配器
        List<CaseModel> caseList = createCaseData();
        caseAdapter = new CaseAdapter(this, caseList, this);
        casesRecyclerView.setAdapter(caseAdapter);
        
        // 设置底部导航点击事件
        setupBottomNavigation();
    }

    private List<CaseModel> createCaseData() {
        List<CaseModel> caseList = new ArrayList<>();
        
        // 创建案例数据
        caseList.add(new CaseModel(
                "case1",
                "在手机相册中浏览照片时，不小心滑动屏幕导致一些重要的照片被误删除。",
                "2023-12-29 13:15:26",
                "李女士在整理手机相册时，不小心删除了一些珍贵的家庭照片。这些照片记录了她孩子的成长瞬间，对她来说意义重大。经过专业的数据恢复处理，我们成功帮助她找回了所有被误删的照片，让这些珍贵的回忆得以保存。专业的恢复技术让看似消失的数据重新回到了用户手中，再次证明了数据恢复的重要性。",
                "image_photo"
        ));
        
        caseList.add(new CaseModel(
                "case2",
                "在手机上使用存储卡来存储大量的照片和视频，不小心，格式化了存储卡，导致里面的所有文件都丢失了。",
                "2023-12-24 15:15:32",
                "明先生是一位手机用户，他在一次意外中不小心格式化了自己的手机存储卡。这个存储卡里保存着他珍贵的照片和视频文件，明先生非常着急，因为这些照片和视频对他来说非常重要。\n\n他立刻寻求帮助，并咨询了专业的文件恢复大师。尽管存储卡已经被格式化，但是这些文件其实并没有被完全删除，只是变得不可见了。通过仔细分析存储卡的数据，文件恢复大师成功地找回了小明丢失的照片和视频文件，明先生非常感激文件恢复大师的帮助，他可以重新拥有那些重要的照片和视频，重新留住了宝贵的回忆。",
                "image_sdcard"
        ));
        
        caseList.add(new CaseModel(
                "case3",
                "不小心将微信聊天记录删除，非常着急想要找回。",
                "2023-12-15 09:23:10",
                "王先生在清理手机时不小心删除了重要的微信聊天记录，其中包含了大量的工作沟通和重要文件。由于这些聊天记录对他的工作至关重要，他非常着急。通过专业的微信数据恢复技术，我们成功帮助他找回了所有的聊天记录，包括文字、图片和文件，让他的工作得以正常进行。",
                "image_wechat"
        ));
        
        caseList.add(new CaseModel(
                "case4",
                "误删重要的音频文件，手机找不到该音频文件。",
                "2023-12-03 19:08:43",
                "张先生误删了一个重要的音频文件，这个文件是他录制的重要会议记录。由于工作需要，这个音频文件非常重要，但在手机中已经找不到了。通过深度扫描和专业恢复技术，我们成功帮助他找回了这个音频文件，确保了他的工作不会受到影响。",
                "image_audio"
        ));
        
        return caseList;
    }

    private void setupBottomNavigation() {
        LinearLayout navHome = findViewById(R.id.nav_home);
        LinearLayout navCases = findViewById(R.id.nav_cases);
        LinearLayout navOrder = findViewById(R.id.nav_order);
        LinearLayout navProfile = findViewById(R.id.nav_profile);

        navHome.setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });

        navCases.setOnClickListener(v -> {
            // 已经在案例页面，不需要操作
        });

        navOrder.setOnClickListener(v -> {
            // TODO: 跳转到订单页面
            // startActivity(new Intent(this, OrderActivity.class));
        });

        navProfile.setOnClickListener(v -> {
            // TODO: 跳转到个人页面
            // startActivity(new Intent(this, ProfileActivity.class));
        });
    }

    @Override
    public void onCaseClick(CaseModel caseModel) {
        Intent intent = new Intent(this, CaseDetailActivity.class);
        intent.putExtra("case_id", caseModel.getId());
        intent.putExtra("case_title", caseModel.getTitle());
        intent.putExtra("case_date", caseModel.getDate());
        intent.putExtra("case_description", caseModel.getDescription());
        intent.putExtra("case_image", caseModel.getImageUrl());
        startActivity(intent);
    }
} 