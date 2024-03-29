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
import io.swagger.model.NeighborhoodPath;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)

/**
 * GridInfo
 */
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2018-10-05T07:30:29.578Z")
public class GridInfo   {
  @JsonProperty("gridId")
  private Integer gridId = null;

  @JsonProperty("gridName")
  private String gridName = null;

  @JsonProperty("gridPurpose")
  private String gridPurpose = null;

  @JsonProperty("view")
  private String view = null;

  @JsonProperty("importTxId")
  private Integer importTxId = null;

  @JsonProperty("exportTxId")
  private Integer exportTxId = null;

  @JsonProperty("filter")
  private String filter = null;

  @JsonProperty("asOfTxId")
  private Integer asOfTxId = null;

  @JsonProperty("baselineId")
  private Integer baselineId = null;

  @JsonProperty("serverName")
  private String serverName = null;

  @JsonProperty("serverURL")
  private String serverURL = null;

  @JsonProperty("collabId")
  private Integer collabId = null;

  @JsonProperty("wbId")
  private Integer wbId = null;

  @JsonProperty("memberId")
  private Integer memberId = null;

  @JsonProperty("userId")
  private Integer userId = null;

  @JsonProperty("nhId")
  private Integer nhId = null;

  @JsonProperty("rowCount")
  private Integer rowCount = null;

  @JsonProperty("colCount")
  private Integer colCount = null;

  @JsonProperty("maxTxId")
  private Integer maxTxId = null;

  @JsonProperty("mode")
  private Integer mode = null;

  @JsonProperty("criteriaTableId")
  private Integer criteriaTableId = null;

  @JsonProperty("neighborhoodHeirarchy")
  private NeighborhoodPath neighborhoodHeirarchy = null;

  @JsonProperty("creationTxId")
  private Integer creationTxId = null;

  @JsonProperty("peerAccess")
  private Integer peerAccess = null;

  @JsonProperty("privateAccess")
  private Integer privateAccess = null;

  @JsonProperty("friendAccess")
  private Integer friendAccess = null;

  @JsonProperty("isActive")
  private Boolean isActive = null;

  @JsonProperty("sequenceNumber")
  private Float sequenceNumber = null;

  @JsonProperty("isLocked")
  private Boolean isLocked = null;

  @JsonProperty("lockTxId")
  private Integer lockTxId = null;

  public GridInfo gridId(Integer gridId) {
    this.gridId = gridId;
    return this;
  }

  /**
   * Get gridId
   * @return gridId
   **/
  @JsonProperty("gridId")
  @ApiModelProperty(value = "")
  public Integer getGridId() {
    return gridId;
  }

  public void setGridId(Integer gridId) {
    this.gridId = gridId;
  }

  public GridInfo gridName(String gridName) {
    this.gridName = gridName;
    return this;
  }

  /**
   * Get gridName
   * @return gridName
   **/
  @JsonProperty("gridName")
  @ApiModelProperty(value = "")
  public String getGridName() {
    return gridName;
  }

  public void setGridName(String gridName) {
    this.gridName = gridName;
  }

  public GridInfo gridPurpose(String gridPurpose) {
    this.gridPurpose = gridPurpose;
    return this;
  }

  /**
   * Get gridPurpose
   * @return gridPurpose
   **/
  @JsonProperty("gridPurpose")
  @ApiModelProperty(value = "")
  public String getGridPurpose() {
    return gridPurpose;
  }

  public void setGridPurpose(String gridPurpose) {
    this.gridPurpose = gridPurpose;
  }

  public GridInfo view(String view) {
    this.view = view;
    return this;
  }

  /**
   * Get view
   * @return view
   **/
  @JsonProperty("view")
  @ApiModelProperty(value = "")
  public String getView() {
    return view;
  }

  public void setView(String view) {
    this.view = view;
  }

  public GridInfo importTxId(Integer importTxId) {
    this.importTxId = importTxId;
    return this;
  }

