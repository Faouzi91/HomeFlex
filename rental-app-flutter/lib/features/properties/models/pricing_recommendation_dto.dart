import 'package:freezed_annotation/freezed_annotation.dart';

part 'pricing_recommendation_dto.freezed.dart';
part 'pricing_recommendation_dto.g.dart';

@freezed
abstract class PricingRecommendationDto with _$PricingRecommendationDto {
  const factory PricingRecommendationDto({
    required String propertyId,
    required double currentPrice,
    required double recommendedPrice,
    required String confidenceLevel,
    required String reasoning,
  }) = _PricingRecommendationDto;

  factory PricingRecommendationDto.fromJson(Map<String, dynamic> json) => _$PricingRecommendationDtoFromJson(json);
}
