package com.arquisoft.fichas.application.estudiantefichaperfil.command;

import com.arquisoft.fichas.application.estudiantefichaperfil.command.model.AsignarEstudiantesFichaPerfilCommand;
import com.arquisoft.fichas.application.estudiantefichaperfil.command.port.in.AsignarEstudiantesFichaPerfilUseCase;
import com.arquisoft.fichas.application.estudiantefichaperfil.command.validator.AsignarEstudiantesFichaPerfilValidator;
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
public class AsignarEstudiantesFichaPerfilUseCaseImpl implements AsignarEstudiantesFichaPerfilUseCase {

    private final EstudianteFichaPerfilOutputPort estudianteFichaPerfilOutputPort;
    private final AsignarEstudiantesFichaPerfilValidator asignarEstudiantesFichaPerfilValidator;
    private final AppLogger logger;

    @Override
    public void ejecutar(AsignarEstudiantesFichaPerfilCommand entrada) {
        UUID fichaPerfil = entrada.fichaPerfil();
        List<UUID> estudiantes = entrada.estudiantes();

        List<EstudianteFichaPerfilAggregate> relaciones =
                EstudianteFichaPerfilAggregate.crear(fichaPerfil, estudiantes);

        asignarEstudiantesFichaPerfilValidator.validar(fichaPerfil, estudiantes, relaciones);

        relaciones.forEach(estudianteFichaPerfilOutputPort::guardar);

        logger.info(FichasMessages.EstudianteFichaPerfil.LOG_ASIGNADO, fichaPerfil, relaciones.size());
    }
}
