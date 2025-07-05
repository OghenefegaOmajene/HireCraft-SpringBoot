//package HireCraft.com.SpringBoot.models;
//
//import jakarta.persistence.*;
//import lombok.Builder;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//
//@Entity
//@Table(name = "tasks")
//@Data
//@NoArgsConstructor
//
//@Builder
//public class Task {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @Column(nullable = false)
//    private String title;
//
//    @Column(nullable = false)
//    private LocalDate reminderDate;
//
//    @Column(nullable = false)
//    private LocalDate reminderTime;
//
//    @Column(nullable = false)
//    private LocalDateTime updatedAt;
//}
