package com.arquisoft.fichas.domain.estudiantefichaperfil.aggregate;

import com.arquisoft.shared.message.FichasMessages;
import com.arquisoft.shared.validation.DomainValidator;
import com.arquisoft.shared.validation.ValidationResult;

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

    public static EstudianteFichaPerfilAggregate crear(UUID fichaPerfilId, UUID estudianteId) {
        EstudianteFichaPerfilAggregate relacion = new EstudianteFichaPerfilAggregate();
        ValidationResult result = new ValidationResult();

        relacion.setId(UUID.randomUUID(), result);
        relacion.setFichaPerfilId(fichaPerfilId, result);
        relacion.setEstudianteId(estudianteId, result);

        result.throwIfHasErrors();
        return relacion;
    }

    public static EstudianteFichaPerfilAggregate reconstruir(UUID id, UUID fichaPerfilId, UUID estudianteId) {
        return new EstudianteFichaPerfilAggregate(id, fichaPerfilId, estudianteId);
    }

    private void setId(UUID id, ValidationResult result) {
        if (!DomainValidator.notNull(id,
                FichasMessages.EstudianteFichaPerfil.CAMPO_ID,
                FichasMessages.EstudianteFichaPerfil.ID_REQUERIDO, result)) {
            return;
        }
        this.id = id;
    }

    private void setFichaPerfilId(UUID fichaPerfilId, ValidationResult result) {
        if (!DomainValidator.notNull(fichaPerfilId,
                FichasMessages.EstudianteFichaPerfil.CAMPO_FICHA_PERFIL_ID,
                FichasMessages.EstudianteFichaPerfil.FICHA_PERFIL_ID_REQUERIDO, result)) {
            return;
        }
        this.fichaPerfilId = fichaPerfilId;
    }

    private void setEstudianteId(UUID estudianteId, ValidationResult result) {
        if (!DomainValidator.notNull(estudianteId,
                FichasMessages.EstudianteFichaPerfil.CAMPO_ESTUDIANTE_ID,
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
