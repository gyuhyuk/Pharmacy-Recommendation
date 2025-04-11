package com.example.project.pharmacy.service;

import com.example.project.api.dto.DocumentDto;
import com.example.project.api.dto.KakaoApiResponseDto;
import com.example.project.api.service.KakaoAddressSearchService;
import com.example.project.direction.entity.Direction;
import com.example.project.direction.service.DirectionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class PharmacyRecommendationService {
    private final KakaoAddressSearchService kakaoAddressSearchService;
    private final DirectionService directionService;
    public void recommendPharmacyList(String address) { // 가까운 약국 찾기
        KakaoApiResponseDto kakaoApiResponseDto = kakaoAddressSearchService.requestAddressSearch(address); // 문자열 주소를 입력 받아 위치 기반 데이터로 변환
        if(Objects.isNull(kakaoApiResponseDto) || CollectionUtils.isEmpty(kakaoApiResponseDto.getDocumentList())) { // validation check
            log.error("[PharmacyRecommendationService recommendPharmacyList fail] Input address: {}", address);
            return;
        }

        DocumentDto documentDto = kakaoApiResponseDto.getDocumentList().get(0); // 변환된 위도 경도 기준으로 가까운 약국 찾기

//        List<Direction> directionList = directionService.buildDirectionList(documentDto); // 거리 계산 알고리즘을 통해 가까운 약국 리스트 반환
        List<Direction> directionList = directionService.buildDirectionListByCategoryApi(documentDto);
        directionService.saveAll(directionList); // 데이터베이스에 저장
    }
}
