package com.arquisoft.evaluaciones.application.itemcuantitativoasesor.command.primaryport.mapper;

import com.arquisoft.evaluaciones.application.itemcuantitativoasesor.command.primaryport.model.RegistrarItemCuantitativoAsesorCommand;
import com.arquisoft.evaluaciones.domain.itemcuantitativoasesor.ItemCuantitativoAsesorDomain;

public final class RegistrarItemCuantitativoAsesorMapper {

    private RegistrarItemCuantitativoAsesorMapper() {}

    public static ItemCuantitativoAsesorDomain toDomain(RegistrarItemCuantitativoAsesorCommand command) {
        return ItemCuantitativoAsesorDomain.crear(
                command.nombre(), command.descripcion(), command.categoria(), command.valor());
    }
}
