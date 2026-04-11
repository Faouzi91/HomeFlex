// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'property_lease_dto.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_PropertyLeaseDto _$PropertyLeaseDtoFromJson(Map<String, dynamic> json) =>
    _PropertyLeaseDto(
      id: json['id'] as String,
      bookingId: json['bookingId'] as String,
      tenantId: json['tenantId'] as String,
      landlordId: json['landlordId'] as String,
      content: json['content'] as String,
      status: json['status'] as String,
      signedAt: json['signedAt'] as String?,
      blockchainTxHash: json['blockchainTxHash'] as String?,
      onChainStatus: json['onChainStatus'] as String,
      contractAddress: json['contractAddress'] as String?,
      tokenId: json['tokenId'] as String?,
      createdAt: json['createdAt'] as String,
      updatedAt: json['updatedAt'] as String,
    );

Map<String, dynamic> _$PropertyLeaseDtoToJson(_PropertyLeaseDto instance) =>
    <String, dynamic>{
      'id': instance.id,
      'bookingId': instance.bookingId,
      'tenantId': instance.tenantId,
      'landlordId': instance.landlordId,
      'content': instance.content,
      'status': instance.status,
      'signedAt': instance.signedAt,
      'blockchainTxHash': instance.blockchainTxHash,
      'onChainStatus': instance.onChainStatus,
      'contractAddress': instance.contractAddress,
      'tokenId': instance.tokenId,
      'createdAt': instance.createdAt,
      'updatedAt': instance.updatedAt,
    };
