package soaksoak.zippick.zippick.domain.data.controller;

import lombok.RequiredArgsConstructor;
import org.json.simple.parser.ParseException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import soaksoak.zippick.zippick.domain.data.dto.DataAddRequestDto;
import soaksoak.zippick.zippick.domain.data.dto.DataResponseDto;
import soaksoak.zippick.zippick.domain.data.dto.DataUpdateRequestDto;
import soaksoak.zippick.zippick.domain.data.dto.KeywordSearchRequestDto;
import soaksoak.zippick.zippick.domain.data.service.DataService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/datas")
public class DataController {

    private final DataService dataService;

    // 새 기록 추가 (키워드 부여 및 요약문 생성까지 진행)
    @PostMapping
    public ResponseEntity<String> addData (@RequestBody DataAddRequestDto requestDto, @RequestHeader("Authorization") String accessToken) throws ParseException {
        return ResponseEntity.ok().body(dataService.addData(requestDto, accessToken));
    }

    // 기록 수정 (설명문이 수정될 경우 키워드 부여 및 요약문 생성 다시 진행)
    @PostMapping("/{dataId}")
    public ResponseEntity<String> updateData (@PathVariable Long dataId, @RequestBody DataUpdateRequestDto requestDto, @RequestHeader("Authorization") String accessToken) throws ParseException{
        return ResponseEntity.ok().body(dataService.updateData(dataId, requestDto, accessToken));
    }

    // 기록 삭제
    @DeleteMapping("/{dataId}")
    public ResponseEntity<String> deleteData (@PathVariable Long dataId, @RequestHeader("Authorization") String accessToken) {
        return ResponseEntity.ok().body(dataService.deleteData(dataId, accessToken));
    }

    // 기록 하나 조회
    @GetMapping("/{dataId}")
    public DataResponseDto getData (@PathVariable Long dataId, @RequestHeader("Authorization") String accessToken) {
        return dataService.getData(dataId, accessToken);
    }

    // 특정 회원의 모든 기록 조회
    @GetMapping("/mydb")
    public List<DataResponseDto> getMyDataList (@RequestHeader("Authorization") String accessToken) {
        return dataService.getMyDataList(accessToken);
    }

    // 특정 회원의 특정 키워드에 해당하는 모든 기록 조회
    @GetMapping("/mykeyword")
    public List<DataResponseDto> getDataListByKeyword (@RequestBody KeywordSearchRequestDto requestDto, @RequestHeader("Authorization") String accessToken) {
        return dataService.getDataListByKeyword(requestDto, accessToken);
    }


}
