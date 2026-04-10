package edu.eci.dosw.DOSW_Library.persistence.jpa.mapper;

import edu.eci.dosw.DOSW_Library.core.model.Loan;
import org.springframework.stereotype.Component;

/**
 * Mapper que convierte entre entidad JPA Loan y modelo de dominio Loan.
 *
 * En este proyecto, se utilizan los mismos modelos como entidades JPA,
 * por lo que el mapping es directo (identity mapping).
 *
 * En proyectos más grandes, aquí se haría la conversión:
 * LoanEntity (JPA) ↔ Loan (Domain)
 */
@Component
public class LoanEntityMapper {

    /**
     * Convierte modelo de dominio a entidad JPA.
     * (Actualmente: identity mapping)
     *
     * @param loan modelo de dominio
     * @return entidad JPA
     */
    public Loan toEntity(Loan loan) {
        // En esta arquitectura, el modelo es a la vez entidad JPA
        return loan;
    }

    /**
     * Convierte entidad JPA a modelo de dominio.
     * (Actualmente: identity mapping)
     *
     * @param entity entidad JPA
     * @return modelo de dominio
     */
    public Loan toDomain(Loan entity) {
        // En esta arquitectura, el modelo es a la vez entidad JPA
        return entity;
    }
}
