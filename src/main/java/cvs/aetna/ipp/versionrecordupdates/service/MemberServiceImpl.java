package cvs.aetna.ipp.versionrecordupdates.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

import cvs.aetna.ipp.versionrecordupdates.model.Member;
import cvs.aetna.ipp.versionrecordupdates.repositories.MemberRepository;

@Service
public class MemberServiceImpl implements MemberService {

    @Autowired
    private MemberRepository memberRepository;


    /**
     * Inserts a new member record into the database
     * This method creates a brand new member without versioning
     * 
     * @param member The member entity to be inserted
     * @return The inserted member with generated ID and metadata
     */
    public Member insertMember(Member member) {
        return memberRepository.save(member);
    }

    /**
     * Updates an existing member record while preserving history
     * This method creates a new version of the member record and marks the previous version as inactive
     * 
     * @param updatedMember The updated member information
     * @return List of all versions of the member (including the new version)
     */
    public List<Member> updateMember(Member updatedMember) {
        return memberRepository.updateMemberKeepHistory(updatedMember);
    }
    
    /**
     * Retrieves the current (latest) member record by memberId
     * Only returns the active record with currInd = "Y"
     * 
     * @param memberId The unique identifier for the member
     * @return The current active member record or null if not found
     */
    @Override
    public Member getMemberById(String memberId) {
        return memberRepository.getMemberByID(memberId);
    }
}
