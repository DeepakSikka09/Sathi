package `in`.ecomexpress.sathi.repo.remote.model.mps

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
data class MpsPickupItem(
    val subProductNumbering: String,
    val imageUrls: List<String>,
    val itemDescription: String
)