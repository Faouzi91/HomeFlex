import 'package:freezed_annotation/freezed_annotation.dart';
import '../../auth/models/user_dto.dart';

part 'review_dto.freezed.dart';
part 'review_dto.g.dart';

enum ReviewType {
  PROPERTY,
  TENANT
}

@freezed
abstract class ReviewDto with _$ReviewDto {
  const factory ReviewDto({
    required String id,
    required ReviewType type,
    String? propertyId,
    UserDto? targetUser,
    UserDto? reviewer,
    required int rating,
    String? comment,
    required String createdAt,
  }) = _ReviewDto;

  factory ReviewDto.fromJson(Map<String, dynamic> json) => _$ReviewDtoFromJson(json);
}

@freezed
abstract class ReviewCreateRequest with _$ReviewCreateRequest {
  const factory ReviewCreateRequest({
    String? propertyId,
    String? targetUserId,
    required int rating,
    String? comment,
  }) = _ReviewCreateRequest;

  factory ReviewCreateRequest.fromJson(Map<String, dynamic> json) => _$ReviewCreateRequestFromJson(json);
}
