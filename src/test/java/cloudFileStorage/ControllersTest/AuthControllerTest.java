package cloudFileStorage.ControllersTest;

import cloudFileStorage.configs.SecurityConfig;
import cloudFileStorage.controllers.AuthController;
import cloudFileStorage.dto.UserDTO;
import cloudFileStorage.models.User;
import cloudFileStorage.services.CustomUserDetailsService;
import cloudFileStorage.services.UserObjectsService;
import cloudFileStorage.utils.UsersMapper;
import cloudFileStorage.utils.UsersValidator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(value = AuthController.class)
@Import({SecurityConfig.class, UsersValidator.class})
public class AuthControllerTest {

    @Autowired
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    private MockMvc mockMvc;

    @MockBean
    CustomUserDetailsService customUserDetailsService;

    @MockBean
    UserObjectsService userObjectsService;

    @Autowired
    UsersValidator usersValidator;

    @MockBean
    UsersMapper usersMapper;

    @Test
    public void validSignUpShouldRedirectToSuccessPage() throws Exception {
        UserDTO userDTO = new UserDTO("TestName", "TestPassword", "TestPassword");
        User savedUser = new User(userDTO.getUsername(), userDTO.getPassword());

        when(customUserDetailsService.signUp(usersMapper.convertToUser(userDTO))).thenReturn(savedUser);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/auth/signUp")
                        .with(csrf()).flashAttr("userDTO", userDTO)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(redirectedUrl("/auth/success"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    public void usernameWithInvalidCharShouldReturnSignUpTemplate() throws Exception {
        UserDTO userDTO = new UserDTO("Test Name", "TestPassword", "TestPassword");

        mockMvc.perform(MockMvcRequestBuilders
                .post("/auth/signUp")
                .with(csrf()).flashAttr("userDTO", userDTO)
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(view().name("auth/signUp"))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    public void shortUsernameShouldReturnSignUpTemplate() throws Exception {
        UserDTO userDTO = new UserDTO("Tes", "TestPassword", "TestPassword");
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/auth/signUp")
                        .with(csrf()).flashAttr("userDTO", userDTO)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(view().name("auth/signUp"))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    public void longUsernameShouldReturnSignUpTemplate() throws Exception {
        UserDTO userDTO = new UserDTO("TestNameTestNameTestNameTestNameTestName", "TestPassword", "TestPassword");
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/auth/signUp")
                        .with(csrf()).flashAttr("userDTO", userDTO)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(view().name("auth/signUp"))
                .andExpect(status().is2xxSuccessful());
    }


    @Test
    public void emptyUsernameShouldReturnSignUpTemplate() throws Exception {
        UserDTO userDTO = new UserDTO("", "TestPassword", "TestPassword");
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/auth/signUp")
                        .with(csrf()).flashAttr("userDTO", userDTO)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(view().name("auth/signUp"))
                .andExpect(status().is2xxSuccessful());
    }


    @Test
    public void passwordWithInvalidCharShouldReturnSignUpTemplate() throws Exception {
        UserDTO userDTO = new UserDTO("TestName", "Test Password", "Test Password");
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/auth/signUp")
                        .with(csrf()).flashAttr("userDTO", userDTO)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(view().name("auth/signUp"))
                .andExpect(status().is2xxSuccessful());
    }
    @Test
    public void shortPasswordShouldReturnSignUpTemplate() throws Exception {
        UserDTO userDTO = new UserDTO("TestName", "Test", "Test");
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/auth/signUp")
                        .with(csrf()).flashAttr("userDTO", userDTO)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(view().name("auth/signUp"))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    public void longPasswordShouldReturnSignUpTemplate() throws Exception {
        UserDTO userDTO = new UserDTO("TestName", "TestPasswordTestPasswordTestPasswordTestPassword", "TestPasswordTestPasswordTestPasswordTestPassword");
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/auth/signUp")
                        .with(csrf()).flashAttr("userDTO", userDTO)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(view().name("auth/signUp"))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    public void emptyPasswordShouldReturnSignUpTemplate() throws Exception {
        UserDTO userDTO = new UserDTO("TestName", "", "");
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/auth/signUp")
                        .with(csrf()).flashAttr("userDTO", userDTO)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(view().name("auth/signUp"))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    public void passwordsDoNotMatchShouldReturnSignUpTemplate() throws Exception {
        UserDTO userDTO = new UserDTO("TestName", "TestPassword1", "TestPassword2");
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/auth/signUp")
                        .with(csrf()).flashAttr("userDTO", userDTO)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(view().name("auth/signUp"))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    public void signUpWithExistedUsernameShouldReturnSignUpTemplate() throws Exception {
        UserDTO userDTO = new UserDTO("TestName", "TestPassword", "TestPassword");
        User savedUser = new User(userDTO.getUsername(), userDTO.getPassword());

        when(customUserDetailsService.signUp(usersMapper.convertToUser(userDTO))).thenReturn(savedUser);
        when(customUserDetailsService.loadUserOptionalByUsername(userDTO.getUsername())).thenReturn(Optional.of(savedUser));

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/auth/signUp")
                        .with(csrf()).flashAttr("userDTO", userDTO)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(view().name("auth/signUp"))
                .andExpect(status().is2xxSuccessful());
    }

//    @Test
//    public void validSignInShouldRedirectToIndexPage() throws Exception {
//        UserDTO userDTO = new UserDTO("TestName", "TestPassword", "TestPassword");
//        User savedUser = new User(userDTO.getUsername(), userDTO.getPassword());
//
//        when(userDetailsService.signUp(usersMapper.convertToUser(userDTO))).thenReturn(savedUser);
//
//        mockMvc.perform(MockMvcRequestBuilders
//                        .post("/process_signIn").with(csrf())
//                        .contentType(MediaType.MULTIPART_FORM_DATA)
//                        .principal(new UserPrincipal("TestName")))
//                .andExpect(redirectedUrl("/"))
//                .andExpect(status().is3xxRedirection());
//
//    }
}
