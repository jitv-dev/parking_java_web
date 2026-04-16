package com.javier.parking.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "parking_history")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParkingHistory {
    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    @NotBlank(message = "Placa/Patente no puede estar vacia")
    private String plate;

    @PastOrPresent(message = "La fecha no puede ser futura")
    private LocalDateTime entryTime;

    @PastOrPresent(message = "La fecha no puede ser futura")
    private LocalDateTime exitTime;

    @DecimalMin(value = "0.0", message = "No puede ser negativo")
    private Double minutes;

    @DecimalMin(value = "0.0", message = "No puede ser negativo")
    private Double cost;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    @Column(nullable = false)
    @NotBlank(message = "Operador no puede estar vacio")
    private String operator;
}
