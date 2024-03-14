package cloudFileStorage.ServicesTest;

import cloudFileStorage.models.User;
import cloudFileStorage.repositories.UsersRepository;
import cloudFileStorage.services.CustomUserDetailsService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MinIOContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Testcontainers
@TestPropertySource(locations = "classpath:application-test.properties")
public class CustomUserDetailsServiceTest {
    @Container
    private static final PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres")
            .withInitScript("scripts/init-test.sql")
            .withExposedPorts(5432)
            .withUsername("postgres")
            .withPassword("CFS_Test_password")
            .withDatabaseName("CloudFileStorageTest");

    @Container
    private static final MinIOContainer minioContainer = new MinIOContainer("minio/minio")
            .withCommand("server /data")
            .withExposedPorts(9000)
            .withUserName("minio")
            .withPassword("password");
    @Container
    private static final GenericContainer<?> redisContainer = new GenericContainer<>("redis")
            .withExposedPorts(6379);
    @Autowired
    private UsersRepository usersRepository;
    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
        registry.add("minio.client-user", minioContainer::getUserName);
        registry.add("minio.client-password", minioContainer::getPassword);
        registry.add("minio.client-endpoint", minioContainer::getS3URL);
        registry.add("spring.data.redis.host", redisContainer::getHost);
        registry.add("spring.data.redis.port", () -> redisContainer.getMappedPort(6379));
    }

    @BeforeEach
    public void clearDB() {
        usersRepository.deleteAll();
    }

    @Test
    public void testSignUp() {
        customUserDetailsService.signUp(new User("TestName", "TestPassword"));

        Assertions.assertEquals(1, usersRepository.count());
    }

    @Test
    public void signUpWithExistedUsernameShouldThrowException() {
        customUserDetailsService.signUp(new User("TestName", "TestPassword"));

        DataIntegrityViolationException e = assertThrows(DataIntegrityViolationException.class,
                () -> customUserDetailsService.signUp(new User("TestName", "TestPassword")));
    }
}
