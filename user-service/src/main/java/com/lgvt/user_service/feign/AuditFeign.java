package com.lgvt.user_service.feign;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.lgvt.user_service.dto.AuditDto;

@FeignClient(name = "AUDIT-LOG-SERVICE")
public interface AuditFeign {
    @GetMapping("/api/audits")
    public ResponseEntity<List<AuditDto>> getAllAudits();

    @PostMapping("/api/audits")
    public ResponseEntity<AuditDto> createAudit(@RequestBody AuditDto audit);
}