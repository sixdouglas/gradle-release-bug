package com.utils.client.iam.enterprise.directory;

import com.utils.client.iam.enterprise.directory.config.IamEnterpriseDirectoryConfig;
import com.utils.client.iam.enterprise.directory.dto.IamEnterpriseDirectoryResponseDto;
import com.utils.client.iam.enterprise.directory.dto.IamEnterpriseDirectoryUserDto;
import java.util.List;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestOperations;

@Component
@Slf4j
public class IamEnterpriseDirectoryClient {

  private static final String AUTHORIZATION_HEADER_VALUE_PREFIX = "Bearer ";
  private static final String X_API_KEY = "X-Api-Key";
  private final IamEnterpriseDirectoryConfig config;
  private RestOperations rest;

  public IamEnterpriseDirectoryClient(
    IamEnterpriseDirectoryConfig iamEnterpriseDirectoryConfig
  ) {
    this.config = iamEnterpriseDirectoryConfig;
    rest = new RestTemplateBuilder().rootUri(config.getUrl()).build();
  }

  public IamEnterpriseDirectoryUserDto getUser(String identification) {
    log.info(
      "START - Getting user information from iam enterprise directory for identification {}",
      identification
    );
    MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
    headers.add(X_API_KEY, config.getApiKey());
    HttpEntity<Object> entity = new HttpEntity<>(headers);
    ParameterizedTypeReference<IamEnterpriseDirectoryResponseDto> responseType =
      new ParameterizedTypeReference<>() {};
    try {
      ResponseEntity<IamEnterpriseDirectoryResponseDto> responseEntity =
        rest.exchange(
          config.getPath() +
            "?uid=" +
            identification +
            "&fields=bn,givenName,mail",
          HttpMethod.GET,
          entity,
          responseType
        );
      if (
        responseEntity.getStatusCode().is2xxSuccessful() &&
        responseEntity.hasBody()
      ) {
        log.debug(
          "Got user informations from iam enterprise directory for id {}",
          identification
        );
        var body = responseEntity.getBody();
        if (
          body != null && body.getUsers() != null && !body.getUsers().isEmpty()
        ) {
          return body.getUsers().getFirst();
        }
      }
    } catch (Exception e) {
      log.warn(
        "Can't retrieve user informations from iam enterprise directory for id " +
          identification +
          " caused by : ",
        e
      );
      throw e;
    }

    log.warn(
      "Can't get user informations from iam enterprise directory for id {}",
      identification
    );
    throw new RuntimeException(
      "An error has occurred when retrieve user informations from iam enterprise directory"
    );
  }
}
