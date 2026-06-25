# 오늘의 목표 (TodoTimer2)

할 일을 등록하고, 타이머로 수행하며, 결과를 기록하는 Android 목표 관리 앱

---

## 앱 소개

공부·운동 등 반복적으로 수행해야 하는 목표를 단순히 나열하는 것에 그치지 않고,
타이머로 실제 수행 시간을 측정하고 완료 후 진행도와 메모를 누적 기록할 수 있는 앱입니다.

---

## 개발 환경

| 항목 | 내용 |
|---|---|
| Language | Java |
| Min SDK | API 24 (Android 7.0) |
| Target SDK | API 34 |
| Build System | Gradle (Kotlin DSL) |
| IDE | Android Studio |

---

## 화면 구성

| Activity | 설명 |
|---|---|
| SplashActivity | 앱 시작 화면 (2.5초 후 자동 전환) |
| MainActivity | 목표 등록 / 목록 관리 / 완료 처리 |
| TimerActivity | 타이머 수행 (시작 / 중지 / 완료) |
| ResultActivity | 수행 결과 입력 (별점 · 메모 · 저장) |

---

## 주요 기능

- 목표 등록 — 제목, 중요도(상/중/하), 마감일 설정
- 타이머 수행 — Chronometer 기반 경과 시간 측정, 일시정지 후 재개 시 시간 유지
- 진행도 별점 — ImageView 10개 커스텀 구현, 0.5 단위 선택 가능
- 수행 이력 누적 — 완료할 때마다 "경과시간 메모" 형식으로 이력 추가
- 목표 완료 처리 — 완료 버튼으로 등록된 목표 → 완료된 목표 섹션으로 이동
- 목표 삭제 — 롱클릭 → AlertDialog 확인 후 삭제
- 마감일 선택 — DatePickerDialog

---

## Activity 간 데이터 흐름

```
MainActivity
  └─ startActivityForResult → TimerActivity
                                └─ startActivityForResult → ResultActivity
                                                              └─ setResult(RESULT_OK)
                                └─ onActivityResult → setResult(RESULT_OK)
  └─ onActivityResult → 목록 갱신
```

- TodoItem implements Serializable 로 객체 직렬화 전달
- 3단계 체인으로 수행 결과를 MainActivity까지 역방향 반환

---

## 개발자

- 이재용 — 인공지능 소프트웨어학과 2학년
