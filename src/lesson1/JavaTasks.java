package lesson1;

import kotlin.NotImplementedError;
import org.jetbrains.annotations.NotNull;


import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Pattern;

@SuppressWarnings("unused")
public class JavaTasks {
    /**
     * Сортировка времён
     *
     * Простая
     * (Модифицированная задача с сайта acmp.ru)
     *
     * Во входном файле с именем inputName содержатся моменты времени в формате ЧЧ:ММ:СС AM/PM,
     * каждый на отдельной строке. См. статью википедии "12-часовой формат времени".
     *
     * Пример:
     *
     * 01:15:19 PM
     * 07:26:57 AM
     * 10:00:03 AM
     * 07:56:14 PM
     * 01:15:19 PM
     * 12:40:31 AM
     *
     * Отсортировать моменты времени по возрастанию и вывести их в выходной файл с именем outputName,
     * сохраняя формат ЧЧ:ММ:СС AM/PM. Одинаковые моменты времени выводить друг за другом. Пример:
     *
     * 12:40:31 AM
     * 07:26:57 AM
     * 10:00:03 AM
     * 01:15:19 PM
     * 01:15:19 PM
     * 07:56:14 PM
     *
     * В случае обнаружения неверного формата файла бросить любое исключение.
     */
    // можно использовать такой же подход, как в задаче sortTemperatures (создать трехмерный массив int[25][60][60]),
    // но для разнообразия решил реализовать по другому:
    // T = O(n + nlog(n)) = O(nlog(n)), R = O(n)
    static public void sortTimes(String inputName, String outputName) throws Exception {
        class Date implements Comparable<Date>{
            private final String dateStr;
            private final Integer dateInt;
            public Date(String date) throws Exception {
                if (!Pattern.matches("((\\d{2}):){2}(\\d\\d)\\s(PM|AM)", date)) throw new Exception();
                String[] tmS = date.split("(:|\s)");
                int[] tmI = {Integer.parseInt(tmS[0]), Integer.parseInt(tmS[1]), Integer.parseInt(tmS[2])};
                if (tmI[0] > 12 || tmI[1] >= 60 || tmI[2] >= 60) throw new Exception(date);
                if (tmI[0] == 12) tmI[0] = 0;
                if (tmS[3].equals("PM")) tmI[0] += 12;
                this.dateStr = date;
                this.dateInt = tmI[0] * 3600 + tmI[1] * 60 + tmI[2];
            }
            @Override
            public String toString() {
                return dateStr;
            }
            @Override
            public int compareTo(Date o) {
                return dateInt - o.dateInt;
            }
        }
        Scanner scanner = new Scanner(Paths.get(inputName));
        List<Date> dateList = new ArrayList<>();
        while (scanner.hasNextLine()) {
            dateList.add(new Date(scanner.nextLine()));
        }
        Collections.sort(dateList);
        PrintWriter out = new PrintWriter(outputName, StandardCharsets.UTF_8);
        dateList.forEach(out::println);
        out.close();
    }

    /**
     * Сортировка адресов
     *
     * Средняя
     *
     * Во входном файле с именем inputName содержатся фамилии и имена жителей города с указанием улицы и номера дома,
     * где они прописаны. Пример:
     *
     * Петров Иван - Железнодорожная 3
     * Сидоров Петр - Садовая 5
     * Иванов Алексей - Железнодорожная 7
     * Сидорова Мария - Садовая 5
     * Иванов Михаил - Железнодорожная 7
     *
     * Людей в городе может быть до миллиона.
     *
     * Вывести записи в выходной файл outputName,
     * упорядоченными по названию улицы (по алфавиту) и номеру дома (по возрастанию).
     * Людей, живущих в одном доме, выводить через запятую по алфавиту (вначале по фамилии, потом по имени). Пример:
     *
     * Железнодорожная 3 - Петров Иван
     * Железнодорожная 7 - Иванов Алексей, Иванов Михаил
     * Садовая 5 - Сидоров Петр, Сидорова Мария
     *
     * В случае обнаружения неверного формата файла бросить любое исключение.
     */

