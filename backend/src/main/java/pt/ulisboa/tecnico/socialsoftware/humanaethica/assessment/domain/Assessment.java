package pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.domain;

import jakarta.persistence.*;

import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.domain.Institution;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer;

import java.time.LocalDateTime;

@Entity
@Table(name = "assessment")
public class Assessment {
    // state?
    // ...
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String review;
    private LocalDateTime reviewDate;


    // Relations with an Institution and a Volunteer

    @ManyToOne
    @JoinColumn(name = "institution_id")
    private Institution institution;

    @ManyToOne
    @JoinColumn(name = "volunteer_id")
    private Volunteer volunteer;

    public Assessment(){
        //empty constructor
    }

    // public Assessment(Institution institution, Volunteer volunteer, AssessmentDto assessmentDto) {
        //TO DO

    //public void update(AssessmentDto assessmentDto, Institution institution,  Volunteer volunteer)
        //TO DO


    // Getters and Setters for Assessment Class

    public String getReview() { return this.review; }

    public void setReview(String review) {
        this.review = review;
    }

    public LocalDateTime getReviewDate() {
        return reviewDate;
    }

    public void setReviewDate(LocalDateTime reviewDate) {
        this.reviewDate = reviewDate;
    }

    public Institution getInstitution() {
        return institution;
    }

    public void setInstitution(Institution institution) {
        this.institution = institution;
        institution.addAssessments(this);
    }

    public Volunteer getVolunteer() {
        return volunteer;
    }

    public void setVolunteer(Volunteer volunteer) {
        this.volunteer = volunteer;
        volunteer.addAssessments(this);
    }

    //implement invariants and conditions
}
