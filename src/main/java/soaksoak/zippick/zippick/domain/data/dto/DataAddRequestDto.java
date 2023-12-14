package soaksoak.zippick.zippick.domain.data.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

@Getter
@NoArgsConstructor
public class DataAddRequestDto {

    private String dataName;
    private Long startYear;
    private Long startMonth;
    private Long startDay;
    @Nullable
    private Long endYear;
    @Nullable
    private Long endMonth;
    @Nullable
    private Long endDay;
    private String description;

}
