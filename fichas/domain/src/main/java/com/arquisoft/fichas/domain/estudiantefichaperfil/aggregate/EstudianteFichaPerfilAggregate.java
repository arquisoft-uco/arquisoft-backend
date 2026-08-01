package com.arquisoft.fichas.domain.estudiantefichaperfil.aggregate;

import com.arquisoft.shared.message.FichasMessages;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.DomainValidator;
import com.arquisoft.shared.validation.ValidationResult;

import java.util.List;
import java.util.UUID;

public final class EstudianteFichaPerfilAggregate {

    private UUID id;
    private UUID fichaPerfilId;
    private UUID estudianteId;

    private EstudianteFichaPerfilAggregate() {}

    private EstudianteFichaPerfilAggregate(UUID id, UUID fichaPerfilId, UUID estudianteId) {
        this.id = id;
        this.fichaPerfilId = fichaPerfilId;
        this.estudianteId = estudianteId;
    }

    private static EstudianteFichaPerfilAggregate crear(UUID fichaPerfilId, UUID estudianteId) {
        var relacion = new EstudianteFichaPerfilAggregate();
        var result = new ValidationResult();

        relacion.setId();
        relacion.setFichaPerfilId(fichaPerfilId, result);
        relacion.setEstudianteId(estudianteId, result);

        result.lanzarSiTieneErrores();
        return relacion;
    }

    public static List<EstudianteFichaPerfilAggregate> crear(
            UUID fichaPerfilId,
            List<UUID> nuevosEstudiantesIds) {

        var result = new ValidationResult();

        DomainValidator.noVacia(nuevosEstudiantesIds,
                FichasMessages.EstudianteFichaPerfil.CAMPO_ESTUDIANTES,
                FichasMessages.EstudianteFichaPerfil.ESTUDIANTES_REQUERIDOS, result);

        result.lanzarSiTieneErrores();

        return nuevosEstudiantesIds.stream()
                .map(estudianteId -> crear(fichaPerfilId, estudianteId))
                .toList();
    }

    public static EstudianteFichaPerfilAggregate reconstruir(UUID id, UUID fichaPerfilId, UUID estudianteId) {
        return new EstudianteFichaPerfilAggregate(id, fichaPerfilId, estudianteId);
    }

    private void setId() {
        this.id = UtilUUID.generateNewUUID();
    }

    private void setFichaPerfilId(UUID fichaPerfilId, ValidationResult result) {
        if (!DomainValidator.noNulo(fichaPerfilId,
                FichasMessages.EstudianteFichaPerfil.CAMPO_FICHA_PERFIL,
                FichasMessages.EstudianteFichaPerfil.FICHA_PERFIL_ID_REQUERIDO, result)) {
            return;
        }
        this.fichaPerfilId = fichaPerfilId;
    }

    private void setEstudianteId(UUID estudianteId, ValidationResult result) {
        if (!DomainValidator.noNulo(estudianteId,
                FichasMessages.EstudianteFichaPerfil.CAMPO_ESTUDIANTE,
                FichasMessages.EstudianteFichaPerfil.ESTUDIANTE_ID_REQUERIDO, result)) {
            return;
        }
        this.estudianteId = estudianteId;
    }

    public UUID getId() {
        return id;
    }

    public UUID getFichaPerfilId() {
        return fichaPerfilId;
    }

    public UUID getEstudianteId() {
        return estudianteId;
    }
}
