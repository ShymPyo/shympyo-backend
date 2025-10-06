package shympyo.report.domain;

public enum ReportAction {
    NONE,           // 조치 없음
    HIDE_PLACE,     // 장소 숨김(지도 미노출)
    SUSPEND_USER,   // 사용자 이용 정지
    BLOCK_CONTENT   // 콘텐츠 차단(글/댓글/편지 등)
}
