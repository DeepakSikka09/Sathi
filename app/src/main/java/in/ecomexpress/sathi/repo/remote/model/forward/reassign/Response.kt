package `in`.ecomexpress.sathi.repo.remote.model.forward.reassign

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
data class Response(
    val description: String,
    val is_dc_update_allowed_for_dept: Boolean,
)
