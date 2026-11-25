package com.arquisoft.fichas.infrastructure.evaluacionfichaperfil.command.primaryadapter.web.mapper;

import com.arquisoft.fichas.application.evaluacionfichaperfil.command.result.InicioEvaluacionFichaPerfilResult;
import com.arquisoft.fichas.infrastructure.evaluacionfichaperfil.command.primaryadapter.web.dto.RegistrarEvaluacionFichaPerfilResponseDTO;

public final class RegistrarEvaluacionFichaPerfilResponseMapper {

    private RegistrarEvaluacionFichaPerfilResponseMapper() {}

    public static RegistrarEvaluacionFichaPerfilResponseDTO toResponse(InicioEvaluacionFichaPerfilResult result) {
        return switch (result) {
            case InicioEvaluacionFichaPerfilResult.Registrada registrada ->
                    new RegistrarEvaluacionFichaPerfilResponseDTO(registrada.evaluacion());
            case InicioEvaluacionFichaPerfilResult.Reactivada reactivada ->
                    new RegistrarEvaluacionFichaPerfilResponseDTO(reactivada.evaluacion());
        };
    }
}
