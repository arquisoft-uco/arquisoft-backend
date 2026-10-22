package com.arquisoft.evaluaciones.application.estudianteproyectogrado.command.usecase.impl;

import com.arquisoft.evaluaciones.application.estudianteproyectogrado.command.finder.VinculosDeProyectoGradoFinder;
import com.arquisoft.evaluaciones.application.estudianteproyectogrado.command.result.AgregacionEstudiantesProyectoGradoResult;
import com.arquisoft.evaluaciones.application.estudianteproyectogrado.command.result.mapper.AgregacionEstudiantesProyectoGradoResultMapper;
import com.arquisoft.evaluaciones.application.estudianteproyectogrado.command.secondaryport.EstudianteProyectoGradoOutputPort;
import com.arquisoft.evaluaciones.application.estudianteproyectogrado.command.secondaryport.entity.EstudianteProyectoGradoEntity;
import com.arquisoft.evaluaciones.application.estudianteproyectogrado.command.secondaryport.mapper.EstudianteProyectoGradoMapper;
import com.arquisoft.evaluaciones.application.estudianteproyectogrado.command.usecase.AgregarEstudiantesProyectoGradoUseCase;
import com.arquisoft.evaluaciones.domain.estudianteproyectogrado.AgregacionEstudiantesProyectoGradoDomain;
import com.arquisoft.evaluaciones.domain.estudianteproyectogrado.EstudianteProyectoGradoDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.evaluaciones.EstudianteProyectoGradoKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class AgregarEstudiantesProyectoGradoUseCaseImpl implements AgregarEstudiantesProyectoGradoUseCase {

    private final EstudianteProyectoGradoOutputPort estudianteProyectoGradoOutputPort;
    private final VinculosDeProyectoGradoFinder vinculosDeProyectoGradoFinder;
    private final AppLogger logger;

    @Override
    public AgregacionEstudiantesProyectoGradoResult ejecutar(AgregacionEstudiantesProyectoGradoDomain entrada) {
        var existentes = vinculosDeProyectoGradoFinder.obtener(entrada.getProyectoGrado()).stream()
                .collect(Collectors.toMap(EstudianteProyectoGradoDomain::getEstudiante, Function.identity()));
        logger.debug(EstudianteProyectoGradoKey.LOG_VERIFICACION_AGREGAR,
                entrada.getProyectoGrado(), existentes.size());

        var paraGuardar = new ArrayList<EstudianteProyectoGradoEntity>();
        var agregados = 0;
        var reactivados = 0;
        var duplicados = 0;
        var descartados = 0;

        for (var vinculo : entrada.getVinculos()) {
            var existente = existentes.getOrDefault(vinculo.getEstudiante(), EstudianteProyectoGradoDomain.VACIO);

            if (existente.esVacio()) {
                paraGuardar.add(EstudianteProyectoGradoMapper.toEntity(vinculo));
                agregados++;
            } else if (!vinculo.getOcurridoEn().isAfter(existente.getOcurridoEn())) {
                descartados++;
            } else if (!existente.estaEliminado()) {
                duplicados++;
            } else {
                existente.reactivar(vinculo.getOcurridoEn());
                paraGuardar.add(EstudianteProyectoGradoMapper.toEntity(existente));
                reactivados++;
            }
        }

        estudianteProyectoGradoOutputPort.guardarTodos(paraGuardar);
        return AgregacionEstudiantesProyectoGradoResultMapper.toResult(
                entrada.getProyectoGrado(), agregados, reactivados, duplicados, descartados);
    }
}
