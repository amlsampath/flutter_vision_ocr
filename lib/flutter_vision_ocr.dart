import 'dart:async';

import 'package:flutter/services.dart';

class FlutterVisionOcr {
  static const MethodChannel _channel = MethodChannel('flutter_vision_ocr');

  static Future<String?> get platformVersion async {
    final String? version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }

  static Future<String?> get takeFromCamera async {
    final String? text = await _channel.invokeMethod('cameraImage');
    return text;
  }

  static Future<String?> get takeFromGallery async {
    final String? text = await _channel.invokeMethod('galleryImage');
    return text;
  }
}
