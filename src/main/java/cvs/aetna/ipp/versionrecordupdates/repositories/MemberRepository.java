package cvs.aetna.ipp.versionrecordupdates.repositories;

import cvs.aetna.ipp.versionrecordupdates.model.Member;

import java.util.List;

public interface MemberRepository {

    public Member save(Member newMember);

    public List<Member> updateMemberKeepHistory(Member newMember);

    public Member getMemberByID(String memberId);
}

