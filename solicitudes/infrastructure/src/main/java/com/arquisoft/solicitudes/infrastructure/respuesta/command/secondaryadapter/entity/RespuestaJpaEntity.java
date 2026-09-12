package com.arquisoft.solicitudes.infrastructure.respuesta.command.secondaryadapter.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "respuesta")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RespuestaJpaEntity {

    @Id
    @Column(name = "id", columnDefinition = "uuid")
    private UUID id;

    @Column(name = "solicitud_id", columnDefinition = "uuid", nullable = false)
    private UUID solicitudId;

    @Column(name = "fecha_respuesta", nullable = false)
    private LocalDateTime fechaRespuesta;

    @Column(name = "contenido", nullable = false, length = 100)
    private String contenido;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estado_respuesta_id", nullable = false)
    private EstadoRespuestaJpaEntity estadoRespuesta;
}
