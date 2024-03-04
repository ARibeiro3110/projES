package pt.ulisboa.tecnico.socialsoftware.humanaethica.enrolment.domain;

import jakarta.persistence.*;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.enrolment.dto.EnrolmentDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage.*;

@Entity
@Table(name = "enrolment")
public class Enrolment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String motivation;
    private LocalDateTime enrolmentDateTime;

    @ManyToOne
    private Activity activity;

    @ManyToOne
    private Volunteer volunteer;

    public Enrolment() {
    }

    public Enrolment(Activity activity, Volunteer volunteer, EnrolmentDto enrolmentDto) {
        setActivity(activity);
        setVolunteer(volunteer);
        setMotivation(enrolmentDto.getMotivation());
        setEnrolmentDateTime(DateHandler.now());

        verifyInvariants();
    }

    public Integer getId() {
        return id;
    }

    public String getMotivation() {
        return motivation;
    }

    public void setMotivation(String motivation) {
        this.motivation = motivation;
    }

    public LocalDateTime getEnrolmentDateTime() {
        return enrolmentDateTime;
    }

    public void setEnrolmentDateTime(LocalDateTime enrolmentDateTime) {
        this.enrolmentDateTime = enrolmentDateTime;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
        activity.addEnrolment(this);
    }

    public Volunteer getVolunteer() {
        return volunteer;
    }

    public void setVolunteer(Volunteer volunteer) {
        this.volunteer = volunteer;
        volunteer.addEnrolment(this);
    }

    public void verifyInvariants() {
        motivationHasAtLeastTenCharacters();
        onlyOneEnrolmentPerActivityPerVolunteer(); // TODO: Implement invariant
        cannotEnrolAfterDeadline(); // TODO: Implement invariant
    }

    private void motivationHasAtLeastTenCharacters() {
        if (motivation.length() < 10) {
            throw new HEException(MOTIVATION_HAS_LESS_THAN_TEN_CHARACTERS);
        }
    }

    private void onlyOneEnrolmentPerActivityPerVolunteer() {
        // TODO: Implement invariant
    }

    private void cannotEnrolAfterDeadline() {
        // TODO: Implement invariant
    }


}