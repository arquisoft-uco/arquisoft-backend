package com.arquisoft.fichas.infrastructure.fichaperfil.command.primaryadapter.web.dto;

import java.util.List;

public record RegistrarFichaPerfilRequestDTO(
        String tituloProyecto,
        String asesorFicha,
        List<String> estudiantes) {}
