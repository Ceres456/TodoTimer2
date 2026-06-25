package kr.ac.kopo.todotimer2;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;

// Reservation 패턴:        Chronometer 시작/중지
// DialogTest 패턴:         완료 확인 AlertDialog
// MultiDirectionData 패턴: 결과를 ResultActivity로 전달 후 MainActivity에 반환
public class TimerActivity extends AppCompatActivity {

    static final int REQUEST_RESULT = 200;

    Chronometer chronometer;
    Button btnBack, btnStart, btnStop, btnComplete;
    TextView tvGoalTitle, tvBadge;

    TodoItem todoItem;
    int todoPosition;
    boolean isRunning = false;
    long elapsedMs = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), true);
        setContentView(R.layout.activity_timer);

        btnBack      = findViewById(R.id.btn_back);
        chronometer  = findViewById(R.id.chronometer);
        btnStart     = findViewById(R.id.btn_start);
        btnStop      = findViewById(R.id.btn_stop);
        btnComplete  = findViewById(R.id.btn_complete);
        tvGoalTitle  = findViewById(R.id.tv_goal_title);
        tvBadge      = findViewById(R.id.tv_badge);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // ActivityTest 패턴: Intent에서 데이터 수신
        todoItem     = (TodoItem) getIntent().getSerializableExtra("todo");
        todoPosition = getIntent().getIntExtra("position", 0);

        if (todoItem != null) {
            tvGoalTitle.setText(todoItem.getTitle());
            tvBadge.setText(todoItem.getPriority());
            applyBadgeStyle(tvBadge, todoItem.getPriority());
        }

        // Reservation 패턴: Chronometer 시작
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isRunning) {
                    chronometer.setBase(SystemClock.elapsedRealtime() - elapsedMs);
                    chronometer.start();
                    isRunning = true;
                }
            }
        });

        // Reservation 패턴: Chronometer 일시정지
        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isRunning) {
                    elapsedMs = SystemClock.elapsedRealtime() - chronometer.getBase();
                    chronometer.stop();
                    isRunning = false;
                }
            }
        });

        // DialogTest 패턴: 완료 확인 AlertDialog
        btnComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isRunning) {
                    elapsedMs = SystemClock.elapsedRealtime() - chronometer.getBase();
                    chronometer.stop();
                    isRunning = false;
                }

                AlertDialog.Builder dialog = new AlertDialog.Builder(TimerActivity.this);
                dialog.setTitle("수행 완료");
                dialog.setMessage("수행을 완료하셨습니까?");
                dialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface d, int which) {
                        int elapsedSeconds = (int) (elapsedMs / 1000);
                        // ActivityTest 패턴: ResultActivity로 데이터 전달
                        Intent intent = new Intent(TimerActivity.this, ResultActivity.class);
                        intent.putExtra("todo", todoItem);
                        intent.putExtra("position", todoPosition);
                        intent.putExtra("elapsedSeconds", elapsedSeconds);
                        startActivityForResult(intent, REQUEST_RESULT);
                    }
                });
                dialog.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface d, int which) {
                        // 타이머 재개
                        chronometer.setBase(SystemClock.elapsedRealtime() - elapsedMs);
                        chronometer.start();
                        isRunning = true;
                    }
                });
                dialog.show();
            }
        });
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

    // MultiDirectionData 패턴: ResultActivity 결과 수신 → MainActivity로 전달
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_RESULT && resultCode == RESULT_OK && data != null) {
            setResult(RESULT_OK, data);
            finish();
        }
    }
}
