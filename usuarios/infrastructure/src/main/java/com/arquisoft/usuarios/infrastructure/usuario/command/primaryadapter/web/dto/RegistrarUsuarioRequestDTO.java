package com.arquisoft.usuarios.infrastructure.usuario.command.primaryadapter.web.dto;

import java.util.List;

public record RegistrarUsuarioRequestDTO(
        String identificador,
        String nombres,
        String apellidos,
        String email,
        String contacto,
        List<String> roles) {}
