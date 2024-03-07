package pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity
import pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.repository.AssessmentRepository
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.domain.Theme
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.dto.ThemeDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler
import spock.lang.Unroll

@DataJpaTest
class CreateAssessmentServiceTest extends SpockTest {
    public static final String EXIST = "exist"
    public static final String NO_EXIST = "noExist"

    def institution
    def volunteer
    def finishedActivity;

    def setup() {
        volunteer = authUserService.loginDemoVolunteerAuth().getUser()
        institution = institutionService.getDemoInstitution()
        institution.setAssessments([])

        finishedActivity = new  Activity()
        finishedActivity.setEndingDate(ONE_DAY_AGO)
        finishedActivity.setName(ACTIVITY_NAME_1)
        activityRepository.save(finishedActivity)

        institution.addActivity(finishedActivity)
        institutionRepository.save(institution)
    }

    def "create assessment"() {
        given: "an assessment dto"
        def assessmentDto = createAssessmentDto(ASSESSMENT_REVIEW_1, DateHandler.toISOString(NOW))
        

        when:
        def result = assessmentService.createAssessment(volunteer.getId(), institution.getId(), assessmentDto)

        then: "the returned data is correct"
        result.review == ASSESSMENT_REVIEW_1
        result.reviewDate == DateHandler.toISOString(NOW)
        result.institution.id == institution.id
        result.volunteer.id == volunteer.id
        and: "the activity is saved in the database"
        assessmentRepository.findAll().size() == 1
        and: "the stored data is correct"
        def storedAssessment = assessmentRepository.findById(result.id).get()
        storedAssessment.review == ASSESSMENT_REVIEW_1
        storedAssessment.reviewDate == NOW
        storedAssessment.institution.id == institution.id
        storedAssessment.volunteer.id == volunteer.id

    }

    @Unroll
    def 'invalid arguments: review=#review | volunteerId=#volunteerId | institutionId=#institutionId'() {
        given: "an assessment dto"

        def assessmentDto = createAssessmentDto(review, DateHandler.toISOString(IN_ONE_DAY))

        when:
        def result = assessmentService.createAssessment(getVolunteerId(volunteerId), getInstitutionId(institutionId), assessmentDto)
        
        then:
        def error = thrown(HEException)
        error.getErrorMessage() == errorMessage
        and: "no assessment is stored in the database"
        assessmentRepository.findAll().size() == 0

        where:
        review              | volunteerId | institutionId  || errorMessage
        null                | EXIST       | EXIST          || ErrorMessage.ASSESSMENT_REVIEW_TOO_SHORT
        ASSESSMENT_REVIEW_2 | null        | EXIST          || ErrorMessage.USER_NOT_FOUND
        ASSESSMENT_REVIEW_2 | NO_EXIST    | EXIST          || ErrorMessage.USER_NOT_FOUND
        ASSESSMENT_REVIEW_2 | EXIST       | null           || ErrorMessage.INSTITUTION_NOT_FOUND
        ASSESSMENT_REVIEW_2 | EXIST       | NO_EXIST       || ErrorMessage.INSTITUTION_NOT_FOUND
    }

    def getVolunteerId(volunteerId){
        if (volunteerId == EXIST)
            return volunteer.id
        else if (volunteerId == NO_EXIST)
            return 222
        return null
    }

    def getInstitutionId(institutionId){
        if (institutionId == EXIST)
            return institution.id
        else if (institutionId == NO_EXIST)
            return 223
        return null
    }


    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}