package kr.ac.kopo.todotimer2;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import java.util.Locale;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;

// ActivityTest 패턴:    Intent에서 데이터 수신
// MultiDirectionData:   수정된 데이터를 MainActivity로 반환 (setResult)
// UserDialogTest 패턴:  EditText로 메모 입력 후 저장
public class ResultActivity extends AppCompatActivity {

    TodoItem todoItem;
    int todoPosition;
    int elapsedSeconds;
    float currentRating = 7f;

    Button btnBack;
    TextView tvGoalTitle, tvBadge, tvElapsedTime;
    EditText editMemo;
    Button btnSave;

    final int[] starIds = {
        R.id.star_1, R.id.star_2, R.id.star_3, R.id.star_4, R.id.star_5,
        R.id.star_6, R.id.star_7, R.id.star_8, R.id.star_9, R.id.star_10
    };
    ImageView[] stars = new ImageView[10];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), true);
        setContentView(R.layout.activity_result);

        btnBack       = findViewById(R.id.btn_back);
        tvGoalTitle   = findViewById(R.id.tv_goal_title);
        tvBadge       = findViewById(R.id.tv_badge);
        tvElapsedTime = findViewById(R.id.tv_elapsed_time);
        editMemo      = findViewById(R.id.edit_memo);
        btnSave       = findViewById(R.id.btn_save);

        btnBack.setOnClickListener(v -> finish());

        // ActivityTest 패턴: Intent에서 데이터 수신
        todoItem      = (TodoItem) getIntent().getSerializableExtra("todo");
        todoPosition  = getIntent().getIntExtra("position", 0);
        elapsedSeconds = getIntent().getIntExtra("elapsedSeconds", 0);

        if (todoItem != null) {
            tvGoalTitle.setText(todoItem.getTitle());
            tvBadge.setText(todoItem.getPriority());
            applyBadgeStyle(tvBadge, todoItem.getPriority());
            if (todoItem.getProgress() > 0) {
                currentRating = todoItem.getProgress();
            }
            if (!todoItem.getMemo().isEmpty()) {
                editMemo.setText(todoItem.getMemo());
            }
        }

        int minutes = elapsedSeconds / 60;
        int seconds = elapsedSeconds % 60;
        tvElapsedTime.setText(minutes + "분 " + seconds + "초");

        // 별 10개 초기화 — 터치 위치로 0.5 단위 선택
        for (int i = 0; i < 10; i++) {
            stars[i] = findViewById(starIds[i]);
            final float halfValue = i + 0.5f;
            final float fullValue = i + 1f;
            stars[i].setOnTouchListener((v, event) -> {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    // 별의 왼쪽 절반 터치 → 0.5점, 오른쪽 절반 → 1점
                    if (event.getX() < v.getWidth() / 2f) {
                        currentRating = halfValue;
                    } else {
                        currentRating = fullValue;
                    }
                    updateStars();
                    v.performClick();
                }
                return true;
            });
        }
        updateStars();

        // MultiDirectionData 패턴: 결과 저장 후 setResult로 MainActivity에 반환
        btnSave.setOnClickListener(v -> {
            if (todoItem != null) {
                todoItem.setProgress(currentRating);
                todoItem.setElapsedSeconds(elapsedSeconds);

                // 수행 이력 추가: "MM:SS(경과시간) 수행내용"
                int mins = elapsedSeconds / 60;
                int secs = elapsedSeconds % 60;
                String elapsed = String.format(Locale.getDefault(), "%02d:%02d", mins, secs);
                String memo = editMemo.getText().toString().trim();
                String entry = elapsed + " " + (memo.isEmpty() ? "-" : memo);
                todoItem.addHistory(entry);
                todoItem.setMemo(memo);
            }
            Intent intent = new Intent();
            intent.putExtra("todo", todoItem);
            intent.putExtra("position", todoPosition);
            setResult(RESULT_OK, intent);
            finish();
        });
    }

    private void updateStars() {
        for (int i = 0; i < 10; i++) {
            float threshold = i + 1f;
            float halfThreshold = i + 0.5f;
            if (currentRating >= threshold) {
                stars[i].setImageResource(R.drawable.ic_star_filled);
            } else if (currentRating >= halfThreshold) {
                stars[i].setImageResource(R.drawable.ic_star_half);
            } else {
                stars[i].setImageResource(R.drawable.ic_star_empty);
            }
        }
    }

    private void applyBadgeStyle(TextView badge, String priority) {
        if ("상".equals(priority)) {
            badge.setBackgroundResource(R.drawable.bg_badge_high);
            badge.setTextColor(getColor(R.color.color_badge_high_text));
        } else if ("중".equals(priority)) {
            badge.setBackgroundResource(R.drawable.bg_badge_mid);
            badge.setTextColor(getColor(R.color.color_badge_mid_text));
        } else {
            badge.setBackgroundResource(R.drawable.bg_badge_low);
            badge.setTextColor(getColor(R.color.color_badge_low_text));
        }
    }
}
