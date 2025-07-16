package api.adapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime>{
    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm dd.MM.yy");
    // public static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm dd.MM.yy");

    @Override
    public void write(final JsonWriter jsonWriter, final LocalDateTime localDateTime) throws IOException {
        if (localDateTime != null) { // Проверяем, что localDateTime не равно null
            jsonWriter.value(localDateTime.format(dtf)); // Форматируем localDateTime и записываем его в JSON
        } else {
            jsonWriter.value(""); // Если localDateTime равно null, записываем пустую строку в JSON
        }
    }

    @Override
    public LocalDateTime read(final JsonReader jsonReader) throws IOException {
        String startTimeStr = jsonReader.nextString();
        if (!startTimeStr.isEmpty()) {
            return LocalDateTime.parse(startTimeStr, dtf);
        }
        return null;
    }

}