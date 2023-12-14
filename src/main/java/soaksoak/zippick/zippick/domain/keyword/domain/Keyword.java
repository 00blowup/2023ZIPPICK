package soaksoak.zippick.zippick.domain.keyword.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import soaksoak.zippick.zippick.domain.datakeyword.domain.DataKeyword;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Keyword {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long keywordId;

    @Column(nullable = false)
    private String keywordName;

    @OneToMany(mappedBy = "keyword")
    private final List<DataKeyword> aidatas = new ArrayList<>();

    // 모든 키워드는 DB에 미리 넣어둘 것이므로 생성자 없음
}
