package com.arquisoft.artefactos.application.estudianteproyectogrado.command.usecase.impl;

import com.arquisoft.artefactos.application.estudianteproyectogrado.command.finder.EstudiantesProyectoGradoPorVinculosFinder;
import com.arquisoft.artefactos.application.estudianteproyectogrado.command.result.AgregacionEstudiantesProyectoGradoResult;
import com.arquisoft.artefactos.application.estudianteproyectogrado.command.result.mapper.AgregacionEstudiantesProyectoGradoResultMapper;
import com.arquisoft.artefactos.application.estudianteproyectogrado.command.secondaryport.EstudianteProyectoGradoOutputPort;
import com.arquisoft.artefactos.application.estudianteproyectogrado.command.secondaryport.mapper.EstudianteProyectoGradoMapper;
import com.arquisoft.artefactos.application.estudianteproyectogrado.command.usecase.AgregarEstudiantesProyectoGradoUseCase;
import com.arquisoft.artefactos.domain.estudianteproyectogrado.AgregacionEstudiantesProyectoGradoDomain;
import com.arquisoft.artefactos.domain.estudianteproyectogrado.EstudianteProyectoGradoDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.artefactos.EstudianteProyectoGradoKey;
import com.arquisoft.shared.util.UtilObjeto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class AgregarEstudiantesProyectoGradoUseCaseImpl implements AgregarEstudiantesProyectoGradoUseCase {

    private final EstudianteProyectoGradoOutputPort estudianteProyectoGradoOutputPort;
    private final EstudiantesProyectoGradoPorVinculosFinder estudiantesProyectoGradoPorVinculosFinder;
    private final AppLogger logger;

    @Override
    public AgregacionEstudiantesProyectoGradoResult ejecutar(AgregacionEstudiantesProyectoGradoDomain entrada) {
        var existentes = estudiantesProyectoGradoPorVinculosFinder.obtener(entrada);
        logger.debug(EstudianteProyectoGradoKey.LOG_VERIFICACION_AGREGAR,
                entrada.getProyectoGrado(), entrada.getVinculos().size(), existentes.size());

        var existentesPorEstudiante = existentes.stream()
                .collect(Collectors.toMap(EstudianteProyectoGradoDomain::getEstudiante, Function.identity()));
        var porGuardar = new ArrayList<EstudianteProyectoGradoDomain>();
        var agregados = 0;
        var reactivados = 0;
        var duplicados = 0;
        var descartados = 0;

        for (var vinculo : entrada.getVinculos()) {
            var existente = existentesPorEstudiante.get(vinculo.getEstudiante());
            if (UtilObjeto.esNulo(existente)) {
                porGuardar.add(vinculo);
                agregados++;
            } else if (!vinculo.getOcurridoEn().isAfter(existente.getOcurridoEn())) {
                descartados++;
            } else if (!existente.estaDestituido()) {
                existente.reactivar(vinculo.getOcurridoEn());
                porGuardar.add(existente);
                duplicados++;
            } else {
                existente.reactivar(vinculo.getOcurridoEn());
                porGuardar.add(existente);
                reactivados++;
            }
        }

        estudianteProyectoGradoOutputPort.guardarTodos(
                porGuardar.stream().map(EstudianteProyectoGradoMapper::toEntity).toList());
        return AgregacionEstudiantesProyectoGradoResultMapper.toResult(
                entrada, agregados, reactivados, duplicados, descartados);
    }
}
