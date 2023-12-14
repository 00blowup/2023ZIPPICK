package soaksoak.zippick.zippick.domain.data.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DataResponseDto {
    private Long writerId;
    private String dataName;
    private LocalDate started;
    private LocalDate ended;
    private String desc;
    private String sum;
    private List<String> keywords;

    @Builder
    public DataResponseDto (Long writerId, String dataName, LocalDate started, LocalDate ended, String desc, String sum, List<String> keywords) {
        this.writerId = writerId;
        this.dataName = dataName;
        this.started = started;
        this.ended = ended;
        this.desc = desc;
        this.sum = sum;
        this.keywords = keywords;
    }
}
