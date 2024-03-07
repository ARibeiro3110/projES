package pt.ulisboa.tecnico.socialsoftware.humanaethica.enrolment.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.dto.ActivityDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.User
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.dto.UserDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.domain.Theme
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler
import spock.lang.Unroll

import java.time.LocalDateTime
import java.time.Duration

@DataJpaTest
class CreateEnrolmentServiceTest extends SpockTest {
    public static final String EXIST = "exist"
    public static final String NO_EXIST = "noExist"

    def volunteer
    def activity
    def institution
    def theme

    def setup() {
        institution = institutionService.getDemoInstitution()

        theme = new Theme(THEME_NAME_1, Theme.State.APPROVED,null)
        themeRepository.save(theme)

        activity = new Activity()
        activity.setName(ACTIVITY_NAME_1)
        activity.setRegion(ACTIVITY_REGION_1)
        activity.setParticipantsNumberLimit(1)
        activity.setDescription(ACTIVITY_DESCRIPTION_1)
        activity.setStartingDate(IN_TWO_DAYS)
        activity.setEndingDate(IN_THREE_DAYS)
        activity.setApplicationDeadline(IN_ONE_DAY)
        activity.setInstitution(institution)
        activity.addTheme(theme)
        activity.setState(Activity.State.APPROVED)
        activityRepository.save(activity)

        volunteer = new Volunteer()
        volunteer.setName(USER_1_NAME)
        volunteer.setRole(User.Role.VOLUNTEER)
        volunteer.setState(User.State.ACTIVE)
        userRepository.save(volunteer)
    }

    def "create enrolment"() {
        given: "an enrolment dto"
        def activityDto = new ActivityDto(activity, false)
        def volunteerDto = new UserDto(volunteer)

        def enrolmentDto = createEnrolmentDto(ENROLMENT_MOTIVATION_1, NOW, activityDto, volunteerDto)

        when:
        def result = enrolmentService.createEnrolment(volunteer.getId(), activity.getId(), enrolmentDto)

        then: "the returned data is correct"
        result.motivation == ENROLMENT_MOTIVATION_1
        withinFiveMinutes(result.enrolmentDateTime, DateHandler.toISOString(NOW))
        result.activity.id == activity.getId()
        result.volunteer.id == volunteer.getId()
        and: "the enrolment is saved in the database"
        enrolmentRepository.findAll().size() == 1
        and: "the stored data is correct"
        def storedEnrolment = enrolmentRepository.findById(result.id).get()
        storedEnrolment.motivation == ENROLMENT_MOTIVATION_1
        withinFiveMinutes(result.enrolmentDateTime, DateHandler.toISOString(NOW))
        storedEnrolment.activity.id == activity.getId()
        storedEnrolment.volunteer.id == volunteer.getId()
    }

    @Unroll
    def "invalid arguments: motivation=#motivation | activityId=#activityId | volunteerId=#volunteerId"() {
        given: "an enrolment dto"
        def activityDto = getActivityDto(activityId)
        def volunteerDto = getVolunteerDto(volunteerId)

        def enrolmentDto = createEnrolmentDto(motivation, NOW, activityDto, volunteerDto)

        when:
        def result = enrolmentService.createEnrolment(getVolunteerId(volunteerId), getActivityId(activityId), enrolmentDto)

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == errorMessage
        and: "no enrolment is stored in the database"
        enrolmentRepository.findAll().size() == 0

        where:
        motivation             | activityId | volunteerId || errorMessage
        null                   | EXIST      | EXIST       || ErrorMessage.ENROLMENT_MOTIVATION_INVALID
        ENROLMENT_MOTIVATION_1 | NO_EXIST   | EXIST       || ErrorMessage.ACTIVITY_NOT_FOUND
        ENROLMENT_MOTIVATION_1 | null       | EXIST       || ErrorMessage.ACTIVITY_NOT_FOUND
        ENROLMENT_MOTIVATION_1 | EXIST      | NO_EXIST    || ErrorMessage.USER_NOT_FOUND
        ENROLMENT_MOTIVATION_1 | EXIST      | null        || ErrorMessage.USER_NOT_FOUND
    }

        // withinFiveMinutes: takes two datetime strings and returns true if the difference between them is less than 5 minutes
        def withinFiveMinutes(String datetime1, String datetime2) {
            def date1 = DateHandler.toLocalDateTime(datetime1)
            def date2 = DateHandler.toLocalDateTime(datetime2)

            def duration = Duration.between(date1, date2)

            def minutesDifference = duration.toMinutes()
            minutesDifference < 5
        }

        // getActivityDto: takes an activityId and returns the corresponding ActivityDto
        def getActivityDto(activityId) {
            if (activityId == EXIST)
                return new ActivityDto(activity, false)
            else if (activityId == NO_EXIST) {
                def activityDto = new ActivityDto()
                activityDto.id = 222
                return activityDto
            }
            else
                return new ActivityDto()
        }

        // getVolunteerDto: takes a volunteerId and returns the corresponding UserDto
        def getVolunteerDto(volunteerId) {
            if (volunteerId == EXIST)
                return new UserDto(volunteer)
            else if (volunteerId == NO_EXIST) {
                def volunteerDto = new UserDto()
                volunteerDto.id = 222
                return volunteerDto
            }
            else
                return new UserDto()
        }

        def getVolunteerId(volunteerId){
            if (volunteerId == EXIST)
                return volunteer.id
            else if (volunteerId == NO_EXIST)
                return 222
            return null
        }

        def getActivityId(activityId){
            if (activityId == EXIST)
                return activity.id
            else if (activityId == NO_EXIST)
                return 222
            return null
        }


    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
