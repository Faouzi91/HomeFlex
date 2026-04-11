// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'maintenance_request_dto.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$MaintenanceRequestDto {

 String get id; String get propertyId; String get propertyTitle; String get tenantId; String get tenantName; String get title; String get description; MaintenanceCategory get category; MaintenancePriority get priority; MaintenanceStatus get status; String? get resolutionNotes; String? get resolvedAt; List<String>? get imageUrls; String get createdAt; String get updatedAt;
/// Create a copy of MaintenanceRequestDto
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$MaintenanceRequestDtoCopyWith<MaintenanceRequestDto> get copyWith => _$MaintenanceRequestDtoCopyWithImpl<MaintenanceRequestDto>(this as MaintenanceRequestDto, _$identity);

  /// Serializes this MaintenanceRequestDto to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is MaintenanceRequestDto&&(identical(other.id, id) || other.id == id)&&(identical(other.propertyId, propertyId) || other.propertyId == propertyId)&&(identical(other.propertyTitle, propertyTitle) || other.propertyTitle == propertyTitle)&&(identical(other.tenantId, tenantId) || other.tenantId == tenantId)&&(identical(other.tenantName, tenantName) || other.tenantName == tenantName)&&(identical(other.title, title) || other.title == title)&&(identical(other.description, description) || other.description == description)&&(identical(other.category, category) || other.category == category)&&(identical(other.priority, priority) || other.priority == priority)&&(identical(other.status, status) || other.status == status)&&(identical(other.resolutionNotes, resolutionNotes) || other.resolutionNotes == resolutionNotes)&&(identical(other.resolvedAt, resolvedAt) || other.resolvedAt == resolvedAt)&&const DeepCollectionEquality().equals(other.imageUrls, imageUrls)&&(identical(other.createdAt, createdAt) || other.createdAt == createdAt)&&(identical(other.updatedAt, updatedAt) || other.updatedAt == updatedAt));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,id,propertyId,propertyTitle,tenantId,tenantName,title,description,category,priority,status,resolutionNotes,resolvedAt,const DeepCollectionEquality().hash(imageUrls),createdAt,updatedAt);

@override
String toString() {
  return 'MaintenanceRequestDto(id: $id, propertyId: $propertyId, propertyTitle: $propertyTitle, tenantId: $tenantId, tenantName: $tenantName, title: $title, description: $description, category: $category, priority: $priority, status: $status, resolutionNotes: $resolutionNotes, resolvedAt: $resolvedAt, imageUrls: $imageUrls, createdAt: $createdAt, updatedAt: $updatedAt)';
}


}

/// @nodoc
abstract mixin class $MaintenanceRequestDtoCopyWith<$Res>  {
  factory $MaintenanceRequestDtoCopyWith(MaintenanceRequestDto value, $Res Function(MaintenanceRequestDto) _then) = _$MaintenanceRequestDtoCopyWithImpl;
@useResult
$Res call({
 String id, String propertyId, String propertyTitle, String tenantId, String tenantName, String title, String description, MaintenanceCategory category, MaintenancePriority priority, MaintenanceStatus status, String? resolutionNotes, String? resolvedAt, List<String>? imageUrls, String createdAt, String updatedAt
});




}
/// @nodoc
class _$MaintenanceRequestDtoCopyWithImpl<$Res>
    implements $MaintenanceRequestDtoCopyWith<$Res> {
  _$MaintenanceRequestDtoCopyWithImpl(this._self, this._then);

  final MaintenanceRequestDto _self;
  final $Res Function(MaintenanceRequestDto) _then;

/// Create a copy of MaintenanceRequestDto
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? id = null,Object? propertyId = null,Object? propertyTitle = null,Object? tenantId = null,Object? tenantName = null,Object? title = null,Object? description = null,Object? category = null,Object? priority = null,Object? status = null,Object? resolutionNotes = freezed,Object? resolvedAt = freezed,Object? imageUrls = freezed,Object? createdAt = null,Object? updatedAt = null,}) {
  return _then(_self.copyWith(
id: null == id ? _self.id : id // ignore: cast_nullable_to_non_nullable
as String,propertyId: null == propertyId ? _self.propertyId : propertyId // ignore: cast_nullable_to_non_nullable
as String,propertyTitle: null == propertyTitle ? _self.propertyTitle : propertyTitle // ignore: cast_nullable_to_non_nullable
as String,tenantId: null == tenantId ? _self.tenantId : tenantId // ignore: cast_nullable_to_non_nullable
as String,tenantName: null == tenantName ? _self.tenantName : tenantName // ignore: cast_nullable_to_non_nullable
as String,title: null == title ? _self.title : title // ignore: cast_nullable_to_non_nullable
as String,description: null == description ? _self.description : description // ignore: cast_nullable_to_non_nullable
as String,category: null == category ? _self.category : category // ignore: cast_nullable_to_non_nullable
as MaintenanceCategory,priority: null == priority ? _self.priority : priority // ignore: cast_nullable_to_non_nullable
as MaintenancePriority,status: null == status ? _self.status : status // ignore: cast_nullable_to_non_nullable
as MaintenanceStatus,resolutionNotes: freezed == resolutionNotes ? _self.resolutionNotes : resolutionNotes // ignore: cast_nullable_to_non_nullable
as String?,resolvedAt: freezed == resolvedAt ? _self.resolvedAt : resolvedAt // ignore: cast_nullable_to_non_nullable
as String?,imageUrls: freezed == imageUrls ? _self.imageUrls : imageUrls // ignore: cast_nullable_to_non_nullable
as List<String>?,createdAt: null == createdAt ? _self.createdAt : createdAt // ignore: cast_nullable_to_non_nullable
as String,updatedAt: null == updatedAt ? _self.updatedAt : updatedAt // ignore: cast_nullable_to_non_nullable
as String,
  ));
}

}


