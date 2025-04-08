package cvs.aetna.ipp.versionrecordupdates.service;

import cvs.aetna.ipp.versionrecordupdates.model.Member;

import java.util.List;

public interface MemberService {
    public Member insertMember(Member member);

    public List<Member> updateMember(Member updatedMember);
    
    public Member getMemberById(String memberId);
}
