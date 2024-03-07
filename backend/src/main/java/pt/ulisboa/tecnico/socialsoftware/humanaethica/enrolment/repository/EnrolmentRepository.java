package pt.ulisboa.tecnico.socialsoftware.humanaethica.enrolment.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.enrolment.domain.Enrolment;

@Repository
@Transactional
public interface EnrolmentRepository extends JpaRepository<Enrolment, Integer> {
    @Query("SELECT e FROM Enrolment e WHERE e.activity.id = :activityId")
    List<Enrolment> getEnrolmentsByActivityId(Integer activityId);
}