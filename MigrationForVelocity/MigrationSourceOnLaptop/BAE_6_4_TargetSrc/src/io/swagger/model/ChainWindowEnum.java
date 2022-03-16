/*
 * Boardwalk Cuboid Services
 * Boardwalk Rest API
 *
 * OpenAPI spec version: 1.0.2
 * Contact: apisupport@boardwalltech.com
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */

package io.swagger.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
/**
 * Gets or Sets ChainWindowEnum
 */
public enum ChainWindowEnum {
  
  LATESTTX("LATESTTX"),
  
  ALLTX("ALLTX"),
  
  ASOFTX("ASOFTX"),
  
  BETWEENTX("BETWEENTX");

  private String value;

  ChainWindowEnum(String value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return String.valueOf(value);
  }

  @JsonCreator
  public static ChainWindowEnum fromValue(String text) {
    for (ChainWindowEnum b : ChainWindowEnum.values()) {
      if (String.valueOf(b.value).equals(text)) {
        return b;
      }
    }
    return null;
  }
}
