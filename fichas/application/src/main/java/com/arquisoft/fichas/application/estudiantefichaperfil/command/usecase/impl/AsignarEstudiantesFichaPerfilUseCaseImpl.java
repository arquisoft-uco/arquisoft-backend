package com.arquisoft.fichas.application.estudiantefichaperfil.command.usecase.impl;

import com.arquisoft.shared.message.MessageCatalog;
import com.arquisoft.shared.message.FichasKeys;
import com.arquisoft.fichas.application.estudiantefichaperfil.command.usecase.AsignarEstudiantesFichaPerfilUseCase;
import com.arquisoft.fichas.application.estudiantefichaperfil.command.validator.AsignarEstudiantesFichaPerfilValidator;
import com.arquisoft.fichas.domain.estudiantefichaperfil.aggregate.EstudianteFichaPerfilDomain;
import com.arquisoft.fichas.domain.estudiantefichaperfil.port.out.EstudianteFichaPerfilOutputPort;
import com.arquisoft.shared.logger.AppLogger;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AsignarEstudiantesFichaPerfilUseCaseImpl implements AsignarEstudiantesFichaPerfilUseCase {

    private final EstudianteFichaPerfilOutputPort estudianteFichaPerfilOutputPort;
    private final AsignarEstudiantesFichaPerfilValidator asignarEstudiantesFichaPerfilValidator;
    private final AppLogger logger;
    private final MessageCatalog catalog;

    @Override
    public void ejecutar(List<EstudianteFichaPerfilDomain> relaciones) {
        UUID fichaPerfil = relaciones.getFirst().getFichaPerfilId();
        List<UUID> estudiantes = relaciones.stream().map(EstudianteFichaPerfilDomain::getEstudianteId).toList();

        asignarEstudiantesFichaPerfilValidator.validar(fichaPerfil, estudiantes, relaciones);

        relaciones.forEach(estudianteFichaPerfilOutputPort::vincularEstudiante);

        logger.info(catalog.obtener(FichasKeys.EstudianteFichaPerfil.LOG_ASIGNADO), fichaPerfil, relaciones.size());
    }
}
