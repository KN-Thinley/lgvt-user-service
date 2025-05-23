package com.lgvt.user_service.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuditDto {
    private Long id;
    private LocalDateTime timestamp;
    private String user_email;
    private String action;     // Enum as string
    private String description;
    private String ipAddress;
    private String status;     // Enum as string
}
