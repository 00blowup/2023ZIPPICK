package soaksoak.zippick.zippick.domain.data.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

@Getter
@NoArgsConstructor
public class DataUpdateRequestDto {

    private String dataName;
    private Long startYear;
    private Long startMonth;
    private Long startDay;
    @Nullable
    private Long EndYear;
    @Nullable
    private Long endMonth;
    @Nullable
    private Long endDay;
    private String description;

}
