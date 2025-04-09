package com.example.project.api.service

import com.example.project.AbstractIntegrationContainerBaseTest
import com.example.project.api.dto.DocumentDto
import com.example.project.api.dto.KakaoApiResponseDto
import com.example.project.api.dto.MetaDto
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.spockframework.spring.SpringBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper

import static org.springframework.http.HttpHeaders.*

class KakaoAddressSearchServiceRetryTest extends AbstractIntegrationContainerBaseTest {

    @Autowired
    private KakaoAddressSearchService kakaoAddressSearchService

    // @MockBean -> 스프링 컨테이너 내에 있는 빈을 목킹
    @SpringBean
    private KakaoUriBuilderService kakaoUriBuilderService = Mock()

    private MockWebServer mockWebServer

    private ObjectMapper mapper = new ObjectMapper()

    private String inputAddress = "서울 성북구 종암로 10길"

    def setup() {
        mockWebServer = new MockWebServer()
        mockWebServer.start()
        println mockWebServer.port
        println mockWebServer.url("/") // localhost에서 실행
    }

    def cleanup() { // 테스트 메소드가 끝나고 나서 실행되는 메소드
        mockWebServer.shutdown()
    }

    def "requestAddressSearch retry success"() {
        given:
        def metaDto = new MetaDto(1)
        def documentDto = DocumentDto.builder()
        .addressName(inputAddress)
        .build()
        def expectedResponse = new KakaoApiResponseDto(metaDto, Arrays.asList(documentDto))
        def uri = mockWebServer.url("/").uri()

        when:
        mockWebServer.enqueue(new MockResponse().setResponseCode(504)) // 실패
        mockWebServer.enqueue(new MockResponse().setResponseCode(200) // 성공
            .addHeader(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .setBody(mapper.writeValueAsString(expectedResponse)))

        def kakaoApiResult = kakaoAddressSearchService.requestAddressSearch(inputAddress)

        then:
        2 * kakaoUriBuilderService.buildUrlByAddressSearch(inputAddress) >> uri // 두번 호출 된 게 맞는지 확인 (첫번째 응답 실패, 두번째 응답 성공)
        kakaoApiResult.getDocumentList().size() == 1 // 사이즈가 맞는지 확인
        kakaoApiResult.getMetaDto().totalCount == 1
        kakaoApiResult.getDocumentList().get(0).getAddressName() == inputAddress
    }

    def "requestAddressSearch retry fail"() {
        given:
        def uri = mockWebServer.url("/").uri()

        when:
        mockWebServer.enqueue(new MockResponse().setResponseCode(504)) // 실패
        mockWebServer.enqueue(new MockResponse().setResponseCode(504)) // 실패

        def result = kakaoAddressSearchService.requestAddressSearch(inputAddress)

        then:
        2 * kakaoUriBuilderService.buildUrlByAddressSearch(inputAddress) >> uri
        result == null
    }
}