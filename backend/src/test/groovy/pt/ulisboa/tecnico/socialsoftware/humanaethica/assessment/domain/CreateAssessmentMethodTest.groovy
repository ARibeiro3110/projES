package pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.domain

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity
import pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.domain.Assessment
import pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.dto.AssessmentDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.domain.Institution
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler
import spock.lang.Unroll

import java.time.LocalDateTime

@DataJpaTest
class CreateAssessmentMethodTest extends SpockTest {
    Institution institution = Mock()
    Volunteer volunteer = Mock()
    Activity finishedActivity = Mock()
    Assessment otherAssessment = Mock()

    def assessmentDto

    def setup() {
        given: "assessment info"
        assessmentDto = new AssessmentDto()
        assessmentDto.review = ASSESSMENT_REVIEW_1
        assessmentDto.reviewDate = DateHandler.toISOString(ONE_DAY_AGO)

    }

    def "create assessment with volunteer and institution has another assessment"() {
        given:
        finishedActivity.getEndingDate() >> ONE_DAY_AGO
        finishedActivity.getName() >> ACTIVITY_NAME_1
        otherAssessment.getReview() >> ASSESSMENT_REVIEW_2
        institution.getActivities() >> [finishedActivity]
        institution.getAssessments() >> [otherAssessment]
        volunteer.getAssessments() >> []


        when: "create assessment"
        def result = new Assessment(institution, volunteer, assessmentDto)

        then: "check result"
        result.getInstitution() == institution
        result.getReview() == ASSESSMENT_REVIEW_1
        result.getReviewDate() == ONE_DAY_AGO
        result.getVolunteer() == volunteer
        result.getVolunteer().getAssessments().get(0) == result
        result.getVolunteer().getAssessments().size() == 1
        result.getInstitution().getAssessments().size() == 2
        and: "invocations"
        1 * institution.addAssessment(_)
        1 * volunteer.addAssessment(_)
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}