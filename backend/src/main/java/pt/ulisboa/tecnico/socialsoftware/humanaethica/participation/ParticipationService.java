package pt.ulisboa.tecnico.socialsoftware.humanaethica.participation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;


import pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.dto.ParticipationDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.repository.ParticipationRepository;


import java.util.Comparator;
import java.util.List;


@Service
public class ParticipationService {
    @Autowired
    ParticipationRepository participationRepository;

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public List<ParticipationDto> getParticipationsByActivity(Integer activityId) {
        return participationRepository.getParticipationsByActivityId(activityId).stream()
                .map(participation-> new ParticipationDto(participation, true, true)) // TODO check if deepCopy is needed
                .sorted(Comparator.comparing(ParticipationDto::getId))
                .toList();
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public ParticipationDto createParticipation(Integer activityId, ParticipationDto participationDto) {
        // TODO
        return null;
    }

}
