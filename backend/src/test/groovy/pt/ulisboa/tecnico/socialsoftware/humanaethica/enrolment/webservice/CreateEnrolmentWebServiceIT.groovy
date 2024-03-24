package pt.ulisboa.tecnico.socialsoftware.humanaethica.enrolment.webservice

import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import org.springframework.http.HttpStatus
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.enrolment.dto.EnrolmentDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.dto.ActivityDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.dto.UserDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.domain.Theme
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.User
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.InstitutionService
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler

import java.time.LocalDateTime


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CreateEnrolmentWebServiceIT extends SpockTest {
    @LocalServerPort
    private int port

    def enrolmentDto
    def demo_volunteer

    def setup() {
        deleteAll()

        webClient = WebClient.create("http://localhost:" + port)
        headers = new HttpHeaders()
        headers.setContentType(MediaType.APPLICATION_JSON)

        def institution = institutionService.getDemoInstitution()

        def theme = new Theme(THEME_NAME_1, Theme.State.APPROVED, null)
        themeRepository.save(theme)

        def activity = new Activity()
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

        def volunteer = new Volunteer()
        volunteer.setName(USER_1_NAME)
        volunteer.setRole(User.Role.VOLUNTEER)
        volunteer.setState(User.State.ACTIVE)
        userRepository.save(volunteer)

        def activityDto = new ActivityDto(activity, false)
        def volunteerDto = new UserDto(volunteer)

        enrolmentDto = createEnrolmentDto(ENROLMENT_MOTIVATION_1, NOW, activityDto, volunteerDto)
    }

    def "login as volunteer, and create an enrolment"() {
        given:
        demoVolunteerLogin()

        when:
        def activityId = activityRepository.findAll().get(0).getId()
        def response = webClient.post()
                .uri("/enrolments/${activityId}")
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .bodyValue(enrolmentDto)
                .retrieve()
                .bodyToMono(EnrolmentDto.class)
                .block()

        then: "check response data"
        response.motivation == ENROLMENT_MOTIVATION_1
        withinFiveMinutes(response.enrolmentDateTime, DateHandler.toISOString(NOW))
        response.activity.getName() == ACTIVITY_NAME_1
        and: "check database data"
        enrolmentRepository.count() == 1
        def enrolment = enrolmentRepository.findAll().get(0)
        enrolment.getMotivation() == ENROLMENT_MOTIVATION_1
        withinFiveMinutes(DateHandler.toISOString(enrolment.getEnrolmentDateTime()), DateHandler.toISOString(NOW))
        enrolment.getActivity().getName() == ACTIVITY_NAME_1

        cleanup:
        deleteAll()
    }

    def "login as volunteer, and create an enrolment with error"() {
        given: "a volunteer"
        demoVolunteerLogin()
        and: "a short motivation"
        enrolmentDto.motivation = "  "

        when: "the volunteer creates an enrolment"
        def activityId = activityRepository.findAll().get(0).getId()
        def response = webClient.post()
                .uri("/enrolments/${activityId}")
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .bodyValue(enrolmentDto)
                .retrieve()
                .bodyToMono(EnrolmentDto.class)
                .block()

        then: "check response status"
        def error = thrown(WebClientResponseException)
        error.statusCode == HttpStatus.BAD_REQUEST
        enrolmentRepository.count() == 0

        cleanup:
        deleteAll()
    }

    def "login as member, and create an enrolment"() {
        given: "a member"
        demoMemberLogin()

        when: "the member creates an enrolment"
        def activityId = activityRepository.findAll().get(0).getId()
        webClient.post()
                .uri("/enrolments/${activityId}")
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .bodyValue(enrolmentDto)
                .retrieve()
                .bodyToMono(EnrolmentDto.class)
                .block()

        then: "an error is returned"
        def error = thrown(WebClientResponseException)
        error.statusCode == HttpStatus.FORBIDDEN
        enrolmentRepository.count() == 0

        cleanup:
        deleteAll()
    }

    def "login as admin, and create an enrolment"() {
        given: "a demo"
        demoAdminLogin()

        when: "the admin creates an enrolment"
        def activityId = activityRepository.findAll().get(0).getId()
        webClient.post()
                .uri("/enrolments/${activityId}")
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .bodyValue(enrolmentDto)
                .retrieve()
                .bodyToMono(EnrolmentDto.class)
                .block()

        then: "an error is returned"
        def error = thrown(WebClientResponseException)
        error.statusCode == HttpStatus.FORBIDDEN
        enrolmentRepository.count() == 0

        cleanup:
        deleteAll()
    }
}