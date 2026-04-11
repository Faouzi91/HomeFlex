import 'package:freezed_annotation/freezed_annotation.dart';

part 'dispute_dto.freezed.dart';
part 'dispute_dto.g.dart';

@freezed
abstract class DisputeDto with _$DisputeDto {
  const factory DisputeDto({
    required String id,
    required String bookingId,
    required String initiatorId,
    required String reason,
    String? description,
    required String status,
    String? resolutionNotes,
    String? resolvedAt,
    String? resolvedById,
    required String createdAt,
    required String updatedAt,
  }) = _DisputeDto;

  factory DisputeDto.fromJson(Map<String, dynamic> json) => _$DisputeDtoFromJson(json);
}
