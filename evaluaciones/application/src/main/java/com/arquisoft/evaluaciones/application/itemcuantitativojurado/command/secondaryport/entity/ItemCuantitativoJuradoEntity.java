package com.arquisoft.evaluaciones.application.itemcuantitativojurado.command.secondaryport.entity;

import java.util.UUID;

public record ItemCuantitativoJuradoEntity(
        UUID id, String nombre, String descripcion, UUID categoriaId, Integer valor) {
}
