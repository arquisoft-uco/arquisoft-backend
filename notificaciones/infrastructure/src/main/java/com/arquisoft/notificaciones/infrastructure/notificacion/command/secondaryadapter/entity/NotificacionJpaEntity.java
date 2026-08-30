package com.arquisoft.notificaciones.infrastructure.notificacion.command.secondaryadapter.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "notificacion")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificacionJpaEntity {

    @Id
    @Column(name = "id", columnDefinition = "uuid")
    private UUID id;

    @Column(name = "event_id", nullable = false, unique = true, length = 36)
    private String idEvento;

    @Column(name = "tipo", nullable = false, length = 60)
    private String tipo;

    @Column(name = "destinatario", nullable = false, length = 50)
    private String destinatario;

    @Column(name = "asunto", nullable = false, length = 200)
    private String asunto;

    @Column(name = "destinatario_nombre", nullable = false, length = 100)
    private String destinatarioNombre;

    @Column(name = "cuerpo", nullable = false)
    private String cuerpo;

    @Column(name = "estado", nullable = false, length = 20)
    private String estado;

    @Column(name = "detalle_error")
    private String detalleError;

    @Column(name = "fecha_creacion", nullable = false)
    private Instant fechaCreacion;

    @Column(name = "fecha_envio")
    private Instant fechaEnvio;

    @Column(name = "intentos", nullable = false)
    private int intentos;

    @Column(name = "fecha_ultimo_intento")
    private Instant fechaUltimoIntento;
}
