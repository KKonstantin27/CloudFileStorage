package cloudFileStorage.UtilsTest;

import cloudFileStorage.dao.UserObjectsDAO;
import cloudFileStorage.utils.UserObjectsUtil;
import io.minio.errors.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MinIOContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@SpringBootTest
@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestPropertySource(locations = "classpath:application-test.properties")
public class UserObjectsUtilTest {

    private static final String TEST_USER_STORAGE_NAME = "user-1-files/";
    private static final String FOLDER_DELIMITER = "/";
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
    private UserObjectsUtil userObjectsUtil;

    @Autowired
    private UserObjectsDAO userObjectsDAO;

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

    @Test
    @Order(1)
    public void testBuildUserObjectPathWithoutStorageName() {
        String userObjectPath = userObjectsUtil.buildUserObjectPathWithoutStorageName(TEST_USER_STORAGE_NAME
                + "TestFolder" + FOLDER_DELIMITER
                + "TestFolder1" + FOLDER_DELIMITER
                + "TestFile1.txt");

        Assertions.assertEquals("TestFolder" + FOLDER_DELIMITER + "TestFolder1" + FOLDER_DELIMITER, userObjectPath);
    }

    @Test
    @Order(1)
    public void testGetShortUserObjectName() {
        String shortUserObjectName = userObjectsUtil.getShortUserObjectName(TEST_USER_STORAGE_NAME
                + "TestFolder" + FOLDER_DELIMITER
                + "TestFolder1" + FOLDER_DELIMITER
                + "TestFile1.txt");

        Assertions.assertEquals("TestFile1.txt", shortUserObjectName);
    }

    @Test
    @Order(2)
    public void testBuildUserObjectNameWithoutStorageName() {
        String userFileName = userObjectsUtil.buildUserObjectNameWithoutStorageName(TEST_USER_STORAGE_NAME
                + "TestFolder" + FOLDER_DELIMITER
                + "TestFolder1" + FOLDER_DELIMITER
                + "TestFile1.txt");

        String userFolderName = userObjectsUtil.buildUserObjectNameWithoutStorageName(TEST_USER_STORAGE_NAME
                + "TestFolder" + FOLDER_DELIMITER);

        Assertions.assertEquals("TestFolder" + FOLDER_DELIMITER
                + "TestFolder1" + FOLDER_DELIMITER
                + "TestFile1.txt", userFileName);
        Assertions.assertEquals("TestFolder" + FOLDER_DELIMITER, userFolderName);
    }

    @Test
    @Order(3)
    public void testIsDir() {
        String userFileName = TEST_USER_STORAGE_NAME
                + "TestFolder" + FOLDER_DELIMITER
                + "TestFolder1" + FOLDER_DELIMITER
                + "TestFile1.txt";

        String userFolderName = TEST_USER_STORAGE_NAME
                + "TestFolder" + FOLDER_DELIMITER
                + "TestFolder1" + FOLDER_DELIMITER;

        Assertions.assertTrue(userObjectsUtil.isDir(userFolderName));
        Assertions.assertFalse(userObjectsUtil.isDir(userFileName));
    }

    @Test
    @Order(3)
    public void testIsUserObjectNameBusy() throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        String createdUserFolderName1 = TEST_USER_STORAGE_NAME + "TestName1";
        String createdUserFolderName2 = TEST_USER_STORAGE_NAME + "TestName2";
        userObjectsDAO.createUserFolder(createdUserFolderName1 + FOLDER_DELIMITER);
        userObjectsDAO.createUserFolder(createdUserFolderName1 + " (1)" + FOLDER_DELIMITER);
        String uniqueUserObjectName1 = userObjectsUtil.buildUniqueUserObjectName(TEST_USER_STORAGE_NAME
                + "TestName1", TEST_USER_STORAGE_NAME)
                + FOLDER_DELIMITER;
        String uniqueUserObjectName2 = userObjectsUtil.buildUniqueUserObjectName(TEST_USER_STORAGE_NAME
                + "TestName2", TEST_USER_STORAGE_NAME)
                + FOLDER_DELIMITER;

        Assertions.assertEquals(createdUserFolderName1 + " (2)" + FOLDER_DELIMITER, uniqueUserObjectName1);
        Assertions.assertEquals(createdUserFolderName2 + FOLDER_DELIMITER, uniqueUserObjectName2);
    }
}
