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
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.dto.ActivityDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.dto.AssessmentDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CreateAssessmentWebServiceIT extends SpockTest {
    @LocalServerPort
    private int port

    def assessmentDto
    def institution
    def activity
    def volunteer

    def setup() {
        deleteAll()

        webClient = WebClient.create("http://localhost:" + port)
        headers = new HttpHeaders()
        headers.setContentType(MediaType.APPLICATION_JSON)

        institution = institutionService.getDemoInstitution()
        activity = new Activity()
        activity.setEndingDate(NOW)
        activity.setInstitution(institution)
        activityRepository.save(activity)

        institution.addActivity(activity)
        institutionRepository.save(institution)

        volunteer  = new Volunteer()
        userRepository.save(volunteer)

        assessmentDto = createAssessmentDto(ASSESSMENT_REVIEW_1, DateHandler.toISOString(IN_ONE_DAY))
    }

    def "login as volunteer, and create an assessment"() {
        given:
        demoVolunteerLogin()

        when:
        def response = webClient.post()
                .uri('/assessments/' + institution.getId())
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .bodyValue(assessmentDto)
                .retrieve()
                .bodyToMono(AssessmentDto.class)
                .block()

        then: "check response data"
        response.review == ASSESSMENT_REVIEW_1
        withinFiveMinutes(response.reviewDate, DateHandler.toISOString(IN_ONE_DAY))

        and: 'check database data'
        assessmentRepository.count() == 1
        def assessment = assessmentRepository.findAll().get(0)
        assessment.getReview() == ASSESSMENT_REVIEW_1
        withinFiveMinutes(DateHandler.toISOString(assessment.reviewDate), DateHandler.toISOString(IN_ONE_DAY))

        cleanup:
        deleteAll()
    }

    def "login as volunteer, and create an activity with error"() {
        given: 'a volunteer'
        demoVolunteerLogin()
        and: 'a name with blanks'
        assessmentDto.review = "  "

        when: 'the member registers the activity'
        webClient.post()
                .uri('/assessments/' + institution.getId())
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .bodyValue(assessmentDto)
                .retrieve()
                .bodyToMono(assessmentDto.class)
                .block()

        then: "check response status"
        def error = thrown(WebClientResponseException)
        error.statusCode == HttpStatus.BAD_REQUEST
        assessmentRepository.count() == 0

        cleanup:
        deleteAll()
    }

    def "login as a member, and create an activity"() {
        given: 'a member'
        demoMemberLogin()

        when: 'the volunteer registers the activity'
        webClient.post()
                .uri('/assessments/' + institution.getId())
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .bodyValue(assessmentDto)
                .retrieve()
                .bodyToMono(AssessmentDto.class)
                .block()

        then: "an error is returned"
        def error = thrown(WebClientResponseException)
        error.statusCode == HttpStatus.FORBIDDEN
        assessmentRepository.count() == 0

        cleanup:
        deleteAll()
    }

    def "login as admin, and create an activity"() {
        given: 'a demo'
        demoAdminLogin()

        when: 'the admin registers the activity'
        webClient.post()
                .uri('/assessments/' + institution.getId())
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .bodyValue(assessmentDto)
                .retrieve()
                .bodyToMono(AssessmentDto.class)
                .block()

        then: "an error is returned"
        def error = thrown(WebClientResponseException)
        error.statusCode == HttpStatus.FORBIDDEN
        assessmentRepository.count() == 0

        cleanup:
        deleteAll()
    }

}
