package pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity
import pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.dto.ParticipationDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.User
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.dto.UserDto
import spock.lang.Unroll

@DataJpaTest
class CreateParticipationServiceTest extends SpockTest{
    public static final String EXIST = "exist"
    public static final String NO_EXIST = "noExist"

    def activity
    def volunteer

    def setup() {
        volunteer = new Volunteer()
        volunteer.setName(USER_1_NAME)
        volunteer.setRole(User.Role.VOLUNTEER)
        volunteer.setState(User.State.ACTIVE)
        userRepository.save(volunteer)

        activity = new Activity()
        activity.setName(ACTIVITY_NAME_1)
        activity.setRegion(ACTIVITY_REGION_1)
        activity.setParticipantsNumberLimit(1)
        activity.setDescription(ACTIVITY_DESCRIPTION_1)
        activity.setStartingDate(IN_TWO_DAYS)
        activity.setEndingDate(IN_THREE_DAYS)
        activity.setApplicationDeadline(ONE_DAY_AGO)
        activity.setState(Activity.State.APPROVED)
        activityRepository.save(activity)
    }

    def "create participation"() {
        given: "a participation dto"
        def participationDto = new ParticipationDto()
        participationDto.setRating(PARTICIPATION_RATING)
        participationDto.setVolunteer(new UserDto(volunteer))

        when:
        def result = participationService.createParticipation(activity.getId(), participationDto)

        then: "the returned data is correct"
        result.rating == PARTICIPATION_RATING
        and: "the participation is saved in the database"
        participationRepository.findAll().size() == 1
        and: "the stored data is correct"
        def storedParticipation = participationRepository.findById(result.id).get()
        storedParticipation.rating == PARTICIPATION_RATING
    }

    @Unroll
    def 'invalid arguments: activityId=#activityId | volunteerId=#volunteerId'() {
        given: "a participation dto"
        def participationDto = new ParticipationDto()
        participationDto.setRating(PARTICIPATION_RATING)
        println("before")
        participationDto.setVolunteer(getVolunteerDto(volunteerId))
        println("after")
        when:
        participationService.createParticipation(getActivityId(activityId), participationDto)
        println("way after")
        then:
        def error = thrown(HEException)
        error.getErrorMessage() == errorMessage
        and: "no participation is stored in the database"
        participationRepository.findAll().size() == 0

        where:
        activityId  | volunteerId || errorMessage
        null        | EXIST       || ErrorMessage.ACTIVITY_NOT_FOUND
        NO_EXIST    | EXIST       || ErrorMessage.ACTIVITY_NOT_FOUND
        EXIST       | null        || ErrorMessage.VOLUNTEER_NOT_FOUND
        EXIST       | NO_EXIST    || ErrorMessage.VOLUNTEER_NOT_FOUND
    }

    def getActivityId(activityId) {
        if (activityId == EXIST)
            return activity.id
        else if (activityId == NO_EXIST)
            return 222
        return null
    }

    def getVolunteerDto(volunteerId) {
        if (volunteerId == EXIST)
            return new UserDto(volunteer)
        else if (volunteerId == NO_EXIST) {
            def UserDto = new UserDto()
            UserDto.id = 222
            return UserDto
        }
        else
            return new UserDto()
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}



