package com.arquisoft.fichas.domain.estudiantefichaperfil.aggregate;

import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.constant.FichasFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.DomainValidator;
import com.arquisoft.shared.validation.ValidationResult;

import java.util.List;
import java.util.UUID;

public final class EstudianteFichaPerfilDomain {

    private UUID id;
    private UUID fichaPerfilId;
    private UUID estudianteId;

    private EstudianteFichaPerfilDomain() {}

    private EstudianteFichaPerfilDomain(UUID id, UUID fichaPerfilId, UUID estudianteId) {
        this.id = id;
        this.fichaPerfilId = fichaPerfilId;
        this.estudianteId = estudianteId;
    }

    private static EstudianteFichaPerfilDomain crear(UUID fichaPerfilId, UUID estudianteId) {
        var relacion = new EstudianteFichaPerfilDomain();
        var result = new ValidationResult();

        relacion.setId();
        relacion.setFichaPerfilId(fichaPerfilId, result);
        relacion.setEstudianteId(estudianteId, result);

        result.lanzarSiTieneErrores();
        return relacion;
    }

    public static List<EstudianteFichaPerfilDomain> crear(
            UUID fichaPerfilId,
            List<UUID> nuevosEstudiantesIds) {

        var result = new ValidationResult();

        DomainValidator.noVacia(nuevosEstudiantesIds,
                FichasFields.EstudianteFichaPerfil.ESTUDIANTES,
                FichasCodes.EstudianteFichaPerfil.ESTUDIANTES_REQUERIDOS, result);

        result.lanzarSiTieneErrores();

        return nuevosEstudiantesIds.stream()
                .map(estudianteId -> crear(fichaPerfilId, estudianteId))
                .toList();
    }

    public static EstudianteFichaPerfilDomain reconstruir(UUID id, UUID fichaPerfilId, UUID estudianteId) {
        return new EstudianteFichaPerfilDomain(id, fichaPerfilId, estudianteId);
    }

    private void setId() {
        this.id = UtilUUID.generateNewUUID();
    }

    private void setFichaPerfilId(UUID fichaPerfilId, ValidationResult result) {
        if (!DomainValidator.noNulo(fichaPerfilId,
                FichasFields.EstudianteFichaPerfil.FICHA_PERFIL,
                FichasCodes.EstudianteFichaPerfil.FICHA_PERFIL_ID_REQUERIDO, result)) {
            return;
        }
        this.fichaPerfilId = fichaPerfilId;
    }

    private void setEstudianteId(UUID estudianteId, ValidationResult result) {
        if (!DomainValidator.noNulo(estudianteId,
                FichasFields.EstudianteFichaPerfil.ESTUDIANTE,
                FichasCodes.EstudianteFichaPerfil.ESTUDIANTE_ID_REQUERIDO, result)) {
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
