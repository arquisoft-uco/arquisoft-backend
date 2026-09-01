package com.arquisoft.fichas.application.fichaperfil.command.usecase.impl;

import com.arquisoft.shared.message.key.fichas.FichaPerfilKey;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.fichas.application.asesorficha.command.finder.AsesorFichaFinder;
import com.arquisoft.fichas.application.estadofichaperfil.command.usecase.AsignarEstadoInicialFichaPerfilUseCase;
import com.arquisoft.fichas.application.estudiantefichaperfil.command.usecase.AsignarEstudiantesFichaPerfilUseCase;
import com.arquisoft.fichas.application.fichaperfil.command.finder.TituloFichaPerfilExisteFinder;
import com.arquisoft.fichas.application.fichaperfil.command.usecase.RegistrarFichaPerfilUseCase;
import com.arquisoft.fichas.application.fichaperfil.command.validator.RegistrarFichaPerfilValidator;
import com.arquisoft.fichas.domain.asesorficha.AsesorFichaDomain;
import com.arquisoft.fichas.domain.asesorficha.model.ContactoAsesor;
import com.arquisoft.fichas.domain.fichaperfil.RegistroFichaPerfilDomain;
import com.arquisoft.fichas.domain.fichaperfil.event.FichaPerfilRegistradaEvent;
import com.arquisoft.fichas.application.fichaperfil.command.secondaryport.FichaPerfilOutputPort;
import com.arquisoft.fichas.application.fichaperfil.command.secondaryport.mapper.FichaPerfilMapper;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.publisher.EventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RegistrarFichaPerfilUseCaseImpl implements RegistrarFichaPerfilUseCase {

    private final FichaPerfilOutputPort fichaPerfilOutputPort;
    private final AsesorFichaFinder asesorFichaFinder;
    private final TituloFichaPerfilExisteFinder tituloFichaPerfilExisteFinder;
    private final RegistrarFichaPerfilValidator registrarFichaPerfilValidator;
    private final AsignarEstadoInicialFichaPerfilUseCase asignarEstadoInicialFichaPerfilUseCase;
    private final AsignarEstudiantesFichaPerfilUseCase asignarEstudiantesFichaPerfilUseCase;
    private final EventPublisher eventPublisher;
    private final AppLogger logger;

    @Override
    public UUID ejecutar(RegistroFichaPerfilDomain registro) {
        var ficha = registro.getFicha();

        logger.info(Mensajes.obtener(FichaPerfilKey.LOG_REGISTRANDO),
                ficha.getTituloProyecto(), ficha.getAsesorFicha());

        var asesorFicha = asesorFichaFinder.obtener(ficha.getAsesorFicha())
                .orElse(AsesorFichaDomain.VACIO);
        boolean tituloYaExiste = tituloFichaPerfilExisteFinder.obtener(ficha.getTituloProyecto());

        logger.debug(Mensajes.obtener(FichaPerfilKey.LOG_VERIFICACION_PREVIA),
                ficha.getAsesorFicha(), !asesorFicha.esVacio(), tituloYaExiste);

        registrarFichaPerfilValidator.validar(ficha, !asesorFicha.esVacio(), tituloYaExiste);

        logger.debug(Mensajes.obtener(FichaPerfilKey.LOG_VALIDACION_SUPERADA), ficha.getId());

        fichaPerfilOutputPort.registrarFicha(FichaPerfilMapper.toEntity(ficha));

        logger.debug(Mensajes.obtener(FichaPerfilKey.LOG_REGISTRADA), ficha.getId());

        asignarEstadoInicialFichaPerfilUseCase.ejecutar(registro.getEstadoInicial());

        asignarEstudiantesFichaPerfilUseCase.ejecutar(registro.getEstudiantes());

        eventPublisher.publish(new FichaPerfilRegistradaEvent(
                ficha.getId(),
                ficha.getTituloProyecto(),
                asesorFicha.getId(),
                new ContactoAsesor(asesorFicha.getNombre(), asesorFicha.getEmail())));

        return ficha.getId();
    }
}
