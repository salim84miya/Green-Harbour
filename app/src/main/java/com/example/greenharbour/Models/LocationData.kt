package com.example.greenharbour.Models

// Data classes to represent the JSON structure

data class FeatureCollection(
    val type: String,
    val features: List<Feature>
)

data class Feature(
    val type: String,
    val properties: Properties,
    val geometry: Geometry
)

data class Properties(
    val formatted: String?,
    val datasource: DataSource?,
    val name: String?,
    val country: String?,
    val country_code: String?,
    val state: String?,
    val state_district: String?,
    val county: String?,
    val city: String?,
    val lon: Double?,
    val lat: Double?,
    val state_code: String?,
    val result_type: String?,
    val address_line1: String?,
    val address_line2: String?,
    val timezone: Timezone?,
    val plus_code: String?,
    val rank: Rank?,
    val place_id: String?
)

data class DataSource(
    val sourcename: String,
    val attribution: String,
    val license: String,
    val url: String
)

data class Timezone(
    val name: String,
    val offset_STD: String,
    val offset_STD_seconds: Int,
    val offset_DST: String,
    val offset_DST_seconds: Int,
    val abbreviation_STD: String,
    val abbreviation_DST: String
)

data class Rank(
    val importance: Double,
    val confidence: Int,
    val match_type: String
)

data class Geometry(
    val type: String,
    val coordinates: List<Double>
)

