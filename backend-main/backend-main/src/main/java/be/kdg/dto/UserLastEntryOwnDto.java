package be.kdg.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserLastEntryOwnDto {
    private Long userId;
    private Long lockId;
    private String method;
    private boolean success;
    private LocalDateTime timestamp;



}