/// Adds pattern-matching-related methods to [MaintenanceRequestDto].
extension MaintenanceRequestDtoPatterns on MaintenanceRequestDto {
/// A variant of `map` that fallback to returning `orElse`.
///
/// It is equivalent to doing:
/// ```dart
/// switch (sealedClass) {
///   case final Subclass value:
///     return ...;
///   case _:
///     return orElse();
/// }
/// ```

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _MaintenanceRequestDto value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _MaintenanceRequestDto() when $default != null:
return $default(_that);case _:
  return orElse();

}
}
/// A `switch`-like method, using callbacks.
///
/// Callbacks receives the raw object, upcasted.
/// It is equivalent to doing:
/// ```dart
/// switch (sealedClass) {
///   case final Subclass value:
///     return ...;
///   case final Subclass2 value:
///     return ...;
/// }
/// ```

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _MaintenanceRequestDto value)  $default,){
final _that = this;
switch (_that) {
case _MaintenanceRequestDto():
return $default(_that);case _:
  throw StateError('Unexpected subclass');

}
}
/// A variant of `map` that fallback to returning `null`.
///
/// It is equivalent to doing:
/// ```dart
/// switch (sealedClass) {
///   case final Subclass value:
///     return ...;
///   case _:
///     return null;
/// }
/// ```

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _MaintenanceRequestDto value)?  $default,){
final _that = this;
switch (_that) {
case _MaintenanceRequestDto() when $default != null:
return $default(_that);case _:
  return null;

}
}
/// A variant of `when` that fallback to an `orElse` callback.
///
/// It is equivalent to doing:
/// ```dart
/// switch (sealedClass) {
///   case Subclass(:final field):
///     return ...;
///   case _:
///     return orElse();
/// }
/// ```

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( String id,  String propertyId,  String propertyTitle,  String tenantId,  String tenantName,  String title,  String description,  MaintenanceCategory category,  MaintenancePriority priority,  MaintenanceStatus status,  String? resolutionNotes,  String? resolvedAt,  List<String>? imageUrls,  String createdAt,  String updatedAt)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _MaintenanceRequestDto() when $default != null:
return $default(_that.id,_that.propertyId,_that.propertyTitle,_that.tenantId,_that.tenantName,_that.title,_that.description,_that.category,_that.priority,_that.status,_that.resolutionNotes,_that.resolvedAt,_that.imageUrls,_that.createdAt,_that.updatedAt);case _:
  return orElse();

}
}
/// A `switch`-like method, using callbacks.
///
/// As opposed to `map`, this offers destructuring.
/// It is equivalent to doing:
/// ```dart
/// switch (sealedClass) {
///   case Subclass(:final field):
///     return ...;
///   case Subclass2(:final field2):
///     return ...;
/// }
/// ```

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( String id,  String propertyId,  String propertyTitle,  String tenantId,  String tenantName,  String title,  String description,  MaintenanceCategory category,  MaintenancePriority priority,  MaintenanceStatus status,  String? resolutionNotes,  String? resolvedAt,  List<String>? imageUrls,  String createdAt,  String updatedAt)  $default,) {final _that = this;
switch (_that) {
case _MaintenanceRequestDto():
return $default(_that.id,_that.propertyId,_that.propertyTitle,_that.tenantId,_that.tenantName,_that.title,_that.description,_that.category,_that.priority,_that.status,_that.resolutionNotes,_that.resolvedAt,_that.imageUrls,_that.createdAt,_that.updatedAt);case _:
  throw StateError('Unexpected subclass');

}
}
/// A variant of `when` that fallback to returning `null`
///
/// It is equivalent to doing:
/// ```dart
/// switch (sealedClass) {
///   case Subclass(:final field):
///     return ...;
///   case _:
///     return null;
/// }
/// ```

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( String id,  String propertyId,  String propertyTitle,  String tenantId,  String tenantName,  String title,  String description,  MaintenanceCategory category,  MaintenancePriority priority,  MaintenanceStatus status,  String? resolutionNotes,  String? resolvedAt,  List<String>? imageUrls,  String createdAt,  String updatedAt)?  $default,) {final _that = this;
switch (_that) {
case _MaintenanceRequestDto() when $default != null:
return $default(_that.id,_that.propertyId,_that.propertyTitle,_that.tenantId,_that.tenantName,_that.title,_that.description,_that.category,_that.priority,_that.status,_that.resolutionNotes,_that.resolvedAt,_that.imageUrls,_that.createdAt,_that.updatedAt);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _MaintenanceRequestDto implements MaintenanceRequestDto {
  const _MaintenanceRequestDto({required this.id, required this.propertyId, required this.propertyTitle, required this.tenantId, required this.tenantName, required this.title, required this.description, required this.category, required this.priority, required this.status, this.resolutionNotes, this.resolvedAt, final  List<String>? imageUrls, required this.createdAt, required this.updatedAt}): _imageUrls = imageUrls;
  factory _MaintenanceRequestDto.fromJson(Map<String, dynamic> json) => _$MaintenanceRequestDtoFromJson(json);

@override final  String id;
@override final  String propertyId;
@override final  String propertyTitle;
@override final  String tenantId;
@override final  String tenantName;
@override final  String title;
@override final  String description;
@override final  MaintenanceCategory category;
@override final  MaintenancePriority priority;
@override final  MaintenanceStatus status;
@override final  String? resolutionNotes;
@override final  String? resolvedAt;
 final  List<String>? _imageUrls;
@override List<String>? get imageUrls {
  final value = _imageUrls;
  if (value == null) return null;
  if (_imageUrls is EqualUnmodifiableListView) return _imageUrls;
  // ignore: implicit_dynamic_type
  return EqualUnmodifiableListView(value);
}

@override final  String createdAt;
@override final  String updatedAt;

/// Create a copy of MaintenanceRequestDto
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$MaintenanceRequestDtoCopyWith<_MaintenanceRequestDto> get copyWith => __$MaintenanceRequestDtoCopyWithImpl<_MaintenanceRequestDto>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$MaintenanceRequestDtoToJson(this, );
}

@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is _MaintenanceRequestDto&&(identical(other.id, id) || other.id == id)&&(identical(other.propertyId, propertyId) || other.propertyId == propertyId)&&(identical(other.propertyTitle, propertyTitle) || other.propertyTitle == propertyTitle)&&(identical(other.tenantId, tenantId) || other.tenantId == tenantId)&&(identical(other.tenantName, tenantName) || other.tenantName == tenantName)&&(identical(other.title, title) || other.title == title)&&(identical(other.description, description) || other.description == description)&&(identical(other.category, category) || other.category == category)&&(identical(other.priority, priority) || other.priority == priority)&&(identical(other.status, status) || other.status == status)&&(identical(other.resolutionNotes, resolutionNotes) || other.resolutionNotes == resolutionNotes)&&(identical(other.resolvedAt, resolvedAt) || other.resolvedAt == resolvedAt)&&const DeepCollectionEquality().equals(other._imageUrls, _imageUrls)&&(identical(other.createdAt, createdAt) || other.createdAt == createdAt)&&(identical(other.updatedAt, updatedAt) || other.updatedAt == updatedAt));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,id,propertyId,propertyTitle,tenantId,tenantName,title,description,category,priority,status,resolutionNotes,resolvedAt,const DeepCollectionEquality().hash(_imageUrls),createdAt,updatedAt);

