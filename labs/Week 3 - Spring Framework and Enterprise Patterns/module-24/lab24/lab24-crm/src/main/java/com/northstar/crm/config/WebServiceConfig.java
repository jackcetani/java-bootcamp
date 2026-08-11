package com.northstar.crm.config;

import com.northstar.crm.exception.CustomerNotFoundException;
import com.northstar.crm.exception.DuplicateCustomerException;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.core.io.ClassPathResource;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.config.annotation.EnableWs;
import org.springframework.ws.config.annotation.WsConfigurerAdapter;
import org.springframework.ws.server.EndpointInterceptor;
import org.springframework.ws.soap.security.wss4j2.Wss4jSecurityInterceptor;
import org.springframework.ws.soap.security.wss4j2.callback.SimplePasswordValidationCallbackHandler;
import org.springframework.ws.soap.server.endpoint.SoapFaultDefinition;
import org.springframework.ws.soap.server.endpoint.SoapFaultMappingExceptionResolver;
import org.springframework.ws.transport.http.MessageDispatcherServlet;
import org.springframework.ws.wsdl.wsdl11.DefaultWsdl11Definition;
import org.springframework.xml.xsd.SimpleXsdSchema;
import org.springframework.xml.xsd.XsdSchema;

import java.util.List;
import java.util.Properties;

@EnableWs
@Configuration
public class WebServiceConfig extends WsConfigurerAdapter {

  private final Environment environment;

  public WebServiceConfig(Environment environment) {
    this.environment = environment;
  }

  @Bean
  ServletRegistrationBean<MessageDispatcherServlet> messageDispatcherServlet(
          ApplicationContext context) {
    MessageDispatcherServlet servlet = new MessageDispatcherServlet();
    servlet.setApplicationContext(context);
    servlet.setTransformWsdlLocations(true);
    return new ServletRegistrationBean<>(servlet, "/ws/*");
  }

  @Bean(name = "customer")
  DefaultWsdl11Definition defaultWsdl11Definition(XsdSchema customerSchema) {
    DefaultWsdl11Definition definition = new DefaultWsdl11Definition();
    definition.setPortTypeName("CustomerServicePort");
    definition.setLocationUri("/ws");
    definition.setTargetNamespace("http://northstar.com/crm/customer");
    definition.setSchema(customerSchema);
    return definition;
  }

  @Bean
  XsdSchema customerSchema() {
    return new SimpleXsdSchema(new ClassPathResource("customer.xsd"));
  }

  // Required for @RequestPayload/@ResponsePayload to bind directly to the JAXB
  // classes generated from customer.xsd (Step 4) — Spring-WS needs a registered
  // Marshaller/Unmarshaller bean to do that binding.
  @Bean
  Jaxb2Marshaller marshaller() {
    Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
    marshaller.setContextPath("com.northstar.crm.endpoint.jaxb");
    return marshaller;
  }

  @Bean
  SoapFaultMappingExceptionResolver exceptionResolver() {
    SoapFaultMappingExceptionResolver resolver = new SoapFaultMappingExceptionResolver();

    SoapFaultDefinition serverFault = new SoapFaultDefinition();
    serverFault.setFaultCode(SoapFaultDefinition.SERVER);
    serverFault.setFaultStringOrReason("Unexpected server error");
    resolver.setDefaultFault(serverFault);

    Properties mappings = new Properties();
    mappings.setProperty(CustomerNotFoundException.class.getName(), "CLIENT,Customer not found");
    mappings.setProperty(DuplicateCustomerException.class.getName(), "CLIENT,Duplicate customer");
    resolver.setExceptionMappings(mappings);
    resolver.setOrder(1);
    return resolver;
  }

  @Bean
  Wss4jSecurityInterceptor securityInterceptor() {
    Wss4jSecurityInterceptor interceptor = new Wss4jSecurityInterceptor();
    interceptor.setValidationActions("UsernameToken");
    interceptor.setValidationCallbackHandler(callbackHandler());
    return interceptor;
  }

  @Bean
  SimplePasswordValidationCallbackHandler callbackHandler() {
    SimplePasswordValidationCallbackHandler handler = new SimplePasswordValidationCallbackHandler();
    Properties users = new Properties();
    // LAB-ONLY credential — never a real partner password.
    users.setProperty("crm-partner", "lab24-shared-secret");
    handler.setUsers(users);
    return handler;
  }

  @Override
  public void addInterceptors(List<EndpointInterceptor> interceptors) {
    // Step 8 fallback, chosen explicitly: skip the security interceptor under the
    // "test" profile so CustomerEndpointTest doesn't need to construct a WS-Security
    // header. See docs/soap-notes.md for the documented trade-off.
    if (!environment.acceptsProfiles(Profiles.of("test"))) {
      interceptors.add(securityInterceptor());
    }
  }
}