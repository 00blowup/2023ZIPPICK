package soaksoak.zippick.zippick.domain.data.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

@Getter
@NoArgsConstructor
public class DataUpdateRequestDto {
    @Nullable
    private String dataName;
    @Nullable
    private Long startYear;
    @Nullable
    private Long startMonth;
    @Nullable
    private Long startDay;
    @Nullable
    private Long endYear;
    @Nullable
    private Long endMonth;
    @Nullable
    private Long endDay;
    @Nullable
    private String description;

}
