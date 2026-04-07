import 'package:freezed_annotation/freezed_annotation.dart';
import '../../auth/models/user_dto.dart';

part 'review_dto.freezed.dart';
part 'review_dto.g.dart';

@freezed
abstract class ReviewDto with _$ReviewDto {
  const factory ReviewDto({
    required String id,
    required String propertyId,
    UserDto? reviewer,
    required int rating,
    String? comment,
    required String createdAt,
  }) = _ReviewDto;

  factory ReviewDto.fromJson(Map<String, dynamic> json) => _$ReviewDtoFromJson(json);
}
