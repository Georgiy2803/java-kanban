package api.adapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime>{
    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm dd.MM.yy");

    @Override
    public void write(final JsonWriter jsonWriter, final LocalDateTime localDateTime) throws IOException {
        if (localDateTime != null) {
            jsonWriter.value(localDateTime.format(dtf));
        } else {
            jsonWriter.value("");
        }
    }

    @Override
    public LocalDateTime read(final JsonReader jsonReader) throws IOException {
        String startTimeStr = jsonReader.nextString();
        if (!startTimeStr.isEmpty()) {
            try {
                return LocalDateTime.parse(startTimeStr, dtf);
            } catch (DateTimeParseException e) {
                throw new IllegalArgumentException("Недопустимый формат даты для LocalDateTime: " + startTimeStr, e);
            }
        }
        return null;
    }
}