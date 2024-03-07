package pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.domain.Assessment
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.User
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.domain.Institution
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.domain.Theme


@DataJpaTest
class GetAssessmentsByInstitutionsServiceTest extends SpockTest {

    def institution

    def setup() {
        institution = institutionService.getDemoInstitution()

        def theme = new Theme(THEME_NAME_1, Theme.State.APPROVED,null)
        themeRepository.save(theme)

        def activity = new Activity()
        activity.setName(ACTIVITY_NAME_1)
        activity.setRegion(ACTIVITY_REGION_1)
        activity.setParticipantsNumberLimit(3)
        activity.setDescription(ACTIVITY_DESCRIPTION_1)
        activity.setStartingDate(ONE_DAY_AGO)
        activity.setEndingDate(IN_ONE_DAY)
        activity.setApplicationDeadline(IN_TWO_DAYS)
        activity.setInstitution(institution)
        activity.addTheme(theme)
        activity.setState(Activity.State.APPROVED)
        activityRepository.save(activity)

        institution.addActivity(activity)
        institutionRepository.save(institution)

        def volunteer_1 = new Volunteer()
        volunteer_1.setName(USER_1_NAME)
        volunteer_1.setRole(User.Role.VOLUNTEER)
        volunteer_1.setState(User.State.ACTIVE)
        userRepository.save(volunteer_1)

        def volunteer_2 = new Volunteer()
        volunteer_2.setName(USER_2_NAME)
        volunteer_2.setRole(User.Role.VOLUNTEER)
        volunteer_2.setState(User.State.ACTIVE)
        userRepository.save(volunteer_2)

        given: "assessment info"
        def assessmentDto = createAssessmentDto(ASSESSMENT_REVIEW_1, DateHandler.toISOString(IN_TWO_DAYS))
        and: "an assessment"
        def assessment = new Assessment(institution, volunteer_1, assessmentDto)
        assessmentRepository.save(assessment)
        and: 'another assessment'
        assessmentDto.review = ASSESSMENT_REVIEW_2
        assessment = new Assessment(institution, volunteer_2, assessmentDto)
        assessmentRepository.save(assessment)
    }

    def 'get two assessments'() {
        when:
        def result = assessmentService.getAssessmentsByInstitution(institution.getId())

        then:
        result.size() == 2
        result.get(0).review == ASSESSMENT_REVIEW_1
        result.get(1).review == ASSESSMENT_REVIEW_2
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}