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
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)

/**
 * SequencedCellArray
 */
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2018-10-05T07:30:29.578Z")
public class SequencedCellArray   {
  @JsonProperty("columnId")
  private Integer columnId = null;

  @JsonProperty("columnSequenceNumber")
  private Float columnSequenceNumber = null;

  @JsonProperty("cellValues")
  private List<String> cellValues = null;

  @JsonProperty("cellFormulas")
  private List<String> cellFormulas = null;

  @JsonProperty("cellAccess")
  private List<Integer> cellAccess = null;

  public SequencedCellArray columnId(Integer columnId) {
    this.columnId = columnId;
    return this;
  }

  /**
   * Get columnId
   * @return columnId
   **/
  @JsonProperty("columnId")
  @ApiModelProperty(value = "")
  public Integer getColumnId() {
    return columnId;
  }

  public void setColumnId(Integer columnId) {
    this.columnId = columnId;
  }

  public SequencedCellArray columnSequenceNumber(Float columnSequenceNumber) {
    this.columnSequenceNumber = columnSequenceNumber;
    return this;
  }

  /**
   * Get columnSequenceNumber
   * @return columnSequenceNumber
   **/
  @JsonProperty("columnSequenceNumber")
  @ApiModelProperty(value = "")
  public Float getColumnSequenceNumber() {
    return columnSequenceNumber;
  }

  public void setColumnSequenceNumber(Float columnSequenceNumber) {
    this.columnSequenceNumber = columnSequenceNumber;
  }

  public SequencedCellArray cellValues(List<String> cellValues) {
    this.cellValues = cellValues;
    return this;
  }

  public SequencedCellArray addCellValuesItem(String cellValuesItem) {
    if (this.cellValues == null) {
      this.cellValues = new ArrayList<String>();
    }
    this.cellValues.add(cellValuesItem);
    return this;
  }

  /**
   * Get cellValues
   * @return cellValues
   **/
  @JsonProperty("cellValues")
  @ApiModelProperty(value = "")
  public List<String> getCellValues() {
    return cellValues;
  }

  public void setCellValues(List<String> cellValues) {
    this.cellValues = cellValues;
  }

  public SequencedCellArray cellFormulas(List<String> cellFormulas) {
    this.cellFormulas = cellFormulas;
    return this;
  }

  public SequencedCellArray addCellFormulasItem(String cellFormulasItem) {
    if (this.cellFormulas == null) {
      this.cellFormulas = new ArrayList<String>();
    }
    this.cellFormulas.add(cellFormulasItem);
    return this;
  }

  /**
   * Get cellFormulas
   * @return cellFormulas
   **/
  @JsonProperty("cellFormulas")
  @ApiModelProperty(value = "")
  public List<String> getCellFormulas() {
    return cellFormulas;
  }

  public void setCellFormulas(List<String> cellFormulas) {
    this.cellFormulas = cellFormulas;
  }

  public SequencedCellArray cellAccess(List<Integer> cellAccess) {
    this.cellAccess = cellAccess;
    return this;
  }

  public SequencedCellArray addCellAccessItem(Integer cellAccessItem) {
    if (this.cellAccess == null) {
      this.cellAccess = new ArrayList<Integer>();
    }
    this.cellAccess.add(cellAccessItem);
    return this;
  }

  /**
   * Get cellAccess
   * @return cellAccess
   **/
  @JsonProperty("cellAccess")
  @ApiModelProperty(value = "")
  public List<Integer> getCellAccess() {
    return cellAccess;
  }

  public void setCellAccess(List<Integer> cellAccess) {
    this.cellAccess = cellAccess;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SequencedCellArray sequencedCellArray = (SequencedCellArray) o;
    return Objects.equals(this.columnId, sequencedCellArray.columnId) &&
        Objects.equals(this.columnSequenceNumber, sequencedCellArray.columnSequenceNumber) &&
        Objects.equals(this.cellValues, sequencedCellArray.cellValues) &&
        Objects.equals(this.cellFormulas, sequencedCellArray.cellFormulas) &&
        Objects.equals(this.cellAccess, sequencedCellArray.cellAccess);
  }

  @Override
  public int hashCode() {
    return Objects.hash(columnId, columnSequenceNumber, cellValues, cellFormulas, cellAccess);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SequencedCellArray {\n");
    
    sb.append("    columnId: ").append(toIndentedString(columnId)).append("\n");
    sb.append("    columnSequenceNumber: ").append(toIndentedString(columnSequenceNumber)).append("\n");
    sb.append("    cellValues: ").append(toIndentedString(cellValues)).append("\n");
    sb.append("    cellFormulas: ").append(toIndentedString(cellFormulas)).append("\n");
    sb.append("    cellAccess: ").append(toIndentedString(cellAccess)).append("\n");
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
