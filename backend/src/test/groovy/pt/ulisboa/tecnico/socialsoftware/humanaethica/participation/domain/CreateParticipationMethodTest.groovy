package pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.domain

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException
import pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.dto.ParticipationDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler
import spock.lang.Unroll

@DataJpaTest
class CreateParticipationMethodTest extends SpockTest{
    Activity activity = Mock()
    Volunteer volunteer = Mock()
    Participation otherParticipation = Mock()
    Volunteer otherVolunteer = Mock()
    def participationDto

    def setup() {
        given: "participation info"
        participationDto = new ParticipationDto()
        participationDto.rating = PARTICIPATION_RATING
    }

    def "create participation with volunteer and activity has another participation"() {
        given:
        activity.getParticipations() >> [otherParticipation]
        activity.getApplicationDeadline() >> ONE_DAY_AGO
        activity.getParticipantsNumberLimit() >> PARTICIPANTS_NUMBER_LIMIT
        volunteer.getParticipations() >> []
        volunteer.getId() >> VOLUNTEER_ID_1
        otherParticipation.getVolunteer() >> otherVolunteer
        otherParticipation.getVolunteer().getId() >> VOLUNTEER_ID_2


        when:
        def result = new Participation(activity, volunteer, participationDto)

        then: "check result"
        result.getActivity() == activity
        result.getVolunteer() == volunteer
        result.getRating() == PARTICIPATION_RATING
        and: "invocations"
        1 * activity.addParticipation(_)
        1 * volunteer.addParticipation(_)

    }

    @Unroll
    def "create participation and violate invariant total participations <= maxParticipations"() {
        given:
        activity.getParticipantsNumberLimit() >> 1
        activity.getParticipations() >> [otherParticipation]
        activity.getApplicationDeadline() >> ONE_DAY_AGO
        volunteer.getId() >> VOLUNTEER_ID_1
        otherParticipation.getVolunteer() >> otherVolunteer
        otherParticipation.getVolunteer().getId() >> VOLUNTEER_ID_2

        when:
        new Participation(activity, volunteer, participationDto)

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == ErrorMessage.ACTIVITY_PARTICIPANTS_LIMIT_EXCEEDED

    }

    @Unroll
    def "create participation and violate invariant volunteer can only participate once in an activity"() {
        given:
        activity.getParticipations() >> [otherParticipation]
        activity.getParticipantsNumberLimit() >> PARTICIPANTS_NUMBER_LIMIT
        activity.getApplicationDeadline() >> ONE_DAY_AGO
        volunteer.getId() >> VOLUNTEER_ID_1
        otherParticipation.getVolunteer() >> volunteer

        when:
        new Participation(activity, volunteer, participationDto)

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == ErrorMessage.VOLUNTEER_ALREADY_PARTICIPATING_IN_ACTIVITY

    }

    @Unroll
    def "create participation and violate invariant acceptanceDate after applicationDeadline"() {
        given:
        activity.getParticipations() >> [otherParticipation]
        activity.getParticipantsNumberLimit() >> PARTICIPANTS_NUMBER_LIMIT
        activity.getApplicationDeadline() >> IN_ONE_DAY
        volunteer.getId() >> VOLUNTEER_ID_1
        otherParticipation.getVolunteer() >> otherVolunteer
        otherParticipation.getVolunteer().getId() >> VOLUNTEER_ID_2

        when:
        new Participation(activity, volunteer, participationDto)

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == ErrorMessage.VOLUNTEER_ADDED_BEFORE_APPLICATION_DEADLINE

    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}

}
