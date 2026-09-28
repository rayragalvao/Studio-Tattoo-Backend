package hub.orcana.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Files;
import java.nio.file.Path;

@Configuration
public class DotEnvConfig {

    static {
        Path dotenvDirectory = findDotenvDirectory(Path.of("").toAbsolutePath());

        Dotenv dotenv = Dotenv.configure()
                .directory(dotenvDirectory.toString())
                .ignoreIfMalformed()
                .ignoreIfMissing()
                .load();

        dotenv.entries().forEach(entry -> {
            // Só define se a variável de ambiente não estiver já definida
            if (System.getenv(entry.getKey()) == null && System.getProperty(entry.getKey()) == null) {
                System.setProperty(entry.getKey(), entry.getValue());
            }
        });
    }

    private static Path findDotenvDirectory(Path startDirectory) {
        Path directory = startDirectory;

        while (directory != null) {
            if (Files.isRegularFile(directory.resolve(".env"))) {
                return directory;
            }
            directory = directory.getParent();
        }

        return startDirectory;
    }
}