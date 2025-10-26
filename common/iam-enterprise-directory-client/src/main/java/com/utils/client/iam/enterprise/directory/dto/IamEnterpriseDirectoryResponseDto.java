package com.utils.client.iam.enterprise.directory.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IamEnterpriseDirectoryResponseDto {

  @JsonProperty("hydra:member")
  private List<IamEnterpriseDirectoryUserDto> users;
}
