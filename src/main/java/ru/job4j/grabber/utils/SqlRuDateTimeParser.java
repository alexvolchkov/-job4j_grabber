package ru.job4j.grabber.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import static java.util.Map.entry;

public class SqlRuDateTimeParser implements DateTimeParser {

    private static final Map<String, String> MONTHS = Map.ofEntries(
            entry("янв", "1"),
            entry("фев", "2"),
            entry("мар", "3"),
            entry("апр", "4"),
            entry("май", "5"),
            entry("июн", "6"),
            entry("июл", "7"),
            entry("авг", "8"),
            entry("сен", "9"),
            entry("окт", "10"),
            entry("ноя", "11"),
            entry("дек", "12")
    );

    @Override
    public LocalDateTime parse(String parse) {
        if (parse == null) {
            throw new IllegalArgumentException("Не передана строка для парсинга даты");
        }
        String[] arrString = parseString(parse, ",");
        validation(arrString, 2);
        return LocalDateTime.of(
                parseDay(arrString[0]),
                parseTime(arrString[1]));
    }

    private void validation(String[] arrString, int length) {
        if (arrString.length != length) {
            throw new IllegalArgumentException("Не верный формат строки для парсинга даты.");
        }
    }

    private LocalTime parseTime(String parse) {
        String[] arrString = parseString(parse, ":");
        validation(arrString, 2);
        return LocalTime.of(Integer.parseInt(arrString[0]), Integer.parseInt(arrString[1]));
    }

    private LocalDate parseDay(String parse) {
        LocalDate rsl;
        String[] arrString = parseString(parse, "\s");
        if (arrString.length == 1 && "вчера".equals(arrString[0])) {
            rsl = LocalDate.now().minusDays(1);
        } else if (arrString.length == 1 && "сегодня".equals(arrString[0])) {
            rsl = LocalDate.now();
        } else {
            validation(arrString, 3);
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("d-M-yy");
            rsl = LocalDate.parse(
                    String.format("%s-%s-%s", arrString[0], MONTHS.get(arrString[1]), arrString[2]),
                    dtf);
        }
        return rsl;
    }

    private static String[] parseString(String parse, String regex) {
        String[] arrayString = parse.split(regex);
        for (String s : arrayString) {
            s.trim();
        }
        return arrayString;
    }
}
