package com.example.project.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DocumentDto {

    @JsonProperty("place_name")
    private String placeName;

    @JsonProperty("address_name") // 도로명 주소
    private String addressName;

    @JsonProperty("y") // 위도
    private double latitude;

    @JsonProperty("x") // 경도
    private double longitude;

    @JsonProperty("distance")
    private double distance;
}
