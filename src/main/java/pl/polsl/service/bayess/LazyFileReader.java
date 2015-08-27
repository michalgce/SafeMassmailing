package pl.polsl.service.bayess;

import org.mapdb.HTreeMap;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class LazyFileReader {

    protected Long counter;

    public LazyFileReader() {
        counter = 0L;
    }

    public void buildArrayWord(final String file, final HTreeMap<Long, String> wordMap) {
        System.out.println(file);

        try {
            Stream<String> lines = Files.lines(Paths.get(file), StandardCharsets.ISO_8859_1);
            writeWordToMap(wordMap, lines);
            lines.close();
        } catch (IOException e) {
           System.out.println("KECZ");
        }
    }

    public void writeWordToMap(HTreeMap<Long, String> map, Stream<String> lines) {
        lines.forEach(line -> {
            String[] split = line.split("\\W");
            for (String s : split) {
                map.put(counter++, s);
            }
        });

        System.out.println(counter);
    }

}
