package edu.eci.dosw.DOSW_Library.persistence.dao;

import edu.eci.dosw.DOSW_Library.core.model.Book;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * DAO BookDAO - Data Access Object para operaciones complejas sobre libros
 * 
 * IMPORTANTE: Book es una entidad JPA (@Entity en core.model)
 * El DAO accede directamente a EntityManager para:
 * - Queries JPQL avanzadas
 * - Updates/Deletes batch
 * - Manejo transaccional fino
 * 
 * Diferencia vs BookRepository:
 * - Repository: operaciones simples, generadas por Spring Data
 * - DAO: logica compleja, transacciones batch, queries nativas
 * 
 * @author DOSW-Library Team
 */
@Component
public class BookDAO {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Decrementar copias disponibles de forma atomica (SQL UPDATE)
     * 
     * IMPORTANTE: Usa SQL nativo directamente para atomicidad
     * Evita cargar entidad, modificar, guardar → es mas eficiente
     * 
     * @param bookId   identificador del libro
     * @param quantity cantidad a decrementar
     * @return cantidad de filas afectadas (1 si exito, 0 si falla)
     */
    @Transactional
    public void decrementAvailableCopies(String bookId, int quantity) {
        String sql = "UPDATE books SET available = available - :qty WHERE id = :id AND available >= :qty";
        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("qty", quantity);
        query.setParameter("id", bookId);
        int updated = query.executeUpdate();

        if (updated == 0) {
            throw new IllegalStateException("No hay copias disponibles del libro " + bookId);
        }
    }

    /**
     * Incrementar copias disponibles de forma atomica (SQL UPDATE)
     * Se usa cuando un usuario devuelve un libro
     * 
     * @param bookId   identificador del libro
     * @param quantity cantidad a incrementar
     */
    @Transactional
    public void incrementAvailableCopies(String bookId, int quantity) {
        String sql = "UPDATE books SET available = available + :qty WHERE id = :id AND available + :qty <= copies";
        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("qty", quantity);
        query.setParameter("id", bookId);
        int updated = query.executeUpdate();

        if (updated == 0) {
            throw new IllegalStateException("No se pudo incrementar disponibilidad del libro " + bookId);
        }
    }

    /**
     * Obtener reporte de inventario: libros agotados (available = 0)
     * 
     * @return lista de libros sin copias disponibles
     */
    public List<Book> getOutOfStockReport() {
        String jpql = "SELECT b FROM Book b WHERE b.available = 0 ORDER BY b.title";
        return entityManager.createQuery(jpql, Book.class).getResultList();
    }

    /**
     * Obtener reporte de inventario bajo: libros con pocas copias
     * 
     * @param threshold umbral minimo de copias
     * @return lista de libros con available < threshold
     */
    public List<Book> getLowStockReport(int threshold) {
        String jpql = "SELECT b FROM Book b WHERE b.available <= :threshold AND b.available > 0 ORDER BY b.available ASC";
        return entityManager.createQuery(jpql, Book.class)
                .setParameter("threshold", threshold)
                .getResultList();
    }

    /**
     * Obtener valor total de inventario de libros disponibles
     * (sumar: copies * available para todos los libros)
     * 
     * @return cantidad total de copias disponibles en toda la biblioteca
     */
    public long getTotalAvailableInventory() {
        String jpql = "SELECT SUM(b.available) FROM Book b WHERE b.available > 0";
        Long result = (Long) entityManager.createQuery(jpql)
                .getSingleResult();
        return result != null ? result : 0L;
    }

    /**
     * Busqueda avanzada: libros por palabra clave (titulo o autor)
     * 
     * @param keyword palabra a buscar (insensible a mayusculas)
     * @return lista de libros que coinciden
     */
    public List<Book> searchBooks(String keyword) {
        String jpql = "SELECT b FROM Book b WHERE LOWER(b.title) LIKE CONCAT('%', LOWER(:keyword), '%') OR LOWER(b.author) LIKE CONCAT('%', LOWER(:keyword), '%') ORDER BY b.title";
        return entityManager.createQuery(jpql, Book.class)
                .setParameter("keyword", keyword)
                .getResultList();
    }

    /**
     * Actualizar stock total de un libro (ADMIN only)
     * Usa UPDATE batch en lugar de cargar/guardar entidad
     * 
     * @param bookId    identificador del libro
     * @param newCopies nuevo valor de stock
     */
    @Transactional
    public void updateCopiesCount(String bookId, int newCopies) {
        String sql = "UPDATE books SET copies = :copies WHERE id = :id";
        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("copies", newCopies);
        query.setParameter("id", bookId);
        int updated = query.executeUpdate();

        if (updated == 0) {
            throw new IllegalStateException("Libro no encontrado: " + bookId);
        }
    }
}
