package com.arquisoft.fichas.application.estudiantefichaperfil.command.usecase.impl;

import com.arquisoft.shared.message.key.fichas.EstudianteFichaPerfilKey;
import com.arquisoft.shared.message.CatalogoMensajes;
import com.arquisoft.fichas.application.estudiantefichaperfil.command.usecase.AsignarEstudiantesFichaPerfilUseCase;
import com.arquisoft.fichas.application.estudiantefichaperfil.command.validator.AsignarEstudiantesFichaPerfilValidator;
import com.arquisoft.fichas.domain.estudiantefichaperfil.EstudianteFichaPerfilDomain;
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
    private final CatalogoMensajes catalogo;

    @Override
    public void ejecutar(List<EstudianteFichaPerfilDomain> relaciones) {
        UUID fichaPerfil = relaciones.getFirst().getFichaPerfilId();
        List<UUID> estudiantes = relaciones.stream().map(EstudianteFichaPerfilDomain::getEstudianteId).toList();

        asignarEstudiantesFichaPerfilValidator.validar(fichaPerfil, estudiantes, relaciones);

        relaciones.forEach(estudianteFichaPerfilOutputPort::vincularEstudiante);

        logger.info(catalogo.obtener(EstudianteFichaPerfilKey.LOG_ASIGNADO), fichaPerfil, relaciones.size());
    }
}
