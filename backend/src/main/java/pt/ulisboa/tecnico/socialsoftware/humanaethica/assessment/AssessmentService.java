package pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.dto.AssessmentDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.domain.Assessment;
import  pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.repository.AssessmentRepository;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.domain.Institution;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.repository.InstitutionRepository;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.repository.UserRepository;

import static pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage.*;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.ArrayList;

@Service
public class AssessmentService {
    @Autowired
    AssessmentRepository assessmentRepository;

    @Autowired
    InstitutionRepository institutionRepository;

    @Autowired
    UserRepository userRepository;

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public List<AssessmentDto> getAssessmentsByInstitutionId(Integer institutionId) {
        if ( institutionId == null ) throw new HEException(INSTITUTION_NOT_FOUND);
        List<Assessment> assessments = assessmentRepository.getAssessmentsByInstitutionId(institutionId);
        List<AssessmentDto> assessmentDtos = new ArrayList<>();
        assessments.forEach(assessment -> {
            assessmentDtos.add(new AssessmentDto(assessment, true, true));
        });

        return assessmentDtos;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public AssessmentDto createAssessment(Integer userId, Integer institutionId, AssessmentDto assessmentDto) {

        if ( userId == null ) throw new HEException(USER_NOT_FOUND);
        if ( institutionId == null ) throw new HEException(INSTITUTION_NOT_FOUND);

        Institution institution = (Institution) institutionRepository.findById(institutionId).orElseThrow(
                () -> new HEException(INSTITUTION_NOT_FOUND, institutionId));
        Volunteer volunteer = (Volunteer) userRepository.findById(userId).orElseThrow(
                () -> new HEException(USER_NOT_FOUND, userId));

        Assessment assessment = new Assessment(institution, volunteer, assessmentDto);

        assessmentRepository.save(assessment);

        return new AssessmentDto(assessment, true, true);
    }
}