  /**
   * Get importTxId
   * @return importTxId
   **/
  @JsonProperty("importTxId")
  @ApiModelProperty(value = "")
  public Integer getImportTxId() {
    return importTxId;
  }

  public void setImportTxId(Integer importTxId) {
    this.importTxId = importTxId;
  }

  public GridInfo exportTxId(Integer exportTxId) {
    this.exportTxId = exportTxId;
    return this;
  }

  /**
   * Get exportTxId
   * @return exportTxId
   **/
  @JsonProperty("exportTxId")
  @ApiModelProperty(value = "")
  public Integer getExportTxId() {
    return exportTxId;
  }

  public void setExportTxId(Integer exportTxId) {
    this.exportTxId = exportTxId;
  }

  public GridInfo filter(String filter) {
    this.filter = filter;
    return this;
  }

  /**
   * Get filter
   * @return filter
   **/
  @JsonProperty("filter")
  @ApiModelProperty(value = "")
  public String getFilter() {
    return filter;
  }

  public void setFilter(String filter) {
    this.filter = filter;
  }

  public GridInfo asOfTxId(Integer asOfTxId) {
    this.asOfTxId = asOfTxId;
    return this;
  }

  /**
   * Get asOfTxId
   * @return asOfTxId
   **/
  @JsonProperty("asOfTxId")
  @ApiModelProperty(value = "")
  public Integer getAsOfTxId() {
    return asOfTxId;
  }

  public void setAsOfTxId(Integer asOfTxId) {
    this.asOfTxId = asOfTxId;
  }

  public GridInfo baselineId(Integer baselineId) {
    this.baselineId = baselineId;
    return this;
  }

  /**
   * Get baselineId
   * @return baselineId
   **/
  @JsonProperty("baselineId")
  @ApiModelProperty(value = "")
  public Integer getBaselineId() {
    return baselineId;
  }

  public void setBaselineId(Integer baselineId) {
    this.baselineId = baselineId;
  }

  public GridInfo serverName(String serverName) {
    this.serverName = serverName;
    return this;
  }

  /**
   * Get serverName
   * @return serverName
   **/
  @JsonProperty("serverName")
  @ApiModelProperty(value = "")
  public String getServerName() {
    return serverName;
  }

  public void setServerName(String serverName) {
    this.serverName = serverName;
  }

  public GridInfo serverURL(String serverURL) {
    this.serverURL = serverURL;
    return this;
  }

  /**
   * Get serverURL
   * @return serverURL
   **/
  @JsonProperty("serverURL")
  @ApiModelProperty(value = "")
  public String getServerURL() {
    return serverURL;
  }

  public void setServerURL(String serverURL) {
    this.serverURL = serverURL;
  }

  public GridInfo collabId(Integer collabId) {
    this.collabId = collabId;
    return this;
  }

  /**
   * Get collabId
   * @return collabId
   **/
  @JsonProperty("collabId")
  @ApiModelProperty(value = "")
  public Integer getCollabId() {
    return collabId;
  }

  public void setCollabId(Integer collabId) {
    this.collabId = collabId;
  }

  public GridInfo wbId(Integer wbId) {
    this.wbId = wbId;
    return this;
  }

  /**
   * Get wbId
   * @return wbId
   **/
  @JsonProperty("wbId")
  @ApiModelProperty(value = "")
  public Integer getWbId() {
    return wbId;
  }

  public void setWbId(Integer wbId) {
    this.wbId = wbId;
  }

  public GridInfo memberId(Integer memberId) {
    this.memberId = memberId;
    return this;
  }

  /**
   * Get memberId
   * @return memberId
   **/
  @JsonProperty("memberId")
  @ApiModelProperty(value = "")
  public Integer getMemberId() {
    return memberId;
  }

  public void setMemberId(Integer memberId) {
    this.memberId = memberId;
  }

  public GridInfo userId(Integer userId) {
    this.userId = userId;
    return this;
  }

