package pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.webservice

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
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.domain.Theme
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.dto.ThemeDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.User
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer
import pt.ulisboa.tecnico.socialsoftware.humanaethica.enrolment.dto.EnrolmentDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.enrolment.domain.Enrolment
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.dto.UserDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.InstitutionService
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.domain.Institution
import pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.domain.AuthUser

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GetEnrolmentsWebServiceIT extends SpockTest {
    @LocalServerPort
    private int port

    def activityId
    def enrolmentDto

    def setup() {
        deleteAll()

        webClient = WebClient.create("http://localhost:" + port)
        headers = new HttpHeaders()
        headers.setContentType(MediaType.APPLICATION_JSON)

        def user = demoMemberLogin()

        def institution = institutionService.getDemoInstitution()

        def theme = createTheme(THEME_NAME_1, Theme.State.APPROVED,null)
        def themesDto = new ArrayList<>()
        themesDto.add(new ThemeDto(theme,false,false,false))
        def themes = new ArrayList<>()
        themes.add(theme)

        def activityDto = createActivityDto(ACTIVITY_NAME_1,ACTIVITY_REGION_1,2,ACTIVITY_DESCRIPTION_1,
                IN_ONE_DAY,IN_TWO_DAYS,IN_THREE_DAYS,themesDto)


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
        
        given: "enrolment info"
        enrolmentDto = createEnrolmentDto(ENROLMENT_MOTIVATION_1, NOW, null, null)
        and: "an enrolment"
        def enrolment = new Enrolment(activity, volunteer_1, enrolmentDto)
        enrolmentRepository.save(enrolment)
        and: 'another enrolment'
        enrolment = new Enrolment(activity, volunteer_2, enrolmentDto)
        enrolmentRepository.save(enrolment)
    }

    def "login as a member and get enrolments"() {
        given: 'a member'
        demoMemberLogin()

        when:
        def response = webClient.get()
                .uri("/enrolments/${activityId}")
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .retrieve()
                .bodyToFlux(EnrolmentDto.class)
                .collectList()
                .block()

        then: "check response"
        response.size() == 2
        response.get(0).motivation == ENROLMENT_MOTIVATION_1
        response.get(0).volunteer.name == USER_1_NAME
        withinFiveMinutes(response.get(0).enrolmentDateTime, DateHandler.toISOString(NOW))
        response.get(1).motivation == ENROLMENT_MOTIVATION_1
        response.get(1).volunteer.name == USER_2_NAME
        withinFiveMinutes(response.get(1).enrolmentDateTime, DateHandler.toISOString(NOW))

        cleanup:
        deleteAll()
    }

    def "login as member of another institution and cannot get enrolments"() {
        given:
        def otherInstitution = new Institution(INSTITUTION_1_NAME, INSTITUTION_1_EMAIL, INSTITUTION_1_NIF)
        institutionRepository.save(otherInstitution)
        def otherMember = createMember(USER_1_NAME,USER_1_USERNAME,USER_1_PASSWORD,USER_1_EMAIL, AuthUser.Type.NORMAL, otherInstitution, User.State.APPROVED)
        normalUserLogin(USER_1_USERNAME, USER_1_PASSWORD)

        when:
        def response = webClient.get()
                .uri("/enrolments/${activityId}")
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .retrieve()
                .bodyToFlux(EnrolmentDto.class)
                .collectList()
                .block()

        then: "check response status"
        def error = thrown(WebClientResponseException)
        error.statusCode == HttpStatus.FORBIDDEN
        enrolmentRepository.count() == 2

        cleanup:
        deleteAll()
    }

    def "login as volunteer and cannot get enrolments"() {
        given: 'a volunteer'
        demoVolunteerLogin()

        when:
        def response = webClient.get()
                .uri("/enrolments/${activityId}")
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .retrieve()
                .bodyToFlux(EnrolmentDto.class)
                .collectList()
                .block()

        then: "check response status"
        def error = thrown(WebClientResponseException)
        error.statusCode == HttpStatus.FORBIDDEN
        enrolmentRepository.count() == 2

        cleanup:
        deleteAll()
    }

    def "login as admin and cannot get enrolments"() {
        given: 'a volunteer'
        demoAdminLogin()

        when:
        def response = webClient.get()
                .uri("/enrolments/${activityId}")
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .retrieve()
                .bodyToFlux(EnrolmentDto.class)
                .collectList()
                .block()

        then: "check response status"
        def error = thrown(WebClientResponseException)
        error.statusCode == HttpStatus.FORBIDDEN
        enrolmentRepository.count() == 2

        cleanup:
        deleteAll()
    }
}
