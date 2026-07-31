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

        result.throwIfHasErrors();
        return relacion;
    }

    /**
     * Construye las relaciones validando la integridad de la petición: la colección debe
     * traer elementos y cada relación se autovalida. No consulta estado externo, por lo
     * que el caso de uso puede invocarla antes de tocar la base de datos.
     */
    public static List<EstudianteFichaPerfilAggregate> crear(
            UUID fichaPerfilId,
            List<UUID> nuevosEstudiantesIds) {

        var result = new ValidationResult();

        DomainValidator.notEmpty(nuevosEstudiantesIds,
                FichasMessages.EstudianteFichaPerfil.CAMPO_ESTUDIANTES,
                FichasMessages.EstudianteFichaPerfil.ESTUDIANTES_REQUERIDOS, result);

        result.throwIfHasErrors();

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
        if (!DomainValidator.notNull(fichaPerfilId,
                FichasMessages.EstudianteFichaPerfil.CAMPO_FICHA_PERFIL,
                FichasMessages.EstudianteFichaPerfil.FICHA_PERFIL_ID_REQUERIDO, result)) {
            return;
        }
        this.fichaPerfilId = fichaPerfilId;
    }

    private void setEstudianteId(UUID estudianteId, ValidationResult result) {
        if (!DomainValidator.notNull(estudianteId,
                FichasMessages.EstudianteFichaPerfil.CAMPO_ESTUDIANTE,
                FichasMessages.EstudianteFichaPerfil.ESTUDIANTE_ID_REQUERIDO, result)) {
            return;
        }
        this.estudianteId = estudianteId;
    }

    /**
     * Regla de negocio del conjunto: los estudiantes ya vinculados más los que se pretende
     * asignar no pueden superar {@code ESTUDIANTES_MAX}. Es una invariante de la ficha y no
     * de una relación individual — de ahí que sea estática y se evalúe una sola vez.
     * El conteo actual lo consulta el caso de uso y lo entrega como parámetro, porque el
     * dominio no accede a la base de datos.
     */
    public static void validarCupoDisponible(int nuevos, long existentes) {
        var result = new ValidationResult();

        if (existentes + nuevos > FichasMessages.FichaPerfil.ESTUDIANTES_MAX) {
            result.addError(
                    FichasMessages.EstudianteFichaPerfil.CAMPO_ESTUDIANTES,
                    FichasMessages.EstudianteFichaPerfil.LIMITE_ESTUDIANTES_EXCEDIDO,
                    FichasMessages.EstudianteFichaPerfil.LIMITE_EXCEDIDO_MSG.formatted(
                            FichasMessages.FichaPerfil.ESTUDIANTES_MAX
                    )
            );
        }

        result.throwIfHasErrors();
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
