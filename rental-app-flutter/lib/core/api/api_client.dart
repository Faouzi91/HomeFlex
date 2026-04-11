import 'package:dio/dio.dart';
import 'package:dio_cookie_manager/dio_cookie_manager.dart';
import 'package:cookie_jar/cookie_jar.dart';
import 'package:flutter/foundation.dart' show kIsWeb;
import 'package:path_provider/path_provider.dart';

class ApiClient {
  static final ApiClient _instance = ApiClient._internal();
  factory ApiClient() => _instance;
  late Dio dio;
  CookieJar? cookieJar;

  ApiClient._internal() {
    dio = Dio(BaseOptions(
      baseUrl: 'http://localhost:8080/api/v1',
      connectTimeout: const Duration(seconds: 10),
      receiveTimeout: const Duration(seconds: 10),
      headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/json',
      },
      // On web, the browser must send cookies for cross-origin requests.
      extra: kIsWeb ? {'withCredentials': true} : {},
    ));
  }

  Future<void> init() async {
    if (!kIsWeb) {
      // Native: persist cookies to disk via path_provider.
      final appDocDir = await getApplicationDocumentsDirectory();
      cookieJar = PersistCookieJar(
        storage: FileStorage('${appDocDir.path}/.cookies/'),
      );
      dio.interceptors.add(CookieManager(cookieJar!));
    }
    // On web, do NOT add CookieManager — the browser handles cookies natively.
    // path_provider has no web implementation, so calling it would throw
    // MissingPluginException.

    dio.interceptors.add(LogInterceptor(
      requestBody: true,
      responseBody: true,
      requestHeader: true,
      responseHeader: true,
    ));
  }

  // --- Auth ---
  Future<Response> login(Map<String, dynamic> data) => dio.post('/auth/login', data: data);
  Future<Response> register(Map<String, dynamic> data) => dio.post('/auth/register', data: data);
  Future<Response> logout() => dio.post('/auth/logout');
  Future<Response> googleLogin(String token) => dio.post('/auth/google', data: {'token': token});
  Future<Response> appleLogin(String token) => dio.post('/auth/apple', data: {'token': token});
  Future<Response> facebookLogin(String token) => dio.post('/auth/facebook', data: {'token': token});

  // --- Properties ---
  Future<Response> searchProperties(Map<String, dynamic> params) => dio.get('/properties/search', queryParameters: params);
  Future<Response> getProperty(String id) => dio.get('/properties/$id');
  Future<Response> createProperty(Map<String, dynamic> data) => dio.post('/properties', data: data);
  Future<Response> updateProperty(String id, Map<String, dynamic> data) => dio.put('/properties/$id', data: data);
  Future<Response> deleteProperty(String id) => dio.delete('/properties/$id');

  // --- Bookings ---
  Future<Response> createBooking(Map<String, dynamic> data) => dio.post('/bookings', data: data);
  Future<Response> getMyBookings() => dio.get('/bookings/my');
  Future<Response> getBooking(String id) => dio.get('/bookings/$id');
  Future<Response> approveBooking(String id, String response) => dio.patch('/bookings/$id/approve', queryParameters: {'response': response});
  Future<Response> rejectBooking(String id, String reason) => dio.patch('/bookings/$id/reject', queryParameters: {'reason': reason});

  // --- Reviews ---
  Future<Response> createReview(Map<String, dynamic> data) => dio.post('/reviews', data: data);
  Future<Response> getPropertyReviews(String id) => dio.get('/reviews/property/$id');
  Future<Response> getTenantReviews(String id) => dio.get('/reviews/tenant/$id');

  // --- Insurance ---
  Future<Response> getInsurancePlans(String type) => dio.get('/insurance/plans', queryParameters: {'type': type});
  Future<Response> purchaseInsurance(String planId, String bookingId) => dio.post('/insurance/purchase', queryParameters: {'planId': planId, 'bookingId': bookingId});

  // --- Finance ---
  Future<Response> getMyReceipts() => dio.get('/finance/receipts');

  // --- Disputes ---
  Future<Response> openDispute(Map<String, dynamic> data) => dio.post('/disputes', queryParameters: data);
  Future<Response> getAllDisputes() => dio.get('/disputes');

  // --- AI Pricing ---
  Future<Response> getPricingRecommendation(String id) => dio.get('/properties/$id/pricing/recommendation');

  // --- Currencies ---
  Future<Response> getCurrencyRates() => dio.get('/currencies/rates');
  Future<Response> convertCurrency(double amount, String from, String to) => 
      dio.get('/currencies/convert', queryParameters: {'amount': amount, 'from': from, 'to': to});
}
