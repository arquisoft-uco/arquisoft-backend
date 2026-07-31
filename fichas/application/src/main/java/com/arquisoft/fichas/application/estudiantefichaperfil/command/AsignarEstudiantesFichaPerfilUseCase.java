package com.arquisoft.fichas.application.estudiantefichaperfil.command;

import com.arquisoft.fichas.application.estudiantefichaperfil.command.model.AsignarEstudiantesFichaPerfilCommand;
import com.arquisoft.fichas.application.estudiantefichaperfil.command.validator.EstudiantesFichaValidator;
import com.arquisoft.fichas.application.fichaperfil.command.validator.FichaPerfilValidator;
import com.arquisoft.fichas.domain.estudiantefichaperfil.aggregate.EstudianteFichaPerfilAggregate;
import com.arquisoft.fichas.domain.estudiantefichaperfil.port.out.EstudianteFichaPerfilOutputPort;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.FichasMessages;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AsignarEstudiantesFichaPerfilUseCase {

    private final EstudianteFichaPerfilOutputPort estudianteFichaPerfilOutputPort;
    private final FichaPerfilValidator fichaPerfilValidator;
    private final EstudiantesFichaValidator estudiantesFichaValidator;
    private final AppLogger logger;

    public void ejecutar(AsignarEstudiantesFichaPerfilCommand command) {
        UUID fichaPerfil = command.fichaPerfil();
        List<UUID> estudiantes = command.estudiantes();

        List<EstudianteFichaPerfilAggregate> relaciones =
                EstudianteFichaPerfilAggregate.crear(fichaPerfil, estudiantes);
        estudiantesFichaValidator.validarSinDuplicados(estudiantes);

        fichaPerfilValidator.validarFichaExiste(fichaPerfil);
        estudiantesFichaValidator.validarExistencia(estudiantes);
        estudiantesFichaValidator.validarNoVinculados(fichaPerfil, estudiantes);

        EstudianteFichaPerfilAggregate.validarCupoDisponible(
                relaciones.size(),
                estudianteFichaPerfilOutputPort.contarPorFichaPerfilId(fichaPerfil));

        relaciones.forEach(estudianteFichaPerfilOutputPort::guardar);

        logger.info(FichasMessages.EstudianteFichaPerfil.LOG_ASIGNADO, fichaPerfil, relaciones.size());
    }
}
