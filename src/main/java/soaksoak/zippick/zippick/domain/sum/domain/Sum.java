package soaksoak.zippick.zippick.domain.sum.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import soaksoak.zippick.zippick.domain.data.domain.Data;

import javax.persistence.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Sum {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long sumId;

    @Column(nullable = false, length = 1000)
    private String content;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "data_id", nullable = false, updatable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    Data data;

    @Builder
    public Sum (String content, Data data) {
        this.content = content;
        this.data = data;
    }

}
