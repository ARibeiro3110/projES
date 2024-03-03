package pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain;

import jakarta.persistence.*;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.domain.AuthUser;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.domain.Participation;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.domain.Assessment;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.enrolment.domain.Enrolment;

import java.util.ArrayList;
import java.util.List;

@Entity
@DiscriminatorValue(User.UserTypes.VOLUNTEER)
public class Volunteer extends User {

    // Relation with Assessment
    @OneToMany(mappedBy = "volunteer", orphanRemoval = true)
    private List<Assessment> assessments = new ArrayList<>();

    @OneToMany(mappedBy = "volunteer", fetch = FetchType.EAGER, orphanRemoval = true)
    private List<Enrolment> enrolments = new ArrayList<>();


    @OneToMany(mappedBy = "volunteer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Participation> participations = new ArrayList<>();

    public Volunteer() {
    }

    public Volunteer(String name, String username, String email, AuthUser.Type type, State state) {
        super(name, username, email, Role.VOLUNTEER, type, state);
    }

    public List<Enrolment> getEnrolments() {
        return enrolments;
    }

    public void addEnrolment(Enrolment enrolment) {
        this.enrolments.add(enrolment);
    }

    public Volunteer(String name, State state) {
        super(name, Role.VOLUNTEER, state);
    }

    // Setters, Getters and Add for Asssessments

    public void setAssessments(List<Assessment> assessments) {
        this.assessments = assessments;
    }

    public List<Assessment> getAssessments() { return assessments; }

    public void addAssessment(Assessment assessment) { this.assessments.add(assessment); }

    public List<Participation> getParticipations() {
        return participations;
    }

    public void addParticipation(Participation participation) {
        participations.add(participation);
        participation.setVolunteer(this);
    }
}