@override
String toString() {
  return 'MaintenanceRequestDto(id: $id, propertyId: $propertyId, propertyTitle: $propertyTitle, tenantId: $tenantId, tenantName: $tenantName, title: $title, description: $description, category: $category, priority: $priority, status: $status, resolutionNotes: $resolutionNotes, resolvedAt: $resolvedAt, imageUrls: $imageUrls, createdAt: $createdAt, updatedAt: $updatedAt)';
}


}

/// @nodoc
abstract mixin class _$MaintenanceRequestDtoCopyWith<$Res> implements $MaintenanceRequestDtoCopyWith<$Res> {
  factory _$MaintenanceRequestDtoCopyWith(_MaintenanceRequestDto value, $Res Function(_MaintenanceRequestDto) _then) = __$MaintenanceRequestDtoCopyWithImpl;
@override @useResult
$Res call({
 String id, String propertyId, String propertyTitle, String tenantId, String tenantName, String title, String description, MaintenanceCategory category, MaintenancePriority priority, MaintenanceStatus status, String? resolutionNotes, String? resolvedAt, List<String>? imageUrls, String createdAt, String updatedAt
});




}
/// @nodoc
class __$MaintenanceRequestDtoCopyWithImpl<$Res>
    implements _$MaintenanceRequestDtoCopyWith<$Res> {
  __$MaintenanceRequestDtoCopyWithImpl(this._self, this._then);

  final _MaintenanceRequestDto _self;
  final $Res Function(_MaintenanceRequestDto) _then;

/// Create a copy of MaintenanceRequestDto
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? id = null,Object? propertyId = null,Object? propertyTitle = null,Object? tenantId = null,Object? tenantName = null,Object? title = null,Object? description = null,Object? category = null,Object? priority = null,Object? status = null,Object? resolutionNotes = freezed,Object? resolvedAt = freezed,Object? imageUrls = freezed,Object? createdAt = null,Object? updatedAt = null,}) {
  return _then(_MaintenanceRequestDto(
id: null == id ? _self.id : id // ignore: cast_nullable_to_non_nullable
as String,propertyId: null == propertyId ? _self.propertyId : propertyId // ignore: cast_nullable_to_non_nullable
as String,propertyTitle: null == propertyTitle ? _self.propertyTitle : propertyTitle // ignore: cast_nullable_to_non_nullable
as String,tenantId: null == tenantId ? _self.tenantId : tenantId // ignore: cast_nullable_to_non_nullable
as String,tenantName: null == tenantName ? _self.tenantName : tenantName // ignore: cast_nullable_to_non_nullable
as String,title: null == title ? _self.title : title // ignore: cast_nullable_to_non_nullable
as String,description: null == description ? _self.description : description // ignore: cast_nullable_to_non_nullable
as String,category: null == category ? _self.category : category // ignore: cast_nullable_to_non_nullable
as MaintenanceCategory,priority: null == priority ? _self.priority : priority // ignore: cast_nullable_to_non_nullable
as MaintenancePriority,status: null == status ? _self.status : status // ignore: cast_nullable_to_non_nullable
as MaintenanceStatus,resolutionNotes: freezed == resolutionNotes ? _self.resolutionNotes : resolutionNotes // ignore: cast_nullable_to_non_nullable
as String?,resolvedAt: freezed == resolvedAt ? _self.resolvedAt : resolvedAt // ignore: cast_nullable_to_non_nullable
as String?,imageUrls: freezed == imageUrls ? _self._imageUrls : imageUrls // ignore: cast_nullable_to_non_nullable
as List<String>?,createdAt: null == createdAt ? _self.createdAt : createdAt // ignore: cast_nullable_to_non_nullable
as String,updatedAt: null == updatedAt ? _self.updatedAt : updatedAt // ignore: cast_nullable_to_non_nullable
as String,
  ));
}


}


