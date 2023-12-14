package soaksoak.zippick.zippick.domain.data.domain;

import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.validator.constraints.Length;
import soaksoak.zippick.zippick.domain.data.dto.DataAddRequestDto;
import soaksoak.zippick.zippick.domain.data.dto.DataUpdateRequestDto;
import soaksoak.zippick.zippick.domain.datakeyword.domain.DataKeyword;
import soaksoak.zippick.zippick.domain.member.domain.Member;
import soaksoak.zippick.zippick.domain.sum.domain.Sum;
import soaksoak.zippick.zippick.global.entity.BaseTimeEntity;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Data extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long dataId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writer_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Member writer;

    @Column(nullable = false)
    private String dataName;

    @Column(nullable = false)
    private LocalDate started;

    @Column
    private LocalDate ended;

    @Column(nullable = false, length = 10000)
    private String description;

    @OneToOne(mappedBy = "data")
    private Sum sum;

    @OneToMany(mappedBy = "data")
    private List<DataKeyword> dataKeywords = new ArrayList<>();

    // Member 객체와 DTO를 매개변수로 받는 생성자
    @Builder
    public Data (Member writer, DataAddRequestDto requestDto) {
        this.writer = writer;
        this.dataName = requestDto.getDataName();

        String month = requestDto.getStartMonth().toString();
        String day = requestDto.getStartDay().toString();
        if(requestDto.getStartMonth()<10) month = "0" + month;
        if(requestDto.getStartDay()<10) day = "0" + day;
        this.started = LocalDate.parse(requestDto.getStartYear() + "-" + month + "-" + day);

        if(requestDto.getEndYear() != null){
            month = requestDto.getEndMonth().toString();
            day = requestDto.getEndDay().toString();
            if(requestDto.getEndMonth()<10) month = "0" + month;
            if(requestDto.getEndDay()<10) day = "0" + day;
            this.ended = LocalDate.parse(requestDto.getEndYear() + "-" + month + "-" + day);
        }

        this.description = requestDto.getDescription();
    }

    public void update (DataUpdateRequestDto requestDto) {
        if(requestDto.getDataName() != null) this.dataName = requestDto.getDataName();

        System.out.println("STARTYEAR " + requestDto.getStartYear());
        System.out.println("ENDYEAR " + requestDto.getEndYear());

        String month;
        String day;

        if(requestDto.getStartYear() != null){
            month = requestDto.getStartMonth().toString();
            day = requestDto.getStartDay().toString();
            if(requestDto.getStartMonth()<10) month = "0" + month;
            if(requestDto.getStartDay()<10) day = "0" + day;
            this.started = LocalDate.parse(requestDto.getStartYear() + "-" + month + "-" + day);
        }

        if(requestDto.getEndYear() != null){
            month = requestDto.getEndMonth().toString();
            day = requestDto.getEndDay().toString();
            if(requestDto.getEndMonth()<10) month = "0" + month;
            if(requestDto.getEndDay()<10) day = "0" + day;
            this.ended = LocalDate.parse(requestDto.getEndYear() + "-" + month + "-" + day);
        }

        if(requestDto.getDescription() != null) this.description = requestDto.getDescription();
    }

}
