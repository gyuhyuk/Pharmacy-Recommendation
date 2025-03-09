package com.example.project.api.service

import com.example.project.AbstractIntegrationContainerBaseTest
import org.springframework.beans.factory.annotation.Autowired

class KakaoAddressSearchServiceTest extends AbstractIntegrationContainerBaseTest {

    @Autowired
    private KakaoAddressSearchService kakaoAddressSearchService

    def "address 파라미터 값이 null이면, requestAddressSearch 메소드는 null을 리턴한다."() {
        given:
        def address = null

        when:
        def search = kakaoAddressSearchService.requestAddressSearch(address)

        then:
        search == null
    }

    def "주소 값이 valid하다면, requestAddressSearch 메소드는 정상적으로 document를 반환한다."() {
        given:
        def address = "서울특별시 노원구 상계동"

        when:
        def result = kakaoAddressSearchService.requestAddressSearch(address)

        then:
        result.documentList.size() > 0
        result.metaDto.totalCount > 0
        result.documentList.get(0).addressName != null
    }
}