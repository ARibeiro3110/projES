package pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.domain.Theme
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.dto.ThemeDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler
import spock.lang.Unroll

@DataJpaTest
class CreateAssessmentServiceTest extends SpockTest {
    public static final String EXIST = "exist"
    public static final String NO_EXIST = "noExist"

    def institution
    def volunteer
    def theme

    def setup() {
        volunteer = authUserService.loginDemoVolunteerAuth().getUser()
        institution = institutionService.getDemoInstitution()
        institution.setAssessments([])

    }

    def "create assessment"() {
        given: "an assessment dto"
        def assessmentDto = createAssessmentDto(ASSESSMENT_REVIEW_1, DateHandler.toISOString(ONE_DAY_AGO))
        def finishedActivity = new  Activity()
        finishedActivity.setEndingDate(ONE_DAY_AGO)

        institution.addActivity(finishedActivity)
        institutionRepository.save(institution)

        when:
        def result = assessmentService.createAssessment(volunteer.getId(), institution.getId(), assessmentDto)

        then: "the returned data is correct"
        result.review == ASSESSMENT_REVIEW_1
        result.reviewDate == DateHandler.toISOString(ONE_DAY_AGO)
        result.institution.id == institution.id
        result.volunteer.id == volunteer.id
        and: "the activity is saved in the database"
        assessmentRepository.findAll().size() == 1
        and: "the stored data is correct"
        def storedAssessment = assessmentRepository.findById(result.id).get()
        storedAssessment.review == ASSESSMENT_REVIEW_1
        storedAssessment.reviewDate == ONE_DAY_AGO
        storedAssessment.institution.id == institution.id
        storedAssessment.volunteer.id == volunteer.id

    }


    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}