package com.example.project.api.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Service
@Slf4j
public class KakaoUriBuilderService {

    private static final String KAKAO_LOCAL_SEARCH_ADDRESS_URL = "https://dapi.kakao.com/v2/local/search/address.json";

    public URI buildUrlByAddressSearch(String address) {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(KAKAO_LOCAL_SEARCH_ADDRESS_URL);

        uriBuilder.queryParam("query", address);

        URI uri = uriBuilder.build().encode().toUri();
        log.info("[KakaoUriBuilderService buildUrlByAddressSearch] address: {}, uri: {}", address, uri);

        return uri;
    }
}
