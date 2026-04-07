import 'package:freezed_annotation/freezed_annotation.dart';
import '../../auth/models/user_dto.dart';

part 'report_dto.freezed.dart';
part 'report_dto.g.dart';

@freezed
abstract class ReportDto with _$ReportDto {
  const factory ReportDto({
    required String id,
    UserDto? reporter,
    String? propertyId,
    String? reason,
    String? description,
    required String status,
    String? resolvedAt,
    required String createdAt,
  }) = _ReportDto;

  factory ReportDto.fromJson(Map<String, dynamic> json) => _$ReportDtoFromJson(json);
}
