package kr.ac.kopo.todotimer2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

// AdapterViewTest 패턴: ArrayAdapter 커스텀 구현
public class TodoAdapter extends ArrayAdapter<TodoItem> {

    public TodoAdapter(Context context, ArrayList<TodoItem> items) {
        super(context, 0, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.item_todo, parent, false);
        }

        TodoItem item = getItem(position);
        if (item == null) return convertView;

        TextView tvTitle    = convertView.findViewById(R.id.tv_title);
        TextView tvDeadline = convertView.findViewById(R.id.tv_deadline);
        TextView tvBadge    = convertView.findViewById(R.id.tv_badge);
        TextView tvRating   = convertView.findViewById(R.id.tv_rating);
        TextView tvHistory  = convertView.findViewById(R.id.tv_history);

        tvTitle.setText(item.getTitle());

        // 마감일 표시 (날짜 미지정이면 숨김)
        String deadline = item.getDeadline();
        if (deadline != null && !deadline.equals("날짜 미지정")) {
            tvDeadline.setText("마감: " + deadline);
            tvDeadline.setVisibility(View.VISIBLE);
        } else {
            tvDeadline.setVisibility(View.GONE);
        }

        tvBadge.setText(item.getPriority());

        // 중요도 배지 색상
        String priority = item.getPriority();
        if ("상".equals(priority)) {
            tvBadge.setBackgroundResource(R.drawable.bg_badge_high);
            tvBadge.setTextColor(getContext().getColor(R.color.color_badge_high_text));
        } else if ("중".equals(priority)) {
            tvBadge.setBackgroundResource(R.drawable.bg_badge_mid);
            tvBadge.setTextColor(getContext().getColor(R.color.color_badge_mid_text));
        } else {
            tvBadge.setBackgroundResource(R.drawable.bg_badge_low);
            tvBadge.setTextColor(getContext().getColor(R.color.color_badge_low_text));
        }

        // 진행도 별 (마지막 저장된 별점, 0.5 단위 표시)
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

        // 수행 이력 누적 표시
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

        return convertView;
    }
}
