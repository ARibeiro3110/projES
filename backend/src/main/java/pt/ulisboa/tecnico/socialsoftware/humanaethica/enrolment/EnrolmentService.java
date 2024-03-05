package  pt.ulisboa.tecnico.socialsoftware.humanaethica.enrolment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import pt.ulisboa.tecnico.socialsoftware.humanaethica.enrolment.domain.Enrolment;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.enrolment.dto.EnrolmentDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.enrolment.repository.EnrolmentRepository;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.domain.Institution;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.repository.InstitutionRepository;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.dto.ActivityDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.repository.ActivityRepository;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.repository.UserRepository;

import static pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EnrolmentService {
    @Autowired
    EnrolmentRepository enrolmentRepository;
    @Autowired
    ActivityRepository activityRepository;
    @Autowired
    UserRepository userRepository;

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public EnrolmentDto createEnrolment(Integer userId, Integer activityId, EnrolmentDto enrolmentDto) {
        if (userId == null) throw new HEException(USER_NOT_FOUND);
        Volunteer volunteer = (Volunteer) userRepository.findById(userId).orElseThrow(() -> new HEException(USER_NOT_FOUND, userId));
        Activity activity = activityRepository.findById(activityId).orElseThrow(() -> new HEException(ACTIVITY_NOT_FOUND, activityId));

        Enrolment enrolment = new Enrolment(activity, volunteer, enrolmentDto);

        enrolmentRepository.save(enrolment);

        return new EnrolmentDto(enrolment, true, true);
    }

    // TODO: public List<EnrolmentDto> getEnrolmentsByActivity(Integer activityId)


}





























