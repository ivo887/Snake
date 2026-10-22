package snake.persistence;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public record SaveInfo(String name, File file) {
    @Override
    public String toString() {
        String date = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date(file.lastModified()));
        return name + "   (" + date + ")";
    }
}