  /**
   * Get userId
   * @return userId
   **/
  @JsonProperty("userId")
  @ApiModelProperty(value = "")
  public Integer getUserId() {
    return userId;
  }

  public void setUserId(Integer userId) {
    this.userId = userId;
  }

  public GridInfo nhId(Integer nhId) {
    this.nhId = nhId;
    return this;
  }

  /**
   * Get nhId
   * @return nhId
   **/
  @JsonProperty("nhId")
  @ApiModelProperty(value = "")
  public Integer getNhId() {
    return nhId;
  }

  public void setNhId(Integer nhId) {
    this.nhId = nhId;
  }

  public GridInfo rowCount(Integer rowCount) {
    this.rowCount = rowCount;
    return this;
  }

  /**
   * Get rowCount
   * @return rowCount
   **/
  @JsonProperty("rowCount")
  @ApiModelProperty(value = "")
  public Integer getRowCount() {
    return rowCount;
  }

  public void setRowCount(Integer rowCount) {
    this.rowCount = rowCount;
  }

  public GridInfo colCount(Integer colCount) {
    this.colCount = colCount;
    return this;
  }

  /**
   * Get colCount
   * @return colCount
   **/
  @JsonProperty("colCount")
  @ApiModelProperty(value = "")
  public Integer getColCount() {
    return colCount;
  }

  public void setColCount(Integer colCount) {
    this.colCount = colCount;
  }

  public GridInfo maxTxId(Integer maxTxId) {
    this.maxTxId = maxTxId;
    return this;
  }

  /**
   * Get maxTxId
   * @return maxTxId
   **/
  @JsonProperty("maxTxId")
  @ApiModelProperty(value = "")
  public Integer getMaxTxId() {
    return maxTxId;
  }

  public void setMaxTxId(Integer maxTxId) {
    this.maxTxId = maxTxId;
  }

  public GridInfo mode(Integer mode) {
    this.mode = mode;
    return this;
  }

  /**
   * Get mode
   * @return mode
   **/
  @JsonProperty("mode")
  @ApiModelProperty(value = "")
  public Integer getMode() {
    return mode;
  }

  public void setMode(Integer mode) {
    this.mode = mode;
  }

  public GridInfo criteriaTableId(Integer criteriaTableId) {
    this.criteriaTableId = criteriaTableId;
    return this;
  }

  /**
   * Get criteriaTableId
   * @return criteriaTableId
   **/
  @JsonProperty("criteriaTableId")
  @ApiModelProperty(value = "")
  public Integer getCriteriaTableId() {
    return criteriaTableId;
  }

  public void setCriteriaTableId(Integer criteriaTableId) {
    this.criteriaTableId = criteriaTableId;
  }

  public GridInfo neighborhoodHeirarchy(NeighborhoodPath neighborhoodHeirarchy) {
    this.neighborhoodHeirarchy = neighborhoodHeirarchy;
    return this;
  }

  /**
   * Get neighborhoodHeirarchy
   * @return neighborhoodHeirarchy
   **/
  @JsonProperty("neighborhoodHeirarchy")
  @ApiModelProperty(value = "")
  public NeighborhoodPath getNeighborhoodHeirarchy() {
    return neighborhoodHeirarchy;
  }

  public void setNeighborhoodHeirarchy(NeighborhoodPath neighborhoodHeirarchy) {
    this.neighborhoodHeirarchy = neighborhoodHeirarchy;
  }

  public GridInfo creationTxId(Integer creationTxId) {
    this.creationTxId = creationTxId;
    return this;
  }

  /**
   * Get creationTxId
   * @return creationTxId
   **/
  @JsonProperty("creationTxId")
  @ApiModelProperty(value = "")
  public Integer getCreationTxId() {
    return creationTxId;
  }

  public void setCreationTxId(Integer creationTxId) {
    this.creationTxId = creationTxId;
  }

  public GridInfo peerAccess(Integer peerAccess) {
    this.peerAccess = peerAccess;
    return this;
  }

