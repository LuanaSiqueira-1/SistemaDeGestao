package com.concessionaria.backend.dto;

import java.time.LocalDateTime;
import java.util.Map;

public record ErroResponse (
        int status,
        String erro,
        String mensagem,
        Map<String, String> campos,
        LocalDateTime dataHora
){
}