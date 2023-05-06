package com.parse.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.sql.Timestamp;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@JsonIgnoreProperties(ignoreUnknown = true, value = {
    "avgTotalVolume",
    "calculationPrice",
    "change",
    "changePercent",
    "close",
    "closeSource",
    "closeTime",
    "currency",
    "delayedPrice",
    "delayedPriceTime",
    "extendedChange",
    "extendedChangePercent",
    "extendedPrice",
    "extendedPriceTime",
    "high",
    "highSource",
    "highTime",
    "iexAskPrice",
    "iexAskSize",
    "iexBidPrice",
    "iexBidSize",
    "iexClose",
    "iexCloseTime",
    "iexLastUpdated",
    "iexMarketPercent",
    "iexOpen",
    "iexOpenTime",
    "iexRealtimePrice",
    "iexRealtimeSize",
    "iexVolume",
    "lastTradeTime",
    "latestSource",
    "latestTime",
    "latestVolume",
    "low",
    "lowSource",
    "lowTime",
    "marketCap",
    "oddLotDelayedPrice",
    "oddLotDelayedPriceTime",
    "open",
    "openTime",
    "openSource",
    "peRatio",
    "previousClose",
    "previousVolume",
    "primaryExchange",
    "volume",
    "week52High",
    "week52Low",
    "ytdChange",
    "isUSMarketOpen"
})
@Getter
@Setter
@ToString
public class PriceDTO {

    @JsonProperty("symbol")
    private String symbol;
    @JsonProperty("companyName")
    private String companyName;
    @JsonProperty("latestPrice")
    private Double latestPrice;
    @JsonProperty("latestUpdate")
    private Timestamp latestUpdate;
}
