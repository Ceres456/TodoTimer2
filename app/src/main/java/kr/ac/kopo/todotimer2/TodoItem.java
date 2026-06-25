package kr.ac.kopo.todotimer2;

import java.io.Serializable;
import java.util.ArrayList;

public class TodoItem implements Serializable {
    private String title;
    private String priority;
    private String deadline;
    private float progress;
    private String memo;
    private int elapsedSeconds;
    private ArrayList<String> historyLog = new ArrayList<>();

    public TodoItem(String title, String priority, String deadline) {
        this.title = title;
        this.priority = priority;
        this.deadline = deadline;
        this.progress = 0;
        this.memo = "";
        this.elapsedSeconds = 0;
    }

    public String getTitle() { return title; }
    public String getPriority() { return priority; }
    public String getDeadline() { return deadline; }
    public float getProgress() { return progress; }
    public String getMemo() { return memo; }
    public int getElapsedSeconds() { return elapsedSeconds; }
    public ArrayList<String> getHistoryLog() { return historyLog; }

    public void setProgress(float progress) { this.progress = progress; }
    public void setMemo(String memo) { this.memo = memo; }
    public void setElapsedSeconds(int elapsedSeconds) { this.elapsedSeconds = elapsedSeconds; }

    public void addHistory(String entry) {
        historyLog.add(entry);
    }
}