/// @nodoc
mixin _$MaintenanceRequestCreateRequest {

 String get propertyId; String get title; String get description; MaintenanceCategory get category; MaintenancePriority get priority;
/// Create a copy of MaintenanceRequestCreateRequest
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$MaintenanceRequestCreateRequestCopyWith<MaintenanceRequestCreateRequest> get copyWith => _$MaintenanceRequestCreateRequestCopyWithImpl<MaintenanceRequestCreateRequest>(this as MaintenanceRequestCreateRequest, _$identity);

  /// Serializes this MaintenanceRequestCreateRequest to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is MaintenanceRequestCreateRequest&&(identical(other.propertyId, propertyId) || other.propertyId == propertyId)&&(identical(other.title, title) || other.title == title)&&(identical(other.description, description) || other.description == description)&&(identical(other.category, category) || other.category == category)&&(identical(other.priority, priority) || other.priority == priority));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,propertyId,title,description,category,priority);

@override
String toString() {
  return 'MaintenanceRequestCreateRequest(propertyId: $propertyId, title: $title, description: $description, category: $category, priority: $priority)';
}


}

/// @nodoc
abstract mixin class $MaintenanceRequestCreateRequestCopyWith<$Res>  {
  factory $MaintenanceRequestCreateRequestCopyWith(MaintenanceRequestCreateRequest value, $Res Function(MaintenanceRequestCreateRequest) _then) = _$MaintenanceRequestCreateRequestCopyWithImpl;
@useResult
$Res call({
 String propertyId, String title, String description, MaintenanceCategory category, MaintenancePriority priority
});




}
/// @nodoc
class _$MaintenanceRequestCreateRequestCopyWithImpl<$Res>
    implements $MaintenanceRequestCreateRequestCopyWith<$Res> {
  _$MaintenanceRequestCreateRequestCopyWithImpl(this._self, this._then);

  final MaintenanceRequestCreateRequest _self;
  final $Res Function(MaintenanceRequestCreateRequest) _then;

/// Create a copy of MaintenanceRequestCreateRequest
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? propertyId = null,Object? title = null,Object? description = null,Object? category = null,Object? priority = null,}) {
  return _then(_self.copyWith(
propertyId: null == propertyId ? _self.propertyId : propertyId // ignore: cast_nullable_to_non_nullable
as String,title: null == title ? _self.title : title // ignore: cast_nullable_to_non_nullable
as String,description: null == description ? _self.description : description // ignore: cast_nullable_to_non_nullable
as String,category: null == category ? _self.category : category // ignore: cast_nullable_to_non_nullable
as MaintenanceCategory,priority: null == priority ? _self.priority : priority // ignore: cast_nullable_to_non_nullable
as MaintenancePriority,
  ));
}

}


