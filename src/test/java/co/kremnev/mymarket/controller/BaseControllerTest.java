package co.kremnev.mymarket.controller;

import co.kremnev.mymarket.service.CartService;
import co.kremnev.mymarket.service.ItemService;
import co.kremnev.mymarket.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
public abstract class BaseControllerTest {

    @Autowired
    protected WebApplicationContext webContext;

    @MockitoBean
    protected CartService cartService;

    @MockitoBean
    protected OrderService orderService;

    @MockitoBean
    protected ItemService itemService;

    protected MockMvc mockMvc;

    @BeforeEach
    void baseSetup() {
        mockMvc = MockMvcBuilders
            .webAppContextSetup(webContext)
            .build();
    }
}
