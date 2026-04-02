package app.persistence.interfaces;

import java.util.List;
import java.util.Optional;

public interface IDAO<T, ID> {

    T create(T entity);

    Optional<T> getById(ID id);

    List<T> getAll();

    T update(T entity);

    boolean delete(ID id);
}
