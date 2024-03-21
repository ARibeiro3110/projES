package pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.dto;

import pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.domain.Enrollment;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.dto.UserDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler;

public class EnrollmentDto {
    private Integer id;
    private String motivation;
    private String enrollmentDateTime;
    private UserDto volunteer;
    private boolean isParticipating;
    private Integer activityId;
    private Integer volunteerId;

    public EnrollmentDto() {}

    public EnrollmentDto(Enrollment enrollment) {
        setId(enrollment.getId());
        setMotivation(enrollment.getMotivation());
        setEnrollmentDateTime(DateHandler.toISOString(enrollment.getEnrollmentDateTime()));
        setActivityId(enrollment.getActivity().getId());
        setVolunteerId(enrollment.getVolunteer().getId());
        this.volunteer = new UserDto(enrollment.getVolunteer());
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

    public UserDto getVolunteer() {
        return volunteer;
    }

    public void setVolunteer(UserDto volunteer) {
        this.volunteer = volunteer;
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

    public Integer getVolunteerId() {
        return volunteerId;
    }

    public void setVolunteerId(Integer volunteerId) {
        this.volunteerId = volunteerId;
    }
}