/// Adds pattern-matching-related methods to [MaintenanceRequestCreateRequest].
extension MaintenanceRequestCreateRequestPatterns on MaintenanceRequestCreateRequest {
/// A variant of `map` that fallback to returning `orElse`.
///
/// It is equivalent to doing:
/// ```dart
/// switch (sealedClass) {
///   case final Subclass value:
///     return ...;
///   case _:
///     return orElse();
/// }
/// ```

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _MaintenanceRequestCreateRequest value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _MaintenanceRequestCreateRequest() when $default != null:
return $default(_that);case _:
  return orElse();

}
}
/// A `switch`-like method, using callbacks.
///
/// Callbacks receives the raw object, upcasted.
/// It is equivalent to doing:
/// ```dart
/// switch (sealedClass) {
///   case final Subclass value:
///     return ...;
///   case final Subclass2 value:
///     return ...;
/// }
/// ```

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _MaintenanceRequestCreateRequest value)  $default,){
final _that = this;
switch (_that) {
case _MaintenanceRequestCreateRequest():
return $default(_that);case _:
  throw StateError('Unexpected subclass');

}
}
/// A variant of `map` that fallback to returning `null`.
///
/// It is equivalent to doing:
/// ```dart
/// switch (sealedClass) {
///   case final Subclass value:
///     return ...;
///   case _:
///     return null;
/// }
/// ```

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _MaintenanceRequestCreateRequest value)?  $default,){
final _that = this;
switch (_that) {
case _MaintenanceRequestCreateRequest() when $default != null:
return $default(_that);case _:
  return null;

}
}
/// A variant of `when` that fallback to an `orElse` callback.
///
/// It is equivalent to doing:
/// ```dart
/// switch (sealedClass) {
///   case Subclass(:final field):
///     return ...;
///   case _:
///     return orElse();
/// }
/// ```

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( String propertyId,  String title,  String description,  MaintenanceCategory category,  MaintenancePriority priority)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _MaintenanceRequestCreateRequest() when $default != null:
return $default(_that.propertyId,_that.title,_that.description,_that.category,_that.priority);case _:
  return orElse();

}
}
/// A `switch`-like method, using callbacks.
///
/// As opposed to `map`, this offers destructuring.
/// It is equivalent to doing:
/// ```dart
/// switch (sealedClass) {
///   case Subclass(:final field):
///     return ...;
///   case Subclass2(:final field2):
///     return ...;
/// }
/// ```

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( String propertyId,  String title,  String description,  MaintenanceCategory category,  MaintenancePriority priority)  $default,) {final _that = this;
switch (_that) {
case _MaintenanceRequestCreateRequest():
return $default(_that.propertyId,_that.title,_that.description,_that.category,_that.priority);case _:
  throw StateError('Unexpected subclass');

}
}
/// A variant of `when` that fallback to returning `null`
///
/// It is equivalent to doing:
/// ```dart
/// switch (sealedClass) {
///   case Subclass(:final field):
///     return ...;
///   case _:
///     return null;
/// }
/// ```

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( String propertyId,  String title,  String description,  MaintenanceCategory category,  MaintenancePriority priority)?  $default,) {final _that = this;
switch (_that) {
case _MaintenanceRequestCreateRequest() when $default != null:
return $default(_that.propertyId,_that.title,_that.description,_that.category,_that.priority);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _MaintenanceRequestCreateRequest implements MaintenanceRequestCreateRequest {
  const _MaintenanceRequestCreateRequest({required this.propertyId, required this.title, required this.description, required this.category, required this.priority});
  factory _MaintenanceRequestCreateRequest.fromJson(Map<String, dynamic> json) => _$MaintenanceRequestCreateRequestFromJson(json);

@override final  String propertyId;
@override final  String title;
@override final  String description;
@override final  MaintenanceCategory category;
@override final  MaintenancePriority priority;

/// Create a copy of MaintenanceRequestCreateRequest
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$MaintenanceRequestCreateRequestCopyWith<_MaintenanceRequestCreateRequest> get copyWith => __$MaintenanceRequestCreateRequestCopyWithImpl<_MaintenanceRequestCreateRequest>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$MaintenanceRequestCreateRequestToJson(this, );
}

@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is _MaintenanceRequestCreateRequest&&(identical(other.propertyId, propertyId) || other.propertyId == propertyId)&&(identical(other.title, title) || other.title == title)&&(identical(other.description, description) || other.description == description)&&(identical(other.category, category) || other.category == category)&&(identical(other.priority, priority) || other.priority == priority));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,propertyId,title,description,category,priority);

