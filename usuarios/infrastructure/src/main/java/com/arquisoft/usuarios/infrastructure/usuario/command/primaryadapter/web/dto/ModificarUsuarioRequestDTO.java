package com.arquisoft.usuarios.infrastructure.usuario.command.primaryadapter.web.dto;

import java.util.List;

public record ModificarUsuarioRequestDTO(
        String identificador,
        String nombre,
        String email,
        String contacto,
        String nombres,
        String apellidos,
        List<String> roles) {}
