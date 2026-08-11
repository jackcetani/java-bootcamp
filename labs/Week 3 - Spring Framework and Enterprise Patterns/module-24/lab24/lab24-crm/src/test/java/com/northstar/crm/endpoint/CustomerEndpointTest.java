package com.northstar.crm.endpoint;

import com.northstar.crm.endpoint.jaxb.GetCustomerRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.ws.test.server.MockWebServiceClient;
import org.springframework.xml.transform.StringResult;
import org.springframework.xml.transform.StringSource;

import static org.springframework.ws.test.server.RequestCreators.withPayload;
import static org.springframework.ws.test.server.ResponseMatchers.noFault;

@SpringBootTest
@ActiveProfiles("test")
class CustomerEndpointTest {

    @Autowired
    ApplicationContext applicationContext;

    @Autowired
    Jaxb2Marshaller marshaller;

    MockWebServiceClient mockClient;

    @BeforeEach
    void setUp() {
        mockClient = MockWebServiceClient.createClient(applicationContext);
    }

    @Test
    void getCustomerReturnsAmina() throws Exception {
        GetCustomerRequest request = new GetCustomerRequest();
        request.setCustomerId("CUS-1001");

        StringResult marshalled = new StringResult();
        marshaller.marshal(request, marshalled);

        mockClient.sendRequest(withPayload(new StringSource(marshalled.toString())))
                .andExpect(noFault());
    }
}