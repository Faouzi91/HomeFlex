package com.homeflex.features.property.domain.repository;

import com.homeflex.features.property.domain.document.PropertyDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface PropertySearchRepository extends ElasticsearchRepository<PropertyDocument, String> {
}
