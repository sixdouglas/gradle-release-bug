package com.utils.client.iam.enterprise.directory.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "iam-enterprise-directory")
public class IamEnterpriseDirectoryConfig {

  private String url;

  private boolean rootUri = true;

  private long connectionTimeout = 5000L; // Millis

  private long readTimeout = 5000L; // Millis

  private String path;

  private String basicAuthUsername;

  private String basicAuthPassword;

  private String apiKey;

  private String healthCheckUrl;

  private int maxInMemorySize = 500 * 1024;

  private boolean withFedAuthentication = true;

  private boolean enableLoggingRequestDetails = false;
}
