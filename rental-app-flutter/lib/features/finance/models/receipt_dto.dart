import 'package:freezed_annotation/freezed_annotation.dart';

part 'receipt_dto.freezed.dart';
part 'receipt_dto.g.dart';

@freezed
abstract class ReceiptDto with _$ReceiptDto {
  const factory ReceiptDto({
    required String id,
    String? bookingId,
    required String userId,
    required String receiptNumber,
    required double amount,
    required String currency,
    required String status,
    String? receiptUrl,
    required String issuedAt,
    required String createdAt,
  }) = _ReceiptDto;

  factory ReceiptDto.fromJson(Map<String, dynamic> json) => _$ReceiptDtoFromJson(json);
}
