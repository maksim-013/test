package localhost.test.service;

import static java.lang.String.format;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import localhost.test.exception.AppException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class FileService {
  private final static String ERR_TEXT =
      "Ошибка при %s. Проверьте наличие файла в пути %s";

  // возможность задавать свои пути до файлов для чтения и записи
  @Value("${path.read}")
  private String pathRead;
  @Value("${path.write}")
  private String pathWrite;

  // подсчет количества строк, начинающихся с указанной подстроки
  public Map<String, Long> count(final String startsWith) {
    try (final BufferedReader reader = new BufferedReader(
        new FileReader(pathRead))) {
      Map<String, Long> map = new TreeMap<>();
      String line = reader.readLine();
      while (line != null) {
        line = replaceString(line);
        // если содержаться символы, отличные от кириллицы, бросаю исключение
        if (!Pattern.compile("^[а-я]+$").matcher(line).find()) {
          throw new AppException("Строки должны содержать только кириллицу");
        }
        if (line.length() >= startsWith.length() + 1) {
          if (line.startsWith(startsWith)) {
            map.merge(
                startsWith + line.charAt(startsWith.length()),
                1L,
                Long::sum);
          }
        }
        line = reader.readLine();
      }
      return map;
    } catch (IOException e) {
      throw new AppException(errTextRead());
    }
  }

  /*
    для сортировки строк, начинающихся на указанную подстроку,
    использую реализацию TreeSet
    (если по условию строки могут дублироваться,
    то можно взять List и применить Collections.sort())
   */
  public Set<String> read(final Predicate<String> condition) {
    try (final BufferedReader reader = new BufferedReader(
        new FileReader(pathRead))) {
      Set<String> lineSet = new TreeSet<>();
      String line = reader.readLine();
      while (line != null) {
        line = replaceString(line);
        if (condition.test(line)) {
          lineSet.add(line);
        }
        line = reader.readLine();
      }
      return lineSet;
    } catch (IOException e) {
      throw new AppException(errTextRead());
    }
  }

  // запись в новый документ отсортированных строк
  public void write(final Set<String> lineString) {
    try (final FileWriter writer = new FileWriter(pathWrite, true)) {
      for (String line : lineString) {
        writer.write(format("%s\n", line));
      }
    } catch (IOException e) {
      throw new AppException(format(ERR_TEXT, "записи", pathWrite));
    }
  }

  // превращаю все в нижний регистр и заменяю ё на е
  private String replaceString(String line) {
    return line.toLowerCase().replace("ё", "е");
  }

  // текст ошибки чтения
  private String errTextRead() {
    return format(ERR_TEXT, "чтении", pathRead);
  }
}
