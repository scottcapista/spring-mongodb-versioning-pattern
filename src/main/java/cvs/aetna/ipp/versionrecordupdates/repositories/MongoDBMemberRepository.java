package cvs.aetna.ipp.versionrecordupdates.repositories;

import com.mongodb.ReadConcern;
import com.mongodb.ReadPreference;
import com.mongodb.TransactionOptions;
import com.mongodb.WriteConcern;
import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.ReturnDocument;
import cvs.aetna.ipp.versionrecordupdates.model.Member;
import jakarta.annotation.PostConstruct;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.*;

@Repository
public class MongoDBMemberRepository implements MemberRepository {
    private final static Logger LOGGER = LoggerFactory.getLogger(MongoDBMemberRepository.class);

    private static final TransactionOptions txnOptions = TransactionOptions.builder()
            .readPreference(ReadPreference.primary())
            .readConcern(ReadConcern.MAJORITY)
            .writeConcern(WriteConcern.MAJORITY)
            .build();
    private final MongoClient client;
    private MongoCollection<Member> memberCollection;

    /**
     * Constructor that initializes the repository with a MongoDB client
     * 
     * @param mongoClient The MongoDB client for database operations
     */
    public MongoDBMemberRepository(MongoClient mongoClient) {
        this.client = mongoClient;
    }

    /**
     * Initializes the MongoDB collection after bean construction
     * Sets up the connection to the member collection in the database
     * Throws an exception if connection fails to fail fast on startup
     */
    @PostConstruct
    void init() {
        try {
            memberCollection = client.getDatabase("memberdb").getCollection("member", Member.class);
            LOGGER.info("MongoDB collection 'member' initialized successfully");
        } catch (Exception e) {
            LOGGER.error("Failed to initialize MongoDB collection: {}", e.getMessage(), e);
            // Rethrow to fail fast if we can't connect to MongoDB
            throw new RuntimeException("Failed to initialize MongoDB connection", e);
        }
    }

    /**
     * Saves a new member record to the database
     * Sets initial metadata like version=1, currInd="Y", and effective dates
     * 
     * @param newMember The member object to save
     * @return The saved member with generated ID
     */
    @Override
    public Member save(Member newMember) {
        try {
            LOGGER.info("Saving new member with ID: {}", newMember.getMemberId());
            newMember.setVersion(1);
            newMember.setCurrInd("Y");
            newMember.setEffStartDate(LocalDate.now());
            newMember.setEffEndDate(LocalDate.of(2099, 12, 31));
            memberCollection.insertOne(newMember);
            return memberCollection.find(eq("_id", newMember.getId())).first();
        } catch (Exception e) {
            LOGGER.error("Error saving member: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Updates a member record while preserving the version history
     * Uses MongoDB transactions to:
     * 1. Mark the current record as inactive (currInd="N")
     * 2. Create a new version with incremented version number
     * This implements the temporal data pattern for auditing and history tracking
     * 
     * @param newMember The updated member information
     * @return List of all versions of the member record
     */
    @Override
    public List<Member> updateMemberKeepHistory(Member newMember) {
        LOGGER.info("Starting updateMemberKeepHistory for member ID: {}", newMember.getMemberId());
        
        try (ClientSession clientSession = client.startSession()) {
            return clientSession.withTransaction(() -> {
                LOGGER.info("Inside transaction: Updating member document with memberId {}", newMember.getMemberId());
                
                //building key value filters
                Bson memberQuery = eq("memberId", newMember.getMemberId());
                Bson currentQuery = and(eq("currInd", "Y"), memberQuery);

                //create the update document to set the new effEndDate and currInd for the original record
                Document updates = new Document()
                        .append("effEndDate", LocalDate.now())
                        .append("currInd", "N");
                
                LOGGER.debug("Finding and updating current member document");
                //update the original member record and return the document before change
                Member originalMemberDocument = memberCollection.findOneAndUpdate(
                        currentQuery,
                        new Document("$set", updates),
                        new com.mongodb.client.model.FindOneAndUpdateOptions().returnDocument(ReturnDocument.BEFORE)
                );

                // Check if originalMemberDocument is null and handle it
                if (originalMemberDocument == null) {
                    LOGGER.info("No current member found with ID: {}. Creating new member.", newMember.getMemberId());
                    return List.of(this.save(newMember));
                }

                // Set common fields on the new document for insert into database
                LOGGER.debug("Setting version and date fields on new document");
                newMember.setVersion(originalMemberDocument.getVersion() + 1);
                newMember.setEffStartDate(LocalDate.now());
                newMember.setEffEndDate(LocalDate.of(2099, 12, 31));
                newMember.setCurrInd("Y");
                
                LOGGER.debug("Inserting updated member document");
                memberCollection.insertOne(newMember);

                // Return all versions of the document
                // This is done for illastrative purposes only for what the new history looks like.
                LOGGER.debug("Retrieving all versions of member document");
                return memberCollection.find(memberQuery).into(new ArrayList<>());
            }, txnOptions);
        } catch (Exception e) {
            LOGGER.error("Error in updateMemberKeepHistory: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to update member: " + e.getMessage(), e);
        }
    }

    /**
     * Retrieves the current active member record by member ID
     * Only returns the record with currInd="Y" (active record)
     * 
     * @param memberId The unique identifier for the member
     * @return The current active member record or null if not found
     */
    @Override
    public Member getMemberByID(String memberId){
        Bson query = and(
                eq("memberId", memberId),
                eq("currInd", "Y")
        );
        return memberCollection.find(query, Member.class).first();
    }
}

