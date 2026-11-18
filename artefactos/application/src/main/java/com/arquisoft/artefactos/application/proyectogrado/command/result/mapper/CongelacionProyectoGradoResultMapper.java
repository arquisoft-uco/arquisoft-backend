package com.arquisoft.artefactos.application.proyectogrado.command.result.mapper;

import com.arquisoft.artefactos.application.proyectogrado.command.result.CongelacionProyectoGradoResult;
import com.arquisoft.artefactos.domain.proyectogrado.CongelacionProyectoGradoDomain;

public final class CongelacionProyectoGradoResultMapper {

    private CongelacionProyectoGradoResultMapper() {}

    public static CongelacionProyectoGradoResult.Congelado toResultCongelado(CongelacionProyectoGradoDomain congelacion) {
        return new CongelacionProyectoGradoResult.Congelado(congelacion.getProyectoGrado());
    }

    public static CongelacionProyectoGradoResult.YaCongelado toResultYaCongelado(CongelacionProyectoGradoDomain congelacion) {
        return new CongelacionProyectoGradoResult.YaCongelado(congelacion.getProyectoGrado());
    }
}
