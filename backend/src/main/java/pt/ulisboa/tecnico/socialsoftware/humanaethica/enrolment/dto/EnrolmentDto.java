package pt.ulisboa.tecnico.socialsoftware.humanaethica.enrolment.dto;

import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.dto.ActivityDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.enrolment.domain.Enrolment;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.dto.UserDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler;

public class EnrolmentDto {
    private Integer id;
    private String motivation;
    private String enrolmentDateTime;
    private ActivityDto activity;
    private UserDto volunteer;


    public EnrolmentDto() {
    }

    public EnrolmentDto(Enrolment enrolment, boolean deepCopyActivity, boolean deepCopyVolunteer) {
        setId(enrolment.getId());
        setMotivation(enrolment.getMotivation());
        setEnrolmentDateTime(DateHandler.toISOString(enrolment.getEnrolmentDateTime()));

        if (deepCopyActivity && (enrolment.getActivity() != null)) {
            setActivity(new ActivityDto(enrolment.getActivity(), false));
        }

        if (deepCopyVolunteer && (enrolment.getVolunteer() != null)) {
            setVolunteer(new UserDto(enrolment.getVolunteer()));
        }

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

    public String getEnrolmentDateTime() {
        return enrolmentDateTime;
    }

    public void setEnrolmentDateTime(String enrolmentDateTime) {
        this.enrolmentDateTime = enrolmentDateTime;
    }

    public ActivityDto getActivity() {
        return activity;
    }

    public void setActivity(ActivityDto activity) {
        this.activity = activity;
    }

    public UserDto getVolunteer() {
        return volunteer;
    }

    public void setVolunteer(UserDto volunteer) {
        this.volunteer = volunteer;
    }
}



