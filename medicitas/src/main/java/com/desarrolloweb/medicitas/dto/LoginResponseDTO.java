package com.desarrolloweb.medicitas.dto;

import java.util.List;

public record LoginResponseDTO(
    String token,
    String tipo,
    String username,
    List<String> roles
) {}
