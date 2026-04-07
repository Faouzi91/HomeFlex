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
}
