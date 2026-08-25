package com.arquisoft.usuarios.domain.representantecomitecurriculum;

import com.arquisoft.shared.events.AggregateRoot;
import com.arquisoft.shared.message.constant.UsuariosCodes;
import com.arquisoft.shared.message.constant.UsuariosFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;
import com.arquisoft.usuarios.domain.representantecomitecurriculum.event.RepresentanteComiteCurriculumAgregadoEvent;

import java.util.UUID;

public final class RepresentanteComiteCurriculumDomain extends AggregateRoot {

    private UUID usuario;

    private RepresentanteComiteCurriculumDomain() {
    }

    public static RepresentanteComiteCurriculumDomain crear(UUID usuario) {
        var resultado = new ValidationResult();

        ValidatorObjeto.noNulo(
                usuario,
                UsuariosFields.RepresentanteComiteCurriculum.USUARIO,
                UsuariosCodes.RepresentanteComiteCurriculum.USUARIO_REQUERIDO,
                resultado
        );

        resultado.lanzarSiTieneErrores();

        var representante = new RepresentanteComiteCurriculumDomain();
        representante.setUsuario(usuario);

        representante.publicarEvento(new RepresentanteComiteCurriculumAgregadoEvent(
                representante.getUsuario(),
                representante.getUsuario(),
                "",
                ""
        ));

        return representante;
    }

    public static RepresentanteComiteCurriculumDomain reconstruir(UUID usuario) {
        var representante = new RepresentanteComiteCurriculumDomain();
        representante.setUsuario(usuario);
        return representante;
    }

    private void setUsuario(UUID usuario) {
        this.usuario = usuario != null ? usuario : UtilUUID.obtenerUUIDPorDefecto();
    }

    public UUID getUsuario() {
        return usuario;
    }
}
