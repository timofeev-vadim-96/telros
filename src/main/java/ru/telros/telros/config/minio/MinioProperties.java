package ru.telros.telros.config.minio;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

@ConfigurationProperties(prefix = "minio")
@Getter
public class MinioProperties {
    private final String address;

    private final int port;

    private final boolean tsl;

    private final String login;

    private final String password;

    @ConstructorBinding
    public MinioProperties(String address, int port, boolean tsl, String login, String password) {
        this.address = address;
        this.port = port;
        this.tsl = tsl;
        this.login = login;
        this.password = password;
    }
}
