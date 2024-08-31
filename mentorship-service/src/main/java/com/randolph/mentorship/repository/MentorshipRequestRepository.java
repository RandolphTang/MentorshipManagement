package com.randolph.mentorship.repository;

import com.randolph.mentorship.entity.RequestStatus;
import com.randolph.mentorship.entity.request.MentorshipRequestEntity;
import com.randolph.mentorship.entity.session.MentorshipSessionEntity;
import com.randolph.mentorship.entity.user.UserMentorshipEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MentorshipRequestRepository extends JpaRepository<MentorshipRequestEntity, Long> {

    List<MentorshipRequestEntity> findByMentee(UserMentorshipEntity mentee);

    List<MentorshipRequestEntity> findByMentor(UserMentorshipEntity mentor);

    RequestStatus findByRequestStatus(MentorshipRequestEntity mentorshipRequestEntity);

    List<MentorshipRequestEntity> findAllByMentorIdOrMenteeId(Long mentorId, Long menteeId);

    List<MentorshipRequestEntity> findAllByMentorIdAndMenteeId(Long mentorId, Long menteeId);

    List<MentorshipRequestEntity> findByMentorAndRequestStatus(UserMentorshipEntity mentor, RequestStatus requestStatus);
    List<MentorshipRequestEntity> findByMenteeAndMentor(UserMentorshipEntity mentee, UserMentorshipEntity mentor);
    MentorshipRequestEntity findTopByMenteeAndMentorOrderByRequestDateDesc(UserMentorshipEntity mentee, UserMentorshipEntity mentor);
}
