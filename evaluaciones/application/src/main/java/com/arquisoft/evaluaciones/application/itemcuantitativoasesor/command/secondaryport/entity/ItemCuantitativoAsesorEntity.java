package com.arquisoft.evaluaciones.application.itemcuantitativoasesor.command.secondaryport.entity;

import java.util.UUID;

public record ItemCuantitativoAsesorEntity(
        UUID id, String nombre, String descripcion, UUID categoriaId, Integer valor) {
}
