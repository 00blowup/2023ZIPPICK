package soaksoak.zippick.zippick.domain.sum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import soaksoak.zippick.zippick.domain.data.domain.Data;
import soaksoak.zippick.zippick.domain.sum.domain.Sum;

import java.util.Optional;

public interface SumRepository extends JpaRepository<Sum, Long> {
    Optional<Sum> findByData (Data data);
}
