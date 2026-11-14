package com.arquisoft.evaluaciones.infrastructure.itemcuantitativoasesor.command.primaryadapter.web.dto;

public record RegistrarItemCuantitativoAsesorRequestDTO(
        String nombre, String descripcion, String categoria, Integer valor) {
}
