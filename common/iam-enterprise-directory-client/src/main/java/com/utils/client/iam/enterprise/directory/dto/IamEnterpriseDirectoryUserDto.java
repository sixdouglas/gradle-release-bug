package com.utils.client.iam.enterprise.directory.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class IamEnterpriseDirectoryUserDto {

  private String bn;
  private String uuid;
  private List<String> givenName;
  private List<String> mail;

  public String getFullName() {
    if (
      this.bn != null && this.givenName != null && !this.givenName.isEmpty()
    ) {
      return this.getGivenName() + " " + this.getBn();
    } else return "UNKNOWN";
  }
}
