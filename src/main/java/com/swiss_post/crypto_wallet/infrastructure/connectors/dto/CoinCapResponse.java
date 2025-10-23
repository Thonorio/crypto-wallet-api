package com.swiss_post.crypto_wallet.infrastructure.connectors.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class CoinCapResponse {

    private long timestamp;

    private List<String> data;
}