  /**
   * Get peerAccess
   * @return peerAccess
   **/
  @JsonProperty("peerAccess")
  @ApiModelProperty(value = "")
  public Integer getPeerAccess() {
    return peerAccess;
  }

  public void setPeerAccess(Integer peerAccess) {
    this.peerAccess = peerAccess;
  }

  public GridInfo privateAccess(Integer privateAccess) {
    this.privateAccess = privateAccess;
    return this;
  }

  /**
   * Get privateAccess
   * @return privateAccess
   **/
  @JsonProperty("privateAccess")
  @ApiModelProperty(value = "")
  public Integer getPrivateAccess() {
    return privateAccess;
  }

  public void setPrivateAccess(Integer privateAccess) {
    this.privateAccess = privateAccess;
  }

  public GridInfo friendAccess(Integer friendAccess) {
    this.friendAccess = friendAccess;
    return this;
  }

  /**
   * Get friendAccess
   * @return friendAccess
   **/
  @JsonProperty("friendAccess")
  @ApiModelProperty(value = "")
  public Integer getFriendAccess() {
    return friendAccess;
  }

  public void setFriendAccess(Integer friendAccess) {
    this.friendAccess = friendAccess;
  }

  public GridInfo isActive(Boolean isActive) {
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

  public GridInfo sequenceNumber(Float sequenceNumber) {
    this.sequenceNumber = sequenceNumber;
    return this;
  }

  /**
   * Get sequenceNumber
   * @return sequenceNumber
   **/
  @JsonProperty("sequenceNumber")
  @ApiModelProperty(value = "")
  public Float getSequenceNumber() {
    return sequenceNumber;
  }

  public void setSequenceNumber(Float sequenceNumber) {
    this.sequenceNumber = sequenceNumber;
  }

  public GridInfo isLocked(Boolean isLocked) {
    this.isLocked = isLocked;
    return this;
  }

  /**
   * Get isLocked
   * @return isLocked
   **/
  @JsonProperty("isLocked")
  @ApiModelProperty(value = "")
  public Boolean isIsLocked() {
    return isLocked;
  }

  public void setIsLocked(Boolean isLocked) {
    this.isLocked = isLocked;
  }

  public GridInfo lockTxId(Integer lockTxId) {
    this.lockTxId = lockTxId;
    return this;
  }

  /**
   * Get lockTxId
   * @return lockTxId
   **/
  @JsonProperty("lockTxId")
  @ApiModelProperty(value = "")
  public Integer getLockTxId() {
    return lockTxId;
  }

  public void setLockTxId(Integer lockTxId) {
    this.lockTxId = lockTxId;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    GridInfo gridInfo = (GridInfo) o;
    return Objects.equals(this.gridId, gridInfo.gridId) &&
        Objects.equals(this.gridName, gridInfo.gridName) &&
        Objects.equals(this.gridPurpose, gridInfo.gridPurpose) &&
        Objects.equals(this.view, gridInfo.view) &&
        Objects.equals(this.importTxId, gridInfo.importTxId) &&
        Objects.equals(this.exportTxId, gridInfo.exportTxId) &&
        Objects.equals(this.filter, gridInfo.filter) &&
        Objects.equals(this.asOfTxId, gridInfo.asOfTxId) &&
        Objects.equals(this.baselineId, gridInfo.baselineId) &&
        Objects.equals(this.serverName, gridInfo.serverName) &&
        Objects.equals(this.serverURL, gridInfo.serverURL) &&
        Objects.equals(this.collabId, gridInfo.collabId) &&
        Objects.equals(this.wbId, gridInfo.wbId) &&
        Objects.equals(this.memberId, gridInfo.memberId) &&
        Objects.equals(this.userId, gridInfo.userId) &&
        Objects.equals(this.nhId, gridInfo.nhId) &&
        Objects.equals(this.rowCount, gridInfo.rowCount) &&
        Objects.equals(this.colCount, gridInfo.colCount) &&
        Objects.equals(this.maxTxId, gridInfo.maxTxId) &&
        Objects.equals(this.mode, gridInfo.mode) &&
        Objects.equals(this.criteriaTableId, gridInfo.criteriaTableId) &&
        Objects.equals(this.neighborhoodHeirarchy, gridInfo.neighborhoodHeirarchy) &&
        Objects.equals(this.creationTxId, gridInfo.creationTxId) &&
        Objects.equals(this.peerAccess, gridInfo.peerAccess) &&
        Objects.equals(this.privateAccess, gridInfo.privateAccess) &&
        Objects.equals(this.friendAccess, gridInfo.friendAccess) &&
        Objects.equals(this.isActive, gridInfo.isActive) &&
        Objects.equals(this.sequenceNumber, gridInfo.sequenceNumber) &&
        Objects.equals(this.isLocked, gridInfo.isLocked) &&
        Objects.equals(this.lockTxId, gridInfo.lockTxId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(gridId, gridName, gridPurpose, view, importTxId, exportTxId, filter, asOfTxId, baselineId, serverName, serverURL, collabId, wbId, memberId, userId, nhId, rowCount, colCount, maxTxId, mode, criteriaTableId, neighborhoodHeirarchy, creationTxId, peerAccess, privateAccess, friendAccess, isActive, sequenceNumber, isLocked, lockTxId);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class GridInfo {\n");
    
    sb.append("    gridId: ").append(toIndentedString(gridId)).append("\n");
    sb.append("    gridName: ").append(toIndentedString(gridName)).append("\n");
    sb.append("    gridPurpose: ").append(toIndentedString(gridPurpose)).append("\n");
    sb.append("    view: ").append(toIndentedString(view)).append("\n");
    sb.append("    importTxId: ").append(toIndentedString(importTxId)).append("\n");
    sb.append("    exportTxId: ").append(toIndentedString(exportTxId)).append("\n");
    sb.append("    filter: ").append(toIndentedString(filter)).append("\n");
    sb.append("    asOfTxId: ").append(toIndentedString(asOfTxId)).append("\n");
    sb.append("    baselineId: ").append(toIndentedString(baselineId)).append("\n");
    sb.append("    serverName: ").append(toIndentedString(serverName)).append("\n");
    sb.append("    serverURL: ").append(toIndentedString(serverURL)).append("\n");
    sb.append("    collabId: ").append(toIndentedString(collabId)).append("\n");
    sb.append("    wbId: ").append(toIndentedString(wbId)).append("\n");
    sb.append("    memberId: ").append(toIndentedString(memberId)).append("\n");
    sb.append("    userId: ").append(toIndentedString(userId)).append("\n");
    sb.append("    nhId: ").append(toIndentedString(nhId)).append("\n");
    sb.append("    rowCount: ").append(toIndentedString(rowCount)).append("\n");
    sb.append("    colCount: ").append(toIndentedString(colCount)).append("\n");
    sb.append("    maxTxId: ").append(toIndentedString(maxTxId)).append("\n");
    sb.append("    mode: ").append(toIndentedString(mode)).append("\n");
    sb.append("    criteriaTableId: ").append(toIndentedString(criteriaTableId)).append("\n");
    sb.append("    neighborhoodHeirarchy: ").append(toIndentedString(neighborhoodHeirarchy)).append("\n");
    sb.append("    creationTxId: ").append(toIndentedString(creationTxId)).append("\n");
    sb.append("    peerAccess: ").append(toIndentedString(peerAccess)).append("\n");
    sb.append("    privateAccess: ").append(toIndentedString(privateAccess)).append("\n");
    sb.append("    friendAccess: ").append(toIndentedString(friendAccess)).append("\n");
    sb.append("    isActive: ").append(toIndentedString(isActive)).append("\n");
    sb.append("    sequenceNumber: ").append(toIndentedString(sequenceNumber)).append("\n");
    sb.append("    isLocked: ").append(toIndentedString(isLocked)).append("\n");
    sb.append("    lockTxId: ").append(toIndentedString(lockTxId)).append("\n");
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

