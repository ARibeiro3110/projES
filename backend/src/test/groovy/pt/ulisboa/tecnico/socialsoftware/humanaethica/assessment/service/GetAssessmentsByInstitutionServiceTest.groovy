package pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest

@DataJpaTest
class GetAssessmentsByInstitutionServiceTest extends SpockTest {
    def setup() {
    }

    def 'get two assessments'() {
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
