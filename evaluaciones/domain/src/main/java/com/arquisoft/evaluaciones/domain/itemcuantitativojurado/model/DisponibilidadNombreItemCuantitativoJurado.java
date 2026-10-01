package com.arquisoft.evaluaciones.domain.itemcuantitativojurado.model;

import java.util.UUID;

public record DisponibilidadNombreItemCuantitativoJurado(
        String nombre, UUID categoria, boolean yaExiste) {}
