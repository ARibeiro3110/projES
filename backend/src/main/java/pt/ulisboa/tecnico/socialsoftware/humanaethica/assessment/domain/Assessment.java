package pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.domain;

import jakarta.persistence.*;

import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.domain.Institution;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.dto.AssessmentDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler;

import static pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "assessment")
public class Assessment {

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

    public Assessment(Institution institution, Volunteer volunteer, AssessmentDto assessmentDto) {
        setInstitution(institution);
        setVolunteer(volunteer);
        setReview(assessmentDto.getReview());
        setReviewDate(DateHandler.toLocalDateTime(assessmentDto.getReviewDate()));

        verifyInvariants();
    }


    // Getters and Setters for Assessment Class

    public Integer getId() { return this.id; }

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
        institution.addAssessment(this);
    }

    public Volunteer getVolunteer() {
        return volunteer;
    }

    public void setVolunteer(Volunteer volunteer) {
        this.volunteer = volunteer;
        volunteer.addAssessment(this);
    }

    // Implement invariants and conditions
    private void verifyInvariants(){
        reviewSizeMinimumTen();
        volunteerHasNotAssessedInstitution();
        institutionHasFinishedActivity();
    }

    // Review has at least 10 chars
    private void reviewSizeMinimumTen(){

        if (this.review == null || this.review.isEmpty()) {
            throw new HEException(ASSESSMENT_REVIEW_TOO_SHORT, 0);
        }
        if (this.review.length() <10) {
            throw new HEException(ASSESSMENT_REVIEW_TOO_SHORT,this.review.length());
        }
    }

    // Volunteer cannot assess the same institution twice
    private void volunteerHasNotAssessedInstitution() {
        if (this.volunteer.getAssessments().stream().anyMatch(
                assessment -> assessment != this && assessment.
                        getInstitution().getId().equals(this.institution.getId()))) {
            throw new HEException(ASSESSMENT_ALREADY_MADE_BY_VOLUNTEER);
        }
    }

    // Institutions without finished activities cannot be assessed
    private void institutionHasFinishedActivity(){
        if (this.institution.getActivities() == null || this.institution.getActivities().isEmpty() ||
                this.institution.getActivities().stream().noneMatch(activity -> activity.getEndingDate().isBefore(this.reviewDate))) {
            throw new HEException(ASSESSMENT_TO_UNFINISHED_ACTIVITIES_INSTITUTION);
        }
    }
}
