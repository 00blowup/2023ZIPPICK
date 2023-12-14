package soaksoak.zippick.zippick.domain.data.service;

import lombok.RequiredArgsConstructor;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;
import soaksoak.zippick.zippick.domain.ai.service.AiService;
import soaksoak.zippick.zippick.domain.data.dto.KeywordSearchRequestDto;
import soaksoak.zippick.zippick.domain.data.repository.DataRepository;
import soaksoak.zippick.zippick.domain.data.domain.Data;
import soaksoak.zippick.zippick.domain.data.dto.DataAddRequestDto;
import soaksoak.zippick.zippick.domain.data.dto.DataResponseDto;
import soaksoak.zippick.zippick.domain.data.dto.DataUpdateRequestDto;
import soaksoak.zippick.zippick.domain.datakeyword.domain.DataKeyword;
import soaksoak.zippick.zippick.domain.datakeyword.repository.DataKeywordRepository;
import soaksoak.zippick.zippick.domain.keyword.domain.Keyword;
import soaksoak.zippick.zippick.domain.keyword.repository.KeywordRepository;
import soaksoak.zippick.zippick.domain.member.domain.Member;
import soaksoak.zippick.zippick.domain.member.repository.MemberRepository;
import soaksoak.zippick.zippick.domain.member.service.MemberService;
import soaksoak.zippick.zippick.domain.sum.domain.Sum;
import soaksoak.zippick.zippick.domain.sum.repository.SumRepository;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

@Service
@RequiredArgsConstructor
public class DataService {

    private final DataRepository dataRepository;
    private final MemberRepository memberRepository;
    private final KeywordRepository keywordRepository;
    private final DataKeywordRepository dataKeywordRepository;
    private final SumRepository sumRepository;
    private final AiService aiService;
    private final MemberService memberService;

    // 새 기록 추가
    public String addData(DataAddRequestDto requestDto, String accessToken) throws ParseException{
        // AccessToken에 담긴 유저명을 기준으로 데이터 작성
        String loginUsername = memberService.getUsernameFromToken(accessToken);
        Member loginMember = memberRepository.findByUsername(loginUsername)
                .orElseThrow(() -> new EntityNotFoundException("해당 사용자를 찾을 수 없습니다: " + loginUsername));
        Long writerId = loginMember.getMemberId();

        // AI와 관련 없는 데이터 저장
        Member writer = memberRepository.findById(writerId).get();
        Data data = Data.builder()
                .writer(writer)
                .requestDto(requestDto)
                .build();

        Data savedData = dataRepository.save(data);

        // 키워드 부여
        // Chat GPT에게서 키워드 받아오기
        List<String> keywordStrings = getKeywords(requestDto.getDescription());
        for(String s : keywordStrings) {
            System.out.println(s);
            // 데이터베이스에서 해당하는 키워드 데이터 찾아오기
            Keyword foundKeyword = keywordRepository.findByKeywordName(s.trim())
                    .orElseThrow(() -> new EntityNotFoundException("해당 키워드를 찾을 수 없습니다: " + s));
            // 기록-키워드 매핑 정보를 데이터베이스에 저장
            dataKeywordRepository.save(
                    DataKeyword.builder()
                            .data(savedData)
                            .keyword(foundKeyword)
                            .build()
            );
        }

        // 요약문 부여
        String summary = getSummary(requestDto.getDescription());
        // 요약문을 데이터베이스에 저장
        sumRepository.save(
                Sum.builder()
                        .content(summary)
                        .data(savedData)
                        .build()
        );

        return "Success";
    }


