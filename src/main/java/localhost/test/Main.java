package localhost.test;

import java.util.Objects;
import localhost.test.exception.AppException;
import localhost.test.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@RequiredArgsConstructor
@SpringBootApplication
public class Main {

	// возможность изменять размерность коллекции для компьютеров разных мощностей
	@Value("${maxValue}")
	private Long maxValue;

	private final FileService service;

	public static void main(String[] args) {
		SpringApplication.run(Main.class, args);
	}

	// запуск метода
	@Bean
	void start() {
		try {
			recursion("");
			System.out.println("Программа завершилась успешно");
		} catch (AppException e) {
			/*
				это простое консольное приложение,
				поэтому убрал логи и оставил вывод через sout;
				в этом блоке отлавливаются возможные клиентские ошибки
			 */
			System.out.printf("%s!", e.getMessage());
		}
	}

	/*
		рекурсивный метод: если на определенный символ начинаются много строк,
		то берутся первые 2, 3, ... n символов
	 */
	private void recursion(final String startsWith) {
		service.count(startsWith).forEach((start, quantity) -> {
			if (quantity < maxValue) {
				service.write(service.read(line -> line.startsWith(start)));
			} else {
				/*
					если для проверки требуются n символа, а в строке из всего n,
					то запись происходит сразу
				 */
				service.write(service.read(line -> line.length() == start.length()
						&& Objects.equals(line, start)));
				recursion(start);
			}
		});
	}
}
