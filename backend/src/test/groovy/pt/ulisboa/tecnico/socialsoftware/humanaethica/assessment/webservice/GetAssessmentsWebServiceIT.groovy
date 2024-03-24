package pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.webservice

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity
import pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.dto.AssessmentDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.domain.Assessment
import pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.AssessmentRepository
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.domain.Theme
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.User
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GetAssessmentsWebServiceIT extends SpockTest {
    @LocalServerPort
    private int port

    def institutionId

    def setup() {
        deleteAll()

        webClient = WebClient.create("http://localhost:" + port)
        headers = new HttpHeaders()
        headers.setContentType(MediaType.APPLICATION_JSON)

        def institution = institutionService.getDemoInstitution()

        def theme = new Theme(THEME_NAME_1, Theme.State.APPROVED,null)
        themeRepository.save(theme)

        def activity = new Activity()
        activity.setName(ACTIVITY_NAME_1)
        activity.setRegion(ACTIVITY_REGION_1)
        activity.setParticipantsNumberLimit(3)
        activity.setDescription(ACTIVITY_DESCRIPTION_1)
        activity.setStartingDate(ONE_DAY_AGO)
        activity.setEndingDate(IN_ONE_DAY)
        activity.setApplicationDeadline(IN_TWO_DAYS)
        activity.setInstitution(institution)
        activity.addTheme(theme)
        activity.setState(Activity.State.APPROVED)
        activityRepository.save(activity)

        institution.addActivity(activity)
        institutionId = institution.id

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


        given: "assessment info"
        def assessmentDto = createAssessmentDto(ASSESSMENT_REVIEW_1, DateHandler.toISOString(IN_TWO_DAYS))
        and: "an assessment"
        def assessment = new Assessment(institution, volunteer_1, assessmentDto)
        assessmentRepository.save(assessment)
        and: 'another assessment'
        assessmentDto.review = ASSESSMENT_REVIEW_2
        assessment = new Assessment(institution, volunteer_2, assessmentDto)
        assessmentRepository.save(assessment)
    }


    def "Login as a member and get two assessments"() {
        given:
        demoMemberLogin()
        when:
        def response = webClient.get()
                .uri("/assessments/${institutionId}")
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .retrieve()
                .bodyToFlux(AssessmentDto.class)
                .collectList()
                .block()

        then: "check response data"
        response.size() == 2
        response.get(0).review == ASSESSMENT_REVIEW_1
        response.get(0).volunteer.name == USER_1_NAME
        withinFiveMinutes(response.get(0).reviewDate, DateHandler.toISOString(IN_TWO_DAYS))
        response.get(1).review == ASSESSMENT_REVIEW_2
        response.get(1).volunteer.name == USER_2_NAME
        withinFiveMinutes(response.get(1).reviewDate, DateHandler.toISOString(IN_TWO_DAYS))

        cleanup:
        deleteAll()
    }

    def "Login as an admin and get two assessments"() {
        given:
        demoAdminLogin()
        when:
        def response = webClient.get()
                .uri("/assessments/${institutionId}")
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .retrieve()
                .bodyToFlux(AssessmentDto.class)
                .collectList()
                .block()

        then: "check response data"
        response.size() == 2
        response.get(0).review == ASSESSMENT_REVIEW_1
        response.get(0).volunteer.name == USER_1_NAME
        withinFiveMinutes(response.get(0).reviewDate, DateHandler.toISOString(IN_TWO_DAYS))
        response.get(1).review == ASSESSMENT_REVIEW_2
        response.get(1).volunteer.name == USER_2_NAME
        withinFiveMinutes(response.get(1).reviewDate, DateHandler.toISOString(IN_TWO_DAYS))

        cleanup:
        deleteAll()
    }

    def "Login as a volunteer and get two assessments"() {
        given:
        demoVolunteerLogin()
        when:
        def response = webClient.get()
                .uri("/assessments/${institutionId}")
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .retrieve()
                .bodyToFlux(AssessmentDto.class)
                .collectList()
                .block()

        then: "check response data"
        response.size() == 2
        response.get(0).review == ASSESSMENT_REVIEW_1
        response.get(0).volunteer.name == USER_1_NAME
        withinFiveMinutes(response.get(0).reviewDate, DateHandler.toISOString(IN_TWO_DAYS))
        response.get(1).review == ASSESSMENT_REVIEW_2
        response.get(1).volunteer.name == USER_2_NAME
        withinFiveMinutes(response.get(1).reviewDate, DateHandler.toISOString(IN_TWO_DAYS))

        cleanup:
        deleteAll()
    }



    def "institutionId is not associated to any institution and cannot get assessments"() {
        given:
        def otherInstitutionId = 2

        when:
        webClient.get()
                .uri("/assessments/${otherInstitutionId}")
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .retrieve()
                .bodyToFlux(AssessmentDto.class)
                .collectList()
                .block()

        then: "an error is returned"
        def error = thrown(WebClientResponseException)
        error.statusCode == HttpStatus.FORBIDDEN
        assessmentRepository.count() == 2

        cleanup:
        deleteAll()
    }


}
