package com.organicmart.service;

import com.organicmart.entity.CertificateStatus;
import java.util.Map;

public interface AdminService {
    Map<String, Object> getDashboardStats();
    void reviewCertificate(Long certificateId, CertificateStatus status, String rejectionReason, Long reviewerId);
    void approveDeliveryPartner(Long userId, boolean approve);
}
