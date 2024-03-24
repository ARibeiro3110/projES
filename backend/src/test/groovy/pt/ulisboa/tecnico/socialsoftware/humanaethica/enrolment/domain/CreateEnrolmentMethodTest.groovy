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
        otherEnrolment.getVolunteer() >> volunteer
        otherEnrolment.getLocalDateTime() >> NOW
        activity.getEnrolments() >> [otherEnrolment]
        activity.getApplicationDeadline() >> IN_ONE_DAY
        volunteer.getId() >> USER_1_ID
        volunteer.getEnrolments() >> []

        when: "create enrolment"
        def result = new Enrolment(activity, volunteer, enrolmentDto)

        then: "enrolment created"
        result.getMotivation() == ENROLMENT_MOTIVATION_1
        result.getActivity() == activity
        result.getVolunteer() == volunteer
        and: "invocations"
        1 * activity.addEnrolment(_)
        1 * volunteer.addEnrolment(_)
    }

    @Unroll
    def "create enrolment and violate motivation has at least 10 characters : motivation=#motivation"() {
        given: "enrolment context"
        otherEnrolment.getMotivation() >> ENROLMENT_MOTIVATION_5
        otherEnrolment.getVolunteer() >> volunteer
        otherEnrolment.getLocalDateTime() >> NOW
        activity.getEnrolments() >> [otherEnrolment]
        activity.getApplicationDeadline() >> IN_ONE_DAY
        volunteer.getId() >> USER_1_ID
        volunteer.getEnrolments() >> []
        and: "an enrolment dto"
        enrolmentDto = new EnrolmentDto()
        enrolmentDto.motivation = motivation

        when: "create enrolment"
        new Enrolment(activity, volunteer, enrolmentDto)

        then: "exception thrown"
        def error = thrown(HEException)
        error.getErrorMessage() == ErrorMessage.MOTIVATION_HAS_LESS_THAN_TEN_CHARACTERS

        where:
        motivation << [ENROLMENT_MOTIVATION_2, ENROLMENT_MOTIVATION_4]

    }

    def "create enrolment and violate only one enrolment per volunteer per activity"() {
        given: "enrolment context"
        def activity_1 = new Activity()
        activity_1.setParticipantsNumberLimit(2)
        activity_1.addEnrolment(otherEnrolment)
        activity_1.setApplicationDeadline(IN_ONE_DAY)
        otherEnrolment.getMotivation() >> ENROLMENT_MOTIVATION_5
        otherEnrolment.getVolunteer() >> volunteer
        otherEnrolment.getLocalDateTime() >> NOW
        volunteer.getId() >> USER_1_ID
        and: "an enrolment dto"
        enrolmentDto = new EnrolmentDto()
        enrolmentDto.motivation = ENROLMENT_MOTIVATION_1

        when: "create enrolment"
        new Enrolment(activity_1, volunteer, enrolmentDto)

        then: "exception thrown"
        def error = thrown(HEException)
        error.getErrorMessage() == ErrorMessage.VOLUNTEER_ALREADY_ENROLLED_IN_ACTIVITY
    }

    @Unroll
    def "create enrolment and violate volunteer cannot enrol after deadline : deadline=#deadline "() {
        given: "enrolment context"
        activity.getEnrolments() >> []
        activity.getApplicationDeadline() >> deadline
        volunteer.getId() >> USER_1_ID
        and: "an enrolment dto"
        enrolmentDto = new EnrolmentDto()
        enrolmentDto.motivation = ENROLMENT_MOTIVATION_1
        enrolmentDto.setEnrolmentDateTime(DateHandler.toISOString(NOW))

        when: "create enrolment"
        new Enrolment(activity, volunteer, enrolmentDto)

        then: "exception thrown"
        def error = thrown(HEException)
        error.getErrorMessage() == ErrorMessage.ENROLMENT_LATE_FOR_DEADLINE

        where:
        deadline << [TWO_DAYS_AGO, ONE_DAY_AGO]

    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
