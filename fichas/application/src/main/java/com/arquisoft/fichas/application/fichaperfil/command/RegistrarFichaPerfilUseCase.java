package com.arquisoft.fichas.application.fichaperfil.command;

import com.arquisoft.fichas.application.estudiantefichaperfil.command.validator.EstudiantesFichaValidator;
import com.arquisoft.fichas.application.fichaperfil.command.model.RegistrarFichaPerfilCommand;
import com.arquisoft.fichas.application.fichaperfil.command.validator.FichaPerfilValidator;
import com.arquisoft.fichas.domain.estadofichaperfil.aggregate.EstadoFichaPerfilAggregate;
import com.arquisoft.fichas.domain.estadofichaperfil.port.out.EstadoFichaPerfilOutputPort;
import com.arquisoft.fichas.domain.estudiantefichaperfil.aggregate.EstudianteFichaPerfilAggregate;
import com.arquisoft.fichas.domain.estudiantefichaperfil.port.out.EstudianteFichaPerfilOutputPort;
import com.arquisoft.fichas.domain.fichaperfil.aggregate.FichaPerfilAggregate;
import com.arquisoft.fichas.domain.fichaperfil.port.out.FichaPerfilOutputPort;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.FichasMessages;
import com.arquisoft.shared.util.UtilCollection;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RegistrarFichaPerfilUseCase {

    private static final long SIN_ESTUDIANTES_PREVIOS = 0L;

    private final FichaPerfilOutputPort fichaPerfilOutputPort;
    private final EstudianteFichaPerfilOutputPort estudianteFichaPerfilOutputPort;
    private final EstadoFichaPerfilOutputPort estadoFichaPerfilOutputPort;
    private final FichaPerfilValidator fichaPerfilValidator;
    private final EstudiantesFichaValidator estudiantesFichaValidator;
    private final AppLogger logger;

    public UUID ejecutar(RegistrarFichaPerfilCommand command) {
        List<UUID> estudiantes = command.estudiantes();

        estudiantesFichaValidator.validarSinDuplicados(estudiantes);

        fichaPerfilValidator.validarAsesorExiste(command.asesorFicha());
        fichaPerfilValidator.validarTituloUnico(command.tituloProyecto());
        estudiantesFichaValidator.validarExistencia(estudiantes);

        FichaPerfilAggregate ficha = FichaPerfilAggregate.crear(
                command.tituloProyecto(),
                command.asesorFicha()
        );

        fichaPerfilOutputPort.guardar(ficha);
        asignarEstadoInicial(ficha.getId());
        vincularEstudiantes(ficha.getId(), estudiantes);

        logger.info(FichasMessages.FichaPerfil.LOG_REGISTRADA, ficha.getId());
        return ficha.getId();
    }

    private void asignarEstadoInicial(UUID fichaPerfil) {
        var estadoInicial = EstadoFichaPerfilAggregate.crear(fichaPerfil);
        estadoFichaPerfilOutputPort.guardar(estadoInicial);
        logger.info(FichasMessages.EstadoFichaPerfil.LOG_CREADO,
                estadoInicial.getId(),
                estadoInicial.getFichaPerfilId(),
                estadoInicial.getEstadoFicha().getNombre());
    }

    private void vincularEstudiantes(UUID fichaPerfil, List<UUID> estudiantes) {
        if (UtilCollection.isEmptyOrNull(estudiantes)) {
            return;
        }
        var relaciones = EstudianteFichaPerfilAggregate.crear(fichaPerfil, estudiantes);
        EstudianteFichaPerfilAggregate.validarCupoDisponible(relaciones.size(), SIN_ESTUDIANTES_PREVIOS);
        relaciones.forEach(estudianteFichaPerfilOutputPort::guardar);
    }
}
