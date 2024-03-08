package pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.webservice

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import org.springframework.http.HttpStatus
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.dto.ActivityDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.domain.Institution
import pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.domain.Participation
import pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.dto.ParticipationDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.domain.Theme
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.dto.ThemeDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.User
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GetActivityParticipationsServiceTest extends SpockTest{
    @LocalServerPort
    private int port

    def participationDto
    def activityId

    def setup() {
        deleteAll()

        webClient = WebClient.create("http://localhost:" + port)
        headers = new HttpHeaders()
        headers.setContentType(MediaType.APPLICATION_JSON)

        def user = demoMemberLogin()

        def theme = createTheme(THEME_NAME_1, Theme.State.APPROVED,null)
        def themesDto = new ArrayList<>()
        themesDto.add(new ThemeDto(theme,false,false,false))

        def activityDto = createActivityDto(ACTIVITY_NAME_1, ACTIVITY_REGION_1, 2, ACTIVITY_DESCRIPTION_1,
                ONE_DAY_AGO, IN_TWO_DAYS, IN_THREE_DAYS, themesDto)

        activityDto = activityService.registerActivity(user.id, activityDto)

        activityId = activityDto.id

        def activity = activityRepository.findById(activityId).orElse(null)

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

        given: "participation info"
        def participationDto = createParticipationDto(PARTICIPATION_RATING, NOW, null, null)
        and: "a participation"
        def participation = new Participation(activity, volunteer_1, participationDto)
        participationRepository.save(participation)
        and: 'another participation'
        participation = new Participation(activity, volunteer_2, participationDto)
        participationRepository.save(participation)
    }


    def "login as member of the institution and get activity participations"() {
        given: 'a member of the institution'
        demoMemberLogin()

        when: 'the member requests the list of participations for the activity'
        def response = webClient.get()
                .uri("/participations/${activityId}")
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .retrieve()
                .bodyToFlux(ParticipationDto.class)
                .collectList()
                .block()

        then: "check response"
        response.size() == 2
        response.get(0).rating == PARTICIPATION_RATING
        response.get(0).activity.id == activityId
        response.get(0).volunteer.name == USER_1_NAME

        response.get(1).rating == PARTICIPATION_RATING
        response.get(1).activity.id == activityId
        response.get(1).volunteer.name == USER_2_NAME

        cleanup:
        deleteAll()
    }

    def "login as member of another institution and cannot get participations"() {
        given:
        def otherInstitution = new Institution(INSTITUTION_1_NAME, INSTITUTION_1_EMAIL, INSTITUTION_1_NIF)
        institutionRepository.save(otherInstitution)
        def otherMember = createMember(USER_1_NAME,USER_1_USERNAME,USER_1_PASSWORD,USER_1_EMAIL, AuthUser.Type.NORMAL,
                otherInstitution, User.State.APPROVED)
        normalUserLogin(USER_1_USERNAME, USER_1_PASSWORD)

        when:
        webClient.get()
                .uri("/participations/${activityId}")
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .retrieve()
                .bodyToFlux(ParticipationDto.class)
                .collectList()
                .block()

        then: "check response status"
        def error = thrown(WebClientResponseException)
        error.statusCode == HttpStatus.FORBIDDEN
        participationRepository.count() == 2

        cleanup:
        deleteAll()
    }

    def "login as admin and cannot get participations"() {
        given: 'an admin'
        demoAdminLogin()

        when:
        webClient.get()
                .uri("/participations/${activityId}")
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .retrieve()
                .bodyToFlux(ParticipationDto.class)
                .collectList()
                .block()

        then: "check response status"
        def error = thrown(WebClientResponseException)
        error.statusCode == HttpStatus.FORBIDDEN
        participationRepository.count() == 2

        cleanup:
        deleteAll()
    }

    def "login as volunteer and cannot get participations"() {
        given: 'a volunteer'
        demoVolunteerLogin()

        when:
        webClient.get()
                .uri("/participations/${activityId}")
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .retrieve()
                .bodyToFlux(ParticipationDto.class)
                .collectList()
                .block()

        then: "check response status"
        def error = thrown(WebClientResponseException)
        error.statusCode == HttpStatus.FORBIDDEN
        participationRepository.count() == 2

        cleanup:
        deleteAll()
    }
}
