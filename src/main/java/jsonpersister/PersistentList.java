package jsonpersister;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.indexOf;


/**
 * Date: 5/10/13
 * Time: 23:26
 */
public class PersistentList<T> {
    private final static Map<String, PersistentList<?>> lists = new LinkedHashMap<>();
    private final Class<T> clazz;

    public synchronized static <T> PersistentList<T> on(Class<T> clazz, File file, ObjectMapper objectMapper) {
        try {
            String key = file.getAbsoluteFile().getCanonicalPath();
            PersistentList<?> persistentList = lists.get(key);
            if (persistentList == null) {
                persistentList = new PersistentList<>(clazz, file, objectMapper);
                lists.put(key, persistentList);
            }
            return (PersistentList<T>) persistentList;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    private final File file;
    private final List<T> data;
    private final ObjectMapper objectMapper;

    private PersistentList(Class<T> clazz, File file, ObjectMapper objectMapper) {
        this.clazz = checkNotNull(clazz);
        this.file = checkNotNull(file);
        this.objectMapper = checkNotNull(objectMapper);
        this.data = new CopyOnWriteArrayList(read());
    }

    public synchronized PersistentList add(T t) {
        data.add(t);
        store();
        return this;
    }

    public synchronized PersistentList set(Predicate<T> predicate, T t) {
        int i = indexOf(data, predicate);
        if (i != -1) {
            data.set(i, t);
            store();
        }
        return this;
    }

    public synchronized PersistentList removeIf(Predicate<T> predicate) {
        Iterables.removeIf(data, predicate);
        store();
        return this;
    }

    public List<T> list() {
        return ImmutableList.copyOf(data);
    }

    private Collection<? extends T> read() {
        try {
            Collection<? extends T> collection = objectMapper
                    .reader(objectMapper.getTypeFactory().constructCollectionType(List.class, clazz))
                    .readValue(file);
            return collection;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void store() {
        try {
            objectMapper.writeValue(file, data);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
