package pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.domain.Theme
import pt.ulisboa.tecnico.socialsoftware.humanaethica.enrolment.dto.EnrolmentDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.enrolment.domain.Enrolment
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.User
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler
import spock.lang.Unroll

@DataJpaTest
class GetEnrolmentsServiceTest extends SpockTest {

    def activity

    def setup() {
        def institution = institutionService.getDemoInstitution()

        def theme = new Theme(THEME_NAME_1, Theme.State.APPROVED,null)
        themeRepository.save(theme)

        activity = new Activity()
        activity.setName(ACTIVITY_NAME_1)
        activity.setRegion(ACTIVITY_REGION_1)
        activity.setParticipantsNumberLimit(2)
        activity.setDescription(ACTIVITY_DESCRIPTION_1)
        activity.setStartingDate(IN_TWO_DAYS)
        activity.setEndingDate(IN_THREE_DAYS)
        activity.setApplicationDeadline(IN_ONE_DAY)
        activity.setInstitution(institution)
        activity.addTheme(theme)
        activity.setState(Activity.State.APPROVED)
        activityRepository.save(activity)

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
        
        given: "enrolment info"
        def enrolmentDto = createEnrolmentDto(ENROLMENT_MOTIVATION_1, NOW, null, null)
        and: "an enrolment"
        def enrolment = new Enrolment(activity, volunteer_1, enrolmentDto)
        enrolmentRepository.save(enrolment)
        and: 'another enrolment'
        enrolment = new Enrolment(activity, volunteer_2, enrolmentDto)
        enrolmentRepository.save(enrolment)
    }

    def 'get two enrolments'() {
        when:
        def result = enrolmentService.getEnrolmentsByActivity(activity.getId())

        then:
        result.size() == 2
        result.get(0).volunteer.name == USER_1_NAME
        result.get(1).volunteer.name == USER_2_NAME
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
