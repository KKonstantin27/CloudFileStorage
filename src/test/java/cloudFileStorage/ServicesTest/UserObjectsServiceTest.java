package cloudFileStorage.ServicesTest;

import cloudFileStorage.dao.UserObjectsDAO;
import cloudFileStorage.dto.UserFileDTO;
import cloudFileStorage.dto.UserFolderDTO;
import cloudFileStorage.dto.UserObjectDTO;
import cloudFileStorage.services.UserObjectsService;
import cloudFileStorage.utils.UserObjectsUtil;
import io.minio.Result;
import io.minio.errors.*;
import io.minio.messages.Item;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
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
import java.util.List;

@SpringBootTest
@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestPropertySource(locations = "classpath:application-test.properties")
public class UserObjectsServiceTest {
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
    private UserObjectsService userObjectsService;

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
    public void testCreateUserStorage() throws ServerException, InsufficientDataException, ErrorResponseException,
            IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException,
            InternalException {

        userObjectsService.createUserStorage(TEST_USER_STORAGE_NAME);
        Iterable<Result<Item>> results = userObjectsDAO.getUserObjects("", false);
        int countObjects = 0;
        for (Result<Item> result : results) {
            countObjects++;
        }

        Assertions.assertEquals(1, countObjects);
    }

    @Test
    @Order(2)
    public void testCreateUserFolder() throws ServerException, InsufficientDataException, ErrorResponseException, IOException,
            NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {

        userObjectsService.createUserFolder(TEST_USER_STORAGE_NAME, new UserFolderDTO(
                "", "TestName", TEST_USER_STORAGE_NAME, ""));
        Iterable<Result<Item>> results = userObjectsDAO.getUserObjects(TEST_USER_STORAGE_NAME, false);
        int countObjects = 0;
        String shortUserFolderName = "";

        for (Result<Item> result : results) {
            shortUserFolderName = userObjectsUtil.getShortUserObjectName(result.get().objectName());
            countObjects++;
        }

        Assertions.assertEquals(1, countObjects);
        Assertions.assertEquals("TestName", shortUserFolderName);
    }

    @Test
    @Order(3)
    public void testUploadUserFolder() throws ServerException, InsufficientDataException, ErrorResponseException, IOException,
            NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {

        MockMultipartFile[] mockMultipartFiles = {new MockMultipartFile("Test", "FolderUploadTest/File.txt",
                "text/plain", "TestFile".getBytes())};

        userObjectsService.uploadUserFolder(TEST_USER_STORAGE_NAME, "", mockMultipartFiles);
        Iterable<Result<Item>> results = userObjectsDAO.getUserObjects(TEST_USER_STORAGE_NAME + "FolderUploadTest" + FOLDER_DELIMITER, false);
        String shortUserFileName = "";
        int countObjects = 0;

        for (Result<Item> result : results) {
            shortUserFileName = userObjectsUtil.getShortUserObjectName(result.get().objectName());
            countObjects++;
        }

        Assertions.assertEquals(1, countObjects);
        Assertions.assertEquals("File.txt", shortUserFileName);
    }

    @Test
    @Order(4)
    public void testUploadUserFiles() throws ServerException, InsufficientDataException, ErrorResponseException, IOException,
            NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {

        MockMultipartFile[] mockMultipartFiles = new MockMultipartFile[2];

        for (int i = 0; i < mockMultipartFiles.length; i++) {
            mockMultipartFiles[i] = new MockMultipartFile("Test" + i, "File" + i + ".txt", "text/plain", "TestFile".getBytes());
        }

        userObjectsService.uploadUserFiles(TEST_USER_STORAGE_NAME, "TestName" + FOLDER_DELIMITER, mockMultipartFiles);
        Iterable<Result<Item>> results = userObjectsDAO.getUserObjects(TEST_USER_STORAGE_NAME + "TestName" + FOLDER_DELIMITER, false);
        String[] shortUserFileNames = new String[2];
        int countObjects = 0;

        for (Result<Item> result : results) {
            shortUserFileNames[countObjects] = userObjectsUtil.getShortUserObjectName(result.get().objectName());
            countObjects++;
        }

        Assertions.assertEquals(2, countObjects);
        Assertions.assertEquals("File0.txt", shortUserFileNames[0]);
        Assertions.assertEquals("File1.txt", shortUserFileNames[1]);
    }

