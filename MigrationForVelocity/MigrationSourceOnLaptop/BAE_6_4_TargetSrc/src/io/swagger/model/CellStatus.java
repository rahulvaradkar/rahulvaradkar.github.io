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
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
/**
 * CellStatus
 */
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2018-10-05T07:30:29.578Z")
public class CellStatus   {
  @JsonProperty("cellStatusId")
  private Integer cellStatusId = null;

  @JsonProperty("cellId")
  private Integer cellId = null;

  @JsonProperty("isActive")
  private Boolean isActive = null;

  @JsonProperty("txId")
  private Integer txId = null;

  public CellStatus cellStatusId(Integer cellStatusId) {
    this.cellStatusId = cellStatusId;
    return this;
  }

  /**
   * Get cellStatusId
   * @return cellStatusId
   **/
  @JsonProperty("cellStatusId")
  @ApiModelProperty(value = "")
  public Integer getCellStatusId() {
    return cellStatusId;
  }

  public void setCellStatusId(Integer cellStatusId) {
    this.cellStatusId = cellStatusId;
  }

  public CellStatus cellId(Integer cellId) {
    this.cellId = cellId;
    return this;
  }

  /**
   * Get cellId
   * @return cellId
   **/
  @JsonProperty("cellId")
  @ApiModelProperty(value = "")
  public Integer getCellId() {
    return cellId;
  }

  public void setCellId(Integer cellId) {
    this.cellId = cellId;
  }

  public CellStatus isActive(Boolean isActive) {
    this.isActive = isActive;
    return this;
  }

  /**
   * Get isActive
   * @return isActive
   **/
  @JsonProperty("isActive")
  @ApiModelProperty(value = "")
  public Boolean isIsActive() {
    return isActive;
  }

  public void setIsActive(Boolean isActive) {
    this.isActive = isActive;
  }

  public CellStatus txId(Integer txId) {
    this.txId = txId;
    return this;
  }

  /**
   * Get txId
   * @return txId
   **/
  @JsonProperty("txId")
  @ApiModelProperty(value = "")
  public Integer getTxId() {
    return txId;
  }

  public void setTxId(Integer txId) {
    this.txId = txId;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CellStatus cellStatus = (CellStatus) o;
    return Objects.equals(this.cellStatusId, cellStatus.cellStatusId) &&
        Objects.equals(this.cellId, cellStatus.cellId) &&
        Objects.equals(this.isActive, cellStatus.isActive) &&
        Objects.equals(this.txId, cellStatus.txId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(cellStatusId, cellId, isActive, txId);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CellStatus {\n");
    
    sb.append("    cellStatusId: ").append(toIndentedString(cellStatusId)).append("\n");
    sb.append("    cellId: ").append(toIndentedString(cellId)).append("\n");
    sb.append("    isActive: ").append(toIndentedString(isActive)).append("\n");
    sb.append("    txId: ").append(toIndentedString(txId)).append("\n");
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
