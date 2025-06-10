package HireCraft.com.SpringBoot.dtos.response;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClientBookingViewResponse {
    private Long id;
    private String providerFullName;
    private String occupation;

    private String city;
    private String state;
    private String country;
    private String timeAgo;
    private String status;
}
