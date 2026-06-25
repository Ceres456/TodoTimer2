package kr.ac.kopo.todotimer2;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

// AdapterViewTest 패턴: ScrollView + 동적 뷰 인플레이션으로 두 섹션(진행중/완료) 표시
// ActivityTest 패턴:   Intent로 데이터 전달 및 결과 수신
// DialogTest 패턴:     AlertDialog 삭제 확인
public class MainActivity extends Activity {

    static final int REQUEST_TIMER = 100;

    ArrayList<TodoItem> todoList = new ArrayList<>();
    ArrayList<TodoItem> completedList = new ArrayList<>();

    EditText editTodo;
    RadioGroup rgPriority;
    RadioButton rbHigh, rbMid, rbLow;
    TextView textDeadline;
    LinearLayout layoutDeadline;
    Button btnRegister;
    LinearLayout llActiveList, llCompletedList, llCompletedSection;

    String selectedDeadline = "날짜 미지정";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getWindow().getDecorView().setImportantForAutofill(View.IMPORTANT_FOR_AUTOFILL_NO_EXCLUDE_DESCENDANTS);
        }
        setContentView(R.layout.activity_main);

        editTodo           = findViewById(R.id.edit_todo);
        rgPriority         = findViewById(R.id.rg_priority);
        rbHigh             = findViewById(R.id.rb_high);
        rbMid              = findViewById(R.id.rb_mid);
        rbLow              = findViewById(R.id.rb_low);
        textDeadline       = findViewById(R.id.text_deadline);
        layoutDeadline     = findViewById(R.id.layout_deadline);
        btnRegister        = findViewById(R.id.btn_register);
        llActiveList       = findViewById(R.id.ll_active_list);
        llCompletedList    = findViewById(R.id.ll_completed_list);
        llCompletedSection = findViewById(R.id.ll_completed_section);

        // Reservation 패턴: DatePickerDialog로 날짜 선택
        layoutDeadline.setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();
            new DatePickerDialog(MainActivity.this,
                (view, year, month, dayOfMonth) -> {
                    selectedDeadline = year + "년 " + (month + 1) + "월 " + dayOfMonth + "일";
                    textDeadline.setText(selectedDeadline);
                    textDeadline.setTextColor(getColor(R.color.color_text_primary));
                },
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show();
        });

        // AdapterViewTest 패턴: 항목 추가
        btnRegister.setOnClickListener(v -> {
            String title = editTodo.getText().toString().trim();
            if (title.isEmpty()) {
                editTodo.setError("할 일을 입력하세요");
                editTodo.requestFocus();
                return;
            }

            String priority = "상";
            int checkedId = rgPriority.getCheckedRadioButtonId();
            if (checkedId == R.id.rb_mid) priority = "중";
            else if (checkedId == R.id.rb_low) priority = "하";

            todoList.add(new TodoItem(title, priority, selectedDeadline));
            refreshActiveList();

            editTodo.setText("");
            rbHigh.setChecked(true);
            selectedDeadline = "날짜 미지정";
            textDeadline.setText("날짜를 선택하세요");
            textDeadline.setTextColor(getColor(R.color.color_text_secondary));

            Toast.makeText(getApplicationContext(), "목표가 등록되었습니다", Toast.LENGTH_SHORT).show();
        });
    }

    // 진행 중 목표 목록 갱신
    private void refreshActiveList() {
        llActiveList.removeAllViews();
        for (int i = 0; i < todoList.size(); i++) {
            final TodoItem item = todoList.get(i);

            // 카드 + 버튼을 가로로 묶는 wrapper
            LinearLayout wrapper = new LinearLayout(this);
            wrapper.setOrientation(LinearLayout.HORIZONTAL);
            wrapper.setGravity(Gravity.CENTER_VERTICAL);

            // 카드 (weight=1로 남은 공간 차지, 높이는 wrapper에 맞춤)
            View card = getLayoutInflater().inflate(R.layout.item_todo, wrapper, false);
            LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.MATCH_PARENT, 1f);
            card.setLayoutParams(cardParams);
            bindTodoView(card, item);

            // ActivityTest 패턴: 카드 클릭 → TimerActivity로 데이터 전달
            card.setOnClickListener(click -> {
                Intent intent = new Intent(getApplicationContext(), TimerActivity.class);
                intent.putExtra("todo", item);
                intent.putExtra("position", todoList.indexOf(item));
                startActivityForResult(intent, REQUEST_TIMER);
            });

            // DialogTest 패턴: 롱클릭 → 삭제 확인 다이얼로그
            card.setOnLongClickListener(lv -> {
                new AlertDialog.Builder(MainActivity.this)
                    .setTitle("목표 삭제")
                    .setMessage("이 목표를 삭제하시겠습니까?")
                    .setPositiveButton("확인", (d, w) -> {
                        todoList.remove(item);
                        refreshActiveList();
                    })
                    .setNegativeButton("취소", null)
                    .show();
                return true;
            });

            // 목표 완료 버튼 (카드 오른쪽 바깥)
            Button btnComplete = new Button(this);
            btnComplete.setText("목표\n완료!");
            btnComplete.setTextSize(11f);
            btnComplete.setTextColor(getColor(R.color.white));
            btnComplete.setBackgroundResource(R.drawable.bg_button_primary);
            btnComplete.setPadding(16, 16, 16, 16);
            LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
            btnParams.setMargins(10, 0, 0, 0);
            btnComplete.setLayoutParams(btnParams);
            btnComplete.setOnClickListener(btn -> {
                todoList.remove(item);
                completedList.add(item);
                refreshActiveList();
                refreshCompletedList();
            });

            wrapper.addView(card);
            wrapper.addView(btnComplete);

            LinearLayout.LayoutParams wrapperParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
            wrapperParams.setMargins(0, 0, 0, 12);
            wrapper.setLayoutParams(wrapperParams);

            llActiveList.addView(wrapper);
        }
    }

    // 완료된 목표 목록 갱신
    private void refreshCompletedList() {
        llCompletedList.removeAllViews();
        llCompletedSection.setVisibility(completedList.isEmpty() ? View.GONE : View.VISIBLE);
        for (TodoItem item : completedList) {
            View v = getLayoutInflater().inflate(R.layout.item_todo, llCompletedList, false);
            bindTodoView(v, item);
            setItemMargin(v);
            llCompletedList.addView(v);
        }
    }

    private void bindTodoView(View v, TodoItem item) {
        TextView tvTitle    = v.findViewById(R.id.tv_title);
        TextView tvDeadline = v.findViewById(R.id.tv_deadline);
        TextView tvBadge    = v.findViewById(R.id.tv_badge);
        TextView tvRating   = v.findViewById(R.id.tv_rating);
        TextView tvHistory  = v.findViewById(R.id.tv_history);

        tvTitle.setText(item.getTitle());

        String deadline = item.getDeadline();
        if (deadline != null && !deadline.equals("날짜 미지정")) {
            tvDeadline.setText("마감: " + deadline);
            tvDeadline.setVisibility(View.VISIBLE);
        } else {
            tvDeadline.setVisibility(View.GONE);
        }

        tvBadge.setText(item.getPriority());
        applyBadgeStyle(tvBadge, item.getPriority());

        float progress = item.getProgress();
        if (progress > 0) {
            String ratingStr = (progress == (int) progress)
                ? String.valueOf((int) progress)
                : String.valueOf(progress);
            tvRating.setText("진행도 ★ " + ratingStr + "/10");
            tvRating.setVisibility(View.VISIBLE);
        } else {
            tvRating.setVisibility(View.GONE);
        }

        ArrayList<String> history = item.getHistoryLog();
        if (!history.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < history.size(); i++) {
                if (i > 0) sb.append("\n");
                sb.append((i + 1)).append(". ").append(history.get(i));
            }
            tvHistory.setText(sb.toString());
            tvHistory.setVisibility(View.VISIBLE);
        } else {
            tvHistory.setVisibility(View.GONE);
        }
    }

    private void setItemMargin(View v) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 0, 12);
        v.setLayoutParams(params);
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

    // MultiDirectionData 패턴: 결과 화면에서 데이터 수신 → 목록 갱신
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_TIMER && resultCode == RESULT_OK && data != null) {
            TodoItem updatedItem = (TodoItem) data.getSerializableExtra("todo");
            int position = data.getIntExtra("position", -1);
            if (updatedItem != null && position >= 0 && position < todoList.size()) {
                todoList.set(position, updatedItem);
                refreshActiveList();
            }
        }
    }
}
