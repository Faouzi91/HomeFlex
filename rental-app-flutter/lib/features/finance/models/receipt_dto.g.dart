// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'receipt_dto.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_ReceiptDto _$ReceiptDtoFromJson(Map<String, dynamic> json) => _ReceiptDto(
  id: json['id'] as String,
  bookingId: json['bookingId'] as String?,
  userId: json['userId'] as String,
  receiptNumber: json['receiptNumber'] as String,
  amount: (json['amount'] as num).toDouble(),
  currency: json['currency'] as String,
  status: json['status'] as String,
  receiptUrl: json['receiptUrl'] as String?,
  issuedAt: json['issuedAt'] as String,
  createdAt: json['createdAt'] as String,
);

Map<String, dynamic> _$ReceiptDtoToJson(_ReceiptDto instance) =>
    <String, dynamic>{
      'id': instance.id,
      'bookingId': instance.bookingId,
      'userId': instance.userId,
      'receiptNumber': instance.receiptNumber,
      'amount': instance.amount,
      'currency': instance.currency,
      'status': instance.status,
      'receiptUrl': instance.receiptUrl,
      'issuedAt': instance.issuedAt,
      'createdAt': instance.createdAt,
    };
