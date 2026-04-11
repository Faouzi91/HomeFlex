// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'pricing_recommendation_dto.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_PricingRecommendationDto _$PricingRecommendationDtoFromJson(
  Map<String, dynamic> json,
) => _PricingRecommendationDto(
  propertyId: json['propertyId'] as String,
  currentPrice: (json['currentPrice'] as num).toDouble(),
  recommendedPrice: (json['recommendedPrice'] as num).toDouble(),
  confidenceLevel: json['confidenceLevel'] as String,
  reasoning: json['reasoning'] as String,
);

Map<String, dynamic> _$PricingRecommendationDtoToJson(
  _PricingRecommendationDto instance,
) => <String, dynamic>{
  'propertyId': instance.propertyId,
  'currentPrice': instance.currentPrice,
  'recommendedPrice': instance.recommendedPrice,
  'confidenceLevel': instance.confidenceLevel,
  'reasoning': instance.reasoning,
};