    // 기록 수정
    public String updateData(Long dataId, DataUpdateRequestDto requestDto, String accessToken) throws ParseException{
        // 기존에 존재하는 데이터 불러오기
        Data data = dataRepository.findById(dataId)
                .orElseThrow(() -> new EntityNotFoundException("해당 기록을 찾을 수 없습니다: " + dataId));

        // AccessToken에 담긴 유저명을 기준으로 권한 검증
        String loginUsername = memberService.getUsernameFromToken(accessToken);
        Member loginMember = memberRepository.findByUsername(loginUsername)
                .orElseThrow(() -> new EntityNotFoundException("해당 사용자를 찾을 수 없습니다: " + loginUsername));
        Long writerId = loginMember.getMemberId();
        if(data.getWriter().getMemberId() != writerId)
            throw new IllegalArgumentException("기록 작성자와 로그인된 사용자가 일치하지 않습니다");

        // AI와 관련 없는 데이터 수정 (null이 아닌 데이터만)
        data.update(requestDto);

        // desc가 null이 아닐 경우
        String newDesc = requestDto.getDescription();
        if(newDesc != null) {
            // 기존에 부여되어있던 키워드 매핑 데이터들을 삭제
            List<DataKeyword> originalKeywords = dataKeywordRepository.findAllByData(data);
            for(DataKeyword dk : originalKeywords) dataKeywordRepository.delete(dk);

            // 키워드 재부여
            List<String> keywordStrings = getKeywords(newDesc);
            for(String s : keywordStrings) {
                // 데이터베이스에서 해당하는 키워드 데이터 찾아오기
                Keyword foundKeyword = keywordRepository.findByKeywordName(s.trim())
                        .orElseThrow(() -> new EntityNotFoundException("해당 키워드를 찾을 수 없습니다: " + s));
                // 데이터-키워드 매핑 정보를 데이터베이스에 저장
                dataKeywordRepository.save(
                        DataKeyword.builder()
                                .data(data)
                                .keyword(foundKeyword)
                                .build()
                );
            }

            // 기존에 부여되어있던 요약문을 삭제
            Sum originalSum = sumRepository.findByData(data)
                    .orElseThrow(() -> new EntityNotFoundException("해당 기록의 요약문을 찾을 수 없습니다: " + data.getDataId()));
            sumRepository.delete(originalSum);

            // 요약문 재부여
            String summary = getSummary(newDesc);
            // 요약문을 데이터베이스에 저장
            sumRepository.save(
                    Sum.builder()
                            .content(summary)
                            .data(data)
                            .build()
            );

        }

        return "Success";
    }

    // 기록 삭제
    public String deleteData(Long dataId, String accessToken) {
        Data foundData = dataRepository.findById(dataId)
                .orElseThrow(() -> new EntityNotFoundException("해당 기록을 찾을 수 없습니다: " + dataId));

        // 현재 로그인한 유저와 기록의 작성자 정보가 일치하는지 확인
        String loginUsername = memberService.getUsernameFromToken(accessToken);
        Member loginMember = memberRepository.findByUsername(loginUsername)
                .orElseThrow(() -> new EntityNotFoundException("해당 사용자를 찾을 수 없습니다: " + loginUsername));
        if(foundData.getWriter().getMemberId() != loginMember.getMemberId())
            throw new IllegalArgumentException("기록 작성자와 로그인된 사용자가 일치하지 않습니다");

        // 기록 삭제
        dataRepository.delete(foundData);

        return "Success";
    }

    // 기록 하나 조회
    public DataResponseDto getData(Long dataId, String accessToken) {
        Data foundData = dataRepository.findById(dataId)
                .orElseThrow(() -> new EntityNotFoundException("해당 기록을 찾을 수 없습니다: " + dataId));

        // 현재 로그인한 유저와 기록의 작성자 정보가 일치하는지 확인
        String loginUsername = memberService.getUsernameFromToken(accessToken);
        Member loginMember = memberRepository.findByUsername(loginUsername)
                .orElseThrow(() -> new EntityNotFoundException("해당 사용자를 찾을 수 없습니다: " + loginUsername));
        if(foundData.getWriter().getMemberId() != loginMember.getMemberId())
            throw new IllegalArgumentException("기록 작성자와 로그인된 사용자가 일치하지 않습니다");

        Sum foundSum = sumRepository.findByData(foundData)
                .orElseThrow(() -> new EntityNotFoundException("해당 기록의 요약문을 찾을 수 없습니다: " + dataId));
        List<DataKeyword> foundKeywordMappings = dataKeywordRepository.findAllByData(foundData);
        List<String> keywords = new ArrayList<>();
        for(DataKeyword dk : foundKeywordMappings) keywords.add(dk.getKeyword().getKeywordName());
        return DataResponseDto.builder()
                .writerId(foundData.getWriter().getMemberId())
                .dataName(foundData.getDataName())
                .started(foundData.getStarted())
                .ended(foundData.getEnded())
                .desc(foundData.getDescription())
                .sum(foundSum.getContent())
                .keywords(keywords)
                .build();
    }

