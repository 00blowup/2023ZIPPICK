package soaksoak.zippick.zippick.domain.datakeyword.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import soaksoak.zippick.zippick.domain.data.domain.Data;
import soaksoak.zippick.zippick.domain.datakeyword.domain.DataKeyword;

import java.util.List;

public interface DataKeywordRepository extends JpaRepository<DataKeyword, Long> {
    List<DataKeyword> findAllByData (Data data);
}
