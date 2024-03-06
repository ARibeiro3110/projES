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

import java.awt.HeadlessException
import java.time.LocalDateTime

import static pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage.ASSESSMENT_TO_UNFINISHED_ACTIVITIES_INSTITUTION

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
        finishedActivity.getEndingDate() >> TWO_DAYS_AGO
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
        and: "invocations"
        1 * institution.addAssessment(_)
        1 * volunteer.addAssessment(_)
    }

    def "create assessment and violate invariant review must have at least 10 characters"() {
        given:
        finishedActivity.getEndingDate() >> TWO_DAYS_AGO
        finishedActivity.getName() >> ACTIVITY_NAME_1
        institution.getActivities() >> [finishedActivity]
        and:
        assessmentDto.setReview(name)
        assessmentDto.setReviewDate(DateHandler.toISOString(ONE_DAY_AGO))

        when:
        def result = new Assessment(institution, volunteer, assessmentDto)

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == errorMessage

        where:
        name            || errorMessage
        null            || ErrorMessage.ASSESSMENT_REVIEW_TOO_SHORT
        " "             || ErrorMessage.ASSESSMENT_REVIEW_TOO_SHORT
        "123456789"     || ErrorMessage.ASSESSMENT_REVIEW_TOO_SHORT
        "o"             || ErrorMessage.ASSESSMENT_REVIEW_TOO_SHORT
        ""              || ErrorMessage.ASSESSMENT_REVIEW_TOO_SHORT
    }


    def "create assessment and violate invariant an institution can only be assessed when it has at least one concluded activity"(){
        given:
        finishedActivity.getEndingDate() >> IN_THREE_DAYS
        finishedActivity.getName() >> ACTIVITY_NAME_1
        otherAssessment.getReview() >> ASSESSMENT_REVIEW_2
        institution.getActivities() >> [finishedActivity]
        institution.getAssessments() >> [otherAssessment]
        volunteer.getAssessments() >> []
        and:
        assessmentDto.setReview(ASSESSMENT_REVIEW_1)
        assessmentDto.setReviewDate(date instanceof LocalDateTime ? DateHandler.toISOString(date) : date as String)

        when :
        def result = new Assessment(institution, volunteer, assessmentDto)

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == errorMessage

        where:
        date            || errorMessage
        TWO_DAYS_AGO    || ErrorMessage.ASSESSMENT_TO_UNFINISHED_ACTIVITIES_INSTITUTION
        ONE_DAY_AGO     || ErrorMessage.ASSESSMENT_TO_UNFINISHED_ACTIVITIES_INSTITUTION
        NOW             || ErrorMessage.ASSESSMENT_TO_UNFINISHED_ACTIVITIES_INSTITUTION
        IN_ONE_DAY      || ErrorMessage.ASSESSMENT_TO_UNFINISHED_ACTIVITIES_INSTITUTION
        IN_TWO_DAYS     || ErrorMessage.ASSESSMENT_TO_UNFINISHED_ACTIVITIES_INSTITUTION
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}