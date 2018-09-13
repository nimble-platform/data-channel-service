package eu.nimble.service.datachannel.controller;

import com.google.common.collect.Sets;
import com.google.gson.Gson;
import eu.nimble.service.datachannel.entity.tracing.EpcCodes;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@FixMethodOrder
@RunWith(SpringJUnit4ClassRunner.class)
public class EpcControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DirtiesContext
    public void registerNewCodes() throws Exception {

        Gson gson = new Gson();
        EpcCodes codes = new EpcCodes("234234", Sets.newHashSet("a", "b", "c"));
        String codesString = gson.toJson(codes, EpcCodes.class);

        // register codes
        MockHttpServletRequestBuilder registerRequest = post("/epc/")
                .header(HttpHeaders.AUTHORIZATION, "Bearer SOMETHING")
                .contentType(MediaType.APPLICATION_JSON)
                .content(codesString);
        this.mockMvc.perform(registerRequest).andDo(print()).andExpect(status().isOk());

        // fetch codes again
        MvcResult fetchedResult = this.mockMvc.perform(get("/epc/234234").header("Authorization", "Bearer SOMETHING"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        // check response
        EpcCodes codeResponse = gson.fromJson(fetchedResult.getResponse().getContentAsString(), EpcCodes.class);
        Assert.assertEquals(codeResponse.getOrderId(), "234234");
        Assert.assertEquals(codeResponse.getCodes(), Sets.newHashSet("a", "b", "c"));
    }

    @Test
    @DirtiesContext
    public void removeCodes() throws Exception {

        Gson gson = new Gson();
        EpcCodes codes = new EpcCodes("234234", Sets.newHashSet("a", "b", "c"));
        String codesString = gson.toJson(codes, EpcCodes.class);

        // register codes
        MockHttpServletRequestBuilder registerRequest = post("/epc/")
                .header(HttpHeaders.AUTHORIZATION, "Bearer SOMETHING")
                .contentType(MediaType.APPLICATION_JSON)
                .content(codesString);
        this.mockMvc.perform(registerRequest).andDo(print()).andExpect(status().isOk());

        // remove certain codes
        EpcCodes codesToBeRemoved = new EpcCodes("234234", Sets.newHashSet("a", "b"));
        String codesToBeRemoveString = gson.toJson(codesToBeRemoved, EpcCodes.class);
        MockHttpServletRequestBuilder removeRequest = delete("/epc/")
                .header(HttpHeaders.AUTHORIZATION, "Bearer SOMETHING")
                .contentType(MediaType.APPLICATION_JSON)
                .content(codesToBeRemoveString);
        MvcResult fetchedResult = this.mockMvc.perform(removeRequest)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        // check response
        EpcCodes codeResponse = gson.fromJson(fetchedResult.getResponse().getContentAsString(), EpcCodes.class);
        Assert.assertEquals(codeResponse.getOrderId(), "234234");
        Assert.assertEquals(codeResponse.getCodes(), Sets.newHashSet("c"));
    }

    @Test
    public void codesNotFound() throws Exception {
        this.mockMvc.perform(get("/epc/234234").header("Authorization", "Bearer SOMETHING"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }


}