@override
String toString() {
  return 'MaintenanceRequestCreateRequest(propertyId: $propertyId, title: $title, description: $description, category: $category, priority: $priority)';
}


}

/// @nodoc
abstract mixin class _$MaintenanceRequestCreateRequestCopyWith<$Res> implements $MaintenanceRequestCreateRequestCopyWith<$Res> {
  factory _$MaintenanceRequestCreateRequestCopyWith(_MaintenanceRequestCreateRequest value, $Res Function(_MaintenanceRequestCreateRequest) _then) = __$MaintenanceRequestCreateRequestCopyWithImpl;
@override @useResult
$Res call({
 String propertyId, String title, String description, MaintenanceCategory category, MaintenancePriority priority
});




}
/// @nodoc
class __$MaintenanceRequestCreateRequestCopyWithImpl<$Res>
    implements _$MaintenanceRequestCreateRequestCopyWith<$Res> {
  __$MaintenanceRequestCreateRequestCopyWithImpl(this._self, this._then);

  final _MaintenanceRequestCreateRequest _self;
  final $Res Function(_MaintenanceRequestCreateRequest) _then;

/// Create a copy of MaintenanceRequestCreateRequest
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? propertyId = null,Object? title = null,Object? description = null,Object? category = null,Object? priority = null,}) {
  return _then(_MaintenanceRequestCreateRequest(
propertyId: null == propertyId ? _self.propertyId : propertyId // ignore: cast_nullable_to_non_nullable
as String,title: null == title ? _self.title : title // ignore: cast_nullable_to_non_nullable
as String,description: null == description ? _self.description : description // ignore: cast_nullable_to_non_nullable
as String,category: null == category ? _self.category : category // ignore: cast_nullable_to_non_nullable
as MaintenanceCategory,priority: null == priority ? _self.priority : priority // ignore: cast_nullable_to_non_nullable
as MaintenancePriority,
  ));
}


}


