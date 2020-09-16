package com.travelrights.model

data class SearchResultResponse(val proposals: ArrayList<ProposalsItem>? = null,val meta: MetaResponse? = null,val gates_info: Map<String,Gatesmember>? = null)