package com.example.taobao.a2ui

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
data class A2UIMessage(
    @JsonProperty("version") val version: String = "v0.9",
    @JsonProperty("createSurface") val createSurface: CreateSurfaceMessage? = null,
    @JsonProperty("updateComponents") val updateComponents: UpdateComponentsMessage? = null,
    @JsonProperty("updateDataModel") val updateDataModel: UpdateDataModelMessage? = null,
    @JsonProperty("deleteSurface") val deleteSurface: DeleteSurfaceMessage? = null
)

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
data class CreateSurfaceMessage(
    @JsonProperty("surfaceId") val surfaceId: String,
    @JsonProperty("catalogId") val catalogId: String? = null,
    @JsonProperty("theme") val theme: Theme? = null,
    @JsonProperty("rootComponentId") val rootComponentId: String? = null
)

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
data class UpdateComponentsMessage(
    @JsonProperty("surfaceId") val surfaceId: String,
    @JsonProperty("components") val components: List<Component>
)

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
data class UpdateDataModelMessage(
    @JsonProperty("surfaceId") val surfaceId: String,
    @JsonProperty("path") val path: String,
    @JsonProperty("data") val data: Any? = null
)

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
data class DeleteSurfaceMessage(
    @JsonProperty("surfaceId") val surfaceId: String
)

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
data class Theme(
    @JsonProperty("colors") val colors: Map<String, String>? = null,
    @JsonProperty("typography") val typography: Map<String, Any>? = null,
    @JsonProperty("spacing") val spacing: Map<String, Int>? = null
)

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
data class Component(
    @JsonProperty("id") val id: String,
    @JsonProperty("component") val component: Map<String, Any>,
    @JsonProperty("children") val children: ChildrenReference? = null,
    @JsonProperty("events") val events: Map<String, String>? = null
)

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
data class ChildrenReference(
    @JsonProperty("explicitList") val explicitList: List<String>? = null,
    @JsonProperty("dataBinding") val dataBinding: String? = null,
    @JsonProperty("templateComponentId") val templateComponentId: String? = null
)
