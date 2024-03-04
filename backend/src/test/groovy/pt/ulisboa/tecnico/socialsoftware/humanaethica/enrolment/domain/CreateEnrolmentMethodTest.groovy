package pt.ulisboa.tecnico.socialsoftware.humanaethica.enrolment.domain

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.enrolment.dto.EnrolmentDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler
import spock.lang.Unroll

import java.time.LocalDateTime

@DataJpaTest
class CreateEnrolmentMethodTest extends SpockTest {
    Activity activity = Mock()
    Volunteer volunteer = Mock()
    Enrolment otherEnrolment = Mock()
    def enrolmentDto

    def setup() {
        given: "enrolment info"
        enrolmentDto = new EnrolmentDto()
        enrolmentDto.motivation = ENROLMENT_MOTIVATION_1
    }

    def "create enrolment"() {
        given: "enrolment context"
        otherEnrolment.getMotivation() >> ENROLMENT_MOTIVATION_5
        activity.getEnrolments() >> [otherEnrolment]
        volunteer.getEnrolments() >> []

        when: "create enrolment"
        def result = new Enrolment(activity, volunteer, enrolmentDto)

        then: "enrolment created"
        result.getMotivation() == ENROLMENT_MOTIVATION_1
        result.getActivity() == activity
        result.getVolunteer() == volunteer
        and: "invocations"
        1 * activity.addEnrolment(result)
        1 * volunteer.addEnrolment(result)
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
