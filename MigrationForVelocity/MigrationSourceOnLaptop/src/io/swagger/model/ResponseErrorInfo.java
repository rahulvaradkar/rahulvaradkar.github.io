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

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.model.ErrorRequestObject;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)

/**
 * ResponseErrorInfo
 */
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2018-10-05T07:30:29.578Z")
public class ResponseErrorInfo   {
  @JsonProperty("errorMessage")
  private String errorMessage = null;

  @JsonProperty("errorDetails")
  private List<ErrorRequestObject> errorDetails = null;

  public ResponseErrorInfo errorMessage(String errorMessage) {
    this.errorMessage = errorMessage;
    return this;
  }

  /**
   * Get errorMessage
   * @return errorMessage
   **/
  @JsonProperty("errorMessage")
  @ApiModelProperty(value = "")
  public String getErrorMessage() {
    return errorMessage;
  }

  public void setErrorMessage(String errorMessage) {
    this.errorMessage = errorMessage;
  }

  public ResponseErrorInfo errorDetails(List<ErrorRequestObject> errorDetails) {
    this.errorDetails = errorDetails;
    return this;
  }

  public ResponseErrorInfo addErrorDetailsItem(ErrorRequestObject errorDetailsItem) {
    if (this.errorDetails == null) {
      this.errorDetails = new ArrayList<ErrorRequestObject>();
    }
    this.errorDetails.add(errorDetailsItem);
    return this;
  }

  /**
   * Get errorDetails
   * @return errorDetails
   **/
  @JsonProperty("errorDetails")
  @ApiModelProperty(value = "")
  public List<ErrorRequestObject> getErrorDetails() {
    return errorDetails;
  }

  public void setErrorDetails(List<ErrorRequestObject> errorDetails) {
    this.errorDetails = errorDetails;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ResponseErrorInfo responseErrorInfo = (ResponseErrorInfo) o;
    return Objects.equals(this.errorMessage, responseErrorInfo.errorMessage) &&
        Objects.equals(this.errorDetails, responseErrorInfo.errorDetails);
  }

  @Override
  public int hashCode() {
    return Objects.hash(errorMessage, errorDetails);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ResponseErrorInfo {\n");
    
    sb.append("    errorMessage: ").append(toIndentedString(errorMessage)).append("\n");
    sb.append("    errorDetails: ").append(toIndentedString(errorDetails)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

