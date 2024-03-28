package pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.dto;

import pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.domain.Enrollment;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler;

public class EnrollmentDto {
    private Integer id;
    private String motivation;
    private String enrollmentDateTime;
    private Integer volunteerId;
    private String volunteerName;
    private boolean isParticipating;
    private Integer activityId;

    public EnrollmentDto() {}

    public EnrollmentDto(Enrollment enrollment) {
        setId(enrollment.getId());
        setMotivation(enrollment.getMotivation());
        setEnrollmentDateTime(DateHandler.toISOString(enrollment.getEnrollmentDateTime()));
        setActivityId(enrollment.getActivity().getId());
        setVolunteerId(enrollment.getVolunteer().getId());
        setVolunteerName(enrollment.getVolunteer().getName());
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getMotivation() {
        return motivation;
    }

    public void setMotivation(String motivation) {
        this.motivation = motivation;
    }

    public String getEnrollmentDateTime() {
        return enrollmentDateTime;
    }

    public void setEnrollmentDateTime(String enrollmentDateTime) {
        this.enrollmentDateTime = enrollmentDateTime;
    }

    public Integer getVolunteerId() {
        return volunteerId;
    }

    public void setVolunteerId(Integer volunteerId) {
        this.volunteerId = volunteerId;
    }

    public String getVolunteerName() {
        return volunteerName;
    }

    public void setVolunteerName(String volunteerName) {
        this.volunteerName = volunteerName;
    }

    public boolean isParticipating() {
        return isParticipating;
    }

    public void setParticipating(boolean isParticipating) {
        this.isParticipating = isParticipating;
    }

    public Integer getActivityId() {
        return activityId;
    }

    public void setActivityId(Integer activityId) {
        this.activityId = activityId;
    }
}
