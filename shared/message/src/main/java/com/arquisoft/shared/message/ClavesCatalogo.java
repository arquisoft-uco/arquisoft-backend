package com.arquisoft.shared.message;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import com.arquisoft.shared.message.key.app.AlmacenamientoKey;
import com.arquisoft.shared.message.key.biblioteca.BibliotecarioKey;
import com.arquisoft.shared.message.key.app.ConsultaKey;
import com.arquisoft.shared.message.key.app.HttpKey;
import com.arquisoft.shared.message.key.app.MensajeriaKey;
import com.arquisoft.shared.message.key.app.PaginacionKey;
import com.arquisoft.shared.message.key.app.ValidadorKey;
import com.arquisoft.shared.message.key.evaluaciones.CategoriaItemCuantitativoJuradoKey;
import com.arquisoft.shared.message.key.evaluaciones.CriterioItemCualitativoJuradoKey;
import com.arquisoft.shared.message.key.evaluaciones.EvaluacionCualitativaJuradoKey;
import com.arquisoft.shared.message.key.evaluaciones.EvaluacionJuradoKey;
import com.arquisoft.shared.message.key.evaluaciones.EvaluacionCuantitativaJuradoKey;
import com.arquisoft.shared.message.key.evaluaciones.EvaluacionKey;
import com.arquisoft.shared.message.key.evaluaciones.ItemCualitativoJuradoKey;
import com.arquisoft.shared.message.key.evaluaciones.ItemCuantitativoJuradoKey;
import com.arquisoft.shared.message.key.evaluaciones.JuradoKey;
import com.arquisoft.shared.message.key.evaluaciones.ObservacionItemJuradoKey;
import com.arquisoft.shared.message.key.fichas.EstadoEvaluacionFichaKey;
import com.arquisoft.shared.message.key.fichas.EstadoEvaluacionKey;
import com.arquisoft.shared.message.key.fichas.EstadoFichaKey;
import com.arquisoft.shared.message.key.fichas.EstadoFichaPerfilKey;
import com.arquisoft.shared.message.key.fichas.EstudianteFichaPerfilKey;
import com.arquisoft.shared.message.key.fichas.AsesorFichaKey;
import com.arquisoft.shared.message.key.fichas.EstudianteKey;
import com.arquisoft.shared.message.key.fichas.EvaluacionFichaPerfilKey;
import com.arquisoft.shared.message.key.fichas.FichaPerfilKey;
import com.arquisoft.shared.message.key.fichas.ItemFichaPerfilKey;
import com.arquisoft.shared.message.key.fichas.MinioGuiaKey;
import com.arquisoft.shared.message.key.fichas.ObservacionEvaluacionKey;
import com.arquisoft.shared.message.key.fichas.ObservacionItemKey;
import com.arquisoft.shared.message.key.fichas.TipoItemKey;
import com.arquisoft.shared.message.key.fichas.RepresentanteComiteKey;
import com.arquisoft.shared.message.key.fichas.RevisionItemKey;
import com.arquisoft.shared.message.key.fichas.EstadoRevisionKey;
import com.arquisoft.shared.message.key.mapas_ruta.AsignacionAsesorKey;
import com.arquisoft.shared.message.key.mapas_ruta.AsignacionProyectoKey;
import com.arquisoft.shared.message.key.mapas_ruta.MapaRutaKey;
import com.arquisoft.shared.message.key.mapas_ruta.TareaKey;
import com.arquisoft.shared.message.key.notificaciones.ConsumidorKey;
import com.arquisoft.shared.message.key.notificaciones.EnvioNotificacionKey;
import com.arquisoft.shared.message.key.notificaciones.NotificacionKey;
import com.arquisoft.shared.message.key.notificaciones.PlantillaKey;
import com.arquisoft.shared.message.key.proyectos.AsesorKey;
import com.arquisoft.shared.message.key.proyectos.CoordinadorKey;
import com.arquisoft.shared.message.key.repositorio_artefactos.AdministradorKey;
import com.arquisoft.shared.message.key.repositorio_artefactos.RepositorioArtefactoKey;
import com.arquisoft.shared.message.key.proyectos.AsesorProyectoGradoKey;
import com.arquisoft.shared.message.key.proyectos.EstadoAsesorKey;
import com.arquisoft.shared.message.key.proyectos.EstadoProyectoGradoKey;
import com.arquisoft.shared.message.key.proyectos.EstadoProyectoKey;
import com.arquisoft.shared.message.key.proyectos.EstudianteProyectoGradoKey;
import com.arquisoft.shared.message.key.proyectos.EstudianteProyectosKey;
import com.arquisoft.shared.message.key.proyectos.ProyectoGradoKey;
import com.arquisoft.shared.message.key.solicitudes.EstadoRespuestaKey;
import com.arquisoft.shared.message.key.solicitudes.RespuestaKey;
import com.arquisoft.shared.message.key.solicitudes.SolicitudKey;
import com.arquisoft.shared.message.key.solicitudes.TipoSolicitudKey;
import com.arquisoft.shared.message.key.solicitudes.UsuarioReplicaKey;
import com.arquisoft.shared.message.key.seguridad.AutenticacionKey;
import com.arquisoft.shared.message.key.seguridad.ConfiguracionKey;
import com.arquisoft.shared.message.key.seguridad.IniciarSesionKey;
import com.arquisoft.shared.message.key.seguridad.LimiteSolicitudesKey;
import com.arquisoft.shared.message.key.seguridad.RolKey;
import com.arquisoft.shared.message.key.seguridad.SesionKey;
import com.arquisoft.shared.message.key.seguridad.TokenInvalidadoKey;
import com.arquisoft.shared.message.key.seguridad.TokenKey;
import com.arquisoft.shared.message.key.usuarios.AgregarAsesorKey;
import com.arquisoft.shared.message.key.usuarios.AgregarCoordinadorKey;
import com.arquisoft.shared.message.key.usuarios.AgregarAsesorFichaKey;
import com.arquisoft.shared.message.key.usuarios.AgregarRepresentanteComiteKey;
import com.arquisoft.shared.message.key.usuarios.AgregarAdministradorKey;
import com.arquisoft.shared.message.key.usuarios.AgregarBibliotecarioKey;
import com.arquisoft.shared.message.key.usuarios.AgregarJuradoKey;
import com.arquisoft.shared.message.key.usuarios.AgregarEstudianteKey;
import com.arquisoft.shared.message.key.usuarios.ConsultarAsesoresFichaVigentesKey;
import com.arquisoft.shared.message.key.usuarios.ConsultarRepresentantesComiteVigentesKey;
import com.arquisoft.shared.message.key.usuarios.ConsultarAsesoresVigentesKey;
import com.arquisoft.shared.message.key.usuarios.ConsultarCoordinadoresVigentesKey;
import com.arquisoft.shared.message.key.usuarios.ConsultarJuradosVigentesKey;
import com.arquisoft.shared.message.key.usuarios.ConsultarEstudiantesVigentesKey;
import com.arquisoft.shared.message.key.usuarios.ConsultarEstadosUsuarioKey;
import com.arquisoft.shared.message.key.usuarios.ConsultarIdentidadUsuarioKey;
import com.arquisoft.shared.message.key.usuarios.ConsultarUsuariosAdministradorKey;
import com.arquisoft.shared.message.key.usuarios.CambiarEstadoUsuarioKey;
import com.arquisoft.shared.message.key.usuarios.EliminarUsuarioKey;
import com.arquisoft.shared.message.key.usuarios.ModificarUsuarioKey;
import com.arquisoft.shared.message.key.usuarios.ProveedorIdentidadKey;
import com.arquisoft.shared.message.key.usuarios.RegistrarUsuarioKey;
import com.arquisoft.shared.message.key.usuarios.RemoverAdministradorKey;
import com.arquisoft.shared.message.key.usuarios.RemoverAsesorFichaKey;
import com.arquisoft.shared.message.key.usuarios.RemoverAsesorKey;
import com.arquisoft.shared.message.key.usuarios.RemoverBibliotecarioKey;
import com.arquisoft.shared.message.key.usuarios.RemoverCoordinadorKey;
import com.arquisoft.shared.message.key.usuarios.RemoverEstudianteKey;
import com.arquisoft.shared.message.key.usuarios.RemoverRepresentanteComiteKey;


