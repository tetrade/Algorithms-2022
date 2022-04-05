package lesson5;

import kotlin.NotImplementedError;
import org.jetbrains.annotations.NotNull;

import java.util.AbstractSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

public class OpenAddressingSet<T> extends AbstractSet<T> {

    private final Object occupied = new Object();

    private final int bits;

    private final int capacity;

    private final Object[] storage;

    private int size = 0;

    private int startingIndex(Object element) {
        return element.hashCode() & (0x7FFFFFFF >> (31 - bits));
    }

    public OpenAddressingSet(int bits) {
        if (bits < 2 || bits > 31) {
            throw new IllegalArgumentException();
        }
        this.bits = bits;
        capacity = 1 << bits;
        storage = new Object[capacity];
    }

    @Override
    public int size() {
        return size;
    }

    /**
     * Проверка, входит ли данный элемент в таблицу
     */
    @Override
    public boolean contains(Object o) {
        int index = startingIndex(o);
        int startIndex = index;
        Object current = storage[index];
        while (true) {
            if (current == occupied) {
                index = (index + 1) % capacity;
                if (index == startIndex) return false;
                current = storage[index];
                continue;
            }
            if (current == null) return false;
            if (current.equals(o)) return true;
            index = (index + 1) % capacity;
            if (index == startIndex) return false;
            current = storage[index];
        }
    }

    /**
     * Добавление элемента в таблицу.
     *
     * Не делает ничего и возвращает false, если такой же элемент уже есть в таблице.
     * В противном случае вставляет элемент в таблицу и возвращает true.
     *
     * Бросает исключение (IllegalStateException) в случае переполнения таблицы.
     * Обычно Set не предполагает ограничения на размер и подобных контрактов,
     * но в данном случае это было введено для упрощения кода.
     */
    @Override
    public boolean add(T t) {
        int startingIndex = startingIndex(t);
        int index = startingIndex;
        Object current = storage[index];
        while (current != null && current != occupied) {
            if (current.equals(t)) {
                return false;
            }
            index = (index + 1) % capacity;
            if (index == startingIndex) {
                throw new IllegalStateException("Table is full");
            }
            current = storage[index];
        }
        storage[index] = t;
        size++;
        return true;
    }

    /**
     * Удаление элемента из таблицы
     *
     * Если элемент есть в таблица, функция удаляет его из дерева и возвращает true.
     * В ином случае функция оставляет множество нетронутым и возвращает false.
     * Высота дерева не должна увеличиться в результате удаления.
     *
     * Спецификация: {@link Set#remove(Object)} (Ctrl+Click по remove)
     *
     * Средняя
     */

    // R = O(1), T = O(n), n - capacity
    @Override
    public boolean remove(Object o) {
        int index = startingIndex(o);
        int startIndex = index;
        Object current = storage[index];
        while (current != null) {
            if (current.equals(o)) {
                remove(index); //  R = O(1), T = O(1)
                return true;
            }
            index = (index + 1) % capacity;
            if (index == startIndex) break;
            current = storage[index];
        }
        return false;
    }

//  R = O(1), T = O(1)
    public void remove(int index) {
        storage[index] = occupied;
        size--;
    }


    private int getNextIndex(int startIndex) {
        int index = startIndex;
        Object current = storage[index];
        while (current == null || current == occupied){
            index = (index + 1) % capacity;
            current = storage[index];
        }
        return index;
    }

    /**
     * Создание итератора для обхода таблицы
     *
     * Не забываем, что итератор должен поддерживать функции next(), hasNext(),
     * и опционально функцию remove()
     *
     * Спецификация: {@link Iterator} (Ctrl+Click по Iterator)
     *
     * Средняя (сложная, если поддержан и remove тоже)
     */
    @NotNull
    @Override
    public Iterator<T> iterator() { return new OpenAddressingSetIterator(); }

    public class OpenAddressingSetIterator implements Iterator<T> {
        private int lastIndex;
        private int startIndex;
        private int count;

        private OpenAddressingSetIterator() {
            count = size;
            startIndex = 0;
            lastIndex = -1;
        }

//      T = O(1), R = O(1)
        @Override
        public boolean hasNext() {
            return count != 0;
        }

//      T = O(n), R = O(1)
        @Override
        public T next() {
            if (!hasNext()) throw new NoSuchElementException();
            startIndex = getNextIndex(startIndex) + 1; //R = O(1), T = O(n), n - capacity
            lastIndex = startIndex - 1;
            count -= 1;
            return (T) storage[lastIndex];
        }

//      T = O(1), R = O(1)
        @Override
        public void remove() {
             if (lastIndex == -1) throw new IllegalStateException();
            OpenAddressingSet.this.remove(lastIndex); // T = O(1), R = O(1)
            lastIndex = -1;
        }
    }
}
