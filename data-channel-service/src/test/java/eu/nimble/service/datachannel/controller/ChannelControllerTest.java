package eu.nimble.service.datachannel.controller;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@FixMethodOrder
@RunWith(SpringJUnit4ClassRunner.class)
public class ChannelControllerTest {

    @Test
    @DirtiesContext
    public void createChannelTest() throws Exception {

        // TODO
    }

    @Test
    @DirtiesContext
    public void closeChannelTest() throws Exception {

        // TODO
    }
    @Test
    @DirtiesContext
    public void addSensorsForChannel() throws Exception {

        // TODO
    }

    @Test
    @DirtiesContext
    public void removeSensorsForChannel() throws Exception {

        // TODO
    }

}
