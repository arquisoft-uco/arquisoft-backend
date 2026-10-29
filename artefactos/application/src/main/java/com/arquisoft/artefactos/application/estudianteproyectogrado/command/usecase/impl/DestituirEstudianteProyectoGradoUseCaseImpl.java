package com.arquisoft.artefactos.application.estudianteproyectogrado.command.usecase.impl;

import com.arquisoft.artefactos.application.estudianteartefacto.command.usecase.DesvincularEstudianteArtefactosUseCase;
import com.arquisoft.artefactos.application.estudianteproyectogrado.command.finder.EstudianteProyectoGradoPorVinculoFinder;
import com.arquisoft.artefactos.application.estudianteproyectogrado.command.finder.model.VinculoEstudianteProyectoGrado;
import com.arquisoft.artefactos.application.estudianteproyectogrado.command.result.DestitucionEstudianteProyectoGradoResult;
import com.arquisoft.artefactos.application.estudianteproyectogrado.command.result.mapper.DestitucionEstudianteProyectoGradoResultMapper;
import com.arquisoft.artefactos.application.estudianteproyectogrado.command.secondaryport.EstudianteProyectoGradoOutputPort;
import com.arquisoft.artefactos.application.estudianteproyectogrado.command.secondaryport.mapper.EstudianteProyectoGradoMapper;
import com.arquisoft.artefactos.application.estudianteproyectogrado.command.usecase.DestituirEstudianteProyectoGradoUseCase;
import com.arquisoft.artefactos.domain.estudianteproyectogrado.DestitucionEstudianteProyectoGradoDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.artefactos.EstudianteProyectoGradoKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DestituirEstudianteProyectoGradoUseCaseImpl implements DestituirEstudianteProyectoGradoUseCase {

    private final EstudianteProyectoGradoOutputPort estudianteProyectoGradoOutputPort;
    private final EstudianteProyectoGradoPorVinculoFinder estudianteProyectoGradoPorVinculoFinder;
    private final DesvincularEstudianteArtefactosUseCase desvincularEstudianteArtefactosUseCase;
    private final AppLogger logger;

    @Override
    public DestitucionEstudianteProyectoGradoResult ejecutar(DestitucionEstudianteProyectoGradoDomain entrada) {
        var vinculo = entrada.getVinculo();
        var existente = estudianteProyectoGradoPorVinculoFinder.obtener(
                new VinculoEstudianteProyectoGrado(vinculo.getEstudiante(), vinculo.getProyectoGrado()));
        logger.debug(EstudianteProyectoGradoKey.LOG_VERIFICACION_DESTITUIR,
                vinculo.getProyectoGrado(), vinculo.getEstudiante(), !existente.esVacio());

        if (existente.esVacio()) {
            vinculo.destituir(vinculo.getOcurridoEn());
            estudianteProyectoGradoOutputPort.guardar(EstudianteProyectoGradoMapper.toEntity(vinculo));
            desvincularEstudianteArtefactosUseCase.ejecutar(entrada.getDesvinculacion());
            return DestitucionEstudianteProyectoGradoResultMapper.toResultLapida(vinculo);
        }
        if (!vinculo.getOcurridoEn().isAfter(existente.getOcurridoEn())) {
            return DestitucionEstudianteProyectoGradoResultMapper.toResultDescartada(
                    vinculo, existente.getOcurridoEn());
        }

        existente.destituir(vinculo.getOcurridoEn());
        estudianteProyectoGradoOutputPort.guardar(EstudianteProyectoGradoMapper.toEntity(existente));
        desvincularEstudianteArtefactosUseCase.ejecutar(entrada.getDesvinculacion());
        return DestitucionEstudianteProyectoGradoResultMapper.toResultDestituida(existente);
    }
}