/// @nodoc
mixin _$MaintenanceStatusUpdateRequest {

 MaintenanceStatus get status; String? get resolutionNotes;
/// Create a copy of MaintenanceStatusUpdateRequest
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$MaintenanceStatusUpdateRequestCopyWith<MaintenanceStatusUpdateRequest> get copyWith => _$MaintenanceStatusUpdateRequestCopyWithImpl<MaintenanceStatusUpdateRequest>(this as MaintenanceStatusUpdateRequest, _$identity);

  /// Serializes this MaintenanceStatusUpdateRequest to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is MaintenanceStatusUpdateRequest&&(identical(other.status, status) || other.status == status)&&(identical(other.resolutionNotes, resolutionNotes) || other.resolutionNotes == resolutionNotes));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,status,resolutionNotes);

@override
String toString() {
  return 'MaintenanceStatusUpdateRequest(status: $status, resolutionNotes: $resolutionNotes)';
}


}

/// @nodoc
abstract mixin class $MaintenanceStatusUpdateRequestCopyWith<$Res>  {
  factory $MaintenanceStatusUpdateRequestCopyWith(MaintenanceStatusUpdateRequest value, $Res Function(MaintenanceStatusUpdateRequest) _then) = _$MaintenanceStatusUpdateRequestCopyWithImpl;
@useResult
$Res call({
 MaintenanceStatus status, String? resolutionNotes
});




}
/// @nodoc
class _$MaintenanceStatusUpdateRequestCopyWithImpl<$Res>
    implements $MaintenanceStatusUpdateRequestCopyWith<$Res> {
  _$MaintenanceStatusUpdateRequestCopyWithImpl(this._self, this._then);

  final MaintenanceStatusUpdateRequest _self;
  final $Res Function(MaintenanceStatusUpdateRequest) _then;

/// Create a copy of MaintenanceStatusUpdateRequest
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? status = null,Object? resolutionNotes = freezed,}) {
  return _then(_self.copyWith(
status: null == status ? _self.status : status // ignore: cast_nullable_to_non_nullable
as MaintenanceStatus,resolutionNotes: freezed == resolutionNotes ? _self.resolutionNotes : resolutionNotes // ignore: cast_nullable_to_non_nullable
as String?,
  ));
}

}


