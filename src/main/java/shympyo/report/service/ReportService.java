package shympyo.report.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shympyo.map.domain.PlaceType;
import shympyo.rental.repository.RentalRepository;
import shympyo.report.domain.*;
import shympyo.report.dto.ProviderCreateReportRequest;
import shympyo.report.repository.ReportRepository;
import shympyo.user.domain.User;
import shympyo.user.domain.UserRole;
import shympyo.user.repository.UserRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ReportService {

    private static final int REPORT_THRESHOLD = 3;       // 임계값

    private final UserRepository userRepository;
    private final ReportRepository reportRepository;
    private final RentalRepository rentalRepository;
    private final SanctionService sanctionService;

    @Transactional
    public void report(Long reporterId, ProviderCreateReportRequest request) {

        User reporter = userRepository.findById(reporterId)
                .orElseThrow(() -> new IllegalArgumentException("제공자를 찾을 수 없습니다."));
        if (reporter.getRole() != UserRole.PROVIDER) {
            throw new IllegalArgumentException("장소 제공자만 신고할 수 있습니다.");
        }

        User reported = userRepository.findById(request.getReportedUserId())
                .orElseThrow(() -> new IllegalArgumentException("신고 대상 사용자를 찾을 수 없습니다."));

        if (reported.getId().equals(reporter.getId())) {
            throw new IllegalArgumentException("자기 자신은 신고할 수 없습니다.");
        }

        if (request.getRentalId() != null) {
            boolean validRental = rentalRepository.existsByIdAndPlaceOwnerId(request.getRentalId(), reporter.getId());
            if (!validRental) {
                throw new IllegalArgumentException("해당 대여 건은 이 제공자의 장소에서 발생한 기록이 아닙니다.");
            }
        }

        if (request.getRentalId() != null &&
                reportRepository.existsByRentalIdAndReportedUserId(request.getRentalId(), request.getReportedUserId())) {
            throw new IllegalArgumentException("이미 해당 대여 건에서 이 사용자를 신고했습니다.");
        }

        reportRepository.save(
                Report.builder()
                        .reporter(reporter)
                        .reportedUser(reported)
                        .rentalId(request.getRentalId())
                        .reason(request.getReason())
                        .content(request.getContent())
                        .build()
        );



        long pendingCount = reportRepository.countByReportedUserIdAndStatus(
                request.getReportedUserId(), ReportStatus.PENDING);

        if (pendingCount >= REPORT_THRESHOLD) {


            Long sanctionId = sanctionService.issueBlockContentByCategory(
                    reported.getId(),
                    PlaceType.USER_SHELTER,
                    SanctionReason.POLICY_VIOLATION,
                    "제공자 쉼터만 미노출",
                    LocalDateTime.now(),
                    LocalDateTime.now().plusDays(7),
                    SanctionSource.MANUAL
            );

            String note = "\n[AUTO] sanctionId=" + sanctionId + "로 자동 종결";
            reportRepository.resolveAllPendingByUser(
                    reported.getId(),
                    ReportStatus.PENDING,
                    ReportStatus.RESOLVED,
                    ReportAction.BLOCK_CONTENT, // 제재 타입에 맞게
                    note,
                    LocalDateTime.now());
        }


    }

    public getReport(){

    }



}
