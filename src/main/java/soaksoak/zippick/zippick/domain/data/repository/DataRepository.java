package soaksoak.zippick.zippick.domain.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import soaksoak.zippick.zippick.domain.data.domain.Data;
import soaksoak.zippick.zippick.domain.member.domain.Member;

import java.util.List;

public interface DataRepository extends JpaRepository<Data, Long> {
    List<Data> findAllByWriter(Member writer);
}
