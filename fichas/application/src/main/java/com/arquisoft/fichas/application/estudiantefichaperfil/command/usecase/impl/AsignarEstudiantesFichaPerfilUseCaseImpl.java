package com.arquisoft.fichas.application.estudiantefichaperfil.command.usecase.impl;

import com.arquisoft.shared.message.key.fichas.EstudianteFichaPerfilKey;
import com.arquisoft.shared.message.CatalogoMensajes;
import com.arquisoft.fichas.application.estudiante.command.finder.EstudiantesExistentesFinder;
import com.arquisoft.fichas.application.estudiantefichaperfil.command.finder.EstudiantesVinculadosContadorFinder;
import com.arquisoft.fichas.application.estudiantefichaperfil.command.finder.EstudiantesYaVinculadosFinder;
import com.arquisoft.fichas.application.estudiantefichaperfil.command.usecase.AsignarEstudiantesFichaPerfilUseCase;
import com.arquisoft.fichas.application.estudiantefichaperfil.command.validator.AsignarEstudiantesFichaPerfilValidator;
import com.arquisoft.fichas.application.fichaperfil.command.finder.FichaPerfilExisteFinder;
import com.arquisoft.fichas.domain.estudiantefichaperfil.AgregacionEstudiantesFichaPerfilDomain;
import com.arquisoft.fichas.application.estudiantefichaperfil.command.secondaryport.EstudianteFichaPerfilOutputPort;
import com.arquisoft.fichas.application.estudiantefichaperfil.command.secondaryport.mapper.EstudianteFichaPerfilMapper;
import com.arquisoft.shared.logger.AppLogger;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AsignarEstudiantesFichaPerfilUseCaseImpl implements AsignarEstudiantesFichaPerfilUseCase {

    private final EstudianteFichaPerfilOutputPort estudianteFichaPerfilOutputPort;
    private final FichaPerfilExisteFinder fichaPerfilExisteFinder;
    private final EstudiantesExistentesFinder estudiantesExistentesFinder;
    private final EstudiantesYaVinculadosFinder estudiantesYaVinculadosFinder;
    private final EstudiantesVinculadosContadorFinder estudiantesVinculadosContadorFinder;
    private final AsignarEstudiantesFichaPerfilValidator asignarEstudiantesFichaPerfilValidator;
    private final AppLogger logger;
    private final CatalogoMensajes catalogo;

    @Override
    public void ejecutar(AgregacionEstudiantesFichaPerfilDomain entrada) {
        boolean fichaExiste = fichaPerfilExisteFinder.obtener(entrada.getFichaPerfil());
        List<UUID> estudiantesExistentes = estudiantesExistentesFinder.obtener(entrada.getEstudiantes());
        List<UUID> yaVinculados = estudiantesYaVinculadosFinder.obtener(entrada.getRelaciones());
        long vinculadosActuales = estudiantesVinculadosContadorFinder.obtener(entrada.getFichaPerfil());

        asignarEstudiantesFichaPerfilValidator.validar(
                entrada, fichaExiste, estudiantesExistentes, yaVinculados, vinculadosActuales);

        entrada.getRelaciones().stream()
                .map(EstudianteFichaPerfilMapper::toEntity)
                .forEach(estudianteFichaPerfilOutputPort::vincularEstudiante);

        logger.info(catalogo.obtener(EstudianteFichaPerfilKey.LOG_ASIGNADO),
                entrada.getFichaPerfil(), entrada.getCantidad());
    }
}
