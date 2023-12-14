package soaksoak.zippick.zippick.domain.data.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class ChatbotRequestDto {
    private List<DataIdRequestDto> dataIds;
    private String question;
}