    // 특정 유저의 모든 기록 리스트 조회
    public List<DataResponseDto> getMyDataList(String accessToken) {
        // AccessToken에 담긴 유저 정보 꺼내오기
        String loginUsername = memberService.getUsernameFromToken(accessToken);
        Member loginMember = memberRepository.findByUsername(loginUsername)
                .orElseThrow(() -> new EntityNotFoundException("해당 사용자를 찾을 수 없습니다: " + loginUsername));

        // 현재 로그인된 사용자의 모든 기록 조회
        List<Data> dataList = dataRepository.findAllByWriter(loginMember);
        List<DataResponseDto> result = new ArrayList<>();
        for(Data d : dataList) {
            Sum foundSum = sumRepository.findByData(d)
                    .orElseThrow(() -> new EntityNotFoundException("해당 기록의 요약문을 찾을 수 없습니다: " + d.getDataId()));
            List<DataKeyword> foundKeywordMappings = dataKeywordRepository.findAllByData(d);
            List<String> keywords = new ArrayList<>();
            for (DataKeyword dk : foundKeywordMappings) keywords.add(dk.getKeyword().getKeywordName());

            result.add(
                    DataResponseDto.builder()
                            .writerId(d.getWriter().getMemberId())
                            .dataName(d.getDataName())
                            .started(d.getStarted())
                            .ended(d.getEnded())
                            .desc(d.getDescription())
                            .sum(foundSum.getContent())
                            .keywords(keywords)
                            .build()
            );
        }

        // 결과 리턴
        return result;
    }

    // 특정 유저의 특정 키워드에 해당하는 모든 기록 리스트 조회
    public List<DataResponseDto> getDataListByKeyword(KeywordSearchRequestDto requestDto, String accessToken) {
        // 로그인된 유저의 모든 기록 조회
        List<DataResponseDto> usersAllData = getMyDataList(accessToken);
        List<DataResponseDto> result = new ArrayList<>();

        // 조회된 기록 중, 주어진 키워드에 해당하는 것만 골라내기
        for(DataResponseDto dto : usersAllData) {
            if(dto.getKeywords().contains(requestDto.getKeyword())) result.add(dto);
        }

        // 결과 리턴
        return result;
    }

    // 키워드 부여 메소드
    private List<String> getKeywords (String desc) throws ParseException {
        List<String> keywords = new ArrayList<>();

        // Chat GPT에게서 답 받아오기
        String question = desc +
                " 이러한 활동기록에서 드러난 역량이 무엇인지 다음의 키워드들 중에서 3가지를 골라줘: 리더십, 봉사정신, 창의력, 탐구정신, 협동정신, 기획역량, 디자인역량, 연구역량, 개발역량. " +
                "키워드/키워드/키워드 형식으로 적어줘.";
        String answer = aiService.getAnswer(question);

        // 받아온 답을 ArrayList로 변환
        StringTokenizer st = new StringTokenizer(answer,"/");
        while(st.hasMoreTokens()) keywords.add(st.nextToken());

        // 결과 리스트 리턴하기
        return keywords;
    }

    // 요약문 생성 메소드
    private String getSummary (String desc) throws ParseException {
        // Chat GPT에서 답 받아와 리턴하기
        String question = desc +
                " 위의 활동기록문을 300자 이내로 요약해줘.";
        return aiService.getAnswer(question);
    }

}
