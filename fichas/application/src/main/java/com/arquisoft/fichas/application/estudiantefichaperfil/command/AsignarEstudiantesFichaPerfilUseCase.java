package com.arquisoft.fichas.application.estudiantefichaperfil.command;

import com.arquisoft.fichas.application.estudiante.query.port.out.EstudianteQueryOutputPort;
import com.arquisoft.fichas.application.estudiantefichaperfil.command.model.AsignarEstudiantesFichaPerfilCommand;
import com.arquisoft.fichas.application.estudiantefichaperfil.command.port.in.AsignarEstudiantesFichaPerfilInputPort;
import com.arquisoft.fichas.application.estudiantefichaperfil.exception.EstudianteDuplicadoException;
import com.arquisoft.fichas.application.estudiante.exception.EstudianteNoEncontradoException;
import com.arquisoft.fichas.application.fichaperfil.exception.FichaPerfilNoEncontradaException;
import com.arquisoft.fichas.domain.estudiantefichaperfil.aggregate.EstudianteFichaPerfilAggregate;
import com.arquisoft.fichas.domain.estudiantefichaperfil.port.out.EstudianteFichaPerfilOutputPort;
import com.arquisoft.fichas.application.fichaperfil.query.port.out.FichaPerfilQueryOutputPort;
import com.arquisoft.shared.message.FichasMessages;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class AsignarEstudiantesFichaPerfilUseCase implements AsignarEstudiantesFichaPerfilInputPort {

    private final FichaPerfilQueryOutputPort fichaPerfilQueryOutputPort;
    private final EstudianteQueryOutputPort estudianteQueryOutputPort;
    private final EstudianteFichaPerfilOutputPort estudianteFichaPerfilOutputPort;

    @Override
    @Transactional(transactionManager = "fichasTransactionManager")
    public void ejecutar(AsignarEstudiantesFichaPerfilCommand command) {
        UUID fichaPerfilId = command.fichaPerfilId();
        var estudiantesIds = command.estudiantesIds();

        // 1. Ficha existe
        if (!fichaPerfilQueryOutputPort.existsById(fichaPerfilId)) {
            throw new FichaPerfilNoEncontradaException(fichaPerfilId);
        }

        // 2. Sin duplicados dentro de la lista
        if (new HashSet<>(estudiantesIds).size() != estudiantesIds.size()) {
            // Identificar el primer duplicado
            var visto = new HashSet<UUID>();
            for (UUID estudianteId : estudiantesIds) {
                if (!visto.add(estudianteId)) {
                    throw new EstudianteDuplicadoException(estudianteId);
                }
            }
        }

        // 3. Por cada estudiante: existe + no ya vinculado
        for (UUID estudianteId : estudiantesIds) {
            if (!estudianteQueryOutputPort.existsById(estudianteId)) {
                throw new EstudianteNoEncontradoException(estudianteId);
            }
            if (estudianteFichaPerfilOutputPort.existePorFichaYEstudiante(fichaPerfilId, estudianteId)) {
                throw new EstudianteDuplicadoException(estudianteId);
            }
        }

        // 4. Crear relaciones validando límite atómicamente
        long existentes = estudianteFichaPerfilOutputPort.contarPorFichaPerfilId(fichaPerfilId);
        var relaciones = EstudianteFichaPerfilAggregate.crear(fichaPerfilId, estudiantesIds, existentes);

        // 5. Guardar cada relación
        for (EstudianteFichaPerfilAggregate relacion : relaciones) {
            estudianteFichaPerfilOutputPort.guardar(relacion);
        }

        // 6. Log
        log.info(FichasMessages.EstudianteFichaPerfil.LOG_ASIGNADO, fichaPerfilId, estudiantesIds.size());
    }
}
