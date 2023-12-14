package soaksoak.zippick.zippick.domain.keyword.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import soaksoak.zippick.zippick.domain.keyword.domain.Keyword;

import java.util.Optional;

public interface KeywordRepository extends JpaRepository<Keyword, Long> {
    Boolean existsByKeywordName(String keywordName);
    Optional<Keyword> findByKeywordName(String keywordName);
}
