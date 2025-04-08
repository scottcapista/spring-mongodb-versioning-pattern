package cvs.aetna.ipp.versionrecordupdates.controller;

import cvs.aetna.ipp.versionrecordupdates.model.Member;
import cvs.aetna.ipp.versionrecordupdates.service.MemberService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/members")
public class MemberController {
    private static final Logger LOGGER = LoggerFactory.getLogger(MemberController.class);

    @Autowired
    private MemberService memberService;

    /**
     * REST endpoint to insert a new member record
     * Validates that member ID is provided and creates the initial record
     * 
     * @param member The member data from request body
     * @return HTTP 201 Created with the created member record
     * @throws IllegalArgumentException if member ID is empty or null
     */
    @PostMapping("/insert")
    public ResponseEntity<Member> insertMember(@RequestBody Member member) {
        LOGGER.info("Received request to insert member with ID: {}", member.getMemberId());
        
        if (member.getMemberId() == null || member.getMemberId().trim().isEmpty()) {
            throw new IllegalArgumentException("Member ID cannot be empty");
        }
        
        return ResponseEntity.status(HttpStatus.CREATED).body(memberService.insertMember(member));
    }

    /**
     * REST endpoint to update a member while keeping version history
     * Creates a new version of the member record and marks the previous version as inactive
     * 
     * @param member The updated member data from request body
     * @return HTTP 200 OK with a list of all versions of the member
     * @throws IllegalArgumentException if member ID is empty or null
     * @throws NoSuchElementException if no member with given ID exists
     */
    @PostMapping("/updateMemberKeepHistory")
    public ResponseEntity<List<Member>> updateMember(@RequestBody Member member) {
        LOGGER.info("Received request to update member with ID: {}", member.getMemberId());
        
        if (member.getMemberId() == null || member.getMemberId().trim().isEmpty()) {
            throw new IllegalArgumentException("Member ID cannot be empty");
        }
        
        List<Member> updatedMembers = memberService.updateMember(member);
        
        if (updatedMembers == null || updatedMembers.isEmpty()) {
            throw new NoSuchElementException("No member found with ID: " + member.getMemberId());
        }
        
        return ResponseEntity.ok(updatedMembers);
    }
    
    /**
     * REST endpoint to retrieve the current version of a member record
     * Returns only the active record (currInd="Y") for the given member ID
     * 
     * @param memberId The unique identifier for the member
     * @return HTTP 200 OK with the current member record
     * @throws NoSuchElementException if no active member with given ID exists
     */
    @GetMapping("/latestRecord/{memberId}")
    public ResponseEntity<Member> getMemberById(@PathVariable String memberId) {
        LOGGER.info("Received request to get member with ID: {}", memberId);
        
        Member member = memberService.getMemberById(memberId);
        
        if (member == null) {
            throw new NoSuchElementException("No member found with ID: " + memberId);
        }
        
        return ResponseEntity.ok(member);
    }
}
