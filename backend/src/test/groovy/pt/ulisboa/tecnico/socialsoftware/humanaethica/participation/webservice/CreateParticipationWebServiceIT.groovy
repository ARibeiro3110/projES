package pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.webservice

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import org.springframework.http.HttpStatus
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.domain.Participation
import pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.dto.ParticipationDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.dto.ActivityDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.dto.UserDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.domain.Theme
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.dto.ThemeDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.User
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.InstitutionService
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CreateParticipationWebServiceIT extends SpockTest {
    @LocalServerPort
    private int port

    def participationDto
    def activityId

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
        activity.setParticipantsNumberLimit(5)
        activity.setDescription(ACTIVITY_DESCRIPTION_1)
        activity.setStartingDate(IN_TWO_DAYS)
        activity.setEndingDate(IN_THREE_DAYS)
        activity.setApplicationDeadline(ONE_DAY_AGO)
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

        participationDto = createParticipationDto(PARTICIPATION_RATING, NOW, activityDto, volunteerDto)
    }

    /* def "login as member, and create participation"() {
        given:
        demoMemberLogin()

        when:
        activityId = activityRepository.findAll().get(0).getId()
        def response = webClient.post()
                .uri("/participations/${activityId}")
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .bodyValue(participationDto)
                .retrieve()
                .bodyToMono(ParticipationDto.class)
                .block()

        then: "check response data"
        response.rating == PARTICIPATION_RATING
        withinFiveMinutes(response.getAcceptanceDate(), DateHandler.toISOString(NOW))
        response.getActivity().getName() == ACTIVITY_NAME_1
        and: "check database data"
        participationRepository.count() == 1
        def participation = participationRepository.findAll().get(0)
        participation.getRating() == PARTICIPATION_RATING
        withinFiveMinutes(DateHandler.toISOString(participation.getAcceptanceDate()), DateHandler.toISOString(NOW))
        participation.getActivity().getName() == ACTIVITY_NAME_1

        cleanup:
        deleteAll()
    } */

    def "login as member, and create participation with error"() {
        given: "a member"
        demoMemberLogin()
        and: "an invalid acceptance date"
        participationDto.setAcceptanceDate(DateHandler.toISOString(TWO_DAYS_AGO))

        when: "the member tries to create a participation"
        def response = webClient.post()
                .uri("/participations/${activityId}")
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .bodyValue(participationDto)
                .retrieve()
                .bodyToMono(ParticipationDto.class)
                .block()

        then: "check response status"
        def error = thrown(WebClientResponseException)
        error.getStatusCode() == HttpStatus.BAD_REQUEST
        participationRepository.count() == 0

        cleanup:
        deleteAll()
    }

    def "login as volunteer, and create participation"() {
        given: "a volunteer"
        demoVolunteerLogin()

        when: "the volunteer tries to create a participation"
        def activityId = activityRepository.findAll().get(0).getId()
        def response = webClient.post()
            .uri("/participations/${activityId}")
            .headers(httpHeaders -> httpHeaders.putAll(headers))
            .bodyValue(participationDto)
            .retrieve()
            .bodyToMono(ParticipationDto.class)
            .block()

        then: "check response status"
        def error = thrown(WebClientResponseException)
        error.getStatusCode() == HttpStatus.FORBIDDEN
        participationRepository.count() == 0

        cleanup:
        deleteAll()
    }

    def "login as admin, and create participation"() {
        given: "an admin"
        demoAdminLogin()

        when: "the admin tries to create a participation"
        def activityId = activityRepository.findAll().get(0).getId()
        webClient.post()
            .uri("/participations/${activityId}")
            .headers(httpHeaders -> httpHeaders.putAll(headers))
            .bodyValue(participationDto)
            .retrieve()
            .bodyToMono(ParticipationDto.class)
            .block()

        then: "check response status"
        def error = thrown(WebClientResponseException)
        error.getStatusCode() == HttpStatus.FORBIDDEN
        participationRepository.count() == 0

        cleanup:
        deleteAll()
    }
}