public final class ClavesCatalogo {

    private ClavesCatalogo() {}

    public static final Set<Class<? extends ClaveMensaje>> ENUMS = Set.of(
            AlmacenamientoKey.class,
            ConsultaKey.class,
            HttpKey.class,
            MensajeriaKey.class,
            PaginacionKey.class,
            ValidadorKey.class,
            EstadoEvaluacionFichaKey.class,
            EstadoEvaluacionKey.class,
            com.arquisoft.shared.message.key.evaluaciones.EstadoEvaluacionKey.class,
            EstadoFichaKey.class,
            EstadoFichaPerfilKey.class,
            EstudianteFichaPerfilKey.class,
            EstudianteKey.class,
            AsesorFichaKey.class,
            EvaluacionFichaPerfilKey.class,
            ItemCualitativoJuradoKey.class,
            ItemCuantitativoJuradoKey.class,
            CriterioItemCualitativoJuradoKey.class,
            CategoriaItemCuantitativoJuradoKey.class,
            EvaluacionCualitativaJuradoKey.class,
            EvaluacionCuantitativaJuradoKey.class,
            EvaluacionJuradoKey.class,
            EvaluacionKey.class,
            ObservacionItemJuradoKey.class,
            FichaPerfilKey.class,
            ItemFichaPerfilKey.class,
            MinioGuiaKey.class,
            ObservacionEvaluacionKey.class,
            ObservacionItemKey.class,
            TipoItemKey.class,
            RepresentanteComiteKey.class,
            RevisionItemKey.class,
            EstadoRevisionKey.class,
            EstadoRespuestaKey.class,
            RespuestaKey.class,
            SolicitudKey.class,
            TipoSolicitudKey.class,
            UsuarioReplicaKey.class,
            ConsumidorKey.class,
            EnvioNotificacionKey.class,
            NotificacionKey.class,
            PlantillaKey.class,
            AutenticacionKey.class,
            ConfiguracionKey.class,
            IniciarSesionKey.class,
            LimiteSolicitudesKey.class,
            RolKey.class,
            SesionKey.class,
            TokenInvalidadoKey.class,
            TokenKey.class,
            RegistrarUsuarioKey.class,
            ModificarUsuarioKey.class,
            EliminarUsuarioKey.class,
            CambiarEstadoUsuarioKey.class,
            AgregarEstudianteKey.class,
            RemoverEstudianteKey.class,
            ProveedorIdentidadKey.class,
            AgregarCoordinadorKey.class,
            CoordinadorKey.class,
            AgregarAsesorFichaKey.class,
            AgregarAsesorKey.class,
            RemoverAsesorKey.class,
            RemoverCoordinadorKey.class,
            RemoverAsesorFichaKey.class,
            AgregarRepresentanteComiteKey.class,
            RemoverRepresentanteComiteKey.class,
            AgregarAdministradorKey.class,
            RemoverAdministradorKey.class,
            AgregarBibliotecarioKey.class,
            RemoverBibliotecarioKey.class,
            BibliotecarioKey.class,
            AdministradorKey.class,
            RepositorioArtefactoKey.class,
            AgregarJuradoKey.class,
            JuradoKey.class,
            AsesorKey.class,
            EstudianteProyectosKey.class,
            ProyectoGradoKey.class,
            EstudianteProyectoGradoKey.class,
            EstadoProyectoGradoKey.class,
            EstadoProyectoKey.class,
            AsesorProyectoGradoKey.class,
            EstadoAsesorKey.class,
            ConsultarCoordinadoresVigentesKey.class,
            ConsultarJuradosVigentesKey.class,
            ConsultarEstudiantesVigentesKey.class,
            ConsultarAsesoresVigentesKey.class,
            ConsultarAsesoresFichaVigentesKey.class,
            ConsultarRepresentantesComiteVigentesKey.class,
            ConsultarUsuariosAdministradorKey.class,
            ConsultarEstadosUsuarioKey.class,
            ConsultarIdentidadUsuarioKey.class,
            AsignacionProyectoKey.class,
            MapaRutaKey.class,
            TareaKey.class,
            AsignacionAsesorKey.class,
            com.arquisoft.shared.message.key.mapas_ruta.ProyectoGradoKey.class
    );

    /** Todas las claves declaradas por los enums de {@link #ENUMS}. */
    public static final List<ClaveMensaje> TODAS = ENUMS.stream()
            .map(Class::getEnumConstants)
            .flatMap(Arrays::stream)
            .map(ClaveMensaje.class::cast)
            .toList();
}
