package com.gateway.controller;
import com.gateway.dto.ResponseDto;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import static com.gateway.dto.enumDto.EnumResult.FALLBACK_SERVICE;

@RestController
public class FallbackController {

    @RequestMapping("/fallback")
    public Mono<ResponseDto> shopServiceFallback() {
        return Mono.just(new ResponseDto(FALLBACK_SERVICE));
    }
}
