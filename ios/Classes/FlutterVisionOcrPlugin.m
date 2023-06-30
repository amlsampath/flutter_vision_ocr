#import "FlutterVisionOcrPlugin.h"
#if __has_include(<flutter_vision_ocr/flutter_vision_ocr-Swift.h>)
#import <flutter_vision_ocr/flutter_vision_ocr-Swift.h>
#else
// Support project import fallback if the generated compatibility header
// is not copied when this plugin is created as a library.
// https://forums.swift.org/t/swift-static-libraries-dont-copy-generated-objective-c-header/19816
#import "flutter_vision_ocr-Swift.h"
#endif

@implementation FlutterVisionOcrPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftFlutterVisionOcrPlugin registerWithRegistrar:registrar];
}
@end
