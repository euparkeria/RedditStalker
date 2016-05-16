package ga.dryco.stalker.repositories;

import ga.dryco.stalker.domain.SubredditObjects;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface SubredditObjectsRepository extends PagingAndSortingRepository<SubredditObjects, Long>{

    List<SubredditObjects> findById(String id);

    @Query("SELECT COUNT(u.id) FROM SubredditObjects u")
    Long totalNumOfSubreddits();


    List<SubredditObjects> findFirstByOrderByCreatedUtcDesc();


    @Query(value = "SELECT * FROM subreddit_objects WHERE subscribers > 0  ORDER BY subscribers DESC LIMIT 100", nativeQuery = true)
    List<SubredditObjects> findTopSubreddits();

    @Query("SELECT u FROM SubredditObjects u WHERE u.createdUtc > :timestamp AND u.subscribers >= :minSubscribers")
    Page<SubredditObjects> createdAfter(@Param("timestamp")Long timestamp, @Param("minSubscribers")Integer minSubscribers, Pageable pageable);

    @Query("SELECT COUNT(u.id) FROM SubredditObjects u WHERE u.subredditType = :subtype")
    Integer countBySubType(@Param("subtype")String subtype);

    @Query("SELECT COUNT(u.id) FROM SubredditObjects u WHERE u.quarantine = TRUE")
    Integer countQuarantined();


}
