package com.arquisoft.fichas.application.estudiantefichaperfil.command.usecase.impl;

import com.arquisoft.shared.message.key.fichas.EstudianteFichaPerfilKey;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.fichas.application.estudiante.command.finder.EstudiantesExistentesFinder;
import com.arquisoft.fichas.application.estudiante.command.finder.EstudiantesFinder;
import com.arquisoft.fichas.application.estudiantefichaperfil.command.finder.EstudiantesVinculadosContadorFinder;
import com.arquisoft.fichas.application.estudiantefichaperfil.command.finder.EstudiantesYaVinculadosFinder;
import com.arquisoft.fichas.application.estudiantefichaperfil.command.usecase.AsignarEstudiantesFichaPerfilUseCase;
import com.arquisoft.fichas.application.estudiantefichaperfil.command.validator.AsignarEstudiantesFichaPerfilValidator;
import com.arquisoft.fichas.application.fichaperfil.command.finder.FichaPerfilFinder;
import com.arquisoft.fichas.domain.estudiante.EstudianteDomain;
import com.arquisoft.fichas.domain.estudiantefichaperfil.AgregacionEstudiantesFichaPerfilDomain;
import com.arquisoft.fichas.domain.estudiantefichaperfil.event.EstudiantesFichaPerfilAsignadosEvent;
import com.arquisoft.fichas.domain.estudiantefichaperfil.model.ContactoEstudiante;
import com.arquisoft.fichas.domain.fichaperfil.FichaPerfilDomain;
import com.arquisoft.fichas.application.estudiantefichaperfil.command.secondaryport.EstudianteFichaPerfilOutputPort;
import com.arquisoft.fichas.application.estudiantefichaperfil.command.secondaryport.mapper.EstudianteFichaPerfilMapper;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.publisher.EventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AsignarEstudiantesFichaPerfilUseCaseImpl implements AsignarEstudiantesFichaPerfilUseCase {

    private final EstudianteFichaPerfilOutputPort estudianteFichaPerfilOutputPort;
    private final FichaPerfilFinder fichaPerfilFinder;
    private final EstudiantesExistentesFinder estudiantesExistentesFinder;
    private final EstudiantesFinder estudiantesFinder;
    private final EstudiantesYaVinculadosFinder estudiantesYaVinculadosFinder;
    private final EstudiantesVinculadosContadorFinder estudiantesVinculadosContadorFinder;
    private final AsignarEstudiantesFichaPerfilValidator asignarEstudiantesFichaPerfilValidator;
    private final EventPublisher eventPublisher;
    private final AppLogger logger;

    @Override
    public void ejecutar(AgregacionEstudiantesFichaPerfilDomain entrada) {
        logger.info(Mensajes.obtener(EstudianteFichaPerfilKey.LOG_ASIGNANDO),
                entrada.getFichaPerfil(), entrada.getCantidad());

        var ficha = fichaPerfilFinder.obtener(entrada.getFichaPerfil()).orElse(FichaPerfilDomain.VACIO);
        List<UUID> estudiantesExistentes = estudiantesExistentesFinder.obtener(entrada.getEstudiantes());
        List<UUID> yaVinculados = estudiantesYaVinculadosFinder.obtener(entrada.getRelaciones());
        long vinculadosActuales = estudiantesVinculadosContadorFinder.obtener(entrada.getFichaPerfil());

        logger.debug(Mensajes.obtener(EstudianteFichaPerfilKey.LOG_VERIFICACION_ASIGNAR),
                !ficha.esVacio(), estudiantesExistentes.size(), yaVinculados.size(), vinculadosActuales);

        asignarEstudiantesFichaPerfilValidator.validar(
                entrada, ficha, estudiantesExistentes, yaVinculados, vinculadosActuales);

        entrada.getRelaciones().stream()
                .map(EstudianteFichaPerfilMapper::toEntity)
                .forEach(estudianteFichaPerfilOutputPort::vincularEstudiante);

        eventPublisher.publish(new EstudiantesFichaPerfilAsignadosEvent(
                ficha.getId(),
                ficha.getTituloProyecto(),
                contactos(entrada.getEstudiantes())));

        logger.info(Mensajes.obtener(EstudianteFichaPerfilKey.LOG_ASIGNADO),
                entrada.getFichaPerfil(), entrada.getCantidad());
    }

    private List<ContactoEstudiante> contactos(List<UUID> estudiantes) {
        return estudiantesFinder.obtener(estudiantes).stream()
                .map(AsignarEstudiantesFichaPerfilUseCaseImpl::aContacto)
                .toList();
    }

    private static ContactoEstudiante aContacto(EstudianteDomain estudiante) {
        return new ContactoEstudiante(estudiante.getNombre(), estudiante.getEmail());
    }
}