/// Adds pattern-matching-related methods to [MaintenanceStatusUpdateRequest].
extension MaintenanceStatusUpdateRequestPatterns on MaintenanceStatusUpdateRequest {
/// A variant of `map` that fallback to returning `orElse`.
///
/// It is equivalent to doing:
/// ```dart
/// switch (sealedClass) {
///   case final Subclass value:
///     return ...;
///   case _:
///     return orElse();
/// }
/// ```

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _MaintenanceStatusUpdateRequest value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _MaintenanceStatusUpdateRequest() when $default != null:
return $default(_that);case _:
  return orElse();

}
}
/// A `switch`-like method, using callbacks.
///
/// Callbacks receives the raw object, upcasted.
/// It is equivalent to doing:
/// ```dart
/// switch (sealedClass) {
///   case final Subclass value:
///     return ...;
///   case final Subclass2 value:
///     return ...;
/// }
/// ```

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _MaintenanceStatusUpdateRequest value)  $default,){
final _that = this;
switch (_that) {
case _MaintenanceStatusUpdateRequest():
return $default(_that);case _:
  throw StateError('Unexpected subclass');

}
}
/// A variant of `map` that fallback to returning `null`.
///
/// It is equivalent to doing:
/// ```dart
/// switch (sealedClass) {
///   case final Subclass value:
///     return ...;
///   case _:
///     return null;
/// }
/// ```

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _MaintenanceStatusUpdateRequest value)?  $default,){
final _that = this;
switch (_that) {
case _MaintenanceStatusUpdateRequest() when $default != null:
return $default(_that);case _:
  return null;

}
}
/// A variant of `when` that fallback to an `orElse` callback.
///
/// It is equivalent to doing:
/// ```dart
/// switch (sealedClass) {
///   case Subclass(:final field):
///     return ...;
///   case _:
///     return orElse();
/// }
/// ```

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( MaintenanceStatus status,  String? resolutionNotes)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _MaintenanceStatusUpdateRequest() when $default != null:
return $default(_that.status,_that.resolutionNotes);case _:
  return orElse();

}
}
/// A `switch`-like method, using callbacks.
///
/// As opposed to `map`, this offers destructuring.
/// It is equivalent to doing:
/// ```dart
/// switch (sealedClass) {
///   case Subclass(:final field):
///     return ...;
///   case Subclass2(:final field2):
///     return ...;
/// }
/// ```

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( MaintenanceStatus status,  String? resolutionNotes)  $default,) {final _that = this;
switch (_that) {
case _MaintenanceStatusUpdateRequest():
return $default(_that.status,_that.resolutionNotes);case _:
  throw StateError('Unexpected subclass');

}
}
/// A variant of `when` that fallback to returning `null`
///
/// It is equivalent to doing:
/// ```dart
/// switch (sealedClass) {
///   case Subclass(:final field):
///     return ...;
///   case _:
///     return null;
/// }
/// ```

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( MaintenanceStatus status,  String? resolutionNotes)?  $default,) {final _that = this;
switch (_that) {
case _MaintenanceStatusUpdateRequest() when $default != null:
return $default(_that.status,_that.resolutionNotes);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _MaintenanceStatusUpdateRequest implements MaintenanceStatusUpdateRequest {
  const _MaintenanceStatusUpdateRequest({required this.status, this.resolutionNotes});
  factory _MaintenanceStatusUpdateRequest.fromJson(Map<String, dynamic> json) => _$MaintenanceStatusUpdateRequestFromJson(json);

@override final  MaintenanceStatus status;
@override final  String? resolutionNotes;

/// Create a copy of MaintenanceStatusUpdateRequest
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$MaintenanceStatusUpdateRequestCopyWith<_MaintenanceStatusUpdateRequest> get copyWith => __$MaintenanceStatusUpdateRequestCopyWithImpl<_MaintenanceStatusUpdateRequest>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$MaintenanceStatusUpdateRequestToJson(this, );
}

@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is _MaintenanceStatusUpdateRequest&&(identical(other.status, status) || other.status == status)&&(identical(other.resolutionNotes, resolutionNotes) || other.resolutionNotes == resolutionNotes));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,status,resolutionNotes);

@override
String toString() {
  return 'MaintenanceStatusUpdateRequest(status: $status, resolutionNotes: $resolutionNotes)';
}


}

/// @nodoc
abstract mixin class _$MaintenanceStatusUpdateRequestCopyWith<$Res> implements $MaintenanceStatusUpdateRequestCopyWith<$Res> {
  factory _$MaintenanceStatusUpdateRequestCopyWith(_MaintenanceStatusUpdateRequest value, $Res Function(_MaintenanceStatusUpdateRequest) _then) = __$MaintenanceStatusUpdateRequestCopyWithImpl;
@override @useResult
$Res call({
 MaintenanceStatus status, String? resolutionNotes
});




}
/// @nodoc
class __$MaintenanceStatusUpdateRequestCopyWithImpl<$Res>
    implements _$MaintenanceStatusUpdateRequestCopyWith<$Res> {
  __$MaintenanceStatusUpdateRequestCopyWithImpl(this._self, this._then);

  final _MaintenanceStatusUpdateRequest _self;
  final $Res Function(_MaintenanceStatusUpdateRequest) _then;

/// Create a copy of MaintenanceStatusUpdateRequest
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? status = null,Object? resolutionNotes = freezed,}) {
  return _then(_MaintenanceStatusUpdateRequest(
status: null == status ? _self.status : status // ignore: cast_nullable_to_non_nullable
as MaintenanceStatus,resolutionNotes: freezed == resolutionNotes ? _self.resolutionNotes : resolutionNotes // ignore: cast_nullable_to_non_nullable
as String?,
  ));
}


}

// dart format on