    @Test
    @Order(5)
    public void testRenameUserFolder() throws ServerException, InsufficientDataException, ErrorResponseException, IOException,
            NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {

        userObjectsService.renameUserFolder(TEST_USER_STORAGE_NAME, "TestName", new UserFolderDTO(
                "", "NewTestName", TEST_USER_STORAGE_NAME, ""));

        Iterable<Result<Item>> results = userObjectsDAO.getUserObjects(TEST_USER_STORAGE_NAME, false);
        int countObjects = 0;
        String shortUserFolderName = "";

        for (Result<Item> result : results) {
            shortUserFolderName = userObjectsUtil.getShortUserObjectName(result.get().objectName());
            countObjects++;
        }

        Assertions.assertEquals(2, countObjects);
        Assertions.assertEquals("NewTestName", shortUserFolderName);
    }

    @Test
    @Order(6)
    public void testRenameUserFile() throws ServerException, InsufficientDataException, ErrorResponseException, IOException,
            NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {

        userObjectsService.renameUserFile(TEST_USER_STORAGE_NAME, "File1.txt", new UserFileDTO(
                "", "NewFile1.txt", 0, TEST_USER_STORAGE_NAME, "NewTestName" + FOLDER_DELIMITER));

        Iterable<Result<Item>> results = userObjectsDAO.getUserObjects(TEST_USER_STORAGE_NAME + "NewTestName" + FOLDER_DELIMITER, false);
        int countObjects = 0;
        String shortUserFolderName = "";

        for (Result<Item> result : results) {
            shortUserFolderName = userObjectsUtil.getShortUserObjectName(result.get().objectName());
            countObjects++;
        }

        Assertions.assertEquals(2, countObjects);
        Assertions.assertEquals("NewFile1.txt", shortUserFolderName);
    }

    @Test
    @Order(7)
    public void testSearchUserObjects() throws ServerException, InsufficientDataException, ErrorResponseException, IOException,
            NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {

        List<UserObjectDTO> fileSearchResult = userObjectsService.getUserObjectsBySearchQuery(TEST_USER_STORAGE_NAME, "NewFile1.txt");
        List<UserObjectDTO> folderSearchResult = userObjectsService.getUserObjectsBySearchQuery(TEST_USER_STORAGE_NAME, "NewTestName");

        Assertions.assertEquals(1, fileSearchResult.size());
        Assertions.assertEquals("NewTestName" + FOLDER_DELIMITER + "NewFile1.txt", fileSearchResult.get(0).getName());
        Assertions.assertEquals(1, folderSearchResult.size());
        Assertions.assertEquals("NewTestName" + FOLDER_DELIMITER, folderSearchResult.get(0).getName());
    }

    @Test
    @Order(8)
    public void testDeleteUserFile() throws ServerException, InsufficientDataException, ErrorResponseException, IOException,
            NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {

        userObjectsService.deleteUserFile(TEST_USER_STORAGE_NAME, new UserFileDTO("NewTestName" + FOLDER_DELIMITER + "NewFile1.txt",
                "NewFile1.txt", 0, TEST_USER_STORAGE_NAME, "NewTestName" + FOLDER_DELIMITER));

        Iterable<Result<Item>> results = userObjectsDAO.getUserObjects(TEST_USER_STORAGE_NAME + "NewTestName" + FOLDER_DELIMITER, false);
        int countObjects = 0;
        String shortUserFolderName = "";

        for (Result<Item> result : results) {
            System.out.println(result.get().objectName());
            shortUserFolderName = userObjectsUtil.getShortUserObjectName(result.get().objectName());
            countObjects++;
        }

        Assertions.assertEquals(1, countObjects);
        Assertions.assertEquals("File0.txt", shortUserFolderName);
    }

    @Test
    @Order(9)
    public void testDeleteUserFolder() throws ServerException, InsufficientDataException, ErrorResponseException, IOException,
            NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {

        userObjectsService.deleteUserFolder(TEST_USER_STORAGE_NAME, new UserFolderDTO(
                "NewTestName", "", TEST_USER_STORAGE_NAME, ""));

        Iterable<Result<Item>> results = userObjectsDAO.getUserObjects(TEST_USER_STORAGE_NAME, false);
        int countObjects = 0;

        for (Result<Item> result : results) {
            countObjects++;
        }

        Assertions.assertEquals(1, countObjects);
    }
}