    //                          get         contKey       next
    // TreeMap               | O(log n) |   O(log n)  | O(log n)
    // n - кол. человек, m - кол. городов
    // T = O(n(log(n) + log(n)) + m * nlog(n)) = O(m * nlog(n)), R = O(n)
    static public void sortAddresses(String inputName, String outputName) throws Exception {
        class Pair implements Comparable<Pair>{
            private final String s;
            private final Integer i;
            public Pair(String addr){
                String[] a = addr.split(" ");
                this.s = a[0];
                this.i = Integer.parseInt(a[1]);
            }
            public String getS() {
                return s;
            }
            public Integer getI() {
                return i;
            }
            public String getStr() {
                return s + " " + i;
            }
            @Override
            public int compareTo(Pair o) {
                int resComp = getS().compareTo(o.getS());
                if (resComp == 0) {
                    return getI() - o.getI();
                } else {
                    return resComp;
                }
            }
        }
        Map<Pair, ArrayList<String>> map = new TreeMap<>();
        Scanner scanner = new Scanner(Paths.get(inputName), StandardCharsets.UTF_8);
        while (scanner.hasNext()) {
            String info = scanner.nextLine();
            if (!Pattern.matches("([А-Я][а-я]+\\s){2}-\\s(.+\\s\\d+)", info)) throw new Exception();
            String[] infoList = info.split(" - ");
            Pair addr = new Pair(infoList[1]);
            if (map.containsKey(addr)) {
                map.get(addr).add(infoList[0]); // log(n)
            } else {
                map.put(addr, new ArrayList<>(List.of(infoList[0]))); // log(n)
            }
        }
        PrintWriter out = new PrintWriter(outputName, StandardCharsets.UTF_8);
        map.forEach((k, v) -> {
            Collections.sort(v);
            out.println(k.getStr() + " - " + String.join(", ", v));
        });
        out.close();
    }

    /**
     * Сортировка температур
     *
     * Средняя
     * (Модифицированная задача с сайта acmp.ru)
     *
     * Во входном файле заданы температуры различных участков абстрактной планеты с точностью до десятых градуса.
     * Температуры могут изменяться в диапазоне от -273.0 до +500.0.
     * Например:
     *
     * 24.7
     * -12.6
     * 121.3
     * -98.4
     * 99.5
     * -12.6
     * 11.0
     *
     * Количество строк в файле может достигать ста миллионов.
     * Вывести строки в выходной файл, отсортировав их по возрастанию температуры.
     * Повторяющиеся строки сохранить. Например:
     *
     * -98.4
     * -12.6
     * -12.6
     * 11.0
     * 24.7
     * 99.5
     * 121.3
     */
    // T = O(n), R = 0(1)
    static public void sortTemperatures(String inputName, String outputName) throws IOException {
        int[][] temp = new int[775][20];
        Scanner scanner = new Scanner(Paths.get(inputName));
        while (scanner.hasNext()) {
            String[] floatN = scanner.nextLine().split("\\.");
            if (floatN[0].contains("-")) {
                temp[Integer.parseInt(floatN[0]) + 274][-Integer.parseInt(floatN[1]) + 10] += 1;
            } else {
                temp[Integer.parseInt(floatN[0]) + 274][Integer.parseInt(floatN[1]) + 10] += 1;
            }
        }
        PrintWriter out = new PrintWriter(outputName);
        for (int i = 0; i < 774; ++i) {
            for (int j = 0; j < 20; ++j) {
                for (int k = 0; k < temp[i][j]; k++) {
                    out.println((i - 274) + (j - 10) / 10.0);
                }
            }
        }
        out.close();
    }

    /**
     * Сортировка последовательности
     *
     * Средняя
     * (Задача взята с сайта acmp.ru)
     *
     * В файле задана последовательность из n целых положительных чисел, каждое в своей строке, например:
     *
     * 1
     * 2
     * 3
     * 2
     * 3
     * 1
     * 2
     *
     * Необходимо найти число, которое встречается в этой последовательности наибольшее количество раз,
     * а если таких чисел несколько, то найти минимальное из них,
     * и после этого переместить все такие числа в конец заданной последовательности.
     * Порядок расположения остальных чисел должен остаться без изменения.
     *
     * 1
     * 3
     * 3
     * 1
     * 2
     * 2
     * 2
     */
    static public void sortSequence(String inputName, String outputName) {
        throw new NotImplementedError();
    }

    /**
     * Соединить два отсортированных массива в один
     *
     * Простая
     *
     * Задан отсортированный массив first и второй массив second,
     * первые first.size ячеек которого содержат null, а остальные ячейки также отсортированы.
     * Соединить оба массива в массиве second так, чтобы он оказался отсортирован. Пример:
     *
     * first = [4 9 15 20 28]
     * second = [null null null null null 1 3 9 13 18 23]
     *
     * Результат: second = [1 3 4 9 9 13 15 20 23 28]
     */
    static <T extends Comparable<T>> void mergeArrays(T[] first, T[] second) {
        throw new NotImplementedError();
    }
}
