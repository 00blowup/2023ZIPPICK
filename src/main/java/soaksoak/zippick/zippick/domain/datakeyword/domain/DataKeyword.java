package soaksoak.zippick.zippick.domain.datakeyword.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import soaksoak.zippick.zippick.domain.data.domain.Data;
import soaksoak.zippick.zippick.domain.keyword.domain.Keyword;
import soaksoak.zippick.zippick.global.entity.BaseTimeEntity;

import javax.persistence.*;

// 기록과 키워드 다대다 매핑테이블
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class DataKeyword extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long aidataId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "data_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Data data;  // 사용자 입력 데이터와의 매핑

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "keyword_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Keyword keyword;    // 키워드와의 매핑

    // 생성자
    @Builder
    public DataKeyword (Data data, Keyword keyword) {
        this.data = data;
        this.keyword = keyword;
    }

}
