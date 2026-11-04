package com.arquisoft.mapas_ruta.application.tarea.command.usecase.impl;

import com.arquisoft.mapas_ruta.application.asignacionasesor.command.finder.AsesorAsignadoProyectoGradoFinder;
import com.arquisoft.mapas_ruta.application.asignacionasesor.command.finder.model.AsesorEnProyectoGrado;
import com.arquisoft.mapas_ruta.application.maparuta.command.finder.MapaRutaPorIdFinder;
import com.arquisoft.mapas_ruta.application.proyectogrado.command.finder.ProyectoGradoPorIdFinder;
import com.arquisoft.mapas_ruta.application.tarea.command.finder.TareaDeMapaRutaExisteFinder;
import com.arquisoft.mapas_ruta.application.tarea.command.secondaryport.TareaOutputPort;
import com.arquisoft.mapas_ruta.application.tarea.command.secondaryport.mapper.TareaMapper;
import com.arquisoft.mapas_ruta.application.tarea.command.usecase.AsignarTareaUseCase;
import com.arquisoft.mapas_ruta.application.tarea.command.validator.AsignarTareaValidator;
import com.arquisoft.mapas_ruta.domain.proyectogrado.ProyectoGradoDomain;
import com.arquisoft.mapas_ruta.domain.tarea.TareaDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.mapas_ruta.TareaKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AsignarTareaUseCaseImpl implements AsignarTareaUseCase {

    private final TareaOutputPort tareaOutputPort;
    private final MapaRutaPorIdFinder mapaRutaPorIdFinder;
    private final ProyectoGradoPorIdFinder proyectoGradoPorIdFinder;
    private final TareaDeMapaRutaExisteFinder tareaDeMapaRutaExisteFinder;
    private final AsesorAsignadoProyectoGradoFinder asesorAsignadoProyectoGradoFinder;
    private final AsignarTareaValidator asignarTareaValidator;
    private final AppLogger logger;

    @Override
    public UUID ejecutar(TareaDomain entrada) {
        logger.info(TareaKey.LOG_ASIGNANDO, entrada.getMapaRuta(), entrada.getAsesor());

        var mapaRuta = mapaRutaPorIdFinder.obtener(entrada.getMapaRuta());
        var mapaRutaExiste = !mapaRuta.esVacio();
        var proyectoGrado = mapaRutaExiste
                ? proyectoGradoPorIdFinder.obtener(mapaRuta.getProyectoGrado())
                : ProyectoGradoDomain.VACIO;
        var tareaExiste = mapaRutaExiste && tareaDeMapaRutaExisteFinder.obtener(entrada);
        var asesorAsignado = mapaRutaExiste && asesorAsignadoProyectoGradoFinder.obtener(
                new AsesorEnProyectoGrado(mapaRuta.getProyectoGrado(), entrada.getAsesor()));

        logger.debug(TareaKey.LOG_VERIFICACION_ASIGNAR,
                mapaRutaExiste, !proyectoGrado.esVacio(), tareaExiste, asesorAsignado);
        asignarTareaValidator.validar(entrada, mapaRuta, proyectoGrado, tareaExiste, asesorAsignado);

        tareaOutputPort.registrar(TareaMapper.toEntity(entrada));

        logger.info(TareaKey.LOG_ASIGNADA, entrada.getId(), entrada.getMapaRuta());
        return entrada.getId();
    }
}
