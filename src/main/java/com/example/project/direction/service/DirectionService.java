package com.example.project.direction.service;

import com.example.project.api.dto.DocumentDto;
import com.example.project.direction.entity.Direction;
import com.example.project.pharmacy.dto.PharmacyDto;
import com.example.project.pharmacy.service.PharmacySearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class DirectionService {

    // 약국 최대 검색 개수
    private static final int MAX_SEARCH_COUNT = 3;
    // 반경 10km
    private static final double RADIUS_KM = 10.0;

    private final PharmacySearchService pharmacySearchService;

    public List<Direction> buildDirectionList(DocumentDto documentDto) {
        if(Objects.isNull(documentDto)) {
            return Collections.emptyList();
        }
        // 약국 데이터 조회
        return pharmacySearchService.searchPharmacyDtoList()
                .stream().map(pharmacyDto ->
                        Direction.builder()
                                .inputAddress(documentDto.getAddressName())
                                .inputLatitude(documentDto.getLatitude())
                                .inputLongitude(documentDto.getLongitude())
                                .targetPharmacyName(pharmacyDto.getPharmacyName())
                                .targetAddress(pharmacyDto.getPharmacyAddress())
                                .targetLatitude(pharmacyDto.getLatitude())
                                .targetLongitude(pharmacyDto.getLongitude())
                                .distance(calculateDistance(documentDto.getLatitude(), documentDto.getLongitude(), pharmacyDto.getLatitude(), pharmacyDto.getLongitude()))
                                .build())
                                .filter(direction -> direction.getDistance() <= RADIUS_KM)
                                .sorted(Comparator.comparing(Direction::getDistance))
                                .limit(MAX_SEARCH_COUNT)
                .collect(Collectors.toList());
        // 거리계산 알고리즘을 이용하여, 고객과 약국 사이의 거리를 계산하고 sort

    }

    // Haversine formula
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        lat1 = Math.toRadians(lat1);
        lon1 = Math.toRadians(lon1);
        lat2 = Math.toRadians(lat2);
        lon2 = Math.toRadians(lon2);

        double earthRadius = 6371;
        return earthRadius * Math.acos(Math.sin(lat1) * Math.sin(lat2) + Math.cos(lat1) * Math.cos(lat2) * Math.cos(lon1 - lon2));
    }
}
