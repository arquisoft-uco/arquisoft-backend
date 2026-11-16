package com.arquisoft.biblioteca.application.estudianteproyectogrado.command.usecase.impl;

import com.arquisoft.biblioteca.application.estudianteproyectogrado.command.finder.VinculosDeProyectoGradoFinder;
import com.arquisoft.biblioteca.application.estudianteproyectogrado.command.result.AgregacionEstudiantesProyectoGradoResult;
import com.arquisoft.biblioteca.application.estudianteproyectogrado.command.result.mapper.AgregacionEstudiantesProyectoGradoResultMapper;
import com.arquisoft.biblioteca.application.estudianteproyectogrado.command.secondaryport.EstudianteProyectoGradoOutputPort;
import com.arquisoft.biblioteca.application.estudianteproyectogrado.command.secondaryport.entity.EstudianteProyectoGradoEntity;
import com.arquisoft.biblioteca.application.estudianteproyectogrado.command.secondaryport.mapper.EstudianteProyectoGradoMapper;
import com.arquisoft.biblioteca.application.estudianteproyectogrado.command.usecase.AgregarEstudiantesProyectoGradoUseCase;
import com.arquisoft.biblioteca.application.proyectogrado.command.usecase.AgregarProyectoGradoUseCase;
import com.arquisoft.biblioteca.domain.estudianteproyectogrado.AgregacionEstudiantesProyectoGradoDomain;
import com.arquisoft.biblioteca.domain.estudianteproyectogrado.EstudianteProyectoGradoDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.biblioteca.EstudianteProyectoGradoKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class AgregarEstudiantesProyectoGradoUseCaseImpl implements AgregarEstudiantesProyectoGradoUseCase {

    private final AgregarProyectoGradoUseCase agregarProyectoGradoUseCase;
    private final EstudianteProyectoGradoOutputPort estudianteProyectoGradoOutputPort;
    private final VinculosDeProyectoGradoFinder vinculosDeProyectoGradoFinder;
    private final AppLogger logger;

    @Override
    public AgregacionEstudiantesProyectoGradoResult ejecutar(AgregacionEstudiantesProyectoGradoDomain entrada) {
        agregarProyectoGradoUseCase.ejecutar(entrada.getProyecto());

        var proyectoGrado = entrada.getProyecto().getId();
        var existentes = vinculosDeProyectoGradoFinder.obtener(proyectoGrado).stream()
                .collect(Collectors.toMap(EstudianteProyectoGradoDomain::getAutor, Function.identity()));
        logger.debug(EstudianteProyectoGradoKey.LOG_VERIFICACION_AGREGAR, proyectoGrado, existentes.size());

        var paraGuardar = new ArrayList<EstudianteProyectoGradoEntity>();
        var agregados = 0;
        var reactivados = 0;
        var duplicados = 0;
        var descartados = 0;

        for (var vinculo : entrada.getVinculos()) {
            var existente = existentes.getOrDefault(vinculo.getAutor(), EstudianteProyectoGradoDomain.VACIO);

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
                proyectoGrado, agregados, reactivados, duplicados, descartados);
    }
}
