package ga.dryco.stalker.repositories;

import java.util.List;

import ga.dryco.stalker.domain.srsusers;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SrsusersRepository extends PagingAndSortingRepository<srsusers, Long> {

    List<srsusers> findByUsername(String username);
}