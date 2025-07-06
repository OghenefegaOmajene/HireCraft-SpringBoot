package HireCraft.com.SpringBoot.dtos.requests;

import lombok.Data;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EscrowReleaseRequest {
    private Long transactionId;
    private String releaseReason;
    private String releasedBy;
}
