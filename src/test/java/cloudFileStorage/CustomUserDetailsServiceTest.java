package cloudFileStorage;

import cloudFileStorage.models.User;
import cloudFileStorage.repositories.UsersRepository;
import cloudFileStorage.services.UserDetailsService;
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
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Testcontainers
@TestPropertySource(locations="classpath:application-test.properties")
public class CustomUserDetailsServiceTest {

    @Container
    private static final MySQLContainer<?> mySQLContainer = new MySQLContainer<>("mysql")
            .withExposedPorts(3306)
            .withUsername("CFS_Test_User")
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
    private UserDetailsService userDetailsService;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mySQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", mySQLContainer::getUsername);
        registry.add("spring.datasource.password", mySQLContainer::getPassword);
        registry.add("minio.client-user", minioContainer::getUserName);
        registry.add("minio.client-password", minioContainer::getPassword);
        registry.add("minio.client-endpoint", minioContainer::getS3URL);
        registry.add("spring.data.redis.host", redisContainer::getHost);
        registry.add("spring.data.redis.port", () -> redisContainer.getMappedPort(6379));
        mySQLContainer.withInitScript("scripts/init-test.sql");
    }

    @BeforeEach
    public void clearDB() {
        usersRepository.deleteAll();
    }

    @Test
    public void testSignUp() {
        userDetailsService.signUp(new User("TestName", "TestPassword"));
        Assertions.assertEquals(1, usersRepository.count());
    }

    @Test
    public void signUpNotValidatedDataShouldThrowException() {
        userDetailsService.signUp(new User("TestName", "TestPassword"));
        DataIntegrityViolationException e = assertThrows(DataIntegrityViolationException.class,
                () -> userDetailsService.signUp(new User("TestName", "TestPassword")));
    }

}
