package pt.ulisboa.tecnico.socialsoftware.humanaethica.enrolment;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.enrolment.dto.EnrolmentDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.domain.AuthUser;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/enrolments")
public class EnrolmentController {
    @Autowired
    private EnrolmentService enrolmentService;

    private static final Logger logger = LoggerFactory.getLogger(EnrolmentController.class);

    @GetMapping("/{activityId}")
    @PreAuthorize("hasRole('ROLE_MEMBER') and hasPermission(#activityId, 'ACTIVITY.MEMBER')")
    public List<EnrolmentDto> getActivityEnrolments(@PathVariable Integer activityId) {
        return enrolmentService.getEnrolmentsByActivity(activityId);
    }

    @PostMapping("/{activityId}")
    @PreAuthorize("(hasRole('ROLE_VOLUNTEER'))")
    public EnrolmentDto createEnrolment(Principal principal, @PathVariable Integer activityId, @Valid @RequestBody EnrolmentDto enrolmentDto){
        int userId = ((AuthUser) ((Authentication) principal).getPrincipal()).getUser().getId();
        return enrolmentService.createEnrolment(userId, activityId, enrolmentDto);
    }


}