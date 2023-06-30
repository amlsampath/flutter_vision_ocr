import 'dart:async';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_vision_ocr/flutter_vision_ocr.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatefulWidget {
  const MyApp({Key? key}) : super(key: key);

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String imageText = 'Unknown';

  @override
  void initState() {
    super.initState();
  }

  // Platform messages are asynchronous, so we initialize in an async method.
  Future<void> takeImages({bool isCamera = true}) async {
    String _imageText;
    // Platform messages may fail, so we use a try/catch PlatformException.
    // We also handle the message potentially returning null.
    try {
      if (isCamera) {
        _imageText = await FlutterVisionOcr.takeFromCamera ?? 'Something went wrong';
      } else {
        _imageText = await FlutterVisionOcr.takeFromGallery ?? 'Something went wrong';
      }
    } on PlatformException {
      _imageText = 'Failed to get platform version.';
    }

    // If the widget was removed from the tree while the asynchronous platform
    // message was in flight, we want to discard the reply rather than calling
    // setState to update our non-existent appearance.
    if (!mounted) return;

    setState(() {
      imageText = _imageText;
    });
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        // body: Center(
        //   child: GestureDetector(
        //       onTap: () {
        //         initPlatformState();
        //       },
        //       child: Text('Running on: $imageText\n')),
        body: Container(
          alignment: Alignment.center,
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            crossAxisAlignment: CrossAxisAlignment.center,
            children: [
              Text('$imageText\n'),
              TextButton(
                  onPressed: ()async {
                   await takeImages(isCamera: true);
                  },
                  child: const Text("Camera")),
              TextButton(
                  onPressed: () async{
                   await takeImages(isCamera: false);
                  },
                  child: const Text("Gallery"))
            ],
          ),
        ),
      ),
    );
  }
}
