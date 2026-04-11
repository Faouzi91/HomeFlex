import 'package:freezed_annotation/freezed_annotation.dart';

part 'property_lease_dto.freezed.dart';
part 'property_lease_dto.g.dart';

@freezed
abstract class PropertyLeaseDto with _$PropertyLeaseDto {
  const factory PropertyLeaseDto({
    required String id,
    required String bookingId,
    required String tenantId,
    required String landlordId,
    required String content,
    required String status,
    String? signedAt,
    String? blockchainTxHash,
    required String onChainStatus,
    String? contractAddress,
    String? tokenId,
    required String createdAt,
    required String updatedAt,
  }) = _PropertyLeaseDto;

  factory PropertyLeaseDto.fromJson(Map<String, dynamic> json) => _$PropertyLeaseDtoFromJson(json);
}
