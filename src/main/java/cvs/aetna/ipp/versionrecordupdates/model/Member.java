package cvs.aetna.ipp.versionrecordupdates.model;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDate;

@Document(collection = "members")
public class Member {

    @Id
    private ObjectId id;
    private String memberId;
    private String firstName;
    private String lastName;
    private String primaryNumber;
    private LocalDate effStartDate;
    private LocalDate effEndDate;
    private Integer version; // Managed in Mongo, not from client payload
    private String currInd;  // 'Y' for active, 'N' for inactive

    // Getters and setters


    public ObjectId getId() {
        return id;
    }
    public void setId(ObjectId id) {
        this.id = id;
    }
    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    public String getPrimaryNumber() {
        return primaryNumber;
    }
    public void setPrimaryNumber(String primaryNumber) {
        this.primaryNumber = primaryNumber;
    }
    public LocalDate getEffStartDate() {
        return effStartDate;
    }
    public void setEffStartDate(LocalDate effStartDate) {
        this.effStartDate = effStartDate;
    }
    public LocalDate getEffEndDate() {
        return effEndDate;
    }
    public void setEffEndDate(LocalDate effEndDate) {
        this.effEndDate = effEndDate;
    }
    public Integer getVersion() {
        return this.version;
    }
    public void setVersion(Integer version) {
        this.version = version;
    }
    public String getCurrInd() {
        return currInd;
    }
    public void setCurrInd(String currInd) {
        this.currInd = currInd;
    }
    public String getMemberId() {
        return memberId;
    }
    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }
}
