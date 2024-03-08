package pt.ulisboa.tecnico.socialsoftware.humanaethica.participation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.validation.Valid;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.dto.ParticipationDto;

import java.util.List;

@RestController
@RequestMapping("/participations")
public class ParticipationController {

    @Autowired
    private ParticipationService participationService;

    @GetMapping("/{activityId}")
    @PreAuthorize("hasRole('ROLE_MEMBER') and hasPermission(#activityId, 'ACTIVITY.MEMBER')")
    public List<ParticipationDto> getActivityParticipations(@PathVariable Integer activityId) {
        return participationService.getParticipationsByActivity(activityId);
    }

    @PostMapping("/{activityId}")
    @PreAuthorize("hasRole('ROLE_MEMBER')")
    public ParticipationDto createParticipation(@PathVariable Integer activityId, 
                                                @Valid @RequestBody ParticipationDto participationDto) {
        return participationService.createParticipation(activityId, participationDto);
    }
    
}
