package br.ufpe.tasktrack.DTO;

import java.time.LocalDate;

public class CourseResponseDTO {
    private Integer idCourse;
    private String name;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer creatorId;
    private String creatorEmail;

    public CourseResponseDTO(
            Integer idCourse,
            String name,
            String description,
            LocalDate startDate,
            LocalDate endDate,
            Integer creatorId,
            String creatorEmail
    ) {
        this.idCourse = idCourse;
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.creatorId = creatorId;
        this.creatorEmail = creatorEmail;
    }

    public Integer getIdCourse() { return idCourse; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
    public Integer getCreatorId() { return creatorId; }
    public String getCreatorEmail() { return creatorEmail; }
}