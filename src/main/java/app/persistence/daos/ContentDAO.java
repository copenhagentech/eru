package app.persistence.daos;

import app.config.hibernate.HibernateConfig;
import app.entities.Content;
import app.entities.enums.ContentType;
import app.exceptions.DatabaseException;
import app.exceptions.enums.DatabaseErrorType;
import app.persistence.interfaces.IDAO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceException;
import java.util.List;
import java.util.Optional;

public class ContentDAO implements IDAO<Content, Integer> {
    private final EntityManagerFactory emf;

    public ContentDAO() {
        this(HibernateConfig.getEntityManagerFactory());
    }

    public ContentDAO(EntityManagerFactory emf) {
        this.emf = emf;
    }

    @Override
    public Content create(Content content) {
        if (content == null) {
            throw new DatabaseException("Content cannot be null", DatabaseErrorType.VALIDATION);
        }

        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.persist(content);
            em.getTransaction().commit();
            return content;
        } catch (PersistenceException e) {
            throw new DatabaseException("Create content failed", DatabaseErrorType.TRANSACTION_FAILURE, e);
        } catch (RuntimeException e) {
            throw new DatabaseException("Create content failed", DatabaseErrorType.UNKNOWN, e);
        }
    }

    @Override
    public List<Content> getAll() {
        try (EntityManager em = emf.createEntityManager()) {
            return em.createQuery("SELECT c FROM Content c", Content.class).getResultList();
        } catch (RuntimeException e) {
            throw new DatabaseException("Get all content failed", DatabaseErrorType.UNKNOWN, e);
        }
    }

    public List<Content> getAllActive() {
        try (EntityManager em = emf.createEntityManager()) {
            return em.createNamedQuery("Content.findAllActive", Content.class)
                    .getResultList();
        } catch (RuntimeException e) {
            throw new DatabaseException("Get active content failed", DatabaseErrorType.UNKNOWN, e);
        }
    }

    @Override
    public Optional<Content> getById(Integer id) {
        if (id == null) {
            throw new DatabaseException("Content id cannot be null", DatabaseErrorType.VALIDATION);
        }

        try (EntityManager em = emf.createEntityManager()) {
            Content content = em.find(Content.class, id);
            return Optional.ofNullable(content);
        } catch (RuntimeException e) {
            throw new DatabaseException("Get content by id failed", DatabaseErrorType.UNKNOWN, e);
        }
    }

    public List<Content> getByType(ContentType type) {
        if (type == null) {
            throw new DatabaseException("Content type cannot be null", DatabaseErrorType.VALIDATION);
        }

        try (EntityManager em = emf.createEntityManager()) {
            return em.createNamedQuery("Content.findByType", Content.class)
                    .setParameter("type", type)
                    .getResultList();
        } catch (RuntimeException e) {
            throw new DatabaseException("Get content by type failed", DatabaseErrorType.UNKNOWN, e);
        }
    }

    @Override
    public Content update(Content updatedContent) {
        if (updatedContent == null) {
            throw new DatabaseException("Content cannot be null", DatabaseErrorType.VALIDATION);
        }

        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            Content merged = em.merge(updatedContent);
            em.getTransaction().commit();
            return merged;
        } catch (PersistenceException e) {
            throw new DatabaseException("Update content failed", DatabaseErrorType.TRANSACTION_FAILURE, e);
        } catch (RuntimeException e) {
            throw new DatabaseException("Update content failed", DatabaseErrorType.UNKNOWN, e);
        }
    }

    @Override
    public boolean delete(Integer id) {
        if (id == null) {
            throw new DatabaseException("Content id cannot be null", DatabaseErrorType.VALIDATION);
        }

        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            Content content = em.find(Content.class, id);
            if (content == null) {
                em.getTransaction().rollback();
                return false;
            }
            em.remove(content);
            em.getTransaction().commit();
            return true;
        } catch (PersistenceException e) {
            throw new DatabaseException("Delete content failed", DatabaseErrorType.TRANSACTION_FAILURE, e);
        } catch (RuntimeException e) {
            throw new DatabaseException("Delete content failed", DatabaseErrorType.UNKNOWN, e);
        }
    }
}
